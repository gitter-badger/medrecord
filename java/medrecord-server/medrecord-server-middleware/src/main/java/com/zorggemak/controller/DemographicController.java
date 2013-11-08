package com.zorggemak.controller;

import com.medvision360.kernel.locatableutils.LocatableUtilsException;
import com.medvision360.kernel.locatableutils.demographics.DemographicUtils;
import com.zorggemak.commons.AuditException;
import com.zorggemak.commons.MiddlewareErrors;
import com.zorggemak.data.ArchetypeObject;
import com.zorggemak.util.ObjectHelper;
import com.zorggemak.util.WebUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static com.zorggemak.commons.ZorgGemakDefines.USE_VALIDATION;
import static com.zorggemak.util.AuditHelper.auditParam;
import static com.zorggemak.util.WebUtils.json2HashMap;

@SuppressWarnings({"unchecked", "SpellCheckingInspection"})
@Controller
@RequestMapping("/v1/demographic")
public class DemographicController extends AbstractController {

    @RequestMapping(value = "/addrelationship", method = RequestMethod.POST)
    public
    @ResponseBody
    String addrelationshipWithREST(@RequestBody String body, HttpServletRequest request)
            throws LocatableUtilsException, AuditException {
        HashMap map;

        map = json2HashMap(body);
        return addrelationship((String) map.get("sourceid"), (String) map.get("targetid"), (String[]) map.get("paths"),
                (String[]) map.get("values"), request);
    }

    private String addrelationship(String sourceid, String targetid, String[] paths, String[] values,
                                   HttpServletRequest request)
            throws AuditException, LocatableUtilsException {
        HashMap result;
        String value;

        if (sourceid != null) {
            value = DemographicUtils.addPartyRelationship(auditParam(request), sourceid, targetid, paths, values,
                    USE_VALIDATION);
            result = new HashMap();
            result.put("sourceid", sourceid);
            result.put("targetid", targetid);
            result.put("result", value);
            result.put("errorcode", MiddlewareErrors.OK);
        } else {
            result = WebUtils.middlewareException(request, MiddlewareErrors.OBJECT_NOT_FOUND, "Node not found");
        }
        return WebUtils.createJsonString(result);
    }

    @RequestMapping(value = "/removerelationship", method = RequestMethod.POST, params = {"relationshipid"})
    public
    @ResponseBody
    String removerelationship(@RequestParam("relationshipid") String relationshipid, HttpServletRequest request)
            throws AuditException, LocatableUtilsException {
        HashMap result;

        DemographicUtils.removePartyRelationship(auditParam(request), relationshipid, USE_VALIDATION);
        result = new HashMap();
        result.put("relationshipid", relationshipid);
        result.put("result", "OK");
        result.put("errorcode", MiddlewareErrors.OK);
        return WebUtils.createJsonString(result);
    }

    @RequestMapping(value = "/removerelationship", method = RequestMethod.POST)
    public
    @ResponseBody
    String removerelationshipWithREST(@RequestBody String body, HttpServletRequest request)
            throws LocatableUtilsException, AuditException {
        HashMap map;

        map = json2HashMap(body);
        return removerelationship((String) map.get("relationshipid"), request);
    }

    @RequestMapping(value = "/countactors", method = RequestMethod.GET)
    public
    @ResponseBody
    String countactors()
            throws Exception {
        HashMap result;
        long cnt;

        cnt = DemographicUtils.countActors();
        result = new HashMap();
        result.put("result", new Long(cnt));
        result.put("errorcode", MiddlewareErrors.OK);
        return WebUtils.createJsonString(result);
    }

    @RequestMapping(value = "/getactors", method = RequestMethod.GET)
    public
    @ResponseBody
    String getactors(@RequestParam("offset") String offset, @RequestParam("limit") String limit,
                     HttpServletRequest request)
            throws LocatableUtilsException {
        HashMap result;
        String[] values;

        if (offset != null && limit != null) {
            values = DemographicUtils.listActors(Long.parseLong(offset), Long.parseLong(limit));
            if (values.length != 0) {
                result = new HashMap();
                result.put("offset", offset);
                result.put("limit", limit);
                result.put("result", values);
                result.put("errorcode", MiddlewareErrors.OK);
            } else {
                result = WebUtils.middlewareException(request, MiddlewareErrors.NOTHING_FOUND, "No actors found");
            }
        } else {
            result = WebUtils.middlewareException(request, MiddlewareErrors.PARAMETERS_NOT_CORRECT,
                    "Parameters not correct");
        }
        return WebUtils.createJsonString(result);
    }

    @RequestMapping(value = "/getactor", method = RequestMethod.GET)
    public
    @ResponseBody
    String getactor(@RequestParam("guid") String guid, HttpServletRequest request) {
        ArchetypeObject actor;
        HashMap result;

        actor = ObjectHelper.getArchetypeObject(guid, true);
        if (actor != null) {
            return ObjectHelper.getPaths(actor.getObjectId(), request).replaceAll("objid", "actorid");
        } else {
            result = WebUtils.middlewareException(request, MiddlewareErrors.OBJECT_NOT_FOUND, "Actor not found");
        }
        return WebUtils.createJsonString(result);
    }

    @RequestMapping(value = "/createactor", method = RequestMethod.POST, params = {"paths", "values"})
    public
    @ResponseBody
    String createactor(@RequestParam("paths") String paths, @RequestParam("values") String values,
                       HttpServletRequest request)
            throws LocatableUtilsException, AuditException {
        String[] ppars;
        String[] vpars;

        ppars = WebUtils.json2StringArray(paths);
        vpars = WebUtils.json2StringArray(values);
        return createactor(ppars, vpars, request);
    }

