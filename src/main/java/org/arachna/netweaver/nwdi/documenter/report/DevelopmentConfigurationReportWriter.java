/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.IDevelopmentComponentFilter;
import org.arachna.netweaver.nwdi.dot4j.DevelopmentComponentDotFileGenerator;
import org.arachna.netweaver.nwdi.dot4j.DevelopmentConfigurationDotFileGenerator;
import org.arachna.netweaver.nwdi.dot4j.DotFileWriter;
import org.arachna.netweaver.nwdi.dot4j.UsingDevelopmentComponentsDotFileGenerator;

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

    private final IDevelopmentComponentFilter vendorFilter;

    /**
     * Create an instance of a {@link DevelopmentConfigurationReportWriter}.
     * 
     * @param dcFactory
     *            registry for development components
     * @param writerConfiguration
     *            configuration to be used creating the reports
     */
    public DevelopmentConfigurationReportWriter(final DevelopmentComponentFactory dcFactory,
        final ReportWriterConfiguration writerConfiguration, final IDevelopmentComponentFilter vendorFilter) {
        this.dcFactory = dcFactory;
        this.writerConfiguration = writerConfiguration;
        this.vendorFilter = vendorFilter;
    }

    /**
     * Create report for the given development configurations.
     * 
     * @param configurations
     *            development configurations for which the reports shall be
     *            created.
     * @throws IOException
     *             when an error occurs writing a report
     */
    public void write(final DevelopmentConfiguration configuration) throws IOException {
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
        dotFileWriter.write(new DevelopmentConfigurationDotFileGenerator(configuration), configuration.getName());

        writeDevelopmentConfigurationReport(configuration);
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

            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                if (component.isNeedsRebuild()) {
                    String componentName = component.getVendor() + "~" + component.getName().replace("/", "~");
                    new DevelopmentComponentHtmlReportWriter(new FileWriter(baseDir.getAbsolutePath() + File.separator
                        + componentName + ".html"), writerConfiguration, component, dcFactory).write();
                    dotFileWriter.write(new DevelopmentComponentDotFileGenerator(dcFactory, component,
                        this.vendorFilter), componentName);

                    componentName = componentName + "-usingDCs";
                    dotFileWriter.write(new UsingDevelopmentComponentsDotFileGenerator(component, this.vendorFilter),
                        componentName);
                }
            }

            dotFileWriter.write(new UsingDevelopmentComponentsDotFileGenerator(compartment.getDevelopmentComponents(),
                this.vendorFilter), compartment.getName() + "-usingDCs");
            dotFileWriter.write(
                new DevelopmentComponentDotFileGenerator(dcFactory, compartment.getDevelopmentComponents(),
                    this.vendorFilter), compartment.getName() + "-usedDCs");
        }
    }
}
