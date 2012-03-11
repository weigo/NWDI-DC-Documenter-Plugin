/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import org.arachna.netweaver.dc.types.DevelopmentComponent;

/**
 * A <code>DocumentationFacet</code> presents information about a particular
 * part of {@link DevelopmentComponent}.
 * 
 * @author Dirk Weigenand
 */
public class DocumentationFacet {
    /**
     * Name of this facet.
     */
    private final String name;

    /**
     * Content of this facet.
     */
    private final Object content;

    /**
     * Create a new <code>DocumentationFacet</code> instance.
     * 
     * @param name
     *            name of the facet.
     * @param content
     *            content of the facet.
     */
    public DocumentationFacet(final String name, final Object content) {
        this.name = name;
        this.content = content;
    }

    /**
     * Gets the name of the facet.
     * 
     * @return the name to be used when referencing the contents of this facet.
     */
    String getName() {
        return name;
    }

    /**
     * Gets the content of this facet.
     * 
     * @return the content which is to be transformed into documentation.
     */
    Object getContent() {
        return content;
    }
}
