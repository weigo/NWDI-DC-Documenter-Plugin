/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.StringWriter;
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
public class DevelopmentComponentReportGeneratorTest extends AbstractXmlTestCase {
    private DevelopmentComponentReportGenerator generator;
    private DevelopmentComponentFactory dcFactory;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() {
        dcFactory = new DevelopmentComponentFactory();
        final VelocityEngine velocityEngine = new VelocityHelper().getVelocityEngine();
        final DevelopmentComponent component = dcFactory.create("example.com", "dc", DevelopmentComponentType.Java);
        generator =
            new DevelopmentComponentReportGenerator(new DocumentationFacetProviderFactory(new AntHelper("", dcFactory)), dcFactory,
                velocityEngine, ResourceBundle.getBundle(DocumentationBuilder.DC_REPORT_BUNDLE, Locale.GERMAN), component);
    }

    /**
     * Test method for
     * {@link DevelopmentComponentReportGenerator#execute(java.io.Writer, org.arachna.netweaver.dc.types.DevelopmentComponent, java.util.Map, java.io.Reader)}
     * .
     */
    @Test
    public final void testExecute() {
        // FIXME: add asserts for project url generation.
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createDocument() {
        final StringWriter writer = new StringWriter();

        final Map<String, Object> context = new HashMap<String, Object>();
        context.put(ContextPropertyName.ProjectUrl.getName(), "projectUrl");
        context.put(ContextPropertyName.WikiSpace.getName(), "NWENV");

        generator.execute(writer, context, DocBookVelocityTemplate.DevelopmentComponent.getTemplate());

        return writer.toString();
    }
}
