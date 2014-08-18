/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.restservices;

import java.util.List;

import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;

/**
 * Facet for REST service documentation.
 * 
 * @author Dirk Weigenand
 */
public class RestServiceDocumentationFacet extends DocumentationFacet {
    /**
     * Create a new <code>WebServiceDocumentationFacet</code> instance using the given definitions of virtual interfaces.
     * 
     * @param services
     *            list of virtual interface definitions.
     */
    public RestServiceDocumentationFacet(final List<RestService> services) {
        super("RESTServices", services);
    }
}
