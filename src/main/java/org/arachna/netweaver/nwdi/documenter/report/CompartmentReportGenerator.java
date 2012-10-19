/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.arachna.netweaver.dc.types.Compartment;

/**
 * Generator for a report of the properties of a {@link Compartment}.
 * 
 * @author Dirk Weigenand
 */
public final class CompartmentReportGenerator extends AbstractReportGenerator {
    /**
     * Compartment to generate report for.
     */
    private final Compartment compartment;

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
     * @param compartment
     *            compartment/software component to document.
     */
    public CompartmentReportGenerator(final VelocityEngine velocityEngine, final ResourceBundle bundle,
        final Compartment compartment) {
        super(velocityEngine, bundle);
        this.compartment = compartment;
    }

    /**
     * Generate documentation for the given development component into the given
     * writer object.
     * 
     * @param writer
     *            writer to generate documentation into.
     * @param additionalContext
     *            additional context attributes supplied externally
     * @param template
     *            a reader to supply the used template
     */
    public void execute(final Writer writer, final Map<String, Object> additionalContext, final Reader template) {
        final Context context = createContext(additionalContext);
        context.put("compartment", compartment);

        evaluate(context, writer, template);
    }
}
