package com.medvision360.medrecord.basex;

import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.spi.tck.ArchetypeStoreTCKTestBase;
import org.basex.core.Context;
import org.openehr.rm.support.identification.HierObjectID;

public class BaseXArchetypeStoreTest extends ArchetypeStoreTCKTestBase
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

    XmlWrappedArchetypeConverter converter = new XmlWrappedArchetypeConverter();
    String name = "BaseXArchetypeStoreTest";
    String path = "unittest";

    @Override
    protected ArchetypeStore getStore() throws Exception
    {
        return new BaseXArchetypeStore(
                ctx,
                converter,
                converter,
                name,
                path
        );
    }
}
