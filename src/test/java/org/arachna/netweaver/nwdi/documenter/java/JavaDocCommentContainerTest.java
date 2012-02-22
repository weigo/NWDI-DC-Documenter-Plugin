/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.java;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

/**
 * @author Dirk Weigenand
 * 
 */
public class JavaDocCommentContainerTest {
    /**
     * Container instance under test.
     */
    private JavaDocCommentContainer container;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
    }

    @Test
    public final void testParseEmptyCommentHasNoDescription() {
        container = new JavaDocCommentContainer("");

        assertThat(container.getDescription(), equalTo(""));
    }

    @Test
    public final void testParseEmptyCommentHasNoTagDescriptors() {
        container = new JavaDocCommentContainer("");

        assertThat(container.getTagDescriptors(), hasSize(0));
    }

    @Test
    public final void testParseEmptyCommentWithStarsAndSlashesOnly() {
        container = new JavaDocCommentContainer("/**\n *\n */");

        assertThat(container.getDescription(), equalTo(""));
    }

    @Test
    public final void testParseCommentWithDescriptionOnly() {
        container = new JavaDocCommentContainer("/**\n * description\n */");

        assertThat(container.getDescription(), equalTo("description"));
    }

    @Test
    public final void testParseCommentWithDescriptionOnlyHasNoTags() {
        container = new JavaDocCommentContainer("/**\n * description\n */");

        assertThat(container.getTagDescriptors(), hasSize(0));
    }

    @Test
    public final void testParseCommentWithInheritDocTagOnly() {
        container = new JavaDocCommentContainer("/**\n * @inheritDoc\n */");

        assertThat(container.getTagDescriptors("@inheritdoc"), hasSize(1));
    }

    @Test
    public final void testParseCommentWithInheritDocTagOnlyHasEmptyDescription() {
        container = new JavaDocCommentContainer("/**\n * @inheritDoc\n */");
        final TagDescriptor descriptor = container.getTagDescriptors("@inheritdoc").iterator().next();
        assertThat(descriptor.getDescription(), equalTo(""));
    }

    @Test
    public final void testParseCommentWithAuthorTagOnlyHasCorrectDescription() {
        container = new JavaDocCommentContainer("/**\n * @author weigo\n */");
        final TagDescriptor descriptor = container.getTagDescriptors("@author").iterator().next();
        assertThat(descriptor.getDescription(), equalTo("weigo"));
    }

    @Test
    public final void testParseCommentWithLoneMulitLineDescription() {
        container = new JavaDocCommentContainer("/**\n * This is a\n * test description. */");
        assertThat(container.getDescription(), equalTo("This is a\ntest description."));
    }

    @Test
    public final void testParseCommentWithMultiLineDescriptionAndLoneTag() {
        container =
            new JavaDocCommentContainer("/**\n * This is a\n * test description.\n * @throws IllegalStateException */");
        assertThat(container.getDescription(), equalTo("This is a\ntest description."));

        final TagDescriptor descriptor = container.getTagDescriptors("@throws").iterator().next();
        assertThat(descriptor.getDescription(), equalTo("IllegalStateException"));
    }
}
