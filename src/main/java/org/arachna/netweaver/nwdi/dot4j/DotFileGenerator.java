/**
 *
 */
package org.arachna.netweaver.nwdi.dot4j;

/**
 * Interface for <code>.dot</code>file generators.
 *
 * @author Dirk Weigenand
 */
public interface DotFileGenerator {

    /**
     * Generate a graph in <code>.dot</code> syntax.
     *
     * @return the generated graph.
     */
    String generate();
}
