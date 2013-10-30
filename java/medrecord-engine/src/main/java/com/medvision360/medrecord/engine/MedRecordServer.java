package com.medvision360.medrecord.engine;

import java.io.IOException;

import com.medvision360.medrecord.spi.ArchetypeParser;
import com.medvision360.medrecord.spi.ArchetypeSerializer;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.AuditInfo;
import com.medvision360.medrecord.spi.AuditService;
import com.medvision360.medrecord.spi.EHRParser;
import com.medvision360.medrecord.spi.EHRSerializer;
import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.spi.Engine;
import com.medvision360.medrecord.spi.LocatableEditor;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableSummary;
import com.medvision360.medrecord.spi.LocatableValidator;
import com.medvision360.medrecord.spi.XQueryStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.demographic.Person;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;

public class MedRecordServer implements Engine
{
    @Override
    public ArchetypeStore getArchetypeStore()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getArchetypeStore()");
    }

    @Override
    public ArchetypeParser getArchetypeParser(String mimeType, String format) throws NotSupportedException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getArchetypeParser()");
    }

    @Override
    public ArchetypeSerializer getArchetypeSerializer(String mimeType, String format) throws NotSupportedException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getArchetypeSerializer()");
    }

    @Override
    public EHRStore getEHRStore()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEHRStore()");
    }

    @Override
    public EHRParser getEHRParser(String mimeType, String format) throws NotSupportedException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEHRParser()");
    }

    @Override
    public EHRSerializer getEHRSerializer(String mimeType, String format) throws NotSupportedException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEHRSerializer()");
    }

    @Override
    public XQueryStore getLocatableStore()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getLocatableStore()");
    }

    @Override
    public LocatableEditor getEditor()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEditor()");
    }

    @Override
    public LocatableValidator getValidator()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getValidator()");
    }

    @Override
    public LocatableParser getLocatableParser(String mimeType, String format) throws NotSupportedException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getLocatableParser()");
    }

    @Override
    public LocatableSerializer getLocatableSerializer(String mimeType, String format) throws NotSupportedException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getLocatableSerializer()");
    }

    @Override
    public AuditService getAuditService()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getAuditService()");
    }

    @Override
    public TerminologyService getTerminologyService()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getTerminologyService()");
    }

    @Override
    public MeasurementService getMeasurementService()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getMeasurementService()");
    }

    @Override
    public EHR getEHRBySubject(Person subject) throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEHRBySubject()");
    }

    @Override
    public EHR getEHRBySubject(UIDBasedID subject) throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEHRBySubject()");
    }

    @Override
    public EHR getEHRForLocatable(Locatable locatable) throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEHRForLocatable()");
    }

    @Override
    public EHR getEHRForLocatable(UIDBasedID locatable) throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEHRForLocatable()");
    }

    @Override
    public EHR createEHR(EHRStatus EHRStatus)
            throws NotFoundException, DuplicateException, NotSupportedException, IOException, SerializeException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.createEHR()");
    }

    @Override
    public EHRStatus getEHRStatus(EHR EHR) throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEHRStatus()");
    }

    @Override
    public EHRStatus getEHRStatus(HierObjectID ehrId) throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getEHRStatus()");
    }

    @Override
    public LocatableSummary summarizeLocatable(UIDBasedID locatable)
            throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.summarizeLocatable()");
    }

    @Override
    public Iterable<LocatableSummary> summarizeEHR(EHR EHR) throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.summarizeEHR()");
    }

    @Override
    public Iterable<LocatableSummary> summarizeEHR(HierObjectID EHR)
            throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.summarizeEHR()");
    }

    @Override
    public void setAuditInfo(AuditInfo auditInfo)
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.setAuditInfo()");
    }

    @Override
    public void verifyStatus() throws StatusException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.verifyStatus()");
    }

    @Override
    public String reportStatus() throws StatusException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.reportStatus()");
    }

    @Override
    public String getName()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.getName()");
    }

    @Override
    public boolean supportsTransactions()
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.supportsTransactions()");
    }

    @Override
    public void begin() throws TransactionException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.begin()");
    }

    @Override
    public void commit() throws TransactionException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.commit()");
    }

    @Override
    public void rollback() throws TransactionException
    {
        throw new UnsupportedOperationException("todo implement MedRecordServer.rollback()");
    }
}
