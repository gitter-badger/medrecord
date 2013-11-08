package com.zorggemak.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

@SuppressWarnings({"SpellCheckingInspection", "unchecked", "UnusedDeclaration"})
public class ArchetypeObject implements Serializable {
    public static final int TYPE_UNKNOWN = 0;
    public static final int TYPE_GENERAL = 2;

    private int type = TYPE_UNKNOWN;
    private String objid = "";
    private String guid = "";
    private String archid = "";
    private String nodeid = "";
    private String partyid = "";
    private long lasttime = 0;
    private HashMap values = null;
    private boolean preppost = false;
    private boolean isnew = false;

    public ArchetypeObject(String objid) {
        this.type = TYPE_GENERAL;
        this.objid = objid;
        this.guid = "";
        this.archid = "";
        this.lasttime = System.currentTimeMillis();
        this.values = new HashMap();
    }

    public String getObjectId() {
        return objid;
    }

    public void setObjectId(String value) {
        objid = value;
    }

    public int getType() {
        return type;
    }

    public void setType(int value) {
        type = value;
    }

    public String getGUID() {
        return guid;
    }

    public void setGUID(String value) {
        guid = value;
    }

    public String getArchetypeId() {
        return archid;
    }

    public void setArchetypeId(String value) {
        archid = value;
    }

    public String getNodeId() {
        return nodeid;
    }

    public void setNodeId(String value) {
        nodeid = value;
    }

    public String getPartyId() {
        return partyid;
    }

    public void setPartyId(String value) {
        partyid = value;
    }

    /**
     * Add a path/value pair to the object
     *
     * @param path the path (is the key)
     * @param value the value
     */
    public void addPathValue(String path, String value) {
        if (preppost) {
            values.put(path, value);
            lasttime = System.currentTimeMillis();
        } else {
            // To avoid changing of the values
            if (!values.containsKey(path)) {
                values.put(path, value);
                lasttime = System.currentTimeMillis();
            }
        }
        if ("/archetype_id/value".equals(path)) {
            archid = value;
        }
    }

    public String getPathValue(String path) {
        if (values.containsKey(path)) {
            return ((String) values.get(path));
        }
        return "";
    }

    public Iterator getPathIterator() {
        ArrayList list;

        if (values.isEmpty()) {
            list = new ArrayList();
            list.add("Empty object");
            return (list.iterator());
        }
        return (values.keySet().iterator());
    }

    public int getPathSize() {
        if (values.isEmpty()) {
            return 0;
        }
        return (values.keySet().size());
    }

    public long getTimePassed() {
        return (System.currentTimeMillis() - lasttime);
    }

    public boolean isPrepareForPost() {
        return preppost;
    }

    public void setPrepareForPost(boolean flag) {
        preppost = flag;
    }

    public boolean isNew() {
        return isnew;
    }

    public void setNew(boolean flag) {
        isnew = flag;
    }

}
