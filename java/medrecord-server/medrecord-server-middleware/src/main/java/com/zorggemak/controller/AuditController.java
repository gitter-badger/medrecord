package com.zorggemak.controller;

import com.medvision360.kernel.auditutils.AuditUtils;
import com.medvision360.kernel.auditutils.AuditUtilsException;
import com.zorggemak.commons.MiddlewareErrors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;

import static com.zorggemak.util.WebUtils.createJsonString;
import static com.zorggemak.util.WebUtils.notSupported;

@SuppressWarnings({"unchecked", "SpellCheckingInspection"})
@Controller
@RequestMapping("/v1/audit")
public class AuditController extends AbstractController {

    @RequestMapping(value = "/auditinformation", method = RequestMethod.GET)
    public
    @ResponseBody
    String auditinformation(@RequestParam("auditid") String auditid)
            throws AuditUtilsException {
        HashMap result;

        result = new HashMap();
        result.put("auditid", auditid);
        result.put("result", AuditUtils.getContributionInformation(auditid));
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

    @RequestMapping(value = "/audituuidsforauditableobject", method = RequestMethod.GET)
    public
    @ResponseBody
    String audituuidsforauditableobject(HttpServletRequest request) { // todo
        return notSupported(request);
    }

    @RequestMapping(value = "/audituidsforperson", method = RequestMethod.GET)
    public
    @ResponseBody
    String audituidsforperson(HttpServletRequest request) { // todo
        return notSupported(request);
    }

    @RequestMapping(value = "/versionuidsofversionedobject", method = RequestMethod.GET)
    public
    @ResponseBody
    String versionuidsofversionedobject(@RequestParam("objectuid") String ouid, @RequestParam("begindate") String bdate,
                                        @RequestParam("enddate") String edate)
            throws Exception {
        HashMap result;

        result = new HashMap();
        result.put("objectuid", ouid);
        result.put("begindate", bdate);
        result.put("enddate", edate);
        result.put("result", AuditUtils.getVersionUIDsOfVersionedObject(ouid, bdate, edate));
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

    @RequestMapping(value = "/versionedobject", method = RequestMethod.GET)
    public
    @ResponseBody
    String versionedobject(@RequestParam("objectuid") String objectid)
            throws Exception {
        HashMap result;

        result = new HashMap();
        result.put("objectuid", objectid);
        result.put("result", AuditUtils.getVersionedObject(objectid));
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

    @RequestMapping(value = "/contributionuuidsforcommitter", method = RequestMethod.GET)
    public
    @ResponseBody
    String contributionuuidsforcommitter(@RequestParam("personuid") String personuid,
                                         @RequestParam("begindate") String begindate,
                                         @RequestParam("enddate") String enddate)
            throws Exception {
        HashMap result;

        result = new HashMap();
        result.put("personuid", personuid);
        result.put("begindate", begindate);
        result.put("enddate", enddate);
        result.put("result", AuditUtils.getContributionUUIDsForCommitter(personuid, begindate, enddate));
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

    @RequestMapping(value = "/contributionuuidsforobject", method = RequestMethod.GET)
    public
    @ResponseBody
    String contributionuuidsforobject(@RequestParam("objectuid") String objectuid,
                                      @RequestParam("begindate") String begindate,
                                      @RequestParam("enddate") String enddate)
            throws Exception {
        HashMap result;

        result = new HashMap();
        result.put("objectuid", objectuid);
        result.put("begindate", begindate);
        result.put("enddate", enddate);
        result.put("result", AuditUtils.getContributionUUIDsForObject(objectuid, begindate, enddate));
        result.put("errorcode", MiddlewareErrors.OK);
        return createJsonString(result);
    }

}
