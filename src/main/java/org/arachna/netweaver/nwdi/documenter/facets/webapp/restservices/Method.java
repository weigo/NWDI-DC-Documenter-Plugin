/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp.restservices;

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
 */
public class Method {
    /**
     * The HTTP-method employed using this REST-service method.
     */
    private final String method;

    /**
     * The path that is possibly to add to the root path of the REST service to execute this method.
     */
    private final String path;

    /**
     * This is the description from the JavaDoc of the method defining this REST method.
     */
    private String description;

    /**
     * This map contains the parameters for this method.
     */
    private final Map<String, Parameter> parameters = new LinkedHashMap<String, Parameter>();

    /**
     * This collection contains the media types that can be consumed by this method.
     */
    private final Collection<String> consumedMediaTypes = new HashSet<String>();

    /**
     * This collection contains the media types that can be produced by this method.
     */
    private final Collection<String> producedMediaTypes = new HashSet<String>();

    /**
     * Create a method instance using the given method and path.
     *
     * @param method
     *            HTTP method name.
     * @param path
     *            path to add to the REST service path.
     */
    Method(final String method, final String path) {
        this.method = method;
        this.path = path;
    }

    /**
     * Add a method parameter definition.
     *
     * @param parameter
     *            parameter definition to add.
     */
    void add(final Parameter parameter) {
        parameters.put(parameter.getName(), parameter);
    }

    /**
     * Add the given collection of media types to this method.
     *
     * @param mediaTypes
     *            media types that can be consumed by this method.
     */
    void addConsumedMediaTypes(final Collection<String> mediaTypes) {
        for (final String mediaType : mediaTypes) {
            consumedMediaTypes.add(translateMediaType(mediaType));
        }
    }

    /**
     * Add the given collection of media types to this method.
     *
     * @param mediaTypes
     *            media types that can be produced by this method.
     */
    void addProducedMediaTypes(final Collection<String> mediaTypes) {
        for (final String mediaType : mediaTypes) {
            producedMediaTypes.add(translateMediaType(mediaType));
        }
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

    @Override
    public String toString() {
        return "Method [method=" + method + ", path=" + path + ", description=" + description + ", parameters=" + parameters + "]";
    }

    /**
     * Get the media types that this method can consume.
     *
     * @return media types that that can be consumed by this method.
     */
    public Collection<String> getConsumedMediaTypes() {
        return consumedMediaTypes;
    }

    /**
     * Get the media types that this method can produce.
     *
     * @return media types that that can be produced by this method.
     */
    public Collection<String> getProducedMediaTypes() {
        return producedMediaTypes;
    }

    private String translateMediaType(final String mediaType) {
        String result = mediaType;

        try {
            result = MediaType.valueOf(mediaType.replaceAll("MediaType\\.", "")).getMediaType();
        }
        catch (final IllegalArgumentException e) {

        }

        return result;
    }

    enum MediaType {
        /**
         *
         */
        APPLICATION_XML("application/xml"),

        /**
         *
         */
        APPLICATION_XML_TYPE("application/xml"),

        /**
         *
         */
        APPLICATION_ATOM_XML("application/atom+xml"),

        /**
         *
         */
        APPLICATION_ATOM_XML_TYPE("application/atom+xml"),

        /**
         *
         */
        APPLICATION_XHTML_XML("application/xhtml+xml"),

        /**
         *
         */
        APPLICATION_XHTML_XML_TYPE("application/xhtml+xml"),

        /**
         *
         */
        APPLICATION_SVG_XML("application/svg+xml"),

        /**
         *
         */
        APPLICATION_SVG_XML_TYPE("application/svg+xml"),

        /**
         *
         */
        APPLICATION_JSON("application/json"),

        /**
         *
         */
        APPLICATION_JSON_TYPE("application/json"),

        /**
         *
         */
        APPLICATION_FORM_URLENCODED("application/x-www-form-urlencoded"),

        /**
         *
         */
        APPLICATION_FORM_URLENCODED_TYPE("application/x-www-form-urlencoded"),

        /**
         *
         */
        MULTIPART_FORM_DATA("multipart/form-data"),

        /**
         *
         */
        MULTIPART_FORM_DATA_TYPE("multipart/form-data"),

        /**
         *
         */
        APPLICATION_OCTET_STREAM("application/octet-stream"),

        /**
         *
         */
        APPLICATION_OCTET_STREAM_TYPE("application/octet-stream"),

        /**
         *
         */
        TEXT_PLAIN("text/plain"),

        /**
         *
         */
        TEXT_PLAIN_TYPE("text/plain"),

        /**
         *
         */
        TEXT_XML("text/xml"),

        /**
         *
         */
        TEXT_XML_TYPE("text/xml"),

        /**
         *
         */
        TEXT_HTML("text/html"),

        /**
         *
         */
        TEXT_HTML_TYPE("text/html");

        /**
         *
         */
        private final String mediaType;

        /**
         *
         * @param mediaType
         */
        private MediaType(final String mediaType) {
            this.mediaType = mediaType;
        }

        /**
         * @return the mediaType
         */
        public String getMediaType() {
            return mediaType;
        }
    }
}