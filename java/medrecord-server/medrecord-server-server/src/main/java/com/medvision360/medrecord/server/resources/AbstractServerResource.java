package com.medvision360.medrecord.server.resources;

import com.medvision360.lib.api.ServiceUnavailableException;
import com.medvision360.lib.server.ServerResourceBase;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.server.MedRecordService;
import com.medvision360.medrecord.spi.exceptions.InitializationException;
import org.restlet.service.Service;

public abstract class AbstractServerResource
        extends ServerResourceBase
{
    protected <T extends Service> T getService(Class<T> serviceClass)
    {
        return getApplication().getServices().get(serviceClass);
    }
    
    protected MedRecordEngine engine() throws ServiceUnavailableException
    {
        MedRecordService service = getService(MedRecordService.class);
        MedRecordEngine engine;
        try
        {
            engine = service.engine();
        }
        catch (InitializationException e)
        {
            throw new ServiceUnavailableException(e);
        }
        return engine;
    }
}
