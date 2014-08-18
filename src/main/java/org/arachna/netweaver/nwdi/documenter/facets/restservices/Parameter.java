/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.restservices;

import org.apache.commons.lang.StringUtils;

/**
 * A parameter for a REST service method.
 * 
 * @author Dirk Weigenand
 */
public class Parameter {
    private final ParameterType type;
    private final String name;
    private String description;

    public Parameter(final ParameterType type, final String name) {
        this.type = type;
        this.name = name;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return StringUtils.trimToEmpty(description);
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the type
     */
    public ParameterType getType() {
        return type;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public String toString() {
        return "Parameter [type=" + type + ", name=" + name + ", description=" + description + "]";
    }
}
