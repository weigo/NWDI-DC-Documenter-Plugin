/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.apache.velocity.exception.MethodInvocationException;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.arachna.netweaver.dc.config.DevelopmentConfigurationReader;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.velocity.VelocityHelper;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.SAXException;

/**
 * Generator for a report of the properties of a {@link DevelopmentComponent}.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentReportGenerator {
    /**
     * velocity engine to generate a report for a {@link DevelopmentComponent}.
     */
    private final VelocityEngine velocityEngine;

    /**
     * template name for rendering a development components properties into a
     * legible report.
     */
    private final String template;

    /**
     * {@link ResourceBundle} for internationalization of reports.
     */
    private final ResourceBundle bundle;

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    private final Locale locale;

    /**
     * 
     */
    public DevelopmentComponentReportGenerator(final DevelopmentComponentFactory dcFactory,
        final VelocityEngine velocityEngine, final String template, final ResourceBundle bundle, final Locale locale) {
        this.dcFactory = dcFactory;
        this.velocityEngine = velocityEngine;
        this.template = template;
        this.bundle = bundle;
        this.locale = locale;
    }

    /**
     * 
     * @param writer
     * @param component
     * @throws IOException
     * @throws ResourceNotFoundException
     * @throws MethodInvocationException
     * @throws ParseErrorException
     */
    public void execute(final Writer writer, final DevelopmentComponent component) throws ParseErrorException,
        MethodInvocationException, ResourceNotFoundException, IOException {
        final Context context = new VelocityContext();
        context.put("component", component);
        context.put("bundle", bundle);
        context.put("usedDCs", component.getUsedDevelopmentComponents());
        context.put("bundleHelper", new BundleHelper(bundle, locale));
        context.put("dcFactory", dcFactory);
        velocityEngine.evaluate(context, writer, "", getTemplateReader());
        writer.flush();
    }

    public class BundleHelper {
        private final ResourceBundle bundle;
        private final Locale locale;

        BundleHelper(final ResourceBundle bundle, final Locale locale) {
            this.bundle = bundle;
            this.locale = locale;
        }

        public String render(String key, String arg) {
            String message = key;

            try {
                message = bundle.getString(key);
            }
            catch (MissingResourceException mre) {
                // use key as message format...
            }

            return String.format(locale, message, arg);
        }

        public boolean isNull(Object value) {
            return value == null;
        }
    }

    /**
     * 
     */
    protected InputStreamReader getTemplateReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream(template));
    }

    public static void main(final String[] args) throws IOException, SAXException {
        final VelocityEngine velocityEngine = new VelocityHelper(System.err).getVelocityEngine();
        final DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();

        final DevelopmentComponentReportGenerator generator =
            new DevelopmentComponentReportGenerator(dcFactory, velocityEngine,
                "/org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentHtmlTemplate.vm",
                ResourceBundle.getBundle("org/arachna/netweaver/nwdi/documenter/report/DevelopmentComponentReport",
                    Locale.GERMAN), Locale.GERMAN);

        final DevelopmentConfigurationReader reader = new DevelopmentConfigurationReader(dcFactory);
        final String workspace = "/tmp";
        new XmlReaderHelper(reader).parse(new FileReader(workspace + "/DevelopmentConfiguration.xml"));
        dcFactory.updateUsingDCs();

        final DevelopmentComponent component = dcFactory.get("example.com", "lib/webdynpro/helper/impl");
        FileWriter writer = new FileWriter(String.format("/tmp/%s.html", component.getName().replace('/', '~')));
        generator.execute(writer, component);
        writer.close();
    }
}
