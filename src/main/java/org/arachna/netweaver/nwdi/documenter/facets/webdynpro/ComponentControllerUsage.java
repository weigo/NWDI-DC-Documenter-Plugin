/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

/**
 * Usage/handle of a WD component controller.
 * 
 * @author Dirk Weigenand
 */
public final class ComponentControllerUsage extends AbstractCoreReferenceType {
    /**
     * name of this used component controller.
     */
    private String name;

    /**
     * Returns the name of this WD component controller usage.
     * 
     * @return the name of this WD component controller usage.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this WD component controller usage.
     * 
     * @param name
     *            the name of this WD component controller usage.
     */
    public void setName(final String name) {
        this.name = name;
    }
}
