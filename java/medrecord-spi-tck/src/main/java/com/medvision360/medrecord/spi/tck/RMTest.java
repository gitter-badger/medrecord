package com.medvision360.medrecord.spi.tck;

import org.openehr.rm.common.archetyped.Locatable;

public class RMTest extends RMTestBase
{
    public void testTest() throws Exception
    {
        Locatable locatable = makeLocatable();
        assertNotNull(locatable);
    }
}
