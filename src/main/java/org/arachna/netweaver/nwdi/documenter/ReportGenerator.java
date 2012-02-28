/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.Writer;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.arachna.netweaver.dc.config.DevelopmentConfigurationReader;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.nwdi.documenter.report.DependencyGraphGenerator;
import org.arachna.netweaver.nwdi.documenter.report.DevelopmentConfigurationHtmlGenerator;
import org.arachna.netweaver.nwdi.documenter.report.ReportWriterConfiguration;
import org.arachna.velocity.VelocityHelper;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.SAXException;

/**
 * (HTML-)Documentation generator for a development configuration.
 * 
 * @author Dirk Weigenand
 */
public class ReportGenerator {
    /**
     * timeout for dependency diagram generation.
     */
    private static final int TIMEOUT = 1000 * 60;

    /**
     * Logger.
     */
    private final PrintStream logger;

    /**
     * development configuration to generator documentation for.
     */
    private final DevelopmentConfiguration config;

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * target directory for generated documentation.
     */
    private final String outputLocation;

    /**
     * location of dot executable.
     */
    private final String dotExecutable;

    /**
     * regular expression for vendors to ignore during generation of
     * documentation.
     */
    private final Pattern ignorableVendorRgexp;

    /**
     * Velocity engine for transformations.
     */
    private final VelocityEngine engine;

    /**
     * Create an report generator instance using the given configuration
     * parameters.
     * 
     * @param logger
     *            Logger
     * @param config
     *            development configuration
     * @param dcFactory
     *            DC registry
     * @param outputLocation
     *            target directory
     * @param dotExecutable
     *            path to dot executable
     * @param ignorableVendorRgexp
     *            vendors to ignore
     */
    ReportGenerator(final PrintStream logger, final DevelopmentConfiguration config,
        final DevelopmentComponentFactory dcFactory, final VelocityEngine engine, final String outputLocation,
        final String dotExecutable, final Pattern ignorableVendorRgexp) {
        this.logger = logger;
        this.config = config;
        this.dcFactory = dcFactory;
        this.engine = engine;
        this.outputLocation = outputLocation;
        this.dotExecutable = dotExecutable;
        this.ignorableVendorRgexp = ignorableVendorRgexp;

    }

    /**
     * Generate documentation.
     * 
     * @return <code>true</code> when no error occurred during the generation
     *         process, <code>false</code> otherwise.
     */
    boolean execute() {
        boolean result = true;
        final DevelopmentComponentByVendorFilter vendorFilter =
            new DevelopmentComponentByVendorFilter(ignorableVendorRgexp);
        final CompartmentByVendorFilter compartmentByVendorFilter = new CompartmentByVendorFilter(ignorableVendorRgexp);

        final DependencyGraphGenerator dependenciesGeneratory =
            new DependencyGraphGenerator(dcFactory, compartmentByVendorFilter, vendorFilter, new File(outputLocation));
        config.accept(dependenciesGeneratory);

        // TODO: Factory oder ähnlichen Mechanismus zum Erzeugen der
        // eigentlichen Dokumentation (zum einfachen Umschalten zwischen HTML &
        // Wiki)
        final ReportWriterConfiguration writerConfiguration = new ReportWriterConfiguration();
        writerConfiguration.setOutputLocation(outputLocation);

        try {
            final long start = System.currentTimeMillis();
            logger.append("Creating development configuration report...");
            config.accept(new DevelopmentConfigurationHtmlGenerator(writerConfiguration, dcFactory, vendorFilter,
                engine));
            duration(logger, start);

            materializeDot2SvgBuildXml(dependenciesGeneratory.getDotFiles());
        }
        catch (final IOException e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }

    /**
     * Create an Ant build file for translating GraphViz <code>.dot</code> files
     * int SVG graphics.
     * 
     * @param dotFiles
     *            collection of GraphViz <code>.dot</code> to translate into
     *            SVG.
     * @throws IOException
     *             when writing the build file fails.
     */
    protected void materializeDot2SvgBuildXml(final Collection<String> dotFiles) throws IOException {
        final Context context = new VelocityContext();
        context.put("dotFiles", dotFiles);
        context.put("dot", dotExecutable);
        context.put("timeout", Integer.toString(TIMEOUT));
        final Writer writer = new FileWriter(new File(outputLocation, "Dot2Svg-build.xml"));
        engine.evaluate(context, writer, "", getTemplateReader());
        writer.close();
    }

    private void duration(final PrintStream logger, final long start) {
        logger.append(String.format("(%f sec.).\n", (System.currentTimeMillis() - start) / 1000f));
    }

    private Reader getTemplateReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/nwdi/documenter/report/Dot2Svg-build.vm"));
    }

    public static void main(final String[] args) throws IOException, SAXException {
        final DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();
        final DevelopmentConfigurationReader reader = new DevelopmentConfigurationReader(dcFactory);
        // new XmlReaderHelper(reader).parse(new FileReader(
        // "/tmp/jenkins/jobs/enviaM/workspace/DevelopmentConfiguration.xml"));
        // new XmlReaderHelper(reader).parse(new FileReader(
        // "/NWDI-Redesign/PN3_enviaMPr_D-refactored.xml"));
        new XmlReaderHelper(reader).parse(new FileReader(
            "/home/weigo/tmp/enviaM/workspace/DevelopmentConfiguration.xml"));

        dcFactory.updateUsingDCs();

        for (final DevelopmentComponent component : dcFactory.getAll()) {
            if (!"sap.com".equals(component.getCompartment().getVendor())) {
                component.setNeedsRebuild(true);
            }
        }

        final String dot = "/usr/local/bin/dot";
        // final String dot = "/ZusatzSW/GraphViz/bin/dot.exe";
        final PrintStream s = new PrintStream(new File("/tmp/report.log"));
        new ReportGenerator(s, reader.getDevelopmentConfiguration(), dcFactory,
            new VelocityHelper(s).getVelocityEngine(), "/tmp/enviaMPR", dot, Pattern.compile("sap\\.com")).execute();
        s.close();
    }

}
