/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webservices;

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
     * description of a parameter.
     */
    private String description = "";

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
    public void setName(final String name) {
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
    public void setMappedName(final String mappedName) {
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
    public void setType(final Type type) {
        this.type = type;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Parameter [name=" + name + ", mappedName=" + mappedName + ", description=" + description + ", type="
            + type + "]";
    }
}
