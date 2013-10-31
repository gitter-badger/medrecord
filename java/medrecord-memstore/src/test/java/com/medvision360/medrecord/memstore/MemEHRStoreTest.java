package com.medvision360.medrecord.memstore;

import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.spi.tck.EHRStoreTCKTestBase;

public class MemEHRStoreTest extends EHRStoreTCKTestBase
{
    @Override
    protected EHRStore getStore() throws Exception
    {
        return new MemEHRStore();
    }
}
