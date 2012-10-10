/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.collection.IsEmptyCollection.empty;

import java.io.InputStreamReader;
import java.util.Collection;
import java.util.Iterator;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 * 
 */
public final class WebDynproComponentReaderTest {
    /**
     * 
     */
    private static final String EXPECTED_PACKAGE = "org.example.wd.comp";
    /**
     * result of reading an example WD component.
     */
    private WebDynproComponent component;

    /**
     * Read example WD Component from classpath and assign read
     * WebDynproComponent for later perusal.
     */
    @Before
    public void setUp() {
        final WebDynproComponentReader reader = new WebDynproComponentReader();
        component =
            reader.read(new InputStreamReader(getClass().getResourceAsStream(
                "/org/arachna/netweaver/nwdi/documenter/webdynpro/ExampleComp.wdcomponent")));
    }

    /**
     */
    @After
    public void tearDown() {
        component = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.webdynpro.WebDynproComponentReader#read(java.io.Reader)}
     * .
     */
    @Test
    public void verifySetUpCreateWebDynproComponentRules() {
        assertThat(component.getName(), equalTo("ExampleComp"));
        assertThat(component.getPackageName(), equalTo(EXPECTED_PACKAGE));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.webdynpro.WebDynproComponentReader#read(java.io.Reader)}
     * .
     */
    @Test
    public void verifySetUpComponentInterfaceRules() {
        final ComponentInterface componentInterface = component.getComponentInterface();
        assertThat(componentInterface, notNullValue());
        assertExpectedCoreReferenceProperties(componentInterface.getCoreReference(), "ExampleCompInterface",
            EXPECTED_PACKAGE, ReferenceType.ComponentInterfaceImplementation);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.webdynpro.WebDynproComponentReader#read(java.io.Reader)}
     * .
     */
    @Test
    public void verifySetUpComponentUsageRules() {
        final Collection<ComponentUsage> componentUsages = component.getUsedComponents();

        assertThat(componentUsages, notNullValue());
        assertThat(componentUsages, not(empty()));

        final ComponentUsage usage = componentUsages.iterator().next();
        assertThat(usage.getName(), equalTo("ExampleComp"));
        assertThat(usage.getLifeCycleControl(), equalTo(WDLifeCycleControl.CreateOnDemand));

        final CoreReference coreReference = usage.getCoreReference();
        assertExpectedCoreReferenceProperties(coreReference, "ExampleCompInterface", EXPECTED_PACKAGE,
            ReferenceType.ComponentInterfaceImplementation);

        final Collection<ComponentControllerUsage> usedControllers = usage.getUsedControllers();
        assertThat(usedControllers, notNullValue());

        final ComponentControllerUsage controllerUsage = usedControllers.iterator().next();
        assertThat(controllerUsage.getName(), equalTo("Interface"));
        assertExpectedCoreReferenceProperties(controllerUsage.getCoreReference(), "ExampleCompInterface",
            EXPECTED_PACKAGE, ReferenceType.Controller);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.webdynpro.WebDynproComponentReader#read(java.io.Reader)}
     * .
     */
    @Test
    public void verifySetUpCoreReferenceRulesForTypeController() {
        final Iterator<CoreReference> controllers = component.getControllers().iterator();
        assertExpectedCoreReferenceProperties(controllers.next(), "ExampleViewCont", EXPECTED_PACKAGE,
            ReferenceType.Controller);
        assertExpectedCoreReferenceProperties(controllers.next(), "ExampleComp", EXPECTED_PACKAGE,
            ReferenceType.Controller);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.webdynpro.WebDynproComponentReader#read(java.io.Reader)}
     * .
     */
    @Test
    public void verifySetUpCoreReferenceRulesForTypeViews() {
        final Iterator<CoreReference> views = component.getViews().iterator();
        assertExpectedCoreReferenceProperties(views.next(), "ExampleView", EXPECTED_PACKAGE, ReferenceType.View);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.webdynpro.WebDynproComponentReader#read(java.io.Reader)}
     * .
     */
    @Test
    public void verifySetUpCoreReferenceRulesForTypeWindows() {
        final Iterator<CoreReference> windows = component.getWindows().iterator();
        assertExpectedCoreReferenceProperties(windows.next(), "ExampleWindow", EXPECTED_PACKAGE, ReferenceType.Window);
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.webdynpro.WebDynproComponentReader#read(java.io.Reader)}
     * .
     */
    @Test
    public void verifySetUpCoreReferenceRulesForComponentController() {
        assertExpectedCoreReferenceProperties(component.getComponentController(), "ExampleComp", EXPECTED_PACKAGE,
            ReferenceType.Controller);
    }

    /**
     * Assert that the given core reference instance matches the given property
     * values.
     * 
     * @param coreReference
     *            core reference instance to validate.
     * @param name
     *            expected name.
     * @param packageName
     *            expected package name.
     * @param referenceType
     *            expected reference type.
     */
    protected void assertExpectedCoreReferenceProperties(final CoreReference coreReference, final String name,
        final String packageName, final ReferenceType referenceType) {
        assertThat(coreReference, notNullValue());
        assertThat(coreReference.getName(), equalTo(name));
        assertThat(coreReference.getPackageName(), equalTo(packageName));
        assertThat(coreReference.getType(), equalTo(referenceType));
    }
}
