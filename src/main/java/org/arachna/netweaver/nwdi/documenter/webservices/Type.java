/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

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
    public void setName(String name) {
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
    public void setOriginalType(String originalType) {
        this.originalType = originalType;
    }
}
