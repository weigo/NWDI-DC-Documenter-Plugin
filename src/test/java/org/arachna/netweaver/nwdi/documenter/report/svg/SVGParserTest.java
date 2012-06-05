/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report.svg;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.InputStreamReader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * JUnit test for {@link SVGParser}.
 * 
 * @author Dirk Weigenand
 */
public class SVGParserTest {
    private SVGParser instance;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        instance = new SVGParser();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for {@link org.arachna.netweaver.nwdi.documenter.report.svg.SVGParser#parse(java.io.Reader)}.
     */
    @Test
    public final void testParse() {
        SVGProperties properties = this.instance.parse(new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/nwdi/documenter/report/svg/svg.xml")));

        assertThat("839pt", equalTo(properties.getProperty(SVGPropertyName.WIDTH)));
    }
}
