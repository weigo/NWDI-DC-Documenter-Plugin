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
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.AbstractDevelopmentConfigurationVisitor;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.nwdi.documenter.VendorFilter;

/**
 * Generator for documentation of a development configuration in HTML.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationHtmlGenerator extends AbstractDevelopmentConfigurationVisitor {
    /**
     * constant for index.html in development configuration and compartment folders.
     */
    private static final String INDEX_HTML = "index.html";

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
    private final VendorFilter vendorFilter;

    /**
     * Generator for a report on a development component. The target format is determined via the Velocity template given at build time.
     */
    private final DevelopmentComponentReportGenerator generator;

    /**
     * Additional context to provide to velocity.
     */
    private final Map<String, Object> additionalContext = new HashMap<String, Object>();

    /**
     * output documentation using this encoding.
     */
    private final String charsetName = "UTF-8";

    /**
     * Create a new generator for HTML documentation.
     * 
     * @param writerConfiguration
     *            configuration to use for generation (contains locations for CSS & JS files).
     * @param dcFactory
     *            registry for development components
     * @param vendorFilter
     *            filter for exclusion of vendors
     * @param velocityEngine
     *            velocity engine to documentation generation.
     */
    public DevelopmentConfigurationHtmlGenerator(final ReportWriterConfiguration writerConfiguration,
        final DevelopmentComponentFactory dcFactory, final VendorFilter vendorFilter,
        final VelocityEngine velocityEngine, final DevelopmentComponentReportGenerator generator) {
        this.writerConfiguration = writerConfiguration;
        this.dcFactory = dcFactory;
        this.vendorFilter = vendorFilter;
        this.generator = generator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentConfiguration configuration) {
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
     * Create a <code>ReportWriterConfiguration</code> for suitable for a compartment (i.e. in a subdirectory).
     * 
     * @return a <code>ReportWriterConfiguration</code> for suitable for a compartment
     */
    protected ReportWriterConfiguration getCurrentWriterConfiguration() {
        final String template = "../";
        final ReportWriterConfiguration writerConfiguration = new ReportWriterConfiguration();
        writerConfiguration.setCssLocation(String.format(template, this.writerConfiguration.getCssLocation()));
        writerConfiguration.setImageFormat(this.writerConfiguration.getImageFormat());
        writerConfiguration.setImagesLocation(this.writerConfiguration.getImagesLocation());
        writerConfiguration.setJsLocation(String.format(template, this.writerConfiguration.getJsLocation()));

        return writerConfiguration;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final Compartment compartment) {
        final File baseDir =
            createDirectoryIffNotExists(writerConfiguration.getOutputLocation() + File.separatorChar
                + compartment.getSoftwareComponent());
        final ReportWriterConfiguration writerConfiguration = getCurrentWriterConfiguration();
        writerConfiguration.setOutputLocation(baseDir.getAbsolutePath());

        try {
            new CompartmentHtmlReportWriter(getWriter(baseDir), writerConfiguration, compartment, dcFactory).write();
        }
        catch (final IOException e) {
            new RuntimeException(e);
        }
    }

    /**
     * Create a writer for a file "index.html" in the given base directory. Use the global charsetName as encoding.
     * 
     * @param baseDir
     *            base folder where to create the "index.html".
     * @return a writer for "index.html" in the given base folder.
     * @throws UnsupportedEncodingException
     *             when the globally configured encoding is not supported.
     * @throws FileNotFoundException
     *             when the given base folder cannot be found.
     */
    protected Writer getWriter(final File baseDir) throws UnsupportedEncodingException, FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(new File(baseDir.getAbsolutePath(), INDEX_HTML)),
            charsetName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentComponent component) {
        final File baseDir =
            createDirectoryIffNotExists(writerConfiguration.getOutputLocation() + File.separatorChar
                + component.getCompartment().getSoftwareComponent());
        final ReportWriterConfiguration writerConfiguration = getCurrentWriterConfiguration();
        writerConfiguration.setOutputLocation(baseDir.getAbsolutePath());

        if (!vendorFilter.accept(component) && component.isNeedsRebuild()) {
            FileWriter writer = null;

            try {
                writer = new FileWriter(new File(baseDir, String.format("%s.html", component.getNormalizedName("~"))));
                generator.execute(writer, component, additionalContext);
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

    /**
     * Creates the overview page for the given development configuration.
     * 
     * @param configuration
     *            development configuration to create overview page for.
     * @throws IOException
     *             when the overview page cannot be written.
     */
    protected void createOverviewPage(final DevelopmentConfiguration configuration) throws IOException {
        final File baseDir = createDirectoryIffNotExists(writerConfiguration.getOutputLocation());
        final Writer indexHtmlWriter = getWriter(baseDir);
        final DevelopmentConfigurationsHtmlWriter cfgWriter =
            new DevelopmentConfigurationsHtmlWriter(indexHtmlWriter, writerConfiguration, configuration, dcFactory);
        cfgWriter.write();
        indexHtmlWriter.close();
    }

    /**
     * Copies the resources (JavaScript and CSS) to the respective target folders.
     * 
     * @throws IOException
     *             when a resource could not be copied into its respective target folder
     */
    private void copyResources() throws IOException {
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
     * Copy the given resource into its target folder using the given target file name.
     * 
     * @param targetFolder
     *            folder to copy resource into.
     * @param absoluteResourcePath
     *            absolute class path reference to resource.
     * @param targetName
     *            name to use in target folder.
     * @throws IOException
     *             when the resource could not be copied into the target folder.
     */
    private void copyResourceToTargetFolder(final File targetFolder, final String absoluteResourcePath,
        final String targetName) throws IOException {
        IOUtils.copy(this.getClass().getResourceAsStream(absoluteResourcePath),
            new FileOutputStream(targetFolder.getAbsolutePath() + File.separatorChar + targetName));
    }

    /**
     * Create a directory for the given file iff it does not exist. Throws a <code>RuntimeException</code> if the directory could not be
     * created.
     * 
     * @param folderName
     *            absolute path to folder that should be created.
     * @return the newly created or already existing file.
     */
    private File createDirectoryIffNotExists(final String folderName) {
        final File directory = new File(folderName);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Could not create " + directory.getAbsolutePath() + "!");
        }

        return directory;
    }
}
