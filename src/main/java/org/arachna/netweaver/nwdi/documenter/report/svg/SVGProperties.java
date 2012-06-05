/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report.svg;

import java.util.HashMap;
import java.util.Map;

/**
 * Properties of an SVG document.
 * 
 * @author Dirk Weigenand
 */
public final class SVGProperties {
    /**
     * map property names to their respective values.
     */
    private final Map<SVGPropertyName, String> properties = new HashMap<SVGPropertyName, String>();

    /**
     * Set the width of an SVG document.
     * 
     * @param width
     *            width of an SVG document.
     */
    public void setWidth(String width) {
        this.properties.put(SVGPropertyName.WIDTH, width);
    }

    /**
     * Set the height of an SVG document.
     * 
     * @param height
     *            height of an SVG document.
     */
    public void setHeight(String height) {
        this.properties.put(SVGPropertyName.HEIGHT, height);
    }

    /**
     * Get the value for the given property name.
     * 
     * @param propertyNMame
     *            name of SVG document property
     * @return value of property or <code>null</code>.
     */
    public String getProperty(SVGPropertyName propertyNMame) {
        return this.properties.get(propertyNMame);
    }
}
