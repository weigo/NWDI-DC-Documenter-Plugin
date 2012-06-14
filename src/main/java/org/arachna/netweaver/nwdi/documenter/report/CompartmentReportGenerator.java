/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;

/**
 * Generator for a report of the properties of a {@link DevelopmentComponent}.
 * 
 * @author Dirk Weigenand
 */
public final class CompartmentReportGenerator {
    /**
     * velocity engine to generate a report for a {@link DevelopmentComponent}.
     */
    private final VelocityEngine velocityEngine;

    /**
     * {@link ResourceBundle} for internationalization of reports.
     */
    private final ResourceBundle bundle;

    /**
     * Create a <code>DevelopmentComponentReportGenerator</code> using the given {@link DevelopmentComponentFactory}, {@link VelocityEngine}
     * , velocity template and resource bundle.
     * 
     * The given {@link ResourceBundle} is used for internationalization.
     * 
     * @param velocityEngine
     *            VelocityEngine used to transform template.
     * @param bundle
     *            the ResourceBundle used for I18N.
     */
    public CompartmentReportGenerator(final VelocityEngine velocityEngine,
        final ResourceBundle bundle) {
        this.velocityEngine = velocityEngine;
        this.bundle = bundle;
    }

    /**
     * Generate documentation for the given development component into the given writer object.
     * 
     * @param writer
     *            writer to generate documentation into.
     * @param component
     *            development component to document.
     * @param additionalContext
     *            additional context attributes supplied externally
     */
    public void execute(final Writer writer, final Compartment compartment,
        final Map<String, Object> additionalContext, Reader template) {
        final Context context = new VelocityContext();
        context.put("compartment", compartment);
        context.put("bundle", bundle);
        context.put("bundleHelper", new BundleHelper(bundle));

        for (final Map.Entry<String, Object> entry : additionalContext.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }

        velocityEngine.evaluate(context, writer, "", template);

        try {
            writer.close();
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }
}
