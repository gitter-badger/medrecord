package com.medvision360.medrecord.server.query;

import java.io.IOException;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.medvision360.medrecord.api.exceptions.DeletedException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.engine.MedRecordEngine;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Predicate applied to EHR IDs that checks whether there are any locatables stored for this EHR.
 */
class NonEmptyEHRPredicate implements Predicate<HierObjectID>
{
    private final static Logger log = LoggerFactory.getLogger(EHRPropertyPredicate.class);

    private final MedRecordEngine m_engine;

    public NonEmptyEHRPredicate(MedRecordEngine engine)
    {
        m_engine = engine;
    }

    @Override
    public boolean apply(HierObjectID id)
    {
        EHR EHR;
        try
        {
            EHR = m_engine.getEHRStore().get(id);
        }
        catch (DeletedException e)
        {
            if (e.getDeleted() instanceof EHR)
            {
                EHR = (EHR) e.getDeleted();
            }
            else
            {
                log.warn("Exception retrieving EHR from EHR list (deleted object not an EHR??): {}",
                        e.getMessage(), e);
                return false;
            }
        }
        catch (RecordException | IOException e)
        {
            log.warn("Exception retrieving EHR from EHR list: {}", e.getMessage(), e);
            return false;
        }
        try
        {
            Iterable<HierObjectID> locatableList = m_engine.getLocatableStore().list(EHR);
            return !Iterables.isEmpty(locatableList);
        }
        catch (NotFoundException e)
        {
            return false;
        }
        catch (IOException e)
        {
            log.warn("Exception listing locatables in EHR list: {}", e.getMessage(), e);
            return false;
        }
    }
}
