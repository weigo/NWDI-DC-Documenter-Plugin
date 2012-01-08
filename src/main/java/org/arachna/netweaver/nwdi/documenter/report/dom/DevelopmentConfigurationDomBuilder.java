package org.arachna.netweaver.nwdi.documenter.report.dom;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.arachna.netweaver.dc.types.BuildVariant;
import org.arachna.netweaver.dc.types.Compartment;
import org.arachna.netweaver.dc.types.DevelopmentComponentFactory;
import org.arachna.netweaver.dc.types.DevelopmentConfiguration;
import org.arachna.xml.DomHelper;
import org.w3c.dom.Element;

/**
 * Builder for DOM of a development configuration.
 * 
 * @author Dirk Weigenand
 */
public final class DevelopmentConfigurationDomBuilder {
    /**
     * value attribute of build option.
     */
    private static final String VALUE = "value";

    /**
     * an option element of a build variant.
     */
    private static final String OPTION = "option";

    /**
     * a build-variant element.
     */
    private static final String BUILD_VARIANT = "build-variant";

    /**
     * a location attribute.
     */
    private static final String LOCATION = "location";

    /**
     * a caption attribute.
     */
    private static final String CAPTION = "caption";

    /**
     * a description attribute.
     */
    private static final String DESCRIPTION = "description";

    /**
     * a name attribute.
     */
    private static final String NAME = "name";

    /**
     * element for a development configuration.
     */
    private static final String DEVELOPMENT_CONFIGURATION = "development-configuration";

    /**
     * A helper object for creating DOMs.
     */
    private final DomHelper domHelper;

    /**
     * Registry for development components.
     */
    private final DevelopmentComponentFactory dcFactory;

    /**
     * Create a DOM builder for a developemtn configuration.
     * 
     * @param document
     *            Dokument welches zum Erzeugen der Elemente benötigt wird.
     */
    public DevelopmentConfigurationDomBuilder(final DomHelper domHelper, final DevelopmentComponentFactory dcFactory) {
        this.domHelper = domHelper;
        this.dcFactory = dcFactory;
    }

    /**
     * Erzeugt einen DOM-Baum für die übergebene Entwicklungskonfiguration.
     * 
     * @param configuration
     *            Entwicklungskonfiguration für die ein DOM-Baum erzeugt werden
     *            soll.
     * @return Wurzelelement des erzeugten DOM-Baumes
     * @throws IOException
     */
    public Element write(final DevelopmentConfiguration configuration) {
        final Element developmentConfiguration =
            domHelper.createElement(DEVELOPMENT_CONFIGURATION, new String[] { NAME, DESCRIPTION, CAPTION, LOCATION },
                new String[] { configuration.getName(), configuration.getDescription(), configuration.getCaption(),
                    configuration.getLocation() });
        final Element buildVariant = createBuildVariantElement(configuration.getBuildVariant());

        developmentConfiguration.appendChild(buildVariant);

        final CompartmentDomBuilder compartmentDOMCreator = new CompartmentDomBuilder(domHelper, dcFactory);
        final List<Compartment> compartments = new ArrayList<Compartment>();
        compartments.addAll(configuration.getCompartments());
        Collections.sort(compartments, new Comparator<Compartment>() {

            // @Override
            public int compare(final Compartment o1, final Compartment o2) {
                final int retval = o1.getVendor().compareTo(o2.getVendor());

                if (retval == 0) {
                    return o1.getName().compareTo(o2.getName());
                }

                if ("sap.com".equals(o1.getVendor())) {
                    return 1;
                }

                if ("sap.com".equals(o2.getVendor())) {
                    return -1;
                }

                return retval;
            }
        });

        for (final Compartment compartment : compartments) {
            developmentConfiguration.appendChild(compartmentDOMCreator.write(compartment));
        }

        return developmentConfiguration;
    }

    /**
     * Erzeugt ein Element für die Build-Variante und ihre Optionen.
     * 
     * @param variant
     *            BuildVariante für die eine DOM-Repräsentation erzeugt werden
     *            soll
     * @return DOM-Repräsentation der BuildVariante
     */
    private Element createBuildVariantElement(final BuildVariant variant) {
        final Element buildVariant = domHelper.createElement(BUILD_VARIANT, NAME, variant.getName());

        for (final String optionName : variant.getBuildOptionNames()) {
            buildVariant.appendChild(domHelper.createElement(OPTION, new String[] { NAME, VALUE }, new String[] {
                optionName, variant.getBuildOption(optionName) }));
        }

        return buildVariant;
    }
}
