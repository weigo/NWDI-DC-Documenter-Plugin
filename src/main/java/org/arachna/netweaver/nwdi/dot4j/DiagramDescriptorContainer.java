package org.arachna.netweaver.nwdi.dot4j;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Container for descriptors of dependency diagrams. Maintains a mapping for
 * diagrams of used/using DCs/SCs for DCs/SCs and a collection of all generated
 * diagrams.
 * 
 * @author Dirk Weigenand
 */
public final class DiagramDescriptorContainer {
    /**
     * Mapping of component/compartment/development configuration to generated
     * dependency diagrams.
     */
    private final Map<String, DiagramDescriptor> descriptors = new HashMap<String, DiagramDescriptor>();

    /**
     * Collection of all generated dependency diagrams.
     */
    private final Collection<String> dotFiles = new HashSet<String>();

    /**
     * Add a new descriptor with dependency diagrams to the mapping container.
     * 
     * @param key
     *            component/compartment/development configuration
     * @param descriptor
     *            diagram descriptor.
     */
    private void add(final String key, final DiagramDescriptor descriptor) {
        descriptors.put(key, descriptor);
        dotFiles.add(descriptor.getUsedDCsDiagram());
        dotFiles.add(descriptor.getUsingDCsDiagram());
    }

    /**
     * Add the descriptor for the given component.
     * 
     * @param component
     *            development component for which to add descriptor.
     * @param descriptor
     *            descriptor with paths to dependency diagrams
     */
    void add(final DevelopmentComponent component, final DiagramDescriptor descriptor) {
        this.add(component.getNormalizedName("_"), descriptor);
    }

    /**
     * Add descriptor for dependency diagrams for the given compartment.
     * 
     * @param compartment
     *            compartment whose dependency diagram descriptor should be
     *            added.
     * @param descriptor
     *            descriptor with paths to dependency diagrams
     */
    void add(final Compartment compartment, final DiagramDescriptor descriptor) {
        this.add(compartment.getName(), descriptor);
    }

    /**
     * Add descriptor for dependency diagrams for the given configuration.
     * 
     * @param configuration
     *            configuration whose dependency diagram descriptor should be
     *            added.
     * @param descriptor
     *            descriptor with paths to dependency diagrams
     */
    void add(final DevelopmentConfiguration configuration, final DiagramDescriptor descriptor) {
        this.add(configuration.getName(), descriptor);
    }

    /**
     * Returns all generated dot files.
     * 
     * @return collection of all generated dot files.
     */
    Collection<String> getDotFiles() {
        return Collections.unmodifiableCollection(dotFiles);
    }

    /**
     * Return the descriptor for the given development component.
     * 
     * @param component
     * @return
     */
    public DiagramDescriptor getDescriptor(final DevelopmentComponent component) {
        return descriptors.get(component.getNormalizedName("_"));
    }
}
