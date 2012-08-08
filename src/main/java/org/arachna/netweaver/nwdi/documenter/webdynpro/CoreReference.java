/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

/**
 * @author Dirk Weigenand
 */
final class CoreReference {
    /**
     * package of this reference.
     */
    private String packageName;

    /**
     * reference name.
     */
    private String name;

    /**
     * 
     */
    private ReferenceType type;

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName
     *            the packageName to set
     */
    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

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
     * @return the type
     */
    public ReferenceType getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(final ReferenceType type) {
        this.type = type;
    }
}
