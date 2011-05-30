/**
 *
 */
package org.arachna.netweaver.nwdi.dot4j;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;

/**
 * Writer for graphviz <code>.dot</code> files.
 *
 * @author Dirk Weigenand
 */
public final class DotFileWriter {
    /**
     * Location where to generate the <code>.dot</code> files to.
     */
    private final String outputLocation;

    /**
     * Create an instance of {@link DotFileWriter} and initialize with the given
     * <code>outputLocation</code>.
     *
     * @param outputLocation
     *            determines where to write the .dot files to.
     */
    public DotFileWriter(final String outputLocation) {
        this.outputLocation = outputLocation;
    }

    /**
     * Create a .dot file in the location given at construction time. Use the
     * given {@link DotFileGenerator} to create the content and the given
     * <code>dotFileName</code> to name the .dot file.
     *
     * @param generator
     *            generator to use to generate the content
     * @param dotFileName
     *            name of .dot file
     * @return absolute path to the create file
     * @throws IOException
     *             when an error occurs writing the file
     */
    public String write(final DotFileGenerator generator, final String dotFileName) throws IOException {
        final File file = new File(this.outputLocation + File.separatorChar + dotFileName + ".dot");
        final Writer dotFile = new FileWriter(file);

        try {
            dotFile.write(generator.generate());
        }
        catch (final IOException e) {
            throw e;
        }
        finally {
            dotFile.close();
        }

        return file.getAbsolutePath();
    }
}
