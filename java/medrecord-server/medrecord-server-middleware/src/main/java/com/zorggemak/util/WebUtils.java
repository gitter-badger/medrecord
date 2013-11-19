package com.zorggemak.util;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.zorggemak.commons.AuditException;
import com.zorggemak.commons.MiddlewareErrors;
import com.zorggemak.commons.NoSystemIdAuditException;
import com.zorggemak.commons.NoUserIdAuditException;
import com.zorggemak.data.DataManager;
import com.zorggemak.data.RequestError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@SuppressWarnings({"SpellCheckingInspection", "unchecked"})
public class WebUtils {
    private static final Logger log = LoggerFactory.getLogger(WebUtils.class);

    public static String createJsonString(HashMap map) {
        StringBuilder buf;
        Iterator iter;
        String key;
        Object val;
        boolean first = true;
        Gson gson;

        gson = new Gson();
        buf = new StringBuilder();
        buf.append("{");
        iter = map.keySet().iterator();
        while (iter.hasNext()) {
            key = (String) iter.next();
            val = map.get(key);
            if (!first) {
                buf.append(",\n  ");
            } else {
                buf.append("\n  ");
            }
            buf.append("\"");
            buf.append(key);
            buf.append("\": ");
            if (val instanceof String[]) {
                buf.append(gson.toJson(val));
            } else if (val == null) {
                buf.append("null");
            } else {
                buf.append(gson.toJson(val.toString()));
            }
            first = false;
        }
        buf.append("\n}");
        if (log.isDebugEnabled()) {
            String debugMessage;
            if (buf.length() > 500) {
                debugMessage = "json=" + buf.substring(0, 492) + "...";
            } else {
                debugMessage = "json=" + buf.toString();
            }
            log.debug(debugMessage);
        }
        return buf.toString();
    }

    public static String[] json2StringArray(String value) {
        String[] str;
        Gson gson;

        gson = new Gson();
        str = gson.fromJson(value, String[].class);
        return str;
    }

    public static HashMap json2HashMap(String value) {
        PathValues values;
        HashMap map;
        Gson gson;
        Type maptype;

        gson = new Gson();
        if (value.toLowerCase().indexOf("paths") != -1 && value.toLowerCase().indexOf("values") != -1) {
            values = gson.fromJson(value, PathValues.class);
            map = new HashMap();
            map.put("ehruid", values.ehruid);
            map.put("nodeid", values.nodeid);
            map.put("contactid", values.contactid);
            map.put("partyid", values.partyid);
            map.put("actorid", values.actorid);
            map.put("paths", values.paths);
            map.put("values", values.values);
        } else {
            maptype = new TypeToken<HashMap<String, String>>() {
            }.getType();
            map = gson.fromJson(value, maptype);
        }
        return map;
    }

    public static HashMap middlewareException(HttpServletRequest request, MiddlewareErrors error, String errmsg) {
        return middlewareException(request, error, errmsg, null);
    }

