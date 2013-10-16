package com.medvision360.medrecord.basex.cmd;

import java.util.Iterator;

import org.basex.data.Data;
import org.basex.util.Token;
import org.basex.util.list.IntList;

class DocumentNameIterator implements Iterator<String>
{
    private final IntList m_il;
    private final Data m_data;
    private int i;
    private int size;

    DocumentNameIterator(IntList il, Data data)
    {
        m_il = il;
        m_data = data;
        i = 0;
        size = m_il.size();
    }

    @Override
    public boolean hasNext()
    {
        return i < size;
    }

    @Override
    public String next()
    {
        int pre = m_il.get(i++);
        byte[] txt = m_data.text(pre, true); // map from basex int to basex byte string
        String path = Token.string(txt); // map from basex byte string to basex string
        String id = path.substring(path.lastIndexOf("/") + 1); // strip off {m_path}/{subpath}/
        return id;
    }

    @Override
    public void remove()
    {
        throw new UnsupportedOperationException("Read only iterator");
    }
}
