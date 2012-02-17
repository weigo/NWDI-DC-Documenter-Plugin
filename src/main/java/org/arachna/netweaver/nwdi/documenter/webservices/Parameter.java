/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

/**
 * Parameter of a web service method.
 * 
 * @author Dirk Weigenand
 */
public class Parameter {
    /**
     * name of this parameter.
     */
    private String name;

    /**
     * name this parameter was mapped on.
     */
    private String mappedName;

    /**
     * the type of this parameter.
     */
    private Type type;

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
     * @return the mappedName
     */
    public String getMappedName() {
        return mappedName;
    }

    /**
     * @param mappedName
     *            the mappedName to set
     */
    public void setMappedName(String mappedName) {
        this.mappedName = mappedName;
    }

    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }

    /**
     * @param type
     *            the type to set
     */
    public void setType(Type type) {
        this.type = type;
    }
}
