/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webdynpro;

import java.util.Collection;
import java.util.LinkedList;

/**
 * A representation for a Web Dynpro development component. A Web Dynpro DC
 * consists of possibly several applications and Web Dynpro components.
 * 
 * @author Dirk Weigenand
 */
public final class WebDynpro {
    /**
     * collection of web dynpro applications.
     */
    private final Collection<WebDynproApplication> applications = new LinkedList<WebDynproApplication>();

    /**
     * collection of Web Dynpro components.
     */
    private final Collection<WebDynproComponent> components = new LinkedList<WebDynproComponent>();

    /**
     * @return the applications
     */
    public Collection<WebDynproApplication> getApplications() {
        return applications;
    }

    /**
     * Add a new Web Dynpro application to this Web Dynpro.
     * 
     * @param application
     *            Web Dynpro application to add
     */
    public void addApplication(final WebDynproApplication application) {
        applications.add(application);
    }

    /**
     * @return the components
     */
    public Collection<WebDynproComponent> getComponents() {
        return components;
    }

    /**
     * Add a new Web Dynpro application to this Web Dynpro.
     * 
     * @param component
     *            Web Dynpro component to add
     */
    public void addComponent(final WebDynproComponent component) {
        components.add(component);
    }
}
