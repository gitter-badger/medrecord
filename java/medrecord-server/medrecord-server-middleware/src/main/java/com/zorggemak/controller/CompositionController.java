package com.zorggemak.controller;

import com.zorggemak.commons.MiddlewareErrors;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static com.zorggemak.commons.ZorgGemakDefines.USE_VALIDATION;
import static com.zorggemak.util.AuditHelper.auditParam;
import static com.zorggemak.util.WebUtils.createJsonString;
import static com.zorggemak.util.WebUtils.json2HashMap;
import static com.zorggemak.util.WebUtils.json2StringArray;
import static com.zorggemak.util.WebUtils.parseEHRXml;

@SuppressWarnings({"unchecked", "SpellCheckingInspection"})
@Controller
@RequestMapping("/v1/composition")
public class CompositionController extends AbstractController {

//    @RequestMapping(value = "/retrievecompositionlist", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String retrievecompositionlist(@RequestParam("ehruid") String ehruid)
//            throws Exception {
//        HashMap result;
//        List<String> list;
//        String[] values;
//
//        list = EHRUtils.retrieveCompositionList(ehruid);
//        values = new String[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            values[i] = list.get(i);
//        }
//        result = new HashMap();
//        result.put("ehruid", ehruid);
//        result.put("result", result);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/retrievecomposition", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String retrievecomposition_json(@RequestParam("ehruid") String ehruid)
//            throws Exception {
//        HashMap result;
//        Map comp;
//        Iterator iter;
//        String[] paths;
//        String[] values;
//        String value;
//        int idx = 0;
//
//        value = retrieveComposition(ehruid, null, false);
//        comp = parseEHRXml(value);
//        paths = new String[comp.size()];
//        values = new String[comp.size()];
//        iter = comp.keySet().iterator();
//        while (iter.hasNext()) {
//            paths[idx] = (String) iter.next();
//            values[idx] = (String) comp.get(paths[idx]);
//            idx++;
//        }
//
//        result = new HashMap();
//        result.put("ehruid", ehruid);
//        result.put("paths", paths);
//        result.put("values", values);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/retrievecomposition/xml", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String retrievecomposition_xml(@RequestParam("ehruid") String ehruid)
//            throws Exception {
//        HashMap result;
//        String value;
//
//        value = retrieveComposition(ehruid, null, false);
//
//        result = new HashMap();
//        result.put("ehruid", ehruid);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/retrievecomposition/spn", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String retrievecomposition_spn(@RequestParam("ehruid") String ehruid)
//            throws Exception {
//        HashMap result;
//        String value;
//
//        value = retrieveComposition(ehruid, null, true);
//
//        result = new HashMap();
//        result.put("ehruid", ehruid);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/retrievecomposition/spn/xml", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String retrievecomposition_spn_xml(@RequestParam("ehruid") String ehruid)
//            throws Exception {
//        HashMap result;
//        String value;
//
//        value = retrieveComposition(ehruid, null, true);
//
//        result = new HashMap();
//        result.put("ehruid", ehruid);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//
//    @RequestMapping(value = "/retrievecompositionbyitem", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String retrievecompositionbyitem_json(@RequestParam("itemid") String itemid)
//            throws Exception {
//        HashMap result;
//        Map comp;
//        Iterator iter;
//        String[] paths;
//        String[] values;
//        String value;
//        int idx = 0;
//
//        value = retrieveComposition(null, itemid, false);
//        comp = parseEHRXml(value);
//        paths = new String[comp.size()];
//        values = new String[comp.size()];
//        iter = comp.keySet().iterator();
//        while (iter.hasNext()) {
//            paths[idx] = (String) iter.next();
//            values[idx] = (String) comp.get(paths[idx]);
//            idx++;
//        }
//
//        result = new HashMap();
//        result.put("itemid", itemid);
//        result.put("paths", paths);
//        result.put("values", values);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/retrievecompositionbyitem/xml", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String retrievecompositionbyitem_xml(@RequestParam("itemid") String itemid)
//            throws Exception {
//        HashMap result;
//        String value;
//
//        value = retrieveComposition(null, itemid, false);
//
//        result = new HashMap();
//        result.put("itemid", itemid);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    private String retrieveComposition(String ehruid, String itemid, boolean spn)
//            throws Exception {
//        if (itemid != null) {
//            return CompositionUtils.retrieveCompositionByContentItem(itemid);
//        }
//        if (spn) {
//            return CompositionUtils.retrieveCompositionSPN(ehruid);
//        }
//        return CompositionUtils.retrieveComposition(ehruid);
//    }
//
//    @RequestMapping(value = "/createcomposition", method = RequestMethod.POST, params = {"ehruid", "paths", "values"})
//    public
//    @ResponseBody
//    String createcomposition(@RequestParam("ehruid") String ehruid, @RequestParam("paths") String paths,
//                             @RequestParam("values") String values, HttpServletRequest request)
//            throws Exception {
//        String[] ppars;
//        String[] vpars;
//
//        ppars = json2StringArray(paths);
//        vpars = json2StringArray(values);
//        return createComposition(ehruid, ppars, vpars, request);
//    }
//
//
//    @RequestMapping(value = "/createcomposition", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String createcompositionWithREST(@RequestBody String body, HttpServletRequest request)
//            throws Exception {
//        HashMap map;
//
//        map = json2HashMap(body);
//        return (createComposition((String) map.get("ehruid"), (String[]) map.get("paths"), (String[]) map.get("values"),
//                request));
//    }
//
//    private String createComposition(String ehruid, String[] paths, String[] values, HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//        String value;
//
//        value = CompositionUtils.createComposition(auditParam(request), ehruid, paths, values, USE_VALIDATION);
//        result = new HashMap();
//        result.put("ehruid", ehruid);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/modifycomposition", method = RequestMethod.POST, params = {"paths", "values"})
//    public
//    @ResponseBody
//    String modifycomposition(@RequestParam("paths") String paths, @RequestParam("values") String values,
//                             HttpServletRequest request)
//            throws Exception {
//        String[] ppars;
//        String[] vpars;
//
//        ppars = json2StringArray(paths);
//        vpars = json2StringArray(values);
//        return modifyComposition(ppars, vpars, request);
//    }
//
//    @RequestMapping(value = "/modifycomposition", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String modifycompositionWithREST(@RequestBody String body, HttpServletRequest request)
//            throws Exception {
//        HashMap map;
//
//        map = json2HashMap(body);
//        return modifyComposition((String[]) map.get("paths"), (String[]) map.get("values"), request);
//    }
//
//    private String modifyComposition(String[] paths, String[] values, HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//
//        CompositionUtils.modifyComposition(auditParam(request), paths, values, USE_VALIDATION);
//        result = new HashMap();
//        result.put("result", "OK");
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/deletecomposition", method = RequestMethod.POST, params = {"compid"})
//    public
//    @ResponseBody
//    String deletecomposition(@RequestParam("compid") String compid, HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//
//        CompositionUtils.deleteComposition(auditParam(request), compid);
//        result = new HashMap();
//        result.put("compid", compid);
//        result.put("result", "OK");
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/deletecomposition", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String deletecompositionWithREST(@RequestBody String body, HttpServletRequest request)
//            throws Exception {
//        HashMap map;
//
//        map = json2HashMap(body);
//        return deletecomposition((String) map.get("compid"), request);
//    }
//
//    @RequestMapping(value = "/undeletecomposition", method = RequestMethod.POST, params = {"compid"})
//    public
//    @ResponseBody
//    String undeletecomposition(@RequestParam("compid") String compid, HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//
//        CompositionUtils.undeleteComposition(auditParam(request), compid);
//        result = new HashMap();
//        result.put("compid", compid);
//        result.put("result", "OK");
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/undeletecomposition", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String undeletecompositionWithREST(@RequestBody String body, HttpServletRequest request)
//            throws Exception {
//        HashMap map;
//
//        map = json2HashMap(body);
//        return deletecomposition((String) map.get("compid"), request);
//    }
//
//    @RequestMapping(value = "/listitemsforcomposition", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String listitemsforcomposition(@RequestParam("compid") String compid)
//            throws Exception {
//        List<String> list;
//        String[] values;
//        HashMap result;
//
//        list = CompositionUtils.listContentItemsForComposition(compid);
//        values = new String[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            values[i] = list.get(i);
//        }
//
//        result = new HashMap();
//        result.put("compid", compid);
//        result.put("result", values);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/listsectionsforcomposition", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String listsectionsforcomposition(@RequestParam("compid") String compid)
//            throws Exception {
//        List<String> list;
//        String[] values;
//        HashMap result;
//
//        list = CompositionUtils.listSectionsForComposition(compid);
//        values = new String[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            values[i] = list.get(i);
//        }
//
//        result = new HashMap();
//        result.put("compid", compid);
//        result.put("result", result);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/listitemsforsection", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String listitemsforsection(@RequestParam("sectid") String sectid)
//            throws Exception {
//        List<String> list;
//        String[] values;
//        HashMap result;
//
//        list = CompositionUtils.listContentItemsForSection(sectid);
//        values = new String[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            values[i] = list.get(i);
//        }
//
//        result = new HashMap();
//        result.put("sectid", sectid);
//        result.put("result", result);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/listsectionsforsection", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String listsectionsforsection(@RequestParam("sectid") String sectid)
//            throws Exception {
//        List<String> list;
//        String[] values;
//        HashMap result;
//
//        list = CompositionUtils.listSectionsForSection(sectid);
//        values = new String[list.size()];
//        for (int i = 0; i < list.size(); i++) {
//            values[i] = list.get(i);
//        }
//
//        result = new HashMap();
//        result.put("sectid", sectid);
//        result.put("result", result);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/modifycontentitem", method = RequestMethod.POST, params = {"paths", "values"})
//    public
//    @ResponseBody
//    String modifycontentitem(@RequestParam("paths") String paths, @RequestParam("values") String values,
//                             HttpServletRequest request)
//            throws Exception {
//        String[] ppars;
//        String[] vpars;
//
//        ppars = json2StringArray(paths);
//        vpars = json2StringArray(values);
//        return modifyContentItem(ppars, vpars, request);
//    }
//
//    @RequestMapping(value = "/modifycontentitem", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String modifycontentitemWithREST(@RequestBody String body, HttpServletRequest request)
//            throws Exception {
//        HashMap map;
//
//        map = json2HashMap(body);
//        return modifyContentItem((String[]) map.get("paths"), (String[]) map.get("values"), request);
//    }
//
//    private String modifyContentItem(String[] paths, String[] values, HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//
//        CompositionUtils.modifyContentItem(auditParam(request), paths, values, USE_VALIDATION);
//        result = new HashMap();
//        result.put("result", "OK");
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/movecontentitem", method = RequestMethod.POST, params = {"contid", "toid"})
//    public
//    @ResponseBody
//    String movecontentitem(@RequestParam("contid") String contid, @RequestParam("toid") String toid,
//                           HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//
//        CompositionUtils.moveContentItem(auditParam(request), contid, toid, USE_VALIDATION);
//        result = new HashMap();
//        result.put("contid", contid);
//        result.put("toid", toid);
//        result.put("result", "OK");
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/movecontentitem", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String movecontentitemWithREST(@RequestBody String body, HttpServletRequest request)
//            throws Exception {
//        HashMap map;
//
//        map = json2HashMap(body);
//        return movecontentitem((String) map.get("contid"), (String) map.get("toid"), request);
//    }
//
//    @RequestMapping(value = "/removeitemfromehr", method = RequestMethod.POST, params = {"ehrid", "itemid"})
//    public
//    @ResponseBody
//    String removeitemfromehr(@RequestParam("ehrid") String ehrid, @RequestParam("itemid") String itemid,
//                             HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//
//        CompositionUtils.removeItemFromEHRItem(auditParam(request), ehrid, itemid, USE_VALIDATION);
//        result = new HashMap();
//        result.put("ehrid", ehrid);
//        result.put("itemid", itemid);
//        result.put("result", "OK");
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/removeitemfromehr", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String removeitemfromehrWithREST(@RequestBody String body, HttpServletRequest request)
//            throws Exception {
//        HashMap map;
//
//        map = json2HashMap(body);
//        return removeitemfromehr((String) map.get("ehrid"), (String) map.get("itemid"), request);
//    }
//
//    @RequestMapping(value = "/addcontentitemtocomposition", method = RequestMethod.POST,
//            params = {"compid", "archid", "paths", "values"})
//    public
//    @ResponseBody
//    String addcontentitemtocomposition(@RequestParam("compid") String compid, @RequestParam("archid") String archid,
//                                       @RequestParam("paths") String paths, @RequestParam("values") String values,
//                                       HttpServletRequest request)
//            throws Exception {
//        String[] ppars;
//        String[] vpars;
//
//        ppars = json2StringArray(paths);
//        vpars = json2StringArray(values);
//        return addContentItemToComposition(compid, archid, ppars, vpars, request);
//    }
//
//    @RequestMapping(value = "/addcontentitemtocomposition", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String addcontentitemtocompositionWithREST(@RequestBody String body, HttpServletRequest request)
//            throws Exception {
//        HashMap map;
//
//        map = json2HashMap(body);
//        return addContentItemToComposition((String) map.get("compid"), (String) map.get("archid"), (String[]) map.get(
//                "paths"), (String[]) map.get("values"), request);
//    }
//
//    private String addContentItemToComposition(String compid, String archid, String[] paths, String[] values,
//                                               HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//        String value;
//
//        value = CompositionUtils.addContentItemToComposition(auditParam(request), compid, archid, paths, values,
//                USE_VALIDATION);
//        result = new HashMap();
//        result.put("compid", compid);
//        result.put("archid", archid);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/addcontentitemtosection", method = RequestMethod.POST,
//            params = {"sectid", "archid", "paths", "values"})
//    public
//    @ResponseBody
//    String addcontentitemtosection(@RequestParam("sectid") String sectid, @RequestParam("archid") String archid,
//                                   @RequestParam("paths") String paths, @RequestParam("values") String values,
//                                   HttpServletRequest request)
//            throws Exception {
//        String[] ppars;
//        String[] vpars;
//
//        ppars = json2StringArray(paths);
//        vpars = json2StringArray(values);
//        return addcontentitemtosection(sectid, archid, ppars, vpars, request);
//    }
//
//    @RequestMapping(value = "/addcontentitemtosection", method = RequestMethod.POST)
//    public
//    @ResponseBody
//    String addcontentitemtosectionWithREST(@RequestBody String body, HttpServletRequest request)
//            throws Exception {
//        HashMap map;
//
//        map = json2HashMap(body);
//        return addcontentitemtosection((String) map.get("sectid"), (String) map.get("archid"), (String[]) map.get(
//                "paths"), (String[]) map.get("values"), request);
//    }
//
//    private String addcontentitemtosection(String sectid, String archid, String[] paths, String[] values,
//                                           HttpServletRequest request)
//            throws Exception {
//        HashMap result;
//        String value;
//
//        value = CompositionUtils.addContentItemToSection(auditParam(request), archid, sectid, paths, values,
//                USE_VALIDATION);
//        result = new HashMap();
//        result.put("sectid", sectid);
//        result.put("archid", archid);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/retrievearchetypeidforcontentitemorcomposition", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String retrievearchetypeidforcontentitemorcomposition(@RequestParam("cuid") String cuid)
//            throws Exception {
//        HashMap result;
//        String value;
//
//        value = CompositionUtils.retrieveArchetypeIDForContentItemOrComposition(cuid);
//        result = new HashMap();
//        result.put("cuid", cuid);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
//
//    @RequestMapping(value = "/retrieveehridforcomposition", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String retrieveehridforcomposition(@RequestParam("cuid") String cuid)
//            throws Exception {
//        HashMap result;
//        String value;
//
//        value = CompositionUtils.getEHRUIDForComposition(cuid);
//        result = new HashMap();
//        result.put("cuid", cuid);
//        result.put("result", value);
//        result.put("errorcode", MiddlewareErrors.OK);
//        return createJsonString(result);
//    }
}
