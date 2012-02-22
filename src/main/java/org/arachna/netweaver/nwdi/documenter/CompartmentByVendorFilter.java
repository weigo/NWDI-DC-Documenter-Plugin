/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter;

import java.util.regex.Pattern;

import org.arachna.netweaver.dc.types.Compartment;

/**
 * Filter for {@link Compartment}s by vendor field.
 * 
 * @author Dirk Weigenand
 */
public final class CompartmentByVendorFilter {
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
    public CompartmentByVendorFilter(final Pattern vendorPattern) {
        if (vendorPattern == null) {
            throw new IllegalArgumentException("The vendorPattern argument must not be null!");
        }

        this.vendorPattern = vendorPattern;
    }

    /**
     * Validates that the given compartments vendor matches the pattern given at
     * instantiation time.
     * 
     * @param compartment
     *            the compartment to be tested against the vendor pattern
     * @return <code>true</code> when the compartments vendor matches the vendor
     *         pattern, <code>false</code> otherwise.
     */
    public boolean accept(Compartment compartment) {
        return compartment != null && this.vendorPattern.matcher(compartment.getVendor()).matches();
    }
}
