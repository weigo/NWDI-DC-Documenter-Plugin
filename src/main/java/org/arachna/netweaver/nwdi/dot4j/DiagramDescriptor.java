package org.arachna.netweaver.nwdi.dot4j;

import hudson.Util;

/**
 * Descriptor for paths to generated dependency diagrams.
 * 
 * @author Dirk Weigenand
 */
public final class DiagramDescriptor {
    /**
     * @param usedDCsDiagram
     *            diagram for DCs used by the entity
     */
    private final String usedDCsDiagram;

    /**
     * @param usingDCsDiagram
     *            diagram for DCs using the entity
     */
    private final String usingDCsDiagram;

    /**
     * Create descriptor with absolute paths to the diagrams of used and using
     * DCs.
     * 
     * @param usedDCsDiagram
     *            diagram for DCs used by the entity
     * @param usingDCsDiagram
     *            diagram for DCs using the entity
     */
    DiagramDescriptor(final String usedDCsDiagram, final String usingDCsDiagram) {
        this.usedDCsDiagram = Util.fixNull(usedDCsDiagram);
        this.usingDCsDiagram = Util.fixNull(usingDCsDiagram);
    }

    /**
     * @return the usedDCsDiagram
     */
    public String getUsedDCsDiagram() {
        return usedDCsDiagram;
    }

    /**
     * @return the usingDCsDiagram
     */
    public String getUsingDCsDiagram() {
        return usingDCsDiagram;
    }
}
