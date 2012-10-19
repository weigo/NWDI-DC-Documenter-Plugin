package org.arachna.netweaver.nwdi.documenter;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.Hudson;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;

import java.io.File;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import jenkins.model.Jenkins;
import net.sf.json.JSONObject;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.AntTaskBuilder;
import org.arachna.netweaver.hudson.nwdi.NWDIBuild;
import org.arachna.netweaver.nwdi.documenter.filter.VendorFilter;
import org.arachna.netweaver.nwdi.documenter.report.ContextPropertyName;
import org.arachna.netweaver.nwdi.documenter.report.DevelopmentConfigurationConfluenceWikiGenerator;
import org.arachna.netweaver.nwdi.documenter.report.DocBookReportGenerator;
import org.arachna.netweaver.nwdi.dot4j.DependencyGraphGenerator;
import org.arachna.netweaver.nwdi.dot4j.DiagramDescriptorContainer;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import com.myyearbook.hudson.plugins.confluence.ConfluencePublisher;
import com.myyearbook.hudson.plugins.confluence.ConfluenceSession;
import com.myyearbook.hudson.plugins.confluence.ConfluenceSite;

/**
 * Builder for generating documentation of a development configuration.
 * 
 * @author Dirk Weigenand
 */
public class DocumentationBuilder extends AntTaskBuilder {
    /**
     * bundle to use for report internationalization.
     */
    public static final String DC_REPORT_BUNDLE =
        "org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentReport";

    /**
     * descriptor for DocumentationBuilder.
     */
    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    /**
     * time out for creating dependency diagrams.
     */
    private static final int TIMEOUT = 60 * 1000;

    /**
     * regular expression for ignoring development components of certain
     * vendors.
     */
    private Pattern ignoreVendorRegexp;

    /**
     * the confluence site to publish documentation to.
     */
    private String confluenceSite;

    /**
     * indicate that documentation should be published to confluence.
     */
    private boolean publishToConfluence;

    /**
     * indicate that documentation in HTML format should be produced.
     */
    private boolean createHtmlDocumentation;

    /**
     * the confluence space to publish to.
     */
    private String confluenceSpace;

    /**
     * Create a new instance of a <code>DocumentationBuilder</code> using the
     * given regular expression for vendors to ignore when building
     * documentation.
     * 
     * @param ignoreVendorRegexp
     *            regular expression for vendors to ignore during generation of
     *            documentation. E.g. <code>sap\.com</code> to ignore the usual
     *            suspects like sap.com_SAP_BUILDT, sap.com_SAP_JEE,
     *            sap.com_SAP_JTECHS, etc. Those would only pollute the
     *            dependency diagrams.
     * @param createHtmlDocumentation
     *            indicate that documentation in HTML format should be created.
     * @param publishToConfluence
     *            indicate that documentation should be published to confluence.
     * @param confluenceSite
     *            the selected confluence site.
     * @param confluenceSpace
     *            the confluence space to publish to.
     */
    @DataBoundConstructor
    public DocumentationBuilder(final String ignoreVendorRegexp, final boolean createHtmlDocumentation,
        final boolean publishToConfluence, final String confluenceSite, final String confluenceSpace) {
        this.createHtmlDocumentation = createHtmlDocumentation;
        this.publishToConfluence = publishToConfluence;
        this.ignoreVendorRegexp = Pattern.compile(ignoreVendorRegexp);
        this.confluenceSite = confluenceSite == null ? "" : confluenceSite;
        this.confluenceSpace = confluenceSpace;
    }

    /**
     * default no argument constructor.
     */
    public DocumentationBuilder() {

    }

    /**
     * Returns the selected confluence site.
     * 
     * @return the confluenceSite
     */
    public String getConfluenceSite() {
        return confluenceSite;
    }

    /**
     * Return the regular expression to be used for vendors to ignore when
     * documenting development components.
     * 
     * @return the regular expression to be used for vendors to ignore when
     *         documenting development components.
     */
    public String getIgnoreVendorRegexp() {
        return ignoreVendorRegexp.pattern();
    }

