/**
 *
 */
package org.arachna.netweaver.nwdi.documenter;

import javax.xml.transform.ErrorListener;
import javax.xml.transform.Templates;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamSource;

/**
 * Enum for encupsulation of Confluence version specific converter configuration.
 *
 * @author Dirk Weigenand
 */
public enum ConfluenceVersion {
    /**
     * Stylesheet to convert docbook to confluence storage format before confluence 4.
     */
    V3(3, "confluence-pre4.xsl"),

    /**
     * Stylesheet to convert docbook to confluence storage format of confluence 4.
     */
    V4(4, "confluence-4-new-storage-format.xsl");

    /**
     * path to XSL stylesheets.
     */
    private static final String STYLESHEET_PATH_TEMPLATE = "/org/arachna/netweaver/nwdi/documenter/report/%s";

    /**
     * XSL-Stylesheet to use for conversion of docbook documents.
     */
    private final String template;

    /**
     * confluence major version to match to converter configuration.
     */
    private final int majorVersion;

    /**
     * Create converter configuration specific to given major confluence version with the given template.
     *
     * @param majorVersion
     *            confluence major version matching this converter configuration.
     * @param template
     *            template to use for conversion of docbook documents.
     */
    private ConfluenceVersion(final int majorVersion, final String template) {
        this.majorVersion = majorVersion;
        this.template = template;
    }

    /**
     * Determine converter configuration to use for the given confluence major version.
     *
     * @param majorVersion
     *            confluence major version
     * @return converter configuration matching the given confluence major version.
     */
    public static ConfluenceVersion fromConfluenceVersion(final int majorVersion) {
        return V3.majorVersion > majorVersion ? V3 : V4;
    }

    /**
     * Create a template for XSLT transformations for this confluence version.
     *
     * @return a {@link Templates} object using the XSLT stylesheet matching this confluence version.
     */
    public Templates createTemplate() {
        try {
            final TransformerFactory factory = TransformerFactory.newInstance();

            factory.setErrorListener(new ErrorListener() {
                @Override
                public void warning(final TransformerException exception) throws TransformerException {
                    throw new IllegalStateException(exception);
                }

                @Override
                public void error(final TransformerException exception) throws TransformerException {
                    throw new IllegalStateException(exception);
                }

                @Override
                public void fatalError(final TransformerException exception) throws TransformerException {
                    throw new IllegalStateException(exception);
                }
            });

            return factory.newTemplates(new StreamSource(this.getClass().getResourceAsStream(
                String.format(STYLESHEET_PATH_TEMPLATE, template))));
        }
        catch (final TransformerConfigurationException e) {
            throw new IllegalStateException(e);
        }
        catch (final TransformerFactoryConfigurationError e) {
            throw new IllegalStateException(e);
        }
    }
}
