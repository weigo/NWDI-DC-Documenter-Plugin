/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Collection;
import java.util.Iterator;

import org.arachna.netweaver.nwdi.documenter.facets.webservices.Function;
import org.arachna.netweaver.nwdi.documenter.facets.webservices.Parameter;
import org.arachna.netweaver.nwdi.documenter.facets.webservices.Response;
import org.arachna.netweaver.nwdi.documenter.facets.webservices.Type;
import org.arachna.netweaver.nwdi.documenter.facets.webservices.VirtualInterfaceDefinition;
import org.arachna.netweaver.nwdi.documenter.facets.webservices.VirtualInterfaceDefinitionReader;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * JUnit tests for {@link VirtualInterfaceDefinitionReader}.
 * 
 * @author Dirk Weigenand
 */
public class VirtualInterfaceDefinitionReaderTest {
    /**
     * instance under test.
     */
    private VirtualInterfaceDefinitionReader reader;

    /**
     * interface definition instance to test against.
     */
    private VirtualInterfaceDefinition definition;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        reader = new VirtualInterfaceDefinitionReader();
        definition = reader.read(getReader());
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        reader = null;
    }

    @Test
    public final void testCreateVirtualInterface() throws IOException, SAXException {
        assertThat(definition, not(nullValue()));
    }

    @Test
    public void testReadingNameOfEndPointClass() {
        assertThat(definition.getEndPointClass(), equalTo("com.example.webservice.ExampleWebService"));
    }

    @Test
    public void testReadingNameOfVirtualInterface() {
        assertThat(definition.getName(), equalTo("ExampleWebServiceVi"));
    }

    @Test
    public void testCreateEmptyFunctionObjects() {
        assertThat(definition.getMethods().size(), equalTo(13));
    }

    @Test
    public void testSetFunctionPropertName() {
        Function function = getFirstFunction();
        assertThat(function.getName(),
            equalTo("exampleMethod1(java.lang.String,java.lang.String,java.lang.String,java.lang.String)"));
    }

    @Test
    public void testSetFunctionPropertyMappedName() {
        Function function = getFirstFunction();
        assertThat(function.getMappedName(), equalTo("exampleMethod1"));
    }

    @Test
    public void testSetFunctionPropertyOriginalName() {
        Function function = getFirstFunction();
        assertThat(function.getOriginalName(), equalTo("exampleMethod1"));
    }

    @Test
    public void testCreateFunctionParameters() {
        Function function = getFirstFunction();
        assertThat(function.getParameters().size(), equalTo(4));
    }

    @Test
    public void testSetFunctionParametersMappedNameProperty() {
        Function function = getFirstFunction();
        Iterator<Parameter> parameters = function.getParameters().iterator();
        Parameter parameter0 = parameters.next();
        assertThat(parameter0.getMappedName(), equalTo("surName"));
    }

    @Test
    public void testSetFunctionParametersNameProperty() {
        Function function = getFirstFunction();
        Iterator<Parameter> parameters = function.getParameters().iterator();
        Parameter parameter0 = parameters.next();
        assertThat(parameter0.getName(), equalTo("surName"));
    }

    @Test
    public void testSetFunctionParametersTypeProperty() {
        Function function = getFirstFunction();
        Iterator<Parameter> parameters = function.getParameters().iterator();
        Parameter parameter0 = parameters.next();
        assertThat(parameter0.getType(), not(nullValue()));
    }

    @Test
    public void testSetFunctionParametersTypeNameProperty() {
        Function function = getFirstFunction();
        Iterator<Parameter> parameters = function.getParameters().iterator();
        Parameter parameter0 = parameters.next();
        assertThat(parameter0.getType().getName(), equalTo("java.lang.String"));
    }

    @Test
    public void testSetFunctionParametersTypeOriginalTypeProperty() {
        Function function = getFirstFunction();
        Iterator<Parameter> parameters = function.getParameters().iterator();
        Parameter parameter0 = parameters.next();
        assertThat(parameter0.getType().getOriginalType(), equalTo("java.lang.String"));
    }

    @Test
    public void testSetUpResponseProperty() {
        Function function = getFirstFunction();
        assertThat(function.getResponse(), not(nullValue()));
    }

    @Test
    public void testSetUpResponseParameterNameProperty() {
        Function function = getFirstFunction();
        Response response = function.getResponse();
        Parameter result = response.getParameter();
        assertThat(result.getName(), equalTo("Response"));
    }

    @Test
    public void testSetUpResponseParameterMappedNameProperty() {
        Function function = getFirstFunction();
        Response response = function.getResponse();
        Parameter result = response.getParameter();
        assertThat(result.getMappedName(), equalTo("Response"));
    }

    @Test
    public void testSetUpResponseParameterTypeProperty() {
        Function function = getFirstFunction();
        Response response = function.getResponse();
        Type type = response.getParameter().getType();
        assertThat(type, not(nullValue()));
    }

    @Test
    public void testSetUpResponseParameterTypeNameProperty() {
        Function function = getFirstFunction();
        Response response = function.getResponse();
        Type type = response.getParameter().getType();
        assertThat(type.getName(), equalTo("boolean"));
    }

    @Test
    public void testSetUpResponseParameterTypeOriginalTypeProperty() {
        Function function = getFirstFunction();
        Response response = function.getResponse();
        Type type = response.getParameter().getType();
        assertThat(type.getOriginalType(), equalTo("boolean"));
    }

    @Test
    public void testIncomingTableParameter() {
        Function function = this.getSecondFunction();
        Parameter parameter = function.getParameters().iterator().next();

        assertThat(parameter.getName(), equalTo("businessPartnerIds"));
        assertThat(parameter.getMappedName(), equalTo("businessPartnerIds"));
        assertThat(parameter.getType(), not(nullValue()));
    }

    @Test
    public void testIncomingTableParameterTypeNameProperty() {
        Function function = this.getSecondFunction();
        Parameter parameter = function.getParameters().iterator().next();
        Type type = parameter.getType();

        assertThat(type.getName(), equalTo("java.lang.String[]"));
        assertThat(type.getOriginalType(), equalTo(""));
    }

    @Test
    public void testIncomingTableParameterTypeOriginalTypeProperty() {
        Function function = this.getSecondFunction();
        Parameter parameter = function.getParameters().iterator().next();
        Type type = parameter.getType();

        assertThat(type.getOriginalType(), equalTo(""));
    }

    @Test
    public void testComplexReturnTypeMappedNameProperty() {
        Function function = this.getSecondFunction();
        Parameter result = function.getResponse().getParameter();

        assertThat(result.getMappedName(), equalTo("accountManagers"));
    }

    @Test
    public void testComplexReturnTypeNameProperty() {
        Function function = this.getSecondFunction();
        Parameter result = function.getResponse().getParameter();

        assertThat(result.getName(), equalTo("Response"));
    }

    @Test
    public void testComplexReturnTypeProperty() {
        Function function = this.getSecondFunction();
        Parameter result = function.getResponse().getParameter();

        assertThat(result.getType(), not(nullValue()));
    }

    @Test
    public void testComplexReturnTypeTypeNameProperty() {
        Function function = this.getSecondFunction();
        Type type = function.getResponse().getParameter().getType();

        assertThat(type.getName(), equalTo("com.example.webservice.AccountManagerData[]"));
    }

    @Test
    public void testComplexReturnTypeTypeOriginalTypeProperty() {
        Function function = this.getSecondFunction();
        Type type = function.getResponse().getParameter().getType();

        assertThat(type.getOriginalType(), equalTo(""));
    }

    private Function getFirstFunction() {
        Collection<Function> methods = definition.getMethods();
        return methods.iterator().next();
    }

    private Function getSecondFunction() {
        Iterator<Function> methods = definition.getMethods().iterator();
        methods.next();

        return methods.next();
    }

    /**
     * @return
     */
    protected Reader getReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/nwdi/documenter/webservices/ExampleVirtualInterface.videf"));
    }
}
