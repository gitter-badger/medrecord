package com.medvision360.medrecord.engine;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

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
import com.medvision360.medrecord.spi.Terminology;
import com.medvision360.medrecord.spi.TransformingXQueryStore;
import com.medvision360.medrecord.spi.TypeSelector;
import com.medvision360.medrecord.spi.ValidatingXQueryStore;
import com.medvision360.medrecord.spi.XQueryStore;
import com.medvision360.medrecord.spi.base.BaseLocatableEditor;
import com.medvision360.medrecord.spi.exceptions.DisposalException;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.InitializationException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import com.medvision360.medrecord.spi.exceptions.StatusException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.basex.core.Context;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.demographic.Person;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.UIDBasedID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.measurement.SimpleMeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;
import org.openehr.terminology.SimpleTerminologyService;

import static com.google.common.base.Preconditions.checkNotNull;

public class MedRecordEngine implements Engine, AuditService
{
    private boolean m_storeValidation = true;
    private CodePhrase m_encoding;
    private CodePhrase m_language;
    private CodePhrase m_territory;
    private TerminologyService m_terminologyService;
    private MeasurementService m_measurementService;
    private Map<SystemValue, Object> m_systemValues = new HashMap<>();

    private List<Context> m_baseXContexts = new LinkedList<>();

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

    public void initialize() throws InitializationException
    {
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
    }

    private void initializeArchetypeSupport()
    {
        RIAdlConverter adlConverter = new RIAdlConverter();
        m_archetypeParsers.add(adlConverter);
        m_archetypeSerializers.add(adlConverter);

        XmlWrappedArchetypeConverter wrappedConverter = new XmlWrappedArchetypeConverter(adlConverter);
        m_archetypeParsers.add(wrappedConverter);
        m_archetypeSerializers.add(wrappedConverter);

        m_archetypeStore = new BaseXArchetypeStore(newContext(), wrappedConverter, wrappedConverter,
                m_name + "Archetypes", "archetype");
    }

    private void initializeEHRSupport()
    {
        XmlEHRConverter xmlConverter = new XmlEHRConverter();
        m_ehrParsers.add(xmlConverter);
        m_ehrSerializers.add(xmlConverter);
        
        HierObjectID ehrSystemID = new HierObjectID(m_name + "EHR");
        m_ehrStore = new BaseXEHRStore(newContext(), xmlConverter, xmlConverter, ehrSystemID, "ehr");
    }

    private void initializeLocatableSupport() throws InitializationException
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

        LocatableSelector ehrSelector = LocatableSelectorBuilder
                .start()
                .requireRMName("EHR")
                .matchRMEntity("^(?:COMPOSITION|EHRSTATUS)$")
                .build();
        baseXContext = new Context();
        m_baseXContexts.add(baseXContext);
        BaseXLocatableStore ehrStore = new BaseXLocatableStore(newContext(), xmlConverter, xmlConverter,
                ehrSelector, m_name + "EHR", "ehr");

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
        
        CompositeStore compositeStore = new CompositeStore(m_name + "STORE");
        if (m_storeValidation)
        {
            LocatableSelector validatingSelector = m_locatableValidator;
            ValidatingXQueryStore validatingEhrStore = new ValidatingXQueryStore(
                    ehrStore.getName(), validatingSelector, ehrStore, m_locatableValidator);
            compositeStore.addDelegate(validatingEhrStore);
            
            ValidatingXQueryStore validatingDemographicStore = new ValidatingXQueryStore(
                    demographicStore.getName(), validatingSelector, demographicStore, m_locatableValidator);
            compositeStore.addDelegate(validatingDemographicStore);
        }
        else
        {
            compositeStore.addDelegate(ehrStore);
            compositeStore.addDelegate(demographicStore);
        }
        
        UIDGenerator uidGenerator = new UIDGenerator();
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
        for (Context context : m_baseXContexts)
        {
            context.close();
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
        m_auditInfo = checkNotNull(auditInfo, "auditInfo cannot be null");
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
        return m_name;
    }

    @Override
    public boolean supportsTransactions()
    {
        return false;
    }

    @Override
    public void begin() throws TransactionException
    {
    }

    @Override
    public void commit() throws TransactionException
    {
    }

    @Override
    public void rollback() throws TransactionException
    {
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
