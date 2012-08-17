/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Dirk Weigenand
 * 
 */
enum ReferenceType {
    /**
     * reference type for a web dynpro controller descriptor.
     */
    Controller("Controller", ".wdcontroller"),
    /**
     * reference type for a web dynpro local component interface descriptor.
     */
    LocalComponentInterface("LocalComponentInterface", ".wdinterfaceview"),
    /**
     * reference type for a web dynpro message pool descriptor.
     */
    MessagePool("MessagePool", ".wdmessagepool"),
    /**
     * reference type for a web dynpro view descriptor.
     */
    View("View", ".wdview"),
    /**
     * reference type for a web dynpro window descriptor.
     */
    Window("Window", ".wdwindow");

    /**
     * mapping of reference type names to <code>ReferenceType</code>.
     */
    private static Map<String, ReferenceType> REFERENCE_TYPES = new HashMap<String, ReferenceType>() {
        {
            put(Controller.getName(), Controller);
            put(LocalComponentInterface.getName(), LocalComponentInterface);
            put(MessagePool.getName(), MessagePool);
            put(View.getName(), View);
            put(Window.getName(), Window);
        }
    };

    /**
     * suffix for reference descriptor.
     */
    private final String suffix;

    /**
     * 
     */
    private final String name;

    /**
     * Create a reference type using the given suffix.
     * 
     * @param name
     *            name of reference type.
     * @param suffix
     *            suffix for reference descriptor.
     */
    ReferenceType(final String name, final String suffix) {
        this.name = name;
        this.suffix = suffix;
    }

    /**
     * @return the suffix
     */
    public String getSuffix() {
        return suffix;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Get the reference type by name.
     * 
     * @param name
     *            name of wanted reference type
     * @return the found <code>ReferenceType</code> or <code>null</code>.
     */
    public static ReferenceType fromString(final String name) {
        return REFERENCE_TYPES.get(name);
    }
}
