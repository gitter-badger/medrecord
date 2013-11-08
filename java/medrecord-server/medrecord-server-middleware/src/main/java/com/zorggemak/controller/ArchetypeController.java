package com.zorggemak.controller;

import com.medvision360.kernel.engine.archetype.ArchetypePathMap;
import com.medvision360.kernel.engine.archetype.TermDefinitionMap;
import com.medvision360.kernel.engine.archetype.ArchetypeImplementation;
import com.medvision360.kernel.engine.archetype.ArchetypeExistsException;
import com.medvision360.kernel.engine.archetype.ArchetypeNotFoundException;
import com.medvision360.kernel.engine.KernelException;
import com.medvision360.kernel.xmlutils.XSUtils;
import com.zorggemak.commons.MiddlewareErrors;
import org.openehr.am.serialize.ADLMapPathNodeID;
import org.openehr.am.serialize.ADLSerializerException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

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
    String listarchetypes(@RequestParam("regexpr") String regexpr, HttpServletRequest request)
            throws KernelException {
        HashMap result;
        String[] values;

        if ("ALL".equals(regexpr) || "".equals(regexpr)) {
            List<String> results = ArchetypeImplementation.listArchetypeIds();
            values = results.toArray(new String[results.size()]);
        } else {
            List<String> results = ArchetypeImplementation.listArchetypeIds(regexpr);
            values = results.toArray(new String[results.size()]);
        }
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
    String archetypeexist(@RequestParam("archid") String archid)
            throws KernelException {
        HashMap result;

        result = new HashMap();
        result.put("archid", archid);
        if (ArchetypeImplementation.archetypeExists(archid)) {
            result.put("result", "true");
        } else {
            result.put("result", "false");
        }
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

    @RequestMapping(value = "/getarchetype", method = RequestMethod.GET)
    public
    @ResponseBody
    String getarchetype(@RequestParam("archid") String archid,
                        @RequestParam(value = "pathflag", required = false, defaultValue = "false") Boolean pflag,
                        HttpServletRequest request)
            throws KernelException {
        HashMap result;
        String value;

        result = new HashMap();
        result.put("archid", archid);
        try {
            if (pflag.booleanValue()) {
                value = ArchetypeImplementation.retrieveArchetypeWithPathDirectives(archid);
            } else {
                value = ArchetypeImplementation.retrieveArchetype(archid);
            }
            result.put("result", value);
            result.put("errorcode", MiddlewareErrors.OK);
        } catch (ArchetypeNotFoundException e) {
            result = middlewareException(request, MiddlewareErrors.ARCHETYPE_NOT_FOUND, "Archetype doesn't exist");
        }
        return createJsonString(result);
    }

    @RequestMapping(value = "/storearchetype", method = RequestMethod.POST, params = {"archid", "archetype"})
    public
    @ResponseBody
    String storearchetype(@RequestParam("archid") String archid, @RequestParam("archetype") String archetype,
                          HttpServletRequest request)
            throws KernelException {
        HashMap result;
        String id = null;

        try {
            ArchetypeImplementation.storeArchetype(archid, archetype);
            result = new HashMap();
            result.put("result", id);
            result.put("errorcode", MiddlewareErrors.OK);
        } catch (ArchetypeExistsException e) {
            result = middlewareException(request, MiddlewareErrors.ARCHETYPE_EXISTS,
                    "Archetype with id \"" + id + "\" already exists");
        } catch (IllegalArgumentException e) {
            result = middlewareException(request, MiddlewareErrors.ARCHETYPE_ID_NOT_CORRECT,
                    e.getMessage());
        }
        return createJsonString(result);
    }

    @RequestMapping(value = "/storearchetype", method = RequestMethod.POST)
    public
    @ResponseBody
    String storearchetypeWithREST(@RequestBody String body, HttpServletRequest request)
            throws Exception {
        HashMap map;

        map = json2HashMap(body);
        return storearchetype((String) map.get("archid"), (String) map.get("archetype"), request);
    }

    @RequestMapping(value = "/deletearchetype", method = RequestMethod.POST, params = {"archid"})
    public
    @ResponseBody
    String deletearchetype(@RequestParam("archid") String archid, HttpServletRequest request)
            throws KernelException {
        HashMap result;

        try {
            ArchetypeImplementation.removeArchetype(archid);
            result = new HashMap();
            result.put("archid", archid);
            result.put("result", "OK");
            result.put("errorcode", MiddlewareErrors.OK);
        } catch (ArchetypeNotFoundException e) {
            result = middlewareException(request, MiddlewareErrors.ARCHETYPE_NOT_FOUND, e.getMessage());
        }
        return createJsonString(result);
    }

    @RequestMapping(value = "/deletearchetype", method = RequestMethod.POST)
    public
    @ResponseBody
    String deletearchetypeWithREST(@RequestBody String body, HttpServletRequest request)
            throws Exception {
        HashMap map;

        map = json2HashMap(body);
        return deletearchetype((String) map.get("archid"), request);
    }

    @RequestMapping(value = "/makeshortpath", method = RequestMethod.GET)
    public
    @ResponseBody
    String makeshortpath(@RequestParam("longpath") String lpath)
            throws ADLSerializerException {
        HashMap result;

        result = new HashMap();
        result.put("longpath", lpath);
        result.put("result", ADLMapPathNodeID.makeShortPath(lpath));
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

    @RequestMapping(value = "/resolveidtolongpath", method = RequestMethod.GET)
    public
    @ResponseBody
    String resolveidtolongpath(@RequestParam("archid") String archid)
            throws Exception {
        HashMap result;

        ArchetypePathMap apu = ArchetypePathMap.getInstance(archid);
        String path = apu.archetypeNodeIDPathMap();
        result = new HashMap();
        result.put("archid", archid);
        result.put("result", path);
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

    @RequestMapping(value = "/termdefinitionmap", method = RequestMethod.GET)
    public
    @ResponseBody
    String termdefinitionmap(@RequestParam("lang") String lang, @RequestParam("archid") String archid)
            throws Exception {
        HashMap result;

        result = new HashMap();
        result.put("lang", lang);
        result.put("archid", archid);
        TermDefinitionMap apu = TermDefinitionMap.getInstance(archid);
        result.put("result", apu.termDefinitionMap(lang));
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

    @RequestMapping(value = "/termdefinitionlanguages", method = RequestMethod.GET)
    public
    @ResponseBody
    String termdefinitionlanguages(@RequestParam("archid") String archid, HttpServletRequest request)
            throws Exception {
        HashMap result;
        String[] values;

        TermDefinitionMap apu = TermDefinitionMap.getInstance(archid);

        Set<String> l = apu.languagesAvailable();
        if (l.size() == 0) {
            throw new Exception("No languages are found in this Term definition:" + archid);
        } else {
            values = l.toArray(new String[l.size()]);
        }
        if (values.length != 0) {
            result = new HashMap();
            result.put("archid", archid);
            result.put("result", values);
            result.put("errorcode", MiddlewareErrors.OK);
        } else {
            result = middlewareException(request, MiddlewareErrors.NOTHING_FOUND, "No languages found");
        }
        ;
        return createJsonString(result);
    }

    @RequestMapping(value = "/adltoxpath", method = RequestMethod.GET)
    public
    @ResponseBody
    String adltoxpath(@RequestParam("adlpath") String adlpath) {
        HashMap result;

        result = new HashMap();
        result.put("adlpath", adlpath);
        result.put("result", XSUtils.transformArchetypePathToSimpleXPath(adlpath));
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

}
