/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.librarydc;

/**
 * Descriptor of detected licenses. Contains the license type ({@see License}), the jar archive covered by the respective license and
 * the text of the license.
 * 
 * @author Dirk Weigenand
 */
public class LicenseDescriptor {
    /**
     * kind of license.
     */
    private License license;

    /**
     * jar archive covered by this licence.
     */
    private final String archive;

    /**
     * text of license conditions.
     */
    private final String licenseText;

    /**
     * Create a new instance of a license descriptor using the given kind of license, teh covered archive and the license text.
     * 
     * @param license
     *            kind of license.
     * @param archive
     *            jar archive covered by license.
     * @param licenseText
     *            text of license.
     */
    public LicenseDescriptor(final License license, final String archive, final String licenseText) {
        this.license = license;
        this.archive = archive;
        this.licenseText = licenseText;
    }

    /**
     * @param license
     *            the license to set
     */
    void setLicense(final License license) {
        this.license = license;
    }

    /**
     * @return the license
     */
    public License getLicense() {
        return license;
    }

    /**
     * @return the archive
     */
    public String getArchive() {
        return archive;
    }

    /**
     * @return the licenseText
     */
    public String getLicenseText() {
        return licenseText;
    }

    @Override
    public String toString() {
        final String text = licenseText.length() > 20 ? licenseText.substring(0, 20) + "..." : licenseText;
        return "LicenseDescriptor[license = '" + license.getName() + "', archive = '" + archive + "', text = '"
            + text + "']";
    }
}