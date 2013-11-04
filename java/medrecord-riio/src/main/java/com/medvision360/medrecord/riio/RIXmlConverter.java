package com.medvision360.medrecord.riio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.jaxen.JaxenException;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.XPath;
import org.jaxen.dom.DOMXPath;
import org.openehr.binding.XMLBinding;
import org.openehr.binding.XMLBindingException;
import org.openehr.build.SystemValue;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.support.identification.ArchetypeID;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;
import org.openehr.schemas.v1.ADDRESS;
import org.openehr.schemas.v1.ADDRESSDocument;
import org.openehr.schemas.v1.AGENT;
import org.openehr.schemas.v1.AGENTDocument;
import org.openehr.schemas.v1.CAPABILITY;
import org.openehr.schemas.v1.CAPABILITYDocument;
import org.openehr.schemas.v1.COMPOSITION;
import org.openehr.schemas.v1.CONTACT;
import org.openehr.schemas.v1.CONTACTDocument;
import org.openehr.schemas.v1.CompositionDocument;
import org.openehr.schemas.v1.GROUP;
import org.openehr.schemas.v1.GROUPDocument;
import org.openehr.schemas.v1.ItemsDocument;
import org.openehr.schemas.v1.ORGANISATION;
import org.openehr.schemas.v1.ORGANISATIONDocument;
import org.openehr.schemas.v1.PARTYIDENTITY;
import org.openehr.schemas.v1.PARTYIDENTITYDocument;
import org.openehr.schemas.v1.PARTYRELATIONSHIP;
import org.openehr.schemas.v1.PARTYRELATIONSHIPDocument;
import org.openehr.schemas.v1.PERSON;
import org.openehr.schemas.v1.PERSONDocument;
import org.openehr.schemas.v1.ROLE;
import org.openehr.schemas.v1.ROLEDocument;
import org.openehr.schemas.v1.VersionDocument;
import org.openehr.schemas.v1.VersionedObjectDocument;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import static com.google.common.base.Preconditions.checkNotNull;

public class RIXmlConverter implements LocatableParser, LocatableSerializer
{
    // currently failing with
    //   Caused by: org.apache.xmlbeans.XmlException: error: The document is not a composition@http://schemas.openehr.org/v1: multiple document elements

    private SimpleNamespaceContext m_namespaceContext = new SimpleNamespaceContext();

    {
        m_namespaceContext.addNamespace("openehr", "http://schemas.openehr.org/v1");
        m_namespaceContext.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");
    }

    private Map<String, String> m_rmEntityToXmlFactorySerializerMap = new HashMap<>();

    {
        @SuppressWarnings("SpellCheckingInspection")
        String[] serializerMapping = new String[] {
                "address", ADDRESSDocument.Factory.class.getName(),
                "agent", AGENTDocument.Factory.class.getName(),
                //"archetype",         ArchetypeDocument.Factory.class.getName(),
                "capability", CAPABILITYDocument.Factory.class.getName(),
                "contact", CONTACTDocument.Factory.class.getName(),
                "composition", CompositionDocument.Factory.class.getName(),
                //"extract",           ExtractDocument.Factory.class.getName(),
                //"extractrequest",    ExtractRequestDocument.Factory.class.getName(),
                "group", GROUPDocument.Factory.class.getName(),
                "items", ItemsDocument.Factory.class.getName(),
                "organisation", ORGANISATIONDocument.Factory.class.getName(),
                "partyidentity", PARTYIDENTITYDocument.Factory.class.getName(),
                "partyrelationship", PARTYRELATIONSHIPDocument.Factory.class.getName(),
                "person", PERSONDocument.Factory.class.getName(),
                "role", ROLEDocument.Factory.class.getName(),
                "version", VersionDocument.Factory.class.getName(),
                "versionedobject", VersionedObjectDocument.Factory.class.getName(),
        };
        for (int i = 0; i < serializerMapping.length; i++)
        {
            String rmEntity = serializerMapping[i];
            String xmlFactory = serializerMapping[++i];
            m_rmEntityToXmlFactorySerializerMap.put(rmEntity, xmlFactory);
        }

        @SuppressWarnings("SpellCheckingInspection")
        String[] parserMapping = new String[] {
                "address", ADDRESS.Factory.class.getName(),
                "agent", AGENT.Factory.class.getName(),
                //"archetype",         ArchetypeDocument.Factory.class.getName(),
                "capability", CAPABILITY.Factory.class.getName(),
                "contact", CONTACT.Factory.class.getName(),
                "composition", COMPOSITION.Factory.class.getName(),
                //"extract",           ExtractDocument.Factory.class.getName(),
                //"extractrequest",    ExtractRequestDocument.Factory.class.getName(),
                "group", GROUP.Factory.class.getName(),
                "items", ItemsDocument.Factory.class.getName(),
                "organisation", ORGANISATION.Factory.class.getName(),
                "partyidentity", PARTYIDENTITY.Factory.class.getName(),
                "partyrelationship", PARTYRELATIONSHIP.Factory.class.getName(),
                "person", PERSON.Factory.class.getName(),
                "role", ROLE.Factory.class.getName(),
                "version", VersionDocument.Factory.class.getName(),
                "versionedobject", VersionedObjectDocument.Factory.class.getName(),
        };
        for (int i = 0; i < parserMapping.length; i++)
        {
            String rmEntity = parserMapping[i];
            String xmlFactory = parserMapping[++i];
            m_rmEntityToXmlFactorySerializerMap.put(rmEntity, xmlFactory);
        }
    }

