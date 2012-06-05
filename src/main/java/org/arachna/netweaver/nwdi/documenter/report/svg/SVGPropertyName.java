/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report.svg;

/**
 * @author Dirk Weigenand (G526521)
 * 
 */
public enum SVGPropertyName {
    WIDTH("width"), HEIGHT("height");

    private final String name;

    SVGPropertyName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
