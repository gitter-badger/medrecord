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

import java.io.IOException;

import org.basex.core.Command;
import org.basex.core.DBLocking;
import org.basex.core.LockResult;
import org.basex.core.Perm;
import org.basex.data.Data;
import org.basex.util.list.IntList;

/**
 * List all document nodes in the current database. A little like the LIST command, but lists xml nodes instead of raw
 * resources.
 *
 * @see org.basex.core.cmd.List for inspiration
 */
public class ListDocs extends Command
{
    private Iterable<String> m_result;

    public ListDocs(String path)
    {
        super(Perm.READ, true, path);
    }

    @Override
    protected boolean run() throws IOException
    {
        String path = args[0];
        if (path.startsWith("/"))
        {
            path = path.substring(1);
        }

        IntList il = context.data().resources.docs(path, false);
        m_result = preListToStringIterable(il);

        return true;
    }

    private Iterable<String> preListToStringIterable(final IntList il)
    {
        Data data = context.data();
        Iterable<String> result = new DocumentNameIterable(il, data);
        return result;
    }

    @Override
    public void databases(final LockResult lr)
    {
        lr.read.add(DBLocking.CTX);
    }

    public Iterable<String> list()
    {
        return m_result;
    }
}
