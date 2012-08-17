/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets;

/**
 * Interface for providers of {@link DocumentationFacet}s.
 * 
 * @author Dirk Weigenand
 * @param <T>
 *            the type implementations of this interface should work on.
 */
public interface DocumentationFacetProvider<T> {
    /**
     * Create a {@link DocumentationFacet} using the type given in the parameter
     * <code>t</code>.
     * 
     * @param t
     *            the object to create documentation of a particular aspect of.
     * @return the generated <code>DocumentationFacet</code>
     */
    DocumentationFacet execute(T t);
}
