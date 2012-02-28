/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.DevelopmentConfigurationVisitor;
import org.arachna.netweaver.hudson.nwdi.IDevelopmentComponentFilter;
import org.arachna.netweaver.nwdi.documenter.CompartmentByVendorFilter;
import org.arachna.netweaver.nwdi.dot4j.DevelopmentComponentDotFileGenerator;
import org.arachna.netweaver.nwdi.dot4j.DevelopmentConfigurationDotFileGenerator;
import org.arachna.netweaver.nwdi.dot4j.DotFileWriter;
import org.arachna.netweaver.nwdi.dot4j.UsingDevelopmentComponentsDotFileGenerator;

/**
 * Generator for dependency graphs of software and development components of a
 * development configuration.
 * 
 * @author Dirk Weigenand
 */
public final class DependencyGraphGenerator implements DevelopmentConfigurationVisitor {
    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * filter compartments by vendor.
     */
    private final CompartmentByVendorFilter compartmentByVendorFilter;

    /**
     * filter development components by vendor.
     */
    private final IDevelopmentComponentFilter vendorFilter;

    /**
     * the base folder where reports shall be written to.
     */
    private final File baseDirectory;

    /**
     * Generated <code>.dot</code> files.
     */
    private final Collection<String> dotFiles = new HashSet<String>();

    /**
     * Create a new instance of the dependency graph generator for the given
     * development configuration. Use the given baseDirectory as base for
     * output. Filter compartments using the given filter.
     * 
     * @param dcFactory
     *            registry for development components.
     * @param compartmentByVendorFilter
     *            filter compartments using this filter.
     * @param vendorFilter
     *            filter development components by vendors using this filter.
     * @param baseDirectory
     *            use base directory for graph generation.
     */
    public DependencyGraphGenerator(final DevelopmentComponentFactory dcFactory,
        final CompartmentByVendorFilter compartmentByVendorFilter, final IDevelopmentComponentFilter vendorFilter,
        final File baseDirectory) {
        this.dcFactory = dcFactory;
        this.compartmentByVendorFilter = compartmentByVendorFilter;
        this.vendorFilter = vendorFilter;
        this.baseDirectory = baseDirectory;
    }

    /**
     * Create a directory for the given file iff it does not exist. Throws a
     * <code>RuntimeException</code> if the directory could not be created.
     * 
     * @param folderName
     *            absolute path to folder that should be created.
     */
    private File createDirectoryIffNotExists(final File baseFolder, final String folderName) {
        final File directory = new File(baseFolder.getAbsolutePath(), folderName);

        if (!directory.exists() && !directory.mkdirs()) {
            throw new RuntimeException("Could not create " + directory.getAbsolutePath() + "!");
        }

        return directory;
    }

    /**
     * Create dependency graph for the given development configuration.
     * 
     * @param configuration
     *            the development configuration to generate the dependency graph
     *            for.
     */
    @Override
    public void visit(final DevelopmentConfiguration configuration) {
        try {
            final File images = createDirectoryIffNotExists(baseDirectory, "images");
            final DotFileWriter dotFileWriter = new DotFileWriter(images.getAbsolutePath());

            dotFiles.add(dotFileWriter.write(new DevelopmentConfigurationDotFileGenerator(configuration,
                compartmentByVendorFilter), configuration.getName()));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(final Compartment compartment) {
        try {
            final File baseDir =
                createDirectoryIffNotExists(baseDirectory,
                    String.format("%s/images", compartment.getSoftwareComponent()));
            final DotFileWriter dotFileWriter = new DotFileWriter(baseDir.getAbsolutePath());
            dotFiles.add(dotFileWriter.write(
                new UsingDevelopmentComponentsDotFileGenerator(compartment.getDevelopmentComponents(), vendorFilter),
                compartment.getName() + "-usingDCs"));
            dotFiles.add(dotFileWriter.write(
                new DevelopmentComponentDotFileGenerator(dcFactory, compartment.getDevelopmentComponents(),
                    vendorFilter), compartment.getName() + "-usedDCs"));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void visit(final DevelopmentComponent component) {
        if (!vendorFilter.accept(component) && component.isNeedsRebuild()) {
            try {
                final File baseDir =
                    createDirectoryIffNotExists(baseDirectory, component.getCompartment().getSoftwareComponent()
                        + File.separatorChar + "images");
                final DotFileWriter dotFileWriter = new DotFileWriter(baseDir.getAbsolutePath());

                final String componentName = component.getVendor() + "~" + component.getName().replace("/", "~");
                dotFiles.add(dotFileWriter.write(new DevelopmentComponentDotFileGenerator(dcFactory, component,
                    vendorFilter), componentName));
                dotFiles.add(dotFileWriter.write(
                    new UsingDevelopmentComponentsDotFileGenerator(component, vendorFilter),
                    String.format("%s-usingDCs", componentName)));
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Returns the generated <code>.dot</code> files.
     * 
     * @return the generated <code>.dot</code> files.
     */
    public Collection<String> getDotFiles() {
        return dotFiles;
    }
}
