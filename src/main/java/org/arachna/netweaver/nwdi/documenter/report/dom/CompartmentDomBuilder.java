/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report.dom;

import java.util.Collection;

import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.CompartmentState;
import org.arachna.netweaver.dc.types.DevelopmentComponent;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.xml.DomHelper;
import org.w3c.dom.Element;

/**
 * Builder for a DOM representation of a compartment.
 * 
 * @author Dirk Weigenand
 */
public final class CompartmentDomBuilder {

    /**
     * Element for contained development components.
     */
    private static final String DEVELOPMENT_COMPONENTS = "development-components";

    /**
     * Element for a compartment that is used by this compartment.
     */
    private static final String USED_COMPARTMENT = "used-compartment";

    /**
     * Element for compartments that are used by this compartment.
     */
    private static final String USED_COMPARTMENTS = "used-compartments";

    /**
     * Attribute value 'no'.
     */
    private static final String NO = "no";

    /**
     * Attribute value 'yes'.
     */
    private static final String YES = "yes";

    /**
     * Attribute name 'vendor'.
     */
    private static final String VENDOR = "vendor";

    /**
     * Attribute name for 'sc-name'.
     */
    private static final String SC_NAME = "sc-name";

    /**
     * Attribute name 'archive-state'.
     */
    private static final String ARCHIVE_STATE = "archive-state";

    /**
     * Attribute name 'name'.
     */
    private static final String NAME = "name";

    /**
     * Attribute name 'caption'.
     */
    private static final String CAPTION = "caption";

    /**
     * Element 'compartment'.
     */
    private static final String COMPARTMENT = "compartment";

    /**
     * helper object for creating a DOM.
     */
    private final DomHelper domHelper;

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Create a builder for a DOM representation of a compartment.
     * 
     * @param document
     *            Document zum Erzeugen der Elemente.
     */
    public CompartmentDomBuilder(final DomHelper domHelper, DevelopmentComponentFactory dcFactory) {
        this.domHelper = domHelper;
        this.dcFactory = dcFactory;
    }

    /**
     * Erzeugt DOM für das übergebene Compartment.
     * 
     * @param compartment
     *            Compartment für das eine DOM-Repräsentation ertzeugt werden
     *            soll.
     * @return 'compartment'-Element.
     */
    public Element write(final Compartment compartment) {
        final Element element =
            this.domHelper.createElement(
                COMPARTMENT,
                new String[] { CAPTION, NAME, ARCHIVE_STATE, SC_NAME, VENDOR },
                new String[] { compartment.getCaption(), compartment.getName(),
                    compartment.isArchiveState() ? YES : NO, compartment.getSoftwareComponent(),
                    compartment.getVendor() });
        this.appendUsedCompartments(compartment.getUsedCompartments(), element);
        this.appendDevelopmentComponents(compartment.getDevelopmentComponents(), element);

        return element;
    }

    /**
     * Fügt Elemente für die enthaltenen Entwicklungskomponenten hinzu.
     * 
     * @param components
     *            Entwicklungskomponenten
     * @param parent
     *            Elternelement
     */
    private void appendDevelopmentComponents(final Collection<DevelopmentComponent> components, final Element parent) {
        final Element developmentComponents =
            this.domHelper.createElement(CompartmentDomBuilder.DEVELOPMENT_COMPONENTS);
        final DevelopmentComponentDomBuilder dcDOMCreator =
            new DevelopmentComponentDomBuilder(this.domHelper, this.dcFactory);

        for (final DevelopmentComponent component : components) {
            developmentComponents.appendChild(dcDOMCreator.write(component));
        }

        parent.appendChild(developmentComponents);
    }

    /**
     * Fügt Elemente für die von diesem Compartment benutzten Compartments
     * hinzu.
     * 
     * @param compartments
     *            benutzte Compartments
     * @param element
     *            Elternelement
     */
    private void appendUsedCompartments(final Collection<Compartment> compartments, final Element element) {
        final Element usedCompartments = this.domHelper.createElement(USED_COMPARTMENTS);

        for (final Compartment usedCompartment : compartments) {
            usedCompartments.appendChild(this.domHelper.createElement(USED_COMPARTMENT, new String[] { NAME, VENDOR,
                SC_NAME, ARCHIVE_STATE }, new String[] { usedCompartment.getName(), usedCompartment.getVendor(),
                usedCompartment.getSoftwareComponent(),
                CompartmentState.Archive.equals(usedCompartment.getState()) ? "yes" : "no" }));
        }

        element.appendChild(usedCompartments);
    }
}
