/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

import java.io.InputStreamReader;
import java.io.Reader;

import org.arachna.xml.DigesterHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link WebDynproComponentRulesModuleProducer}.
 * 
 * @author Dirk Weigenand
 */
public class WebDynproComponentRulesModuleProducerTest {
    /**
     * For executing the RulesModuleProducer under test.
     */
    private DigesterHelper<WebDynproComponent> digesterHelper;

    /**
     * Parsing result for inspection.
     */
    private WebDynproComponent component;

    /**
     */
    @Before
    public void setUp() {
        digesterHelper = new DigesterHelper<WebDynproComponent>(new WebDynproComponentRulesModuleProducer());
        component = digesterHelper.execute(getReader());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() {
        digesterHelper = null;
        component = null;
    }

    /**
     * Test method for {@link org.arachna.netweaver.nwdi.documenter.facets.webdynpro.WebDynproComponentRulesModuleProducer#getRulesModule()}
     * .
     */
    @Test
    public final void testGetRulesModule() {
        assertThat(component, notNullValue(WebDynproComponent.class));
    }

    private Reader getReader() {
        return new InputStreamReader(getClass().getResourceAsStream("ExampleComp.wdcomponent"));
    }
}
