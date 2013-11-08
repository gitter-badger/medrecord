package com.medvision360.medrecord.server;

import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.spi.exceptions.InitializationException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.restlet.Context;
import org.restlet.Request;
import org.restlet.Response;
import org.restlet.data.Status;
import org.restlet.resource.ServerResource;
import org.restlet.routing.Filter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TransactionFilter extends Filter
{
    private final static Logger log = LoggerFactory.getLogger(TransactionFilter.class);

    public TransactionFilter(Context context, Class<? extends ServerResource> targetClass)
    {
        super(context);
        setNext(targetClass);
    }
    
    @Override
    protected int beforeHandle(Request request, Response response)
    {
        MedRecordService service = getApplication().getServices().get(MedRecordService.class);
        try
        {
            MedRecordEngine engine = service.engine();
            engine.begin();
            setCommitFlag(true);
            response.setAutoCommitting(false);
        }
        catch (InitializationException | TransactionException e)
        {
            response.setStatus(Status.SERVER_ERROR_INTERNAL);
            // todo write error to client
            log.error("Error beginning transaction: %s", e.getMessage());
            return STOP;
        }
        
        return CONTINUE;
    }

    @Override
    protected int doHandle(Request request, Response response)
    {
        try
        {
            return super.doHandle(request, response);
        }
        catch (RuntimeException e)
        {
            setCommitFlag(false);
            throw e;
        }
    }

    @Override
    protected void afterHandle(Request request, Response response)
    {
        MedRecordService service = getApplication().getServices().get(MedRecordService.class);
        MedRecordEngine engine;
        try
        {
            engine = service.engine();
        }
        catch (InitializationException e)
        {
            setCommitFlag(false);
            response.setStatus(Status.SERVER_ERROR_INTERNAL);
            // todo write error to client
            log.error("Error finalizing transaction: %s", e.getMessage());
            response.commit();
            return;
        }
                    
        boolean shouldCommit = getCommitFlag() && response.getStatus().isSuccess();
        try
        {
            if (shouldCommit)
            {
                engine.commit();
            }
            else
            {
                engine.rollback();
            }
        }
        catch (TransactionException e)
        {
            response.setStatus(Status.SERVER_ERROR_INTERNAL);
            // todo write error to client
            log.error("Error finalizing transaction: %s", e.getMessage());
        }
        finally
        {
            response.commit();
        }
    }

    private void setCommitFlag(boolean value)
    {
        getContext().getAttributes().put("TransactionFilter.commit", value);
    }

    private boolean getCommitFlag()
    {
        return Boolean.valueOf(String.valueOf(
                getContext().getAttributes().get("TransactionFilter.commit")));
    }

}
