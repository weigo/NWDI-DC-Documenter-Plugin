/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A web service function descriptor.
 * 
 * @author Dirk Weigenand
 */
public class Function {
    /**
     * exposed java method signature.
     */
    private String name;

    /**
     * name the original name was mapped to.
     */
    private String mappedName;

    /**
     * original method name.
     */
    private String originalName;

    /**
     * description of method.
     */
    private String description = "";

    /**
     * collection of parameters.
     */
    private final Collection<Parameter> parameters = new ArrayList<Parameter>();

    /**
     * the response to the web service call.
     */
    private Response response;

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
     * @return the originalName
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @param originalName
     *            the originalName to set
     */
    public void setOriginalName(final String originalName) {
        this.originalName = originalName;
    }

    /**
     * @return the parameters
     */
    public Collection<Parameter> getParameters() {
        return parameters;
    }

    /**
     * Add a parameter to this function object.
     * 
     * @param parameter
     *            the parameter to add.
     */
    public void addParameter(final Parameter parameter) {
        parameters.add(parameter);
    }

    /**
     * Returns the response of this web service method call.
     * 
     * @return the response of this web service method call.
     */
    public Response getResponse() {
        return response;
    }

    /**
     * Sets the response of this web service method call.
     * 
     * @param response
     *            the response of this web service method call to set.
     */
    public void setResponse(final Response response) {
        this.response = response;
    }

    /**
     * Returns the methods description.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set this methods description.
     * 
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
        return "Function [name=" + name + ", mappedName=" + mappedName + ", originalName=" + originalName
            + ", description=" + description + ", parameters=" + parameters + ", response=" + response + "]";
    }
}