    @RequestMapping(value = "/createactor", method = RequestMethod.POST)
    public
    @ResponseBody
    String createactorWithREST(@RequestBody String body, HttpServletRequest request)
            throws LocatableUtilsException, AuditException {
        HashMap map;

        map = json2HashMap(body);
        return createactor((String[]) map.get("paths"), (String[]) map.get("values"), request);
    }

    private String createactor(String[] paths, String[] values, HttpServletRequest request)
            throws AuditException, LocatableUtilsException {
        HashMap result;
        String value;

        value = DemographicUtils.saveDemographicNew(auditParam(request), paths, values, USE_VALIDATION);
        result = new HashMap();
        result.put("result", value);
        result.put("errorcode", MiddlewareErrors.OK);
        return WebUtils.createJsonString(result);
    }

    @RequestMapping(value = "/modifyactor/{actorid}", method = RequestMethod.POST, params = {"paths", "values"})
    public
    @ResponseBody
    String modifyactor(@PathVariable("actorid") String actorid, @RequestParam("paths") String paths,
                       @RequestParam("values") String values, HttpServletRequest request)
            throws LocatableUtilsException, AuditException {
        String[] ppars;
        String[] vpars;

        ppars = WebUtils.json2StringArray(paths);
        vpars = WebUtils.json2StringArray(values);
        return modifyactor(actorid, ppars, vpars, request);
    }

    @RequestMapping(value = "/modifyactor/{actorid}", method = RequestMethod.POST)
    public
    @ResponseBody
    String modifyactorWithREST(@PathVariable("actorid") String actorid, @RequestBody String body,
                               HttpServletRequest request)
            throws LocatableUtilsException, AuditException {
        HashMap map = json2HashMap(body);
        return modifyactor(actorid, (String[]) map.get("paths"), (String[]) map.get("values"), request);
    }

    private String modifyactor(String actorid, String[] paths, String[] values, HttpServletRequest request)
            throws AuditException, LocatableUtilsException {
        ArchetypeObject actor;
        HashMap result;
        if ((actor = ObjectHelper.getArchetypeObject(actorid, false)) != null) {
            DemographicUtils.saveDemographicModified(auditParam(request), paths, values, USE_VALIDATION);
            actor.setPrepareForPost(false);
            result = new HashMap();
            result.put("actorid", actorid);
            result.put("result", "OK");
            result.put("errorcode", MiddlewareErrors.OK);
        } else {
            result = WebUtils.middlewareException(request, MiddlewareErrors.OBJECT_NOT_FOUND,
                    "Actor with id " + actorid + " not found");
        }
        return WebUtils.createJsonString(result);
    }

    @RequestMapping(value = "/deleteactor", method = RequestMethod.POST, params = {"guid"})
    public
    @ResponseBody
    String deleteactor(@RequestParam("guid") String guid, HttpServletRequest request)
            throws Exception {
        return removeActor(guid, false, request);
    }

    @RequestMapping(value = "/deleteactor", method = RequestMethod.POST)
    public
    @ResponseBody
    String deleteactorWithREST(@RequestBody String body, HttpServletRequest request)
            throws Exception {
        HashMap map = json2HashMap(body);
        return removeActor((String) map.get("guid"), false, request);
    }

    @RequestMapping(value = "/undeleteactor", method = RequestMethod.POST, params = {"guid"})
    public
    @ResponseBody
    String undeleteactor(@RequestParam("guid") String guid, HttpServletRequest request)
            throws Exception {
        return removeActor(guid, true, request);
    }

    @RequestMapping(value = "/undeleteactor", method = RequestMethod.POST)
    public
    @ResponseBody
    String undeleteactorWithREST(@RequestBody String body, HttpServletRequest request)
            throws Exception {
        HashMap map = json2HashMap(body);
        return removeActor((String) map.get("guid"), true, request);
    }

    private String removeActor(String guid, boolean undel, HttpServletRequest request)
            throws Exception {
        HashMap result;

        if (undel) {
            DemographicUtils.undeleteDemographic(auditParam(request), guid, USE_VALIDATION);
        } else {
            DemographicUtils.deleteDemographic(auditParam(request), guid);
        }
        result = new HashMap();
        result.put("guid", guid);
        result.put("result", "OK");
        result.put("errorcode", MiddlewareErrors.OK);
        return WebUtils.createJsonString(result);
    }

    @RequestMapping(value = "/retrievedemographic", method = RequestMethod.GET)
    public
    @ResponseBody
    String retrievedemographic(@RequestParam("guid") String guid, @RequestParam("ignoredel") Boolean ignoredel)
            throws LocatableUtilsException {
        HashMap result;
        String value = DemographicUtils.retrieveDemographicLocatable(guid, ignoredel);
        result = new HashMap();
        result.put("guid", guid);
        result.put("result", value);
        result.put("errorcode", MiddlewareErrors.OK);
        return WebUtils.createJsonString(result);
    }

    @RequestMapping(value = "/retrievedemographic/spn", method = RequestMethod.GET)
    public
    @ResponseBody
    String retrievedemographic_spn(@RequestParam("guid") String guid, @RequestParam("ignoredel") Boolean ignoredel)
            throws LocatableUtilsException {
        HashMap result;
        String value = DemographicUtils.retrieveDemographicLocatableSPN(guid, ignoredel);
        result = new HashMap();
        result.put("guid", guid);
        result.put("result", value);
        result.put("errorcode", MiddlewareErrors.OK);
        return WebUtils.createJsonString(result);
    }

}
