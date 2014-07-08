/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.xml.RulesModuleProducer;

/**
 * Produces a <code>RulesModule</code> for parsing WebDynpro <code>.wdcomponent</code> files.
 * 
 * @author Dirk Weigenand
 */
public class WebDynproComponentRulesModuleProducer implements RulesModuleProducer {

    /**
     * {@inheritDoc}
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("Component").createObject().ofType(WebDynproComponent.class).then().setProperties().then()
                    .callMethod("setPackageName").withParamTypes(String.class).then().callParam().ofIndex(0).fromAttribute("package");
            }
        };
    }

}
