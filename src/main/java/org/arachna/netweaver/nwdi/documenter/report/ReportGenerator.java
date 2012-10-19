/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.Reader;
import java.io.Writer;
import java.util.Map;

/**
 * Schnittstelle für Reportgeneratoren.
 * 
 * @author Dirk Weigenand
 */
public interface ReportGenerator {
    /**
     * Create a report for an instance of the given type <code>T</code>. Write
     * the report into the given writer using the additional context provided.
     * Use the given template for the transformation.
     * 
     * @param writer
     *            {@link Writer} instance to write report into.
     * @param additionalContext
     *            additional context for report generation.
     * @param template
     *            velocity template to use for transformation.
     */
    void execute(final Writer writer, final Map<String, Object> additionalContext, final Reader template);
}
