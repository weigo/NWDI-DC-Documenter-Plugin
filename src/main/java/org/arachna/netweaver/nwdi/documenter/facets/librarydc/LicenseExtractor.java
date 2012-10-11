/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.librarydc;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;

/**
 * Extract license information from a Jar archive.
 * 
 * @author Dirk Weigenand
 */
final class LicenseExtractor {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LicenseInspector.class);

    /**
     * Regular expression to match Jar-Entries against. Matches jar entries that
     * contain license conditions.
     */
    private final Pattern licenseFile = Pattern
        .compile(".*?/?(LICENSE|COPYING|MPL-\\d+\\.\\d+|lgpl|(apache_)?license|about|.*_lic)(\\.(txt|html))?");

    /**
     * Find licenses (if any) in the given jar archive.
     * 
     * @param archive
     *            jar archive to inspect for known licenses.
     * @return a descriptor detailing the license found (if any).
     */
    Collection<LicenseDescriptor> findLicenses(final ZipFile archive) {
        final Enumeration<? extends ZipEntry> entries = archive.entries();
        final Collection<LicenseDescriptor> descriptors = new LinkedList<LicenseDescriptor>();
        final File archivePath = new File(archive.getName());
        ZipEntry entry = null;

        try {
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();

                if (isKnownLicenseFile(entry.getName())) {
                    descriptors.add(extractLicense(archivePath.getName(), archive.getInputStream(entry)));
                }
            }
        }
        catch (final IOException e) {
            LOGGER.fatal(e.getMessage(), e);
        }

        if (descriptors.isEmpty()) {
            descriptors.add(new LicenseDescriptor(License.None, archivePath.getName(), ""));
        }

        return descriptors;
    }

    /**
     * Determine whether the given entry name matches a known license.
     * 
     * @param entryName
     *            name of entry of a ZipArchive.
     * @return <code>true</code> when the given entry matches the regular
     *         expression of file name known to contain license information,
     *         <code>false</code> otherwise.
     */
    private boolean isKnownLicenseFile(final String entryName) {
        return licenseFile.matcher(entryName).matches();
    }

    /**
     * Extract license from the given content.
     * 
     * @param archive
     *            the name of the archive (used in the returned descriptor).
     * @param content
     *            text of license conditions
     * @return a descriptor naming the license found. If the license could not
     *         be determined a type of {@see License#Other} will be returned.
     */
    private LicenseDescriptor extractLicense(final String archive, final InputStream content) {
        final String licenseText = getLicenseText(content);
        final LicenseDescriptor descriptor = new LicenseDescriptor(License.Other, archive, licenseText);

        for (final License license : License.values()) {
            if (license.is(licenseText)) {
                descriptor.setLicense(license);
                break;
            }
        }

        return descriptor;
    }

    /**
     * Read the license text contained in the given input stream.
     * 
     * @param content
     *            input stream containing license text.
     * @return license text as string.
     */
    private String getLicenseText(final InputStream content) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        final StringBuilder licenseText = new StringBuilder();
        String line = null;

        try {
            while ((line = reader.readLine()) != null) {
                licenseText.append(line).append('\n');
            }
        }
        catch (final IOException e) {
            LOGGER.fatal(e.getMessage(), e);
        }
        finally {
            try {
                content.close();
            }
            catch (final IOException e) {
                LOGGER.fatal(e.getMessage(), e);
            }
        }

        return licenseText.toString().trim();
    }
}
