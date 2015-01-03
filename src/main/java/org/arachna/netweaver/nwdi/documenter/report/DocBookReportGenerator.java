/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.DevelopmentConfigurationVisitor;
import org.arachna.netweaver.nwdi.documenter.filter.VendorFilter;
import org.arachna.netweaver.nwdi.documenter.report.svg.SVGParser;
import org.arachna.netweaver.nwdi.documenter.report.svg.SVGProperties;
import org.arachna.netweaver.nwdi.documenter.report.svg.SVGPropertyName;
import org.arachna.netweaver.nwdi.dot4j.DiagramDescriptor;
import org.arachna.netweaver.nwdi.dot4j.DiagramDescriptorContainer;

/**
 * Generator for DocBook documentation for a development configuration.
 * 
 * @author Dirk Weigenand
 */
public final class DocBookReportGenerator implements DevelopmentConfigurationVisitor {
    /**
     * Folder to write generated documentation into.
     */
    private final File targetFolder;

    /**
     * Factory for creating report generators.
     */
    private final ReportGeneratorFactory reportGeneratorFactory;

    /**
     * additional context to provider for evaluation of velocity templates.
     */
    private final Map<String, Object> additionalContext = new LinkedHashMap<String, Object>();

    /**
     * filter development components by vendor.
     */
    private final VendorFilter vendorFilter;

    /**
     * container for descriptors of generated dependency diagrams.
     */
    private final DiagramDescriptorContainer dotFileDescriptorContainer;

    /**
     * Parser for SVG files. Determines size properties.
     */
    private final SVGParser svgParser = new SVGParser();

    /**
     * Create a new instance of a generator of a report in docbook format.
     * 
     * @param targetFolder
     *            target folder where to write report into.
     * @param reportGeneratorFactory
     *            factory for generators of report content for development
     *            configurations, compartments and development components.
     * @param vendorFilter
     *            filter for suppressing generation of documentation by vendor.
     * @param dotFileDescriptorContainer
     *            container for descriptors of generated usage diagrams.
     */
    public DocBookReportGenerator(final File targetFolder, final ReportGeneratorFactory reportGeneratorFactory,
        final VendorFilter vendorFilter, final DiagramDescriptorContainer dotFileDescriptorContainer) {
        this.targetFolder = targetFolder;
        this.vendorFilter = vendorFilter;
        this.dotFileDescriptorContainer = dotFileDescriptorContainer;
        this.reportGeneratorFactory = reportGeneratorFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentConfiguration configuration) {
        try {
            final File targetFolder = getTargetFolder(null);
            createDevelopmentConfigurationDocument(configuration,
                createWriter(getTargetFile(targetFolder, configuration.getName())));
            createLicenseOverViewDocument(configuration, createWriter(getTargetFile(targetFolder, "LicenseOverview")));
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * Create a writer for the given file object.
     * 
     * @param target
     *            target for content.
     * @return the created writer
     * @throws FileNotFoundException
     *             when the given target file does not exist.
     */
    protected Writer createWriter(final File target) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(target), Charset.forName("UTF-8"));
    }

    /**
     * Create an overview for licenses of external libraries used throughout the
     * given development configuration. Write the generated content into the
     * given writer.
     * 
     * @param configuration
     *            development configuration to generate overview for.
     * @param writer
     *            target for content.
     */
    private void createLicenseOverViewDocument(final DevelopmentConfiguration configuration, final Writer writer) {
        final ReportGenerator generator =
            reportGeneratorFactory.createGlobalLicenseOverviewReportGenerator(configuration);
        generator.execute(writer, additionalContext, DocBookVelocityTemplate.LicenseOverView.getTemplate());
    }

