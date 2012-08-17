/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webservices;

/**
 * A response of a web service call.
 * 
 * @author Dirk Weigenand
 */
public class Response {
    /**
     * return type.
     */
    private Parameter parameter;

    /**
     * @return the parameter
     */
    public Parameter getParameter() {
        return parameter;
    }

    /**
     * @param parameter
     *            the parameter to set
     */
    public void setParameter(final Parameter parameter) {
        this.parameter = parameter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String toString() {
        return "Response [parameter=" + parameter + "]";
    }
}
