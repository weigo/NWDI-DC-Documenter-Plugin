/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.nwdi.documenter.report.dom.DevelopmentComponentDomBuilder;
import org.arachna.xml.DomHelper;
import org.w3c.dom.Document;

/**
 * Create a report about a {@link DevelopmentComponent} detailing its purpose,
 * dependencies and usage (using DCs).
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentHtmlReportWriter extends ReportWriter {
    /**
     * path to XSL style sheet.
     */
    private static final String STYLE_SHEET =
        "/org/arachna/netweaver/nwdi/documenter/report/dom/DevelopmentComponentReport.xsl";

    /**
     * development component the report is all about.
     */
    private final DevelopmentComponent component;

    /**
     * Create an instance of a <code></code> using the given writer,
     * configuration and development component objects.
     * 
     * @param writer
     *            writer used to create report.
     * @param writerConfiguration
     *            configuration to use in creating the report.
     * @param component
     *            development component the report is about.
     */
    public DevelopmentComponentHtmlReportWriter(final Writer writer,
        final ReportWriterConfiguration writerConfiguration, final DevelopmentComponent component,
        DevelopmentComponentFactory dcFactory) {
        super(STYLE_SHEET, writer, writerConfiguration, dcFactory);
        this.component = component;
    }

    /**
     * Create a report about a {@link DevelopmentComponent} detailing its
     * purpose, dependencies and usage (using DCs).
     * 
     * @throws ParserConfigurationException
     * 
     * @throws IOException
     *             any <code>IOException</code> thrown during report creation.
     */
    Document createDocument() throws ParserConfigurationException {
        DomHelper domHelper = this.createDomHelper();
        final Document document = domHelper.getDocument();
        final DevelopmentComponentDomBuilder domWriter =
            new DevelopmentComponentDomBuilder(domHelper, this.getDcFactory());
        document.appendChild(domWriter.write(component));

        return document;
    }
}
