/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProviderFactory;
import org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseInspector.LicenseDescriptor;

/**
 * @author Dirk Weigenand
 * 
 */
public final class GlobalLicenseOverviewReportGenerator {
    /**
     * velocity engine to generate a report for licenses of external libraries
     * used in a development configuration.
     */
    private final VelocityEngine velocityEngine;

    /**
     * {@link ResourceBundle} for internationalization of reports.
     */
    private final ResourceBundle bundle;

    /**
     * factory for providers of documentation facets.
     */
    private final DocumentationFacetProviderFactory documentationFacetProviderFactory;

    /**
     * Create a <code>CompartmentReportGenerator</code> using the given
     * {@link VelocityEngine}, and resource bundle.
     * 
     * The given {@link ResourceBundle} is used for internationalization.
     * 
     * @param documentationFacetProviderFactory
     *            factory for documentation facet providers.
     * @param velocityEngine
     *            VelocityEngine used to transform template.
     * @param bundle
     *            the ResourceBundle used for I18N.
     */
    public GlobalLicenseOverviewReportGenerator(
        final DocumentationFacetProviderFactory documentationFacetProviderFactory, final VelocityEngine velocityEngine,
        final ResourceBundle bundle) {
        this.documentationFacetProviderFactory = documentationFacetProviderFactory;
        this.velocityEngine = velocityEngine;
        this.bundle = bundle;
    }

    /**
     * Generate documentation for the given development component into the given
     * writer object.
     * 
     * @param writer
     *            writer to generate documentation into.
     * @param configuration
     *            development configuration whose external libraries are to
     *            document.
     * @param additionalContext
     *            additional context attributes supplied externally
     * @param template
     *            a reader to supply the used template
     */
    public void execute(final Writer writer, final DevelopmentConfiguration configuration,
        final Map<String, Object> additionalContext, final Reader template) {
        final Context context = new VelocityContext();
        context.put("configuration", configuration);
        context.put("bundle", bundle);
        context.put("bundleHelper", new BundleHelper(bundle));

        for (final Map.Entry<String, Object> entry : additionalContext.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }

        context.put("descriptors", getLicenseDescriptors(configuration));

        velocityEngine.evaluate(context, writer, "", template);

        try {
            writer.close();
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Get {@link LicenseDescriptor} objects for each development component of
     * type {@see DevelopmentComponentType#ExternalLibrary}.
     * 
     * @param configuration
     *            development configuration whose external library components
     *            shall be inspected.
     * @return a list of all license descriptors found (grouped by development
     *         component).
     */
    @SuppressWarnings("unchecked")
    protected List<ExternalLibraryComponentDescriptor> getLicenseDescriptors(
        final DevelopmentConfiguration configuration) {
        final DocumentationFacetProvider<DevelopmentComponent> facetProvider =
            documentationFacetProviderFactory.getInstance(DevelopmentComponentType.ExternalLibrary).iterator().next();
        final List<ExternalLibraryComponentDescriptor> descriptors =
            new LinkedList<ExternalLibraryComponentDescriptor>();

        for (final Compartment compartment : configuration.getCompartments()) {
            for (final DevelopmentComponent component : compartment
                .getDevelopmentComponents(DevelopmentComponentType.ExternalLibrary)) {
                descriptors.add(new ExternalLibraryComponentDescriptor(component,
                    (Collection<LicenseDescriptor>)facetProvider.execute(component).getContent()));
            }
        }

        return descriptors;
    }

    /**
     * Descriptor for external libraries used in a component of this type.
     * 
     * @author Dirk Weigenand
     */
    class ExternalLibraryComponentDescriptor {
        /**
         * Component.
         */
        private final DevelopmentComponent component;

        /**
         * License descriptors.
         */
        private final Collection<LicenseDescriptor> licenseDescriptors = new LinkedList<LicenseDescriptor>();

        /**
         * Create a new descriptor instance with the given component and license
         * descriptors.
         * 
         * @param component
         *            component containing external libraries.
         * @param licenseDescriptors
         *            descriptors for licenses of external libraries.
         */
        ExternalLibraryComponentDescriptor(final DevelopmentComponent component,
            final Collection<LicenseDescriptor> licenseDescriptors) {
            this.component = component;
            getLicenseDescriptors().addAll(licenseDescriptors);
        }

        /**
         * @return the component
         */
        public DevelopmentComponent getComponent() {
            return component;
        }

        /**
         * @return the licenseDescriptors
         */
        public Collection<LicenseDescriptor> getLicenseDescriptors() {
            return licenseDescriptors;
        }
    }
}
