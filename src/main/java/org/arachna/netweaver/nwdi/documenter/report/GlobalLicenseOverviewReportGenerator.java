/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentByNameComparator;
import org.arachna.netweaver.dc.types.DevelopmentComponentByTypeFilter;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.IDevelopmentComponentFilter;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.netweaver.nwdi.documenter.facets.librarydc.License;
import org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseComparator;
import org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseDescriptor;

/**
 * @author Dirk Weigenand
 * 
 */
public final class GlobalLicenseOverviewReportGenerator extends AbstractReportGenerator {
    /**
     * factory for providers of documentation facets.
     */
    private final DocumentationFacetProviderFactory documentationFacetProviderFactory;

    /**
     * development configuration to create license overview of used external
     * libraries for.
     */
    private final DevelopmentConfiguration configuration;

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
     * @param configuration
     *            development configuration to create license overview of used
     *            external libraries for.
     */
    public GlobalLicenseOverviewReportGenerator(
        final DocumentationFacetProviderFactory documentationFacetProviderFactory, final VelocityEngine velocityEngine,
        final ResourceBundle bundle, final DevelopmentConfiguration configuration) {
        super(velocityEngine, bundle);
        this.documentationFacetProviderFactory = documentationFacetProviderFactory;
        this.configuration = configuration;
    }

    /**
     * Generate documentation for the given development component into the given
     * writer object.
     * 
     * @param writer
     *            writer to generate documentation into.
     * @param additionalContext
     *            additional context attributes supplied externally
     * @param template
     *            a reader to supply the used template
     */
    public void execute(final Writer writer, final Map<String, Object> additionalContext, final Reader template) {
        final Context context = createContext(additionalContext);
        context.put("configuration", configuration);
        context.put("licenseContainer", getLicenseContainer(configuration));

        evaluate(context, writer, template);
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
    protected ExternalLibraryComponentDescriptorContainer getLicenseContainer(
        final DevelopmentConfiguration configuration) {
        // FIXME: for tracks using external library DCs built in other tracks
        // this logic is not sufficient! DCs of type EAR, JEE Library etc.
        // should be inspected too...
        final DocumentationFacetProvider<DevelopmentComponent> facetProvider =
            documentationFacetProviderFactory.getInstance(DevelopmentComponentType.ExternalLibrary).iterator().next();

        final ExternalLibraryComponentDescriptorContainer container = new ExternalLibraryComponentDescriptorContainer();
        final IDevelopmentComponentFilter filter =
            new DevelopmentComponentByTypeFilter(DevelopmentComponentType.ExternalLibrary,
                DevelopmentComponentType.J2EEEnterpriseApplication, DevelopmentComponentType.J2EEServerComponentLibrary);

        for (final Compartment compartment : configuration.getCompartments()) {
            for (final DevelopmentComponent component : compartment.getDevelopmentComponents(filter)) {
                container.add(component, (Collection<LicenseDescriptor>)facetProvider.execute(component).getContent());
            }
        }

        return container;
    }

    public class ExternalLibraryComponentDescriptorContainer {
        /**
         * Mapping of licenses to development components and their external
         * libraries.
         */
        private final Map<License, Map<DevelopmentComponent, ExternalLibraryComponentDescriptor>> licenseMap =
            new HashMap<License, Map<DevelopmentComponent, ExternalLibraryComponentDescriptor>>();

        /**
         * Return URL to the used license or a URL to a google search.
         * 
         * @param descriptor
         *            the {@link LicenseDescriptor} the URL is requested for.
         * @return URL to the used license or a URL to a google search.
         */
        public String getLicenseURL(final LicenseDescriptor descriptor) {
            final License license = descriptor.getLicense();
            final String url = license.getUrl();

            return license.equals(License.Other) || license.equals(License.None) ? url + descriptor.getArchive() : url;
        }

        /**
         * Return URL to the used license or a URL to a google search. The
         * returned string can be used as XML attribute value.
         * 
         * @param descriptor
         *            the {@link LicenseDescriptor} the URL is requested for.
         * @return URL to the used license or a URL to a google search.
         */
        public String getEscapedLicenseURL(final LicenseDescriptor descriptor) {
            return StringEscapeUtils.escapeXml(getLicenseURL(descriptor));
        }

        /**
         * Add all given license descriptors to the license library mapping.
         * 
         * @param component
         *            component associated with libraries
         * @param descriptors
         */
        public void add(final DevelopmentComponent component, final Collection<LicenseDescriptor> descriptors) {
            for (final LicenseDescriptor descriptor : descriptors) {
                Map<DevelopmentComponent, ExternalLibraryComponentDescriptor> componentDescriptors =
                    licenseMap.get(descriptor.getLicense());

                if (componentDescriptors == null) {
                    componentDescriptors = new HashMap<DevelopmentComponent, ExternalLibraryComponentDescriptor>();
                    licenseMap.put(descriptor.getLicense(), componentDescriptors);
                }

                ExternalLibraryComponentDescriptor componentDescriptor = componentDescriptors.get(component);

                if (componentDescriptor == null) {
                    componentDescriptor = new ExternalLibraryComponentDescriptor(component);
                    componentDescriptors.put(component, componentDescriptor);
                }

                componentDescriptor.getLicenseDescriptors().add(descriptor);
            }
        }

        public List<DevelopmentComponent> getComponents(final License license) {
            final Map<DevelopmentComponent, ExternalLibraryComponentDescriptor> componentDescriptors =
                licenseMap.get(license);
            final List<DevelopmentComponent> components =
                new ArrayList<DevelopmentComponent>(componentDescriptors.keySet());

            Collections.sort(components, new DevelopmentComponentByNameComparator());

            return components;
        }

        public ExternalLibraryComponentDescriptor getExternalLibraryComponentDescriptor(final License license,
            final DevelopmentComponent component) {
            final Map<DevelopmentComponent, ExternalLibraryComponentDescriptor> componentDescriptors =
                licenseMap.get(license);

            ExternalLibraryComponentDescriptor componentDescriptor = null;

            if (componentDescriptors != null) {
                componentDescriptor = componentDescriptors.get(component);
            }

            if (componentDescriptor == null) {
                componentDescriptor = new ExternalLibraryComponentDescriptor(component);
            }

            return componentDescriptor;
        }

        public List<License> getLicenses() {
            final List<License> licenses = new ArrayList<License>(licenseMap.keySet());

            Collections.sort(licenses, new LicenseComparator());

            return licenses;
        }

        /**
         * Descriptor for external libraries used in a component of this type.
         * 
         * @author Dirk Weigenand
         */
        public class ExternalLibraryComponentDescriptor {
            /**
             * Component.
             */
            private final DevelopmentComponent component;

            /**
             * License descriptors.
             */
            private final Collection<LicenseDescriptor> licenseDescriptors = new LinkedList<LicenseDescriptor>();

            /**
             * Create a new descriptor instance with the given component and
             * license descriptors.
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

            ExternalLibraryComponentDescriptor(final DevelopmentComponent component) {
                this.component = component;
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

            /**
             * {@inheritDoc}
             */
            @Override
            public String toString() {
                return "ExternalLibraryComponentDescriptor [component=" + component.getNormalizedName("~")
                    + ", licenseDescriptors=" + licenseDescriptors + "]";
            }
        }
    }
}
