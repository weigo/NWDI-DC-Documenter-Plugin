/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.webservices;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNot.not;
import static org.hamcrest.core.IsNull.nullValue;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.xml.sax.SAXException;

/**
 * JUnit tests for {@link VirtualInterfaceDefinitionReader}.
 * 
 * @author Dirk Weigenand
 */
public class VirtualInterfaceDefinitionReaderTest {
    /**
     * instance under test.
     */
    private VirtualInterfaceDefinitionReader reader;

    /**
     * @throws java.lang.Exception
     */
    @Before
    public void setUp() throws Exception {
        reader = new VirtualInterfaceDefinitionReader();
    }

    /**
     * @throws java.lang.Exception
     */
    @After
    public void tearDown() throws Exception {
        reader = null;
    }

    @Test
    public final void testCreateVirtualInterface() throws IOException, SAXException {
        assertThat(reader.read(getReader()), not(nullValue()));
    }

    /**
     * @return
     */
    protected Reader getReader() {
        return new InputStreamReader(this.getClass().getResourceAsStream(
            "/org/arachna/netweaver/nwdi/documenter/webservices/ExampleVirtualInterface.videf"));
    }
}
