package com.medvision360.medrecord.basex;

import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.EHRStoreTCKTestBase;
import org.basex.core.Context;
import org.openehr.rm.support.identification.HierObjectID;

public class BaseXEHRStoreTest extends EHRStoreTCKTestBase
{
    Context ctx;

    @Override
    public void setUp() throws Exception
    {
        ctx = new Context();
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        ctx.close();
    }

    XmlEHRConverter converter = new XmlEHRConverter();
    String name = "BaseXEHRStoreTest";
    String path = "unittest";

    @Override
    protected EHRStore getStore() throws Exception
    {
        return new BaseXEHRStore(
                ctx,
                converter,
                converter,
                new HierObjectID(name),
                path
        );
    }
}
