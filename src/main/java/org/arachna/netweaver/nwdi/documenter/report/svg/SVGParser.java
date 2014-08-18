/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.report.svg;

import java.io.Reader;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.xml.DigesterHelper;
import org.arachna.xml.RulesModuleProducer;

/**
 * Parser for SVG documents. Extracts general properties of an SVG {@see SVGProperties}.
 * 
 * @author Dirk Weigenand
 */
public final class SVGParser implements RulesModuleProducer {
    /**
     * Parse the given SVG and extract general properties (width, height).
     * 
     * @param reader
     *            the reader containing the SVG document.
     * @return {@link SVGProperties} read from the SVG document.
     */
    public SVGProperties parse(final Reader reader) {
        return new DigesterHelper<SVGProperties>(this).execute(reader);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {

            @Override
            protected void configure() {
                forPattern("svg").createObject().ofType(SVGProperties.class).then().setProperties();
            }
        };
    }
}
