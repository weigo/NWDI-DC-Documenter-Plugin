/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
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
    private ReportWriterConfiguration writerConfiguration = new ReportWriterConfiguration();

    /**
     * Create an instance of a {@link DevelopmentConfigurationReportWriter}.
     *
     * @param dcFactory
     *            registry for development components
     * @param writerConfiguration
     *            configuration to be used creating the reports
     */
    public DevelopmentConfigurationReportWriter(final DevelopmentComponentFactory dcFactory,
        final ReportWriterConfiguration writerConfiguration) {
        this.dcFactory = dcFactory;
        this.writerConfiguration = writerConfiguration;
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
    public void write(final List<DevelopmentConfiguration> configurations) throws IOException {
        for (final DevelopmentConfiguration configuration : configurations) {
            final File baseDir =
                new File(this.writerConfiguration.getOutputLocation() + File.separatorChar + configuration.getName());

            this.createDirectoryIffNotExists(baseDir);

            final ReportWriterConfiguration config =
                createWriterConfiguration(baseDir.getAbsolutePath(), this.writerConfiguration.getCssLocation(),
                    this.writerConfiguration.getImageFormat());

            final String imageOutput = config.getOutputLocation() + File.separator + config.getImagesLocation();
            this.createDirectoryIffNotExists(new File(imageOutput));

            final DotFileWriter dotFileWriter = new DotFileWriter(imageOutput);
            dotFileWriter.write(new DevelopmentConfigurationDotFileGenerator(configuration), configuration.getName());

            writeDevelopmentConfigurationReport(configuration, config);
        }
    }

    /**
     * Create a directory for the given file iff it does not exist. Throws a
     * <code>RuntimeException</code> if the directory could not be created.
     *
     * @param directory
     *            directory that should be created.
     */
    private void createDirectoryIffNotExists(final File directory) {
        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Could not create " + directory.getAbsolutePath() + "!");
        }
    }

    /**
     * Create a {@link ReportWriterConfiguration} based on the given arguments.
     *
     * @param baseDir
     *            base directory to be used
     * @param cssLocation
     *            current location of CSS stylesheets
     * @param imageFormat
     *            image format to be used
     * @return newly created {@link ReportWriterConfiguration} based on given
     *         parameters
     */
    private ReportWriterConfiguration createWriterConfiguration(final String baseDir, final String cssLocation,
        final String imageFormat) {
        final ReportWriterConfiguration config = new ReportWriterConfiguration();
        config.setCssLocation("../" + cssLocation);
        config.setOutputLocation(baseDir);
        config.setImageFormat(imageFormat);

        return config;
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
    private void writeDevelopmentConfigurationReport(final DevelopmentConfiguration configuration,
        final ReportWriterConfiguration config) throws IOException {

        for (final Compartment compartment : configuration.getCompartments()) {
            if (compartment.isArchiveState()) {
                continue;
            }

            final File baseDir =
                new File(config.getOutputLocation() + File.separatorChar + compartment.getSoftwareComponent());
            this.createDirectoryIffNotExists(baseDir);

            final ReportWriterConfiguration writerConfig =
                createWriterConfiguration(baseDir.getAbsolutePath(), config.getCssLocation(), config.getImageFormat());

            final DotFileWriter dotFileWriter = createUsageDiagramWriter(writerConfig);

            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                if (component.isNeedsRebuild()) {
                    String componentName = component.getVendor() + "~" + component.getName().replace("/", "~");
                    dotFileWriter.write(new DevelopmentComponentDotFileGenerator(dcFactory, component), componentName);

                    componentName = componentName + "-usingDCs";
                    dotFileWriter.write(new UsingDevelopmentComponentsDotFileGenerator(component), componentName);
                }
            }

            dotFileWriter.write(new UsingDevelopmentComponentsDotFileGenerator(compartment.getDevelopmentComponents()),
                compartment.getName() + "-usingDCs");
            dotFileWriter.write(
                new DevelopmentComponentDotFileGenerator(dcFactory, compartment.getDevelopmentComponents()),
                compartment.getName() + "-usedDCs");
        }
    }

    /**
     * Create a {@link DotFileWriter} to be used when generating the usage
     * diagrams for development components. The writer will be set up using the
     * given {@link ReportWriterConfiguration} to determine the output location.
     *
     * @param writerConfig
     *            ReportWriterConfiguration to determine the output location
     * @return {@link DotFileWriter} to be used when generating the usage
     *         diagrams for development components
     * @throws IOException
     *             when creating the target directory failed
     */
    private DotFileWriter createUsageDiagramWriter(final ReportWriterConfiguration writerConfig) throws IOException {
        final String imageOutput = writerConfig.getOutputLocation() + File.separator + writerConfig.getImagesLocation();
        final File imageDir = new File(imageOutput);

        this.createDirectoryIffNotExists(imageDir);

        return new DotFileWriter(imageOutput);
    }
}
