/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * Container for information on internationalization of text resources.
 * 
 * @author Dirk Weigenand
 */
public final class Xliff {
    /**
     * 
     */
    private final Map<String, XliffGroup> resourceTypes = new HashMap<String, XliffGroup>();

    /**
     * Add a group of resources to this container.
     * 
     * @param group
     *            a group of resource types.
     */
    public void addResourceType(final XliffGroup group) {
        resourceTypes.put(group.getResourceType(), group);
    }

    /**
     * Returns all registered resource types.
     * 
     * @return a collection of resource types registered with this XLIFF entity.
     */
    public Collection<XliffGroup> getResourceTypes() {
        return resourceTypes.values();
    }

    /**
     * Get a xliff group for the given resource type.
     * 
     * @param resourceType
     *            resource type for the requested xliff group (caption, header,
     *            etc.)
     * @return a xliff group for the requested resource type of
     *         <code>null</code>
     */
    public XliffGroup getResourceType(final String resourceType) {
        return resourceTypes.get(resourceType);
    }
}
