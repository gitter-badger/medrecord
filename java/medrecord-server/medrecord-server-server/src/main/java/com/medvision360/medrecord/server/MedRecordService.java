package com.medvision360.medrecord.server;

import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.spi.exceptions.InitializationException;
import org.restlet.service.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MedRecordService extends Service
{
    public MedRecordEngine engine() throws InitializationException
    {
        MedRecordEngine instance = MedRecordEngine.getInstance();
        instance.initialize();
        return instance;
    }
}
