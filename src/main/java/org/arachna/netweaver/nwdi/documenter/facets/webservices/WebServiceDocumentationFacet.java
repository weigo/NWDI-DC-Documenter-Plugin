/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webservices;

import java.util.List;

import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;

/**
 * A facet providing details of a web service.
 * 
 * @author Dirk Weigenand
 */
public class WebServiceDocumentationFacet extends DocumentationFacet {
    /**
     * Name of this facet.
     */
    private static final String NAME = "webServices";

    /**
     * Create a new <code>WebServiceDocumentationFacet</code> instance using the
     * given definitions of virtual interfaces.
     * 
     * @param interfaces
     *            list of virtual interface definitions.
     */
    public WebServiceDocumentationFacet(final List<VirtualInterfaceDefinition> interfaces) {
        super(NAME, interfaces);
    }
}
