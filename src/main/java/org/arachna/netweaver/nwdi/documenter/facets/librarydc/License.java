/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.librarydc;

import java.util.regex.Pattern;

/**
 * Enumeration of known licenses.
 * 
 * @author Dirk Weigenand
 */
public enum License {
    /**
     * MIT public license.
     */
    MIT(".*MIT License.*", "MIT"),
    /**
     * GNU public license.
     */
    GPL(".*GNU GENERAL PUBLIC LICENSE.*", "GNU General Public License"),
    /**
     * GNU lesser public license.
     */
    LGPL(".*GNU LESSER GENERAL PUBLIC LICENSE.*", "GNU Lesser General Public License"),
    /**
     * Apache License.
     */
    Apache(".*Apache (\\s*Software\\s+)?License.*", "Apache License"),
    /**
     * Unknown license.
     */
    Other(".*", "unbekannte Lizenz"),
    /**
     * No license.
     */
    None("", "keine Lizenz");

    /**
     * license name.
     */
    private final String name;

    /**
     * regular expression for matching the license string.
     */
    private final Pattern regex;

    /**
     * Create license instance using the given regular expression (for license detection) and license name.
     * 
     * @param regex
     *            regular expression for license detection.
     * @param name
     *            license name.
     */
    private License(final String regex, final String name) {
        this.name = name;
        this.regex = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Determines whether the given license text matches the regular expression of this license.
     * 
     * @param licenseText
     *            test of license.
     * @return <code>true</code> when <code>licenseText</code> matches the regular expression.
     */
    public boolean is(final String licenseText) {
        return regex.matcher(licenseText).matches();
    }
}
