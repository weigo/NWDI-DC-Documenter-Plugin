/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

/**
 * @author Dirk Weigenand
 */
public final class WebDynproApplication {
    /**
     * Web Dynpro application properties.
     */
    private ApplicationProperties properties;

    /**
     * name of this Web Dynpro application.
     */
    private String name;

    /**
     * Get the application properties of this Web Dynpro application.
     * 
     * @return Web Dynpro application properties of this application
     */
    public ApplicationProperties getProperties() {
        return properties;
    }

    /**
     * Set the Web Dynpro application properties.
     * 
     * @param properties
     *            application properties for this Web Dynpro application.
     */
    public void setProperties(final ApplicationProperties properties) {
        this.properties = properties;
    }

    /**
     * Get the name of this Web Dynpro application.
     * 
     * @return the name ot this Web Dynpro application.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this Web Dynpro application.
     * 
     * @param name
     *            the name of this Web Dynpro application.
     */
    public void setName(final String name) {
        this.name = name;
    }
}
