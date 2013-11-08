package com.zorggemak.controller;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.medvision360.medrecord.spi.ArchetypeParser;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.RecordException;
import com.zorggemak.commons.MiddlewareErrors;
import org.apache.commons.io.IOUtils;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.support.identification.ArchetypeID;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.HashMap;
import java.util.regex.Pattern;

import static com.zorggemak.util.WebUtils.createJsonString;
import static com.zorggemak.util.WebUtils.json2HashMap;
import static com.zorggemak.util.WebUtils.middlewareException;

@SuppressWarnings({"unchecked", "SpellCheckingInspection"})
@Controller
@RequestMapping("/v1/archetype")
public class ArchetypeController extends AbstractController {

    @RequestMapping(value = "/listarchetypes", method = RequestMethod.GET)
    public
    @ResponseBody
    String list(@RequestParam("regexpr") String regexpr, HttpServletRequest request)
            throws IOException, RecordException
    {
        HashMap result;
        String[] values;
        
        Iterable<String> archetypeIds;
        Iterable<ArchetypeID> archetypes = engine().getArchetypeStore().list();
        archetypeIds = Iterables.transform(
                archetypes,
                new Function<ArchetypeID, String>()
                {
                    @Override
                    public String apply(ArchetypeID input)
                    {
                        return input == null ? "null" : input.getValue();
                    }
                });

        if (regexpr != null && !"ALL".equals(regexpr) && !"".equals(regexpr)) {
            final Pattern p = Pattern.compile(regexpr);
            archetypeIds = Iterables.filter(archetypeIds, new Predicate<String>()
            {
                @Override
                public boolean apply(String input)
                {
                    return p.matcher(input).matches();
                }
            });
        }

        values = Iterables.toArray(archetypeIds, String.class);

        if (values.length != 0) {
            result = new HashMap();
            result.put("result", values);
            result.put("errorcode", MiddlewareErrors.OK);
        } else {
            result = middlewareException(request, MiddlewareErrors.NOTHING_FOUND, "No archetypes found");
        }
        return createJsonString(result);
    }

    @RequestMapping(value = "/archetypeexist", method = RequestMethod.GET)
    public
    @ResponseBody
    String has(@RequestParam("archid") String archid)
            throws IOException, RecordException
    {
        ArchetypeID archetypeID = new ArchetypeID(archid);
        
        HashMap result;

        result = new HashMap();
        result.put("archid", archid);
        String exists = String.valueOf(engine().getArchetypeStore().has(archetypeID));
        result.put("result", exists);
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

    @RequestMapping(value = "/getarchetype", method = RequestMethod.GET)
    public
    @ResponseBody
    String get(@RequestParam("archid") String archid,
            @RequestParam(value = "pathflag", required = false, defaultValue = "false") Boolean pflag,
            HttpServletRequest request)
            throws RecordException, IOException
    {
        HashMap result;
        String value;

        ArchetypeID archetypeID = new ArchetypeID(archid);

        result = new HashMap();
        result.put("archid", archid);
        try {
            if (pflag.booleanValue()) {
                throw new UnsupportedOperationException("path annotation not supported");
                //value = ArchetypeImplementation.retrieveArchetypeWithPathDirectives(archid);
            } else {
                value = engine().getArchetypeStore().get(archetypeID).getAsString();
            }
            result.put("result", value);
            result.put("errorcode", MiddlewareErrors.OK);
        } catch (NotFoundException e) {
            result = middlewareException(request, MiddlewareErrors.ARCHETYPE_NOT_FOUND, "Archetype doesn't exist");
        }
        return createJsonString(result);
    }

    @RequestMapping(value = "/storearchetype", method = RequestMethod.POST, params = {"archid", "archetype"})
    public
    @ResponseBody
    String insert(@RequestParam("archid") String archid, @RequestParam("archetype") String asString,
            HttpServletRequest request)
            throws IOException, RecordException
    {
        HashMap result;
        
        ArchetypeParser parser = engine().getArchetypeParser("text/plain", "adl");
        WrappedArchetype wrappedArchetype = parser.parse(IOUtils.toInputStream(asString, "UTF-8"));
        Archetype archetype = wrappedArchetype.getArchetype();

        if (archid == null)
        {
            archid = archetype.getArchetypeId().getValue();
        }
        else
        {
            try
            {
                ArchetypeID archetypeID = new ArchetypeID(archid);
                if (!archetypeID.equals(archetype.getArchetypeId()))
                {
                    throw new IllegalArgumentException(String.format(
                            "archid %s does not match ADL archetype %s", archid, 
                            archetype.getArchetypeId()));
                }
            }
            catch (IllegalArgumentException e)
            {
                result = middlewareException(request, MiddlewareErrors.ARCHETYPE_ID_NOT_CORRECT,
                        e.getMessage());
                return createJsonString(result);
            }
        }

        try
        {
            engine().getArchetypeStore().insert(wrappedArchetype);
            result = new HashMap();
            result.put("result", archid);
            result.put("errorcode", MiddlewareErrors.OK);
            return createJsonString(result);
        }
        catch (DuplicateException e)
        {
            result = middlewareException(request, MiddlewareErrors.ARCHETYPE_EXISTS,
                    "Archetype with id \"" + archid + "\" already exists");
            return createJsonString(result);
        }
    }

    @RequestMapping(value = "/storearchetype", method = RequestMethod.POST)
    public
    @ResponseBody
    String insertJSON(@RequestBody String body, HttpServletRequest request)
            throws Exception {
        HashMap map = json2HashMap(body);
        return insert((String) map.get("archid"), (String) map.get("archetype"), request);
    }

    @RequestMapping(value = "/deletearchetype", method = RequestMethod.POST, params = {"archid"})
    public
    @ResponseBody
    String delete(@RequestParam("archid") String archid, HttpServletRequest request)
            throws RecordException, IOException
    {
        HashMap result;

        try {
            ArchetypeID archetypeID = new ArchetypeID(archid);
            engine().getArchetypeStore().delete(archetypeID);
            
            result = new HashMap();
            result.put("archid", archid);
            result.put("result", "OK");
            result.put("errorcode", MiddlewareErrors.OK);
        }
        catch (NotFoundException e)
        {
            result = middlewareException(request, MiddlewareErrors.ARCHETYPE_NOT_FOUND, e.getMessage());
        }
        return createJsonString(result);
    }

    @RequestMapping(value = "/deletearchetype", method = RequestMethod.POST)
    public
    @ResponseBody
    String deleteJSON(@RequestBody String body, HttpServletRequest request)
            throws RecordException, IOException
    {
        HashMap map = json2HashMap(body);
        return delete((String) map.get("archid"), request);
    }
}
