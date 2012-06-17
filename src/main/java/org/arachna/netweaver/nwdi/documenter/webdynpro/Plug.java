/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

/**
 * A plug in a Web Dynpro component.
 * 
 * @author Dirk Weigenand
 */
public final class Plug {
    /**
     * Type of plug ({@see PlugType}).
     */
    private PlugType type;

    /**
     * the name of this plug.
     */
    private String name;

    /**
     * Get the type of this plug.
     * 
     * @return the type of this plug.
     */
    public PlugType getType() {
        return type;
    }

    /**
     * Set the type of this plug.
     * 
     * @param type
     *            the type of this plug.
     */
    public void setType(final PlugType type) {
        this.type = type;
    }

    /**
     * Get the name of this plug.
     * 
     * @return the name of this plug.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this plug.
     * 
     * @param name
     *            the name of this plug.
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * Factory method for plug creation.
     * 
     * @param descriptor
     *            descriptor for the plug ('plug type':'plug name').
     * @return a new plug instance as requested per the given descriptor.
     */
    public static Plug newInstance(final String descriptor) {
        if (descriptor == null) {
            throw new IllegalArgumentException(String.format("Cannot create plug from '%s'", descriptor));
        }

        final String[] params = descriptor.split(":");
        final Plug plug = new Plug();
        plug.setType(PlugType.fromString(params[0]));
        plug.setName(params[1]);

        return plug;
    }
}
