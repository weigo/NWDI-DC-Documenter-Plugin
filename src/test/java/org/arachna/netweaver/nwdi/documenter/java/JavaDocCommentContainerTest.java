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
}
