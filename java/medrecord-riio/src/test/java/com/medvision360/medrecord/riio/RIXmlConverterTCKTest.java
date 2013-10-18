package com.medvision360.medrecord.riio;

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.tck.LocatableConverterTCKTestBase;

public class RIXmlConverterTCKTest extends LocatableConverterTCKTestBase
{
    @Override
    protected LocatableParser getParser() throws Exception
    {
        return new RIXmlConverter(ts, ms, encoding);
    }

    @Override
    protected LocatableSerializer getSerializer() throws Exception
    {
        return new RIXmlConverter(ts, ms, encoding);
    }
}
