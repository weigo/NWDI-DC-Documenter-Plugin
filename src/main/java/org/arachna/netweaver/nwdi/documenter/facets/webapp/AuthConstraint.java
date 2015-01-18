/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

/**
 * @author Dirk Weigenand
 */
public class AuthConstraint {
    /**
     * description of this auth constraint.
     */
    private String description;

    /**
     * name of role associated with this auth constraint.
     */
    private String roleName;

    /**
     * Get the description for this auth constraint.
     *
     * @return the description for this auth constraint.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description
     *            the description to set
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return the roleName
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName
     *            the roleName to set
     */
    public void setRoleName(final String roleName) {
        this.roleName = roleName;
    }
}
