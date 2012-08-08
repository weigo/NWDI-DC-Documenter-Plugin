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
 * Unittests for {@link WebDynproApplicationParser}.
 * 
 * @author Dirk Weigenand
 */
public class WebDynproApplicationParserTest {
    /**
     * Instance under test.
     */
    private WebDynproApplicationParser parser;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        parser = new WebDynproApplicationParser();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        parser = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.webdynpro.WebDynproApplicationParser#parse(java.io.Reader)}
     * .
     */
    @Test
    public final void testParseApplication() {
        final WebDynproApplication application = parser.parse(getWDApplicationReader());
        assertThat(application, notNullValue());
        assertThat(application.getName(), equalTo("ExampleApplication"));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.webdynpro.WebDynproApplicationParser#parse(java.io.Reader)}
     * .
     */
    @Test
    public final void testParseApplicationProperties() {
        final WebDynproApplication application = parser.parse(getWDApplicationReader());
        final ApplicationProperties properties = application.getProperties();
        assertThat(properties, notNullValue());
        assertThat(properties.size(), equalTo(2));
        assertThat(properties.get("property1"), equalTo("value1"));
        assertThat(properties.get("property2"), equalTo("value2"));
    }

    private Reader getWDApplicationReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/nwdi/documenter/webdynpro/WebDynproApplication.wdapplication"));
    }
}
