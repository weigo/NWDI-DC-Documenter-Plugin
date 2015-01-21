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
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.nwdi.documenter.ConfluenceVersion;
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
import org.mockito.Mockito;

/**
 * Unittests for {@link DevelopmentComponentReportGenerator}.
 *
 * @author Dirk Weigenand
 */
public class DevelopmentComponentReportGeneratorTest extends AbstractXmlTestCase {
    /**
     * Instance under test.
     */
    private DevelopmentComponentReportGenerator generator;

    /**
     * Registry for development components.
     */
    private DevelopmentComponentFactory dcFactory;

    /**
     * Velocity engine used to render reports.
     */
    private VelocityEngine velocityEngine;

    private Templates template;

    private DocumentationFacetProviderFactory documentationFacetProviderFactory;

    private DocumentationFacetProvider<DevelopmentComponent> facetProvider;

    /**
     */
    @Override
    @Before
    public void setUp() {
        dcFactory = new DevelopmentComponentFactory();
        velocityEngine = new VelocityHelper().getVelocityEngine();
        template = ConfluenceVersion.V4.createTemplate();
        documentationFacetProviderFactory = Mockito.mock(DocumentationFacetProviderFactory.class);
    }

    /**
     * Test method for
     * {@link DevelopmentComponentReportGenerator#execute(java.io.Writer, org.arachna.netweaver.dc.types.DevelopmentComponent, java.util.Map, java.io.Reader)}
     * .
     *
     * @throws ParseException
     */
    public void testExecuteReportForWebApplication() throws ParseException {
        final DevelopmentComponent component =
            dcFactory.create("example.com", "rest/services/impl", DevelopmentComponentType.J2EEWebModule);
        mockFacetProvider(WebApplicationDocumentationFacetProvider.class, DevelopmentComponentType.J2EEWebModule);

        final WebApplication webApplication = getWebApplication();
        final CompilationUnit unit = JavaParser.parse(getJavaRestResource());
        final RestService restService = new RestService();
        unit.accept(new RestServiceVisitor(), restService);
        webApplication.setRestServices(Arrays.asList(restService));

        Mockito.when(facetProvider.execute(component)).thenReturn(new DocumentationFacet("WebApplication", webApplication));

        generator =
            new DevelopmentComponentReportGenerator(documentationFacetProviderFactory, dcFactory, velocityEngine, ResourceBundle.getBundle(
                DocumentationBuilder.DC_REPORT_BUNDLE, Locale.GERMAN), component);

        createDocument();
    }

    public void testExecuteReportForJavaDC() {
        final DevelopmentComponent component =
            dcFactory.create("example.com", "rest/services/impl", DevelopmentComponentType.J2EEWebModule);
        mockFacetProvider(WebApplicationDocumentationFacetProvider.class, DevelopmentComponentType.J2EEWebModule);

        final WebApplication webApplication = getWebApplication();

        Mockito.when(facetProvider.execute(component)).thenReturn(new DocumentationFacet("WebApplication", webApplication));

        generator =
            new DevelopmentComponentReportGenerator(documentationFacetProviderFactory, dcFactory, velocityEngine, ResourceBundle.getBundle(
                DocumentationBuilder.DC_REPORT_BUNDLE, Locale.GERMAN), component);

        createDocument();
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

        return transform(new StringReader(writer.toString()));
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

    private Transformer newTransformer() {
        Transformer transformer;

        try {
            transformer = template.newTransformer();
            transformer.setParameter("wikiSpace", "NWDOC");
            transformer.setParameter("track", "DI0_XMPL_D");
            transformer.setErrorListener(new ErrorListener() {

                @Override
                public void warning(final TransformerException exception) throws TransformerException {
                    fail(exception.getMessageAndLocation());
                }

                @Override
                public void error(final TransformerException exception) throws TransformerException {
                    fail(exception.getMessageAndLocation());
                }

                @Override
                public void fatalError(final TransformerException exception) throws TransformerException {
                    fail(exception.getMessageAndLocation());
                }
            });
        }
        catch (final TransformerConfigurationException e) {
            throw new IllegalStateException(e);
        }

        return transformer;
    }

    private String transform(final Reader source) {
        final StringWriter result = new StringWriter();

        try {
            newTransformer().transform(new StreamSource(source), new StreamResult(result));
        }
        catch (final TransformerException e) {
            throw new IllegalStateException(e);
        }

        return result.toString();
    }

    private void mockFacetProvider(final Class facetProviderClass, final DevelopmentComponentType dcType) {
        facetProvider = Mockito.mock(facetProviderClass);

        Mockito.when(documentationFacetProviderFactory.getInstance(dcType)).thenReturn(
            new ArrayList<DocumentationFacetProvider<DevelopmentComponent>>() {
                {
                    add(facetProvider);
                }
            });
    }
}
