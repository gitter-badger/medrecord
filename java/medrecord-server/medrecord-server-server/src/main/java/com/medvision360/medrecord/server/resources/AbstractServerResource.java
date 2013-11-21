package com.medvision360.medrecord.server.resources;

import com.medvision360.lib.server.ServerResourceBase;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.server.MedRecordService;
import com.medvision360.medrecord.api.exceptions.InitializationException;
import com.medvision360.medrecord.api.exceptions.MissingParameterException;
import org.restlet.service.Service;

public abstract class AbstractServerResource
        extends ServerResourceBase
{
    protected <T extends Service> T getService(Class<T> serviceClass)
    {
        return getApplication().getServices().get(serviceClass);
    }
    
    protected MedRecordEngine engine() throws InitializationException
    {
        MedRecordService service = getService(MedRecordService.class);
        MedRecordEngine engine = service.engine();
        return engine;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected String getRequiredQueryValue(String name) throws MissingParameterException
    {
        String id = getQueryValue(name);
        if (id == null)
        {
            throw new MissingParameterException(name);
        }
        return id;
    }

    @SuppressWarnings("UnusedDeclaration")
    protected String getRequiredAttributeValue(String name) throws MissingParameterException
    {
        String id = getAttribute(name);
        if (id == null)
        {
            throw new MissingParameterException(name);
        }
        return id;
    }
}
