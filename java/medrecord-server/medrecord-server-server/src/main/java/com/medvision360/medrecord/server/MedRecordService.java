package com.medvision360.medrecord.server;

import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.api.exceptions.InitializationException;
import org.restlet.service.Service;

public class MedRecordService extends Service
{
    private MedRecordEngine m_engine = MedRecordEngine.getInstance();
    
    public MedRecordEngine engine() throws InitializationException
    {
        m_engine.initialize();
        return m_engine;
    }

    @Override
    public synchronized void start() throws Exception
    {
        m_engine.initialize();
    }

    @Override
    public synchronized void stop() throws Exception
    {
        m_engine.dispose();
    }
}
