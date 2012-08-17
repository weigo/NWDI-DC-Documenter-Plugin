/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Container for WebDynpro application properties.
 * 
 * @author Dirk Weigenand
 */
public final class ApplicationProperties implements Iterable<ApplicationProperty> {
    /**
     * store for application properties.
     */
    private final Map<String, ApplicationProperty> properties = new HashMap<String, ApplicationProperty>();

    /**
     * Add an application property.
     * 
     * @param name
     *            name of property
     * @param value
     *            value of property
     */
    public void addProperty(final String name, final String value) {
        properties.put(name, new ApplicationProperty(name, value));
    }

    /**
     * Get the value of the requested application property.
     * 
     * @param name
     *            name of requested application property.
     * @return the value of the requested application property
     */
    public String get(final String name) {
        final ApplicationProperty property = properties.get(name);

        if (property == null) {
            throw new IllegalArgumentException(String.format("Invalid application property '%s' requested!", name));
        }

        return property.getValue();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Iterator<ApplicationProperty> iterator() {
        return properties.values().iterator();
    }

    /**
     * Returns the count of registered application properties.
     * 
     * @return count of registered application properties.
     */
    public int size() {
        return properties.size();
    }
}
