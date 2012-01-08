package org.arachna.netweaver.nwdi.dot4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.arachna.dot4j.model.Attributes;
import org.arachna.dot4j.model.Node;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartReference;

/**
 * Abstract base class for graphviz <code>.dot</code> file generators for
 * development components usage relations.
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractDevelopmentComponentDotFileGenerator extends AbstractDotFileGenerator {
    /**
     * Development component a graphviz graph shall be generated for.
     */
    protected final Collection<DevelopmentComponent> components = new ArrayList<DevelopmentComponent>();

    /**
     * House keeping of sub graphs already generated.
     */
    protected final Set<String> subGraphs = new HashSet<String>();

    /**
     * Registry for getting/creating development components.
     */
    protected final DevelopmentComponentFactory dcFactory;

    /**
     * Constructor using the given factory for development components and the DC
     * to be used as root for the graph to be generated.
     * 
     * @param dcFactory
     *            Registry for getting/creating development components.
     * @param component
     *            DC to be used as root for the graph to be generated.
     */
    public AbstractDevelopmentComponentDotFileGenerator(final DevelopmentComponentFactory dcFactory,
        final DevelopmentComponent component) {
        this.dcFactory = dcFactory;
        components.add(component);
    }

    /**
     * Constructor using the given factory for development components and the DC
     * to be used as root for the graph to be generated.
     * 
     * @param dcFactory
     *            Registry for getting/creating development components.
     * @param components
     *            DC to be used as root for the graph to be generated.
     */
    public AbstractDevelopmentComponentDotFileGenerator(final DevelopmentComponentFactory dcFactory,
        final Collection<DevelopmentComponent> components) {
        this.dcFactory = dcFactory;
        this.components.addAll(components);
    }

    /**
     * Return a node name for the given development component.
     * 
     * @param component
     *            DC a node name should be generated for/a cached value should
     *            be returned for.
     * @return a node name for the given development component. Creates one if
     *         none already exists. Returns the cached value otherwise.
     */
    protected final String getNodeName(final DevelopmentComponent component) {
        return getNodeName(component.getVendor(), component.getName());
    }

    /**
     * Return a node name for the given public part reference.
     * 
     * @param reference
     *            public part reference a node name should be generated for/a
     *            cached value should be returned for.
     * @return a node name for the given public part reference. Creates one if
     *         none already exists. Returns the cached value otherwise.
     */
    protected final String getNodeName(final PublicPartReference reference) {
        return getNodeName(reference.getVendor(), reference.getName());
    }

    /**
     * Return the node name for the given vendor and component name.
     * 
     * @param vendor
     *            vendor of the component
     * @param componentName
     *            name of the component.
     * @return node name for a component as concatnation of vendor and component
     *         name.
     */
    private String getNodeName(final String vendor, final String componentName) {
        return String.format("%s:%s", vendor, componentName);
    }

    /**
     * Create label for the given development component. Consists of component
     * name and vendor, component type and public parts.
     * 
     * @param component
     *            development component to generate label for.
     * @return generated label.
     */
    protected String createLabel(final DevelopmentComponent component) {
        final Iterator<PublicPart> publicParts = component.getPublicParts().iterator();
        final StringBuffer label = new StringBuffer();

        if (component.getName().trim().length() > 0) {
            label.append(component.getName());
        }

        PublicPart publicPart;
        label.append(String.format("|{{%s}|{%s}}", component.getVendor(), component.getType()));

        if (publicParts.hasNext()) {
            label.append("|{");

            while (publicParts.hasNext()) {
                publicPart = publicParts.next();
                label.append(String.format("{<%s>%s}", publicPart.getPublicPart(), publicPart.getPublicPart()));

                if (publicParts.hasNext()) {
                    label.append("|");
                }
            }

            label.append("}");
        }

        return label.toString();
    }

    /**
     * Check whether an edge has already been generated for the given
     * combination of source and target node with the given label.
     * 
     * @param sourceNode
     *            name of source node.
     * @param targetNode
     *            name of target node.
     * @param label
     *            label for the edge.
     * @return <code>true</code> if an edge has already been generated for the
     *         given combination of source and target node with the given label,
     *         <code>false</code> otherwise.
     */
    protected boolean hasBeenGenerated(final String sourceNode, final String targetNode, final String label) {
        final String key = String.format("%s:%s:%s", sourceNode, targetNode, label);
        final boolean hasBeenGenerated = subGraphs.contains(key);

        // TODO: should not keep track of subGraphs!
        if (!hasBeenGenerated) {
            subGraphs.add(key);
        }

        return hasBeenGenerated;
    }

    /**
     * Generate a new node if none has been generated already for the given
     * component.
     * 
     * @param component
     *            development component to generate node for.
     */
    protected final void generateNode(final DevelopmentComponent component) {
        final String nodeName = getNodeName(component);
        Node node = getNode(nodeName);

        if (node == null) {
            node =
                addNode(nodeName, component.getCompartment() != null ? component.getCompartment().getName()
                    : "unknown SC");
            final Attributes attributes = node.getAttributes();
            attributes.setAttribute("label", createLabel(component));
        }
    }
}