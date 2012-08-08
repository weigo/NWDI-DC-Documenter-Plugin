/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reader for <code>wdcomponent</code> files. Reads the referenced
 * views/window/controller configuration files and creates the respective
 * objects.
 * 
 * @author Dirk Weigenand
 */
public final class WebDynproComponentReader {
    public WebDynproComponent read(final Reader reader) {
        try {
            final Digester digester = new Digester(XMLReaderFactory.createXMLReader());

            return (WebDynproComponent)digester.parse(reader);
        }
        catch (final SAXException e) {
            throw new RuntimeException(e);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }
}
