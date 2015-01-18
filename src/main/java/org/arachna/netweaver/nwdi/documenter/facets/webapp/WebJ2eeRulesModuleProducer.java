/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.xml.RulesModuleProducer;

/**
 * Producer for parsing rules for <code>web-j2ee-engine.xml</code>.
 *
 * @author Dirk Weigenand
 */
public class WebJ2eeRulesModuleProducer implements RulesModuleProducer {
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("web-j2ee-engine/security-role-map").createObject().ofType(SecurityRoleMapping.class).then().setNext("add");
                forPattern("web-j2ee-engine/security-role-map/role-name").setBeanProperty().withName("internalSecurityRole");
                forPattern("web-j2ee-engine/security-role-map/server-role-name").setBeanProperty().withName("umeRole");
            }
        };
    }
}
