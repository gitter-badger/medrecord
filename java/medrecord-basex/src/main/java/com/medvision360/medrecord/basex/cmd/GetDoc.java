package com.medvision360.medrecord.basex.cmd;

import java.io.IOException;

import org.basex.core.Command;
import org.basex.core.DBLocking;
import org.basex.core.LockResult;
import org.basex.core.Perm;
import org.basex.core.Prop;
import org.basex.data.Data;
import org.basex.io.serial.Serializer;
import org.basex.io.serial.SerializerProp;
import org.basex.query.value.node.DBNode;

import static org.basex.core.Text.RES_NOT_FOUND_X;
import static org.basex.core.Text.QUERY_EXECUTED_X_X;

/**
 * Retrieves an XML document from the store. Roughly equivalent to doc("dbname/path") in XQuery, 
 * but will always look inside the current database.
 * 
 * @see org.basex.core.cmd.Export for inspiration
 * @see org.basex.core.cmd.Retrieve for inspiration
 */
public class GetDoc extends Command
{
    public GetDoc(String path)
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
        
        Data data = context.data();
        int pre = data.resources.doc(path);
        if (pre == -1)
        {
            return error(RES_NOT_FOUND_X, path);
        }
        
        DBNode doc = new DBNode(data, pre, Data.DOC);
        SerializerProp prop = new SerializerProp(data.meta.prop.get(Prop.EXPORTER));
        Serializer ser = Serializer.get(out, prop);
        ser.serialize(doc);
        ser.close();
        
        return info(QUERY_EXECUTED_X_X, "", perf);
    }

    @Override
    public void databases(final LockResult lr) {
        lr.read.add(DBLocking.CTX);
    }
}
