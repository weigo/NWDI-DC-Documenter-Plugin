/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Locale;
import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.nwdi.documenter.DocumentationBuilder;
import org.arachna.velocity.VelocityHelper;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 */
public class DevelopmentConfigurationReportGeneratorTest extends AbstractXmlTestCase {
    private DevelopmentConfigurationReportGenerator generator;

    /**
     * {@inheritDoc}
     */
    @Override
    @Before
    protected void setUp() throws Exception {
        super.setUp();
        final DevelopmentConfiguration config = new DevelopmentConfiguration("DI0_Example_D");
        final VelocityEngine velocityEngine = new VelocityHelper().getVelocityEngine();
        velocityEngine.addProperty("output.encoding", "UTF-8");
        final ResourceBundle bundle = ResourceBundle.getBundle(DocumentationBuilder.DC_REPORT_BUNDLE, Locale.GERMAN);
        generator = new DevelopmentConfigurationReportGenerator(velocityEngine, bundle, config);
        XMLUnit.getControlDocumentBuilderFactory().setNamespaceAware(false);
    }

    /**
     * Test method for {@link DevelopmentConfigurationReportGenerator#execute(java.io.Writer, java.util.Map, java.io.Reader)}.
     */
    @Test
    public final void testExecute() {
        // Ensure correct encoding.
        assertXpathEvaluatesTo("Überblick", "/book/chapter/title/text()");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected String createDocument() {
        final StringWriter writer = new StringWriter();
        generator.execute(writer, new HashMap<String, Object>(), DocBookVelocityTemplate.DevelopmentConfiguration.getTemplate());

        return writer.toString();
    }
}
