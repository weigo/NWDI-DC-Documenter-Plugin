/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.restservices;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInAnyOrder.containsInAnyOrder;
import japa.parser.JavaParser;
import japa.parser.ast.CompilationUnit;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 * 
 */
public class RestServiceVisitorTest {
    private CompilationUnit unit;
    private RestService restService;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        unit = JavaParser.parse(getResource());
        restService = new RestService();
        unit.accept(new RestServiceVisitor(), restService);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.restservices.RestServiceVisitor#visit(japa.parser.ast.body.ClassOrInterfaceDeclaration, org.arachna.netweaver.nwdi.documenter.facets.restservices.RestService)}
     * .
     */
    @Test
    public void testVisitClassOrInterfaceDeclarationRestService() {
        assertThat(restService.getBasePath(), equalTo("/rest"));
    }

    @Test
    public void testVisitClassOrInterfaceDeclarationRestServiceWithJavaDoc() {
        assertThat(restService.getDescription(), equalTo("This is an example REST service to testing purposes."));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.restservices.RestServiceVisitor#visit(japa.parser.ast.body.MethodDeclaration, org.arachna.netweaver.nwdi.documenter.facets.restservices.RestService)}
     * .
     */
    @Test
    public void testVisitMethodDeclarationRestService() {
        assertThat(restService.getMethods(), hasSize(2));
    }

    @Test
    public void testVisitMethodDeclarationRestServiceParamDeclarations() {
        final List<Method> methods = new ArrayList<Method>(restService.getMethods());
        assertThat(methods, hasSize(2));

        final Method method = methods.get(0);
        final List<Parameter> parameters = method.getParameters();

        assertThat(parameters, hasSize(3));
        assertParameterProperties(parameters.get(0), "client", ParameterType.PathParam, "the example 'client' parameter.");
    }

    @Test
    public void testVisitMethodDeclarationRestServiceParamDeclarationsWithoutJavaDoc() {
        final List<Method> methods = new ArrayList<Method>(restService.getMethods());
        assertThat(methods, hasSize(2));

        final Method method = methods.get(0);
        final List<Parameter> parameters = method.getParameters();

        assertThat(parameters, hasSize(3));
        assertParameterProperties(parameters.get(2), "hashCode", ParameterType.QueryParam, "");
    }

    private void assertParameterProperties(final Parameter parameter, final String parameterName, final ParameterType type,
        final String description) {
        assertThat(parameter.getName(), equalTo(parameterName));
        assertThat(parameter.getType(), equalTo(type));
        assertThat(parameter.getDescription(), equalTo(description));
    }

    @Test
    public void testVisitMethodDeclarationRestServicePathDeclaration() {
        final List<Method> methods = new ArrayList<Method>(restService.getMethods());
        assertThat(methods, hasSize(2));
        final Method method = methods.get(0);

        assertThat(method.getPath(), equalTo("/{client}/{process}"));
    }

    @Test
    public void testVisitMethodDeclarationRestServiceConsumesDeclarations() {
        final List<Method> methods = new ArrayList<Method>(restService.getMethods());
        assertThat(methods, hasSize(2));

        final Method method = methods.get(0);
        assertThat(method.getConsumedMediaTypes(), containsInAnyOrder("MediaType.APPLICATION_JSON"));
    }

    @Test
    public void testVisitMethodDeclarationRestServiceSingleValuedProducesDeclaration() {
        final List<Method> methods = new ArrayList<Method>(restService.getMethods());
        assertThat(methods, hasSize(2));

        final Method method = methods.get(0);
        assertThat(method.getProducedMediaTypes(), containsInAnyOrder("MediaType.APPLICATION_JSON"));
    }

    private InputStream getResource() {
        return getClass().getResourceAsStream("/org/arachna/netweaver/nwdi/documenter/facets/restservices/ExampleRestService.java");
    }
}