    /**
     * Create overview for the given development configuration. Write generated
     * content into the given writer.
     * 
     * @param configuration
     *            development configuration to create overview for.
     * @param writer
     *            target for content.
     */
    protected void createDevelopmentConfigurationDocument(final DevelopmentConfiguration configuration,
        final Writer writer) {
        final ReportGenerator generator = reportGeneratorFactory.create(configuration);
        generator.execute(writer, additionalContext, DocBookVelocityTemplate.DevelopmentConfiguration.getTemplate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final Compartment compartment) {
        if (!vendorFilter.accept(compartment)) {
            try {
                final File targetFolder = getTargetFolder(compartment.getName());
                createDocument(compartment, createWriter(getTargetFile(targetFolder, compartment.getName())));
            }
            catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Create overview for the given compartment. Write generated content into
     * the given writer.
     * 
     * @param compartment
     *            compartment to create overview for.
     * @param writer
     *            target for content.
     */
    protected void createDocument(final Compartment compartment, final Writer writer) {
        final ReportGenerator generator = reportGeneratorFactory.create(compartment);
        generator.execute(writer, additionalContext, DocBookVelocityTemplate.Compartment.getTemplate());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentComponent component) {
        if (!vendorFilter.accept(component)) {
            try {
                final DiagramDescriptor descriptor = dotFileDescriptorContainer.getDescriptor(component);

                if (descriptor != null) {
                    addDiagramWidthToAdditionalContext("UsedDCsDiagramWidth", "UsedDCsDiagramHeight",
                        descriptor.getUsedDCsDiagram());
                    addDiagramWidthToAdditionalContext("UsingDCsDiagramWidth", "UsingDCsDiagramHeight",
                        descriptor.getUsingDCsDiagram());
                }

                final File targetFolder = getTargetFolder(component.getCompartment().getName());
                createDocument(component, createWriter(getTargetFile(targetFolder, component.getNormalizedName("_"))));
            }
            catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * Update additional context map with width and height for the given diagram
     * name.
     * 
     * @param widthProperty
     *            name of property to use for diagram width
     * @param heightProperty
     *            name of property to use for diagram height
     * @param diagramName
     *            diagram name
     */
    private void addDiagramWidthToAdditionalContext(final String widthProperty, final String heightProperty,
        final String diagramName) {
        try {
            final SVGProperties properties =
                svgParser.parse(new FileReader(diagramName.replaceFirst("\\.dot", "\\.svg")));
            additionalContext.put(widthProperty, properties.getProperty(SVGPropertyName.WIDTH));
            additionalContext.put(heightProperty, properties.getProperty(SVGPropertyName.HEIGHT));
        }
        catch (final FileNotFoundException e) {
            Logger.getLogger(getClass()).error(e.getLocalizedMessage(), e);
            additionalContext.remove(widthProperty);
            additionalContext.remove(heightProperty);
        }
    }

    /**
     * Create documentation for the given development component. Write generated
     * content into the given writer.
     * 
     * @param component
     *            development component to create overview for.
     * @param writer
     *            target for content.
     */
    protected void createDocument(final DevelopmentComponent component, final Writer writer) {
        final ReportGenerator generator = reportGeneratorFactory.create(component);
        generator.execute(writer, additionalContext, DocBookVelocityTemplate.DevelopmentComponent.getTemplate());
    }

    /**
     * Return the target folder file object for the given target name. The
     * returned folder object is a child of the target folder for the
     * documentation.
     * 
     * Missing sub folders are created.
     * 
     * @param name
     *            name of target file.
     * @return the target file object.
     */
    private File getTargetFolder(final String name) {
        final String folderName = name == null ? "" : name;
        final File targetFolder = new File(this.targetFolder, folderName);

        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        return targetFolder;
    }

    /**
     * Create file object for the given base name in the given folder. The
     * returned file will have a suffix of '.xml'.
     * 
     * @param targetFolder
     *            folder to create target in.
     * @param baseName
     *            base name to use for file.
     * @return
     */
    private File getTargetFile(final File targetFolder, final String baseName) {
        return new File(targetFolder, String.format("%s.xml", baseName));
    }
}
