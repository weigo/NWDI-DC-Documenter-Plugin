/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.io.Reader;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.Digester;
import org.apache.commons.digester3.ObjectCreationFactory;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.xml.DigesterHelper;
import org.arachna.xml.RulesModuleProducer;
import org.xml.sax.Attributes;

/**
 * Reader for <code>wdcomponent</code> files. Reads the referenced views/window/controller configuration files and creates the respective
 * objects.
 * 
 * @author Dirk Weigenand
 */
public final class WebDynproComponentReader implements RulesModuleProducer {
    /**
     * method name to use for addSetNext() in setUpCoreReferenceRulesForParent().
     */
    private static final String SET_CORE_REFERENCE = "setCoreReference";

    /**
     * pattern prefix for XML parsing rules.
     */
    private static final String COMPONENT = "Component";

    /**
     * pattern prefix for XML parsing rules.
     */
    private static final String COMPONENT_INTERFACE = COMPONENT + "/Component.ComponentInterface";

    /**
     * factory for CoreReference object creation during XML parsing.
     */
    private final CoreReferenceFactory coreReferenceFactory = new CoreReferenceFactory();

    /**
     * Read a WebDynpro component using the given {@link Reader} instance.
     * 
     * @param reader
     *            <code>Reader</code> to read a WebDynpro component definition from (from a <code>.wdcomponent</code> file).
     * @return a {@link WebDynproComponent} set up wrt. to the content of the WebDynpro component descriptor read.
     */
    public WebDynproComponent read(final Reader reader) {
        return new DigesterHelper<WebDynproComponent>(this).execute(reader);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                setUpCreateWebDynproComponent();
                setUpComponentComponentInterfaceRules();
                setUpComponentUsageRules();

                setUpCoreReferenceRulesForType("Controllers");
                setUpCoreReferenceRulesForType("Views");
                setUpCoreReferenceRulesForType("Windows");

                setUpCoreReferenceRulesForParent("Component/Component.ComponentController", "setComponentController");
            }

            /**
             * Set up rules for creating the {@link WebDynproComponent} instance and initial properties.
             */
            private void setUpCreateWebDynproComponent() {
                forPattern(COMPONENT).createObject().ofType(WebDynproComponent.class).then().setProperties().then()
                    .callMethod("setPackageName").withParamTypes(String.class).then().callParam().ofIndex(0).fromAttribute("package");
            }

            /**
             * Set up rules wrt. parsing the XML for a WD component interface reference.
             * 
             * @param digester
             *            {@link Digester} instance to add rules to.
             */
            private void setUpComponentComponentInterfaceRules() {
                forPattern(COMPONENT_INTERFACE).createObject().ofType(ComponentInterface.class).then().setNext("setComponentInterface");
                setUpCoreReferenceRulesForParent(COMPONENT_INTERFACE, SET_CORE_REFERENCE);
            }

            /**
             * Set up digester rules for reading WD component usages.
             * 
             * @param digester
             *            digester to add rules to.
             */
            private void setUpComponentUsageRules() {
                final String componentUsage = "Component/Component.ComponentUsages/ComponentUsage";
                forPattern(componentUsage).factoryCreate().usingFactory(new ComponentUsageFactory()).then().setNext("add");

                setUpCoreReferenceRulesForParent(componentUsage + "/AbstractComponentUsage.UsedComponent", SET_CORE_REFERENCE);
                final String componentControllerUsage =
                    componentUsage + "/ComponentUsage.ComponentControllerUsages/ComponentControllerUsage";

                forPattern(componentControllerUsage).createObject().ofType(ComponentControllerUsage.class).then().setProperties().then()
                    .setNext("add");
                setUpCoreReferenceRulesForParent(componentControllerUsage + "/ComponentControllerUsage.UsedComponentController",
                    SET_CORE_REFERENCE);
            }

            /**
             * Set up rule for adding "Core.Reference"s to a parent using the given parent pattern and method name.
             * 
             * @param digester
             *            digester instance to add rule to.
             * @param parent
             *            parent path pattern.
             * @param setNextMethodName
             *            method name to use to add core reference to parent.
             */
            protected void setUpCoreReferenceRulesForParent(final String parent, final String setNextMethodName) {
                forPattern(parent + "/Core.Reference").factoryCreate().usingFactory(coreReferenceFactory).then().setNext(setNextMethodName);
            }

            /**
             * Set up rules for parsing core references for reference types (Controllers, Views, Window). Take advantage of commonalities in
             * XML representation.
             * 
             * @param digester
             *            digester to add rules to.
             * @param type
             *            the type to parse (Controller, View, Window).
             */
            private void setUpCoreReferenceRulesForType(final String type) {
                final String pattern = String.format("Component/Component.%s/Core.Reference", type);
                forPattern(pattern).factoryCreate().usingFactory(new CoreReferenceFactory()).then().setNext("add");
            }
        };
    }

    /**
     * Implementation of an {@link ObjectCreationFactory} for {@link CoreReference} objects. Set type of reference from enum.
     * 
     * @author Dirk Weigenand
     */
    private static final class CoreReferenceFactory extends AbstractObjectCreationFactory<CoreReference> {
        /**
         * {@inheritDoc}
         */
        @Override
        public CoreReference createObject(final Attributes attributes) throws Exception {
            final CoreReference reference = new CoreReference();
            reference.setType(ReferenceType.fromString(attributes.getValue("type")));
            reference.setName(attributes.getValue("name"));
            reference.setPackageName(attributes.getValue("package"));

            return reference;
        }
    }

    /**
     * Implementation of an {@link ObjectCreationFactory} for {@link ComponentUsage} objects. Set type of life cycle control from enum.
     * 
     * @author Dirk Weigenand
     */
    private static final class ComponentUsageFactory extends AbstractObjectCreationFactory<ComponentUsage> {
        /**
         * {@inheritDoc}
         */
        @Override
        public ComponentUsage createObject(final Attributes attributes) throws Exception {
            final ComponentUsage usage = new ComponentUsage();
            usage.setLifeCycleControl(WDLifeCycleControl.fromString(attributes.getValue("lifecycleControl")));
            usage.setName(attributes.getValue("name"));

            return usage;
        }
    }

}
