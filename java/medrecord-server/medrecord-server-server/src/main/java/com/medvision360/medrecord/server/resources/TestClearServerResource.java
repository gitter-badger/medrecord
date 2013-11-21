package com.medvision360.medrecord.server.resources;

import java.io.IOException;

import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.test.TestClearResource;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClearServerResource
        extends AbstractServerResource
        implements TestClearResource
{
    private final static Logger log = LoggerFactory.getLogger(TestClearServerResource.class);

    @Override
    public void clear() throws RecordException
    {
        String confirmation = getRequiredQueryValue("confirm");
        if (!confirmation.equals("CONFIRM"))
        {
            throw new AnnotatedIllegalArgumentException("confirm should be set to CONFIRM");
        }
        
        log.warn("Clearing out the server!");
        
        try
        {
            engine().getArchetypeStore().clear();
            engine().getLocatableStore().clear();
            engine().getEHRStore().clear();
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }
}
