/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

/**
 *
 * @author Dirk Weigenand
 */
public class WebResourceCollection {
    /**
     * name of this resource collection.
     */
    private String name;

    /**
     * URL pattern that identifies this web resource collection.
     */
    private String urlPattern;

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
     * @return the urlPattern
     */
    public String getUrlPattern() {
        return urlPattern;
    }

    /**
     * @param urlPattern
     *            the urlPattern to set
     */
    public void setUrlPattern(final String urlPattern) {
        this.urlPattern = urlPattern;
    }
}