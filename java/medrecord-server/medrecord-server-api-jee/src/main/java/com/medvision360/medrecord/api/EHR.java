package com.medvision360.medrecord.api;

public class EHR
{
    private String m_id;
    private String m_statusId;
    private String m_systemId;
    private String m_timeCreated;
    private String m_directoryId;
    private boolean m_deleted;

    /**
     * An openEHR HierObjectID value identifying this EHR.
     * Often but not always a UUID.
     * 
     * @apiexample 0F0A7A96-25ED-44AA-B4BD-D6A7F32877F7
     */
    public String getId()
    {
        return m_id;
    }

    public void setId(String id)
    {
        m_id = id;
    }

    /**
     * An openEHR UIDBasedID value identifying the EHRStatus for this EHR.
     * Often but not always a UUID.
     * 
     * @apiexample DC82E362-75AE-443F-9617-C9235A28F69D
     */
    public String getStatusId()
    {
        return m_statusId;
    }

    public void setStatusId(String statusId)
    {
        m_statusId = statusId;
    }

    /**
     * An openEHR HierObjectID value identifying the system that is the origin of this EHR.
     * Often but not always a UUID.
     * 
     * @apiexample B55B6AE8-5DFC-4D0D-82DA-392E331417FB
     */
    public String getSystemId()
    {
        return m_systemId;
    }

    public void setSystemId(String systemId)
    {
        m_systemId = systemId;
    }

    /**
     * An ISO8601 timestamp identifying when this EHR was created.
     * 
     * @apiexample 2013-11-23T17:13:53Z
     */
    public String getTimeCreated()
    {
        return m_timeCreated;
    }

    public void setTimeCreated(String timeCreated)
    {
        m_timeCreated = timeCreated;
    }

    /**
     * An openEHR UIDBasedID value identifying the Directory for this EHR, if any.
     * Often but not always a UUID.
     * 
     * @apiexample A14C2AE8-BA46-4EF8-AFC8-889963DA2BA5
     */
    public String getDirectoryId()
    {
        return m_directoryId;
    }

    public void setDirectoryId(String directoryId)
    {
        m_directoryId = directoryId;
    }

    public boolean isDeleted()
    {
        return m_deleted;
    }

    public void setDeleted(boolean deleted)
    {
        m_deleted = deleted;
    }
}
