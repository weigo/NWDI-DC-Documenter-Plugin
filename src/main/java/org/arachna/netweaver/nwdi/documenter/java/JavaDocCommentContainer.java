/**
 *
 */
package org.arachna.netweaver.nwdi.documenter.java;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;

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

    private final List<TagDescriptor> tagDescriptors = new LinkedList<TagDescriptor>();

    private final Pattern pattern = Pattern.compile("\\s*\\{?(@\\p{Alpha}+)\\}?(\\s+.*)?");

    /**
     * Create a new instance of a <code></code> with the given Javadoc comment text.
     *
     * Parses the comment into a description and the respective tags if any.
     *
     * @param comment
     *            the raw JavaDoc comment.
     */
    public JavaDocCommentContainer(final String comment) {
        final ListIterator<String> lines = getLineIterator(comment);
        extractDescriptions(lines);
        extractTags(lines);
    }

    /**
     * @param comment
     * @return
     */
    protected ListIterator<String> getLineIterator(final String comment) {
        final List<String> lines = new LinkedList<String>();
        String content = comment;

        if (content.endsWith("*/")) {
            content = content.substring(0, content.length() - 2);
        }

        lines.addAll(Arrays.asList(content.split("\\r|\\n")));

        return lines.listIterator();
    }

    /**
     * @return the tagDescriptors
     */
    public Collection<TagDescriptor> getTagDescriptors() {
        return Collections.unmodifiableCollection(tagDescriptors);
    }

    /**
     * Returns a collection of {@link TagDescriptor} objects matching the given tag name.
     *
     * @param tagName
     *            the tag name the tag descriptors should be filter with.
     * @return the tag descriptors matching the given tag name or an empty list.
     */
    public Collection<TagDescriptor> getTagDescriptors(final String tagName) {
        final Collection<TagDescriptor> matches = new LinkedList<TagDescriptor>();

        for (final TagDescriptor descriptor : this.getTagDescriptors()) {
            if (descriptor.getTag().equalsIgnoreCase(tagName)) {
                matches.add(descriptor);
            }
        }

        return matches;
    }

    /**
     * Extract parameter tag descriptors.
     *
     * @return a list of parameter tags.
     */
    public Collection<ParamTagDescriptor> getParamTagDescriptors() {
        final Collection<ParamTagDescriptor> descriptors = new ArrayList<ParamTagDescriptor>();

        for (final TagDescriptor descriptor : this.getTagDescriptors()) {
            if (descriptor.getTag().equalsIgnoreCase("@param")) {
                descriptors.add((ParamTagDescriptor)descriptor);
            }
        }

        return descriptors;
    }

    /**
     * Returns the description part of this JavaDoc comment.
     *
     * @return the description the description part of this JavaDoc comment.
     */
    public String getDescription() {
        return description;
    }

    private ListIterator<String> extractDescriptions(final ListIterator<String> lines) {
        final StringBuilder description = new StringBuilder();
        String line;

        while (lines.hasNext()) {
            line = trimStarsFromStartOfStringAndTrimSpaces(lines.next());

            if (pattern.matcher(line).matches()) {
                lines.previous();
                break;
            }

            description.append(line).append('\n');
        }

        this.description = description.toString().trim();

        return lines;
    }

    private String trimStarsFromStartOfStringAndTrimSpaces(final String line) {
        String result = line;

        if (!result.isEmpty()) {
            final int indexOf = line.indexOf('*');

            if (indexOf > -1) {
                result = result.substring(indexOf + 1);
            }

            if (!result.isEmpty() && result.charAt(0) == '*') {
                result = result.substring(1);
            }
        }

        return result.trim();
    }

    private void extractTags(final ListIterator<String> lines) {
        String line;
        final StringBuilder description = new StringBuilder();
        String tagName = null;
        String paramName = null;

        while (lines.hasNext()) {
            line = trimStarsFromStartOfStringAndTrimSpaces(lines.next());

            final Matcher matcher = pattern.matcher(line);

            if (matcher.matches()) {
                if (tagName != null) {
                    lines.previous();
                    break;
                }

                tagName = matcher.group(1);
                description.append(StringUtils.trimToEmpty(matcher.group(2)));

                if ("@param".equals(tagName)) {
                    final int idx = description.indexOf(" ");

                    if (idx == -1) {
                        paramName = description.toString();
                        description.setLength(0);
                    }
                    else {
                        paramName = description.substring(0, idx);
                        description.delete(0, idx);
                    }
                }

                continue;
            }

            description.append(line).append('\n');
        }

        if (tagName != null) {
            if ("@param".equals(tagName)) {
                tagDescriptors.add(new ParamTagDescriptor(paramName, description.toString().trim()));
            }
            else {
                tagDescriptors.add(new TagDescriptor(tagName, description.toString().trim()));
            }
        }

        if (lines.hasNext()) {
            extractTags(lines);
        }
    }
}
