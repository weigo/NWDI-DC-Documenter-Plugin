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
import org.apache.velocity.tools.generic.EscapeTool;

/**
 * Base class for report generators.
 * 
 * @author Dirk Weigenand
 */
public abstract class AbstractReportGenerator implements ReportGenerator {
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
        try {
            velocityEngine.evaluate(context, writer, "", template);
        }
        finally {
            try {
                writer.close();
            }
            catch (final IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
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
        context.put("bundle", getBundle());
        context.put("bundleHelper", new BundleHelper(getBundle()));
        context.put("escape", new EscapeTool());
        context.put("lang", getBundle().getLocale().getLanguage());

        for (final Map.Entry<String, Object> entry : additionalContext.entrySet()) {
            context.put(entry.getKey(), entry.getValue());
        }

        return context;
    }

    /**
     * @return the bundle
     */
    public ResourceBundle getBundle() {
        return bundle;
    }
}
