/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.java;

/**
 * TagDescriptor class for @param tags.
 *
 * @author Dirk Weigenand
 */
public class ParamTagDescriptor extends TagDescriptor {
    /**
     * parameter name of @param tag.
     */
    private final String paramName;

    /**
     * Create new instance of <code>ParamTagDescriptor</code>.
     *
     * @param paramName
     * @param description
     */
    public ParamTagDescriptor(final String paramName, final String description) {
        super("@param", description);
        this.paramName = paramName;
    }

    public String getParameterName() {
        return paramName;
    }
}
