/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.core.IsNull.notNullValue;

import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 * 
 */
public class XLIFFReaderTest {
    /**
     * instance under test.
     */
    private XLIFFReader xliffReader;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        xliffReader = new XLIFFReader();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        xliffReader = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.webdynpro.XLIFFReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testAddObjectCreateXliff() {
        final Xliff xliff = xliffReader.execute(getTestDocumentReader());

        assertThat(xliff, notNullValue());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.webdynpro.XLIFFReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testAddObjectCreateXliffWithQuickInfoGroup() {
        final Xliff xliff = xliffReader.execute(getTestDocumentReader());

        assertThat(xliff.getResourceType("quickInfo"), notNullValue());
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.webdynpro.XLIFFReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testAddObjectCreateXliffWithQuickInfoGroupWithTranslationUnit() {
        final Xliff xliff = xliffReader.execute(getTestDocumentReader());

        final XliffGroup group = xliff.getResourceType("quickInfo");
        final TranslationUnit translationUnit = group.getTranslationUnit("DocumentationObject@quickInfo");
        assertThat(translationUnit, notNullValue());
        assertThat(translationUnit.getText(), equalTo("quickInfoText"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.webdynpro.XLIFFReader#execute(java.io.Reader)}
     * .
     */
    @Test
    public final void testAddObjectCreateXliffWithQuickInfoGroupWithTranslationUnitForFormattedText() {
        final Xliff xliff = xliffReader.execute(getTestDocumentReader());

        final XliffGroup group = xliff.getResourceType("formattedtext");
        final TranslationUnit translationUnit = group.getTranslationUnit("DocumentationObject@technicalDocumentation");
        assertThat(translationUnit, notNullValue());
        assertThat(translationUnit.getText(), equalTo("technicalDocumentation"));
    }

    /**
     * @return
     */
    private Reader getTestDocumentReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/nwdi/documenter/webdynpro/xliff.xml"));
    }

}
