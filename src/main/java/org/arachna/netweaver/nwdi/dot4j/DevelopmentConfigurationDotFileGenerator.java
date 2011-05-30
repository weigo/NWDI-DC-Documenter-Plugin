/**
 *
 */
package org.arachna.netweaver.nwdi.dot4j;

import org.arachna.dot4j.model.Attributes;
import org.arachna.dot4j.model.Node;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Create a <code>.dot</code> file for a development configuration.
 *
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationDotFileGenerator extends AbstractDotFileGenerator {
    /**
     * development configuration to create the graphviz visualization for.
     */
    private final DevelopmentConfiguration configuration;

    /**
     * Create an instance of a
     * <code>DevelopmentConfigurationDotFileGenerator</code> using the given
     * development configuration.
     *
     * @param configuration
     *            development configuration to visualize
     */
    public DevelopmentConfigurationDotFileGenerator(final DevelopmentConfiguration configuration) {
        super();
        this.configuration = configuration;
    }

    /**
     * Create a graphviz visualization of compartments in a development
     * configuration.
     */
    @Override
    protected void generateInternal() {
        for (final Compartment compartment : this.configuration.getCompartments()) {
            generate(compartment);
        }
    }

    /**
     * Generate the visualization recursively.
     *
     * @param compartment
     *            the compartment to visualize dependencies for.
     */
    private void generate(final Compartment compartment) {
        final Node source = this.getOrCreateNode(compartment);

        for (final Compartment usedCompartment : compartment.getUsedCompartments()) {
            this.addEdge(this.getOrCreateNode(usedCompartment), source);
        }
    }

    /**
     * Get or create a node for a compartment. Nodes are not generated more than
     * once for a given compartment.
     *
     * @param compartment
     *            the compartment to get or create a node for.
     * @return the new or previously created node.
     */
    private Node getOrCreateNode(final Compartment compartment) {
        final String nodeName = this.getNodeName(compartment);
        Node node = this.getNode(nodeName);

        if (node == null) {
            node = this.addNode(nodeName, "");
            final Attributes attributes = node.getAttributes();
            attributes.setAttribute("label", compartment.getName());
        }

        return node;
    }

    /**
     * Return a unique identifier for a node using the given compartment.
     *
     * @param compartment
     *            compartment to generate an identifier for.
     * @return unique identifier for nodes for a given compartment.
     */
    private String getNodeName(final Compartment compartment) {
        return compartment.getVendor() + ":" + compartment.getName();
    }
}
