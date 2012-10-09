/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * A Web Dynpro Component.
 * 
 * @author Dirk Weigenand
 */
public final class WebDynproComponent {
    /**
     * WebDynpro Component name.
     */
    private String name;

    /**
     * Package where component is located.
     */
    private String packageName;

    /**
     * External interface of this WebDynpro component.
     */
    private ComponentInterface componentInterface;

    /**
     * Mapping of names to used WebDynpro components.
     */
    private final Map<String, ComponentUsage> componentUsages = new LinkedHashMap<String, ComponentUsage>();

    /**
     * List of handles to controllers/view/windows in this WD component.
     */
    private final Map<ReferenceType, Collection<CoreReference>> references =
        new LinkedHashMap<ReferenceType, Collection<CoreReference>>();

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     *            the name to set
     */
    public void setName(final String name) {
        this.name = name;
    }

    /**
     * @return the packageName
     */
    public String getPackageName() {
        return packageName;
    }

    /**
     * @param packageName
     *            the packageName to set
     */
    public void setPackageName(final String packageName) {
        this.packageName = packageName;
    }

    /**
     * @return the componentInterface
     */
    public ComponentInterface getComponentInterface() {
        return componentInterface;
    }

    /**
     * @param componentInterface
     *            the componentInterface to set
     */
    public void setComponentInterface(final ComponentInterface componentInterface) {
        this.componentInterface = componentInterface;
    }

    /**
     * Add a new WebDynpro component usage.
     * 
     * @param usage
     *            the WebDynpro component used.
     */
    public void add(final ComponentUsage usage) {
        componentUsages.put(usage.getName(), usage);
    }

    /**
     * Return usages of other WD components.
     * 
     * @return Usages of other WD components.
     */
    public Collection<ComponentUsage> getUsedComponents() {
        return componentUsages.values();
    }

    /**
     * Add handle to a controller in this WD component.
     * 
     * @param reference
     *            handle to controller.
     */
    public void add(final CoreReference reference) {
        getTypedReferences(reference.getType()).add(reference);
    }

    /**
     * Returns a list of the core references of the requested type.
     * 
     * @param referenceType
     *            type of requested core reference type.
     * @return list of the core references of the requested type.
     */
    protected Collection<CoreReference> getTypedReferences(final ReferenceType referenceType) {
        Collection<CoreReference> typedReferences = references.get(referenceType);

        if (typedReferences == null) {
            typedReferences = new LinkedList<CoreReference>();
            references.put(referenceType, typedReferences);
        }

        return typedReferences;
    }

    /**
     * Get the list of controllers in this WD component.
     * 
     * @return list of controllers in this WD component.
     */
    public Collection<CoreReference> getControllers() {
        return getTypedReferences(ReferenceType.Controller);
    }

    /**
     * Get the list of controllers in this WD component.
     * 
     * @return list of controllers in this WD component.
     */
    public Collection<CoreReference> getViews() {
        return getTypedReferences(ReferenceType.View);
    }

    /**
     * Get the list of controllers in this WD component.
     * 
     * @return list of controllers in this WD component.
     */
    public Collection<CoreReference> getWindows() {
        return getTypedReferences(ReferenceType.Window);
    }
}
