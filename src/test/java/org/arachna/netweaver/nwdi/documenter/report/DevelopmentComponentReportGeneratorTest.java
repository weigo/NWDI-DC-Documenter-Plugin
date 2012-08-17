/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.nwdi.documenter.DocumentationBuilder;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProviderFactory;
import org.arachna.velocity.VelocityHelper;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 */
public class DevelopmentComponentReportGeneratorTest {
    private DevelopmentComponentReportGenerator generator;
    private DevelopmentComponentFactory dcFactory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dcFactory = new DevelopmentComponentFactory();
        final VelocityEngine velocityEngine = new VelocityHelper(System.out).getVelocityEngine();
        // final ResourceBundle bundle
        generator =
            new DevelopmentComponentReportGenerator(
                new DocumentationFacetProviderFactory(new AntHelper("", dcFactory)), dcFactory, velocityEngine,
                ResourceBundle.getBundle(DocumentationBuilder.DC_REPORT_BUNDLE, Locale.GERMAN));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.report.DevelopmentComponentReportGenerator#execute(java.io.Writer, org.arachna.netweaver.dc.types.DevelopmentComponent, java.util.Map, java.io.Reader)}
     * .
     */
    @Test
    public final void testExecute() {
        final OutputStreamWriter writer = new OutputStreamWriter(System.out);
        final Map<String, Object> context = new HashMap<String, Object>();
        context.put(ContextPropertyName.ProjectUrl.getName(), "projectUrl");
        context.put(ContextPropertyName.WikiSpace.getName(), "NWENV");
        final DevelopmentComponent component = dcFactory.create("example.com", "dc", DevelopmentComponentType.Java);

        generator.execute(writer, component, context, getTemplateReader());
    }

    protected Reader getTemplateReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            DevelopmentConfigurationConfluenceWikiGenerator.DC_WIKI_TEMPLATE));
    }

}
