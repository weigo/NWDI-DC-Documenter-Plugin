/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Grouping of internationalization resources.
 * 
 * @author Dirk Weigenand
 */
public final class XliffGroup {
    /**
     * translation units contained in this XliffGroup.
     */
    private final Map<String, TranslationUnit> translationUnits = new HashMap<String, TranslationUnit>();

    /**
     * resource classification (button, caption, header, etc.)
     */
    private String resourceType;

    /**
     * @return the resourceType
     */
    public String getResourceType() {
        return resourceType;
    }

    /**
     * A <code>XliffGroup</code> is associated with a resource type.
     * 
     * @param resourceType
     *            the resourceType to set
     */
    public void setResourceType(final String resourceType) {
        this.resourceType = resourceType;
    }

    /**
     * Add a translation unit to this <code>XliffGroup</code>.
     * 
     * @param unit
     *            the translation unit to add.
     */
    public void addTranslationUnit(final TranslationUnit unit) {
        translationUnits.put(unit.getResourceName(), unit);
    }

    /**
     * Get all translation units associated with this <code>XliffGroup</code>.
     * 
     * @return
     */
    public Collection<TranslationUnit> getTranslationUnits() {
        return translationUnits.values();
    }

    public TranslationUnit getTranslationUnit(final String resourceName) {
        return translationUnits.get(resourceName);
    }
}