    /**
     * @return the publishToConfluence
     */
    public boolean getPublishToConfluence() {
        return publishToConfluence;
    }

    /**
     * @return the createHtmlDocumentation
     */
    public boolean getCreateHtmlDocumentation() {
        return createHtmlDocumentation;
    }

    /**
     * @param ignoreVendorRegexp
     *            the ignoreVendorRegexp to set
     */
    public void setIgnoreVendorRegexp(final Pattern ignoreVendorRegexp) {
        this.ignoreVendorRegexp = ignoreVendorRegexp;
    }

    /**
     * @param confluenceSite
     *            the confluenceSite to set
     */
    public void setConfluenceSite(final String confluenceSite) {
        this.confluenceSite = confluenceSite;
    }

    /**
     * @param publishToConfluence
     *            the publishToConfluence to set
     */
    public void setPublishToConfluence(final boolean publishToConfluence) {
        this.publishToConfluence = publishToConfluence;
    }

    /**
     * @param createHtmlDocumentation
     *            the createHtmlDocumentation to set
     */
    public void setCreateHtmlDocumentation(final boolean createHtmlDocumentation) {
        this.createHtmlDocumentation = createHtmlDocumentation;
    }

    /**
     * @return the confluenceSpace
     */
    public String getConfluenceSpace() {
        return confluenceSpace;
    }

    /**
     * @param confluenceSpace
     *            the confluenceSpace to set
     */
    public void setConfluenceSpace(final String confluenceSpace) {
        this.confluenceSpace = confluenceSpace;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
        if (!createHtmlDocumentation && !publishToConfluence) {
            return true;
        }

        final NWDIBuild nwdiBuild = (NWDIBuild)build;
        final DevelopmentComponentFactory dcFactory = nwdiBuild.getDevelopmentComponentFactory();
        final DevelopmentConfiguration developmentConfiguration = nwdiBuild.getDevelopmentConfiguration();
        final File workspace = new File(getAntHelper().getPathToWorkspace());
        final File docBookSourceFolder = new File(workspace, "src/docbkx");
        final VendorFilter vendorFilter = new VendorFilter(ignoreVendorRegexp);
        final VelocityEngine engine = getVelocityEngine(listener.getLogger());
        final DiagramDescriptorContainer descriptorContainer =
            generateDependencyDiagrams(dcFactory, developmentConfiguration, docBookSourceFolder, vendorFilter);
        final boolean result =
            generateDot2SvgBuildFile(build, launcher, listener, workspace, engine, descriptorContainer);

        if (result) {
            // FIXME: make language configurable!
            final ResourceBundle bundle = ResourceBundle.getBundle(DC_REPORT_BUNDLE, Locale.GERMAN);
            generateDocBookDocuments(dcFactory, developmentConfiguration, workspace, vendorFilter, engine,
                descriptorContainer, bundle);

            if (publishToConfluence) {
                publishToConfluence(build, listener, developmentConfiguration, docBookSourceFolder, vendorFilter,
                    descriptorContainer);
            }

            if (createHtmlDocumentation) {
                ;
                // FIXME: add code to generate HTML when docbook conversion is
                // there!
            }
        }

        return result;
    }

    /**
     * @param dcFactory
     * @param developmentConfiguration
     * @param docBookSourceFolder
     * @param vendorFilter
     * @return
     */
    protected DiagramDescriptorContainer generateDependencyDiagrams(final DevelopmentComponentFactory dcFactory,
        final DevelopmentConfiguration developmentConfiguration, final File docBookSourceFolder,
        final VendorFilter vendorFilter) {
        final DependencyGraphGenerator dependenciesGenerator =
            new DependencyGraphGenerator(dcFactory, vendorFilter, docBookSourceFolder);

        developmentConfiguration.accept(dependenciesGenerator);
        final DiagramDescriptorContainer descriptorContainer = dependenciesGenerator.getDescriptorContainer();
        return descriptorContainer;
    }

