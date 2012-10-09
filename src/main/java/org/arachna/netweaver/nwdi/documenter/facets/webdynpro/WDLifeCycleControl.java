/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Types of WD life cycle controls used by WD components.
 * 
 * @author Dirk Weigenand
 */
public enum WDLifeCycleControl {
    /**
     * A WD component usage of this type is created on demand. The lifecycle is
     * controlled by the WebDynpro framework.
     */
    CreateOnDemand("createOnDemand");

    /**
     * Mapping of names of life cycle control types to actual instances.
     */
    private static final Map<String, WDLifeCycleControl> VALUES = new LinkedHashMap<String, WDLifeCycleControl>();

    // fill mapping to be used in fromString().
    static {
        for (final WDLifeCycleControl type : values()) {
            VALUES.put(type.name, type);
        }
    }

    /**
     * name of life cycle control type.
     */
    private final String name;

    /**
     * Create a new life cycle control type using the given name.
     * 
     * @param name
     *            name of life cycle control type.
     */
    private WDLifeCycleControl(final String name) {
        this.name = name;
    }

    /**
     * Get the name of this life cycle control type.
     * 
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Factory method for creating types of WD component life cycle controls.
     * 
     * Throws {@link } when the given name could not be matched to a life cycle
     * control type.
     * 
     * @param name
     *            name of requested control, i.e. 'createOnDemand'.
     * @return the requested <code>WDLifeCycleControl</code>.
     */
    public static final WDLifeCycleControl fromString(final String name) {
        final WDLifeCycleControl type = VALUES.get(name);

        if (type == null) {
            throw new IllegalStateException(String.format(
                "Unknown life cycle control type '%s' for usgae of WebDynpro components!", name));
        }

        return type;
    }
}
