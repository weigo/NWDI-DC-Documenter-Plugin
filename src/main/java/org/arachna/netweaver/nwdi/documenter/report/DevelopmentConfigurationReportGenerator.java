/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.Reader;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;

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
public final class DevelopmentConfigurationReportGenerator extends AbstractReportGenerator {
    /**
     * development configuration to create report for.
     */
    private final DevelopmentConfiguration configuration;

    /**
     * Create documentation generator for development configurations using the
     * given velocity template engine and resource bundle for I18N.
     * 
     * @param velocityEngine
     *            template engine
     * @param bundle
     *            I18N resource bundle
     * @param configuration
     *            development configuration to create report for.
     */
    public DevelopmentConfigurationReportGenerator(final VelocityEngine velocityEngine, final ResourceBundle bundle,
        final DevelopmentConfiguration configuration) {
        super(velocityEngine, bundle);
        this.configuration = configuration;
    }

    /**
     * Generate documentation for the given development component into the given
     * writer object.
     * 
     * @param writer
     *            writer to generate documentation into.
     * @param additionalContext
     *            additional context attributes supplied externally.
     * @param template
     *            for velocity template.
     */
    public void execute(final Writer writer, final Map<String, Object> additionalContext, final Reader template) {
        final Context context = createContext(additionalContext);
        context.put("configuration", configuration);

        final List<Compartment> compartments =
            new ArrayList<Compartment>(configuration.getCompartments(CompartmentState.Source));
        Collections.sort(compartments, new CompartmentByNameComparator());
        context.put("compartments", compartments);
        context.put("trackName", configuration.getName());

        evaluate(context, writer, template);
    }
}
