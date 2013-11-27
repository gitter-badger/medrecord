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
