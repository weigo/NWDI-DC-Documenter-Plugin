/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import java.util.regex.Pattern;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.ExecuteOn;
import org.apache.tools.ant.taskdefs.ExecuteOn.FileDirBoth;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.util.GlobPatternMapper;
import org.arachna.netweaver.dc.config.DevelopmentConfigurationReader;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.nwdi.documenter.report.DevelopmentConfigurationReportWriter;
import org.arachna.netweaver.nwdi.documenter.report.ReportWriterConfiguration;
import org.arachna.xml.XmlReaderHelper;
import org.xml.sax.SAXException;

/**
 * (HTML-)Documentation generator for a development configuration.
 * 
 * @author Dirk Weigenand
 */
public class ReportGenerator {
    /**
     * timeout for dependency diagram generation.
     */
    private static final int TIMEOUT = 1000 * 60 * 2;

    /**
     * Logger.
     */
    private final PrintStream logger;

    /**
     * development configuration to generator documentation for.
     */
    private final DevelopmentConfiguration config;

    /**
     * registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * target directory for generated documentation.
     */
    private final String outputLocation;

    /**
     * location of dot executable.
     */
    private final String dotExecutable;

    /**
     * regular expression for vendors to ignore during generation of
     * documentation.
     */
    private final Pattern ignorableVendorRgexp;

    /**
     * Create an report generator instance using the given configuration
     * parameters.
     * 
     * @param logger
     *            Logger
     * @param config
     *            development configuration
     * @param dcFactory
     *            DC registry
     * @param outputLocation
     *            target directory
     * @param dotExecutable
     *            path to dot executable
     * @param ignorableVendorRgexp
     *            vendors to ignore
     */
    ReportGenerator(final PrintStream logger, final DevelopmentConfiguration config,
        final DevelopmentComponentFactory dcFactory, final String outputLocation, final String dotExecutable,
        final Pattern ignorableVendorRgexp) {
        this.logger = logger;
        this.config = config;
        this.dcFactory = dcFactory;
        this.outputLocation = outputLocation;
        this.dotExecutable = dotExecutable;
        this.ignorableVendorRgexp = ignorableVendorRgexp;

    }

    /**
     * Generate documentation.
     * 
     * @return <code>true</code> when no error occurred during the generation
     *         process, <code>false</code> otherwise.
     */
    boolean execute() {
        boolean result = true;
        final ReportWriterConfiguration writerConfiguration = new ReportWriterConfiguration();
        writerConfiguration.setOutputLocation(outputLocation);
        final DevelopmentConfigurationReportWriter reportWriter =
            new DevelopmentConfigurationReportWriter(dcFactory, writerConfiguration,
                new DevelopmentComponentByVendorFilter(this.ignorableVendorRgexp));

        try {
            long start = System.currentTimeMillis();
            logger.append("Creating development configuration report...");
            reportWriter.write(config);
            duration(logger, start);

            start = System.currentTimeMillis();
            logger.append("Creating usage diagrams...");
            setUpApplyTask(outputLocation, dotExecutable).execute();
            duration(logger, start);
        }
        catch (final IOException e) {
            result = false;
            e.printStackTrace();
        }

        return result;
    }

    private void duration(final PrintStream logger, final long start) {
        logger.append(String.format("(%f sec.).\n", (System.currentTimeMillis() - start) / 1000f));
    }

    /**
     * @param outputLocation
     * @return
     */
    private ExecuteOn setUpApplyTask(final String outputLocation, final String dotExecutable) {
        final ExecuteOn task = new ExecuteOn();

        task.setExecutable(dotExecutable);
        final FileDirBoth fileDirBoth = new FileDirBoth();
        fileDirBoth.setValue("both");
        task.setType(fileDirBoth);
        task.add(createMapper());
        final File destDir = new File(outputLocation);
        task.setDir(destDir);
        task.setDest(destDir);
        task.addFileset(createFileSet(outputLocation));
        task.createArg().setValue("-Tsvg");
        task.createArg().setValue("-o");
        task.createTargetfile();
        task.createSrcfile();
        task.setVMLauncher(true);
        task.setParallel(false);
        task.setProject(new Project());
        task.setTimeout(Integer.valueOf(TIMEOUT));
        task.setVerbose(true);
        
        return task;
    }

    /**
     * @param outputLocation
     * @return
     */
    private FileSet createFileSet(final String outputLocation) {
        final FileSet dotFiles = new FileSet();
        dotFiles.setDir(new File(outputLocation));
        dotFiles.setIncludes("**/*.dot");

        return dotFiles;
    }

    /**
     * @return
     */
    private GlobPatternMapper createMapper() {
        final GlobPatternMapper mapper = new GlobPatternMapper();
        mapper.setFrom("*.dot");
        mapper.setTo("*.svg");

        return mapper;
    }

    public static void main(final String[] args) throws IOException, SAXException {
        final DevelopmentComponentFactory dcFactory = new DevelopmentComponentFactory();
        final DevelopmentConfigurationReader reader = new DevelopmentConfigurationReader(dcFactory);
        // new XmlReaderHelper(reader).parse(new
        // FileReader("/home/weigo/tmp/PN3_enviaMPr_D.xml"));
        new XmlReaderHelper(reader).parse(new FileReader("/home/weigo/tmp/PN3_enviaMPr_D-refactored.xml"));

        dcFactory.updateUsingDCs();

        for (final DevelopmentComponent component : dcFactory.getAll()) {
            if (!"sap.com".equals(component.getCompartment().getVendor())) {
                component.setNeedsRebuild(true);
            }
        }

        new ReportGenerator(System.err, reader.getDevelopmentConfiguration(), dcFactory, "/tmp/enviaMPR",
            "/usr/bin/dot", Pattern.compile("sap\\.com")).execute();
    }

}
