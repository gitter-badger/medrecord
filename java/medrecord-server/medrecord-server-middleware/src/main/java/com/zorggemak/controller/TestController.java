package com.zorggemak.controller;

import com.zorggemak.commons.MiddlewareErrors;
import com.zorggemak.commons.ZorgGemakDefines;
import com.zorggemak.data.ArchetypeObject;
import com.zorggemak.util.ObjectHelper;
import com.zorggemak.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;

@SuppressWarnings({"unchecked", "SpellCheckingInspection"})
@Controller
@RequestMapping("/v1/test")
public class TestController extends AbstractController {
    private static final Logger log = LoggerFactory.getLogger(TestController.class);

    @RequestMapping(value = "/getarchid", method = RequestMethod.GET)
    public
    @ResponseBody
    String getarchid() {
        return "zorggemak-demographic-PERSON.person.v1";
    }

    @RequestMapping(value = "/geterrortable", method = RequestMethod.GET)
    public
    @ResponseBody
    String geterrortable() {
        try {
            return MiddlewareErrors.getHtmlTable();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            return e.getMessage();
        }
    }

//    @RequestMapping(value = "/getobjectdata", method = RequestMethod.GET)
//    public
//    @ResponseBody
//    String getobjectdata(@RequestParam("objid") String objid, HttpServletRequest request) {
//        ArchetypeObject archetype;
//        Iterator iter;
//        String path;
//        String value;
//        StringBuffer buf = new StringBuffer();
//
//        try {
//            if ((archetype = ObjectHelper.getArchetypeObject(objid, true)) != null) {
//                buf.append("<table border=\"1\">");
//                iter = archetype.getPathIterator();
//                while (iter.hasNext()) {
//                    path = (String) iter.next();
//                    value = archetype.getPathValue(path).trim();
//                    buf.append("<tr><td>");
//                    buf.append(archetype.getArchetypeId());
//                    buf.append(" ");
//                    buf.append(path);
//                    buf.append("</td><td>");
//                    if (value != null) {
//                        if (value.startsWith("http:") || value.startsWith("https:")) {
//                            buf.append("<a href=\"");
//                            buf.append(value);
//                            buf.append("\" target=\"_blank\">");
//                            buf.append(value);
//                            buf.append("</a>");
//                        } else {
//                            buf.append(value);
//                        }
//                    } else {
//                        buf.append("");
//                    }
//                    buf.append("</td><td>");
//                    buf.append(path);
//                    buf.append("</td></tr>");
//                }
//                buf.append("</table>");
//                buf.append("\n");
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//            buf.append(e.getMessage());
//            WebUtils.middlewareException(request, MiddlewareErrors.SERVER_EXCEPTION, e.getMessage(), e);
//        }
//        return buf.toString();
//    }

    @RequestMapping(value = "/getversion", method = RequestMethod.GET)
    public
    @ResponseBody
    String getversion() {
        String ver = "";

        try {
            ver = "v";
            ver += ZorgGemakDefines.APPLICATION_VERSION;
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return ver;
    }
}
