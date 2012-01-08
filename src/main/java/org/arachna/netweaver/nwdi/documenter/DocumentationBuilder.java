package org.arachna.netweaver.nwdi.documenter;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.Builder;
import hudson.util.FormValidation;

import java.io.File;
import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.arachna.netweaver.hudson.nwdi.AntTaskBuilder;
import org.arachna.netweaver.hudson.nwdi.NWDIBuild;
import org.arachna.netweaver.hudson.nwdi.NWDIProject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * Sample {@link Builder}.
 * 
 * <p>
 * When the user configures the project and enables this builder,
 * {@link DescriptorImpl#newInstance(StaplerRequest)} is invoked and a new
 * {@link DocumentationBuilder} is created. The created instance is persisted to
 * the project configuration XML by using XStream, so this allows you to use
 * instance fields (like {@link #name}) to remember the configuration.
 * 
 * <p>
 * When a build is performed, the
 * {@link #perform(AbstractBuild, Launcher, BuildListener)} method will be
 * invoked.
 * 
 * @author Kohsuke Kawaguchi
 */
public class DocumentationBuilder extends AntTaskBuilder {
    /**
     * 
     */
    private static final int TEN_MINUTES = 1000 * 60 * 10;
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
     */
    private final Pattern ignoreSoftwareComponentRegex;

    // Fields in config.jelly must match the parameter names in the
    // "DataBoundConstructor"
    @DataBoundConstructor
    public DocumentationBuilder(final String ignoreVendorRegexp, final String ignoreSoftwareComponentRegex) {
        this.ignoreVendorRegexp = Pattern.compile(ignoreVendorRegexp);
        this.ignoreSoftwareComponentRegex = Pattern.compile(ignoreSoftwareComponentRegex);
    }

    /**
     * Return the regular expression to be used for vendors to ignore when
     * documenting development components.
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

    @Override
    public boolean perform(final AbstractBuild build, final Launcher launcher, final BuildListener listener) {
        final NWDIBuild nwdiBuild = (NWDIBuild)build;

        return new ReportGenerator(listener.getLogger(), nwdiBuild.getDevelopmentConfiguration(),
            nwdiBuild.getDevelopmentComponentFactory(), getAntHelper().getPathToWorkspace() + File.separatorChar
                + "documentation", DESCRIPTOR.getDotExecutable(), ignoreVendorRegexp).execute();
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
         */
        private Pattern ignoreVendorRegexp;

        /**
         * regular expression for ignoring development components of certain
         * software components.
         */
        private Pattern ignoreSoftwareComponentRegex;

        /**
         * Path to the 'dot' executable.
         */
        private String dotExecutable;

        public DescriptorImpl() {
            load();
        }

        /**
         * Performs on-the-fly validation of the form field 'name'.
         * 
         * @param value
         *            This parameter receives the value that the user has typed.
         * @return Indicates the outcome of the validation. This is sent to the
         *         browser.
         */
        public FormValidation doCheckVendorRegexp(@QueryParameter final String value) throws IOException,
            ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a name");
            }
            if (value.length() < 4) {
                return FormValidation.warning("Isn't the name too short?");
            }
            return FormValidation.ok();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean isApplicable(final Class<? extends AbstractProject> aClass) {
            return NWDIProject.class.equals(aClass);
        }

        /**
         * This human readable name is used in the configuration screen.
         */
        @Override
        public String getDisplayName() {
            return "NWDI Documentation Builder";
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData) throws FormException {
            try {
                setIgnoreVendorRegexp(formData.getString("ignoreVendorRegexp"));
            }
            catch (final PatternSyntaxException pse) {
                throw new FormException(pse.getLocalizedMessage(), pse, "ignoreVendorRegexp");
            }

            try {
                setIgnoreSoftwareComponentRegex(formData.getString("ignoreSoftwareComponentRegex"));
            }
            catch (final PatternSyntaxException pse) {
                throw new FormException(pse.getLocalizedMessage(), pse, "ignoreSoftwareComponentRegex");
            }

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
        // TODO Auto-generated method stub
        return null;
    }
}
