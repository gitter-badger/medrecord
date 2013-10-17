package com.medvision360.medrecord.spi.base;

import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.UIDBasedID;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractLocatableStore implements LocatableStore
{
    protected String m_name;
    protected LocatableSelector m_locatableSelector;

    public AbstractLocatableStore(String name, LocatableSelector locatableSelector)
    {
        m_name = checkNotNull(name, "name cannot be null");
        m_locatableSelector = checkNotNull(locatableSelector, "locatableSelector cannot be null");
    }

    protected AbstractLocatableStore(String name)
    {
        this(name, LocatableSelectorBuilder.any());
    }

    @Override
    public boolean supports(Locatable test)
    {
        checkNotNull(test, "locatable cannot be null");
        return m_locatableSelector.supports(test);
    }

    @Override
    public boolean supports(Archetyped test)
    {
        checkNotNull(test, "archetyped cannot be null");
        return m_locatableSelector.supports(test);
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public boolean supportsTransactions()
    {
        return false;
    }

    @Override
    public void begin()
            throws TransactionException
    {
    }

    @Override
    public void commit()
            throws TransactionException
    {
    }

    @Override
    public void rollback()
            throws TransactionException
    {
    }

    protected NotFoundException notFound(Object obj)
    {
        return new NotFoundException(String.format("Locatable %s not found", obj));
    }

    protected NotFoundException notFound(Object obj, Throwable cause)
    {
        return new NotFoundException(String.format("Locatable %s not found", obj), cause);
    }

    protected DuplicateException duplicate(Object obj)
    {
        return new DuplicateException(String.format("Locatable %s already exists", obj));
    }

    protected HierObjectID getHierObjectID(Locatable locatable)
    {
        UIDBasedID uidBasedID = locatable.getUid();
        if (!(uidBasedID instanceof HierObjectID))
        {
            return new HierObjectID(uidBasedID.getValue());
        }
        return (HierObjectID) uidBasedID;
    }
}
