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
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;

/**
 * Generator for a report of the properties of a {@link DevelopmentComponent}.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentComponentReportGenerator extends AbstractReportGenerator {
    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * factory for provider of documentation facets.
     */
    private final DocumentationFacetProviderFactory documentationFacetProviderFactory;

    /**
     * Create a <code>DevelopmentComponentReportGenerator</code> using the given
     * {@link DevelopmentComponentFactory}, {@link VelocityEngine} , velocity
     * template and resource bundle.
     * 
     * The given {@link ResourceBundle} is used for internationalization.
     * 
     * @param documentationFacetProviderFactory
     *            factory for provider of documentation facets
     * @param dcFactory
     *            used in template to resolve public part references into
     *            development components.
     * @param velocityEngine
     *            VelocityEngine used to transform template.
     * @param bundle
     *            the ResourceBundle used for I18N.
     */
    public DevelopmentComponentReportGenerator(
        final DocumentationFacetProviderFactory documentationFacetProviderFactory,
        final DevelopmentComponentFactory dcFactory, final VelocityEngine velocityEngine, final ResourceBundle bundle) {
        super(velocityEngine, bundle);
        this.documentationFacetProviderFactory = documentationFacetProviderFactory;
        this.dcFactory = dcFactory;
    }

    /**
     * Generate documentation for the given development component into the given
     * writer object.
     * 
     * @param writer
     *            writer to generate documentation into.
     * @param component
     *            development component to document.
     * @param additionalContext
     *            additional context attributes supplied externally
     * @param template
     *            a Reader for supplying the velocity template used for
     *            generation of documentation.
     */
    public void execute(final Writer writer, final DevelopmentComponent component,
        final Map<String, Object> additionalContext, final Reader template) {
        final Context context = createContext(additionalContext);
        context.put("component", component);
        context.put("dcFactory", dcFactory);

        if (component.getType().canContainJavaSources()) {
            context.put(
                "javaDocUrl",
                String.format("%sws/javadoc/%s/index.html",
                    additionalContext.get(ContextPropertyName.ProjectUrl.getName()), component.getNormalizedName("~")));
        }

        for (final DocumentationFacetProvider<DevelopmentComponent> provider : documentationFacetProviderFactory
            .getInstance(component.getType())) {
            final DocumentationFacet facet = provider.execute(component);
            context.put(facet.getName(), facet.getContent());
        }

        evaluate(context, writer, template);
    }
}
