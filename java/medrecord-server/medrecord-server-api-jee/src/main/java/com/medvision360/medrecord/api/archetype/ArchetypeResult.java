package com.medvision360.medrecord.api.archetype;

public class ArchetypeResult
{
    private String m_archetypeId;
    
    private String m_archetype;

    public void setArchetypeId(String archetypeId)
    {
        m_archetypeId = archetypeId;
    }

    /**
     * The openEHR ArchetypeID value for the retrieved archetype.
     * 
     * @apiexample openEHR-EHR-OBSERVATION.blood_pressure.v1
     */
    public String getArchetypeId()
    {
        return m_archetypeId;
    }

    public void setArchetype(String archetype)
    {
        m_archetype = archetype;
    }

    /**
     * The openEHR Archetype as an ADL string.
     * 
     * @apiexample
     *     archetype (adl_version=1.4)
     *              openEHR-EHR-OBSERVATION.blood_pressure.v1
     *     
     *     concept
     *        [at0000]	-- Blood Pressure
     *     language
     *        original_language = <[ISO_639-1::en]>
     *        translations = <
     *            ["de"] = <
     *                language = <[ISO_639-1::de]>
     *     ...
     */
    public String getAdl()
    {
        return m_archetype;
    }
}
