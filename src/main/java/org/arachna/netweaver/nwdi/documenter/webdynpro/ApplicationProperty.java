/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

/**
 * @author Dirk Weigenand
 */
public final class ApplicationProperty {
    /**
     * name of property.
     */
    private final String name;

    /**
     * value of property.
     */
    private final String value;

    /**
     * Create a new application property.
     * 
     * @param name
     *            name of property.
     * @param value
     *            value of property.
     */
    ApplicationProperty(final String name, final String value) {
        this.name = name;
        this.value = value;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the value
     */
    public String getValue() {
        return value;
    }
}
