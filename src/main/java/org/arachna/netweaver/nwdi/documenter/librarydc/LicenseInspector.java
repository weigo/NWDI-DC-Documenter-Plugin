/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.librarydc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Collection;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.nwdi.documenter.report.DocumentationFacet;
import org.arachna.netweaver.nwdi.documenter.report.DocumentationFacetProvider;
import org.arachna.util.io.FileFinder;

/**
 * @author Dirk Weigenand
 * 
 */
public final class LicenseInspector implements DocumentationFacetProvider<DevelopmentComponent> {
    /**
     * development component base folder
     */
    private final AntHelper antHelper;

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
     * 
     * @param component
     * @return
     */
    public DocumentationFacet execute(final DevelopmentComponent component) {
        final FileFinder finder =
            new FileFinder(new File(String.format("%s/libraries", antHelper.getBaseLocation(component))), ".*\\.jar");
        final Collection<LicenseDescriptor> licenses = new LinkedList<LicenseDescriptor>();

        for (final File jar : finder.find()) {
            try {
                final ZipFile archive = new ZipFile(jar);
                licenses.add(findLicense(archive));
                archive.close();
            }
            catch (final ZipException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

        return new DocumentationFacet("externalLibraries", licenses);
    }

    /**
     * @param archive
     * @return
     */
    private LicenseDescriptor findLicense(final ZipFile archive) {
        final Enumeration<? extends ZipEntry> entries = archive.entries();
        ZipEntry entry = null;
        LicenseDescriptor descriptor = null;

        try {
            while (entries.hasMoreElements()) {
                entry = entries.nextElement();

                if (licenseFile.matcher(entry.getName()).matches()) {
                    descriptor = extractLicense(archive.getName(), archive.getInputStream(entry));
                    break;
                }
            }
        }
        catch (final IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return descriptor == null ? new LicenseDescriptor(License.None, archive.getName(), "") : descriptor;
    }

    /**
     * @param content
     * @return
     * @throws IOException
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
     * @param content
     * @return
     * @throws IOException
     */
    private String getLicenseText(final InputStream content) throws IOException {
        final LineNumberReader reader = new LineNumberReader(new InputStreamReader(content));
        final StringBuilder licenseText = new StringBuilder();
        String line = null;

        while ((line = reader.readLine()) != null) {
            licenseText.append(line).append('\n');
        }

        content.close();

        return licenseText.toString().trim();
    }

    public class LicenseDescriptor {
        private License license;
        private String archive;
        private String licenseText;

        LicenseDescriptor(final License license, final String archive, final String licenseText) {
            this.license = license;
            this.archive = archive;
            this.licenseText = licenseText;
        }

        LicenseDescriptor() {
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
