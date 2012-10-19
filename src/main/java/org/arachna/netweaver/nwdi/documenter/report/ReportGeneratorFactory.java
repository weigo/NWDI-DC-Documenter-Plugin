/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

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
     * @param antHelper
     *            helper class for determining development component related
     *            information (e.g. source paths)
     * @param dcFactory
     *            Registry for development components.
     * @param velocity
     *            template engine.
     * @param resourceBundle
     *            Resource bundle for texts/I18N.
     */
    public ReportGeneratorFactory(final AntHelper antHelper, final DevelopmentComponentFactory dcFactory,
        final VelocityEngine velocity, final ResourceBundle resourceBundle) {
        facetProviderFactory = new DocumentationFacetProviderFactory(antHelper);
        this.dcFactory = dcFactory;
        this.velocity = velocity;
        this.resourceBundle = resourceBundle;
    }

    /**
     * Create a new generator for documentation of development components.
     * 
     * @param component
     *            development component to generate report for.
     * @return a new instance of a generator for documentation of development
     *         components.
     */
    public ReportGenerator create(final DevelopmentComponent component) {
        return new DevelopmentComponentReportGenerator(facetProviderFactory, dcFactory, velocity, resourceBundle,
            component);
    }

    /**
     * Create a new generator for documentation of a development configuration.
     * 
     * @param configuration
     *            development configuration to generate report for.
     * @return a new instance of a generator for documentation of a development
     *         configuration.
     */
    public ReportGenerator create(final DevelopmentConfiguration configuration) {
        return new DevelopmentConfigurationReportGenerator(velocity, resourceBundle, configuration);
    }

    /**
     * Create a new generator for documentation of compartment.
     * 
     * @param compartment
     *            compartment to generate report for.
     * @return a new instance of a generator for documentation of a compartment.
     */
    public ReportGenerator create(final Compartment compartment) {
        return new CompartmentReportGenerator(velocity, resourceBundle, compartment);
    }

    /**
     * Create a new generator for documentation of external libraries contained
     * in a track.
     * 
     * @param configuration
     *            development configuration to create license overview of used
     *            external libraries for.
     * @return a new instance of a generator for documentation of external
     *         libraries contained in a track.
     */
    public ReportGenerator createGlobalLicenseOverviewReportGenerator(final DevelopmentConfiguration configuration) {
        return new GlobalLicenseOverviewReportGenerator(facetProviderFactory, velocity, resourceBundle, configuration);
    }
}
