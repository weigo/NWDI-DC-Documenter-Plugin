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
import org.arachna.netweaver.nwdi.documenter.report.DevelopmentConfigurationReportWriter;
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
        final DevelopmentComponentFactory dcFactory, final String outputLocation, final String dotExecutable,
        final Pattern ignorableVendorRgexp) {
        this.logger = logger;
        this.config = config;
        this.dcFactory = dcFactory;
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
        final ReportWriterConfiguration writerConfiguration = new ReportWriterConfiguration();
        writerConfiguration.setOutputLocation(outputLocation);
        final DevelopmentConfigurationReportWriter reportWriter =
            new DevelopmentConfigurationReportWriter(logger, dcFactory, writerConfiguration,
                new DevelopmentComponentByVendorFilter(ignorableVendorRgexp), new CompartmentByVendorFilter(
                    ignorableVendorRgexp));

        try {
            long start = System.currentTimeMillis();
            logger.append("Creating development configuration report...");
            Collection<String> dotFiles = reportWriter.write(config);
            duration(logger, start);

            VelocityEngine engine = new VelocityHelper(logger).getVelocityEngine();
            Context context = new VelocityContext();
            context.put("dotFiles", dotFiles);
            context.put("dot", this.dotExecutable);
            context.put("timeout", Integer.toString(TIMEOUT));
            Writer writer = new FileWriter(new File(this.outputLocation, "Dot2Svg-build.xml"));
            engine.evaluate(context, writer, "", getTemplateReader());
            writer.close();
        }
        catch (final IOException e) {
            result = false;
            e.printStackTrace();
        }

        return result;
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
//        new XmlReaderHelper(reader).parse(new FileReader(
//            "/tmp/jenkins/jobs/enviaM/workspace/DevelopmentConfiguration.xml"));
        new XmlReaderHelper(reader).parse(new FileReader(
            "/NWDI-Redesign/PN3_enviaMPr_D-refactored.xml"));
        // new XmlReaderHelper(reader).parse(new FileReader(
        // "/home/weigo/tmp/enviaM/workspace/DevelopmentConfiguration.xml"));

        dcFactory.updateUsingDCs();

        for (final DevelopmentComponent component : dcFactory.getAll()) {
            if (!"sap.com".equals(component.getCompartment().getVendor())) {
                component.setNeedsRebuild(true);
            }
        }

        // final String dot = "/usr/bin/dot";
        String dot = "/ZusatzSW/GraphViz/bin/dot.exe";
        final PrintStream s = new PrintStream(new File("/tmp/report.log"));
        new ReportGenerator(s, reader.getDevelopmentConfiguration(), dcFactory, "/tmp/enviaMPR", dot,
            Pattern.compile("sap\\.com")).execute();
        s.close();
    }

}
