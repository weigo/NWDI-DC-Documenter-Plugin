/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.java;

/**
 * Descriptor for JavaDoc tags.
 *
 * @author Dirk Weigenand
 */
public class TagDescriptor {
    /**
     * The tag (@param, ...)
     */
    private final String tag;

    /**
     * the tag description.
     */
    private final String description;

    /**
     * Create a new instance of a tag descriptor using the given tag name and description.
     *
     * @param tag
     *            the name of this tag (e.g. @param).
     * @param description
     *            the description for this tag.
     */
    public TagDescriptor(final String tag, final String description) {
        if (tag == null || !tag.startsWith("@") || tag.contains(" ")) {
            throw new IllegalArgumentException("tag name must not be null, start with @ and must not contain any spaces!");
        }

        this.tag = tag;
        this.description = description == null ? "" : description.replaceAll("\\n\\s+", "\\n");
    }

    /**
     * @return the tag
     */
    public String getTag() {
        return tag;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }
}
