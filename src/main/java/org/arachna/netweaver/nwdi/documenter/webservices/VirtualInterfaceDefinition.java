/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import java.util.Collection;
import java.util.HashSet;

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
    private Collection<Function> methods = new HashSet<Function>();
}