    private XMLBinding m_binding;
    private CodePhrase m_encoding;

    public RIXmlConverter(Map<SystemValue, Object> systemValues)
    {
        m_encoding = checkNotNull((CodePhrase) systemValues.get(SystemValue.ENCODING), 
                "systemValues.ENCODING cannot be null");
        m_binding = new XMLBinding(systemValues);
    }

    public RIXmlConverter(TerminologyService terminologyService, MeasurementService measurementService,
            CodePhrase encoding, CodePhrase language)
    {
        m_encoding = encoding;

        Map<SystemValue, Object> systemValues = new HashMap<>();
        systemValues.put(SystemValue.TERMINOLOGY_SERVICE, terminologyService);
        systemValues.put(SystemValue.MEASUREMENT_SERVICE, measurementService);
        systemValues.put(SystemValue.CHARSET, encoding);
        systemValues.put(SystemValue.ENCODING, encoding);
        systemValues.put(SystemValue.LANGUAGE, language);

		m_binding = new XMLBinding(systemValues);
    }

    @Override
    public Locatable parse(InputStream is) throws IOException, ParseException
    {
        return parse(is, m_encoding.getCodeString());
    }

    @Override
    public Locatable parse(InputStream is, String encoding) throws IOException, ParseException
    {
        Document doc = toDOM(is);
        String rmEntity = findRmEntity(doc);
        Class<?> xmlFactory = findXmlFactory(rmEntity);
        XmlObject xmlObject = parseXml(xmlFactory, doc);

//        System.out.println("Parsed object:\n----");
//        writeXml(xmlObject, System.out, encoding);
//        System.out.println();
//        System.out.println("----");

        Locatable result = bindToRM(xmlObject);
        return result;
    }

    @Override
    public void serialize(Locatable locatable, OutputStream os) throws IOException, SerializeException
    {
        serialize(locatable, os, m_encoding.getCodeString());
    }

    @Override
    public void serialize(Locatable locatable, OutputStream os, String encoding) throws IOException, SerializeException
    {
        XmlObject xmlObject = bindToXML(locatable);
//        System.out.println("Serialized object:\n----");
//        writeXml(xmlObject, System.out, encoding);
//        System.out.println();
//        System.out.println("----");
        writeXml(xmlObject, os, encoding);
    }

    public Document toDOM(InputStream is) throws ParseException, IOException
    {
        try
        {
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            dbFactory.setNamespaceAware(true);
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(is);
            Element root = doc.getDocumentElement();
            root.normalize();

            return doc;
        }
        catch (ParserConfigurationException e)
        {
            throw new ParseException(e);
        }
        catch (SAXException e)
        {
            throw new IOException(e);
        }
    }

    public String findRmEntity(Document doc) throws ParseException
    {
        try
        {
            Element root = doc.getDocumentElement();
            Object result;
            ArchetypeID archetypeID;

            result = selectSingleNode(root, "/openehr:*/openehr:archetype_details" +
                    "/openehr:archetype_id/openehr:value/text()");
            if (result != null)
            {
                archetypeID = new ArchetypeID(getString(result));
                return archetypeID.rmEntity();
            }

            result = selectSingleNode(root, "/openehr:*/@openehr:archetype_node_id");
            if (result != null)
            {
                archetypeID = new ArchetypeID(getString(result));
                return archetypeID.rmEntity();
            }

            result = selectSingleNode(root, "/openehr:*/@xsi:type");
            if (result != null)
            {
                return getString(result);
            }

            throw new ParseException("Cannot determine RM Entity");
        }
        catch (JaxenException e)
        {
            throw new ParseException(e);
        }
    }

