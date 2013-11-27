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
package com.medvision360.medrecord.basex;

import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.EHRStoreTCKTestBase;
import org.basex.core.Context;
import org.openehr.rm.support.identification.HierObjectID;

public class BaseXEHRStoreTest extends EHRStoreTCKTestBase
{
    Context ctx;

    @Override
    public void setUp() throws Exception
    {
        ctx = new Context();
        super.setUp();
    }

    @Override
    public void tearDown() throws Exception
    {
        super.tearDown();
        ctx.close();
    }

    XmlEHRConverter converter = new XmlEHRConverter();
    String name = "BaseXEHRStoreTest";
    String path = "unittest";

    @Override
    protected EHRStore getStore() throws Exception
    {
        return new BaseXEHRStore(
                ctx,
                converter,
                converter,
                new HierObjectID(name),
                path
        );
    }
}
