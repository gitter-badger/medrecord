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
