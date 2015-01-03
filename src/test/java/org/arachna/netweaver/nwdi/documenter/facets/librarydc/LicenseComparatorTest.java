/**
 * 
 */
package org.arachna.netweaver.nwdi.documenter.facets.librarydc;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


/**
 * Unittests for {@link LicenseComparator}.
 * 
 * @author Dirk Weigenand
 */
public class LicenseComparatorTest {
    /**
     * instance under test.
     * 
     */
    private LicenseComparator comparator;

    /**
     * 
     */
    @Before
    public void setUp() {
        comparator = new LicenseComparator();
    }

    /**
     * 
     */
    @After
    public void tearDown() {
        comparator = null;
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseComparator#compare(org.arachna.netweaver.nwdi.documenter.facets.librarydc.License, org.arachna.netweaver.nwdi.documenter.facets.librarydc.License)}
     * .
     */
    @Test
    public final void testCompareLicenseNoneWithItselfReturnsZero() {
        assertThat(comparator.compare(License.None, License.None), equalTo(0));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseComparator#compare(org.arachna.netweaver.nwdi.documenter.facets.librarydc.License, org.arachna.netweaver.nwdi.documenter.facets.librarydc.License)}
     * .
     */
    @Test
    public final void testCompareLicenseOtherWithItselfReturnsZero() {
        assertThat(comparator.compare(License.Other, License.Other), equalTo(0));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseComparator#compare(org.arachna.netweaver.nwdi.documenter.facets.librarydc.License, org.arachna.netweaver.nwdi.documenter.facets.librarydc.License)}
     * .
     */
    @Test
    public final void testCompareLicenseOtherWithLicenseNoneReturnsOne() {
        assertThat(comparator.compare(License.Other, License.None), equalTo(1));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseComparator#compare(org.arachna.netweaver.nwdi.documenter.facets.librarydc.License, org.arachna.netweaver.nwdi.documenter.facets.librarydc.License)}
     * .
     */
    @Test
    public final void testCompareLicenseNoneWithLicenseOtherReturnsMinusOne() {
        assertThat(comparator.compare(License.None, License.Other), equalTo(-1));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseComparator#compare(org.arachna.netweaver.nwdi.documenter.facets.librarydc.License, org.arachna.netweaver.nwdi.documenter.facets.librarydc.License)}
     * .
     */
    @Test
    public final void testCompareNamedLicenseWithLicenseNoneReturnsOne() {
        assertThat(comparator.compare(License.MIT, License.None), equalTo(1));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseComparator#compare(org.arachna.netweaver.nwdi.documenter.facets.librarydc.License, org.arachna.netweaver.nwdi.documenter.facets.librarydc.License)}
     * .
     */
    @Test
    public final void testCompareNamedLicenseWithLicenseOtherReturnsOne() {
        assertThat(comparator.compare(License.MIT, License.Other), equalTo(1));
    }

    /**
     * Test method for
     * {@link org.arachna.netweaver.nwdi.documenter.facets.librarydc.LicenseComparator#compare(org.arachna.netweaver.nwdi.documenter.facets.librarydc.License, org.arachna.netweaver.nwdi.documenter.facets.librarydc.License)}
     * .
     */
    @Test
    public final void testCompareNamedLicenseWithOtherNamedLicenseReturnsMappedNaturalOrderOfName() {
        assertThat(comparator.compare(License.MIT, License.Apache), equalTo(-1));
    }

}
