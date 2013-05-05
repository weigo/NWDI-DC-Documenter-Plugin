/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.io.Reader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import jenkins.plugins.confluence.soap.v1.RemoteException;
import jenkins.plugins.confluence.soap.v1.RemotePage;
import jenkins.plugins.confluence.soap.v1.RemotePageUpdateOptions;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.DevelopmentConfigurationVisitor;
import org.arachna.netweaver.nwdi.documenter.filter.VendorFilter;
import org.arachna.netweaver.nwdi.dot4j.DiagramDescriptor;
import org.arachna.netweaver.nwdi.dot4j.DiagramDescriptorContainer;

import com.myyearbook.hudson.plugins.confluence.ConfluenceSession;

/**
 * Generator for documentation of a development configuration in HTML.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationConfluenceWikiGenerator implements DevelopmentConfigurationVisitor {
    /**
     * path to XSL stylesheets.
     */
    private static final String STYLESHEET_PATH_TEMPLATE = "/org/arachna/netweaver/nwdi/documenter/report/%s";

    /**
     * Confluence session used to publish to confluence site.
     */
    private final ConfluenceSession session;

    /**
     * filter development components by vendor.
     */
    private final VendorFilter vendorFilter;

    /**
     * base folder containing docbook sources for wiki pages.
     */
    private final File reportSourceFolder;

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
     * NWDI track name documentation is generated for.
     */
    private String trackName;

    /**
     * Template for transforming docbook to confluence wiki pages.
     */
    private final Templates template;

    /**
     * Create an instance of the confluence wiki content generator for
     * development components.
     * 
     * @param reportSourceFolder
     *            base folder containing docbook sources for wiki pages.
     * @param vendorFilter
     *            filter for development components by vendor.
     * @param session
     *            the confluence session
     * @param logger
     *            the logger to use
     * @param dotFileDescriptorContainer
     *            container for descriptors of generated dependency diagrams.
     */
    public DevelopmentConfigurationConfluenceWikiGenerator(final File reportSourceFolder,
        final VendorFilter vendorFilter, final ConfluenceSession session, final PrintStream logger,
        final DiagramDescriptorContainer dotFileDescriptorContainer) {
        this.vendorFilter = vendorFilter;
        this.session = session;
        this.logger = logger;
        this.dotFileDescriptorContainer = dotFileDescriptorContainer;
        this.reportSourceFolder = reportSourceFolder;

        try {
            final TransformerFactory factory = TransformerFactory.newInstance();
            factory.setErrorListener(new ErrorListener() {

                @Override
                public void warning(final TransformerException exception) throws TransformerException {
                    System.err.println(exception.getMessageAndLocation());
                    throw new IllegalStateException(exception);
                }

                @Override
                public void error(final TransformerException exception) throws TransformerException {
                    System.err.println(exception.getMessageAndLocation());
                    throw new IllegalStateException(exception);
                }

                @Override
                public void fatalError(final TransformerException exception) throws TransformerException {
                    System.err.println(exception.getMessageAndLocation());
                    throw new IllegalStateException(exception);
                }

            });
            template =
                factory.newTemplates(new StreamSource(this.getClass().getResourceAsStream(
                    String.format(STYLESHEET_PATH_TEMPLATE, "confluence-pre4.xsl"))));
        }
        catch (final TransformerConfigurationException e) {
            throw new IllegalStateException(e);
        }
        catch (final TransformerFactoryConfigurationError e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentConfiguration configuration) {
        try {
            trackName = configuration.getName();
            createOverviewPage(configuration);
            createGlobalLicenseOverviewPage(configuration);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Create an overview page of licenses for external libraries used in the
     * given development configuration.
     * 
     * @param configuration
     *            development configuration to create license overview for.
     */
    private void createGlobalLicenseOverviewPage(final DevelopmentConfiguration configuration) {
        final StringWriter writer = new StringWriter();

        createOrUpdatePage("LicenseOverviewExternalLibraries", writer.toString(), trackOverviewPage.getId());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final Compartment compartment) {
        if (!vendorFilter.accept(compartment)) {
            currentCompartmentOverviewPage =
                createOrUpdatePage(compartment.getName(), generateWikiPageContent(compartment),
                    trackOverviewPage.getId());
        }
    }

    /**
     * Create wiki content for the given compartment.
     * 
     * @param compartment
     *            compartment to generate wiki documentation from.
     * @return generated documentation
     */
    protected String generateWikiPageContent(final Compartment compartment) {
        final String docBook = String.format("%s/%s.xml", compartment.getName(), compartment.getName());
        return transform(createDocBookTemplateReader(docBook));
    }

    private Reader createDocBookTemplateReader(final String docBookTemplate) {
        try {
            return new FileReader(new File(reportSourceFolder, docBookTemplate));
        }
        catch (final FileNotFoundException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentComponent component) {
        if (!vendorFilter.accept(component)) {
            final String pageName = component.getNormalizedName("_");
            final DiagramDescriptor descriptor = dotFileDescriptorContainer.getDescriptor(component);

            final String pageContent = generateWikiPageContent(component);
            final RemotePage page = createOrUpdatePage(pageName, pageContent, currentCompartmentOverviewPage.getId());

            if (descriptor != null) {
                addDependencyDiagram(page.getId(), descriptor.getUsedDCsDiagram());
                addDependencyDiagram(page.getId(), descriptor.getUsingDCsDiagram());
            }
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
        if (!dotFileName.isEmpty()) {
            final File attachment = new File(dotFileName.replaceFirst("\\.dot", "\\.svg"));

            try {
                session.addAttachment(pageId, attachment, "image/svg+xml", "");
            }
            catch (final IOException e) {
                logger.println(String.format("Attachment not found: %s!", attachment.getAbsolutePath()));
            }
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
        RemotePage page = null;

        try {
            // FIXME: Create wrapper for Confluence v1/v2 SOAP API.
            page = session.getPage(getSpaceKey(), pageName);
        }
        catch (final RemoteException e) {
            logger.append(String.format("Page %s does not exist yet.\n", pageName));
        }

        if (page == null) {
            page = new RemotePage();
            page.setSpace(getSpaceKey());
            page.setTitle(pageName);
            page.setParentId(parent);
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
        final String docBook =
            String.format("%s/%s.xml", component.getCompartment().getName(), component.getNormalizedName("_"));
        return transform(createDocBookTemplateReader(docBook));
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
        final String docBook = String.format("%s.xml", configuration.getName());
        return transform(createDocBookTemplateReader(docBook));
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

    private Transformer newTransformer() {
        Transformer transformer;

        try {
            transformer = template.newTransformer();
            transformer.setParameter("wikiSpace", getSpaceKey());
            transformer.setParameter("track", trackName);
        }
        catch (final TransformerConfigurationException e) {
            throw new IllegalStateException(e);
        }

        return transformer;
    }

    private String transform(final Reader source) {
        final StringWriter result = new StringWriter();

        try {
            newTransformer().transform(new StreamSource(source), new StreamResult(result));
        }
        catch (final TransformerException e) {
            throw new IllegalStateException(e);
        }

        return result.toString();
    }
}
