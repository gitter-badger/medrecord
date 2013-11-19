package com.zorggemak.controller;

import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.spi.exceptions.InitializationException;
import com.zorggemak.commons.MiddlewareErrors;
import com.zorggemak.util.WebUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public abstract class AbstractController {
    private static final Logger log = LoggerFactory.getLogger(AbstractController.class);
    
    public MedRecordEngine engine() throws InitializationException
    {
        MedRecordEngine instance = MedRecordEngine.getInstance();
        instance.initialize();
        return instance;
    }

    @ExceptionHandler(Exception.class)
    public void handleException(Exception e, HttpServletRequest request, HttpServletResponse response) {
        log.error(e.getMessage(), e);
        HashMap result = WebUtils.middlewareException(request, MiddlewareErrors.SERVER_EXCEPTION, e.getMessage(), e);
        String body = WebUtils.createJsonString(result);
        response.setContentType("application/json");
        try {
            PrintWriter w = response.getWriter();
            w.print(body);
            w.flush();
        } catch (IOException ioe) {
            // ignore
        }
    }
}
