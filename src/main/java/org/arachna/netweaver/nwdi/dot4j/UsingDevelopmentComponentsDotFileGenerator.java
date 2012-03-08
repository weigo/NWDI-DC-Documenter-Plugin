/**
 *
 */
package org.arachna.netweaver.nwdi.dot4j;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.arachna.dot4j.model.Attributes;
import org.arachna.dot4j.model.Edge;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.nwdi.documenter.VendorFilter;

/**
 * Generator for <code>.dot</code> files visualizing the relation to development
 * components using a development component.
 * 
 * @author Dirk Weigenand
 */
public class UsingDevelopmentComponentsDotFileGenerator extends AbstractDevelopmentComponentDotFileGenerator {
    /**
     * set of nodes already generated.
     */
    private final Set<DevelopmentComponent> generatedNodes = new HashSet<DevelopmentComponent>();

    /**
     * Vendors to ignore during graph generation.
     */
    private final VendorFilter vendorFilter;

    /**
     * Create an instance of a
     * <code>UsingDevelopmentComponentsDotFileGenerator</code> with the given
     * development component.
     * 
     * @param component
     *            development component the using development components
     *            relation shall be visualized for.
     * @param vendorFilter
     *            filter for exclusion of development components when their
     *            vendor matches.
     */
    public UsingDevelopmentComponentsDotFileGenerator(final DevelopmentComponent component,
        final VendorFilter vendorFilter) {
        super(null, component);
        this.vendorFilter = vendorFilter;
    }

    /**
     * Create an instance of a
     * <code>UsingDevelopmentComponentsDotFileGenerator</code> with the given
     * development component.
     * 
     * @param components
     *            development components the using development components shall
     *            be visualized for.
     * @param vendorFilter
     *            filter for exclusion of development components when their
     *            vendor matches.
     */
    public UsingDevelopmentComponentsDotFileGenerator(final Collection<DevelopmentComponent> components,
        final VendorFilter vendorFilter) {
        super(null, components);
        this.vendorFilter = vendorFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.arachna.netweaver.dc.analyzer.writers.AbstractDotFileGenerator#
     * generateInternal()
     */
    @Override
    protected void generateInternal() {
        for (final DevelopmentComponent component : components) {
            generateNodes(component);
        }

        for (final DevelopmentComponent component : components) {
            generateSubGraph(component);
        }

        mergeEdges();
    }

    /**
     * Recursively generate a dot language <code>subgraph</code> fragment for
     * the given development component and the DCs using it.
     * 
     * @param developmentComponent
     *            development component the dot language <code>subgraph</code>
     *            fragment wrt. DCs using it shall be generated for.
     */
    private void generateSubGraph(final DevelopmentComponent developmentComponent) {
        final String sourceNodeName = getNodeName(developmentComponent);
        String targetNodeName;
        final String label = "";

        for (final DevelopmentComponent usingComponent : developmentComponent.getUsingDevelopmentComponents()) {
            if (vendorFilter.accept(usingComponent)) {
                continue;
            }

            targetNodeName = getNodeName(usingComponent);

            // parameters to source and target node name are inverted here since
            // we want to display a usage relation
            if (!hasBeenGenerated(sourceNodeName, targetNodeName, label)) {
                final Edge edge = addEdge(getNode(targetNodeName), getNode(sourceNodeName));
                final Attributes attributes = edge.getAttributes();
                attributes.setAttribute("label", label);
                attributes.setAttribute("dir", "forward");
                attributes.setAttribute("arrowhead", "normal");
            }

            generateSubGraph(usingComponent);
        }
    }

    /**
     * Recursively generate dot language <code>node</code> fragments for the
     * given DCs and the DCs using it.
     * 
     * @param component
     *            development component dot language <code>node</code> fragments
     *            shall be generated for.
     */
    private void generateNodes(final DevelopmentComponent component) {
        if (!generatedNodes.contains(component)) {
            generateNode(component);

            generatedNodes.add(component);

            for (final DevelopmentComponent usingDC : component.getUsingDevelopmentComponents()) {
                generateNodes(usingDC);
            }
        }
    }
}
