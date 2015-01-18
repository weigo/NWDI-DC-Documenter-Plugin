/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import org.arachna.netweaver.nwdi.documenter.facets.webapp.restservices.RestService;

/**
 * Representation of a web application as described by the <code>web.xml</code> descriptor.
 *
 * @author Dirk Weigenand
 */
public class WebApplication {
    /**
     * Collection of servlet descriptors in this web application.
     */
    private final Collection<ServletDescriptor> servlets = new HashSet<ServletDescriptor>();

    /**
     * Collection of security role names defined in this web application.
     */
    private final Collection<String> securityRoles = new HashSet<String>();

    /**
     * Collection of security constraints defined in this web application.
     */
    private final Collection<SecurityConstraintDescriptor> securityConstraints = new LinkedList<SecurityConstraintDescriptor>();

    /**
     * REST services contained in this web application.
     */
    private final List<RestService> restServices = new LinkedList<RestService>();

    /**
     * mapping of security roles internal to the web application to security roles on the server.
     */
    private final List<SecurityRoleMapping> securityRoleMappings = new LinkedList<SecurityRoleMapping>();

    /**
     * Add a new servlet descriptor to this web application.
     *
     * @param descriptor
     *            servlet descriptor to be added.
     */
    public void add(final ServletDescriptor descriptor) {
        servlets.add(descriptor);
    }

    /**
     * Get the collection of servlet descriptors defined in this web application.
     *
     * @return collection of servlet descriptors defined in this web application, may be empty but never <code>null</code>.
     */
    public Collection<ServletDescriptor> getServletDescriptors() {
        return Collections.unmodifiableCollection(servlets);
    }

    /**
     * Add an url to servlet mapping to this web application.
     *
     * @param servletName
     *            name of servlet to map to url pattern.
     * @param urlPattern
     *            url pattern to map servlet to.
     */
    public void addServletMapping(final String servletName, final String urlPattern) {
        for (final ServletDescriptor descriptor : servlets) {
            if (descriptor.getName().equals(servletName)) {
                descriptor.setServletMapping(urlPattern);
                break;
            }
        }
    }

    /**
     * Gets the security roles defined in this web application.
     *
     * @return collection of security roles defined in this web application. May be empty but never <code>null</code>.
     */
    public Collection<String> getSecurityRoles() {
        return Collections.unmodifiableCollection(securityRoles);
    }

    /**
     * Adds a security role name to this web application.
     *
     * @param roleName
     *            the name of the security role to add.
     */
    public void addSecurityRole(final String roleName) {
        securityRoles.add(roleName);
    }

    /**
     * Get the security constraints defined in this web application.
     *
     * @return collection of security constraints defined in this web application. May be empty but never <code>null</code>.
     */
    public Collection<SecurityConstraintDescriptor> getSecurityConstraints() {
        return Collections.unmodifiableCollection(securityConstraints);
    }

    /**
     * Add the given security constraint to this web application.
     *
     * @param descriptor
     *            security constraint to add to this web application.
     */
    public void add(final SecurityConstraintDescriptor descriptor) {
        securityConstraints.add(descriptor);
    }

    /**
     * Set REST services defined in this web application.
     *
     * @param restServices
     *            REST services to add to this web application.
     */
    public void setRestServices(final List<RestService> restServices) {
        this.restServices.clear();

        if (restServices != null) {
            this.restServices.addAll(restServices);
        }
    }

    /**
     * Get REST services defined in this web application if any.
     *
     * @return REST services defined in this web application. May be empty, never <code>null</code>.
     */
    public Collection<RestService> getRestServices() {
        return Collections.unmodifiableCollection(restServices);
    }

    /**
     * Get base url of REST service.
     *
     * @return
     */
    public String getRestServiceBaseUrl() {
        final StringBuilder baseUrl = new StringBuilder();

        for (final ServletDescriptor descriptor : servlets) {
            if (ServletDescriptor.JERSEY_SERVLET_CLASS.equals(descriptor.getClazz())) {
                baseUrl.append(descriptor.getServletMapping());
            }
        }

        // trim '/*' from end of base url.
        for (final char c : new char[] { '*', '/' }) {
            if (c == baseUrl.charAt(baseUrl.length() - 1)) {
                baseUrl.setLength(baseUrl.length() - 1);
            }
        }

        return baseUrl.toString();
    }

    /**
     *
     * @param securityRoleMapping
     */
    public void add(final SecurityRoleMapping securityRoleMapping) {
        securityRoleMappings.add(securityRoleMapping);
    }

    /**
     *
     * @param internalSecurityRole
     * @return
     */
    public String getServerRole(final String internalSecurityRole) {
        for (final SecurityRoleMapping mapping : securityRoleMappings) {
            if (internalSecurityRole.equals(mapping.getInternalSecurityRole())) {
                return mapping.getUmeRole();
            }
        }

        return null;
    }
}
