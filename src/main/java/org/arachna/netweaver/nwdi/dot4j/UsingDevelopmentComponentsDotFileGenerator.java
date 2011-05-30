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
     * Create an instance of a
     * <code>UsingDevelopmentComponentsDotFileGenerator</code> with the given
     * development component.
     *
     * @param component
     *            development component the using development components
     *            relation shall be visualized for.
     */
    public UsingDevelopmentComponentsDotFileGenerator(final DevelopmentComponent component) {
        super(null, component);
    }

    /**
     * Create an instance of a
     * <code>UsingDevelopmentComponentsDotFileGenerator</code> with the given
     * development component.
     *
     * @param components
     *            development components the using development components shall
     *            be visualized for.
     */
    public UsingDevelopmentComponentsDotFileGenerator(final Collection<DevelopmentComponent> components) {
        super(null, components);
    }

    /*
     * (non-Javadoc)
     *
     * @seeorg.arachna.netweaver.dc.analyzer.writers.AbstractDotFileGenerator#
     * generateInternal()
     */
    @Override
    protected void generateInternal() {
        for (final DevelopmentComponent component : this.components) {
            this.generateNodes(component);
        }

        for (final DevelopmentComponent component : this.components) {
            this.generateSubGraph(component);
        }
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
            // TODO: use configurable filter
            if (usingComponent.getVendor().startsWith("sap.com")) {
                continue;
            }

            // TODO: fix PublicPartReference
            targetNodeName = getNodeName(usingComponent);

            // parameters to source and target node name are inverted here since
            // we want to display a usage relation
            if (!this.hasBeenGenerated(sourceNodeName, targetNodeName, label)) {
                final Edge edge = this.addEdge(this.getNode(targetNodeName), this.getNode(sourceNodeName));
                final Attributes attributes = edge.getAttributes();
                attributes.setAttribute("label", label);
                attributes.setAttribute("dir", "forward");
                attributes.setAttribute("arrowhead", "normal");
            }

            this.generateSubGraph(usingComponent);
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
        if (!this.generatedNodes.contains(component)) {
            this.generateNode(component);

            this.generatedNodes.add(component);

            for (final DevelopmentComponent usingDC : component.getUsingDevelopmentComponents()) {
                generateNodes(usingDC);
            }
        }
    }
}
