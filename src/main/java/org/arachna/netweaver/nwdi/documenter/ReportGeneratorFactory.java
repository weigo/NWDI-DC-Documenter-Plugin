/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter;

import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProviderFactory;
import org.arachna.netweaver.nwdi.documenter.report.CompartmentReportGenerator;
import org.arachna.netweaver.nwdi.documenter.report.DevelopmentComponentReportGenerator;
import org.arachna.netweaver.nwdi.documenter.report.DevelopmentConfigurationReportGenerator;
import org.arachna.netweaver.nwdi.documenter.report.GlobalLicenseOverviewReportGenerator;

/**
 * Factory for development component report generators. Bundles knowledge to
 * create those generators based on the given velocity template.
 * 
 * @author Dirk Weigenand
 */
public final class ReportGeneratorFactory {
    /**
     * Factory for the various documentable facets of a development component.
     */
    private final DocumentationFacetProviderFactory facetProviderFactory;

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * template engine.
     */
    private final VelocityEngine velocity;

    /**
     * Resource bundle for texts/I18N.
     */
    private final ResourceBundle resourceBundle;

    /**
     * Create an instance of the factory using the given factory for
     * documentation facets, DC registry, template engine and resource bundle.
     * 
     * @param facetProviderFactory
     *            Factory for the various documentable facets of a development
     *            component.
     * @param dcFactory
     *            Registry for development components.
     * @param velocity
     *            template engine.
     * @param resourceBundle
     *            Resource bundle for texts/I18N.
     */
    public ReportGeneratorFactory(final DocumentationFacetProviderFactory facetProviderFactory,
        final DevelopmentComponentFactory dcFactory, final VelocityEngine velocity, final ResourceBundle resourceBundle) {
        this.facetProviderFactory = facetProviderFactory;
        this.dcFactory = dcFactory;
        this.velocity = velocity;
        this.resourceBundle = resourceBundle;
    }

    /**
     * Create a new generator for documentation of development components.
     * 
     * @return a new instance of a generator for documentation of development
     *         components.
     */
    public DevelopmentComponentReportGenerator createDevelopmentComponentReportGenerator() {
        return new DevelopmentComponentReportGenerator(facetProviderFactory, dcFactory, velocity, resourceBundle);
    }

    /**
     * Create a new generator for documentation of a development configuration.
     * 
     * @return a new instance of a generator for documentation of a development
     *         configuration.
     */
    public DevelopmentConfigurationReportGenerator createDevelopmentConfigurationReportGenerator() {
        return new DevelopmentConfigurationReportGenerator(velocity, resourceBundle);
    }

    /**
     * Create a new generator for documentation of compartment.
     * 
     * @return a new instance of a generator for documentation of a compartment.
     */
    public CompartmentReportGenerator createCompartmentReportGenerator() {
        return new CompartmentReportGenerator(velocity, resourceBundle);
    }

    /**
     * Create a new generator for documentation of external libraries contained
     * in a track.
     * 
     * @return a new instance of a generator for documentation of external
     *         libraries contained in a track.
     */
    public GlobalLicenseOverviewReportGenerator createGlobalLicenseOverviewReportGenerator() {
        return new GlobalLicenseOverviewReportGenerator(facetProviderFactory, velocity, resourceBundle);
    }
}
