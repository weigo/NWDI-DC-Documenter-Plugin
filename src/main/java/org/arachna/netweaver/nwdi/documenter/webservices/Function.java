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
     * collection of parameters.
     */
    private Collection<Parameter> parameters = new ArrayList<Parameter>();

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
     * @return the originalName
     */
    public String getOriginalName() {
        return originalName;
    }

    /**
     * @param originalName
     *            the originalName to set
     */
    public void setOriginalName(String originalName) {
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
    public void addParameter(Parameter parameter) {
        this.parameters.add(parameter);
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
    public void setResponse(Response response) {
        this.response = response;
    }
}
