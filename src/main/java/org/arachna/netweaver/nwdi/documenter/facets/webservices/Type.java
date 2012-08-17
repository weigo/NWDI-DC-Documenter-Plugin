/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webservices;

/**
 * A web service method parameters type.
 * 
 * @author Dirk Weigenand
 */
public class Type {
    /**
     * name of converted type.
     */
    private String name = "";

    /**
     * the original type name.
     */
    private String originalType = "";

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
     * @return the originalType
     */
    public String getOriginalType() {
        return originalType;
    }

    /**
     * @param originalType
     *            the originalType to set
     */
    public void setOriginalType(final String originalType) {
        this.originalType = originalType;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Type [name=" + name + ", originalType=" + originalType + "]";
    }
}
