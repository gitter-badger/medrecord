package com.medvision360.medrecord.riio;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.openehr.am.parser.ContentObject;
import org.openehr.am.parser.DADLParser;
import org.openehr.build.RMObjectBuildingException;
import org.openehr.build.SystemValue;
import org.openehr.rm.binding.DADLBinding;
import org.openehr.rm.binding.DADLBindingException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.datatypes.text.CodePhrase;
import org.openehr.rm.support.measurement.MeasurementService;
import org.openehr.rm.support.terminology.TerminologyService;

public class RIDadlConverter implements LocatableParser, LocatableSerializer
{
    private DADLBinding binding;
    private CodePhrase m_charset;

    public RIDadlConverter(TerminologyService terminologyService, MeasurementService measurementService,
            CodePhrase charset, CodePhrase language)
    {
        m_charset = charset;

        Map<SystemValue, Object> systemValues = new HashMap<>();
        systemValues.put(SystemValue.TERMINOLOGY_SERVICE, terminologyService);
        systemValues.put(SystemValue.MEASUREMENT_SERVICE, measurementService);
        systemValues.put(SystemValue.CHARSET, charset);
        systemValues.put(SystemValue.ENCODING, charset);
        systemValues.put(SystemValue.LANGUAGE, language);

        binding = new DADLBinding(systemValues);
    }

    @Override
    public Locatable parse(InputStream is) throws IOException, ParseException
    {
        return parse(is, m_charset.getCodeString());
    }

    @Override
    public Locatable parse(InputStream is, String encoding) throws IOException, ParseException
    {
        try
        {
            DADLParser parser = new DADLParser(is);
            ContentObject content = parser.parse();
            Object object = binding.bind(content);
            if (!(object instanceof Locatable))
            {
                throw new ParseException(String.format("DADL parsing returned a %s, need a Locatable",
                        object.getClass().getName()));
            }
            Locatable locatable = (Locatable) object;
            return locatable;
        }
        catch (org.openehr.am.parser.ParseException | RMObjectBuildingException | DADLBindingException e)
        {
            throw new ParseException(e);
        }
    }

    @Override
    public void serialize(Locatable locatable, OutputStream os) throws IOException, SerializeException
    {
        serialize(locatable, os, m_charset.getCodeString());
    }

    @Override
    public void serialize(Locatable locatable, OutputStream os, String encoding) throws IOException, SerializeException
    {
        try
        {
            List<String> result = binding.toDADL(locatable);
            OutputStreamWriter osw = new OutputStreamWriter(os, encoding);
            for (String line : result)
            {
                osw.write(line);
                osw.write('\n');
            }
            osw.flush();
        }
        catch (IllegalAccessException | InvocationTargetException e)
        {
            throw new SerializeException(e);
        }
    }

    @Override
    public String getMimeType()
    {
        return "text/plain";
    }

    @Override
    public String getFormat()
    {
        return "dadl";
    }

    @Override
    public boolean supports(Locatable test)
    {
        return true;
    }

    @Override
    public boolean supports(Archetyped test)
    {
        return true;
    }
}
