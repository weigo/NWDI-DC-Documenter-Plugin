/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.filter;

import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;

/**
 * Filter for {@link Compartment}s by vendor field.
 * 
 * @author Dirk Weigenand
 */
public final class VendorFilter {
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
    public VendorFilter(final Pattern vendorPattern) {
        if (vendorPattern == null) {
            throw new IllegalArgumentException("The vendorPattern argument must not be null!");
        }

        this.vendorPattern = vendorPattern;
    }

    /**
     * Validates that the given compartments vendor matches the vendor pattern.
     * 
     * @param compartment
     *            the compartment to be tested against the vendor pattern
     * @return <code>true</code> when the compartments vendor matches the vendor
     *         pattern, <code>false</code> otherwise.
     */
    public boolean accept(final Compartment compartment) {
        return compartment != null && vendorPattern.matcher(compartment.getVendor()).matches();
    }

    /**
     * Validates that the given development component matches the vendor
     * pattern.
     * 
     * @param component
     *            development component to check against vendor pattern.
     * 
     * @return <code>true</code> when the vendor of the given component matches
     *         the vendor pattern, <code>false</code> otherwise.
     */
    public boolean accept(final DevelopmentComponent component) {
        return component != null && vendorPattern.matcher(component.getVendor()).matches();
    }
}
