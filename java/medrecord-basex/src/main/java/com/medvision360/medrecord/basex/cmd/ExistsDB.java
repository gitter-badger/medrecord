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
    private boolean result;
    
    public ExistsDB(String name)
    {
        super(Perm.READ, false, name);
    }

    @Override
    protected boolean run() throws IOException
    {
        final String name = args[0];
        if(!Databases.validName(name))
        {
            return error(NAME_INVALID_X, name);
        }
        
        result = context.mprop.dbexists(name);
        
        return true;
    }

    @Override
    public void databases(final LockResult lr) {
        lr.read.add(DBLocking.CTX);
    }
  
    public boolean exists()
    {
        return result;
    }
}
