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

/**
 * Base class for report generators.
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractReportGenerator {
    /**
     * velocity template engine.
     */
    private final VelocityEngine velocityEngine;

    /**
     * I18N resource bundle.
     */
    private final ResourceBundle bundle;

    /**
     * Create a <code>CompartmentReportGenerator</code> using the given
     * {@link VelocityEngine}, and resource bundle.
     * 
     * The given {@link ResourceBundle} is used for internationalization.
     * 
     * @param velocityEngine
     *            VelocityEngine used to transform template.
     * @param bundle
     *            the ResourceBundle used for I18N.
     */
    public AbstractReportGenerator(final VelocityEngine velocityEngine, final ResourceBundle bundle) {
        this.velocityEngine = velocityEngine;
        this.bundle = bundle;
    }

    /**
     * Evaluate the given velocity context using the template for the
     * transformation. The result will be written into the given {@link Writer}
     * instance.
     * 
     * @param context
     *            velocity context to use for evaluating the template.
     * @param writer
     *            writer instance to write result into.
     * @param template
     *            velocity template to use for transformation.
     */
    protected final void evaluate(final Context context, final Writer writer, final Reader template) {
        velocityEngine.evaluate(context, writer, "", template);

        try {
            writer.close();
        }
        catch (final IOException ioe) {
            throw new RuntimeException(ioe);
        }
    }

    /**
     * Create a velocity context pre filled with bundle, bundle helper and the
     * given additional context.
     * 
     * @param additionalContext
     *            additional context information.
     * @return pre filled velocity context
     */
    protected final Context createContext(final Map<String, Object> additionalContext) {
        final Context context = new VelocityContext();
        context.put("bundle", bundle);
        context.put("bundleHelper", new BundleHelper(bundle));

        for (final Map.Entry<String, Object> entry : additionalContext.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }

        return context;
    }
}