    /**
     * @param build
     * @param launcher
     * @param listener
     * @param workspace
     * @param engine
     * @param descriptorContainer
     * @return
     */
    protected boolean generateDot2SvgBuildFile(final AbstractBuild build, final Launcher launcher,
        final BuildListener listener, final File workspace, final VelocityEngine engine,
        final DiagramDescriptorContainer descriptorContainer) {
        final Dot2SvgBuildFileGenerator generator =
            new Dot2SvgBuildFileGenerator(workspace, engine, DESCRIPTOR.getDotExecutable(), TIMEOUT, Runtime
                .getRuntime().availableProcessors());
        return super.execute(build, launcher, listener, "", generator.execute(descriptorContainer.getDotFiles()),
            getAntProperties());
    }

    /**
     * @param dcFactory
     * @param developmentConfiguration
     * @param workspace
     * @param vendorFilter
     * @param engine
     * @param descriptorContainer
     * @param bundle
     */
    protected void generateDocBookDocuments(final DevelopmentComponentFactory dcFactory,
        final DevelopmentConfiguration developmentConfiguration, final File workspace, final VendorFilter vendorFilter,
        final VelocityEngine engine, final DiagramDescriptorContainer descriptorContainer, final ResourceBundle bundle) {
        final DocBookReportGenerator generator =
            new DocBookReportGenerator(workspace, getAntHelper(), dcFactory, engine, bundle, vendorFilter,
                descriptorContainer);
        developmentConfiguration.accept(generator);
    }

