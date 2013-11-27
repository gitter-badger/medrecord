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

/**
 * Check whether the given XML node exists in the current database.
 */
public class Exists extends Command
{
    private boolean m_result;

    public Exists(String path)
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

        m_result = context.data().resources.doc(path) != -1;

        return true;
    }

    @Override
    public void databases(final LockResult lr)
    {
        lr.read.add(DBLocking.CTX);
    }

    public boolean exists()
    {
        return m_result;
    }
}
