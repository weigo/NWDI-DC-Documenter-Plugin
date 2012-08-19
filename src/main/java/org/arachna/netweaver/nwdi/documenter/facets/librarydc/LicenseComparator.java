/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.librarydc;

import java.util.Comparator;

/**
 * Compare licenses (by type and name).
 * 
 * @author Dirk Weigenand
 */
public final class LicenseComparator implements Comparator<License> {

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(final License l1, final License l2) {
        int result = 0;

        if (!l1.equals(l2)) {
            if (License.Other.equals(l1)) {
                result = -1;

                if (License.None.equals(l2)) {
                    result = 1;
                }
            }
            else if (License.None.equals(l1)) {
                result = 1;

                if (License.Other.equals(l2)) {
                    result = -1;
                }
            }
            else if (License.None.equals(l2)) {
                result = 1;
            }
            else if (License.Other.equals(l2)) {
                result = 1;
            }
            else {
                result = -l1.getName().compareTo(l2.getName());

                if (result > 0) {
                    result = 1;
                }
                else if (result < 0) {
                    result = -1;
                }
            }
        }

        return result;
    }
}
