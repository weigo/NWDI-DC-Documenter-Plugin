/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import hudson.Util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Writer;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * @author Dirk Weigenand
 */
public final class POMGenerator {
    /**
     * Folder to write generated documentation into.
     */
    private final File workspace;

    /**
     * target for style sheet to use in generated HTML documentation.
     */
    private final File docBookSourceFolder;

    /**
     * velocity engine for generation of pom.xml.
     */
    private final VelocityEngine velocityEngine;

    /**
     * development configuration to use for pom.xml generation.
     */
    private final DevelopmentConfiguration configuration;

    /**
     * Create an instance of a generator for a pom.xml that contains dependencies and target definition/customazation for generation of HTML
     * documentation from docbook.
     * 
     * @param workspace
     *            workspace folder to generate pom.xml into.
     * @param docBookSourceFolder
     *            target folder to copy CSS into.
     * @param configuration
     *            development configuration used to generate meta information of pom.xml
     * @param velocityEngine
     *            velocity engine to use for transformation.
     */
    public POMGenerator(final File workspace, final File docBookSourceFolder, final DevelopmentConfiguration configuration,
        final VelocityEngine velocityEngine) {
        this.workspace = workspace;
        this.docBookSourceFolder = docBookSourceFolder;
        this.configuration = configuration;
        this.velocityEngine = velocityEngine;
    }

    /**
     * Copy CSS stylesheet to use for display of generated HTML documentation.
     */
    public void execute() {
        copyCssStyleSheet();
        createPOM();
    }

    /**
     * 
     */
    private void copyCssStyleSheet() {
        final File targetFolder = new File(docBookSourceFolder, "css");

        try {
            if (!targetFolder.mkdirs()) {
                throw new IllegalStateException(String.format("Could not create %s!", targetFolder.getAbsolutePath()));
            }

            final String styleCss = "style.css";
            Util.copyStreamAndClose(getTemplate(styleCss), new FileWriter(new File(targetFolder, styleCss)));
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 
     */
    private void createPOM() {
        Writer writer = null;

        try {
            writer = new FileWriter(new File(workspace, "pom.xml"));
            final Context context = new VelocityContext();
            context.put("configuration", configuration);
            context.put("encoding", "utf-8");
            velocityEngine.evaluate(context, writer, "", getTemplate("pom.vm"));
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Return a reader for the given velocity template (from class path).
     * 
     * @param template
     *            name of velocity template.
     * @return a reader for the given template.
     */
    private Reader getTemplate(final String template) {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            String.format("/org/arachna/netweaver/nwdi/documenter/report/%s", template)));
    }
}
