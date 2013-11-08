package com.medvision360.medrecord.server;

import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.spi.exceptions.InitializationException;
import org.restlet.service.Service;

public class MedRecordService extends Service
{
    private final static MedRecordEngine engine = new MedRecordEngine();
    
    public MedRecordEngine engine() throws InitializationException
    {
        engine.initialize();
        return engine;
    }
}
