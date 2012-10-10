/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.io.IOException;
import java.io.Reader;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.Digester;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * Reader for <code>wdcomponent</code> files. Reads the referenced
 * views/window/controller configuration files and creates the respective
 * objects.
 * 
 * @author Dirk Weigenand
 */
public final class WebDynproComponentReader {
    /**
     * 
     */
    private static final String COMPONENT = "Component";
    /**
     * 
     */
    private static final String COMPONENT_INTERFACE = COMPONENT + "/Component.ComponentInterface";

    /**
     * Read a WebDynpro component using the given {@link Reader} instance.
     * 
     * @param reader
     *            <code>Reader</code> to read a WebDynpro component definition
     *            from (from a <code>.wdcomponent</code> file).
     * @return a {@link WebDynproComponent} set up wrt. to the content of the
     *         WebDynpro component descriptor read.
     */
    public WebDynproComponent read(final Reader reader) {
        try {
            final Digester digester = new Digester(XMLReaderFactory.createXMLReader());

            setUpCreateWebDynproComponent(digester);
            setUpComponentComponentInterfaceRules(digester);
            setUpComponentUsageRules(digester);

            setUpCoreReferenceRulesForType(digester, "Controllers");
            setUpCoreReferenceRulesForType(digester, "Views");
            setUpCoreReferenceRulesForType(digester, "Windows");

            setUpCoreReferenceRulesForParent(digester, "Component/Component.ComponentController",
                "setComponentController");

            return (WebDynproComponent)digester.parse(reader);
        }
        catch (final SAXException e) {
            throw new RuntimeException(e);
        }
        catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Set up rules for creating the {@link WebDynproComponent} instance and
     * initial properties.
     * 
     * @param digester
     *            {@link Digester} instance to add rules to.
     */
    private void setUpCreateWebDynproComponent(final Digester digester) {
        digester.addObjectCreate(COMPONENT, WebDynproComponent.class);
        digester.addSetProperties(COMPONENT);
        digester.addCallMethod(COMPONENT, "setPackageName", 1);
        digester.addCallParam(COMPONENT, 0, "package");
    }

    /**
     * Set up rules wrt. parsing the XML for a WD component interface reference.
     * 
     * @param digester
     *            {@link Digester} instance to add rules to.
     */
    private void setUpComponentComponentInterfaceRules(final Digester digester) {
        digester.addObjectCreate(COMPONENT_INTERFACE, ComponentInterface.class);
        digester.addSetNext(COMPONENT_INTERFACE, "setComponentInterface");
        setUpCoreReferenceRulesForParent(digester, COMPONENT_INTERFACE, "setCoreReference");
    }

    /**
     * Set up digester rules for reading WD component usages.
     * 
     * @param digester
     *            digester to add rules to.
     */
    private void setUpComponentUsageRules(final Digester digester) {
        final String componentUsage = "Component/Component.ComponentUsages/ComponentUsage";
        digester.addFactoryCreate(componentUsage, ComponentUsageFactory.class);
        digester.addSetNext(componentUsage, "add", ComponentUsage.class.getName());

        setUpCoreReferenceRulesForParent(digester, componentUsage + "/AbstractComponentUsage.UsedComponent",
            "setCoreReference");
        final String componentControllerUsage =
            componentUsage + "/ComponentUsage.ComponentControllerUsages/ComponentControllerUsage";

        digester.addObjectCreate(componentControllerUsage, ComponentControllerUsage.class);
        digester.addSetProperties(componentControllerUsage);
        digester.addSetNext(componentControllerUsage, "add", ComponentControllerUsage.class.getName());
        setUpCoreReferenceRulesForParent(digester, componentControllerUsage
            + "/ComponentControllerUsage.UsedComponentController", "setCoreReference");
    }

    /**
     * Set up rule for adding "Core.Reference"s to a parent using the given
     * parent pattern and method name.
     * 
     * @param digester
     *            digester instance to add rule to.
     * @param parent
     *            parent path pattern.
     * @param setNextMethodName
     *            method name to use to add core reference to parent.
     */
    protected void setUpCoreReferenceRulesForParent(final Digester digester, final String parent,
        final String setNextMethodName) {
        final String pattern = parent + "/Core.Reference";
        digester.addFactoryCreate(pattern, CoreReferenceFactory.class);
        digester.addSetNext(pattern, setNextMethodName);
    }

    /**
     * Set up rules for parsing core references for reference types
     * (Controllers, Views, Window). Take advantage of commonalities in XML
     * representation.
     * 
     * @param digester
     *            digester to add rules to.
     * @param type
     *            the type to parse (Controller, View, Window).
     */
    private void setUpCoreReferenceRulesForType(final Digester digester, final String type) {
        final String pattern = String.format("Component/Component.%s/Core.Reference", type);
        digester.addFactoryCreate(pattern, CoreReferenceFactory.class);
        digester.addSetNext(pattern, "add", CoreReference.class.getName());
    }

    /**
     * Implementation of an {@link ObjectCreationFactory} for
     * {@link CoreReference} objects. Set type of reference from enum.
     * 
     * @author Dirk Weigenand
     */
    public static final class CoreReferenceFactory extends AbstractObjectCreationFactory<CoreReference> {
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
     * Implementation of an {@link ObjectCreationFactory} for
     * {@link ComponentUsage} objects. Set type of life cycle control from enum.
     * 
     * @author Dirk Weigenand
     */
    public static final class ComponentUsageFactory extends AbstractObjectCreationFactory<ComponentUsage> {
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