    public String getString(Object value)
    {
        if (value == null)
        {
            return null;
        }
        if (value instanceof String)
        {
            return (String) value;
        }
        if (value instanceof CharSequence)
        {
            return value.toString();
        }
        if (value instanceof CharacterData)
        {
            return ((CharacterData) value).getData();
        }
        if (value instanceof Node)
        {
            return ((Node) value).getTextContent();
        }
        return value.toString();
    }

    public Object selectSingleNode(Element root, String xpath) throws JaxenException
    {
        XPath x = new DOMXPath(xpath);
        x.setNamespaceContext(m_namespaceContext);
        return x.selectSingleNode(root);
    }

    public Class<?> findXmlFactory(String rmEntity) throws ParseException
    {
        try
        {
            String factoryClass =
                    m_rmEntityToXmlFactorySerializerMap.get(rmEntity.toLowerCase().replaceAll("[_\\.-]", ""));
            if (factoryClass == null)
            {
                throw new ParseException(String.format("Cannot handle rmEntity %s", rmEntity));
            }
            Class<?> factory = Class.forName(factoryClass);
            return factory;
        }
        catch (ClassNotFoundException e)
        {
            throw new ParseException(e);
        }
    }

    public XmlObject parseXml(Class<?> xmlFactory, Document doc) throws ParseException
    {
        try
        {
            XmlOptions xmlOptions = new XmlOptions();
            xmlOptions.setLoadReplaceDocumentElement(null);

            Method factoryMethod = xmlFactory.getMethod("parse", Node.class, XmlOptions.class);
            Object xmlObject = factoryMethod.invoke(null, doc, xmlOptions);
            if (!(xmlObject instanceof XmlObject))
            {
                throw new ParseException(String.format("Binding resulted in $s, which is not a XmlObject",
                        xmlObject.getClass().getName()));
            }
            return (XmlObject) xmlObject;
        }
        catch (NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
        {
            throw new ParseException(e);
        }
    }

    public Locatable bindToRM(Object xmlBean) throws ParseException
    {
        try
        {
            Object rmObject = m_binding.bindToRM(xmlBean);
            if (!(rmObject instanceof Locatable))
            {
                throw new ParseException(String.format("Binding resulted in %s, which is not a locatable",
                        rmObject.getClass().getName()));
            }
            return (Locatable) rmObject;
        }
        catch (Exception e)
        {
            if (e instanceof ParseException)
            {
                throw (ParseException) e;
            }
            throw new ParseException(e);
        }
    }

    public XmlObject bindToXML(Locatable locatable) throws SerializeException
    {
        try
        {
            Object xmlObject = m_binding.bindToXML(locatable, true);
            if (!(xmlObject instanceof XmlObject))
            {
                throw new SerializeException(String.format("Binding resulted in $s, which is not a XmlObject",
                        xmlObject.getClass().getName()));
            }
            return (XmlObject) xmlObject;
        }
        catch (XMLBindingException e)
        {
            throw new SerializeException(e);
        }
    }

    public void writeXml(XmlObject xmlObject, OutputStream os, String encoding) throws IOException
    {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("http://schemas.openehr.org/v1", "");
        namespaces.put("http://www.w3.org/2001/XMLSchema-instance", "xsi");

        XmlOptions xmlOptions = new XmlOptions();
        xmlOptions.setSaveSuggestedPrefixes(namespaces);
        xmlOptions.setSaveAggressiveNamespaces();
        xmlOptions.setSavePrettyPrint();
        xmlOptions.setCharacterEncoding(encoding);
        xmlOptions.setSaveOuter();
        xmlOptions.setSaveNamespacesFirst();

        m_namespaceContext.addNamespace("openehr", "http://schemas.openehr.org/v1");
        m_namespaceContext.addNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        xmlObject.save(os, xmlOptions);
    }

    @Override
    public String getMimeType()
    {
        return "application/xml";
    }

    @Override
    public String getFormat()
    {
        return "xml";
    }

    @Override
    public boolean supports(Locatable test)
    {
        if (test == null)
        {
            return false;
        }
        return supports(test.getArchetypeDetails());
    }

    @Override
    public boolean supports(Archetyped test)
    {
        if (test == null)
        {
            return false;
        }
        ArchetypeID archetypeID = test.getArchetypeId();
        if (archetypeID == null)
        {
            return false;
        }
        String rmEntity = archetypeID.rmEntity();
        if (rmEntity == null)
        {
            return false;
        }
        return m_rmEntityToXmlFactorySerializerMap.containsKey(rmEntity.toLowerCase());
    }
}
