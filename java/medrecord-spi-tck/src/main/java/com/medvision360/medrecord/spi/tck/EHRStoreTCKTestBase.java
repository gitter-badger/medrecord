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
package com.medvision360.medrecord.spi.tck;

import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

public abstract class EHRStoreTCKTestBase extends RMTestBase
{
    protected EHRStore m_store;

    @Override
    public void setUp() throws Exception
    {
        super.setUp();
        m_store = getStore();
        m_store.clear();
        m_store.initialize();
    }
    
    public void testInitializeCanBeCalledMultipleTimes() throws Exception
    {
        m_store.initialize();
        m_store.initialize();
        m_store.initialize();
    }

    public void testClearCanBeCalledMultipleTimes() throws Exception
    {
        m_store.clear();
        m_store.initialize();
        m_store.clear();
        m_store.initialize();
        m_store.clear();
    }

    public void testStore() throws Exception
    {
        EHR orig = makeEHR();
        HierObjectID id = orig.getEhrID();
        
        assertFalse(m_store.has(id));
        
        try
        {
            m_store.get(id);
            fail("Get should fail on non-existent EHR");
        } catch (NotFoundException e) {}
        
        m_store.insert(orig);
        assertTrue(m_store.has(id));
        EHR result = m_store.get(id);
        assertEqualish(orig, result);
        
        m_store.delete(id);
        assertFalse(m_store.has(id));
        m_store.undelete(id);
        // can undelete multple times
        m_store.undelete(id);
        assertTrue(m_store.has(id));

        // can delete multiple times due to soft-delete
        m_store.delete(id);
        m_store.delete(id);
        
        try
        {
            m_store.insert(orig);
            fail("Delete should fail on previously-existent EHR");
        } catch (DuplicateException e) {}

        // store is empty and EHRs can be re-created after clear()
        m_store.clear();
        assertFalse(m_store.has(id));
        m_store.insert(orig);
    }
    
    protected abstract EHRStore getStore() throws Exception;
    
}
