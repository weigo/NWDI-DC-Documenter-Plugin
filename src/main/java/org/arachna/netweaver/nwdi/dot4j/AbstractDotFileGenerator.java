/**
 *
 */
package org.arachna.netweaver.nwdi.dot4j;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.arachna.dot4j.DotGenerator;
import org.arachna.dot4j.model.Attributes;
import org.arachna.dot4j.model.CommonEdgeMergeAlgorithm;
import org.arachna.dot4j.model.Edge;
import org.arachna.dot4j.model.Graph;
import org.arachna.dot4j.model.Node;

/**
 * Base class for generators of <code>.dot</code> files.
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractDotFileGenerator implements DotFileGenerator {
    /**
     * Model for dependency graph.
     */
    private Graph graph;

    /**
     * house keeping of node names already generated.
     */
    private final Map<String, Node> nodeNames = new HashMap<String, Node>();

    private final Map<String, Graph> groupNames = new HashMap<String, Graph>();

    /**
     * {@inheritDoc}
     */
    public String generate() {
        graph = new Graph();
        final Attributes attributes = graph.getAttributes();
        attributes.setAttribute("shape", "record");
        attributes.setAttribute("rankdir", "LR");
        attributes.setAttribute("ranksep", "equally");
        attributes.setAttribute("compound", "true");

        graph.getNodeAttributes().setAttribute("shape", "record");

        generateInternal();

        final StringWriter result = new StringWriter();

        try {
            final DotGenerator generator = new DotGenerator(graph);
            generator.generate(result);
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return result.toString();
    }

    protected final Graph getCluster(final String groupName) {
        Graph group = groupNames.get(groupName);

        if (group == null) {
            group = graph.newGraph();
            final Attributes attributes = group.getAttributes();
            attributes.setAttribute("label", groupName);
            groupNames.put(groupName, group);
        }

        return group;
    }

    /**
     * Get the node created with the given name.
     * 
     * @param nodeName
     *            name under which the node can be found.
     * @return node indexed with the given node name.
     */
    protected final Node getNode(final String nodeName) {
        return nodeNames.get(nodeName);
    }

    /**
     * Add a new node to this graph.
     * 
     * @param name
     *            the name the new node should be registered with.
     * @return the created node.
     */
    protected final Node addNode(final String name, final String clusterName) {
        final Graph cluster = getCluster(clusterName);
        final Node node = cluster.newNode();
        node.getAttributes().setAttribute("shape", "record");
        nodeNames.put(name, node);

        return node;
    }

    /**
     * Create a new edge using the two given nodes.
     * 
     * @param source
     *            source node for the edge.
     * @param target
     *            target node for the edge.
     * @return the newly created edge.
     */
    protected final Edge addEdge(final Node source, final Node target) {
        return graph.newEdge(source, target);
    }

    protected final void mergeEdges() {
        new CommonEdgeMergeAlgorithm(graph).execute();
    }

    /**
     * Has to be implemented by subclasses to generate the actual graph.
     */
    protected abstract void generateInternal();
}
