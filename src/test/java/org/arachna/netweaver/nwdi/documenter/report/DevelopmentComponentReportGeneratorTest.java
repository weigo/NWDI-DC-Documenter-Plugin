/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.nwdi.documenter.DocumentationBuilder;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.netweaver.nwdi.documenter.facets.webapp.WebApplication;
import org.arachna.netweaver.nwdi.documenter.facets.webapp.WebApplicationDocumentationFacetProvider;
import org.arachna.netweaver.nwdi.documenter.facets.webapp.WebJ2eeRulesModuleProducer;
import org.arachna.netweaver.nwdi.documenter.facets.webapp.WebXmlRulesModuleProducer;
import org.arachna.netweaver.nwdi.documenter.facets.webapp.restservices.RestService;
import org.arachna.netweaver.nwdi.documenter.facets.webapp.restservices.RestServiceVisitor;
import org.arachna.velocity.VelocityHelper;
import org.arachna.xml.DigesterHelper;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * @author Dirk Weigenand
 */
public class DevelopmentComponentReportGeneratorTest extends AbstractXmlTestCase {
    private DevelopmentComponentReportGenerator generator;
    private DevelopmentComponentFactory dcFactory;
    private VelocityEngine velocityEngine;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() {
        dcFactory = new DevelopmentComponentFactory();
        velocityEngine = new VelocityHelper().getVelocityEngine();
    }

    /**
     * Test method for
     * {@link DevelopmentComponentReportGenerator#execute(java.io.Writer, org.arachna.netweaver.dc.types.DevelopmentComponent, java.util.Map, java.io.Reader)}
     * .
     *
     * @throws ParseException
     */
    @Test
    public final void testExecuteReportForWebApplication() throws ParseException {
        final DevelopmentComponent component =
            dcFactory.create("example.com", "rest/services/impl", DevelopmentComponentType.J2EEWebModule);
        final WebApplicationDocumentationFacetProvider webApplicationDocumentationFacetProvider =
            Mockito.mock(WebApplicationDocumentationFacetProvider.class);
        final DocumentationFacetProviderFactory documentationFacetProviderFactory = Mockito.mock(DocumentationFacetProviderFactory.class);
        Mockito.when(documentationFacetProviderFactory.getInstance(DevelopmentComponentType.J2EEWebModule)).thenReturn(
            new ArrayList<DocumentationFacetProvider<DevelopmentComponent>>() {
                {
                    add(webApplicationDocumentationFacetProvider);
                }
            });

        final WebApplication webApplication = getWebApplication();
        final CompilationUnit unit = JavaParser.parse(getJavaRestResource());
        final RestService restService = new RestService();
        unit.accept(new RestServiceVisitor(), restService);
        webApplication.setRestServices(Arrays.asList(restService));

        Mockito.when(webApplicationDocumentationFacetProvider.execute(component)).thenReturn(
            new DocumentationFacet("WebApplication", webApplication));

        generator =
            new DevelopmentComponentReportGenerator(documentationFacetProviderFactory, dcFactory, velocityEngine, ResourceBundle.getBundle(
                DocumentationBuilder.DC_REPORT_BUNDLE, Locale.GERMAN), component);

        System.err.println(createDocument());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createDocument() {
        final StringWriter writer = new StringWriter();

        final Map<String, Object> context = new HashMap<String, Object>();
        context.put(ContextPropertyName.ProjectUrl.getName(), "projectUrl");
        context.put(ContextPropertyName.WikiSpace.getName(), "NWENV");

        generator.execute(writer, context, DocBookVelocityTemplate.DevelopmentComponent.getTemplate());

        return writer.toString();
    }

    /**
     * Read an example {@link WebApplication} instance from a <code>web.xml</code> descriptor.
     *
     * @return an example {@link WebApplication} instancefor test execution.
     */
    private WebApplication getWebApplication() {
        final InputStream resource = getClass().getResourceAsStream("/org/arachna/netweaver/nwdi/documenter/facets/webapp/web.xml");

        if (resource == null) {
            throw new IllegalStateException("web.xml for test not found!");
        }

        final WebApplication application =
            new DigesterHelper<WebApplication>(new WebXmlRulesModuleProducer()).execute(new InputStreamReader(resource));
        return new DigesterHelper<WebApplication>(new WebJ2eeRulesModuleProducer()).update(getWebJ2eeXmlReader(), application);
    }

    /**
     * @param component
     * @return
     * @throws FileNotFoundException
     */
    private Reader getWebJ2eeXmlReader() {
        return new InputStreamReader(getClass().getResourceAsStream(
            "/org/arachna/netweaver/nwdi/documenter/facets/webapp/web-j2ee-engine.xml"));
    }

    private InputStream getJavaRestResource() {
        return getClass().getResourceAsStream("/org/arachna/netweaver/nwdi/documenter/facets/restservices/ExampleRestService.java");
    }
}
