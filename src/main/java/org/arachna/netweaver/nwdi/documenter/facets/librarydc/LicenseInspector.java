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
import java.util.HashSet;
import java.util.LinkedList;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.util.io.FileFinder;

/**
 * Inspector for external library development components. Reads the contained jar archives in order to determine the license under which
 * these are distributed.
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
     * Regular expression to match Jar-Entries against. Matches jar entries that contain license conditions.
     */
    private final Pattern licenseFile = Pattern
        .compile(".*?/?(LICENSE|COPYING|MPL-\\d+\\.\\d+|lgpl|(apache_)?license|about|.*_lic)(\\.(txt|html))?");

    /**
     * Create a new instance of a license inspector.
     * 
     * @param antHelper
     *            helper class for determining properties of development components.
     */
    public LicenseInspector(final AntHelper antHelper) {
        this.antHelper = antHelper;
    }

    /**
     * Inspect the given external library development components jar archives for license conditions.
     * 
     * @param component
     *            development component to inspect.
     * @return a documentation facet containing the found license conditions.
     */
    @Override
    public DocumentationFacet execute(final DevelopmentComponent component) {
        final Collection<String> folders = new HashSet<String>();

        if (CompartmentState.Source.equals(component.getCompartment().getState())) {
            folders.add(String.format("%s/libraries", antHelper.getBaseLocation(component)));
        }
        else {
            for (final PublicPart part : component.getPublicParts()) {
                folders.add(String.format("%s/gen/default/public/%s/lib/java/", antHelper.getBaseLocation(component),
                    part.getPublicPart()));
            }
        }

        final Collection<LicenseDescriptor> licenses = new HashSet<LicenseDescriptor>();

        for (final String folder : folders) {
            final FileFinder finder = new FileFinder(new File(folder), ".*\\.jar");

            for (final File jar : finder.find()) {
                ZipFile archive = null;

                try {
                    archive = new ZipFile(jar);
                    licenses.addAll(findLicense(archive));
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
    private Collection<LicenseDescriptor> findLicense(final ZipFile archive) {
        final Enumeration<? extends ZipEntry> entries = archive.entries();
        ZipEntry entry = null;
        Collection<LicenseDescriptor> descriptors = new LinkedList<LicenseDescriptor>();
        final File archivePath = new File(archive.getName());

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
     * @param entry
     * @return
     */
    protected boolean isKnownLicenseFile(String entryName) {
        return licenseFile.matcher(entryName).matches();
    }

    /**
     * Extract license from the given content.
     * 
     * @param archive
     *            the name of the archive (used in the returned descriptor).
     * @param content
     *            text of license conditions
     * @return a descriptor naming the license found. If the license could not be determined a type of {@see License#Other} will be
     *         returned.
     * @throws IOException
     *             when reading the license text from <code>content</code> fails.
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

    public static void main(String[] args) {
        DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();
        AntHelper antHelper = new AntHelper("C:/tmp/jenkins/jobs/Libraries73/workspace", dcFactory);
        LicenseInspector inspector = new LicenseInspector(antHelper);
        DevelopmentComponent component = dcFactory.create("itext.org", "itext1.2", DevelopmentComponentType.ExternalLibrary);
        Compartment compartment = Compartment.create("itext.org", "ITEXTPDF", CompartmentState.Source, "");
        compartment.add(component);
        inspector.execute(component);
    }
}