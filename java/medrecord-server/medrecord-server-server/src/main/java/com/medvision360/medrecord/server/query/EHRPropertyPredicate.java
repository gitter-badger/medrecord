package com.medvision360.medrecord.server.query;

import java.io.IOException;
import java.util.List;

import com.google.common.base.Predicate;
import com.medvision360.medrecord.api.exceptions.DeletedException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.engine.MedRecordEngine;
import org.joda.time.DateTime;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.PartyRef;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Predicate applied to EHR IDs that retrieves the EHR and inspects whether its properties match the provided 
 * requirements.
 */
class EHRPropertyPredicate implements Predicate<HierObjectID>
{
    private final static Logger log = LoggerFactory.getLogger(EHRPropertyPredicate.class);

    private final MedRecordEngine m_engine;
    private final String m_subject;
    private final DateTime m_createdBefore;
    private final DateTime m_createdAfter;
    private final List<String> m_systemIDList;

    public EHRPropertyPredicate(MedRecordEngine engine,
            String subject, DateTime createdBefore, DateTime createdAfter, List<String> systemIDList)
    {
        m_subject = subject;
        m_createdBefore = createdBefore;
        m_createdAfter = createdAfter;
        m_engine = engine;
        m_systemIDList = systemIDList;
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
        
        if (m_subject != null)
        {
            EHRStatus status;
            try
            {
                status = m_engine.getEHRStatus(EHR);
            }
            catch (RecordException | IOException e)
            {
                log.warn("Exception retrieving EHR status from EHR: {}", e.getMessage(), e);
                return false;
            }
            PartySelf partySelf = status.getSubject();
            PartyRef ref = partySelf.getExternalRef();
            if (ref == null)
            {
                return false;
            }
            String refIdString = ref.getId().getValue();
            if (!m_subject.equals(refIdString))
            {
                return false;
            };
        }
        
        DvDateTime dvCreated = EHR.getTimeCreated();
        DateTime created = dvCreated.getDateTime();
        if (m_createdBefore != null && created.isAfter(m_createdBefore))
        {
            return false;
        }
        if (m_createdAfter != null && created.isBefore(m_createdAfter))
        {
            return false;
        }
        
        if (m_systemIDList != null && !m_systemIDList.isEmpty())
        {
            HierObjectID systemID = EHR.getSystemID();
            if(!m_systemIDList.contains(systemID.getValue()))
            {
                return false;
            }
        }
        
        return true;
    }
}
