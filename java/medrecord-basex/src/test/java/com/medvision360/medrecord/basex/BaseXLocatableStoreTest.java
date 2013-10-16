package com.medvision360.medrecord.basex;

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.LocatableStoreTCKTestBase;
import org.basex.core.Context;

public class BaseXLocatableStoreTest extends LocatableStoreTCKTestBase
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

    LocatableParser parser = new MockLocatableParser();
    LocatableSerializer serializer = new MockLocatableSerializer();
    String name = "BaseXLocatableStoreTest";
    String path = "unittest";
    
    @Override
    protected LocatableStore getStore() throws Exception
    {
        return new BaseXLocatableStore(
                ctx,
                parser,
                serializer,
                name,
                path
        );
    }

}
