/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.Writer;

import javax.xml.parsers.ParserConfigurationException;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.nwdi.documenter.report.dom.CompartmentDomBuilder;
import org.arachna.netweaver.nwdi.documenter.report.dom.DomHelper;
import org.w3c.dom.Document;

/**
 * Create a report about a {@link Compartment}.
 *
 * @author Dirk Weigenand
 */
public final class CompartmentHtmlReportWriter extends ReportWriter {
    /**
     * path to XSL style sheet.
     */
    private static final String STYLE_SHEET = "/org/arachna/netweaver/nwdi/documenter/report/dom/CompartmentReport.xsl";

    /**
     * the compartment this report is all about.
     */
    private final Compartment compartment;

    /**
     * Create a report about a {@link Compartment}.
     *
     * @param writer
     *            writer to use for report creation.
     * @param writerConfiguration
     *            configuration object to use for this report.
     * @param compartment
     *            the compartment this report is all about.
     */
    public CompartmentHtmlReportWriter(final Writer writer, final ReportWriterConfiguration writerConfiguration,
        final Compartment compartment) {
        super(STYLE_SHEET, writer, writerConfiguration);
        this.compartment = compartment;
    }

    /**
     * Create a report about a {@link Compartment}.
     *
     * @throws ParserConfigurationException
     */
    Document createDocument() throws ParserConfigurationException {
        final DomHelper domHelper = createDomHelper();
        Document document = domHelper.getDocument();
        final CompartmentDomBuilder domWriter = new CompartmentDomBuilder(domHelper);
        document.appendChild(domWriter.write(compartment));

        return document;
    }
}
