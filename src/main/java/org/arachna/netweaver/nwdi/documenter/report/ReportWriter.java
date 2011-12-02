/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.IOException;
import java.io.Writer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.xml.DomHelper;
import org.w3c.dom.Document;

/**
 * Abstract base class for html report writers.
 * 
 * @author Dirk Weigenand
 */
abstract class ReportWriter {
    /**
     * XSL stylesheet to use for transformation.
     */
    private final String styleSheet;

    /**
     * configuration to use for report generation.
     */
    private final ReportWriterConfiguration writerConfiguration;

    /**
     * Writer to receive transformation result (i.e. the resulting report).
     */
    private Writer writer;

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * @return the dcFactory
     */
    protected final DevelopmentComponentFactory getDcFactory() {
        return dcFactory;
    }

    /**
     * Create an instance of a report writer using the given stylesheet and
     * configuration.
     * 
     * @param styleSheet
     *            path to stylesheet in class hierarchy.
     * @param writerConfiguration
     *            configuration object.
     * @param dcFactory
     *            Registry for development components.
     */
    ReportWriter(final String styleSheet, final Writer writer, final ReportWriterConfiguration writerConfiguration,
        DevelopmentComponentFactory dcFactory) {
        this.styleSheet = styleSheet;
        this.writer = writer;
        this.writerConfiguration = writerConfiguration;
        this.dcFactory = dcFactory;
    }

    /**
     * Create and configure the transformer object to use generating the report.
     * Throws a <code>RuntimeException</code> when there is an error creating
     * the transformer.
     * 
     * @return the transformer to use for generating the report.
     */
    private Transformer getTransformer() {
        final StreamSource source = new StreamSource(this.getClass().getResourceAsStream(this.styleSheet));
        final TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = null;

        try {
            transformer = factory.newTransformer(source);
            transformer.setOutputProperty("method", "html");
            transformer.setOutputProperty("indent", "yes");
            transformer.setOutputProperty("encoding", "UTF-8");

            transformer.setParameter("cssLocation", this.writerConfiguration.getCssLocation());
            transformer.setParameter("imageLocation", this.writerConfiguration.getImagesLocation());
            transformer.setParameter("jsLocation", this.writerConfiguration.getJsLocation());
            transformer.setParameter("imageFormat", this.writerConfiguration.getImageFormat());
        }
        catch (final TransformerConfigurationException e) {
            throw new RuntimeException(e);
        }

        return transformer;
    }

    /**
     * @return
     * @throws ParserConfigurationException
     */
    protected DomHelper createDomHelper() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();
        return new DomHelper(builder.newDocument());
    }

    protected final void transform(Document document) throws TransformerException {
        this.getTransformer().transform(new DOMSource(document), new StreamResult(this.writer));
    }

    /**
     * Create the document that is to be written as report.
     * 
     * @return
     */
    abstract Document createDocument() throws ParserConfigurationException;

    public final void write() throws IOException {
        try {
            this.transform(this.createDocument());
        }
        catch (TransformerException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        catch (ParserConfigurationException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        finally {
            this.writer.close();
        }
    }
}
