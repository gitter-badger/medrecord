package com.zorggemak.data;

import com.zorggemak.commons.ZorgGemakDefines;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

@SuppressWarnings("unchecked")
public class DataManager {
    private static final Logger log = LoggerFactory.getLogger(DataManager.class);
    private static final DataManager instance = new DataManager();

    private HashMap storingMap;
    private HashMap observingMap;

    public static synchronized DataManager getInstance() {
        return instance;
    }

    private DataManager() {
        storingMap = new HashMap();
        observingMap = new HashMap();
    }

    public void loop() {
        Object[] ids;
        String objId;
        Long timeObj;

        try {
            ids = observingMap.keySet().toArray();
            for (int i = 0; i < ids.length; i++) {
                objId = (String) ids[i];
                timeObj = (Long) observingMap.get(objId);
                if ((timeObj.longValue() + ZorgGemakDefines.REFER_TIMEOUT) < System.currentTimeMillis()) {
                    // Object timeout
                    if (log.isDebugEnabled()) {
                        log.debug("purge object " + objId);
                    }
                    storingMap.remove(objId);
                    observingMap.remove(objId);
                }
            }
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }
    }

    public String insertObject(Object object)
            throws Exception {
        String objId = generateUniqueKey();
        storingMap.put(objId, object);
        observingMap.put(objId, new Long(System.currentTimeMillis()));
        return objId;
    }

    public Object getObject(String objId) {
        if (keyExists(objId)) {
            updateTimestamp(objId);
            log.debug("getObject object=" + storingMap.get(objId) + " (" + objId + ")");
            return (storingMap.get(objId));
        }
        return null;
    }

    public String updateObject(Object object) {
        String objId = "";

        if (object instanceof ArchetypeObject) {
            objId = ((ArchetypeObject) object).getObjectId();
        }
        if (keyExists(objId)) {
            storingMap.put(objId, object);
            observingMap.put(objId, new Long(System.currentTimeMillis()));
            return objId;
        }
        return null;
    }

    public ArchetypeObject createArchetypeObject(String guid) {
        ArchetypeObject object;
        String objId = generateUniqueKey();

        object = new ArchetypeObject(objId);
        if (guid != null) {
            object.setGUID(guid);
        } else {
            object.setPrepareForPost(true);
            object.setNew(true);
        }
        storingMap.put(objId, object);
        observingMap.put(objId, new Long(System.currentTimeMillis()));
        return object;
    }

    public ArchetypeObject findArchetypeObjectByGUID(String guid) {
        Iterator it;
        Object object;

        it = storingMap.values().iterator();
        while (it.hasNext()) {
            object = it.next();
            if (object instanceof ArchetypeObject) {
                if (((ArchetypeObject) object).getGUID().equals(guid)) {
                    return ((ArchetypeObject) object);
                }
            }
        }
        return null;
    }

    private String generateUniqueKey() {
        Random random = new Random();
        String objId;

        for (int i = 0; i < 5; i++) {
            objId = Long.toHexString(random.nextLong());
            if (!keyExists(objId)) {
                return objId;
            }
        }
        return null;
    }

    private boolean keyExists(String objId) {
        return (storingMap.containsKey(objId) && observingMap.containsKey(objId));
    }

    private void updateTimestamp(String objId) {
        if (keyExists(objId)) {
            observingMap.put(objId, new Long(System.currentTimeMillis()));
        }
    }

}