    /**
     * @param build
     * @param listener
     * @param developmentConfiguration
     * @param docBookSourceFolder
     * @param vendorFilter
     * @param descriptorContainer
     */
    protected void publishToConfluence(final AbstractBuild build, final BuildListener listener,
        final DevelopmentConfiguration developmentConfiguration, final File docBookSourceFolder,
        final VendorFilter vendorFilter, final DiagramDescriptorContainer descriptorContainer) {
        try {
            final ConfluenceSite site = getSelectedConfluenceSite();

            if (site != null) {
                // FIXME: encapsulate ConfluenceSession in wrapper class
                // and move page handling code from
                // DevelopmentConfigurationConfluenceWikiGenerator into
                // it!
                final ConfluenceSession confluenceSession = site.createSession();
                final DevelopmentConfigurationConfluenceWikiGenerator visitor =
                    new DevelopmentConfigurationConfluenceWikiGenerator(docBookSourceFolder, vendorFilter,
                        confluenceSession, listener.getLogger(), descriptorContainer);
                visitor.addToGlobalContext(ContextPropertyName.WikiSpace, confluenceSpace);
                visitor.addToGlobalContext(ContextPropertyName.ProjectUrl, Jenkins.getInstance().getRootUrl()
                    + build.getProject().getUrl());
                developmentConfiguration.accept(visitor);
            }
        }
        catch (final RemoteException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Get the selected Confluence site.
     * 
     * @return the selected confluence site or <code>null</code> if none was
     *         selected.
     */
    protected ConfluenceSite getSelectedConfluenceSite() {
        final ConfluencePublisher.DescriptorImpl descriptor = getConfluencePublisherDescriptor();
        return descriptor.getSiteByName(confluenceSite);
    }

    /**
     * Look up the descriptor of the <code>ConfluencePublisher</code>.
     * 
     * @return the descriptor of the <code>ConfluencePublisher</code> or
     *         <code>null</code> if the plugin is not installed.
     */
    protected com.myyearbook.hudson.plugins.confluence.ConfluencePublisher.DescriptorImpl getConfluencePublisherDescriptor() {
        return Hudson.getInstance().getDescriptorByType(ConfluencePublisher.DescriptorImpl.class);
    }

    // Overridden for better type safety.
    // If your plugin doesn't really define any property on Descriptor,
    // you don't have to do this.
    @Override
    public DescriptorImpl getDescriptor() {
        return DESCRIPTOR;
    }

    /**
     * Descriptor for {@link DocumentationBuilder}. Used as a singleton. The
     * class is marked as public so that it can be accessed from views.
     * 
     * <p>
     * See
     * <tt>src/main/resources/hudson/plugins/hello_world/HelloWorldBuilder/*.jelly</tt>
     * for the actual HTML fragment for the configuration screen.
     */

    // This indicates to Jenkins that this is an implementation of an extension
    // point.
    public static final class DescriptorImpl extends BuildStepDescriptor<Builder> {
        /**
         * regular expression for ignoring development components of certain
         * vendors.
         * 
         * @deprecated Not used anymore
         */
        @Deprecated
        private transient Pattern ignoreVendorRegexp;

        /**
         * regular expression for ignoring development components of certain
         * software components.
         * 
         * @deprecated Not used anymore.
         */
        @Deprecated
        private transient Pattern ignoreSoftwareComponentRegex;

        /**
         * Path to the 'dot' executable.
         */
        private String dotExecutable;

        public DescriptorImpl() {
            load();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return true /* NWDIProject.class.equals(aClass) */;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getDisplayName() {
            return "NWDI Documentation Builder";
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {
            dotExecutable = formData.getString("dotExecutable");

            save();
            return super.configure(req, formData);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Builder newInstance(final StaplerRequest req, final JSONObject formData) throws FormException {
            final DocumentationBuilder builder = new DocumentationBuilder();

            builder.setCreateHtmlDocumentation(Boolean.valueOf(formData.getString("createHtmlDocumentation")));
            builder.setIgnoreVendorRegexp(Pattern.compile(formData.getString("ignoreVendorRegexp")));
            builder.setPublishToConfluence(Boolean.valueOf(formData.getString("publishToConfluence")));
            builder.setConfluenceSite(formData.getString("confluenceSite"));
            builder.setConfluenceSpace(formData.getString("confluenceSpace"));

            return builder;
        }

        /**
         * Return the regular expression for ignoring vendors as a String.
         * 
         * @return the regular expression for ignoring vendors as a String.
         */
        public String getIgnoreVendorRegexp() {
            return ignoreVendorRegexp != null ? ignoreVendorRegexp.pattern() : "";
        }

        /**
         * Return the pattern for ignoring vendors.
         * 
         * @return the pattern for ignoring vendors.
         */
        public Pattern getIgnoreVendorRegexpPattern() {
            return ignoreVendorRegexp;
        }

        /**
         * Sets the regular expression to be used when ignoring development
         * components via their compartments vendor.
         * 
         * @param ignoreVendorRegexp
         *            the ignoreVendorRegexp to set
         */
        public void setIgnoreVendorRegexp(final String ignoreVendorRegexp) {
            this.ignoreVendorRegexp = Pattern.compile(ignoreVendorRegexp);
        }

        /**
         * Returns the pattern for ignoring development components via their
         * software components name.
         * 
         * @return the ignoreSoftwareComponentRegex
         */
        public String getIgnoreSoftwareComponentRegex() {
            return ignoreSoftwareComponentRegex != null ? ignoreSoftwareComponentRegex.pattern() : "";
        }

        /**
         * Set the regular expression to be used ignoring software components
         * during the documentation process.
         * 
         * @param ignoreSoftwareComponentRegex
         *            the regular expression to be used ignoring software
         *            components during the documentation process to set
         */
        public void setIgnoreSoftwareComponentRegex(final String ignoreSoftwareComponentRegex) {
            this.ignoreSoftwareComponentRegex = Pattern.compile(ignoreSoftwareComponentRegex);
        }

        /**
         * Returns the absolute path of the 'dot' executable.
         * 
         * @return the absolute path of the 'dot' executable.
         */
        public String getDotExecutable() {
            return dotExecutable;
        }

        /**
         * Sets the absolute path of the 'dot' executable.
         * 
         * @param dotExecutable
         *            the absolute path of the 'dot' executable.
         */
        public void setDotExecutable(final String dotExecutable) {
            this.dotExecutable = dotExecutable;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String getAntProperties() {
        return "";
    }

    /**
     * Return the configured confluence sites.
     * 
     * @return the list of configured confluence sites. The list is empty if no
     *         sites are configured or the confluence publisher plugin is not
     *         installed.
     */
    public List<ConfluenceSite> getConfluenceSites() {
        final ConfluencePublisher.DescriptorImpl descriptor = getConfluencePublisherDescriptor();
        return descriptor == null ? new ArrayList<ConfluenceSite>() : descriptor.getSites();
    }
}
