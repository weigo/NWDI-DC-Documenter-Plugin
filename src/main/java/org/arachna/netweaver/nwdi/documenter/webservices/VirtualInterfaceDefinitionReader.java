/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.Digester;
import org.xml.sax.SAXException;

/**
 * Reader for virtual interface definitions of web services.
 * 
 * @author Dirk Weigenand
 */
public class VirtualInterfaceDefinitionReader {
    private final Digester digester = new Digester();

    public VirtualInterfaceDefinitionReader() {
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

    /**
     * Reads the definition of a virtual interface of a web service from the
     * given reader and returns a descriptor representing the interface.
     * 
     * @param reader
     *            reader to read interface definition from
     * @return descriptor of the read interface definition
     * @throws SAXException
     * @throws IOException
     */
    public VirtualInterfaceDefinition read(final Reader reader) throws IOException, SAXException {
        return (VirtualInterfaceDefinition)digester.parse(reader);
    }

    /**
     * @param digester
     */
    private void setUpResponseParameterTableTypeHandling() {
        digester
            .addObjectCreate(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTableReference",
                Type.class);
        digester
            .addSetNext(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTableReference",
                "setType");
        digester
            .addSetProperties("VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTableReference");
    }

    /**
     * @param digester
     */
    private void setUpResponseParameterSimpleTypeHandling() {
        digester
            .addObjectCreate(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTypeReference",
                Type.class);
        digester
            .addSetNext(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTypeReference",
                "setType");
        digester
            .addSetProperties("VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTypeReference");
    }

    /**
     * @param digester
     */
    private void setUpResponseParameterComplexTypeHandling() {
        digester
            .addObjectCreate(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ComplexTypeReference",
                Type.class);
        digester
            .addSetNext(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ComplexTypeReference",
                "setType");
        digester
            .addSetProperties("VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter/Parameter.MappedTypeReference/ComplexTypeReference");
    }

    /**
     * @param digester
     */
    private void setUpResponseParameterHandling() {
        digester.addObjectCreate(
            "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter",
            Parameter.class);
        digester.addSetNext(
            "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter",
            "setParameter");
        digester
            .addSetProperties("VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter");

        digester.addCallMethod(
            "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter",
            "setMappedName", 1);
        digester.addCallParam(
            "VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters/Parameter", 0,
            "nameMappedTo");
    }

    /**
     * @param digester
     */
    private void setUpResponseHandling() {
        digester.addObjectCreate("VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters",
            Response.class);
        digester.addSetNext("VirtualInterface/VirtualInterface.Functions/Function/Function.OutgoingParameters",
            "setResponse");
    }

    /**
     * @param digester
     */
    private void setUpIncomingParameterSimpleTypeHandling() {
        digester
            .addObjectCreate(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTypeReference",
                Type.class);
        digester
            .addSetNext(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTypeReference",
                "setType");
        digester
            .addSetProperties("VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTypeReference");
    }

    /**
     * @param digester
     */
    private void setUpIncomingParameterTableTypeHandling() {
        digester
            .addObjectCreate(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTableReference",
                Type.class);
        digester
            .addSetNext(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTableReference",
                "setType");
        digester
            .addSetProperties("VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ConvertedTableReference");
    }

    /**
     * @param digester
     */
    private void setUpIncomingParameterComplexTypeHandling() {
        digester
            .addObjectCreate(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ComplexTypeReference",
                Type.class);
        digester
            .addSetNext(
                "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ComplexTypeReference",
                "setType");
        digester
            .addSetProperties("VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter/Parameter.MappedTypeReference/ComplexTypeReference");
    }

    /**
     * @param digester
     */
    private void setUpIncomingParameterHandling() {
        digester.addObjectCreate(
            "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter",
            Parameter.class);
        digester.addSetNext(
            "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter",
            "addParameter");
        digester
            .addSetProperties("VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter");

        digester.addCallMethod(
            "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter",
            "setMappedName", 1);
        digester.addCallParam(
            "VirtualInterface/VirtualInterface.Functions/Function/Function.IncomingParameters/Parameter", 0,
            "nameMappedTo");
    }

    /**
     * @param digester
     */
    private void setUpFunctionHandling() {
        digester.addObjectCreate("VirtualInterface/VirtualInterface.Functions/Function", Function.class);
        digester.addSetNext("VirtualInterface/VirtualInterface.Functions/Function", "addMethod");
        digester.addSetProperties("VirtualInterface/VirtualInterface.Functions/Function");

        digester.addCallMethod("VirtualInterface/VirtualInterface.Functions/Function", "setMappedName", 1);
        digester.addCallParam("VirtualInterface/VirtualInterface.Functions/Function", 0, "nameMappedTo");
    }

    /**
     * @param digester
     */
    private void setUpVirtualInterfaceHandling() {
        digester.addObjectCreate("VirtualInterface", VirtualInterfaceDefinition.class);
        digester.addCallMethod("VirtualInterface", "setName", 1);
        digester.addCallParam("VirtualInterface", 0, "name");

        digester.addCallMethod("VirtualInterface/VirtualInterface.EndpointReference/ClassEndpointReference",
            "setEndPointClass", 1);
        digester.addCallParam("VirtualInterface/VirtualInterface.EndpointReference/ClassEndpointReference", 0,
            "qualifiedClassName");
    }
}
