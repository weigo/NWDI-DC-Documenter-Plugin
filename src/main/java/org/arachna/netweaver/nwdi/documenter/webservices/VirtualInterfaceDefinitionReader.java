/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * Reader for virtual interface definitions of web services.
 * 
 * @author Dirk Weigenand
 */
public class VirtualInterfaceDefinitionReader {
    /**
     * Reads the definition of a virtual interface of a web service from the
     * given reader and returns a descriptor representing the interface.
     * 
     * @param reader
     *            reader to read interface definition from
     * @return descriptor of the read interface definition
     * @throws SAXException
     * @throws IOException
     */
    public VirtualInterfaceDefinition read(final Reader reader) throws IOException, SAXException {
        final Digester digester = new Digester();

        digester.addObjectCreate("VirtualInterface", VirtualInterfaceDefinition.class);

        return (VirtualInterfaceDefinition)digester.parse(reader);
    }
}
