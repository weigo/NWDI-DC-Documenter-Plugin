/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter;

import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.hudson.nwdi.IDevelopmentComponentFilter;

/**
 * Filter for {@link DevelopmentComponent}s by vendor field.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentByVendorFilter implements IDevelopmentComponentFilter {
    /**
     * Pattern for matching vendors against.
     */
    private final Pattern vendorPattern;

    /**
     * Create an instance of a <code>DevelopmentComponentByVendorFilter</code>
     * using the given pattern.
     * 
     * @param vendorPattern
     *            pattern to match vendors against.
     */
    public DevelopmentComponentByVendorFilter(final Pattern vendorPattern) {
        if (vendorPattern == null) {
            throw new IllegalArgumentException("The vendorPattern argument must not be null!");
        }

        this.vendorPattern = vendorPattern;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean accept(DevelopmentComponent component) {
        return component != null && this.vendorPattern.matcher(component.getVendor()).matches();
    }
}