    public static HashMap middlewareException(HttpServletRequest request, MiddlewareErrors error, String errmsg,
                                              Exception exception) {
        DataManager datman = DataManager.getInstance();
        HashMap retval = new HashMap();
        String reqid = "";

        if (errmsg == null) {
            errmsg = "";
        }
        //log.warn("middlewareException " + error.getErrorString() + " " + errmsg, exception);
        try {
            reqid = datman.insertObject(new RequestError(error, errmsg, exception));
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex); // swallow because in the middle of error-handling
        }
        retval.put("reqid", reqid);
        retval.put("result", createMiddlewareUrl(request) + "getlasterror/" + reqid);
        retval.put("errorcode", error.getErrorCode());
        // add in all error details in the response immediately, so you don't actually need to use getlasterror/
        retval.put("errorstr", error.getErrorString());
        retval.put("errormsg", errmsg);
        retval.put("errordetail", toStackTrace(exception));
        return retval;
    }

    public static HashMap middlewareException(HttpServletRequest request, AuditException error, String errmsg) {
        if (error instanceof NoUserIdAuditException) {
            return middlewareException(request, MiddlewareErrors.USER_ID_REQUIRED, errmsg, error);
        } else if (error instanceof NoSystemIdAuditException) {
            return middlewareException(request, MiddlewareErrors.SYSTEM_ID_REQUIRED, errmsg, error);
        } else {
            return middlewareException(request, MiddlewareErrors.SERVER_EXCEPTION, errmsg, error);
        }
    }

    public static String createMiddlewareUrl(HttpServletRequest request) {
        StringBuilder buf = new StringBuilder();
        buf.append(request.getScheme());
        buf.append("://");
        buf.append(request.getServerName());
        buf.append(":");
        buf.append(request.getServerPort());
        buf.append(request.getContextPath());
        buf.append("/");
        return (buf.toString());
    }

    public static Map parseEHRXml(String xml)
            throws SAXException, IOException {
        EHRSAXHandler eh;
        StringReader sr;
        XMLReader xr;

        if (xml != null) {
            xr = XMLReaderFactory.createXMLReader();
            eh = new EHRSAXHandler();
            xr.setContentHandler(eh);
            xr.setErrorHandler(eh);
            sr = new StringReader(xml);
            xr.parse(new InputSource(sr));
            return eh.getValues();
        }
        return null;
    }

    public static String notSupported(HttpServletRequest request) {
        return createJsonString(middlewareException(request, MiddlewareErrors.SERVER_EXCEPTION,
                "This API is no longer supported", null));
    }

    public static String toStackTrace(Exception e) {
        if (e == null) {
            return "";
        }
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        e.printStackTrace(pw);
        String stackTrace = sw.toString();
        return stackTrace;
    }

    @SuppressWarnings("UnusedDeclaration")
    private class PathValues {
        PathValues() {
        }

        private String ehruid;
        private String nodeid;
        private String contactid;
        private String partyid;
        private String actorid;
        private String[] paths;
        private String[] values;
    }

    private static class EHRSAXHandler extends DefaultHandler {
        private HashMap valuemap = new HashMap();
        private String cur_tag = null;
        private String cur_item = null;

        public EHRSAXHandler() {
            super();
        }

        public HashMap getValues() {
            return valuemap;
        }

        @Override
        public void error(SAXParseException saxpe)
                throws SAXException {
            log.debug("EHRSAXHandler: error " + saxpe);
            super.error(saxpe);
        }

        @Override
        public void warning(SAXParseException saxpe)
                throws SAXException {
            log.debug("EHRSAXHandler: warning " + saxpe);
            super.warning(saxpe);
        }

        @Override
        public void fatalError(SAXParseException saxpe)
                throws SAXException {
            log.debug("EHRSAXHandler: fatalError " + saxpe);
            super.fatalError(saxpe);
        }

        @Override
        public void startElement(String uri, String name, String qname, Attributes atts) {
            if ("".equals(uri)) {
                cur_tag = qname;
                if ("value".equals(cur_tag)) {
                    valuemap.put(cur_item, "");
                } else {
                    cur_item = "";
                }
            } else {
                cur_tag = uri + "/" + name;
                if ("value".equals(cur_tag)) {
                    valuemap.put(cur_item, "");
                } else {
                    cur_item = "";
                }
            }
        }

        @Override
        public void endElement(String uri, String name, String qname) {
            cur_tag = null;
        }

        @Override
        public void characters(char ch[], int start, int length) {
            StringBuilder value = new StringBuilder();
            String val;

            for (int i = start; i < start + length; i++) {
                switch (ch[i]) {
                    case '\\':
                    case '"':
                    case '\n':
                    case '\r':
                    case '\t':
                        // Skip control chars
                        break;
                    default:
                        value.append(ch[i]);
                        break;
                }
            }
            if ("item".equals(cur_tag)) {
                cur_item += value.toString();
            }
            if ("path".equals(cur_tag)) {
                cur_item += value.toString();
            }
            if ("value".equals(cur_tag)) {
                val = (String) valuemap.get(cur_item);
                valuemap.put(cur_item, val + value.toString());
            }
        }

    }

}
