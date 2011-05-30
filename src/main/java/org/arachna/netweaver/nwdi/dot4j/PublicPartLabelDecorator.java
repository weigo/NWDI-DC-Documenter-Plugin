/**
 *
 */
package org.arachna.netweaver.nwdi.dot4j;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.arachna.netweaver.dc.types.PublicPartReference;

/**
 * Decorator for {@link org.arachna.netweaver.dc.types.PublicPartReference}.
 *
 * @author Dirk Weigenand
 */
final class PublicPartLabelDecorator {
    /**
     * public part references that should be decorated.
     */
    private final List<PublicPartReference> references = new ArrayList<PublicPartReference>();

    /**
     * development component that should be decorated.
     */
    private PublicPartReference currentReference;

    /**
     * Create an instance of a <code>PublicPartLabelDecorator</code> using the
     * given <code>DevelopmentComponent</code>.
     *
     * @param reference
     *            public part reference that should be decorated with a
     *            corresponding label.
     */
    PublicPartLabelDecorator(final PublicPartReference reference) {
        this.references.add(reference);
    }

    /**
     * Create an instance of a <code>PublicPartLabelDecorator</code> using the
     * given <code>DevelopmentComponent</code>.
     *
     * @param references
     *            collection of public part references that should be decorated
     *            with a corresponding label.
     */
    PublicPartLabelDecorator(final Collection<PublicPartReference> references) {
        this.references.addAll(references);
    }

    /**
     * Create the label that should be used in <code>.dot</code> file generators
     * for this public part reference.
     *
     * @return label that should be used in <code>.dot</code> file generators
     *         for this public part reference
     */
    String getLabel() {
        final StringBuffer label = new StringBuffer();

        Collections.sort(this.references, new PublicPartReferenceComparator());

        for (final PublicPartReference reference : this.references) {
            this.currentReference = reference;

            if (this.currentReference.isAtBuildTime() && currentReference.isAtRunTime()) {
                label.append(getLabelForPublicPartReference("bt&rt, "));
            }
            else if (this.currentReference.isAtBuildTime()) {
                label.append(getLabelForPublicPartReference("bt, "));
            }
            else if (this.currentReference.isAtRunTime()) {
                label.append(getLabelForPublicPartReference("rt, "));
            }
        }

        if (label.lastIndexOf(", ") == label.length() - 2) {
            label.setLength(label.length() - 2);
        }

        return label.toString();
    }

    /**
     * Create label for public part reference.
     *
     * @param type
     *            type of reference to generate for this public part reference.
     * @return label this public part reference should be decorated with
     */
    private StringBuffer getLabelForPublicPartReference(final String type) {
        final StringBuffer label = new StringBuffer();

        if (this.currentReference.getName().trim().length() > 0) {
            label.append(this.currentReference.getName()).append("@");
        }

        label.append(type);

        return label;
    }

    private static final class PublicPartReferenceComparator implements Comparator<PublicPartReference> {

        public int compare(final PublicPartReference first, final PublicPartReference second) {
            final int result = first.getName().compareTo(second.getName());

            return result < 0 ? -1 : (result > 0 ? 1 : 0);
        }
    }
}
