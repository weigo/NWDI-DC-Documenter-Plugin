/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import static org.junit.Assert.assertTrue;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.ant.AntHelper;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.nwdi.documenter.DocumentationBuilder;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for {@link CompartmentReportGenerator}.
 * 
 * @author Dirk Weigenand (G526521)
 */
public class CompartmentReportGeneratorTest {
    /**
     * 
     */
    private static final String TRACK_NAME = "DI1_EXAMPLE_D";

    /**
     * 
     */
    private static final String WIKI_SPACE = "NWENV";

    /**
     * instance under test.
     */
    private ReportGenerator generator;

    private DevelopmentComponentFactory dcFactory;

    private ReportGeneratorFactory generatorFactory;

    private Map<String, Object> additionalContext;

    private Compartment compartment;

    private DevelopmentComponent component;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        dcFactory = new DevelopmentComponentFactory();
        final AntHelper antHelper = new AntHelper("", dcFactory);
        final VelocityEngine engine = new VelocityEngine();
        generatorFactory =
            new ReportGeneratorFactory(antHelper, dcFactory, engine, ResourceBundle.getBundle(
                DocumentationBuilder.DC_REPORT_BUNDLE, Locale.GERMAN));
        compartment =
            new Compartment("example.com_EXAMPLE_SC_1", CompartmentState.Source, "example.com", "EXAMPLE_SC",
                "EXAMPLE_SC");
        additionalContext = new HashMap<String, Object>();
        additionalContext.put("wikiSpace", WIKI_SPACE);
        additionalContext.put("trackName", TRACK_NAME);
        component = dcFactory.create("example.com", "example/dc");
        compartment.add(component);
        generator = generatorFactory.create(compartment);
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.report.CompartmentReportGenerator#execute(java.io.Writer, org.arachna.netweaver.dc.types.Compartment, java.util.Map, java.io.Reader)}
     * .
     */
    @Test
    public final void testExecute() {
        final StringWriter writer = new StringWriter();
        generator.execute(writer, additionalContext, DocBookVelocityTemplate.Compartment.getTemplate());
        final String result = writer.toString();
        final String link =
            String.format("[%s|%s:%s_%s]", component.getName(), WIKI_SPACE, TRACK_NAME,
                component.getNormalizedName("_"));
        assertTrue(result, result.contains(link));
    }

}
