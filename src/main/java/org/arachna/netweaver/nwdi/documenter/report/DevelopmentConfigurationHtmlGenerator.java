/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.AbstractDevelopmentConfigurationVisitor;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.IDevelopmentComponentFilter;

/**
 * Generator for documentation of a development configuration in HTML.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationHtmlGenerator extends AbstractDevelopmentConfigurationVisitor {
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

    /**
     * Generator for a report on a development component. The target format is
     * determined via the Velocity template given at build time.
     */
    private final DevelopmentComponentReportGenerator generator;

    /**
     * 
     */
    public DevelopmentConfigurationHtmlGenerator(final ReportWriterConfiguration writerConfiguration,
        final DevelopmentComponentFactory dcFactory, final IDevelopmentComponentFilter vendorFilter,
        final VelocityEngine velocityEngine) {
        this.writerConfiguration = writerConfiguration;
        this.dcFactory = dcFactory;
        this.vendorFilter = vendorFilter;
        generator =
            new DevelopmentComponentReportGenerator(dcFactory, velocityEngine,
                "/org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentHtmlTemplate.vm",
                ResourceBundle.getBundle("org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentReport",
                    Locale.GERMAN), Locale.GERMAN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitDevelopmentConfiguration(final DevelopmentConfiguration configuration) {
        try {
            createOverviewPage(configuration);
            copyResources();

            getCurrentWriterConfiguration();
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @return
     */
    protected ReportWriterConfiguration getCurrentWriterConfiguration() {
        final ReportWriterConfiguration writerConfiguration = new ReportWriterConfiguration();
        writerConfiguration.setCssLocation("../" + this.writerConfiguration.getCssLocation());
        writerConfiguration.setImageFormat(this.writerConfiguration.getImageFormat());
        writerConfiguration.setImagesLocation(this.writerConfiguration.getImagesLocation());
        writerConfiguration.setJsLocation("../" + this.writerConfiguration.getJsLocation());

        return writerConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitCompartment(final Compartment compartment) {
        final File baseDir =
            createDirectoryIffNotExists(writerConfiguration.getOutputLocation() + File.separatorChar
                + compartment.getSoftwareComponent());
        final ReportWriterConfiguration writerConfiguration = getCurrentWriterConfiguration();
        writerConfiguration.setOutputLocation(baseDir.getAbsolutePath());

        try {
            new CompartmentHtmlReportWriter(new OutputStreamWriter(new FileOutputStream(new File(
                baseDir.getAbsolutePath(), "index.html")), "UTF-8"), writerConfiguration, compartment, dcFactory)
                .write();
        }
        catch (final IOException e) {
            new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visitDevelopmentComponent(final DevelopmentComponent component) {
        final File baseDir =
            createDirectoryIffNotExists(writerConfiguration.getOutputLocation() + File.separatorChar
                + component.getCompartment().getSoftwareComponent());
        final ReportWriterConfiguration writerConfiguration = getCurrentWriterConfiguration();
        writerConfiguration.setOutputLocation(baseDir.getAbsolutePath());

        if (!vendorFilter.accept(component) && component.isNeedsRebuild()) {
            FileWriter writer = null;

            try {
                writer =
                    new FileWriter(String.format("%s%c%s.html", baseDir.getAbsolutePath(), File.separatorChar,
                        String.format("%s~%s", component.getVendor(), component.getName().replace("/", "~"))));
                generator.execute(writer, component);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                if (writer != null) {
                    try {
                        writer.close();
                    }
                    catch (final IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
        }
    }

    protected void createOverviewPage(final DevelopmentConfiguration configuration) throws IOException {
        final File baseDir = createDirectoryIffNotExists(writerConfiguration.getOutputLocation());
        final Writer indexHtmlWriter =
            new OutputStreamWriter(new FileOutputStream(new File(baseDir, "index.html")), "UTF-8");
        final DevelopmentConfigurationsHtmlWriter cfgWriter =
            new DevelopmentConfigurationsHtmlWriter(indexHtmlWriter, writerConfiguration, configuration, dcFactory);
        cfgWriter.write();
        indexHtmlWriter.close();
    }

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
}
