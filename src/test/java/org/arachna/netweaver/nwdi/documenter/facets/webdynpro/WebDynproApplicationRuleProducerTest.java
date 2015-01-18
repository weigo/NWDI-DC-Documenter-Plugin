/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStreamReader;
import java.io.Reader;

import org.arachna.xml.DigesterHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link WebDynproApplicationRuleProducer}.
 *
 * @author Dirk Weigenand
 */
public class WebDynproApplicationRuleProducerTest {
    /**
     * <code>WebDynproApplication</code> parsed using the <code>WebDynproApplicationRuleProducer</code>.
     */
    private WebDynproApplication application;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        application =
            new DigesterHelper<WebDynproApplication>(new WebDynproApplicationRuleProducer()).execute(getExampleWDApplicationReader());
    }

    /**
     */
    @After
    public void tearDown() {
        application = null;
    }

    /**
     * Verify that parsing the 'Application' element creates an WebDynproApplication object.
     */
    @Test
    public final void verifyCreateWebDynproApplicationRule() {
        assertThat(application, notNullValue(WebDynproApplication.class));
    }

    /**
     * Verify that properties of the WebDynproApplication object are set correctly.
     */
    @Test
    public void verifySetApplicationBeanProperties() {
        assertThat(application.getName(), equalTo("ExampleApplication"));
    }

    /**
     * Verify that application properties of the WebDynproApplication are correctly.
     */
    @Test
    public void verifyParseApplicationProperties() {
        final ApplicationProperties properties = application.getProperties();
        assertThat(properties.get("property1"), equalTo("value1"));
        assertThat(properties.get("property2"), equalTo("value2"));
    }

    private Reader getExampleWDApplicationReader() {
        return new InputStreamReader(getClass().getResourceAsStream("WebDynproApplication.wdapplication"));
    }
}
