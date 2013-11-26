package com.medvision360.medrecord.server.ehr;

import java.io.IOException;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.ehr.EHRListResource;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidLocatableTypeException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.HierObjectID;
import org.restlet.representation.Representation;

public class EHRListServerResource
        extends AbstractEHRResource
        implements EHRListResource
{
    @Override
    public ID postEHR(Representation representation) throws RecordException
    {
        try
        {
            Locatable locatable = toLocatable(representation);
            if (!(locatable instanceof EHRStatus))
            {
                throw new InvalidLocatableTypeException(String.format(
                        "Expected a EHRStatus but got a %s", locatable.getClass().getSimpleName()));
            }
            EHRStatus ehrStatus = (EHRStatus) locatable;
            org.openehr.rm.ehr.EHR ehr = engine().createEHR(ehrStatus);
            ID result = new ID();
            result.setId(ehr.getEhrID().getValue());
            return result;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    @Override
    public IDList listEHRs() throws RecordException
    {
        try
        {
            String excludeDeletedString = getQueryValue("excludeDeleted");
            Iterable<HierObjectID> list;
            if (excludeDeletedString != null)
            {
                boolean excludeDeleted = "true".equals(excludeDeletedString);
                list = engine().getEHRStore().list(excludeDeleted);
            }
            else
            {
                list = engine().getEHRStore().list();
            }
            IDList result = toIdList(list);
            return result;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }
}
