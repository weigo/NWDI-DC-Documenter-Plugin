/**
 * 
 */
package org.arachna.netweaver.nwdi.dot4j;

import java.io.File;
import java.io.IOException;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.dc.types.DevelopmentConfigurationVisitor;
import org.arachna.netweaver.nwdi.documenter.filter.VendorFilter;

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
     * filter development components by vendor.
     */
    private final VendorFilter vendorFilter;

    /**
     * the base folder where reports shall be written to.
     */
    private final File baseDirectory;

    /**
     * Mapping for generated <code>.dot</code> files.
     */
    private final DiagramDescriptorContainer descriptorContainer = new DiagramDescriptorContainer();

    /**
     * Create a new instance of the dependency graph generator for the given
     * development configuration. Use the given baseDirectory as base for
     * output. Filter compartments using the given filter.
     * 
     * @param dcFactory
     *            registry for development components.
     * @param vendorFilter
     *            filter development components or compartments by vendors using
     *            this filter.
     * @param baseDirectory
     *            use base directory for graph generation.
     */
    public DependencyGraphGenerator(final DevelopmentComponentFactory dcFactory, final VendorFilter vendorFilter,
        final File baseDirectory) {
        this.dcFactory = dcFactory;
        this.vendorFilter = vendorFilter;
        this.baseDirectory = baseDirectory;
    }

    /**
     * Create a directory for the given file iff it does not exist. Throws a
     * <code>RuntimeException</code> if the directory could not be created.
     * 
     * @param baseFolder
     *            base folder where to generate images to
     * @param folderName
     *            absolute path to folder that should be created.
     * @return the newly created/existing directory
     */
    private File createDirectoryIffNotExists(final File baseFolder, final String folderName) {
        final File directory = new File(String.format("%s/%s", baseFolder.getAbsolutePath(), folderName));

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

            descriptorContainer.add(
                configuration,
                new DiagramDescriptor(dotFileWriter.write(new DevelopmentConfigurationDotFileGenerator(configuration,
                    vendorFilter), configuration.getName()), ""));
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final Compartment compartment) {
        if (!vendorFilter.accept(compartment)) {
            try {
                final File baseDir =
                    createDirectoryIffNotExists(baseDirectory, String.format("%s/images", compartment.getName()));
                final DotFileWriter dotFileWriter = new DotFileWriter(baseDir.getAbsolutePath());
                final String usingDCs =
                    dotFileWriter.write(
                        new UsingDevelopmentComponentsDotFileGenerator(compartment.getDevelopmentComponents(),
                            vendorFilter), compartment.getName() + "-usingDCs");
                final String usedDCs =
                    dotFileWriter.write(
                        new DevelopmentComponentDotFileGenerator(dcFactory, compartment.getDevelopmentComponents(),
                            vendorFilter), compartment.getName() + "-usedDCs");

                descriptorContainer.add(compartment, new DiagramDescriptor(usedDCs, usingDCs));
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void visit(final DevelopmentComponent component) {
        if (!vendorFilter.accept(component) && component.isNeedsRebuild()) {
            try {
                final File baseDir =
                    createDirectoryIffNotExists(baseDirectory,
                        String.format("%s/images", component.getCompartment().getName()));
                final DotFileWriter dotFileWriter = new DotFileWriter(baseDir.getAbsolutePath());

                final String componentName = component.getNormalizedName("~");
                final String usedDCs =
                    dotFileWriter.write(new DevelopmentComponentDotFileGenerator(dcFactory, component, vendorFilter),
                        String.format("%s-usedDCs", componentName));
                String usingDCs = "";

                if (!component.getUsingDevelopmentComponents().isEmpty()) {
                    usingDCs =
                        dotFileWriter.write(new UsingDevelopmentComponentsDotFileGenerator(component, vendorFilter),
                            String.format("%s-usingDCs", componentName));
                }

                descriptorContainer.add(component, new DiagramDescriptor(usedDCs, usingDCs));
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * @return the descriptorContainer
     */
    public DiagramDescriptorContainer getDescriptorContainer() {
        return descriptorContainer;
    }
}
