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
package com.medvision360.medrecord.basex.cmd;

import java.util.Iterator;

import org.basex.data.Data;
import org.basex.util.list.IntList;

class DocumentNameIterable implements Iterable<String>
{
    private final IntList m_il;
    private final Data m_data;

    public DocumentNameIterable(IntList il, Data data)
    {
        m_il = il;
        m_data = data;
    }

    @Override
    public Iterator<String> iterator()
    {
        return new DocumentNameIterator(m_il, m_data);
    }
}
