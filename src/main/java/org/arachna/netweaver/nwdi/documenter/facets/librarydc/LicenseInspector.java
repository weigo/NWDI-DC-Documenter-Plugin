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
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.util.io.FileFinder;

/**
 * Inspector for external library development components. Reads the contained
 * jar archives in order to determine the license under which these are
 * distributed.
 * 
 * @author Dirk Weigenand
 */
public final class LicenseInspector implements DocumentationFacetProvider<DevelopmentComponent> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger(LicenseInspector.class);

    /**
     * development component base folder.
     */
    private final AntHelper antHelper;

    /**
     * Regular expression to match Jar-Entries against. Matches jar entries that
     * contain license conditions.
     */
    private final Pattern licenseFile = Pattern.compile("^((Legal|META-INF)/)?(LICENSE|COPYING)(\\.txt)?");

    /**
     * Create a new instance of a license inspector.
     * 
     * @param antHelper
     *            helper class for determining properties of development
     *            components.
     */
    public LicenseInspector(final AntHelper antHelper) {
        this.antHelper = antHelper;
    }

    /**
     * Inspect the given external library development components jar archives
     * for license conditions.
     * 
     * @param component
     *            development component to inspect.
     * @return a documentation facet containing the found license conditions.
     */
    @Override
    public DocumentationFacet execute(final DevelopmentComponent component) {
        final FileFinder finder =
            new FileFinder(new File(String.format("%s/libraries", antHelper.getBaseLocation(component))), ".*\\.jar");
        final Collection<LicenseDescriptor> licenses = new LinkedList<LicenseDescriptor>();

        for (final File jar : finder.find()) {
            ZipFile archive = null;

            try {
                archive = new ZipFile(jar);
                licenses.add(findLicense(archive));
            }
            catch (final ZipException e) {
                LOGGER.fatal(e.getMessage(), e);
            }
            catch (final IOException e) {
                LOGGER.fatal(e.getMessage(), e);
            }
            finally {
                if (archive != null) {
                    try {
                        archive.close();
                    }
                    catch (final IOException e) {
                        LOGGER.fatal(e.getMessage(), e);
                    }
                }
            }
        }

        return new DocumentationFacet("externalLibraries", licenses);
    }

    /**
     * Find license (if any) in the given jar archive.
     * 
     * @param archive
     *            jar archive to inspect for known licenses.
     * @return a descriptor detailing the license found (if any).
     */
    private LicenseDescriptor findLicense(final ZipFile archive) {
        final Enumeration<? extends ZipEntry> entries = archive.entries();
        ZipEntry entry = null;
        LicenseDescriptor descriptor = null;
        final File archivePath = new File(archive.getName());

        try {
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();

                if (licenseFile.matcher(entry.getName()).matches()) {
                    descriptor = extractLicense(archivePath.getName(), archive.getInputStream(entry));
                    break;
                }
            }
        }
        catch (final IOException e) {
            LOGGER.fatal(e.getMessage(), e);
        }

        return descriptor == null ? new LicenseDescriptor(License.None, archivePath.getName(), "") : descriptor;
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
     * @throws IOException
     *             when reading the license text from <code>content</code>
     *             fails.
     */
    private LicenseDescriptor extractLicense(final String archive, final InputStream content) throws IOException {
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
     * @throws IOException
     *             when reading the content of the input stream fails.
     */
    private String getLicenseText(final InputStream content) throws IOException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(content));
        final StringBuilder licenseText = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            licenseText.append(line).append('\n');
        }

        content.close();

        return licenseText.toString().trim();
    }

    /**
     * Descriptor of detected licenses. Contains the license type ({@see
     * License}), the jar archive covered by the respective license and the text
     * of the license.
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
         * Create a new instance of a license descriptor using the given kind of
         * license, teh covered archive and the license text.
         * 
         * @param license
         *            kind of license.
         * @param archive
         *            jar archive covered by license.
         * @param licenseText
         *            text of license.
         */
        LicenseDescriptor(final License license, final String archive, final String licenseText) {
            this.license = license;
            this.archive = archive;
            this.licenseText = licenseText;
        }

        /**
         * @param license
         *            the license to set
         */
        private void setLicense(final License license) {
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
}
