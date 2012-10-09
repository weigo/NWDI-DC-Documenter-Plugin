/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

/**
 * Base class for types with a handle on a {@link CoreReference}.
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractCoreReferenceType {
    /**
     * Reference to a WebDynpro core type.
     */
    private CoreReference coreReference;

    /**
     * Returns the reference instance this WD component interface refers to.
     * 
     * @return reference instance this WD component interface
     */
    public final CoreReference getCoreReference() {
        return coreReference;
    }

    /**
     * Set the handle to the WD core type this instance represents.
     * 
     * @param coreReference
     *            handle to a WD core type.
     */
    public final void setCoreReference(final CoreReference coreReference) {
        this.coreReference = coreReference;
    }
}
