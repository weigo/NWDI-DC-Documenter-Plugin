/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter;

/**
 * Enum for encupsulation of Confluence version specific converter configuration.
 * 
 * @author Dirk Weigenand
 */
public enum ConfluenceVersion {
    V3(3, "confluence-pre4.xsl"),
    V4(4, "confluence-4-new-storage-format.xsl"),
    V5(5, "confluence-4-new-storage-format.xsl");

    /**
     * XSL-Stylesheet to use for conversion of docbbok documents.
     */
    private final String template;

    /**
     * confluence major version to match to converter configuration.
     */
    private final int majorVersion;

    /**
     * Create converter configuration specific to given major confluence version with the given template.
     * 
     * @param majorVersion
     *            confluence major version matching this converter configuration.
     * @param template
     *            template to use for conversion of docbook documents.
     */
    private ConfluenceVersion(final int majorVersion, final String template) {
        this.majorVersion = majorVersion;
        this.template = template;
    }

    /**
     * Determine converter configuration to use for the given confluence major version.
     * 
     * @param majorVersion
     *            confluence major version
     * @return converter configuration matching the given confluence major version.
     */
    public static ConfluenceVersion fromConfluenceVersion(final int majorVersion) {
        for (final ConfluenceVersion version : values()) {
            if (version.majorVersion == majorVersion) {
                return version;
            }
        }

        if (V3.majorVersion > majorVersion) {
            return V3;
        }

        return V5;
    }

    /**
     * Get the XSL stylesheet template to convert docbook documents.
     * 
     * @return name of XSL stylesheet template
     */
    public String getTemplate() {
        return template;
    }
}
