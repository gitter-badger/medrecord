package com.medvision360.medrecord.server.ehr;

import java.io.IOException;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.ehr.EHRLocatableListResource;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;
import org.restlet.representation.Representation;

public class EHRLocatableListServerResource
        extends AbstractEHRResource
        implements EHRLocatableListResource
{
    @Override
    public ID postLocatable(Representation representation) throws RecordException
    {
        try
        {
            boolean ignoreDeleted = false; // can't update a deleted EHR
            EHR ehr = getEHRModel(ignoreDeleted);
            Locatable locatable = toLocatable(representation);
            Locatable inserted = engine().getLocatableStore().insert(ehr, locatable);
            String idString = inserted.getUid().getValue();
            ID result = new ID();
            result.setId(idString);
            return result;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    @Override
    public IDList listLocatables() throws RecordException
    {
        try
        {
            EHR ehr = getEHRModel();
            Iterable<HierObjectID> list = engine().getLocatableStore().list(ehr);
            IDList result = toIdList(list);
            return result;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }
}
