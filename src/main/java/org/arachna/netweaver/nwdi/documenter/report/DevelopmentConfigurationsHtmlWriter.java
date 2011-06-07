/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.nwdi.documenter.report.dom.DevelopmentConfigurationDomWriter;
import org.w3c.dom.Document;

/**
 * Create a HTML report for development configurations.
 *
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationsHtmlWriter extends ReportWriter {
    /**
     * location of XSL stylesheet.
     */
    private static final String STYLE_SHEET =
        "/org/arachna/netweaver/nwdi/documenter/report/dom/DevelopmentConfigurationReport.xsl";

    private final List<DevelopmentConfiguration> configurations = new ArrayList<DevelopmentConfiguration>();

    /**
     * Create an instance of an {@link DevelopmentConfigurationsHtmlWriter}.
     *
     * @param writer
     *            writer object to generate report into.
     * @param writerConfiguration
     *            configuration object to use
     */
    public DevelopmentConfigurationsHtmlWriter(final Writer writer,
        final ReportWriterConfiguration writerConfiguration, DevelopmentConfiguration developmentConfiguration) {
        super(STYLE_SHEET, writer, writerConfiguration);
        this.configurations.add(developmentConfiguration);
    }

    /**
     * {@inheritDoc}
     * @throws TransformerException
     */
    @Override
    Document createDocument() throws ParserConfigurationException {
        final DevelopmentConfigurationDomWriter domWriter = new DevelopmentConfigurationDomWriter();

        return domWriter.write(configurations);
    }
}
