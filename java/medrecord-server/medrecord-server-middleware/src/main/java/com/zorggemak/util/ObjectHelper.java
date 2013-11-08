package com.zorggemak.util;

import com.medvision360.kernel.locatableutils.LocatableUtils;
import com.zorggemak.commons.MiddlewareErrors;
import com.zorggemak.data.ArchetypeObject;
import com.zorggemak.data.DataManager;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.zorggemak.util.WebUtils.createJsonString;
import static com.zorggemak.util.WebUtils.middlewareException;


public class ObjectHelper {
    private final static Log log = LogFactory.getLog(ObjectHelper.class);

    public static String getPaths(String objid, HttpServletRequest request) {
        ArchetypeObject obj;
        Iterator iter;
        HashMap result;
        String[] paths;
        int i = 0;

        if ((obj = getArchetypeObject(objid)) != null) {
            paths = new String[obj.getPathSize()];
            iter = obj.getPathIterator();
            while (iter.hasNext()) {
                paths[i++] = (String) iter.next();
            }
            result = new HashMap();
            //noinspection unchecked
            result.put("objid", objid);
            //noinspection unchecked
            result.put("result", paths);
            //noinspection unchecked
            result.put("errorcode", MiddlewareErrors.OK);
        } else {
            result = middlewareException(request, MiddlewareErrors.OBJECT_NOT_FOUND,
                    "Object with id " + objid + " not found");
        }
        return createJsonString(result);
    }

    public static ArchetypeObject getArchetypeObject(String guid, boolean retrieve) {
        ArchetypeObject object;

        try {
            if (retrieve) {
                return retrieveArchetypeObject(guid, false, false);
            } else {
                if ((object = getArchetypeObject(guid)) != null) {
                    return object;
                }
                return retrieveArchetypeObject(guid, false, false);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return null;
    }

    public static ArchetypeObject retrieveArchetypeObject(String guid, boolean spath, boolean ignoredel)
            throws Exception {
        DataManager dataManager = DataManager.getInstance();
        ArchetypeObject archetype;
        String xml;
        
        /*
            <RMObject>
                <primitive>
                    <path></path>
                    <value></value>
                </primitive>
            </RMObject>
        */
        if (spath) {
            xml = LocatableUtils.retrieveLocatable(guid.trim(), ignoredel);
        } else {
            xml = LocatableUtils.retrieveLocatableSPN(guid.trim(), ignoredel);
        }
        if (xml != null) {
            Map map = WebUtils.parseEHRXml(xml);
            archetype = dataManager.createArchetypeObject(guid);
            for (Object o : map.entrySet()) {
                Map.Entry e = (Map.Entry) o;
                archetype.addPathValue((String) e.getKey(), (String) e.getValue());
            }
            dataManager.updateObject(archetype);
            return archetype;
        }
        return null;
    }

    public static ArchetypeObject getArchetypeObject(String objid) {
        ArchetypeObject archetype;
        DataManager dataManager;

        dataManager = DataManager.getInstance();
        if ((archetype = (ArchetypeObject) dataManager.getObject(objid)) != null) {
            return archetype;
        } else {
            if ((archetype = dataManager.findArchetypeObjectByGUID(objid)) != null) {
                return archetype;
            }
        }
        return null;
    }
}
