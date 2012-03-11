/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.nwdi.documenter.webservices.VirtualInterfaceDefinitionProvider;

/**
 * Factory for {@link DocumentationFacetProvider}s.
 * 
 * @author Dirk Weigenand
 * 
 */
public final class DocumentationFacetProviderFactory {
    /**
     * Providers for documentation facets for development components.
     */
    private final Map<DevelopmentComponentType, Collection<DocumentationFacetProvider<DevelopmentComponent>>> providers =
        new HashMap<DevelopmentComponentType, Collection<DocumentationFacetProvider<DevelopmentComponent>>>();

    /**
     * 
     */
    public DocumentationFacetProviderFactory() {
        providers.put(DevelopmentComponentType.Java, createDocumentationFacetProvidersOfDCTypeJava());
    }

    private Collection<DocumentationFacetProvider<DevelopmentComponent>> createDocumentationFacetProvidersOfDCTypeJava() {
        final Collection<DocumentationFacetProvider<DevelopmentComponent>> providers =
            new LinkedList<DocumentationFacetProvider<DevelopmentComponent>>();
        providers.add(new VirtualInterfaceDefinitionProvider());

        return providers;
    }

    public Collection<DocumentationFacetProvider<DevelopmentComponent>> getInstance(final DevelopmentComponentType type) {
        final Collection<DocumentationFacetProvider<DevelopmentComponent>> facetProviders = providers.get(type);

        return facetProviders == null ? new ArrayList<DocumentationFacetProvider<DevelopmentComponent>>()
            : facetProviders;
    }
}
