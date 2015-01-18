/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;

/**
 * Descriptor for security constraints.
 *
 * @author Dirk Weigenand
 */
public class SecurityConstraintDescriptor {
    /**
     *
     */
    private final Collection<WebResourceCollection> webResources = new LinkedList<WebResourceCollection>();

    /**
     *
     */
    private AuthConstraint authConstraint;

    /**
     * Gets the collection of web resources in this security constraint.
     *
     * @return collection of web resources in this security constraint. May be empty but never <code>null</code>.
     */
    public Collection<WebResourceCollection> getWebResourceCollections() {
        return Collections.unmodifiableCollection(webResources);
    }

    /**
     * Add the given collection of web resources to this security constraint.
     *
     * @param resources
     *            collection of web resources to add to this security constraint.
     */
    public void add(final WebResourceCollection resources) {
        webResources.add(resources);
    }

    /**
     *
     * @param authConstraint
     */
    public void setAuthConstraint(final AuthConstraint authConstraint) {
        this.authConstraint = authConstraint;
    }

    /**
     *
     * @return
     */
    public AuthConstraint getAuthConstraint() {
        return authConstraint;
    }
}
