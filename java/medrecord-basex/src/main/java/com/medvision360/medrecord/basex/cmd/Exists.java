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
    private boolean result;
    
    public Exists(String path)
    {
        super(Perm.READ, true, path);
    }

    @Override
    protected boolean run() throws IOException
    {
        String path = args[0];
        if (path.startsWith("/")) {
            path = path.substring(1);
        }
        
        result = context.data().resources.doc(path) != -1;
        
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
