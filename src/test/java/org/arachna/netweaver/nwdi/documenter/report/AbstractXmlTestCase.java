/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.IOException;

import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.xml.sax.SAXException;

/**
 * Base class for unit tests dealing with creating XML.
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractXmlTestCase extends XMLTestCase {
    /**
     * The tests have to implement this method to create the document that should be tested against.
     * 
     * @return XML to test against.
     */
    protected abstract String createDocument();

    /**
     * Assert method to test the result of the given xPath against the given 'expected' value. The document to test against will be created
     * in {@see #createDocument()}.
     * 
     * @param expected
     *            expected result
     * @param xPath
     *            xpath to select the actual generated value.
     */
    protected final void assertXpathEvaluatesTo(final String expected, final String xPath) {
        try {
            assertXpathEvaluatesTo(expected, xPath, createDocument());
        }
        catch (final XpathException e) {
            fail(e);
        }
        catch (final SAXException e) {
            fail(e);
        }
        catch (final IOException e) {
            fail(e);
        }
    }

    /**
     * Fail emitting using the cause of the inner most throwable.
     * 
     * @param t
     *            throwable when an exception was caught.
     */
    protected final void fail(final Throwable t) {
        if (t.getCause() != null) {
            fail(t.getCause());
        }
        else {
            fail(t.getLocalizedMessage());
        }
    }
}