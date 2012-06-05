/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report.svg;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import org.apache.commons.digester3.Digester;
import org.arachna.xml.AbstractDefaultHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parser for SVG documents. Extracts general properties of an SVG {@see SVGProperties}.
 * 
 * @author Dirk Weigenand
 */
public final class SVGParser extends AbstractDefaultHandler {
    /**
     * Parse the given SVG and extract general properties (width, height).
     * 
     * @param reader
     *            the reader containing the SVG document.
     * @return {@link SVGProperties} read from the SVG document.
     */
    public SVGProperties parse(Reader reader) {
        try {
            Digester digester = new Digester(XMLReaderFactory.createXMLReader());
            digester.setEntityResolver(new NullEntityResolver());
            digester.addObjectCreate("svg", SVGProperties.class);

            digester.addCallMethod("svg", "setWidth", 1);
            digester.addCallParam("svg", 0, "width");
            digester.addCallMethod("svg", "setHeight", 1);
            digester.addCallParam("svg", 0, "height");

            return (SVGProperties)digester.parse(reader);
        }
        catch (SAXException e) {
            throw new RuntimeException(e);
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * {@link EntityResolver} to avoid hitting the net for URLs that can't be resolved anyway.
     * 
     * @author Dirk Weigenand (G526521)
     */
    private class NullEntityResolver implements EntityResolver {
        /**
         * {@inheritDoc}
         */
        @Override
        public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
            return new InputSource(new StringReader(""));
        }
    }
}
