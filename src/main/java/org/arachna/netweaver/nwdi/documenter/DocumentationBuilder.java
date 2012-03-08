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
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

import net.sf.json.JSONObject;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.config.DevelopmentConfigurationReader;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.AntTaskBuilder;
import org.arachna.netweaver.hudson.util.FilePathHelper;
import org.arachna.netweaver.nwdi.documenter.report.DependencyGraphGenerator;
import org.arachna.netweaver.nwdi.documenter.report.DevelopmentComponentReportGenerator;
import org.arachna.netweaver.nwdi.documenter.report.DevelopmentConfigurationConfluenceWikiGenerator;
import org.arachna.xml.XmlReaderHelper;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.xml.sax.SAXException;

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
    private static final String DC_REPORT_BUNDLE =
        "org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentReport";

    /**
     * velocity template for DC report generation.
     */
    private static final String DC_WIKI_TEMPLATE =
        "/org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentWikiTemplate.vm";

    /**
     * timeout for dependency diagram generation.
     */
    private static final int TIMEOUT = 1000 * 60;

    /**
     * descriptor for DocumentationBuilder.
     */
    @Extension
    public static final DescriptorImpl DESCRIPTOR = new DescriptorImpl();

    /**
     * regular expression for ignoring development components of certain
     * vendors.
     */
    private final Pattern ignoreVendorRegexp;

    /**
     * regular expression for ignoring development components of certain
     * software components.
     * 
     * @deprecated Not used anymore!
     */
    @Deprecated
    private final transient Pattern ignoreSoftwareComponentRegex = null;

    /**
     * 
     */
    private final String confluenceSite;

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
     * @param confluenceSite
     *            the selected confluence site.
     */
    @DataBoundConstructor
    public DocumentationBuilder(final String ignoreVendorRegexp, final String confluenceSite) {
        this.ignoreVendorRegexp = Pattern.compile(ignoreVendorRegexp);
        this.confluenceSite = confluenceSite == null ? "" : confluenceSite;
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
     * Return the regular expression to be used for software components to
     * ignore when documenting development components.
     */
    public String getIgnoreSoftwareComponentRegex() {
        return ignoreSoftwareComponentRegex.pattern();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
        // final DevelopmentComponentFactory dcFactory =
        // nwdiBuild.getDevelopmentComponentFactory();
        // final DevelopmentConfiguration developmentConfiguration =
        // nwdiBuild.getDevelopmentConfiguration();

        // ------------------ set up
        // ------------------------------------------------
        final DCReader dcReader = new DCReader();
        dcReader.execute();

        final DevelopmentComponentFactory dcFactory = dcReader.getDcFactory();
        setAntHelper(new AntHelper(FilePathHelper.makeAbsolute(build.getWorkspace()), dcFactory));
        final DevelopmentConfiguration developmentConfiguration = dcReader.getConfig();
        // ------------------ end set up
        // ------------------------------------------------

        final File workspace = new File(String.format("%s/documentation", getAntHelper().getPathToWorkspace()));

        final VendorFilter vendorFilter = new VendorFilter(ignoreVendorRegexp);
        final DependencyGraphGenerator dependenciesGenerator =
            new DependencyGraphGenerator(dcFactory, vendorFilter, workspace);

        developmentConfiguration.accept(dependenciesGenerator);

        final VelocityEngine engine = getVelocityEngine(listener.getLogger());
        final boolean result = true;
        // super.execute(build, launcher, listener, "",
        // dependenciesGenerator.materializeDot2SvgBuildXml(engine,
        // DESCRIPTOR.getDotExecutable(), TIMEOUT),
        // getAntProperties());

        final boolean confluence = true;

        if (result) {
            if (confluence) {
                try {
                    final ConfluenceSite site = getSelectedConfluenceSite();

                    if (site != null) {
                        final ConfluenceSession confluenceSession = site.createSession();
                        final DevelopmentComponentReportGenerator generator =
                            new DevelopmentComponentReportGenerator(dcFactory, engine, DC_WIKI_TEMPLATE,
                                ResourceBundle.getBundle(DC_REPORT_BUNDLE, Locale.GERMAN));
                        developmentConfiguration.accept(new DevelopmentConfigurationConfluenceWikiGenerator(generator,
                            vendorFilter, confluenceSession, "NETW", listener.getLogger(), dependenciesGenerator
                                .getDescriptorContainer()));
                    }
                }
                catch (final RemoteException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        // new ReportGenerator(listener.getLogger(), developmentConfiguration,
        // dcFactory, engine,
        // workspace.getAbsolutePath(), ignoreVendorRegexp);

        return result;
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

    private final class DCReader {
        private final DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();

        private DevelopmentConfiguration config;

        /**
         * @return the dcFactory
         */
        public DevelopmentComponentFactory getDcFactory() {
            return dcFactory;
        }

        /**
         * @return the config
         */
        public DevelopmentConfiguration getConfig() {
            return config;
        }

        void execute() {
            final DevelopmentConfigurationReader reader = new DevelopmentConfigurationReader(dcFactory);
            // new XmlReaderHelper(reader).parse(new FileReader(
            // "/tmp/jenkins/jobs/enviaM/workspace/DevelopmentConfiguration.xml"));
            // new XmlReaderHelper(reader).parse(new FileReader(
            // "/NWDI-Redesign/PN3_enviaMPr_D-refactored.xml"));
            try {
                new XmlReaderHelper(reader).parse(new FileReader(
                    "/home/weigo/tmp/enviaM/workspace/DevelopmentConfiguration.xml"));
                config = reader.getDevelopmentConfiguration();
            }
            catch (final FileNotFoundException e) {
                throw new RuntimeException(e);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
            catch (final SAXException e) {
                throw new RuntimeException(e);
            }

            dcFactory.updateUsingDCs();

            for (final DevelopmentComponent component : dcFactory.getAll()) {
                if (!"sap.com".equals(component.getCompartment().getVendor())) {
                    component.setNeedsRebuild(true);
                }
            }
        }
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
