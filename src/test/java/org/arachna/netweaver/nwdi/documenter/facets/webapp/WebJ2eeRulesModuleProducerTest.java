/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.arachna.xml.DigesterHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 *
 */
public class WebJ2eeRulesModuleProducerTest {
    /**
     * Webapplication that should be built as a result of the <code>web.xml</code> parsing.
     */
    private WebApplication webApp;

    /**
     * @throws IOException
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws IOException {
        webApp = new WebApplication();
        updateSecurityRoleMappings(webApp);
    }

    @Test
    public final void testUpdateWebApplicationWithSecurityRoleMappingsMapsToRole() {
        assertThat(webApp.getServerRole("examplePermissionRole"), equalTo("EXAMPLE_PERMISSION_ROLE_IN_UME"));
    }

    @Test
    public final void testUpdateWebApplicationWithSecurityRoleMappingsDoesNotMapToRole() {
        assertThat(webApp.getServerRole("examplePermissionRole2"), nullValue(String.class));
    }

    private void updateSecurityRoleMappings(final WebApplication application) throws IOException {
        new DigesterHelper<WebApplication>(new WebJ2eeRulesModuleProducer()).update(getWebJ2eeXmlReader(), webApp);
    }

    /**
     * @param component
     * @return
     * @throws FileNotFoundException
     */
    private Reader getWebJ2eeXmlReader() throws FileNotFoundException {
        return new InputStreamReader(getClass().getResourceAsStream("web-j2ee-engine.xml"));
    }
}
