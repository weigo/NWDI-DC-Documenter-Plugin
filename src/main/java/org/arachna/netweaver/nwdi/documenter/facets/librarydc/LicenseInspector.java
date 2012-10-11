/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.librarydc;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import org.apache.log4j.Logger;
import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.PublicPart;
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
        final Collection<LicenseDescriptor> licenses = new HashSet<LicenseDescriptor>();
        final LicenseExtractor extractor = new LicenseExtractor();

        for (final String folder : getJarFolders(component)) {
            final FileFinder finder = new FileFinder(new File(folder), ".*\\.jar");

            for (final File jar : finder.find()) {
                ZipFile archive = null;

                try {
                    archive = new ZipFile(jar);
                    licenses.addAll(extractor.findLicenses(archive));
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
     * Determine the folders to search for jar archives using the given
     * development component.
     * 
     * @param component
     *            development component that should be inspected for contained
     *            jar archives.
     * @return list of candidate folders containing jar archives (depending on
     *         the type of the given development component).
     */
    protected Collection<String> getJarFolders(final DevelopmentComponent component) {
        final Collection<String> folders = new HashSet<String>();
        final String baseLocation = antHelper.getBaseLocation(component);

        if (CompartmentState.Source.equals(component.getCompartment().getState())) {
            folders.add(String.format("%s/libraries", baseLocation));
        }
        else {
            for (final PublicPart part : component.getPublicParts()) {
                folders.add(String.format("%s/gen/default/public/%s/lib/java/", baseLocation, part.getPublicPart()));
            }
        }

        return folders;
    }
}
