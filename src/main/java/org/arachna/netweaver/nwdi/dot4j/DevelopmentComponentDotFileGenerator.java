/**
 *
 */
package org.arachna.netweaver.nwdi.dot4j;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.arachna.dot4j.model.Attributes;
import org.arachna.dot4j.model.Edge;
import org.arachna.dot4j.model.Node;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.netweaver.nwdi.documenter.filter.VendorFilter;

/**
 * Generator for graphviz <code>.dot</code> files depicting relations between a development component and development components used by
 * this DC.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentDotFileGenerator extends AbstractDevelopmentComponentDotFileGenerator {
    /**
     * Vendors to ignore during graph generation.
     */
    private final VendorFilter vendorFilter;

    /**
     * Create an instance of a <code>DevelopmentComponentDotFileGenerator</code> using the given development component registry and
     * development component.
     * 
     * @param dcFactory
     *            registry used to get/create development components
     * @param components
     *            DCs to generate usage relations for
     * @param vendorFilter
     *            regular expression for exclusion of development components from vendors matching it.
     */
    public DevelopmentComponentDotFileGenerator(final DevelopmentComponentFactory dcFactory,
        final Collection<DevelopmentComponent> components, final VendorFilter vendorFilter) {
        super(dcFactory, components);
        this.vendorFilter = vendorFilter;
    }

    /**
     * Create an instance of a <code>DevelopmentComponentDotFileGenerator</code> using the given development component registry and
     * development component.
     * 
     * @param dcFactory
     *            registry used to get/create development components
     * @param component
     *            DC to generate usage relations for
     * @param vendorFilter
     *            regular expression for exclusion of development components from vendors matching it.
     */
    public DevelopmentComponentDotFileGenerator(final DevelopmentComponentFactory dcFactory,
        final DevelopmentComponent component, final VendorFilter vendorFilter) {
        super(dcFactory, component);
        this.vendorFilter = vendorFilter;
    }

    /*
     * (non-Javadoc)
     * 
     * @seeorg.arachna.netweaver.dc.analyzer.writers.AbstractDotFileGenerator# generateInternal()
     */
    @Override
    protected void generateInternal() {
        for (final DevelopmentComponent component : components) {
            generateNodes(component);
        }

        for (final DevelopmentComponent component : components) {
            generateSubGraph(component);
        }
    }

    /**
     * Generates a graphviz <code>subgraph</code> for the given development component.
     * 
     * @param developmentComponent
     *            DC to generate <code>subgraph</code> for.
     */
    private void generateSubGraph(final DevelopmentComponent developmentComponent) {
        if (!subGraphs.contains(developmentComponent.toString())) {
            subGraphs.add(developmentComponent.toString());
            final String sourceNodeName = getNodeName(developmentComponent);
            final Node sourceNode = getNode(sourceNodeName);
            Node targetNode;
            String targetNodeName;
            Edge edge;

            final PublicPartAggregator aggregator = new PublicPartAggregator(dcFactory);
            aggregator.aggregate(developmentComponent.getUsedDevelopmentComponents());

            for (final Map.Entry<DevelopmentComponent, Set<PublicPartReference>> entry : aggregator
                .getAggregatedReferences().entrySet()) {
                final DevelopmentComponent usedComponent = entry.getKey();

                // exclude DCs matched by the vendor filter.
                if (vendorFilter.accept(usedComponent)) {
                    continue;
                }

                final String label = new PublicPartLabelDecorator(entry.getValue()).getLabel();

                targetNodeName = getNodeName(usedComponent);

                if (!hasBeenGenerated(sourceNodeName, targetNodeName, label)) {
                    targetNode = getNode(targetNodeName);
                    edge = addEdge(sourceNode, targetNode);
                    Attributes attributes = edge.getAttributes();
                    attributes.setAttribute("label", label);
                    attributes.setAttribute("fontsize", Integer.toString(this.getGlobalConfig().getFontSize()));
                }

                generateSubGraph(usedComponent);
            }
        }
    }

    /**
     * Generate graphviz <code>node</code> commands for the public parts referenced by the given development component.
     * 
     * @param component
     *            DC for which <code>node</code> commands of referenced public parts shall be generated for
     */
    private void generateNodes(final DevelopmentComponent component) {
        if (!nodeExists(component)) {
            generateNode(component);

            DevelopmentComponent usedDC;

            for (final PublicPartReference publicPart : component.getUsedDevelopmentComponents()) {
                // exclude DCs matched by the vendor filter.
                usedDC = dcFactory.get(publicPart.getVendor(), publicPart.getComponentName());

                if (usedDC == null) {
                    usedDC = dcFactory.create(publicPart.getVendor(), publicPart.getComponentName());
                }

                // exclude DCs matched by the vendor filter.
                if (vendorFilter.accept(usedDC)) {
                    continue;
                }

                generateNodes(usedDC);
            }
        }
    }

    /**
     * Check whether a node has already been generated for the given component.
     * 
     * @param component
     *            development component a node should be generated for.
     * @return <code>true</code> if a node has already been generated <code>false</code> otherwise.
     */
    private boolean nodeExists(final DevelopmentComponent component) {
        return getNode(this.getNodeName(component)) != null;
    }

    /**
     * Aggregate public parts labels to one label.
     * 
     * @author Dirk Weigenand
     */
    private static final class PublicPartAggregator {
        /**
         * Aggregated public parts by development component.
         */
        private final Map<DevelopmentComponent, Set<PublicPartReference>> aggregatedReferences =
            new HashMap<DevelopmentComponent, Set<PublicPartReference>>();

        /**
         * registry for development components.
         */
        private final DevelopmentComponentFactory dcFactory;

        /**
         * Create an instance of a <code>PublicPartAggregator</code> with the given {@link DevelopmentComponentFactory}.
         * 
         * @param dcFactory
         *            registry for development components.
         */
        PublicPartAggregator(final DevelopmentComponentFactory dcFactory) {
            this.dcFactory = dcFactory;
        }

        /**
         * Aggregate the given public part references by development component.
         * 
         * @param references
         *            collection of {@link PublicPartReference} to aggregate by development component.
         */
        void aggregate(final Collection<PublicPartReference> references) {
            DevelopmentComponent component;
            Set<PublicPartReference> aggregatedReferences;

            for (final PublicPartReference reference : references) {
                component = dcFactory.get(reference.getVendor(), reference.getComponentName());

                aggregatedReferences = this.aggregatedReferences.get(component);

                if (aggregatedReferences == null) {
                    aggregatedReferences = new HashSet<PublicPartReference>();
                    this.aggregatedReferences.put(component, aggregatedReferences);
                }

                aggregatedReferences.add(reference);
            }
        }

        /**
         * @return the aggregatedReferences
         */
        Map<DevelopmentComponent, Set<PublicPartReference>> getAggregatedReferences() {
            return aggregatedReferences;
        }
    }
}