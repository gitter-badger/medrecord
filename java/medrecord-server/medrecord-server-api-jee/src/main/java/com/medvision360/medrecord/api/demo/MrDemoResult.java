package com.medvision360.medrecord.api.demo;

/**
 * Stuff returned by the demo.
 */
public class MrDemoResult
{
    private String m_stuff;


    /**
     * Some stuff.
     *
     * @apiexample world
     * @return The stuff.
     */
    public String getStuff()
    {
        return m_stuff;
    }

    public void setStuff(String stuff)
    {
        m_stuff = stuff;
    }
}
