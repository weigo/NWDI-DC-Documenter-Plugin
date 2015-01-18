/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

/**
 * Descriptor for servlet definitions in <code>web.xml</code>.
 *
 * @author Dirk Weigenand
 */
public class ServletDescriptor {
    /**
     *
     */
    public static final String JERSEY_SERVLET_CLASS = "com.sun.jersey.spi.container.servlet.ServletContainer";

    /**
     *
     */
    private String name;

    /**
     *
     */
    private String clazz;

    /**
     *
     */
    private String servletMapping;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the clazz
     */
    public String getClazz() {
        return clazz;
    }

    /**
     * @param clazz
     *            the clazz to set
     */
    public void setClazz(final String clazz) {
        this.clazz = clazz;
    }

    /**
     * @return the servletMapping
     */
    public String getServletMapping() {
        return servletMapping;
    }

    /**
     * @param servletMapping
     *            the servletMapping to set
     */
    public void setServletMapping(final String servletMapping) {
        this.servletMapping = servletMapping;
    }
}
