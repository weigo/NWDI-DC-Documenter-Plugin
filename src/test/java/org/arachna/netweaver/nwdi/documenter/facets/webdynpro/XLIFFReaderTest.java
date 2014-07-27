/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;

import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unittests for {@link XLIFFReader}.
 * 
 * @author Dirk Weigenand
 */
public class XLIFFReaderTest {
    /**
     * Instance under test.
     */
    private XLIFFReader reader;

    private Xliff xliff;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        reader = new XLIFFReader();
        xliff =
            reader.execute(new InputStreamReader(getClass().getResourceAsStream(
                "/org/arachna/netweaver/nwdi/documenter/facets/webdynpro/xliff.xml")));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void testCreateXliffObject() {
        assertThat(xliff, notNullValue(Xliff.class));
    }

    @Test
    public void testCreateXliffGroup() {
        assertThat(xliff.getResourceTypes(), hasSize(4));
        assertThat(xliff.getResourceType("quickInfo"), notNullValue(XliffGroup.class));
        assertThat(xliff.getResourceType("formattedtext"), notNullValue(XliffGroup.class));
        assertThat(xliff.getResourceType("button"), notNullValue(XliffGroup.class));
        assertThat(xliff.getResourceType("caption"), notNullValue(XliffGroup.class));
    }

    @Test
    public void testAddTranslationUnit() {
        final XliffGroup buttons = xliff.getResourceType("button");
        assertThat(buttons, notNullValue(XliffGroup.class));
        final TranslationUnit translationUnit =
            buttons
                .getTranslationUnit("RootUIElementContainer/Child:ButtonRow/OutgoingAggregation:Buttons/AggregatedObject:Change_Button@text");
        assertThat(translationUnit, notNullValue(TranslationUnit.class));
        assertThat(translationUnit.getText(), equalTo("choose"));
    }
}
