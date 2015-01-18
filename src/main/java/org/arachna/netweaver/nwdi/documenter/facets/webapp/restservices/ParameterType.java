/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp.restservices;

/**
 * Enumeration of possible parameter types for REST service methods.
 * 
 * @author Dirk Weigenand
 */
public enum ParameterType {
    /**
     * A path parameter.
     */
    PathParam,

    /**
     * A query parameter.
     */
    QueryParam,

    /**
     * A parameter from parsing a POST request.
     */
    Param;
}
