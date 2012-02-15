/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

/**
 * Parameter of a web service method.
 * 
 * @author Dirk Weigenand
 */
public class Parameter {
    /**
     * name of this parameter.
     */
    private String name;

    /**
     * name this parameter was mapped on.
     */
    private String mappedName;

    /**
     * the type of this parameter.
     */
    private Type type;
}
