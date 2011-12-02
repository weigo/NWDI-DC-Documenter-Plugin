/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.Writer;
import java.util.Collection;

import javax.xml.parsers.ParserConfigurationException;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.nwdi.documenter.report.dom.CompartmentDomBuilder;
import org.arachna.xml.DomHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Create a report about a set of given {@link Compartment} objects and their
 * respective {@link DevelopmentComponent} objects.
 * 
 * @author Dirk Weigenand
 */
public final class CompartmentsHtmlReportWriter extends ReportWriter {
    /**
     * path to XSL style sheet.
     */
    private static final String STYLE_SHEET =
        "/org/arachna/netweaver/nwdi/documenter/report/dom/CompartmentsReport.xsl";

    /**
     * list of compartments the report is about.
     */
    private final Collection<Compartment> compartments;

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Create an instance of a <code>CompartmentsHtmlReportWriter</code> using
     * the given {@link Writer}, {@link ReportWriterConfiguration} and list of
     * compartments.
     * 
     * @param writer
     *            writer object to use writing the report.
     * @param writerConfiguration
     *            configuration to use writing the report.
     * @param compartments
     *            list of compartments the report should contain.
     */
    public CompartmentsHtmlReportWriter(final Writer writer, final ReportWriterConfiguration writerConfiguration,
        final Collection<Compartment> compartments, DevelopmentComponentFactory dcFactory) {
        super(STYLE_SHEET, writer, writerConfiguration,dcFactory);
        this.compartments = compartments;
        this.dcFactory = dcFactory;
    }

    /**
     * Create a report about a set of given {@link Compartment} objects and
     * their respective {@link DevelopmentComponent} objects.
     * 
     * @throws ParserConfigurationException
     */
    Document createDocument() throws ParserConfigurationException {
        final DomHelper domHelper = this.createDomHelper();
        final Document document = domHelper.getDocument();
        final Element compartments = document.createElement("compartments");

        // FIXME: Name der Entwicklungskonfiguration sollte herein gereicht
        // werden.
        if (!this.compartments.isEmpty()) {
            compartments.setAttribute("development-configuration", this.compartments.iterator().next()
                .getDevelopmentConfiguration().getName());
        }

        for (final Compartment compartment : this.compartments) {
            final CompartmentDomBuilder domWriter = new CompartmentDomBuilder(domHelper, dcFactory);
            compartments.appendChild(domWriter.write(compartment));
        }

        document.appendChild(compartments);

        return document;
    }
}
