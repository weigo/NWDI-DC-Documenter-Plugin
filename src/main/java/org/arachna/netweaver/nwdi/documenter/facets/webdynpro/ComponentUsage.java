/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Usage of another WebDynpro component.
 * 
 * @author Dirk Weigenand
 */
public final class ComponentUsage extends AbstractCoreReferenceType {
    /**
     * the name of this WebDynpro component usage.
     */
    private String name;

    /**
     * Type of life cycle control.
     */
    private WDLifeCycleControl lifeCycleControl;

    /**
     * record used component controllers.
     */
    private final Collection<ComponentControllerUsage> controllerUsages = new LinkedList<ComponentControllerUsage>();

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
     * @return the lifeCycleControl
     */
    public WDLifeCycleControl getLifeCycleControl() {
        return lifeCycleControl;
    }

    /**
     * @param lifeCycleControl
     *            the lifeCycleControl to set
     */
    public void setLifeCycleControl(final WDLifeCycleControl lifeCycleControl) {
        this.lifeCycleControl = lifeCycleControl;
    }

    /**
     * Add a component controller to the list of used components.
     * 
     * @param usage
     *            used component controller.
     */
    public void add(final ComponentControllerUsage usage) {
        controllerUsages.add(usage);
    }

    /**
     * Return the list of used component controllers.
     * 
     * @return list of used component controllers.
     */
    public Collection<ComponentControllerUsage> getUsedControllers() {
        return Collections.unmodifiableCollection(controllerUsages);
    }
}
