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
package com.zorggemak.filters;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CORSFilter implements javax.servlet.Filter {

    public static final String ALLOW_METHODS = "POST, GET, OPTIONS, HEAD, PUT, DELETE";
    public static final String ALLOW_HEADERS =
            "Origin, Content-Type, Accept, Accept-Language, X-Requested-With, X-Originating-System-Id, X-On-Behalf-Of";
    public static final String SIMPLE_HEADERS =
            "Cache-Control, Content-Language, Content-Type, Expires, Last-Modified, Pragma";

    public static final String[] ALLOW_METHODS_LIST;
    public static final String[] ALLOW_HEADERS_LIST;
    public static final String[] SIMPLE_HEADERS_LIST;
    static {
        String[] allowMethods = ALLOW_METHODS.split(",");
        for (int i = 0; i < allowMethods.length; i++) {
            allowMethods[i] = allowMethods[i].trim();
        }
        ALLOW_METHODS_LIST = allowMethods;

        String[] allowHeaders = ALLOW_HEADERS.split(",");
        for (int i = 0; i < allowHeaders.length; i++) {
            allowHeaders[i] = allowHeaders[i].trim();
        }
        ALLOW_HEADERS_LIST = allowHeaders;
        
        String[] simpleHeaders = SIMPLE_HEADERS.split(",");
        for (int i = 0; i < simpleHeaders.length; i++) {
            simpleHeaders[i] = simpleHeaders[i].trim();
        }
        SIMPLE_HEADERS_LIST = simpleHeaders;
    }

    @Override
    public void destroy() {
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        addCORSHeaders(req, res);
        chain.doFilter(req, res);
    }
    
    public void addCORSHeaders(ServletRequest req, ServletResponse res) {
        // this is an actual implementation of the rules in
        //   http://www.w3.org/TR/cors/
        // rather than take any shortcuts, its seems nice to claim full standards-compliance!
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
            
        String origin = request.getHeader("Origin");
        if (origin != null) { // 6.1.1 | 6.2.1
            // 6.2.2: always allow
            String requestMethod = request.getMethod();
            if ("OPTIONS".equalsIgnoreCase(requestMethod)) {  // 7.1.5.1 -- this is preflight, --> 6.2
                String method = request.getHeader("Access-Control-Request-Method"); // 6.2.3
                if (method == null || method.isEmpty() || "TRACE".equals(method)) { // 6.2.3 
                    return;
                }
                String headerFieldNames = request.getHeader("Access-Control-Request-Headers"); // 6.2.4
                // 6.2.5 -- allow all methods
                // 6.2.6 -- allow all headers
                
                // some browsers insist on getting the 'right' origin when using ssl, and if they are using a
                // https:// url that is doing CORS to a http url, that counts as using ssl, so the use of * is
                // not really possible in an environment that includes the use of SSL, at all. Grr.
                //if (request.isSecure()) { 
                response.setHeader("Access-Control-Allow-Origin", origin); // 6.2.7 -- allow specified origin
                // so now that we're down that path, consider that there might be a cookie for another domain
                // somewhere that we probably should allow...
                response.setHeader("Access-Control-Allow-Credentials", "true"); // 6.2.7 -- in case of cookies...
                //} else {
                //    response.setHeader("Access-Control-Allow-Origin", "*"); // 6.2.7 -- allow all origins
                //}
                response.setHeader("Access-Control-Max-Age", "30"); // 6.2.8 -- need to cache-bust the preflight cache
                                                                    // for users that go across multiple origins and
                                                                    // that are using SSL
                
                if (isAllowedMethod(method)) { // 6.2.9
                    response.setHeader("Access-Control-Allow-Methods", ALLOW_METHODS);
                } else {
                    response.setHeader("Access-Control-Allow-Methods", ALLOW_METHODS + ", " + method);
                }
                
                if (headerFieldNames == null || headerFieldNames.isEmpty()) { // 6.2.10 -- allow all headers
                    response.setHeader("Access-Control-Allow-Headers",
                            ALLOW_HEADERS);
                } else {
                    response.setHeader("Access-Control-Allow-Headers", allowAllHeaders(headerFieldNames));
                }
            } else { // not preflight --> 6.1
                // 6.1.2: always allow
                response.setHeader("Access-Control-Allow-Origin", origin); // 6.1.3 -- like above, this is needed....
                response.setHeader("Access-Control-Allow-Credentials", "true"); // 6.1.3 -- in case of cookies...
                response.setHeader("Access-Control-Expose-Headers", // 6.1.4 -- allow some of the headers we like using
                        "Content-Length, Date, Location");
            }
        }
    }
    
    private static boolean isAllowedMethod(String method) {
        for (int i = 0; i < ALLOW_METHODS_LIST.length; i++) {
            String m = ALLOW_METHODS_LIST[i];
            if (m.equals(method)) {
                return true;
            }
        }
        return false;
    }
    
    private static String allowAllHeaders(String headerFieldNames) {
        // This is how painful java is when you write it out!
        // For the record I did this so this class remains as portable as possible.
        String[] headers = headerFieldNames.split(",");
        for (int i = 0; i < headers.length; i++) {
            headers[i] = headers[i].trim();
        }
        List<String> result = new ArrayList<>();
        for (int i = 0; i < ALLOW_HEADERS_LIST.length; i++) {
            String h = ALLOW_HEADERS_LIST[i];
            result.add(h);
        }
        for (int i = 0; i < headers.length; i++) {
            String h = headers[i];
            boolean add = true;
            for (int j = 0; j < result.size(); j++) {
                String h2 = result.get(j);
                if (h.equalsIgnoreCase(h2)) {
                    add = false;
                    break;
                }
            }
            if (add) {
                result.add(h);
            }
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < result.size(); i++) {
            String h = result.get(i);
            buf.append(h);
            if (i < result.size() - 1) {
                buf.append(", ");
            }
        }
        String resultStr = buf.toString();
        return resultStr;
    }

    @Override
    public void init(FilterConfig filterConfig)
            throws ServletException {
    }
}
