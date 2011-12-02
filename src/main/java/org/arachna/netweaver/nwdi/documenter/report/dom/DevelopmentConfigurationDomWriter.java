/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.xml.DomHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Writer f�r Entwicklungskonfigurationen als DOM-Dokument.
 * 
 * @author Dirk Weigenand
 * 
 */
public final class DevelopmentConfigurationDomWriter {
    /**
     * Konstante für 'development-configurations'-Element.
     */
    private static final String DEVELOPMENT_CONFIGURATIONS = "development-configurations";

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    public DevelopmentConfigurationDomWriter(final DevelopmentComponentFactory dcFactory) {
        this.dcFactory = dcFactory;
    }

    /**
     * Schreibt die übergebene Entwicklungskonfiguration in ein DOM-Dokument.
     * 
     * @param developmentConfiguration
     *            zu schreibende Entwicklungskonfiguration
     * @return erzeugtes DOM-Dokument
     * @throws ParserConfigurationException
     *             wenn mit der Konfiguration für XML-Parser Probleme bestehen
     * @throws IOException
     * @throws TransformerException
     */
    public Document write(final DevelopmentConfiguration developmentConfiguration) throws ParserConfigurationException,
        TransformerException {
        final List<DevelopmentConfiguration> developmentConfigurations = new ArrayList<DevelopmentConfiguration>();
        developmentConfigurations.add(developmentConfiguration);
        return write(developmentConfigurations);
    }

    /**
     * Schreibt die übergebenen Entwicklungskonfigurationen in ein
     * DOM-Dokument.
     * 
     * @param developmentConfigurations
     *            zu schreibende Entwicklungskonfiguration
     * @return erzeugtes DOM-Dokument
     * @throws ParserConfigurationException
     */
    public Document write(final List<DevelopmentConfiguration> developmentConfigurations)
        throws ParserConfigurationException {
        final DomHelper domHelper = createDomHelper();
        final Element element = domHelper.createElement(DevelopmentConfigurationDomWriter.DEVELOPMENT_CONFIGURATIONS);
        Document document = domHelper.getDocument();
        document.appendChild(element);
        final DevelopmentConfigurationDomBuilder builder = new DevelopmentConfigurationDomBuilder(domHelper, dcFactory);

        for (final DevelopmentConfiguration developmentConfiguration : developmentConfigurations) {
            element.appendChild(builder.write(developmentConfiguration));
        }

        return document;
    }

    /**
     * @return
     * @throws ParserConfigurationException
     */
    private DomHelper createDomHelper() throws ParserConfigurationException {
        final DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        final DocumentBuilder builder = factory.newDocumentBuilder();

        return new DomHelper(builder.newDocument());
    }
}
