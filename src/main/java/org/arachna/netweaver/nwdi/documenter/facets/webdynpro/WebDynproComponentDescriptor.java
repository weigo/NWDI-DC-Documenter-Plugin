/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.util.Collection;
import java.util.HashSet;

/**
 * Descriptor for reading web dynpro descriptions.
 * 
 * @author Dirk Weigenand
 */
final class WebDynproComponentDescriptor {
    /**
     * References to controllers.
     */
    private final Collection<CoreReference> controllers = new HashSet<CoreReference>();

    private CoreReference componentController;

    private CoreReference localInterface;

    private CoreReference messagePool;

    /**
     * References to views.
     */
    private final Collection<CoreReference> views = new HashSet<CoreReference>();

    /**
     * References to windows.
     */
    private final Collection<CoreReference> windows = new HashSet<CoreReference>();

    /**
     * @return the componentController
     */
    public CoreReference getComponentController() {
        return componentController;
    }

    /**
     * @param componentController
     *            the componentController to set
     */
    public void setComponentController(final CoreReference componentController) {
        this.componentController = componentController;
    }

    /**
     * @return the localInterface
     */
    public CoreReference getLocalInterface() {
        return localInterface;
    }

    /**
     * @param localInterface
     *            the localInterface to set
     */
    public void setLocalInterface(final CoreReference localInterface) {
        this.localInterface = localInterface;
    }

    /**
     * @return the messagePool
     */
    public CoreReference getMessagePool() {
        return messagePool;
    }

    /**
     * @param messagePool
     *            the messagePool to set
     */
    public void setMessagePool(final CoreReference messagePool) {
        this.messagePool = messagePool;
    }

    public void addView(final CoreReference view) {
        views.add(view);
    }

    public void addWindow(final CoreReference window) {
        windows.add(window);
    }

    public void addController(final CoreReference controller) {
        controllers.add(controller);
    }

    /**
     * @return the controllers
     */
    public Collection<CoreReference> getControllers() {
        return controllers;
    }

    /**
     * @return the views
     */
    public Collection<CoreReference> getViews() {
        return views;
    }

    /**
     * @return the windows
     */
    public Collection<CoreReference> getWindows() {
        return windows;
    }

}
