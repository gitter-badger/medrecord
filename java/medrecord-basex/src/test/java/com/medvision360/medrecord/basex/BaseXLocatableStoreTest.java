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

import com.medvision360.medrecord.spi.LocatableParser;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableSerializer;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.tck.LocatableStoreTCKTestBase;
import org.basex.core.Context;

public class BaseXLocatableStoreTest extends LocatableStoreTCKTestBase
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

    LocatableParser parser = new MockLocatableParser();
    LocatableSerializer serializer = new MockLocatableSerializer();
    String name = "BaseXLocatableStoreTest";
    String path = "unittest";

    @Override
    protected LocatableStore getStore() throws Exception
    {
        return new BaseXLocatableStore(
                ctx,
                parser,
                serializer,
                LocatableSelectorBuilder.any(),
                name,
                path
        );
    }

}
