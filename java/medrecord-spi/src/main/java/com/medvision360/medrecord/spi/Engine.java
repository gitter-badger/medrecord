package com.medvision360.medrecord.spi;

import java.io.IOException;

import com.medvision360.medrecord.spi.exceptions.DisposalException;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.InitializationException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.demographic.Person;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;

public interface Engine extends TransactionalService, NamedService, StatusService, AuditedService
{
    ///
    /// Initialization and disposal
    ///
    public void initialize() throws InitializationException;
    
    public void dispose() throws DisposalException;
    
    
    ///
    /// Services
    ///

    ArchetypeStore getArchetypeStore();
    
    ArchetypeParser getArchetypeParser(String mimeType, String format) throws NotSupportedException;

    ArchetypeSerializer getArchetypeSerializer(String mimeType, String format) throws NotSupportedException;

    
    EHRStore getEHRStore();

    EHRParser getEHRParser(String mimeType, String format) throws NotSupportedException;

    EHRSerializer getEHRSerializer(String mimeType, String format) throws NotSupportedException;

    
    /**
     * The provided {@link LocatableStore} is typically a {@link CompositeStore} that additionally implements a chain
     * of {@link LocatableTransformer} (using {@link CompositeTransformer}) on key operations to handle things like 
     * validation and ID generation.
     */
    XQueryStore getLocatableStore();
    
    LocatableEditor getLocatableEditor();

    /**
     * The provided {@link LocatableValidator} is typically already automatically applied on {@link LocatableStore} 
     * insert/update methods, so normally the only reason to retrieve it is for some kind of pre-submission testing. 
     */
    LocatableValidator getLocatableValidator();
    
    LocatableParser getLocatableParser(String mimeType, String format) throws NotSupportedException;

    LocatableSerializer getLocatableSerializer(String mimeType, String format) throws NotSupportedException;

    
    AuditService getAuditService();
    
    TerminologyService getTerminologyService();
    
    MeasurementService getMeasurementService();
    
    ///
    /// High(er) level operations
    ///
    
    EHR getEHRBySubject(Person subject) throws NotFoundException, IOException, ParseException;
    
    EHR getEHRBySubject(UIDBasedID subject) throws NotFoundException, IOException, ParseException;
    
    EHR getEHRForLocatable(Locatable locatable) throws NotFoundException, IOException, ParseException;
    
    EHR getEHRForLocatable(UIDBasedID locatable) throws NotFoundException, IOException, ParseException;
    
    EHR createEHR(EHRStatus EHRStatus) throws NotFoundException, DuplicateException, NotSupportedException,
            IOException, SerializeException;
    
    EHRStatus getEHRStatus(EHR EHR) throws NotFoundException, IOException, ParseException;
    
    EHRStatus getEHRStatus(HierObjectID ehrId) throws NotFoundException, IOException, ParseException;
    
    LocatableSummary summarizeLocatable(UIDBasedID locatable) throws NotFoundException, IOException, ParseException;

    Iterable<LocatableSummary> summarizeEHR(EHR EHR) throws NotFoundException, IOException, ParseException;

    Iterable<LocatableSummary> summarizeEHR(HierObjectID EHR) throws NotFoundException, IOException, ParseException;
}
