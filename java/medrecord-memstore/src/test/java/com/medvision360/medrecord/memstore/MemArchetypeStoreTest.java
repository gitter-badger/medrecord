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
package com.medvision360.medrecord.memstore;

import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.tck.ArchetypeStoreTCKTestBase;

public class MemArchetypeStoreTest extends ArchetypeStoreTCKTestBase
{
    @Override
    protected ArchetypeStore getStore() throws Exception
    {
        return new MemArchetypeStore();
    }
}
