/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Definition of a web services virtual interface.
 * 
 * @author Dirk Weigenand
 */
public class VirtualInterfaceDefinition {
    /**
     * name of this virtual interface.
     */
    private String name;

    /**
     * fully qualified class name of endpoint class.
     */
    private String endPointClass;

    /**
     * Collection of web service methods.
     */
    private List<Function> methods = new ArrayList<Function>();

    /**
     * Return the name of the virtual interface.
     * 
     * @return the name of the virtual interface.
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of the virtual interface.
     * 
     * @param name
     *            the name of the virtual interface.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Return the name of the class implementing the endpoint.
     * 
     * @return the name of the class implementing the endpoint.
     */
    public String getEndPointClass() {
        return endPointClass;
    }

    /**
     * Set the name of the class implementing the endpoint.
     * 
     * @param endPointClass
     *            name of the class implementing the endpoint.
     */
    public void setEndPointClass(String endPointClass) {
        this.endPointClass = endPointClass;
    }

    /**
     * Return the methods of this virtual interface.
     * 
     * @return the methods of this virtual interface.
     */
    public Collection<Function> getMethods() {
        return methods;
    }

    public void addMethod(Function function) {
        this.methods.add(function);
    }
}
