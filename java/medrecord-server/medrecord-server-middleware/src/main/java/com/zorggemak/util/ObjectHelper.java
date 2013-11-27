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
package com.zorggemak.util;

import com.zorggemak.commons.MiddlewareErrors;
import com.zorggemak.data.ArchetypeObject;
import com.zorggemak.data.DataManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.zorggemak.util.WebUtils.createJsonString;
import static com.zorggemak.util.WebUtils.middlewareException;


public class ObjectHelper {
//    private static final Logger log = LoggerFactory.getLogger(ObjectHelper.class);
//
//    public static String getPaths(String objid, HttpServletRequest request) {
//        ArchetypeObject obj;
//        Iterator iter;
//        HashMap result;
//        String[] paths;
//        int i = 0;
//
//        if ((obj = getArchetypeObject(objid)) != null) {
//            paths = new String[obj.getPathSize()];
//            iter = obj.getPathIterator();
//            while (iter.hasNext()) {
//                paths[i++] = (String) iter.next();
//            }
//            result = new HashMap();
//            //noinspection unchecked
//            result.put("objid", objid);
//            //noinspection unchecked
//            result.put("result", paths);
//            //noinspection unchecked
//            result.put("errorcode", MiddlewareErrors.OK);
//        } else {
//            result = middlewareException(request, MiddlewareErrors.OBJECT_NOT_FOUND,
//                    "Object with id " + objid + " not found");
//        }
//        return createJsonString(result);
//    }
//
//    public static ArchetypeObject getArchetypeObject(String guid, boolean retrieve) {
//        ArchetypeObject object;
//
//        try {
//            if (retrieve) {
//                return retrieveArchetypeObject(guid, false, false);
//            } else {
//                if ((object = getArchetypeObject(guid)) != null) {
//                    return object;
//                }
//                return retrieveArchetypeObject(guid, false, false);
//            }
//        } catch (Exception e) {
//            log.error(e.getMessage(), e);
//        }
//        return null;
//    }
//
//    public static ArchetypeObject retrieveArchetypeObject(String guid, boolean spath, boolean ignoredel)
//            throws Exception {
//        DataManager dataManager = DataManager.getInstance();
//        ArchetypeObject archetype;
//        String xml;
//        
//        /*
//            <RMObject>
//                <primitive>
//                    <path></path>
//                    <value></value>
//                </primitive>
//            </RMObject>
//        */
//        if (spath) {
//            xml = LocatableUtils.retrieveLocatable(guid.trim(), ignoredel);
//        } else {
//            xml = LocatableUtils.retrieveLocatableSPN(guid.trim(), ignoredel);
//        }
//        if (xml != null) {
//            Map map = WebUtils.parseEHRXml(xml);
//            archetype = dataManager.createArchetypeObject(guid);
//            for (Object o : map.entrySet()) {
//                Map.Entry e = (Map.Entry) o;
//                archetype.addPathValue((String) e.getKey(), (String) e.getValue());
//            }
//            dataManager.updateObject(archetype);
//            return archetype;
//        }
//        return null;
//    }
//
//    public static ArchetypeObject getArchetypeObject(String objid) {
//        ArchetypeObject archetype;
//        DataManager dataManager;
//
//        dataManager = DataManager.getInstance();
//        if ((archetype = (ArchetypeObject) dataManager.getObject(objid)) != null) {
//            return archetype;
//        } else {
//            if ((archetype = dataManager.findArchetypeObjectByGUID(objid)) != null) {
//                return archetype;
//            }
//        }
//        return null;
//    }
}
