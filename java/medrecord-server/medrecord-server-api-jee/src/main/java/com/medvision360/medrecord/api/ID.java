package com.medvision360.medrecord.api;

@SuppressWarnings("UnusedDeclaration")
public class ID
{
    String m_id;

    /**
     * An openEHR ObjectID value. Often but not always a UUID.
     * 
     * @apiexample A660D3C2-50C4-44AA-8663-83FEEB22ADF1
     */
    public String getId()
    {
        return m_id;
    }

    public void setId(String id)
    {
        m_id = id;
    }
}
