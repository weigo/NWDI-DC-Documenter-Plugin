/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Pattern;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.config.DevelopmentConfigurationReader;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
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
     * regular expression for vendors to ignore during generation of
     * documentation.
     */
    private final VendorFilter vendorFilter;

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
     * @param vendorFilter
     *            vendors to ignore
     */
    ReportGenerator(final PrintStream logger, final DevelopmentConfiguration config,
        final DevelopmentComponentFactory dcFactory, final VelocityEngine engine, final String outputLocation,
        final VendorFilter vendorFilter) {
        this.logger = logger;
        this.config = config;
        this.dcFactory = dcFactory;
        this.engine = engine;
        this.outputLocation = outputLocation;
        this.vendorFilter = vendorFilter;

    }

    /**
     * Generate documentation.
     * 
     * @return <code>true</code> when no error occurred during the generation
     *         process, <code>false</code> otherwise.
     */
    boolean execute() {
        final boolean result = true;

        // TODO: Factory oder ähnlichen Mechanismus zum Erzeugen der
        // eigentlichen Dokumentation (zum einfachen Umschalten zwischen HTML &
        // Wiki)
        final ReportWriterConfiguration writerConfiguration = new ReportWriterConfiguration();
        writerConfiguration.setOutputLocation(outputLocation);

        final long start = System.currentTimeMillis();
        logger.append("Creating development configuration report...");

        config.accept(new DevelopmentConfigurationHtmlGenerator(writerConfiguration, dcFactory, vendorFilter, engine));
        duration(logger, start);

        return result;
    }

    private void duration(final PrintStream logger, final long start) {
        logger.append(String.format("(%f sec.).\n", (System.currentTimeMillis() - start) / 1000f));
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

        // final String dot = "/ZusatzSW/GraphViz/bin/dot.exe";
        final PrintStream s = new PrintStream(new File("/tmp/report.log"));
        new ReportGenerator(s, reader.getDevelopmentConfiguration(), dcFactory,
            new VelocityHelper(s).getVelocityEngine(), "/tmp/enviaMPR", new VendorFilter(Pattern.compile("sap\\.com")))
            .execute();
        s.close();
    }

}
