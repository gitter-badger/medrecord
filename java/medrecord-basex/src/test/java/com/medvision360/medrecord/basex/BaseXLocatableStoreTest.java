package com.medvision360.medrecord.basex;

import java.io.InputStream;
import java.io.OutputStream;

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.LocatableStoreTestBase;
import org.basex.core.Context;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

public class BaseXLocatableStoreTest extends LocatableStoreTestBase
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

    LocatableParser parser = new LocatableParser() {
        @Override
        public Locatable parse(InputStream is)
        {
            throw new UnsupportedOperationException("todo implement .parse()");
        }

        @Override
        public Locatable parse(InputStream is, String encoding)
        {
            throw new UnsupportedOperationException("todo implement .parse()");
        }

        @Override
        public String getMimeType()
        {
            throw new UnsupportedOperationException("todo implement .getMimeType()");
        }

        @Override
        public String getFormat()
        {
            throw new UnsupportedOperationException("todo implement .getFormat()");
        }

        @Override
        public boolean supports(Locatable locatable)
        {
            throw new UnsupportedOperationException("todo implement .supports()");
        }

        @Override
        public boolean supports(Archetyped archetyped)
        {
            throw new UnsupportedOperationException("todo implement .supports()");
        }
    };
    
    LocatableSerializer serializer = new LocatableSerializer() {
        @Override
        public void serialize(Locatable locatable, OutputStream os)
        {
            throw new UnsupportedOperationException("todo implement .serialize()");
        }

        @Override
        public void serialize(Locatable locatable, OutputStream os, String encoding)
        {
            throw new UnsupportedOperationException("todo implement .serialize()");
        }

        @Override
        public String getMimeType()
        {
            throw new UnsupportedOperationException("todo implement .getMimeType()");
        }

        @Override
        public String getFormat()
        {
            throw new UnsupportedOperationException("todo implement .getFormat()");
        }

        @Override
        public boolean supports(Locatable locatable)
        {
            throw new UnsupportedOperationException("todo implement .supports()");
        }

        @Override
        public boolean supports(Archetyped archetyped)
        {
            throw new UnsupportedOperationException("todo implement .supports()");
        }
    };
    
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
