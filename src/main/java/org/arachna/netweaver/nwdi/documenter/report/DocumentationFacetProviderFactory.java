/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseInspector;
import org.arachna.netweaver.nwdi.documenter.facets.restservices.RestServiceDocumentationFacetProvider;
import org.arachna.netweaver.nwdi.documenter.facets.webservices.VirtualInterfaceDefinitionProvider;

/**
 * Factory for {@link DocumentationFacetProvider}s.
 * 
 * @author Dirk Weigenand
 */
public class DocumentationFacetProviderFactory {
    /**
     * 
     */
    private final AntHelper antHelper;

    /**
     * Providers for documentation facets for development components.
     */
    private final Map<DevelopmentComponentType, Collection<DocumentationFacetProvider<DevelopmentComponent>>> providers =
        new HashMap<DevelopmentComponentType, Collection<DocumentationFacetProvider<DevelopmentComponent>>>();

    /**
     * Create a new instance of a <code>DocumentationFacetProviderFactory</code> .
     * 
     * Use the given {@link AntHelper} to determine the location of DCs in the workspace.
     * 
     * @param antHelper
     *            determine the location of DCs in the workspace.
     */
    public DocumentationFacetProviderFactory(final AntHelper antHelper) {
        this.antHelper = antHelper;
        providers.put(DevelopmentComponentType.Java, createProvidersForJavaDCs());
        providers.put(DevelopmentComponentType.J2EEWebModule, createProvidersForWebDCs());
        providers.put(DevelopmentComponentType.ExternalLibrary, createProvidersForExternalLibrariesDCs());
    }

    /**
     * Create documentation facet provider(s) for external library DCs.
     * 
     * @return documentation facet provider(s) for external library DCs.
     */
    private Collection<DocumentationFacetProvider<DevelopmentComponent>> createProvidersForExternalLibrariesDCs() {
        final Collection<DocumentationFacetProvider<DevelopmentComponent>> providers =
            new LinkedList<DocumentationFacetProvider<DevelopmentComponent>>();
        providers.add(new LicenseInspector(antHelper));

        return providers;
    }

    /**
     * Create documentation facet provider(s) for Java DCs.
     * 
     * @return documentation facet provider(s) for Java DCs.
     */
    private Collection<DocumentationFacetProvider<DevelopmentComponent>> createProvidersForJavaDCs() {
        final Collection<DocumentationFacetProvider<DevelopmentComponent>> providers =
            new LinkedList<DocumentationFacetProvider<DevelopmentComponent>>();
        providers.add(new VirtualInterfaceDefinitionProvider());

        return providers;
    }

    /**
     * Create documentation facet provider(s) for Java DCs.
     * 
     * @return documentation facet provider(s) for Java DCs.
     */
    private Collection<DocumentationFacetProvider<DevelopmentComponent>> createProvidersForWebDCs() {
        final Collection<DocumentationFacetProvider<DevelopmentComponent>> providers =
            new LinkedList<DocumentationFacetProvider<DevelopmentComponent>>();
        providers.add(new RestServiceDocumentationFacetProvider());

        return providers;
    }

    /**
     * Get a collection of documentation facet providers for the given type of development component.
     * 
     * @param type
     *            development component type documentation facet providers are requested for.
     * @return registerd providers for the given development component type or an empty list if there were none registered.
     */
    public Collection<DocumentationFacetProvider<DevelopmentComponent>> getInstance(final DevelopmentComponentType type) {
        final Collection<DocumentationFacetProvider<DevelopmentComponent>> facetProviders = providers.get(type);

        return facetProviders == null ? new ArrayList<DocumentationFacetProvider<DevelopmentComponent>>() : facetProviders;
    }
}
