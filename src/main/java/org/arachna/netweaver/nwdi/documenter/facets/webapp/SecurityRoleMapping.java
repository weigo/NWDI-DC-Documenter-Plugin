/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

/**
 * Mapping of internal security role to UME role.
 *
 * @author Dirk Weigenand
 */
public class SecurityRoleMapping {
    /**
     * security role internal to web application.
     */
    private String internalSecurityRole;

    /**
     * UME role mapped to internal security role.
     */
    private String umeRole;

    /**
     * @return the internalSecurityRole
     */
    public String getInternalSecurityRole() {
        return internalSecurityRole;
    }

    /**
     * @param internalSecurityRole
     *            the internalSecurityRole to set
     */
    public void setInternalSecurityRole(final String internalSecurityRole) {
        this.internalSecurityRole = internalSecurityRole;
    }

    /**
     * @return the umeRole
     */
    public String getUmeRole() {
        return umeRole;
    }

    /**
     * @param umeRole
     *            the umeRole to set
     */
    public void setUmeRole(final String umeRole) {
        this.umeRole = umeRole;
    }
}
