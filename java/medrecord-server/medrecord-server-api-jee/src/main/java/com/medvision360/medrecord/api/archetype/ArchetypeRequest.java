package com.medvision360.medrecord.api.archetype;

@SuppressWarnings("UnusedDeclaration")
public class ArchetypeRequest
{
    private String m_adl;

    /**
     * The openEHR Archetype as an ADL string.
     * 
     * @apiexample archetype (adl_version=1.4) openEHR-EHR-OBSERVATION.blood_pressure.v1\n
     *   concept\n
     *     [at0000]\n
     *   language\n
     *     original_language = ....
     */
    public String getAdl()
    {
        return m_adl;
    }

    public void setAdl(String adl)
    {
        m_adl = adl;
    }
}
