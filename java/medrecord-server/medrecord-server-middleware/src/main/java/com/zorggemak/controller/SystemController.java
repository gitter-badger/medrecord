package com.zorggemak.controller;

import com.zorggemak.commons.MiddlewareErrors;
import com.zorggemak.data.DataManager;
import com.zorggemak.data.RequestError;
import com.zorggemak.util.WebUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;

import static com.zorggemak.util.WebUtils.toStackTrace;

@SuppressWarnings({"unchecked", "SpellCheckingInspection"})
@Controller
@RequestMapping("/v1/system")
public class SystemController extends AbstractController {
    private final static Log log = LogFactory.getLog(SystemController.class);
    private static final long MAX_TIME_PASSED = 15 * 60 * 1000;

    @RequestMapping(value = "/getlasterror/{reqid}", method = RequestMethod.GET)
    public
    @ResponseBody
    String getlasterror(@PathVariable("reqid") String reqid) {
        DataManager dataManager = DataManager.getInstance();
        RequestError err;
        HashMap result;

        result = new HashMap();
        try {
            result.put("reqid", reqid);
            result.put("errorcode", MiddlewareErrors.OK.getErrorCode());
            result.put("errorstr", MiddlewareErrors.OK.getErrorString());
            result.put("errormsg", "No error found");
            result.put("timepassed", 0);

            err = (RequestError) dataManager.getObject(reqid);
            if (err != null) {
                if (err.getTimePassed() < MAX_TIME_PASSED) {
                    result.put("reqid", reqid);
                    result.put("errorcode", err.getErrorCode());
                    result.put("errorstr", err.getErrorString());
                    result.put("errormsg", err.getErrorMessage());
                    result.put("timepassed", err.getTimePassed());
                    result.put("errordetail", toStackTrace(err.getException()));
                    result.put("note", "YOU DON'T HAVE TO USE getlasterror ANYMORE");
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            result.put("reqid", reqid);
            result.put("errorcode", MiddlewareErrors.SERVER_EXCEPTION.getErrorCode());
            result.put("errorstr", MiddlewareErrors.SERVER_EXCEPTION.getErrorString());
            result.put("errormsg", e.toString());
            result.put("timepassed", 0);
            result.put("errordetail", toStackTrace(e));
            result.put("note", "YOU DON'T HAVE TO USE getlasterror ANYMORE");
        }
        return WebUtils.createJsonString(result);
    }

}
