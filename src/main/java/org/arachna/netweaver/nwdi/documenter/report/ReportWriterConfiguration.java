/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.report;

import java.io.File;

/**
 * configuration object for report creation.
 *
 * @author G526521
 */
public final class ReportWriterConfiguration {
    /**
     * Location where output shall be written to.
     */
    private String outputLocation = System.getProperty("java.io.tmpdir");

    /**
     * location for images.
     */
    private String imagesLocation = "images";

    /**
     * location for CSS stylesheet used in reports.
     */
    private String cssLocation = "css";

    /**
     * format to be used for images.
     */
    private String imageFormat = "svg";

    /**
     * location of java script files.
     */
    private String jsLocation = "js";

    /**
     * @return the jsLocation
     */
    public String getJsLocation() {
        return jsLocation;
    }

    /**
     * set location of java script files.
     *
     * @param jsLocation
     *            the jsLocation to set
     */
    public void setJsLocation(final String jsLocation) {
        this.jsLocation = jsLocation;
    }

    /**
     * @return the outputLocation
     */
    public String getOutputLocation() {
        return outputLocation;
    }

    /**
     * @param outputLocation
     *            the outputLocation to set
     */
    public void setOutputLocation(final String outputLocation) {
        validateArgumentNotNull(outputLocation, "output location must not be null!");
        this.outputLocation = outputLocation;
    }

    /**
     * @return the imagesLocation
     */
    public String getImagesLocation() {
        return imagesLocation;
    }

    /**
     * @param imagesLocation
     *            the imagesLocation to set
     */
    public void setImagesLocation(final String imagesLocation) {
        validateArgumentNotNull(imagesLocation, "images location must not be null!");
        this.imagesLocation = imagesLocation;
    }

    /**
     * @return the cssLocation
     */
    public String getCssLocation() {
        return cssLocation;
    }

    /**
     * @param cssLocation
     *            the cssLocation to set
     */
    public void setCssLocation(final String cssLocation) {
        validateArgumentNotNull(cssLocation, "css style file location must not be null!");
        this.cssLocation = cssLocation;
    }

    /**
     * Helper for validating arguments in set methods.
     *
     * @param argument
     *            argument to be validated
     * @param message
     *            message to be used when validation fails
     */
    private void validateArgumentNotNull(final String argument, final String message) {
        if (argument == null) {
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * return image format to be used in report creation.
     *
     * @return image format to be used in report creation
     */
    public String getImageFormat() {
        return this.imageFormat;
    }

    /**
     * @param imageFormat
     *            the imageFormat to set
     */
    public void setImageFormat(final String imageFormat) {
        validateArgumentNotNull(imageFormat, "image format must not be null!");
        this.imageFormat = imageFormat;
    }

    /**
     * Return the location of 'index.html' for the output location.
     *
     * @return location of 'index.html' for the output location.
     */
    public String getIndexHtml() {
        return this.getOutputLocation() + File.separatorChar + "index.html";
    }
}
