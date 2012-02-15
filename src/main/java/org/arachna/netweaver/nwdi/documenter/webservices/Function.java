/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import java.util.ArrayList;
import java.util.Collection;

/**
 * A web service function descriptor.
 * 
 * @author Dirk Weigenand
 */
public class Function {
    /**
     * exposed java method signature.
     */
    private String name;

    /**
     * name the original name was mapped to.
     */
    private String mappedName;
    
    /**
     * original method name.
     */
    private String originalName;
    
    /**
     * collection of parameters.
     */
    private Collection<Parameter> parameters = new ArrayList<Parameter>();
}
