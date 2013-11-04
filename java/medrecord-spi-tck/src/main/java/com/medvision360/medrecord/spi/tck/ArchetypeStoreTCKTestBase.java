package com.medvision360.medrecord.spi.tck;

import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.spi.exceptions.InUseException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.support.identification.ArchetypeID;

public abstract class ArchetypeStoreTCKTestBase extends RMTestBase
{
    protected ArchetypeStore m_store;

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
        Archetype orig = loadArchetype();
        ArchetypeID archetypeID = orig.getArchetypeId();
        Archetyped archetyped = new Archetyped(archetypeID, "1.0.2");
        
        assertFalse(m_store.has(archetyped));
        assertFalse(m_store.has(archetypeID));
        
        try
        {
            m_store.get(archetyped);
            fail("Get should fail on non-existent archetype");
        } catch (NotFoundException e) {}
        
        try
        {
            m_store.get(archetypeID);
            fail("Get should fail on non-existent archetype");
        } catch (NotFoundException e) {}
        
        m_store.insert(orig);
        assertTrue(m_store.has(archetyped));
        assertTrue(m_store.has(archetypeID));
        WrappedArchetype wrappedResult = m_store.get(archetypeID);
        Archetype result = wrappedResult.getArchetype();
        assertEqualish(orig, result);
        
        // not locked, so can delete
        m_store.delete(archetypeID);
        assertFalse(m_store.has(archetyped));
        assertFalse(m_store.has(archetypeID));
        
        try
        {
            m_store.delete(archetypeID);
            fail("Delete should fail on non-existent archetype");
        } catch (NotFoundException e) {}
        
        m_store.insert(orig);
        m_store.lock(archetypeID);
        try
        {
            m_store.delete(archetypeID);
            fail("Delete should fail after locking");
        }
        catch (InUseException e) {}

        // store is empty and archetypes are unlocked after clear()
        m_store.clear();
        assertFalse(m_store.has(archetypeID));
        m_store.insert(orig);
    }
    
    protected abstract ArchetypeStore getStore() throws Exception;
}
