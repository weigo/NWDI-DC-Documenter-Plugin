/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

/**
 * @author Dirk Weigenand
 */
public final class TranslationUnit {
    /**
     * name of resource this translation unit is associated with.
     */
    private String resourceName;

    /**
     * text associated with this translation unit.
     */
    private String text;

    /**
     * @return the text
     */
    public String getText() {
        return text;
    }

    /**
     * @param text
     *            the text to set
     */
    public void setText(final String text) {
        this.text = text;
    }

    /**
     * @return the resourceName
     */
    public String getResourceName() {
        return resourceName;
    }

    /**
     * @param resourceName
     *            the resourceName to set
     */
    public void setResourceName(final String resourceName) {
        this.resourceName = resourceName;
    }
}
