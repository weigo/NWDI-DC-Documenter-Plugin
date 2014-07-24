/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webservices;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.LinkedRuleBuilder;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.xml.DigesterHelper;
import org.arachna.xml.RulesModuleProducer;
import org.xml.sax.SAXException;

/**
 * Reader for virtual interface definitions of web services.
 * 
 * @author Dirk Weigenand
 */
public class VirtualInterfaceDefinitionReader implements RulesModuleProducer {
    /**
     * Reads the definition of a virtual interface of a web service from the given reader and returns a descriptor representing the
     * interface.
     * 
     * @param reader
     *            reader to read interface definition from
     * @return descriptor of the read interface definition
     * @throws SAXException
     * @throws IOException
     */
    public VirtualInterfaceDefinition read(final Reader reader) throws IOException, SAXException {
        return new DigesterHelper<VirtualInterfaceDefinition>(this).execute(reader);
    }

    /**
     * Producer for parsing rules for interface definitions (NW 7.0 web services).
     * 
     * @author Dirk Weigenand
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                setUpVirtualInterfaceHandling();
                setUpFunctionHandling();
                setUpIncomingParameterHandling();
                setUpIncomingParameterSimpleTypeHandling();
                setUpIncomingParameterTableTypeHandling();
                setUpIncomingParameterComplexTypeHandling();
                setUpResponseHandling();
                setUpResponseParameterHandling();
                setUpResponseParameterSimpleTypeHandling();
                setUpResponseParameterTableTypeHandling();
                setUpResponseParameterComplexTypeHandling();
            }

            private LinkedRuleBuilder createObjectSetPropertiesAndAddToParent(final String pattern, final Class<?> type,
                final String methodName) {
                return forPattern(pattern).createObject().ofType(type).then().setNext(methodName).then().setProperties().then();
            }

            private LinkedRuleBuilder callMethod(final LinkedRuleBuilder builder, final String methodName, final String attributeName) {
                return builder.callMethod(methodName).withParamTypes(String.class).then().callParam().ofIndex(0)
                    .fromAttribute(attributeName).then();
            }

            /**
             * @param digester
             */
            private void setUpResponseParameterTableTypeHandling() {
                createObjectSetPropertiesAndAddToParent(
                    "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTableReference",
                    Type.class, "setType");
            }

            /**
             * @param digester
             */
            private void setUpResponseParameterSimpleTypeHandling() {
                createObjectSetPropertiesAndAddToParent(
                    "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTypeReference",
                    Type.class, "setType");
            }

            /**
             * @param digester
             */
            private void setUpResponseParameterComplexTypeHandling() {
                createObjectSetPropertiesAndAddToParent(
                    "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ComplexTypeReference",
                    Type.class, "setType");
            }

            /**
             * @param digester
             */
            private void setUpResponseParameterHandling() {
                final LinkedRuleBuilder builder =
                    createObjectSetPropertiesAndAddToParent(
                        "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter", Parameter.class,
                        "setParameter");
                callMethod(builder, "setMappedName", "nameMappedTo");
            }

            /**
             * @param digester
             */
            private void setUpResponseHandling() {
                createObjectSetPropertiesAndAddToParent("VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters",
                    Response.class, "setResponse");
            }

            /**
             * @param digester
             */
            private void setUpIncomingParameterSimpleTypeHandling() {
                createObjectSetPropertiesAndAddToParent(
                    "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTypeReference",
                    Type.class, "setType");
            }

            /**
             * @param digester
             */
            private void setUpIncomingParameterTableTypeHandling() {
                createObjectSetPropertiesAndAddToParent(
                    "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTableReference",
                    Type.class, "setType");
            }

            /**
             * @param digester
             */
            private void setUpIncomingParameterComplexTypeHandling() {
                createObjectSetPropertiesAndAddToParent(
                    "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ComplexTypeReference",
                    Type.class, "setType");
            }

            /**
             * @param digester
             */
            private void setUpIncomingParameterHandling() {
                final LinkedRuleBuilder builder =
                    createObjectSetPropertiesAndAddToParent(
                        "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter", Parameter.class,
                        "addParameter");
                callMethod(builder, "setMappedName", "nameMappedTo");
            }

            /**
             * @param digester
             */
            private void setUpFunctionHandling() {
                final LinkedRuleBuilder builder =
                    createObjectSetPropertiesAndAddToParent("VirtualInterface/VirtualInterface.Functions/Function", Function.class,
                        "addMethod");
                callMethod(builder, "setMappedName", "nameMappedTo");
            }

            /**
             * @param digester
             */
            private void setUpVirtualInterfaceHandling() {
                forPattern("VirtualInterface").createObject().ofType(VirtualInterfaceDefinition.class).then().callMethod("setName")
                    .withParamTypes(String.class).then().callParam().ofIndex(0).fromAttribute("name");

                forPattern("VirtualInterface/VirtualInterface.EndpointReference/ClassEndpointReference").callMethod("setEndPointClass")
                    .withParamTypes(String.class).then().callParam().ofIndex(0).fromAttribute("qualifiedClassName");
            }
        };
    }
}
