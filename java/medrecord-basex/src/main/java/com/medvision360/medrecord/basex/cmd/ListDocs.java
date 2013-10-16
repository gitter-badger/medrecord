package com.medvision360.medrecord.basex.cmd;

import java.io.IOException;

import org.basex.core.Command;
import org.basex.core.DBLocking;
import org.basex.core.LockResult;
import org.basex.core.Perm;
import org.basex.data.Data;
import org.basex.util.list.IntList;

/**
 * List all document nodes in the current database. A little like the LIST command, 
 * but lists xml nodes instead of raw resources.
 * 
 * @see org.basex.core.cmd.List for inspiration
 */
public class ListDocs extends Command
{
    private Iterable<String> result;
    
    public ListDocs(String path)
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
        
        IntList il = context.data().resources.docs(path, false);
        result = preListToStringIterable(il);
        
        return true;
    }

    private Iterable<String> preListToStringIterable(final IntList il) {
        Data data = context.data();
        Iterable<String> result = new DocumentNameIterable(il, data);
        return result;
    }

    @Override
    public void databases(final LockResult lr) {
        lr.read.add(DBLocking.CTX);
    }
  
    public Iterable<String> list()
    {
        return result;
    }
}
