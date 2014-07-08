/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.xml.RulesModuleProducer;

/**
 * Producer of rules for parsing WebDynpro <code>.wdapplication</code> files.
 * 
 * @author Dirk Weigenand
 */
public class WebDynproApplicationRuleProducer implements RulesModuleProducer {
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("Application").createObject().ofType(WebDynproApplication.class).then().setProperties();
                forPattern("Application/Application.ApplicationProperties").createObject().ofType(ApplicationProperties.class).then()
                    .setNext("setProperties");
                forPattern("Application/Application.ApplicationProperties/ApplicationProperty").callMethod("addProperty")
                    .withParamTypes(String.class, String.class).then().callParam().ofIndex(0).fromAttribute("name").then().callParam()
                    .ofIndex(1).fromAttribute("value");
            }
        };
    }
}
