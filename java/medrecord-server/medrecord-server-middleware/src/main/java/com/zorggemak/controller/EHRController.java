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
package com.zorggemak.controller;

import com.zorggemak.commons.AuditException;
import com.zorggemak.commons.MiddlewareErrors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

import static com.zorggemak.commons.ZorgGemakDefines.USE_VALIDATION;
import static com.zorggemak.util.AuditHelper.auditParam;
import static com.zorggemak.util.WebUtils.createJsonString;
import static com.zorggemak.util.WebUtils.json2HashMap;
import static com.zorggemak.util.WebUtils.json2StringArray;
import static com.zorggemak.util.WebUtils.middlewareException;
import static com.zorggemak.util.WebUtils.parseEHRXml;

@SuppressWarnings({"unchecked", "SpellCheckingInspection"})
@Controller
@RequestMapping("/v1/ehr")
public class EHRController {

//    @RequestMapping(value = "/getehr", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String getehr(@RequestParam("userid") String userid, HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//        String xml = EHRUtils.getEHRByUid(userid);
//        Map values = parseEHRXml(xml);
//        if (values != null && values.containsKey("ehr_id")) {
//            result = new HashMap();
//            result.put("time_created", values.get("time_created"));
//            result.put("ehr_status_uid", values.get("ehr_status_uid"));
//            result.put("userid", userid);
//            result.put("result", values.get("ehr_id"));
//            result.put("errorcode", MiddlewareErrors.OK);
//        } else {
//            result = middlewareException(request, MiddlewareErrors.OBJECT_NOT_FOUND,
//                    "EHR for user " + userid + " not found");
//        }
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/createehr", method = RequestMethod.POST, params = {"paths", "values"})
//    public
//    @ResponseBody
//    String createehr(@RequestParam("paths") String paths, @RequestParam("values") String values,
//                     HttpServletRequest request)
//            throws EHRUtilsException, AuditException {
//        String[] ppars = json2StringArray(paths);
//        String[] vpars = json2StringArray(values);
//        return createehr(ppars, vpars, request);
//    }
//
//    @RequestMapping(value = "/createehr", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String createehrWithREST(@RequestBody String body, HttpServletRequest request)
//            throws EHRUtilsException, AuditException {
//        HashMap map = json2HashMap(body);
//        return createehr((String[]) map.get("paths"), (String[]) map.get("values"), request);
//    }
//
//    private String createehr(String[] paths, String[] values, HttpServletRequest request)
//            throws AuditException, EHRUtilsException {
//        HashMap result = new HashMap();
//        result.put("result", EHRUtils.createEHR(auditParam(request), paths, values, USE_VALIDATION));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/isehrmodifiable", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String isehrmodifiable(@RequestParam("ehrid") String ehrid)
//            throws EHRUtilsException {
//        HashMap result = new HashMap();
//        result.put("ehrid", ehrid);
//        result.put("result", String.valueOf(EHRUtils.isEHRModifyable(ehrid)));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/setehrmodifiable", method = RequestMethod.POST, params = {"ehrid", "modflag"})
//    public
//    @ResponseBody
//    String setehrmodifiable(@RequestParam("ehrid") String ehrid, @RequestParam("modflag") String modflag,
//                            HttpServletRequest request)
//            throws AuditException, EHRUtilsException {
//        HashMap result = new HashMap();
//        EHRUtils.setEHRModifyable(auditParam(request), ehrid, Boolean.parseBoolean(modflag));
//        result.put("ehrid", ehrid);
//        result.put("modflag", modflag);
//        result.put("result", "OK");
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/setehrmodifiable", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String setehrmodifiableWithREST(@RequestBody String body, HttpServletRequest request)
//            throws EHRUtilsException, AuditException {
//        HashMap map = json2HashMap(body);
//        return setehrmodifiable((String) map.get("ehrid"), (String) map.get("modflag"), request);
//    }
//
//    @RequestMapping(value = "/isehrqueryable", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String isehrqueryable(@RequestParam("ehrid") String ehrid)
//            throws EHRUtilsException {
//        HashMap result = new HashMap();
//        result.put("ehrid", ehrid);
//        result.put("result", String.valueOf(EHRUtils.isEHRQueryable(ehrid)));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/setehrqueryable", method = RequestMethod.POST, params = {"ehrid", "queryflag"})
//    public
//    @ResponseBody
//    String setehrqueryable(@RequestParam("ehrid") String ehrid, @RequestParam("queryflag") String queryflag,
//                           HttpServletRequest request)
//            throws AuditException, EHRUtilsException {
//        HashMap result = new HashMap();
//        EHRUtils.setEHRQueryable(auditParam(request), ehrid, Boolean.parseBoolean(queryflag));
//        result.put("ehrid", ehrid);
//        result.put("queryflag", queryflag);
//        result.put("result", "OK");
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/setehrqueryable", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String setehrqueryableWithREST(@RequestBody String body, HttpServletRequest request)
//            throws EHRUtilsException, AuditException {
//        HashMap map = json2HashMap(body);
//        return setehrmodifiable((String) map.get("ehrid"), (String) map.get("queryflag"), request);
//    }

}
