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
    MIT(".*MIT License.*", "MIT", "http://opensource.org/licenses/mit-license.php"),
    /**
     * GNU public license.
     */
    GPL(".*GNU GENERAL PUBLIC LICENSE.*", "GNU General Public License", "http://opensource.org/licenses/GPL-3.0"),
    /**
     * GNU lesser public license.
     */
    LGPL(".*GNU LESSER GENERAL PUBLIC LICENSE.*", "GNU Lesser General Public License",
        "http://opensource.org/licenses/LGPL-3.0"),
    /**
     * Apache License.
     */
    Apache(".*Apache (\\s*Software\\s+)?License.*", "Apache License", "http://opensource.org/licenses/Apache-2.0"),

    /**
     * Mozilla Public License.
     */
    MPL(".*MOZILLA PUBLIC LICENSE.*", "Mozilla Public License", "http://opensource.org/licenses/MPL-2.0"),

    /**
     * License by 'The Werken Company'.
     */
    Werken("Copyright\\s+.*?\\s+The Werken Company\\..*?", "The Werken Company", "http://jaxen.codehaus.org/license.html"),

    /**
     * License by 'MetaStuff Limited'.
     */
    MetaStuffLtd("Copyright\\s+.*?\\s+MetaStuff, Ltd\\..*?", "MetaStuff, Ltd. (BSD style license)",
        "http://dom4j.sourceforge.net/dom4j-1.6.1/license.html"),

    /**
     * Copyrighted material by the late Sun Microsystems Inc. and possibly others.
     */
    Sun(".*?Copyright\\s+.*?\\s+Sun Microsystems, Inc\\..*?", "Copyright by Sun Microsystems and now probably Oracle Inc.",
        "https://www.google.com/search?ie=utf-8&oe=utf-8&q=software+license+"),

    /**
     * The Eclipse Public License.
     */
    Eclipse(".*Eclipse Public License.*", "Eclipse Public License", "http://opensource.org/licenses/EPL-1.0"),

    /**
     * HSQL (BSD like license).
     */
    Hypersonic(".*?Copyright.*?The HSQL Development Group.*?", "HyperSQL (based on BSD License)", "http://hsqldb.org/web/hsqlLicense.html"),

    /**
     * The BSD license.
     */
    BSD(".*?BSD License.*?", "BSD License", "http://opensource.org/licenses/BSD-2-Clause"),

    /**
     * JDOM license.
     */
    JDOM(".*?Copyright (C).*?Jason Hunter & Brett McLaughlin\\..*?", "JDOM License (Apache without acknowledgment clause)",
        "http://www.jdom.org/docs/faq.html#a0030"),

    /**
     * Unknown license.
     */
    Other(".*", "unbekannte Lizenz", "https://www.google.com/search?ie=utf-8&oe=utf-8&q=software+license+"),
    /**
     * No license.
     */
    None("", "keine Lizenz", "https://www.google.com/search?ie=utf-8&oe=utf-8&q=software+license+");

    /**
     * license name.
     */
    private final String name;

    /**
     * URL to license.
     */
    private final String url;

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
     * @param url
     *            URL to license text and probably more information.
     */
    private License(final String regex, final String name, final String url) {
        this.name = name;
        this.regex = Pattern.compile(regex, Pattern.MULTILINE | Pattern.DOTALL);
        this.url = url;
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

    /**
     * Return the URL to more information about the license.
     * 
     * @return the url
     */
    public String getUrl() {
        return url;
    }
}
