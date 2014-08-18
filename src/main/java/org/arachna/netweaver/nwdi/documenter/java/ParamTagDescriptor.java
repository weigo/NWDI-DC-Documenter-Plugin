/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.java;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * TagDescriptor class for @param tags.
 * 
 * @author Dirk Weigenand
 */
public class ParamTagDescriptor extends TagDescriptor {
    /**
     * regular expression for parameters.
     */
    private static final Pattern PARAMETER_PATTERN = Pattern.compile("(\\w+)\\s+(.*)");

    /**
     * @param tag
     * @param description
     */
    public ParamTagDescriptor(final String tag, final String description) {
        super(tag, description);
    }

    @Override
    public String getDescription() {
        return getMatchAt(2);
    }

    public String getParameterName() {
        return getMatchAt(1);
    }

    private String getMatchAt(final int position) {
        final Matcher matcher = PARAMETER_PATTERN.matcher(super.getDescription());

        return matcher.matches() ? matcher.group(position) : "";
    }
}
