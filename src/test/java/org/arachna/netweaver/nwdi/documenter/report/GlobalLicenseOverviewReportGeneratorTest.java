/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.netweaver.nwdi.documenter.DocumentationBuilder;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.netweaver.nwdi.documenter.facets.librarydc.License;
import org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseDescriptor;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

/**
 * Unit tests for {@link GlobalLicenseOverviewReportGenerator}.
 * 
 * @author Dirk Weigenand
 */
public class GlobalLicenseOverviewReportGeneratorTest {
    /**
     * 
     */
    private static final String EXAMPLE_DC_1 = "example/dc";

    /**
     * 
     */
    private static final String VENDOR = "example.com";

    /**
     * instance under test.
     */
    private GlobalLicenseOverviewReportGenerator generator;
    private Collection<DocumentationFacetProvider<DevelopmentComponent>> providers;
    private LicenseInspector licenseInspector;

    private DevelopmentConfiguration config;

    private Map<String, Object> additionalContext;

    private DevelopmentComponentFactory dcFactory;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        licenseInspector = new LicenseInspector();
        providers = new ArrayList<DocumentationFacetProvider<DevelopmentComponent>>(Arrays.asList(licenseInspector));

        final DocumentationFacetProviderFactory facetProviderFactory =
            Mockito.mock(DocumentationFacetProviderFactory.class);
        Mockito.when(facetProviderFactory.getInstance(DevelopmentComponentType.ExternalLibrary)).thenReturn(providers);
        config = new DevelopmentConfiguration("DI0_Example_D");
        Compartment compartment = Compartment.create(VENDOR, "EXAMPLE_SC", CompartmentState.Source, "");
        config.add(compartment);
        additionalContext = new HashMap<String, Object>();
        additionalContext.put("wikiSpace", "NWENV");
        additionalContext.put("trackName", config.getName());

        dcFactory = new DevelopmentComponentFactory();
        compartment.add(dcFactory.create(VENDOR, EXAMPLE_DC_1, DevelopmentComponentType.ExternalLibrary));

        generator =
            new GlobalLicenseOverviewReportGenerator(facetProviderFactory, new VelocityEngine(),
                ResourceBundle.getBundle(DocumentationBuilder.DC_REPORT_BUNDLE, Locale.GERMAN));
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.report.GlobalLicenseOverviewReportGenerator#execute(java.io.Writer, org.arachna.netweaver.dc.types.DevelopmentConfiguration, java.util.Map, java.io.Reader)}
     * .
     */
    @Test
    public final void testExecute() {
        licenseInspector.add(dcFactory.get(VENDOR, EXAMPLE_DC_1),
            Arrays.asList(new LicenseDescriptor(License.Apache, "commons-1.2.3", "licenseText..."), new LicenseDescriptor(License.MIT,
                "stapler.jar", "licenseText..."), new LicenseDescriptor(License.Other,
                "junit-4.10.jar", "licenseText..."), new LicenseDescriptor(License.None,
                "xyz.jar", "licenseText...")));
        StringWriter result = new StringWriter();
        generator.execute(result, config, additionalContext, getTemplate());
        System.err.println(result.toString());
    }

    private class LicenseInspector implements DocumentationFacetProvider<DevelopmentComponent> {
        private final Map<DevelopmentComponent, Collection<LicenseDescriptor>> dc2LicenseDescriptors =
            new HashMap<DevelopmentComponent, Collection<LicenseDescriptor>>();

        /**
         * {@inheritDoc}
         */
        @Override
        public DocumentationFacet execute(final DevelopmentComponent component) {
            return new DocumentationFacet("externalLibraries", dc2LicenseDescriptors.get(component));
        }

        void add(final DevelopmentComponent component, final Collection<LicenseDescriptor> descriptors) {
            dc2LicenseDescriptors.put(component, descriptors);
        }
    }

    private Reader getTemplate() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            DevelopmentConfigurationConfluenceWikiGenerator.GLOBAL_LICENSE_OVERVIEW_WIKI_TEMPLATE));
    }
}
