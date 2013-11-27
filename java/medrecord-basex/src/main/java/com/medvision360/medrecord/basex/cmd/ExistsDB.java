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
import org.basex.core.Databases;
import org.basex.core.LockResult;
import org.basex.core.Perm;

import static org.basex.core.Text.NAME_INVALID_X;

/**
 * Check whether the named database exists.
 */
public class ExistsDB extends Command
{
    private boolean m_result;

    public ExistsDB(String name)
    {
        super(Perm.READ, false, name);
    }

    @Override
    protected boolean run() throws IOException
    {
        final String name = args[0];
        if (!Databases.validName(name))
        {
            return error(NAME_INVALID_X, name);
        }

        m_result = context.mprop.dbexists(name);

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
