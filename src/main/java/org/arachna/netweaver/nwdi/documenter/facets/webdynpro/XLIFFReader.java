/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.webdynpro;

import java.io.Reader;

import org.apache.commons.digester3.AbstractObjectCreationFactory;
import org.apache.commons.digester3.binder.AbstractRulesModule;
import org.apache.commons.digester3.binder.RulesModule;
import org.arachna.xml.DigesterHelper;
import org.arachna.xml.RulesModuleProducer;
import org.xml.sax.Attributes;

/**
 * Reader for XLIFF files. These files contain information for internationalization of Web Dynpro text resources.
 * 
 * @author Dirk Weigenand
 */
public final class XLIFFReader implements RulesModuleProducer {
    /**
     * 
     */
    private static final String XLIFF_BODY_GROUP = "xliff/file/body/group";
    /**
     * 
     */
    private static final String TRANS_UNIT = XLIFF_BODY_GROUP + "/trans-unit";
    /**
     * 
     */
    private static final String TRANS_UNIT_SOURCE = TRANS_UNIT + "/source";

    public Xliff execute(final Reader reader) {
        return new DigesterHelper<Xliff>(this).execute(reader);
    }

    /**
     * {@inheritdoc}
     */
    @Override
    public RulesModule getRulesModule() {
        return new AbstractRulesModule() {
            @Override
            protected void configure() {
                forPattern("xliff").createObject().ofType(Xliff.class);
                forPattern(XLIFF_BODY_GROUP).factoryCreate().usingFactory(new XliffGroupFactory()).then().setNext("addResourceType");
                forPattern(TRANS_UNIT).factoryCreate().usingFactory(new TranslationUnitFactory());
                forPattern(TRANS_UNIT_SOURCE).callMethod("setText").withParamTypes(String.class).usingElementBodyAsArgument().then()
                    .setNext("addTranslationUnit");
            }

            class XliffGroupFactory extends AbstractObjectCreationFactory<XliffGroup> {
                @Override
                public XliffGroup createObject(final Attributes attributes) throws Exception {
                    return new XliffGroup(attributes.getValue("restype"));
                }
            }

            class TranslationUnitFactory extends AbstractObjectCreationFactory<TranslationUnit> {
                @Override
                public TranslationUnit createObject(final Attributes attributes) throws Exception {
                    return new TranslationUnit(attributes.getValue("resname"));
                }
            }
        };
    }
}
