/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

/**
 * @author Dirk Weigenand
 */
public final class TranslationUnit {
    /**
     * name of resource this translation unit is associated with.
     */
    private final String resourceName;

    /**
     * text associated with this translation unit.
     */
    private String text;

    /**
     * Create a new translation unit instance with the name of the associated ressource.
     * 
     * @param resourceName
     */
    public TranslationUnit(final String resourceName) {
        this.resourceName = resourceName;
    }

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
}
