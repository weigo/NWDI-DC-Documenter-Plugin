/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Locale;
import java.util.ResourceBundle;

import jenkins.plugins.confluence.soap.v1.RemotePageSummary;
import jenkins.plugins.confluence.soap.v2.RemotePage;
import jenkins.plugins.confluence.soap.v2.RemotePageUpdateOptions;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.AbstractDevelopmentConfigurationVisitor;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.hudson.nwdi.IDevelopmentComponentFilter;

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
    private final IDevelopmentComponentFilter vendorFilter;

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
     * Create an instance of the confluence wiki content generator for
     * development components.
     * 
     * @param dcFactory
     *            registry for development components.
     * @param vendorFilter
     *            filter for development components by vendor.
     * @param velocityEngine
     *            velocity engine for doing transformations.
     * @param session
     *            the confluence session
     * @param spaceKey
     *            the key of the confluence space used to store the generated
     *            documentation.
     */
    public DevelopmentConfigurationConfluenceWikiGenerator(final DevelopmentComponentFactory dcFactory,
        final IDevelopmentComponentFilter vendorFilter, final VelocityEngine velocityEngine,
        final ConfluenceSession session, final String spaceKey) {
        this.vendorFilter = vendorFilter;
        this.session = session;
        this.spaceKey = spaceKey;
        generator =
            new DevelopmentComponentReportGenerator(dcFactory, velocityEngine,
                "/org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentWikiTemplate.vm",
                ResourceBundle.getBundle("org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentReport",
                    Locale.GERMAN), Locale.GERMAN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentConfiguration configuration) {
        try {
            createOverviewPage(configuration);
            copyResources();

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
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentComponent component) {
        if (!vendorFilter.accept(component) && component.isNeedsRebuild()) {
            final StringWriter writer = new StringWriter();

            try {
                generator.execute(writer, component);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
            finally {
                try {
                    writer.close();
                    final RemotePageSummary pageSummary =
                        session.getPageSummary(spaceKey, component.getNormalizedName('_'));
                    RemotePage page = null;

                    if (pageSummary != null) {
                        page = session.getPageV2(pageSummary.getId());
                    }

                    page = new RemotePage();
                    page.setVersion(page.getVersion() + 1);
                    page.setContent(writer.toString());

                    session.updatePageV2(page, new RemotePageUpdateOptions(true, ""));
                }
                catch (final IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    protected void createOverviewPage(final DevelopmentConfiguration configuration) throws IOException {

    }

    private void copyResources() throws IOException, FileNotFoundException {
        // copyResourceToTargetFolder(cssFolder,
        // "/org/arachna/netweaver/nwdi/documenter/report/report.css",
        // "report.css");
        // copyResourceToTargetFolder(jsFolder,
        // "/org/arachna/netweaver/nwdi/documenter/report/search.js",
        // "search.js");
        // copyResourceToTargetFolder(jsFolder,
        // "/org/arachna/netweaver/nwdi/documenter/report/xpath.js",
        // "xpath.js");
    }

    // private void copyResourceToTargetFolder(final File targetFolder, final
    // String absoluteResourcePath,
    // final String targetName) throws IOException, FileNotFoundException {
    // IOUtils.copy(this.getClass().getResourceAsStream(absoluteResourcePath),
    // new FileOutputStream(targetFolder.getAbsolutePath() + File.separatorChar
    // + targetName));
    // }
}
