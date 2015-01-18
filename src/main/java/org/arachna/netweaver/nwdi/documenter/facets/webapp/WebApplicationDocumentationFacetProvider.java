/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

import japa.parser.JavaParser;
import japa.parser.ParseException;
import japa.parser.ast.CompilationUnit;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.netweaver.nwdi.documenter.facets.webapp.restservices.RestService;
import org.arachna.netweaver.nwdi.documenter.facets.webapp.restservices.RestServiceVisitor;
import org.arachna.util.io.FileFinder;
import org.arachna.xml.DigesterHelper;

/**
 * Provider for documentation of web applications.
 *
 * @author Dirk Weigenand
 */
public class WebApplicationDocumentationFacetProvider implements DocumentationFacetProvider<DevelopmentComponent> {
    /**
     * Logger.
     */
    private static final Logger LOGGER = Logger.getLogger("WebApplicationDocumentationFacetProvider");

    /**
     *
     */
    private final AntHelper antHelper;

    /**
     *
     * @param antHelper
     */
    public WebApplicationDocumentationFacetProvider(final AntHelper antHelper) {
        this.antHelper = antHelper;

    }

    @Override
    public DocumentationFacet execute(final DevelopmentComponent component) {
        WebApplication webApplication = null;

        try {
            webApplication = getWebApplication(component);
            webApplication.setRestServices(getRestServices(component));
            updateSecurityRoleMappings(component, webApplication);
        }
        catch (final IOException e) {
            LOGGER.log(Level.SEVERE, e.getLocalizedMessage(), e);
        }

        return new DocumentationFacet("WebApplication", webApplication);
    }

    /**
     * @param component
     */
    private WebApplication getWebApplication(final DevelopmentComponent component) throws IOException {
        return new DigesterHelper<WebApplication>(new WebXmlRulesModuleProducer()).execute(getWebXmlReader(component));
    }

    /**
     * @param component
     * @return
     * @throws FileNotFoundException
     */
    private Reader getWebXmlReader(final DevelopmentComponent component) throws FileNotFoundException {
        return new FileReader(String.format("%s/WebContent/WEB-INF/web.xml", antHelper.getBaseLocation(component)));
    }

    private void updateSecurityRoleMappings(final DevelopmentComponent component, final WebApplication application) throws IOException {
        new DigesterHelper<WebApplication>(new WebJ2eeRulesModuleProducer()).update(getWebJ2eeXmlReader(component), application);
    }

    /**
     * @param component
     * @return
     * @throws FileNotFoundException
     */
    private Reader getWebJ2eeXmlReader(final DevelopmentComponent component) throws FileNotFoundException {
        return new FileReader(String.format("%s/WebContent/WEB-INF/web-j2ee-engine.xml", antHelper.getBaseLocation(component)));
    }

    /**
     * Find REST service components in the sources of the web application.
     *
     * @param component
     *            development component whose java sources should be examined for REST services.
     * @return a list of REST services extracted from the java sources of the given development component.
     */
    private List<RestService> getRestServices(final DevelopmentComponent component) {
        final List<RestService> services = new ArrayList<RestService>();
        final RestServiceVisitor visitor = new RestServiceVisitor();

        for (final String sourceFolder : component.getSourceFolders()) {
            final FileFinder finder = new FileFinder(new File(sourceFolder), ".*\\.java");

            for (final File javaSource : finder.find()) {
                try {
                    final CompilationUnit compilationUnit = JavaParser.parse(new FileInputStream(javaSource), "UTF-8");
                    final RestService service = new RestService();
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

        return services;
    }
}
