/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.java;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Container for a JavaDoc comment.
 * 
 * Provides methods for access to description and tags of a JavaDoc comment.
 * 
 * @author Dirk Weigenand
 */
public class JavaDocCommentContainer {
    /**
     * The description part of this Javadoc comment.
     */
    private String description = "";

    private List<TagDescriptor> tagDescriptors = new LinkedList<TagDescriptor>();

    /**
     * Create a new instance of a <code></code> with the given Javadoc comment
     * text.
     * 
     * Parses the comment into a description and the respective tags if any.
     * 
     * @param comment
     *            the raw JavaDoc comment.
     */
    public JavaDocCommentContainer(String comment) {

    }

    /**
     * @return the tagDescriptors
     */
    public Collection<TagDescriptor> getTagDescriptors() {
        return Collections.unmodifiableCollection(tagDescriptors);
    }

    /**
     * Returns a collection of {@link TagDescriptor} objects matching the given
     * tag name.
     * 
     * @param tagName
     *            the tag name the tag descriptors should be filter with.
     * @return the tag descriptors matching the given tag name or an empty list.
     */
    public Collection<TagDescriptor> getTagDescriptors(String tagName) {
        Collection<TagDescriptor> matches = new LinkedList<TagDescriptor>();

        for (TagDescriptor descriptor : this.getTagDescriptors()) {
            if (descriptor.getTag().equals(tagName)) {
                matches.add(descriptor);
            }
        }

        return matches;
    }

    /**
     * Returns the description part of this JavaDoc comment.
     * 
     * @return the description the description part of this JavaDoc comment.
     */
    public String getDescription() {
        return description;
    }
}
