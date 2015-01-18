/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

import org.arachna.xml.DigesterHelper;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for parsing <code>web.xml</code>.
 *
 * @author Dirk Weigenand
 */
public class WebXmlRulesModuleProducerTest {
    /**
     * Webapplication that should be built as a result of the <code>web.xml</code> parsing.
     */
    private WebApplication webApp;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() {
        webApp = new DigesterHelper<WebApplication>(new WebXmlRulesModuleProducer()).execute(getWebXml());
    }

    /**
     * This rule should push a newly minted {@link WebApplication} object on the digester stack and return it from the parsing process.
     */
    @Test
    public final void testCreateWebAppRule() {
        assertThat(webApp, notNullValue());
    }

    /**
     * This rule should create a new {@link ServletDescriptor} instance and attach it to the web application.
     */
    @Test
    public void testCreateServletAndAddToWebApplicationRule() {
        assertThat(webApp.getServletDescriptors(), not(empty()));
    }

    /**
     * This rule should set the name on the servlet created last.
     */
    @Test
    public void testCreateSetServletNameRule() {
        final ServletDescriptor descriptor = getFirstDeclaredServlet();

        assertThat(descriptor.getName(), equalTo("JerseyRESTService"));
    }

    /**
     * This rule should set the clazz property on the servlet created last.
     */
    @Test
    public void testCreateServletClassRule() {
        final ServletDescriptor descriptor = getFirstDeclaredServlet();

        assertThat(descriptor.getClazz(), equalTo(ServletDescriptor.JERSEY_SERVLET_CLASS));
    }

    /**
     * This rule should set the clazz property on the servlet created last.
     */
    @Test
    public void testCreateServletMappingRule() {
        final ServletDescriptor descriptor = getFirstDeclaredServlet();

        assertThat(descriptor.getServletMapping(), equalTo("/api/*"));
    }

    /**
     * This rule should add security-role/role-name elements to the web application.
     */
    @Test
    public void testCreateSecurityRoleRoleNameMappingRule() {
        assertThat(webApp.getSecurityRoles(), hasSize(2));
    }

    /**
     * This rule should add create and add newly created security constraint to the web application.
     */
    @Test
    public void testCreateSecurityConstraintRule() {
        final List<SecurityConstraintDescriptor> securityConstraints =
            new ArrayList<SecurityConstraintDescriptor>(webApp.getSecurityConstraints());
        assertThat(securityConstraints, hasSize(2));

        final SecurityConstraintDescriptor descriptor = securityConstraints.get(0);

        final List<WebResourceCollection> webResources = new ArrayList<WebResourceCollection>(descriptor.getWebResourceCollections());
        assertThat(webResources, hasSize(1));

        final WebResourceCollection webResource = webResources.get(0);

        assertThat(webResource.getName(), equalTo("ServicesWithAuth"));
        assertThat(webResource.getUrlPattern(), equalTo("/api/*"));

        final AuthConstraint authConstraint = descriptor.getAuthConstraint();
        assertThat(authConstraint.getDescription(), equalTo("Sicherheitsrolle zum Mapping auf Serverrolle in web-j2ee-engine.xml."));
        assertThat(authConstraint.getRoleName(), equalTo("examplePermissionRole"));
    }

    /**
     * Get the <code>web.xml</code> resource for test execution.
     *
     * @return a {@link Reader} object containing the <code>web.xml</code> resource used for test execution.
     */
    private Reader getWebXml() {
        final InputStream resource = getClass().getResourceAsStream("/org/arachna/netweaver/nwdi/documenter/facets/webapp/web.xml");

        if (resource == null) {
            throw new IllegalStateException("web.xml for test not found!");
        }

        return new InputStreamReader(resource);
    }

    /**
     * @return
     */
    private ServletDescriptor getFirstDeclaredServlet() {
        final List<ServletDescriptor> descriptors = new ArrayList<ServletDescriptor>(webApp.getServletDescriptors());

        return descriptors.get(0);
    }
}
