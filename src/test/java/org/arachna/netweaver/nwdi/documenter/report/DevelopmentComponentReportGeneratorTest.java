/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.OutputStreamWriter;
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
import org.arachna.velocity.VelocityHelper;
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
    public void setUp() {
        dcFactory = new DevelopmentComponentFactory();
        final VelocityEngine velocityEngine = new VelocityHelper(System.out).getVelocityEngine();
        // final ResourceBundle bundle
        final DevelopmentComponent component = dcFactory.create("example.com", "dc", DevelopmentComponentType.Java);
        generator =
            new DevelopmentComponentReportGenerator(
                new DocumentationFacetProviderFactory(new AntHelper("", dcFactory)), dcFactory, velocityEngine,
                ResourceBundle.getBundle(DocumentationBuilder.DC_REPORT_BUNDLE, Locale.GERMAN), component);
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

        // FIXME: add asserts for project url generation.
        generator.execute(writer, context, DocBookVelocityTemplate.DevelopmentComponent.getTemplate());
    }
}
