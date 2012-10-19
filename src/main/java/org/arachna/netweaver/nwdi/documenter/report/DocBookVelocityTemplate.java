/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Enumeration of docbook templates to use to create documentation for various
 * objects in a development configuration.
 * 
 * @author Dirk Weigenand
 */
enum DocBookVelocityTemplate {
    /**
     * 
     */
    DevelopmentConfiguration("DevelopmentConfigurationDocBookTemplate.vm"),

    /**
     * 
     */
    LicenseOverView("GlobalLicenseOverviewDocBookTemplate.vm"),

    /**
     * 
     */
    Compartment("CompartmentDocBookTemplate.vm"),

    /**
     * 
     */
    DevelopmentComponent("DevelopmentComponentDocBookTemplate.vm");

    /**
     * 
     */
    private static final String TEMPLATE = "/org/arachna/netweaver/nwdi/documenter/report/%s";

    /**
     * class path resource to use as template.
     */
    private final String resource;

    /**
     * Create instance with the given resource name.
     * 
     * @param resource
     *            class path resource to use as template.
     */
    private DocBookVelocityTemplate(final String resource) {
        this.resource = resource;
    }

    /**
     * Get a reader instance for the velocity template to use for this type of
     * docbook document.
     * 
     * @return a {@link Reader} containing the velocity template for this type
     *         of docbook document.
     */
    Reader getTemplate() {
        return new InputStreamReader(getClass().getResourceAsStream(String.format(TEMPLATE, resource)));
    }
}
