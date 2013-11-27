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
