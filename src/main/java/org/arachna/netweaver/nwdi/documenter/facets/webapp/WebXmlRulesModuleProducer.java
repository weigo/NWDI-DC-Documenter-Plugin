/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.facets.webapp;

import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.xml.RulesModuleProducer;

/**
 * Producer for parsing rules for <code>web.xml</code>.
 *
 * @author Dirk Weigenand
 */
public class WebXmlRulesModuleProducer implements RulesModuleProducer {
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                createWebAppRule();
                createServletAndAddToWebApplicationRule();
                createServletNameRule();
                createServletClassRule();
                createServletMappingRule();
                createSecurityRoleRoleNameMappingRule();
                createSecurityConstraintRule();
                // forPattern("Application/Application.ApplicationProperties/ApplicationProperty").callMethod("addProperty")
                // .withParamTypes(String.class, String.class).then().callParam().ofIndex(0).fromAttribute("name").then().callParam()
                // .ofIndex(1).fromAttribute("value");
            }

            private void createSecurityConstraintRule() {
                forPattern("web-app/security-constraint").createObject().ofType(SecurityConstraintDescriptor.class).then().setNext("add");
                forPattern("web-app/security-constraint/web-resource-collection").createObject().ofType(WebResourceCollection.class).then()
                    .setNext("add");
                forPattern("web-app/security-constraint/web-resource-collection/web-resource-name").setBeanProperty().withName("name");
                forPattern("web-app/security-constraint/web-resource-collection/url-pattern").setBeanProperty().withName("urlPattern");

                forPattern("web-app/security-constraint/auth-constraint").createObject().ofType(AuthConstraint.class).then()
                .setNext("setAuthConstraint");
                forPattern("web-app/security-constraint/auth-constraint/description").setBeanProperty().withName("description");
                forPattern("web-app/security-constraint/auth-constraint/role-name").setBeanProperty().withName("roleName");
            }

            private void createSecurityRoleRoleNameMappingRule() {
                forPattern("web-app/security-role/role-name").callMethod("addSecurityRole").withParamTypes(String.class).then().callParam()
                    .ofIndex(0);
            }

            /**
             *
             */
            private void createServletMappingRule() {
                forPattern("web-app/servlet-mapping").callMethod("addServletMapping").withParamTypes(String.class, String.class);
                forPattern("web-app/servlet-mapping/servlet-name").callParam().ofIndex(0);
                forPattern("web-app/servlet-mapping/url-pattern").callParam().ofIndex(1);
            }

            /**
             *
             */
            private void createServletClassRule() {
                forPattern("web-app/servlet/servlet-class").setBeanProperty().withName("clazz");
            }

            /**
             *
             */
            private void createServletNameRule() {
                forPattern("web-app/servlet/servlet-name").setBeanProperty().withName("name");
            }

            /**
             *
             */
            private void createServletAndAddToWebApplicationRule() {
                forPattern("web-app/servlet").createObject().ofType(ServletDescriptor.class).then().setNext("add");
            }

            /**
             *
             */
            private void createWebAppRule() {
                forPattern("web-app").createObject().ofType(WebApplication.class);
            }
        };
    }
}