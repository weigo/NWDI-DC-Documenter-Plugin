/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

import java.util.HashMap;
import java.util.Map;

/**
 * Types of plugs in WebDynpros.
 * 
 * @author Dirk Weigenand
 */
public enum PlugType {
    /**
     * An in-bound plug.
     */
    InBound(""),
    /**
     * An out-bound plug.
     */
    OutBound("");

    /**
     * map names of concrete plug types to representing instances.
     */
    private static final Map<String, PlugType> TYPES = new HashMap<String, PlugType>();

    static {
        TYPES.put(InBound.getName(), InBound);
        TYPES.put(OutBound.getName(), OutBound);
    }

    /**
     * name of concrete plug type.
     */
    private String name;

    /**
     * Create a new plug type with its corresponding name.
     * 
     * @param name
     *            name of plug type.
     */
    private PlugType(final String name) {
        this.name = name;
    }

    /**
     * Get the name of this concrete plug type.
     * 
     * @return the name of this concrete plug type.
     */
    public String getName() {
        return name;
    }

    /**
     * Get an instance of a PlugType from the given name.
     * 
     * Throws an IllegalArgumentException if no matching plug type could be
     * found.
     * 
     * @param name
     *            name of the requested plug type.
     * @return the requested plug type
     */
    public static final PlugType fromString(final String name) {
        final PlugType type = TYPES.get(name);

        if (type == null) {
            throw new IllegalArgumentException(String.format("No such PlugType for '%s'!", name));
        }

        return type;
    }
}
