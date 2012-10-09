/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import jenkins.plugins.confluence.soap.v1.RemoteException;
import jenkins.plugins.confluence.soap.v1.RemotePage;
import jenkins.plugins.confluence.soap.v1.RemotePageSummary;
import jenkins.plugins.confluence.soap.v1.RemotePageUpdateOptions;

import org.arachna.netweaver.dc.types.AbstractDevelopmentConfigurationVisitor;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.nwdi.documenter.ReportGeneratorFactory;
import org.arachna.netweaver.nwdi.documenter.VendorFilter;
import org.arachna.netweaver.nwdi.documenter.report.svg.SVGParser;
import org.arachna.netweaver.nwdi.documenter.report.svg.SVGProperties;
import org.arachna.netweaver.nwdi.documenter.report.svg.SVGPropertyName;

import com.myyearbook.hudson.plugins.confluence.ConfluenceSession;

/**
 * Generator for documentation of a development configuration in HTML.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationConfluenceWikiGenerator extends AbstractDevelopmentConfigurationVisitor {
    /**
     * velocity template for DC report generation.
     */
    public static final String DC_WIKI_TEMPLATE =
        "/org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentWikiTemplate.vm";

    /**
     * velocity template for formatting wiki content for development
     * configurations.
     */
    public static final String DEV_CONF_WIKI_TEMPLATE =
        "/org/arachna/netweaver/nwdi/documenter/report/DevelopmentConfigurationWikiTemplate.vm";

    /**
     * velocity template for formatting wiki content for compartments.
     */
    public static final String COMPARTMENT_WIKI_TEMPLATE =
        "/org/arachna/netweaver/nwdi/documenter/report/CompartmentWikiTemplate.vm";

    /**
     * velocity template for formatting wiki content for global overview over
     * licenses of external libraries used in a track.
     */
    public static final String GLOBAL_LICENSE_OVERVIEW_WIKI_TEMPLATE =
        "/org/arachna/netweaver/nwdi/documenter/report/GlobalLicenseOverviewWikiTemplate.vm";

    /**
     * Confluence session used to publish to confluence site.
     */
    private final ConfluenceSession session;

    /**
     * filter development components by vendor.
     */
    private final VendorFilter vendorFilter;

    /**
     * Generator for a report on a development component. The target format is
     * determined via the Velocity template given at build time.
     */
    private final ReportGeneratorFactory reportGeneratorFactory;

    /**
     * Log exceptional messages.
     */
    private final PrintStream logger;

    /**
     * keep track of track overview page (to append compartment pages as
     * children).
     */
    private RemotePage trackOverviewPage;

    /**
     * keep track of the current compartment overview page (to add development
     * component reports as child pages).
     */
    private RemotePage currentCompartmentOverviewPage;

    /**
     * container for descriptors of generated dependency diagrams.
     */
    private final DiagramDescriptorContainer dotFileDescriptorContainer;

    /**
     * additional context to supply to velocity.
     */
    private final Map<String, Object> additionalContext = new HashMap<String, Object>();

    /**
     * parser for SVG diagrams.
     */
    private final SVGParser svgParser = new SVGParser();

    /**
     * NWDI track name documentation is generated for.
     */
    private String trackName;

    /**
     * Create an instance of the confluence wiki content generator for
     * development components.
     * 
     * @param reportGeneratorFactory
     *            the report generator factory.
     * @param vendorFilter
     *            filter for development components by vendor.
     * @param session
     *            the confluence session
     * @param logger
     *            the logger to use
     * @param dotFileDescriptorContainer
     *            container for descriptors of generated dependency diagrams.
     */
    public DevelopmentConfigurationConfluenceWikiGenerator(final ReportGeneratorFactory reportGeneratorFactory,
        final VendorFilter vendorFilter, final ConfluenceSession session, final PrintStream logger,
        final DiagramDescriptorContainer dotFileDescriptorContainer) {
        this.vendorFilter = vendorFilter;
        this.session = session;
        this.logger = logger;
        this.dotFileDescriptorContainer = dotFileDescriptorContainer;
        this.reportGeneratorFactory = reportGeneratorFactory;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentConfiguration configuration) {
        try {
            trackName = configuration.getName();
            additionalContext.put("trackName", trackName);
            createOverviewPage(configuration);
            createGlobalLicenseOverviewPage(configuration);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * @param configuration
     */
    private void createGlobalLicenseOverviewPage(final DevelopmentConfiguration configuration) {
        final StringWriter writer = new StringWriter();

        reportGeneratorFactory.createGlobalLicenseOverviewReportGenerator().execute(writer, configuration,
            additionalContext, getTemplateReader(GLOBAL_LICENSE_OVERVIEW_WIKI_TEMPLATE));

        createOrUpdatePage("LicenseOverviewExternalLibraries", writer.toString(), trackOverviewPage.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final Compartment compartment) {
        currentCompartmentOverviewPage =
            createOrUpdatePage(compartment.getName(), generateWikiPageContent(compartment), trackOverviewPage.getId());
    }

    /**
     * Create wiki content for the given development component.
     * 
     * @param component
     *            DC to generate wiki documentation from.
     * @return generated documentation
     */
    protected String generateWikiPageContent(final Compartment compartment) {
        final StringWriter writer = new StringWriter();

        reportGeneratorFactory.createCompartmentReportGenerator().execute(writer, compartment, additionalContext,
            getTemplateReader(COMPARTMENT_WIKI_TEMPLATE));

        return writer.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentComponent component) {
        if (!vendorFilter.accept(component)) {
            final String pageName = component.getNormalizedName("_");
            final DiagramDescriptor descriptor = dotFileDescriptorContainer.getDescriptor(component);

            if (descriptor != null) {
                addDiagramWidthToAdditionalContext("UsedDCsDiagramWidth", "UsedDCsDiagramHeight",
                    descriptor.getUsedDCsDiagram());
                addDiagramWidthToAdditionalContext("UsingDCsDiagramWidth", "UsingDCsDiagramHeight",
                    descriptor.getUsingDCsDiagram());
            }

            final String pageContent = generateWikiPageContent(component);
            final RemotePage page = createOrUpdatePage(pageName, pageContent, currentCompartmentOverviewPage.getId());

            if (descriptor != null) {
                addDependencyDiagram(page.getId(), descriptor.getUsedDCsDiagram());
                addDependencyDiagram(page.getId(), descriptor.getUsingDCsDiagram());
            }
        }
    }

    /**
     * @param descriptor
     */
    private void addDiagramWidthToAdditionalContext(final String widthProperty, final String heightProperty,
        final String diagramName) {
        try {
            final SVGProperties properties =
                svgParser.parse(new FileReader(diagramName.replaceFirst("\\.dot", "\\.svg")));
            additionalContext.put(widthProperty, properties.getProperty(SVGPropertyName.WIDTH));
            additionalContext.put(heightProperty, properties.getProperty(SVGPropertyName.HEIGHT));
        }
        catch (final FileNotFoundException e) {
            logger.append(e.getLocalizedMessage());
        }
    }

    /**
     * Add a dependency diagram to the given page.
     * 
     * @param pageId
     *            id of page to attach diagram to.
     * @param dotFileName
     *            name of original diagram description (.dot).
     */
    private void addDependencyDiagram(final long pageId, final String dotFileName) {
        try {
            session.addAttachment(pageId, new File(dotFileName.replaceFirst("\\.dot", "\\.svg")), "image/svg+xml", "");
        }
        catch (final IOException e) {
            e.printStackTrace(logger);
        }
    }

    /**
     * Create or update a wiki page with the given content. Associate it with
     * the given parent page.
     * 
     * @param pageName
     *            name of wiki page to create or update.
     * @param pageContent
     *            content of page
     * @param parent
     *            the parent to associate the page with.
     * @return the newly created or update page.
     */
    protected RemotePage createOrUpdatePage(final String pageName, final String pageContent, final Long parent) {
        try {
            final String realPageName = trackName.equals(pageName) ? pageName : trackName + '_' + pageName;
            RemotePage page = getRemotePage(realPageName, parent);

            if (!pageContent.equals(page.getContent())) {
                page.setContent(pageContent);

                if (page.getId() == 0) {
                    session.storePage(page);
                    page = getRemotePage(realPageName, parent);
                }
                else {
                    session.updatePage(page, new RemotePageUpdateOptions(true, ""));
                }
            }

            return page;
        }
        catch (final java.rmi.RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Returns the remote page with the given name. If it does not exist yet a
     * new page object will be created and associated with the given parent
     * page.
     * 
     * @param pageName
     *            name of remote page to be retrieved.
     * @param parent
     *            parent page a newly created page should be associated with.
     * @return the remote page iff it exists or a newly created page object.
     * @throws java.rmi.RemoteException
     *             when the user associated with the current confluence session
     *             has no permission to access the page.
     */
    protected RemotePage getRemotePage(final String pageName, final Long parent) throws java.rmi.RemoteException {
        RemotePageSummary pageSummary = null;

        try {
            pageSummary = session.getPageSummary(getSpaceKey(), pageName);
        }
        catch (final RemoteException e) {
            logger.append(String.format("Page %s does not exist yet.\n", pageName));
        }

        RemotePage page = null;

        if (pageSummary == null) {
            page = new RemotePage();
            page.setSpace(getSpaceKey());
            page.setTitle(pageName);
            page.setParentId(parent);
        }
        else {
            page = (RemotePage)pageSummary;
        }

        return page;
    }

    /**
     * Create wiki content for the given development component.
     * 
     * @param component
     *            DC to generate wiki documentation from.
     * @return generated documentation
     */
    protected String generateWikiPageContent(final DevelopmentComponent component) {
        final StringWriter writer = new StringWriter();

        reportGeneratorFactory.createDevelopmentComponentReportGenerator().execute(writer, component,
            additionalContext, getTemplateReader(DC_WIKI_TEMPLATE));

        return writer.toString();
    }

    /**
     * Return a reader for the template to use for generation of documentation.
     * 
     * @param template
     *            name of classpath resource containing the Velocity template.
     * @return {@link Reader} for velocity template used to generate
     *         documentation.
     */
    protected Reader getTemplateReader(final String template) {
        return new InputStreamReader(this.getClass().getResourceAsStream(template));
    }

    /**
     * Create the overview page for a track/development configuration.
     * 
     * @param configuration
     *            the development configuration.
     * @throws java.rmi.RemoteException
     *             when the home page could not be found.
     */
    protected void createOverviewPage(final DevelopmentConfiguration configuration) throws java.rmi.RemoteException {
        final Long homePageId = session.getSpace(getSpaceKey()).getHomePage();
        trackOverviewPage =
            createOrUpdatePage(configuration.getName(), generateWikiPageContent(configuration), homePageId);
    }

    /**
     * @param configuration
     * @return
     */
    private String generateWikiPageContent(final DevelopmentConfiguration configuration) {
        final StringWriter writer = new StringWriter();

        reportGeneratorFactory.createDevelopmentConfigurationReportGenerator().execute(writer, configuration,
            additionalContext, getTemplateReader(DEV_CONF_WIKI_TEMPLATE));

        return writer.toString();
    }

    /**
     * Add a property to the global context.
     * 
     * @param key
     * @param value
     */
    public void addToGlobalContext(final ContextPropertyName key, final String value) {
        additionalContext.put(key.getName(), value);
    }

    private String getSpaceKey() {
        return (String)additionalContext.get(ContextPropertyName.WikiSpace.getName());
    }
}
