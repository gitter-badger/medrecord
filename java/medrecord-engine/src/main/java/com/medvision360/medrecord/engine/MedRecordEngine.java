/**
 * This file is part of MEDrecord.
 * This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
 * "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at
 *
 *     http://creativecommons.org/licenses/by-nc-sa/4.0/
 *
 * @copyright Copyright (c) 2013 MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.medvision360.medrecord.api.exceptions.UnsupportedQueryException;
import com.medvision360.medrecord.basex.BaseXArchetypeStore;
import com.medvision360.medrecord.basex.BaseXEHRStore;
import com.medvision360.medrecord.basex.BaseXLocatableStore;
import com.medvision360.medrecord.basex.XmlEHRConverter;
import com.medvision360.medrecord.basex.XmlWrappedArchetypeConverter;
import com.medvision360.medrecord.pv.PVParser;
import com.medvision360.medrecord.pv.PVSerializer;
import com.medvision360.medrecord.riio.RIAdlConverter;
import com.medvision360.medrecord.riio.RIDadlConverter;
import com.medvision360.medrecord.riio.RIXmlConverter;
import com.medvision360.medrecord.spi.ArchetypeParser;
import com.medvision360.medrecord.spi.ArchetypeSerializer;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.AuditInfo;
import com.medvision360.medrecord.spi.AuditService;
import com.medvision360.medrecord.spi.CompositeStore;
import com.medvision360.medrecord.spi.CompositeTransformer;
import com.medvision360.medrecord.spi.CompositeValidator;
import com.medvision360.medrecord.spi.EHRParser;
import com.medvision360.medrecord.spi.EHRSerializer;
import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.spi.Engine;
import com.medvision360.medrecord.spi.LocatableEditor;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableSummary;
import com.medvision360.medrecord.spi.LocatableValidator;
import com.medvision360.medrecord.spi.StoredEHR;
import com.medvision360.medrecord.spi.Terminology;
import com.medvision360.medrecord.spi.TransformingXQueryStore;
import com.medvision360.medrecord.spi.TypeSelector;
import com.medvision360.medrecord.spi.UIDFactory;
import com.medvision360.medrecord.spi.ValidatingXQueryStore;
import com.medvision360.medrecord.spi.XQueryStore;
import com.medvision360.medrecord.spi.base.BaseLocatableEditor;
import com.medvision360.medrecord.api.exceptions.DisposalException;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.InitializationException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import com.medvision360.medrecord.api.exceptions.StatusException;
import com.medvision360.medrecord.api.exceptions.TransactionException;
import com.medvision360.medrecord.api.exceptions.ValidationException;
import org.basex.core.Context;
import org.basex.io.serial.SerializerProp;
import org.basex.query.MainModule;
import org.basex.query.QueryContext;
import org.basex.query.QueryException;
import org.basex.query.QueryParser;
import org.basex.query.QueryProcessor;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.generic.PartySelf;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.demographic.Person;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectID;
import org.openehr.rm.support.identification.ObjectRef;
import org.openehr.rm.support.identification.PartyRef;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.measurement.SimpleMeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;
import org.openehr.terminology.SimpleTerminologyService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.google.common.base.Preconditions.checkNotNull;

public class MedRecordEngine implements Engine, AuditService
{
    private static final Logger log = LoggerFactory.getLogger(MedRecordEngine.class);

    private boolean m_initialized = false;
    private boolean m_storeValidation = true;
    private CodePhrase m_encoding;
    private CodePhrase m_language;
    private CodePhrase m_territory;
    private TerminologyService m_terminologyService;
    private MeasurementService m_measurementService;
    private Map<SystemValue, Object> m_systemValues = new HashMap<>();

    private List<Context> m_baseXContexts = new LinkedList<>();
    
    private UIDFactory m_uidFactory;

    private ArchetypeStore m_archetypeStore;
    private List<ArchetypeParser> m_archetypeParsers = new LinkedList<>();
    private List<ArchetypeSerializer> m_archetypeSerializers = new LinkedList<>();
    
    private EHRStore m_ehrStore;
    private List<EHRParser> m_ehrParsers = new LinkedList<>();
    private List<EHRSerializer> m_ehrSerializers = new LinkedList<>();

    private XQueryStore m_locatableStore;
    private LocatableEditor m_locatableEditor;
    private LocatableValidator m_locatableValidator;
    private List<LocatableParser> m_locatableParsers = new LinkedList<>();
    private List<LocatableSerializer> m_locatableSerializers = new LinkedList<>();
    
    @SuppressWarnings({"FieldCanBeLocal", "UnusedDeclaration"})
    private AuditInfo m_auditInfo; // todo make use of audit info
    private String m_name;

    ///
    /// Initialization
    ///
    
    private static MedRecordEngine m_instance = null;
    public static synchronized MedRecordEngine getInstance()
    {
        if (m_instance == null)
        {
            m_instance = new MedRecordEngine();
        }
        return m_instance;
    }

    public MedRecordEngine()
    {
        if (log.isTraceEnabled())
        {
            Exception e = new Exception(String.format("constructor stack: %s", this));
            log.trace("--> MedRecordEngine()", e);
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setStoreValidation(boolean storeValidation)
    {
        m_storeValidation = storeValidation;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setName(String name)
    {
        m_name = name;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setEncoding(CodePhrase encoding)
    {
        m_encoding = encoding;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setLanguage(CodePhrase language)
    {
        m_language = language;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setTerritory(CodePhrase territory)
    {
        m_territory = territory;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setTerminologyService(TerminologyService terminologyService)
    {
        m_terminologyService = terminologyService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public void setMeasurementService(MeasurementService measurementService)
    {
        m_measurementService = measurementService;
    }

    @SuppressWarnings("UnusedDeclaration")
    public Map<SystemValue, Object> getSystemValues()
    {
        return m_systemValues;
    }
    
    private Context newContext()
    {
        Context baseXContext = new Context();
        m_baseXContexts.add(baseXContext);
        return baseXContext;
    }

    public synchronized void initialize() throws InitializationException
    {
        if (log.isTraceEnabled())
        {
            Exception e = new Exception(String.format("initialize stack: %s", this));
            log.trace("--> initialize()", e);
        }
        if (m_initialized)
        {
            return;
        }
        
        if (m_name == null)
        {
            m_name = "MedRecordServer";
        }
        if (m_encoding == null)
        {
            m_encoding = Terminology.CHARSET_UTF8;
        }
        if (m_language == null)
        {
            m_language = Terminology.L_en;
        }
        if (m_territory == null)
        {
            m_territory = Terminology.C_NL;
        }
        if (m_terminologyService == null)
        {
            m_terminologyService = SimpleTerminologyService.getInstance();
        }
        if (m_measurementService == null)
        {
            m_measurementService = SimpleMeasurementService.getInstance();
        }
        
        m_systemValues.put(SystemValue.CHARSET, m_encoding);
        m_systemValues.put(SystemValue.ENCODING, m_encoding);
        m_systemValues.put(SystemValue.LANGUAGE, m_language);
        m_systemValues.put(SystemValue.TERRITORY, m_territory);
        m_systemValues.put(SystemValue.TERMINOLOGY_SERVICE, m_terminologyService);
        m_systemValues.put(SystemValue.MEASUREMENT_SERVICE, m_measurementService);

        initializeArchetypeSupport();
        initializeEHRSupport();
        initializeLocatableSupport();
        
        m_initialized = true;
        log.debug("MedRecordEngine initialized");
    }

    private synchronized void initializeArchetypeSupport() throws InitializationException
    {
        RIAdlConverter adlConverter = new RIAdlConverter();
        m_archetypeParsers.add(adlConverter);
        m_archetypeSerializers.add(adlConverter);

        XmlWrappedArchetypeConverter wrappedConverter = new XmlWrappedArchetypeConverter(adlConverter);
        m_archetypeParsers.add(wrappedConverter);
        m_archetypeSerializers.add(wrappedConverter);

        ArchetypeStore archetypeStore = new BaseXArchetypeStore(newContext(), wrappedConverter, wrappedConverter,
                m_name + "Archetypes", "archetype");
        try
        {
            archetypeStore.initialize();
        }
        catch (IOException e)
        {
            throw new InitializationException(e);
        }
        
        m_archetypeStore = archetypeStore;
    }

    private synchronized void initializeEHRSupport() throws InitializationException
    {
        XmlEHRConverter xmlConverter = new XmlEHRConverter();
        m_ehrParsers.add(xmlConverter);
        m_ehrSerializers.add(xmlConverter);
        
        HierObjectID ehrSystemID = new HierObjectID(m_name + "EHRStore");
        EHRStore ehrStore = new BaseXEHRStore(newContext(), xmlConverter, xmlConverter, ehrSystemID, "ehr");
        try
        {
            ehrStore.initialize();
        }
        catch (IOException e)
        {
            throw new InitializationException(e);
        }
        
        m_ehrStore = ehrStore;
    }

    private synchronized void initializeLocatableSupport() throws InitializationException
    {
        Context baseXContext;
        
        baseXContext = new Context();
        m_baseXContexts.add(baseXContext);
        
        RIXmlConverter xmlConverter = new RIXmlConverter(m_systemValues);
        m_locatableParsers.add(xmlConverter);
        m_locatableSerializers.add(xmlConverter);

        RIDadlConverter dadlConverter = new RIDadlConverter(m_systemValues);
        m_locatableParsers.add(dadlConverter);
        m_locatableSerializers.add(dadlConverter);
        
        PVParser pvParser = new PVParser(m_systemValues);
        m_locatableParsers.add(pvParser);
        PVSerializer pvSerializer = new PVSerializer();
        m_locatableSerializers.add(pvSerializer);
        
        ArchetypeBasedValidator archetypeBasedValidator = new ArchetypeBasedValidator(m_archetypeStore, m_systemValues);
        CompositeValidator compositeValidator = new CompositeValidator();
        compositeValidator.addDelegate(archetypeBasedValidator);
        m_locatableValidator = compositeValidator;

        LocatableSelector demographicSelector = LocatableSelectorBuilder
                .start()
                .requireRMName("DEMOGRAPHIC")
                .matchRMEntity("^(?:PARTY_IDENTITY|PARTY_RELATIONSHIP|PERSON|ORGANISATION|ROLE)$")
                .include(m_locatableValidator)
                .build();
        baseXContext = new Context();
        m_baseXContexts.add(baseXContext);
        BaseXLocatableStore demographicStore = new BaseXLocatableStore(newContext(), xmlConverter, xmlConverter,
                demographicSelector, m_name + "DEMOGRAPHIC", "demographic");
        
        LocatableSelector ehrSelector = LocatableSelectorBuilder
                .start()
                .requireRMName("EHR")
                .matchRMEntity("^(?:COMPOSITION|EHRSTATUS|EHR_STATUS)$")
                .build();
        baseXContext = new Context();
        m_baseXContexts.add(baseXContext);
        BaseXLocatableStore ehrStore = new BaseXLocatableStore(newContext(), xmlConverter, xmlConverter,
                ehrSelector, m_name + "EHR", "ehr");

        CompositeStore compositeStore = new CompositeStore(m_name + "STORE");
        if (m_storeValidation)
        {
            LocatableSelector validatingSelector = m_locatableValidator;

            ValidatingXQueryStore validatingDemographicStore = new ValidatingXQueryStore(
                    demographicStore.getName(), validatingSelector, demographicStore, m_locatableValidator);
            compositeStore.addDelegate(validatingDemographicStore);

            ValidatingXQueryStore validatingEhrStore = new ValidatingXQueryStore(
                    ehrStore.getName(), validatingSelector, ehrStore, m_locatableValidator);
            compositeStore.addDelegate(validatingEhrStore);
        }
        else
        {
            compositeStore.addDelegate(demographicStore);
            compositeStore.addDelegate(ehrStore);
        }
        
        UIDGenerator uidGenerator = new UIDGenerator();
        m_uidFactory = uidGenerator;
        CompositeTransformer transformer = new CompositeTransformer();
        transformer.addDelegate(uidGenerator);
        TransformingXQueryStore transformingStore = new TransformingXQueryStore(m_name + "STORE", compositeStore, 
                transformer);
        
        try
        {
            transformingStore.initialize();
        }
        catch (IOException e)
        {
            throw new InitializationException(e);
        }

        m_locatableStore = transformingStore;
        
        m_locatableEditor = new BaseLocatableEditor();
    }

    public void dispose() throws DisposalException
    {
        DisposalException ex = null;
        
        try
        {
            m_archetypeStore.dispose();
        }
        catch(DisposalException e)
        {
            ex = e;
        }
        try
        {
            m_ehrStore.dispose();
        }
        catch(DisposalException e)
        {
            ex = e;
        }
        try
        {
            m_locatableStore.dispose();
        }
        catch(DisposalException e)
        {
            ex = e;
        }
        
        for (Context context : m_baseXContexts)
        {
            context.close();
        }
        
        if (ex != null)
        {
            log.debug("MedRecordEngine disposed with error", ex);
            throw ex;
        }
        else
        {
            log.debug("MedRecordEngine disposed");
        }
    }

    ///
    /// API
    ///
    
    @Override
    public ArchetypeStore getArchetypeStore()
    {
        return m_archetypeStore;
    }

    @Override
    public ArchetypeParser getArchetypeParser(String mimeType, String format) throws NotSupportedException
    {
        return selectForType(m_archetypeParsers, mimeType, format);
    }

    @Override
    public ArchetypeSerializer getArchetypeSerializer(String mimeType, String format) throws NotSupportedException
    {
        return selectForType(m_archetypeSerializers, mimeType, format);
    }

    @Override
    public EHRStore getEHRStore()
    {
        return m_ehrStore;
    }

    @Override
    public EHRParser getEHRParser(String mimeType, String format) throws NotSupportedException
    {
        return selectForType(m_ehrParsers, mimeType, format);
    }

    @Override
    public EHRSerializer getEHRSerializer(String mimeType, String format) throws NotSupportedException
    {
        return selectForType(m_ehrSerializers, mimeType, format);
    }

    @Override
    public XQueryStore getLocatableStore()
    {
        return m_locatableStore;
    }

    @Override
    public LocatableEditor getLocatableEditor()
    {
        return m_locatableEditor;
    }

    @Override
    public LocatableValidator getLocatableValidator()
    {
        return m_locatableValidator;
    }

    @Override
    public LocatableParser getLocatableParser(String mimeType, String format) throws NotSupportedException
    {
        return selectForType(m_locatableParsers, mimeType, format);
    }

    @Override
    public LocatableSerializer getLocatableSerializer(String mimeType, String format) throws NotSupportedException
    {
        return selectForType(m_locatableSerializers, mimeType, format);
    }

    @Override
    public AuditService getAuditService()
    {
        return this;
    }

    @Override
    public TerminologyService getTerminologyService()
    {
        return m_terminologyService;
    }

    @Override
    public MeasurementService getMeasurementService()
    {
        return m_measurementService;
    }

    @Override
    public EHR getEHRBySubject(Person subject) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(subject, "subject cannot be null");
        return getEHRBySubject(subject.getUid());
    }

    @Override
    public EHR getEHRBySubject(UIDBasedID subjectUid) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(subjectUid, "subjectUid cannot be null");
        EHRStatus ehrStatus = getEhrStatusBySubject(subjectUid);
        EHR EHR = getEHRByEHRStatus(ehrStatus);
        return EHR;
    }

    private EHRStatus getEhrStatusBySubject(UIDBasedID subjectUid) throws IOException, NotFoundException, ParseException
    {
        checkNotNull(subjectUid, "subjectUid cannot be null");
        // todo xQuery  /  efficient index
        Iterable<HierObjectID> list = m_locatableStore.list();
        for (HierObjectID hierObjectID : list)
        {
            Locatable locatable;
            try
            {
                locatable = m_locatableStore.get(hierObjectID);
            }
            catch (NotFoundException e)
            {
                continue;
            }
            if (!(locatable instanceof EHRStatus))
            {
                continue;
            }
            EHRStatus ehrStatus = (EHRStatus) locatable;
            PartySelf partySelf = ehrStatus.getSubject();
            PartyRef partyRef = partySelf.getExternalRef();
            if (partyRef == null)
            {
                continue;
            }
            ObjectID partyRefId = partyRef.getId();
            if (partyRefId.equals(subjectUid))
            {
                return ehrStatus;
            }
        }
        throw new NotFoundException(String.format("No EHR_STATUS found for subject %s", subjectUid));
    }

    private EHR getEHRByEHRStatus(EHRStatus ehrStatus) throws IOException, ParseException, NotFoundException
    {
        checkNotNull(ehrStatus, "ehrStatus cannot be null");
        UIDBasedID uid = ehrStatus.getUid();
        return getEHRByEHRStatus(uid);
    }

    private EHR getEHRByEHRStatus(UIDBasedID ehrStatusUid) throws IOException, ParseException, NotFoundException
    {
        checkNotNull(ehrStatusUid, "ehrStatusUid cannot be null");
        // todo xQuery  /  efficient index
        Iterable<HierObjectID> list = m_ehrStore.list();
        for (HierObjectID hierObjectID : list)
        {
            EHR EHR;
            try
            {
                EHR = m_ehrStore.get(hierObjectID);
            }
            catch (NotFoundException e)
            {
                continue;
            }
            ObjectRef ehrStatusRef = EHR.getEhrStatus();
            if (ehrStatusRef == null)
            {
                continue;
            }
            ObjectID ehrStatusRefId = ehrStatusRef.getId();
            if (ehrStatusUid.equals(ehrStatusRefId))
            {
                return EHR;
            }
        }
        throw new NotFoundException(String.format("No EHR found for EHR_STATUS %s", ehrStatusUid));
    }

    @Override
    public EHR getEHRForLocatable(Locatable locatable) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(locatable, "locatable cannot be null");
        UIDBasedID uid = locatable.getUid();
        return getEHRForLocatable(uid);
    }

    @Override
    public EHR getEHRForLocatable(UIDBasedID locatableUid) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(locatableUid, "locatableUid cannot be null");
        // todo xQuery / efficient index
        Iterable<HierObjectID> ehrList = m_ehrStore.list();
        for (HierObjectID ehrID : ehrList)
        {
            EHR EHR;
            try
            {
                EHR = m_ehrStore.get(ehrID);
            }
            catch (NotFoundException e)
            {
                continue;
            }
            Iterable<HierObjectID> locatableList = m_locatableStore.list(EHR);
            for (HierObjectID locatableID : locatableList)
            {
                if (locatableUid.equals(locatableID))
                {
                    return EHR;
                }
            }
        }
        throw new NotFoundException(String.format("No EHR found for locatable %s", locatableUid));
    }

    @Override
    public EHR createEHR(EHRStatus ehrStatus)
            throws NotFoundException, DuplicateException, NotSupportedException, IOException, SerializeException,
            ValidationException
    {
        checkNotNull(ehrStatus, "ehrStatus cannot be null");
        EHRStatus savedEHRStatus = (EHRStatus) m_locatableStore.insert(ehrStatus);
        UIDBasedID uid = savedEHRStatus.getUid();
        
        StoredEHR EHR = new StoredEHR(m_ehrStore, m_locatableStore, m_uidFactory, new ObjectRef(uid, "EHR", 
            "EHR_STATUS"));
        EHR savedEHR = m_ehrStore.insert(EHR);
        return savedEHR;
    }

    @Override
    public EHRStatus getEHRStatus(EHR EHR) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(EHR, "EHR cannot be null");
        ObjectRef ehrStatusRef = EHR.getEhrStatus();
        checkNotNull(ehrStatusRef, "EHR.ehrStatus cannot be null");
        ObjectID ehrStatusRefId = ehrStatusRef.getId();
        HierObjectID ehrStatusUid = new HierObjectID(ehrStatusRefId.getValue());
        return getEHRStatus(ehrStatusUid);
    }

    @Override
    public EHRStatus getEHRStatus(HierObjectID ehrStatusUid) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(ehrStatusUid, "ehrStatusUid cannot be null");
        return (EHRStatus) m_locatableStore.get(ehrStatusUid);
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
    public String findMimeTypeForXQuery(String q) throws ParseException
    {
        // unfortunately this re-parses the XQuery. There is no reasonable portable xquery object that we can pass 
        // out of the Engine API without leaking abstractions in a pretty bad way.
        
        checkNotNull(q, "q cannot be null");
        Context ctx = new Context();
        try
        {
            QueryContext qc = new QueryContext(ctx);
            QueryParser qp;
            try
            {
                qp = new QueryParser(q, null, qc);
                qp.parseMain();
            }
            catch (QueryException e)
            {
                throw new ParseException(e.getMessage(), e);
            }
            SerializerProp serializerProp = qc.serParams(false);
            String value = serializerProp.get(SerializerProp.S_MEDIA_TYPE);
            if (value == null || value.trim().isEmpty())
            {
                return null;
            }
            return value;
        }
        finally
        {
            ctx.close();
        }
    }

    @Override
    public void parseXQuery(String q) throws ParseException, UnsupportedQueryException
    {
        checkNotNull(q, "q cannot be null");
        if (QueryProcessor.isLibrary(q))
        {
            throw new UnsupportedQueryException("Provided query is a library module which is not supported");
        }
        
        Context ctx = new Context();
        try
        {
            QueryContext qc = new QueryContext(ctx);
            QueryParser qp;
            try
            {
                qp = new QueryParser(q, null, qc);
                qp.parseMain();
            }
            catch (QueryException e)
            {
                throw new ParseException(e.getMessage(), e);
            }
        }
        finally
        {
            ctx.close();
        }
    }

    @Override
    public void setAuditInfo(AuditInfo auditInfo)
    {
        m_auditInfo = checkNotNull(auditInfo, "auditInfo cannot be null");
    }

    @Override
    public void verifyStatus() throws StatusException
    {
        m_archetypeStore.verifyStatus();
        m_ehrStore.verifyStatus();
        m_locatableStore.verifyStatus();
    }

    @Override
    public String reportStatus() throws StatusException
    {
        StringBuilder result = new StringBuilder();
        result.append("archetype store: ");
        result.append(m_archetypeStore.reportStatus());
        result.append("\n");

        result.append("ehr store: ");
        result.append(m_ehrStore.reportStatus());
        result.append("\n");
        
        result.append("locatable store: ");
        result.append(m_locatableStore.reportStatus());
        result.append("\n");
        
        return result.toString();
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public boolean supportsTransactions()
    {
        return m_archetypeStore.supportsTransactions() &&
                m_ehrStore.supportsTransactions() &&
                m_locatableStore.supportsTransactions();
    }

    @Override
    public void begin() throws TransactionException
    {
        if (m_archetypeStore.supportsTransactions())
        {
            m_archetypeStore.begin();
        }
        if (m_ehrStore.supportsTransactions())
        {
            m_ehrStore.begin();
        }
        if (m_locatableStore.supportsTransactions())
        {
            m_locatableStore.begin();
        }
    }

    @Override
    public void commit() throws TransactionException
    {
        if (m_archetypeStore.supportsTransactions())
        {
            m_archetypeStore.commit();
        }
        if (m_ehrStore.supportsTransactions())
        {
            m_ehrStore.commit();
        }
        if (m_locatableStore.supportsTransactions())
        {
            m_locatableStore.commit();
        }
    }

    @Override
    public void rollback() throws TransactionException
    {
        if (m_archetypeStore.supportsTransactions())
        {
            m_archetypeStore.rollback();
        }
        if (m_ehrStore.supportsTransactions())
        {
            m_ehrStore.rollback();
        }
        if (m_locatableStore.supportsTransactions())
        {
            m_locatableStore.rollback();
        }
    }
    
    ///
    /// Helpers
    ///
    
    private <T extends TypeSelector> T selectForType(List<T> list, String mimeType, 
            String format) throws NotSupportedException
    {
        for (T option : list)
        {
            if (mimeType != null && !mimeType.equals(option.getMimeType()))
            {
                continue;
            }
            if (format != null && !format.equals(option.getFormat()))
            {
                continue;
            }
            
            return option;
        }

        throw new NotSupportedException(String.format(
                "Nothing supports mimeType %s and format %s", mimeType, format));
    }
}
