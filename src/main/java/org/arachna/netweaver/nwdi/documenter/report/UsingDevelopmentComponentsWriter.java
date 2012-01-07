/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.IOException;
import java.util.List;

import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.nwdi.dot4j.DotFileWriter;
import org.arachna.netweaver.nwdi.dot4j.UsingDevelopmentComponentsDotFileGenerator;

/**
 * Diagram writer for usage relations of development components.
 * 
 * @author Dirk Weigenand
 */
public final class UsingDevelopmentComponentsWriter {
    /**
     * Configuration for this writer.
     */
    private final ReportWriterConfiguration writerConfiguration;

    /**
     * Create an instance of {@link UsingDevelopmentComponentsWriter}.
     * 
     * @param dcFactory
     *            registry for development components.
     * @param writerConfiguration
     *            configuration used creating the diagrams
     */
    public UsingDevelopmentComponentsWriter(final DevelopmentComponentFactory dcFactory,
        final ReportWriterConfiguration writerConfiguration) {
        this.writerConfiguration = writerConfiguration;
    }

    /**
     * Creates diagrams that show which development components are using the
     * development components given in the <code>components</code> parameter.
     * 
     * @param components
     *            development components for which usage diagrams shall be
     *            created.
     * @throws IOException
     *             when an error occurs writing the diagrams
     */
    public void write(final List<DevelopmentComponent> components) throws IOException {
        for (final DevelopmentComponent component : components) {
            final DotFileWriter dotWriter = new DotFileWriter(this.writerConfiguration.getOutputLocation());
            dotWriter.write(new UsingDevelopmentComponentsDotFileGenerator(component), getComponentName(component));
        }
    }

    /**
     * Create file name to be used when writing the usage diagram.
     * 
     * @param component
     *            development component to create the file name from
     * @return Create file name to be used when writing the usage diagram.
     */
    private String getComponentName(final DevelopmentComponent component) {
        return String.format("%s~%s-usingDCs", component.getVendor(), component.getName().replaceAll("/", "~"));
    }
}
