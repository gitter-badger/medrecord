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

import com.zorggemak.commons.MiddlewareErrors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

import static com.zorggemak.util.WebUtils.createJsonString;

@SuppressWarnings({"unchecked", "SpellCheckingInspection"})
@Controller
@RequestMapping("/v1/query")
public class QueryController extends AbstractController {

//    @RequestMapping(value = "/analyzedemographicxpathquery", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String analyzedemographicxpathquery(@RequestParam("query") String query)
//            throws Exception {
//        HashMap result = new HashMap();
//        result.put("query", query);
//        result.put("result", LocatableUtils.analyzeDemographicXQuery(query));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/analyzedemographicxqueryflwornotation", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String analyzedemographicxqueryflwornotation(@RequestParam("query") String query)
//            throws Exception {
//        HashMap result = new HashMap();
//        result.put("query", query);
//        result.put("result", LocatableUtils.analyzeDemographicFLWORXQuery(query));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/analyzeehrxpathquery", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String analyzeehrxpathquery(@RequestParam("query") String query)
//            throws Exception {
//        HashMap result = new HashMap();
//        result.put("query", query);
//        result.put("result", LocatableUtils.analyzeEHRXQuery(query));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/analyzeehrxqueryflwornotation", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String analyzeehrxqueryflwornotation(@RequestParam("query") String query)
//            throws Exception {
//        HashMap result = new HashMap();
//        result.put("query", query);
//        result.put("result", LocatableUtils.analyzeEHRFLWORXQuery(query));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/ehrxpathquery", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String ehrxpathquery(@RequestParam("query") String query)
//            throws Exception {
//        HashMap result = new HashMap();
//        result.put("query", query);
//        result.put("result", LocatableUtils.performEHRXQuery(query));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/ehrxqueryflwornotation", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String ehrxqueryflwornotation(@RequestParam("query") String query)
//            throws Exception {
//        HashMap result = new HashMap();
//        result.put("query", query);
//        result.put("result", LocatableUtils.performEHRFLWORXQuery(query));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/demographicxpathquery", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String demographicxpathquery(@RequestParam("query") String query)
//            throws Exception {
//        HashMap result = new HashMap();
//        result.put("query", query);
//        result.put("result", LocatableUtils.performDemographicXQuery(query));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/demographicxqueryflwornotation", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String demographicxqueryflwornotation(@RequestParam("query") String query)
//            throws Exception {
//        HashMap result = new HashMap();
//        result.put("query", query);
//        result.put("result", LocatableUtils.performDemographicFLWORXQuery(query));
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }

}
