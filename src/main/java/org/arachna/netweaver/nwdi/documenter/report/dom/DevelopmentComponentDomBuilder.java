package org.arachna.netweaver.nwdi.documenter.report.dom;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.PublicPart;
import org.arachna.netweaver.dc.types.PublicPartReference;
import org.arachna.xml.DomHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Builder for a DOM representation of a development component.
 * 
 * @author Dirk Weigenand
 */
public class DevelopmentComponentDomBuilder {
    /**
     * element 'package-folder'.
     */
    private static final String PACKAGE_FOLDER = "package-folder";

    /**
     * element 'sourceFolders'.
     */
    private static final String SOURCE_FOLDERS = "sourceFolders";

    /**
     * attribute 'at-run-time'.
     */
    private static final String AT_RUN_TIME = "at-run-time";

    /**
     * attribute 'at-build-time'.
     */
    private static final String AT_BUILD_TIME = "at-build-time";

    /**
     * element 'dependency'.
     */
    private static final String DEPENDENCY = "dependency";

    /**
     * element 'dependencies'.
     */
    private static final String DEPENDENCIES = "dependencies";

    /**
     * element 'pp-ref'.
     */
    private static final String PP_REF = "pp-ref";

    /**
     * attribute 'caption'.
     */
    private static final String CAPTION = "caption";

    /**
     * element ''.
     */
    private static final String PUBLIC_PART = "public-part";

    /**
     * element ''.
     */
    private static final String PUBLIC_PARTS = "public-parts";

    /**
     * element 'description'.
     */
    private static final String DESCRIPTION = "description";

    /**
     * attribute 'needsRebuild'.
     */
    private static final String NEEDS_REBUILD = "needsRebuild";

    /**
     * attribute 'type'.
     */
    private static final String TYPE = "type";

    /**
     * attribute 'vendor'.
     */
    private static final String VENDOR = "vendor";

    /**
     * attribute 'name'.
     */
    private static final String NAME = "name";

    /**
     * element 'development-component'.
     */
    private static final String DEVELOPMENT_COMPONENT = "development-component";

    /**
     * helper class for building a DOM.
     */
    private final DomHelper domHelper;

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Create an instance of a <code>DevelopmentComponentDOMCreator</code> using
     * the given {@link Document}.
     * 
     * @param document
     *            the <code>Document</code> to use building the DOM.
     * @param dcFactory
     *            registry for development components
     */
    public DevelopmentComponentDomBuilder(final DomHelper domHelper, DevelopmentComponentFactory dcFactory) {
        this.domHelper = domHelper;
        this.dcFactory = dcFactory;
    }

    /**
     * Add the DOM fragment for the given development component to the document
     * given in the constructor.
     * 
     * @param component
     *            to create a DOM fragment for.
     * @return the DOM fragment for the given development component.
     */
    public Element write(final DevelopmentComponent component) {
        final Element element =
            this.domHelper.createElement(
                DEVELOPMENT_COMPONENT,
                new String[] { NAME, VENDOR, TYPE, NEEDS_REBUILD },
                new String[] { component.getName(), component.getVendor(), component.getType().toString(),
                    Boolean.toString(component.isNeedsRebuild()) });
        element.appendChild(this.domHelper.createText(CAPTION, component.getCaption()));
        element.appendChild(this.domHelper.createText(DESCRIPTION, component.getDescription()));

        this.createUsedDevelopmentReferences(element, component);
        this.createSourceFolders(element, component);

        final Element publicParts = this.domHelper.createElement(PUBLIC_PARTS);

        for (final PublicPart pp : component.getPublicParts()) {
            final Element ppElement =
                this.domHelper.createElement(PUBLIC_PART, new String[] { NAME, CAPTION },
                    new String[] { pp.getPublicPart(), pp.getCaption() });
            ppElement.appendChild(this.domHelper.createText(DESCRIPTION, pp.getDescription()));
            publicParts.appendChild(ppElement);
        }

        element.appendChild(publicParts);

        return element;
    }

    /**
     * Create an element for used DC references.
     * 
     * @param parent
     *            the parent element the referenced DCs element should be added
     *            to.
     * @param component
     *            the component to create the referenced DCs element for.
     */
    private void createUsedDevelopmentReferences(final Element parent, final DevelopmentComponent component) {
        if (!component.getUsedDevelopmentComponents().isEmpty()) {
            final Element dependencies = this.domHelper.createElement(DEPENDENCIES);

            for (final PublicPartReference currentReference : component.getUsedDevelopmentComponents()) {
                String softwareComponent = getSoftwareComponent(currentReference);
                final Element reference =
                    this.domHelper.createElement(DEPENDENCY, new String[] { NAME, VENDOR, PP_REF, "compartment" },
                        new String[] { currentReference.getComponentName(), currentReference.getVendor(),
                            currentReference.getName(), softwareComponent });

                if (currentReference.isAtBuildTime()) {
                    reference.appendChild(this.domHelper.createElement(AT_BUILD_TIME));
                }

                if (currentReference.isAtRunTime()) {
                    reference.appendChild(this.domHelper.createElement(AT_RUN_TIME));
                }

                dependencies.appendChild(reference);
            }

            parent.appendChild(dependencies);
        }
    }

    /**
     * @param currentReference
     * @return
     */
    private String getSoftwareComponent(final PublicPartReference currentReference) {
        DevelopmentComponent referencedDC =
            this.dcFactory.get(currentReference.getVendor(), currentReference.getComponentName());

        if (referencedDC == null) {
            System.err.println(currentReference.toString() + " could not be found!");
            referencedDC = this.dcFactory.create(currentReference.getVendor(), currentReference.getComponentName());
        }

        Compartment compartment = referencedDC.getCompartment();

        return compartment == null ? "unknown" : compartment.getSoftwareComponent();
    }

    /**
     * Create an element for the source folders of the given development
     * component.
     * 
     * @param parent
     *            the parent element the 'source-folders' element should be
     *            added to.
     * @param component
     *            development component a 'source-folders' child should be added
     *            for.
     */
    private void createSourceFolders(final Element parent, final DevelopmentComponent component) {
        if (!component.getSourceFolders().isEmpty()) {
            final Element sourceFolders = this.domHelper.createElement(SOURCE_FOLDERS);

            for (final String folderName : component.getSourceFolders()) {
                sourceFolders.appendChild(this.domHelper.createText(PACKAGE_FOLDER, folderName));
            }

            parent.appendChild(sourceFolders);
        }
    }
}
