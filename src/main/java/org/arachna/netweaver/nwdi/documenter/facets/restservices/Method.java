/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.restservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A REST-Method.
 * 
 * @author Dirk Weigenand
 * 
 */
public class Method {
    /**
     * The HTTP-method employed using this REST-service method.
     */
    private final String method;
    private final String path;
    private String description;
    private final Map<String, Parameter> parameters = new LinkedHashMap<String, Parameter>();
    private final Collection<String> consumedMediaTypes = new HashSet<String>();
    private final Collection<String> producedMediaTypes = new HashSet<String>();

    public Method(final String method, final String path) {
        this.method = method;
        this.path = path;
    }

    public void add(final Parameter parameter) {
        parameters.put(parameter.getName(), parameter);
    }

    void addConsumedMediaTypes(final Collection<String> mediaTypes) {
        this.consumedMediaTypes.addAll(mediaTypes);
    }

    void addProducedMediaTypes(final Collection<String> mediaTypes) {
        this.getProducedMediaTypes().addAll(mediaTypes);
    }

    /**
     * @return the method
     */
    public String getMethod() {
        return method;
    }

    /**
     * @return the path
     */
    public String getPath() {
        return path;
    }

    /**
     * @return the parameters
     */
    public List<Parameter> getParameters() {
        return new ArrayList<Parameter>(parameters.values());
    }

    /**
     * Get parameter by name.
     * 
     * @param name
     *            name of requested parameter.
     * @return the requested parameter iff found, <code>null</code> otherwise.
     */
    public Parameter getParameter(final String name) {
        return parameters.get(name);
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
     * {@inheritdoc}
     */
    @Override
    public String toString() {
        return "Method [method=" + method + ", path=" + path + ", description=" + description + ", parameters=" + parameters + "]";
    }

    /**
     * @return
     */
    public Collection<String> getConsumedMediaTypes() {
        return consumedMediaTypes;
    }

    /**
     * @return the producedMediaTypes
     */
    public Collection<String> getProducedMediaTypes() {
        return producedMediaTypes;
    }
}
