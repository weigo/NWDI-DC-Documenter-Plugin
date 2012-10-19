/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import hudson.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.log4j.Logger;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.AbstractDevelopmentConfigurationVisitor;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
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
public final class DocBookReportGenerator extends AbstractDevelopmentConfigurationVisitor {
    /**
     * Folder to write generated documentation into.
     */
    private final File targetFolder;

    /**
     * Factory for creating report generators.
     */
    private final ReportGeneratorFactory reportGeneratorFactory;

    /**
     * velocity engine for transformations.
     */
    private final VelocityEngine velocity;

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
     * @param antHelper
     *            helper class for functionality related to development
     *            components.
     * @param dcFactory
     *            registry for development components.
     * @param velocity
     *            velocity template engine.
     * @param resourceBundle
     *            resource bundle for internationalization (target language).
     * @param vendorFilter
     *            filter for suppressing generation of documentation by vendor.
     * @param dotFileDescriptorContainer
     *            container for descriptors of generated usage diagrams.
     */
    public DocBookReportGenerator(final File targetFolder, final AntHelper antHelper,
        final DevelopmentComponentFactory dcFactory, final VelocityEngine velocity,
        final ResourceBundle resourceBundle, final VendorFilter vendorFilter,
        final DiagramDescriptorContainer dotFileDescriptorContainer) {
        this.targetFolder = targetFolder;
        this.velocity = velocity;
        this.vendorFilter = vendorFilter;
        this.dotFileDescriptorContainer = dotFileDescriptorContainer;
        reportGeneratorFactory = new ReportGeneratorFactory(antHelper, dcFactory, velocity, resourceBundle);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentConfiguration configuration) {
        try {
            createPOM(configuration);
            copyCssStyleSheet();
            final File targetFolder = getTargetFolder(null);
            createDevelopmentConfigurationDocument(configuration,
                createWriter(new File(targetFolder, String.format("%s.xml", configuration.getName()))));
            createLicenseOverViewDocument(configuration, createWriter(new File(targetFolder, "LicenseOverview.xml")));
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    private Writer createWriter(final File target) throws FileNotFoundException {
        return new OutputStreamWriter(new FileOutputStream(target), Charset.forName("UTF-8"));
    }

    /**
     * 
     */
    private void copyCssStyleSheet() {
        final File targetFolder = getTargetFolder("css");

        try {
            final String styleCss = "style.css";
            Util.copyStreamAndClose(getTemplate(styleCss), new FileWriter(new File(targetFolder, styleCss)));
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 
     */
    private void createPOM(final DevelopmentConfiguration configuration) {
        Writer writer = null;

        try {
            writer = new FileWriter(new File(targetFolder, "pom.xml"));
            final Context context = new VelocityContext();
            context.put("configuration", configuration);
            context.put("encoding", "utf-8");
            velocity.evaluate(context, writer, "", getTemplate("pom.vm"));
        }
        catch (final IOException e) {
            throw new IllegalStateException(e);
        }
        finally {
            if (writer != null) {
                try {
                    writer.close();
                }
                catch (final IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * @param configuration
     * @param fileWriter
     */
    private void createLicenseOverViewDocument(final DevelopmentConfiguration configuration, final Writer writer) {
        final ReportGenerator generator =
            reportGeneratorFactory.createGlobalLicenseOverviewReportGenerator(configuration);
        generator.execute(writer, additionalContext, DocBookVelocityTemplate.LicenseOverView.getTemplate());
    }

    /**
     * @param configuration
     * @param writer
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
                createDocument(compartment,
                    createWriter(new File(targetFolder, String.format("%s.xml", compartment.getName()))));
            }
            catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * @param compartment
     * @param createWriter
     */
    private void createDocument(final Compartment compartment, final Writer writer) {
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
                createDocument(component,
                    createWriter(new File(targetFolder, String.format("%s.xml", component.getNormalizedName("_")))));
            }
            catch (final IOException e) {
                throw new IllegalStateException(e);
            }
        }
    }

    /**
     * @param descriptor
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
     * @param component
     * @param writer
     */
    private void createDocument(final DevelopmentComponent component, final Writer writer) {
        final ReportGenerator generator = reportGeneratorFactory.create(component);
        generator.execute(writer, additionalContext, DocBookVelocityTemplate.DevelopmentComponent.getTemplate());
    }

    /**
     * Return a reader for the given velocity template (from class path).
     * 
     * @param template
     *            name of velocity template.
     * @return a reader for the given template.
     */
    private Reader getTemplate(final String template) {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            String.format("/org/arachna/netweaver/nwdi/documenter/report/%s", template)));
    }

    private File getTargetFolder(final String name) {
        final String folderName = name == null ? "" : name;
        final File targetFolder = new File(this.targetFolder, folderName);

        if (!targetFolder.exists()) {
            targetFolder.mkdirs();
        }

        return targetFolder;
    }
}
