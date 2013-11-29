package com.medvision360.medrecord.riio;

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.tck.LocatableConverterTCKTestBase;

public class RiDadlConverterTCKTest extends LocatableConverterTCKTestBase
{
    @Override
    protected LocatableParser getParser() throws Exception
    {
        return new RIDadlConverter(TERMINOLOGY_SERVICE, MEASUREMENT_SERVICE, ENCODING, LANGUAGE);
    }

    @Override
    protected LocatableSerializer getSerializer() throws Exception
    {
        return new RIDadlConverter(TERMINOLOGY_SERVICE, MEASUREMENT_SERVICE, ENCODING, LANGUAGE);
    }
}
