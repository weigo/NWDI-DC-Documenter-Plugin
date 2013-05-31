/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedHashMap;
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
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLTestCase;
import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.exceptions.XpathException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * Unit tests for {@link CompartmentReportGenerator}.
 * 
 * @author Dirk Weigenand
 */
public class CompartmentReportGeneratorTest extends XMLTestCase {
    /**
     * name of track to be documented.
     */
    private static final String TRACK_NAME = "DI1_EXAMPLE_D";

    /**
     * name of wiki space to publish to.
     */
    private static final String WIKI_SPACE = "NWENV";

    /**
     * instance under test.
     */
    private ReportGenerator generator;

    private Map<String, Object> additionalContext;

    private DevelopmentComponent component;

    /**
     * @throws java.lang.Exception
     */
    @Override
    @Before
    public void setUp() throws Exception {
        setUpNamespaceMapping();
        final DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();
        final AntHelper antHelper = new AntHelper("", dcFactory);
        final VelocityEngine engine = new VelocityEngine();
        final ReportGeneratorFactory generatorFactory =
            new ReportGeneratorFactory(antHelper, dcFactory, engine, ResourceBundle.getBundle(DocumentationBuilder.DC_REPORT_BUNDLE,
                Locale.GERMAN));
        final Compartment compartment = Compartment.create("example.com_EXAMPLE_SC_1", CompartmentState.Source);
        additionalContext = new HashMap<String, Object>();
        additionalContext.put("wikiSpace", WIKI_SPACE);
        additionalContext.put("trackName", TRACK_NAME);
        component = dcFactory.create("example.com", "example/dc");
        compartment.add(component);
        generator = generatorFactory.create(compartment);
    }

    /**
     * initialize XMLUitl with name space mapping used in pom (needed because default name space in XPath & XML with name spaces didn't work
     * out for testing).
     */
    private void setUpNamespaceMapping() {
        final Map<String, String> nameSpaceMappings = new LinkedHashMap<String, String>();
        nameSpaceMappings.put("", "http://docbook.org/ns/docbook");
        // nameSpaceMappings.put("xl", "http://www.w3.org/1999/xlink");
        nameSpaceMappings.put("xi", "http://www.w3.org/2001/XInclude");
        XMLUnit.setXpathNamespaceContext(new SimpleNamespaceContext(nameSpaceMappings));
    }

    /**
     * @throws java.lang.Exception
     */
    @Override
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
        assertXpathEvaluatesTo("1", "count(/chapter)");
        // final String link = String.format("[%s|%s:%s_%s]", component.getName(), WIKI_SPACE, TRACK_NAME,
        // component.getNormalizedName("_"));
        // assertTrue(result, result.contains(link));
    }

    private void assertXpathEvaluatesTo(final String expected, final String xPath) {
        try {
            assertXpathEvaluatesTo(expected, xPath, createDocument());
        }
        catch (final XpathException e) {
            fail(e);
        }
        catch (final SAXException e) {
            fail(e);
        }
        catch (final IOException e) {
            fail(e);
        }
    }

    private void fail(final Throwable t) {
        if (t.getCause() != null) {
            fail(t.getCause());
        }
        else {
            fail(t.getLocalizedMessage());
        }
    }

    /**
     * @return
     * @throws IOException
     */
    protected String createDocument() {
        final StringWriter writer = new StringWriter();
        generator.execute(writer, additionalContext, DocBookVelocityTemplate.Compartment.getTemplate());

        System.err.println(writer.toString());
        return writer.toString();
    }
}
