/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.Digester;
import org.arachna.xml.NullEntityResolver;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Parser for WebDynpro applications. Read Application properties of WebDynpro
 * applications.
 * 
 * @author Dirk Weigenand
 */
public final class WebDynproApplicationParser {
    /**
     * 
     */
    private static final String APPLICATION = "Application";

    /**
     * 
     */
    private static final String APPLICATION_PROPERTIES = "Application/Application.ApplicationProperties";

    /**
     * 
     */
    private static final String APPLICATION_PROPERTY = APPLICATION_PROPERTIES + "/ApplicationProperty";

    /**
     * Read a WebDynpro application from the given {@link Reader}.
     * 
     * @param reader
     *            content of WebDynpro application definition.
     * @return the WebDynpro application configured from the content of the
     *         given WebDynpro application definition.
     */
    public WebDynproApplication parse(final Reader reader) {
        try {
            final Digester digester = new Digester(XMLReaderFactory.createXMLReader());
            digester.setEntityResolver(new NullEntityResolver());
            digester.addObjectCreate(APPLICATION, WebDynproApplication.class);
            digester.addSetProperties(APPLICATION);
            digester.addObjectCreate(APPLICATION_PROPERTIES, ApplicationProperties.class);
            digester.addSetNext(APPLICATION_PROPERTIES, "setProperties");

            digester.addCallMethod(APPLICATION_PROPERTY, "addProperty", 2);
            digester.addCallParam(APPLICATION_PROPERTY, 0, "name");
            digester.addCallParam(APPLICATION_PROPERTY, 1, "value");

            return (WebDynproApplication)digester.parse(reader);
        }
        catch (final SAXException e) {
            throw new RuntimeException(e);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                reader.close();
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
