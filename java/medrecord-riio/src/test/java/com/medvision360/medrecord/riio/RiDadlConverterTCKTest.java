package com.medvision360.medrecord.riio;

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.tck.LocatableConverterTCKTestBase;
import org.openehr.rm.support.measurement.MeasurementService;
import com.medvision360.medrecord.spi.tck.TestMeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;
import com.medvision360.medrecord.spi.tck.TestTerminologyService;

public class RiDadlConverterTCKTest extends LocatableConverterTCKTestBase
{
    @Override
    protected LocatableParser getParser() throws Exception
    {
        return new RIDadlConverter(ts, ms, encoding, lang);
    }

    @Override
    protected LocatableSerializer getSerializer() throws Exception
    {
        return new RIDadlConverter(ts, ms, encoding, lang);
    }
}
