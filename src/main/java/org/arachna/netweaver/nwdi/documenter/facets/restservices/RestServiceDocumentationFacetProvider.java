/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.restservices;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.util.io.FileFinder;

/**
 * Provider for documentation of REST-services.
 * 
 * @author Dirk Weigenand
 */
public class RestServiceDocumentationFacetProvider implements DocumentationFacetProvider<DevelopmentComponent> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger("RestServiceDocumentationFacetProvider");

    @Override
    public DocumentationFacet execute(final DevelopmentComponent component) {
        final List<RestService> services = new ArrayList<RestService>();
        final RestServiceVisitor visitor = new RestServiceVisitor();
        LOGGER.fine(String.format("RestServiceDocumentationFacetProvider: %s", component.getNormalizedName("/")));

        for (final String sourceFolder : component.getSourceFolders()) {
            final FileFinder finder = new FileFinder(new File(sourceFolder), ".*\\.java");

            for (final File javaSource : finder.find()) {
                try {
                    final CompilationUnit compilationUnit = JavaParser.parse(new FileInputStream(javaSource), "UTF-8");
                    final RestService service = new RestService();
                    LOGGER.fine(String.format("Untersuche Klasse %s...", javaSource.getAbsolutePath()));
                    compilationUnit.accept(visitor, service);

                    if (!service.getMethods().isEmpty()) {
                        services.add(service);
                    }

                }
                catch (final IOException e) {
                    LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
                }
                catch (final ParseException e) {
                    LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
                }
            }
        }

        return new RestServiceDocumentationFacet(services);
    }
}
