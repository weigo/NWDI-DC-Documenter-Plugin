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
 * Reader for XLIFF files. These files contain information for
 * internationalization of Web Dynpro text resources.
 * 
 * @author Dirk Weigenand
 */
public final class XLIFFReader {
    /**
     * 
     */
    private static final String XLIFF_BODY_GROUP = "xliff/file/body/group";
    /**
     * 
     */
    private static final String TRANS_UNIT = XLIFF_BODY_GROUP + "/trans-unit";
    /**
     * 
     */
    private static final String TRANS_UNIT_SOURCE = TRANS_UNIT + "/source";

    public Xliff execute(final Reader reader) {
        try {
            final Digester digester = new Digester(XMLReaderFactory.createXMLReader());
            digester.addObjectCreate("xliff", Xliff.class);
            digester.addObjectCreate(XLIFF_BODY_GROUP, XliffGroup.class);
            digester.addSetProperties(XLIFF_BODY_GROUP, "restype", "resourceType");
            digester.addSetNext(XLIFF_BODY_GROUP, "addResourceType");

            digester.addObjectCreate(TRANS_UNIT, TranslationUnit.class);
            digester.addSetProperties(TRANS_UNIT, "resname", "resourceName");
            digester.addCallMethod(TRANS_UNIT_SOURCE, "setText", 1);
            digester.addCallParam(TRANS_UNIT_SOURCE, 0);
            // digester.addSetProperties(TRANS_UNIT + "/source", "text");
            digester.addSetNext(TRANS_UNIT, "addTranslationUnit");

            return (Xliff)digester.parse(reader);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
        catch (final SAXException e) {
            throw new RuntimeException(e);
        }
    }
}
