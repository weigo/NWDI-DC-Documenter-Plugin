/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import jenkins.plugins.confluence.soap.v1.RemoteException;
import jenkins.plugins.confluence.soap.v1.RemotePage;
import jenkins.plugins.confluence.soap.v1.RemotePageSummary;

import org.arachna.netweaver.dc.types.AbstractDevelopmentConfigurationVisitor;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.nwdi.documenter.VendorFilter;

import com.myyearbook.hudson.plugins.confluence.ConfluenceSession;

/**
 * Generator for documentation of a development configuration in HTML.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationConfluenceWikiGenerator extends AbstractDevelopmentConfigurationVisitor {
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
    private final DevelopmentComponentReportGenerator generator;

    /**
     * the key of the confluence space used to store the generated
     * documentation.
     */
    private final String spaceKey;

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
    private final DotFileDescriptorContainer dotFileDescriptorContainer;

    /**
     * additional context to supply to velocity.
     */
    private final Map<String, Object> additionalContext = new HashMap<String, Object>();

    /**
     * Create an instance of the confluence wiki content generator for
     * development components.
     * 
     * @param generator
     *            the DC documentation generator.
     * @param vendorFilter
     *            filter for development components by vendor.
     * @param session
     *            the confluence session
     * @param spaceKey
     *            the key of the confluence space used to store the generated
     *            documentation.
     * @param logger
     *            the logger to use
     * @param dotFileDescriptorContainer
     *            container for descriptors of generated dependency diagrams.
     */
    public DevelopmentConfigurationConfluenceWikiGenerator(final DevelopmentComponentReportGenerator generator,
        final VendorFilter vendorFilter, final ConfluenceSession session, final String spaceKey,
        final PrintStream logger, final DotFileDescriptorContainer dotFileDescriptorContainer) {
        this.vendorFilter = vendorFilter;
        this.session = session;
        this.spaceKey = spaceKey;
        this.logger = logger;
        this.dotFileDescriptorContainer = dotFileDescriptorContainer;
        additionalContext.put("wikiSpace", this.spaceKey);

        this.generator = generator;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentConfiguration configuration) {
        try {
            createOverviewPage(configuration);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final Compartment compartment) {
        currentCompartmentOverviewPage =
            createOrUpdatePage(compartment.getName(), compartment.getCaption(), trackOverviewPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentComponent component) {
        if (!vendorFilter.accept(component) && component.isNeedsRebuild()) {
            final String pageName = component.getNormalizedName("_");
            final DotFileDescriptor descriptor = dotFileDescriptorContainer.getDescriptor(component);
            // FIXME: determine width of diagrams and supply it to page
            // generator!
            final String pageContent = generateWikiPageContent(component);
            final RemotePage page = createOrUpdatePage(pageName, pageContent, currentCompartmentOverviewPage);

            addDependencyDiagram(page.getId(), descriptor.getUsedDCsDiagram());
            addDependencyDiagram(page.getId(), descriptor.getUsingDCsDiagram());
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
    protected RemotePage createOrUpdatePage(final String pageName, final String pageContent, final RemotePage parent) {
        try {
            final RemotePage page = getRemotePage(pageName, parent);
            page.setContent(pageContent);
            session.storePage(page);

            if (page.getId() == 0) {
                return getRemotePage(pageName, parent);
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
    protected RemotePage getRemotePage(final String pageName, final RemotePage parent) throws java.rmi.RemoteException {
        RemotePageSummary pageSummary = null;

        try {
            pageSummary = session.getPageSummary(spaceKey, pageName);
        }
        catch (final RemoteException e) {
            logger.append(String.format("Page %s does not exist yet.\n", pageName));
        }

        RemotePage page = null;

        if (pageSummary == null) {
            page = new RemotePage();
            page.setSpace(spaceKey);
            page.setTitle(pageName);
            page.setParentId(parent.getId());
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

        generator.execute(writer, component, additionalContext);

        return writer.toString();
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
        final Long homePageId = session.getSpace(spaceKey).getHomePage();
        final RemotePage homePage = session.getPageV1(homePageId.longValue());
        trackOverviewPage = createOrUpdatePage(configuration.getName(), configuration.getCaption(), homePage);
    }
}
