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
import org.basex.core.Prop;
import org.basex.data.Data;
import org.basex.io.serial.Serializer;
import org.basex.io.serial.SerializerProp;
import org.basex.query.value.node.DBNode;

import static org.basex.core.Text.QUERY_EXECUTED_X_X;
import static org.basex.core.Text.RES_NOT_FOUND_X;

/**
 * Retrieves an XML document from the store. Roughly equivalent to doc("dbname/path") in XQuery, but will always look
 * inside the current database.
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
        if (path.startsWith("/"))
        {
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
    public void databases(final LockResult lr)
    {
        lr.read.add(DBLocking.CTX);
    }
}
