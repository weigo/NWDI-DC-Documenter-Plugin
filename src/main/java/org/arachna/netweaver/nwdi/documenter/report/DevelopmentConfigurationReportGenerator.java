/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.context.Context;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentByNameComparator;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;

/**
 * Generator for documentation of a {@link DevelopmentConfiguration}.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationReportGenerator {
    /**
     * velocity template engine.
     */
    private final VelocityEngine velocityEngine;

    /**
     * I18N resource bundle.
     */
    private final ResourceBundle bundle;

    /**
     * Create documentation generator for development configurations using the given velocity template engine and resource bundle for I18N.
     * 
     * @param velocityEngine
     *            template engine
     * @param bundle
     *            I18N resource bundle
     */
    public DevelopmentConfigurationReportGenerator(final VelocityEngine velocityEngine,
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
    public void execute(final Writer writer, final DevelopmentConfiguration configuration,
        final Map<String, Object> additionalContext, Reader template) {
        final Context context = new VelocityContext();
        context.put("configuration", configuration);
        List<Compartment> compartments = new ArrayList<Compartment>(configuration.getCompartments(CompartmentState.Source));
        Collections.sort(compartments, new CompartmentByNameComparator());
        context.put("compartments", compartments);
        context.put("bundle", bundle);
        context.put("bundleHelper", new BundleHelper(bundle));
        context.put("trackName", configuration.getName());

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
