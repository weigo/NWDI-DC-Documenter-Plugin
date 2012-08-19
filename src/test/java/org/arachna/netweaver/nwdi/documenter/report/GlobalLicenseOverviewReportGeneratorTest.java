/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import org.apache.velocity.app.VelocityEngine;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentType;
import org.arachna.netweaver.nwdi.documenter.DocumentationBuilder;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacet;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProvider;
import org.arachna.netweaver.nwdi.documenter.facets.DocumentationFacetProviderFactory;
import org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseInspector.LicenseDescriptor;
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
     * instance under test.
     */
    private GlobalLicenseOverviewReportGenerator generator;
    private Collection<DocumentationFacetProvider<DevelopmentComponent>> providers;
    private LicenseInspector licenseInspector;

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
        // Compartment compartment = Compartment.create("example.com",
        // "EXAMPLE_SC", CompartmentState.Source, "");
        // Map<String, Object> additionalContext = new HashMap<String,
        // Object>();
        // additionalContext.put("wikiSpace", WIKI_SPACE);
        // additionalContext.put("trackName", TRACK_NAME);
        // component = dcFactory.create(, "example/dc");
        // compartment.add(component);
        // generator = generatorFactory.createCompartmentReportGenerator();

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
        fail("Not yet implemented"); // TODO
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

    // DocumentationFacet("externalLibraries", licenses)
}
