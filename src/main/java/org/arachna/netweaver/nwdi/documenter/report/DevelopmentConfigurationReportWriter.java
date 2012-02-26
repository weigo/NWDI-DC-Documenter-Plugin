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

    private final DevelopmentComponentReportGenerator generator;

    /**
     * Collection of dotFiles created when generating the report.
     */
    private final Set<String> dotFiles = new HashSet<String>();

    /**
     * Create an instance of a {@link DevelopmentConfigurationReportWriter}.
     * 
     * @param dcFactory
     *            registry for development components
     * @param writerConfiguration
     *            configuration to be used creating the reports
     * @param vendorFilter
     *            filter development components by vendor
     * @param velocityEngine
     */
    public DevelopmentConfigurationReportWriter(final DevelopmentComponentFactory dcFactory,
        final ReportWriterConfiguration writerConfiguration, final IDevelopmentComponentFilter vendorFilter,
        final VelocityEngine velocityEngine) {
        this.dcFactory = dcFactory;
        this.writerConfiguration = writerConfiguration;
        this.vendorFilter = vendorFilter;
        generator =
            new DevelopmentComponentReportGenerator(dcFactory, velocityEngine,
                "/org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentHtmlTemplate.vm",
                ResourceBundle.getBundle("org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentReport",
                    Locale.GERMAN), Locale.GERMAN);
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
        createOverviewPage(configuration);
        copyResources();

        writeDevelopmentConfigurationReport(configuration);

        return dotFiles;
    }

    /**
     * @param configuration
     * @param baseDir
     * @throws IOException
     */
    protected void createOverviewPage(final DevelopmentConfiguration configuration) throws IOException {
        final File baseDir = createDirectoryIffNotExists(writerConfiguration.getOutputLocation());
        final FileWriter indexHtmlWriter = new FileWriter(new File(baseDir, "index.html"));
        final DevelopmentConfigurationsHtmlWriter cfgWriter =
            new DevelopmentConfigurationsHtmlWriter(indexHtmlWriter, writerConfiguration, configuration, dcFactory);
        cfgWriter.write();
        indexHtmlWriter.close();
    }

    /**
     * @param config
     * @throws IOException
     * @throws FileNotFoundException
     */
    private void copyResources() throws IOException, FileNotFoundException {
        final File cssFolder =
            createDirectoryIffNotExists(writerConfiguration.getOutputLocation() + File.separator
                + writerConfiguration.getCssLocation());
        copyResourceToTargetFolder(cssFolder, "/org/arachna/netweaver/nwdi/documenter/report/report.css", "report.css");
        final File jsFolder =
            createDirectoryIffNotExists(writerConfiguration.getOutputLocation() + File.separator
                + writerConfiguration.getJsLocation());
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
        final Collection<Compartment> compartments = configuration.getCompartments();

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

            for (final DevelopmentComponent component : compartment.getDevelopmentComponents()) {
                if (!vendorFilter.accept(component) && component.isNeedsRebuild()) {
                    final String componentName = component.getVendor() + "~" + component.getName().replace("/", "~");
                    final FileWriter writer =
                        new FileWriter(String.format("%s%c%s.html", baseDir.getAbsolutePath(), File.separatorChar,
                            componentName));
                    generator.execute(writer, component);
                    writer.close();
                }
            }
        }
    }
}
