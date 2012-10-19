/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;

/**
 * Generator for ant build file to convert <code>.dot</code> into graphics.
 * 
 * @author Dirk Weigenand
 */
public final class Dot2SvgBuildFileGenerator {
    /**
     * folder to generate build file into.
     */
    private final File baseDirectory;

    /**
     * velocity engine to use for transformation.
     */
    private final VelocityEngine engine;

    /**
     * path to dot executable.
     */
    private final String dotExecutable;

    /**
     * timeout to apply to conversion of .dot files into graphics.
     */
    private final int timeout;

    /**
     * count of threads to use for conversion of <code>.dot</code> files to
     * graphics.
     */
    private final int threads;

    /**
     * Create new instance of build file generator for conversion of
     * <code>.dot</code> files into graphics.
     * 
     * @param baseDirectory
     *            folder to write build file into.
     * @param engine
     *            velocity engine to use for transformation of template.
     * @param dotExecutable
     *            absolute path to <code>dot</code> executable.
     * @param timeout
     *            use timeout to abort a run taking too long.
     * @param threads
     *            count of threads to use for conversion of <code>.dot</code>
     *            files to graphics.
     */
    public Dot2SvgBuildFileGenerator(final File baseDirectory, final VelocityEngine engine, final String dotExecutable,
        final int timeout, final int threads) {
        this.baseDirectory = baseDirectory;
        this.engine = engine;
        this.dotExecutable = dotExecutable;
        this.timeout = timeout;
        this.threads = threads;
    }

    /**
     * Create an Ant build file for translating GraphViz <code>.dot</code> files
     * into SVG graphics.
     * 
     * @param dotFiles
     *            list of <code>.dot</code> files to convert into a graphics
     *            format.
     * @return <code>true</code> when the build file was successfully created,
     *         <code>false</code> otherwise.
     */
    public String execute(final Collection<String> dotFiles) {
        final String buildFileName = "Dot2Svg-build.xml";

        Writer writer = null;

        try {
            writer = new FileWriter(new File(baseDirectory, buildFileName));
            final Context context = new VelocityContext();
            context.put("dotFiles", dotFiles);
            context.put("dot", dotExecutable);
            context.put("timeout", Integer.toString(timeout));
            context.put("threads", Integer.toString(threads));
            engine.evaluate(context, writer, "", getTemplateReader());
        }
        catch (final IOException ioe) {
            throw new IllegalStateException(ioe);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (final IOException e) {

                }
            }
        }

        return String.format("%s/%s", baseDirectory.getName(), buildFileName);
    }

    private Reader getTemplateReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/nwdi/documenter/report/Dot2Svg-build.vm"));
    }
}
