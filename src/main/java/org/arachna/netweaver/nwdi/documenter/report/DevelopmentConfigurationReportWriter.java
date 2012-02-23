/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.IDevelopmentComponentFilter;
import org.arachna.netweaver.nwdi.documenter.CompartmentByVendorFilter;
import org.arachna.netweaver.nwdi.dot4j.DevelopmentComponentDotFileGenerator;
import org.arachna.netweaver.nwdi.dot4j.DevelopmentConfigurationDotFileGenerator;
import org.arachna.netweaver.nwdi.dot4j.DotFileWriter;
import org.arachna.netweaver.nwdi.dot4j.UsingDevelopmentComponentsDotFileGenerator;
import org.arachna.velocity.VelocityHelper;

/**
 * Writer for reports on development configurations.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationReportWriter {
    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Configuration for this report writer.
     */
    private final ReportWriterConfiguration writerConfiguration;

    /**
     * filter development components by vendor.
     */
    private final IDevelopmentComponentFilter vendorFilter;

    private final PrintStream logger;

    /**
     * filter compartments by vendor.
     */
    private final CompartmentByVendorFilter compartmentByVendorFilter;

    /**
     * Collection of dotFiles created when generating the report.
     */
    private Set<String> dotFiles = new HashSet<String>();

    /**
     * Create an instance of a {@link DevelopmentConfigurationReportWriter}.
     * 
     * @param logger
     * 
     * @param dcFactory
     *            registry for development components
     * @param writerConfiguration
     *            configuration to be used creating the reports
     * @param vendorFilter
     *            filter development components by vendor
     * @param compartmentByVendorFilter
     *            filter compartments by vendor
     */
    public DevelopmentConfigurationReportWriter(PrintStream logger, final DevelopmentComponentFactory dcFactory,
        final ReportWriterConfiguration writerConfiguration, final IDevelopmentComponentFilter vendorFilter,
        CompartmentByVendorFilter compartmentByVendorFilter) {
        this.logger = logger;
        this.dcFactory = dcFactory;
        this.writerConfiguration = writerConfiguration;
        this.vendorFilter = vendorFilter;
        this.compartmentByVendorFilter = compartmentByVendorFilter;
    }

    /**
     * Create report for the given development configurations.
     * 
     * @param configurations
     *            development configurations for which the reports shall be
     *            created.
     * @return collection of generated .dot files.
     * @throws IOException
     *             when an error occurs writing a report
     */
    public Collection<String> write(final DevelopmentConfiguration configuration) throws IOException {
        final File baseDir = createDirectoryIffNotExists(writerConfiguration.getOutputLocation());

        final FileWriter indexHtmlWriter =
            new FileWriter(baseDir.getAbsolutePath() + File.separatorChar + "index.html");
        final DevelopmentConfigurationsHtmlWriter cfgWriter =
            new DevelopmentConfigurationsHtmlWriter(indexHtmlWriter, writerConfiguration, configuration, dcFactory);
        cfgWriter.write();
        indexHtmlWriter.close();

        final String imageOutput =
            writerConfiguration.getOutputLocation() + File.separator + writerConfiguration.getImagesLocation();
        createDirectoryIffNotExists(imageOutput);

        copyResources(writerConfiguration);

        final DotFileWriter dotFileWriter = new DotFileWriter(imageOutput);
        this.dotFiles.add(dotFileWriter.write(new DevelopmentConfigurationDotFileGenerator(configuration,
            this.compartmentByVendorFilter), configuration.getName()));

        writeDevelopmentConfigurationReport(configuration);

        return this.dotFiles;
    }

    /**
     * @param config
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void copyResources(final ReportWriterConfiguration config) throws IOException, FileNotFoundException {
        final File cssFolder =
            createDirectoryIffNotExists(config.getOutputLocation() + File.separator + config.getCssLocation());
        copyResourceToTargetFolder(cssFolder, "/org/arachna/netweaver/nwdi/documenter/report/report.css", "report.css");
        final File jsFolder =
            createDirectoryIffNotExists(config.getOutputLocation() + File.separator + config.getJsLocation());
        copyResourceToTargetFolder(jsFolder, "/org/arachna/netweaver/nwdi/documenter/report/search.js", "search.js");
        copyResourceToTargetFolder(jsFolder, "/org/arachna/netweaver/nwdi/documenter/report/xpath.js", "xpath.js");
    }

    /**
     * @param targetFolder
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void copyResourceToTargetFolder(final File targetFolder, final String absoluteResourcePath,
        final String targetName) throws IOException, FileNotFoundException {
        IOUtils.copy(this.getClass().getResourceAsStream(absoluteResourcePath),
            new FileOutputStream(targetFolder.getAbsolutePath() + File.separatorChar + targetName));
    }

    /**
     * Create a directory for the given file iff it does not exist. Throws a
     * <code>RuntimeException</code> if the directory could not be created.
     * 
     * @param folderName
     *            absolute path to folder that should be created.
     */
    private File createDirectoryIffNotExists(final String folderName) {
        final File directory = new File(folderName);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Could not create " + directory.getAbsolutePath() + "!");
        }

        return directory;
    }

    /**
     * Write a report for the given development configuration using the given
     * {@link ReportWriterConfiguration}.
     * 
     * @param configuration
     *            development configuration used in creating the report
     * @param config
     *            report writer configuration to be used
     * @throws IOException
     *             when an error writing the reports occurs
     */
    private void writeDevelopmentConfigurationReport(final DevelopmentConfiguration configuration) throws IOException {
        final Collection<Compartment> compartments = configuration.getCompartments(/*
                                                                                    * CompartmentState
                                                                                    * .
                                                                                    * Source
                                                                                    */);

        new CompartmentsHtmlReportWriter(new FileWriter(writerConfiguration.getOutputLocation() + File.separatorChar
            + "compartments.html"), writerConfiguration, compartments, dcFactory).write();

        final ReportWriterConfiguration writerConfiguration = new ReportWriterConfiguration();
        writerConfiguration.setCssLocation("../" + this.writerConfiguration.getCssLocation());
        writerConfiguration.setImageFormat(this.writerConfiguration.getImageFormat());
        writerConfiguration.setImagesLocation(this.writerConfiguration.getImagesLocation());
        writerConfiguration.setJsLocation("../" + this.writerConfiguration.getJsLocation());

        for (final Compartment compartment : compartments) {
            final File baseDir =
                createDirectoryIffNotExists(this.writerConfiguration.getOutputLocation() + File.separatorChar
                    + compartment.getSoftwareComponent());
            writerConfiguration.setOutputLocation(baseDir.getAbsolutePath());

            new CompartmentHtmlReportWriter(new FileWriter(baseDir.getAbsolutePath() + File.separatorChar
                + "index.html"), writerConfiguration, compartment, dcFactory).write();

            final String imageOutput =
                baseDir.getAbsolutePath() + File.separator + this.writerConfiguration.getImagesLocation();
            createDirectoryIffNotExists(imageOutput);

            final DotFileWriter dotFileWriter = new DotFileWriter(imageOutput);

            final VelocityEngine velocityEngine = new VelocityHelper(this.logger).getVelocityEngine();

            final DevelopmentComponentReportGenerator generator =
                new DevelopmentComponentReportGenerator(dcFactory, velocityEngine,
                    "/org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentHtmlTemplate.vm",
                    ResourceBundle.getBundle("org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentReport",
                        Locale.GERMAN), Locale.GERMAN);

            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                // FIXME: dependency graphs should only be generated for DCs
                // matching the vendor filter, were affected (transitively) be
                // the
                if (!vendorFilter.accept(component) && component.isNeedsRebuild()) {
                    String componentName = component.getVendor() + "~" + component.getName().replace("/", "~");
                    FileWriter writer =
                        new FileWriter(String.format("%s%c%s.html", baseDir.getAbsolutePath(), File.separatorChar,
                            componentName));
                    generator.execute(writer, component);
                    writer.close();
                    dotFiles.add(dotFileWriter.write(new DevelopmentComponentDotFileGenerator(dcFactory, component,
                        this.vendorFilter), componentName));

                    componentName = componentName + "-usingDCs";
                    dotFiles.add(dotFileWriter.write(new UsingDevelopmentComponentsDotFileGenerator(component,
                        this.vendorFilter), componentName));
                }
            }

            dotFiles.add(dotFileWriter.write(
                new UsingDevelopmentComponentsDotFileGenerator(compartment.getDevelopmentComponents(),
                    this.vendorFilter), compartment.getName() + "-usingDCs"));
            dotFiles.add(dotFileWriter.write(
                new DevelopmentComponentDotFileGenerator(dcFactory, compartment.getDevelopmentComponents(),
                    this.vendorFilter), compartment.getName() + "-usedDCs"));
        }
    }
}
