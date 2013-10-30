package com.medvision360.medrecord.spi.base;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.medvision360.medrecord.spi.LocatableSelector;
import com.medvision360.medrecord.spi.LocatableSelectorBuilder;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.UIDBasedID;

import static com.google.common.base.Preconditions.checkNotNull;

@SuppressWarnings("UnusedDeclaration")
public abstract class AbstractLocatableStore
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

    public boolean supports(Locatable test)
    {
        checkNotNull(test, "locatable cannot be null");
        return m_locatableSelector.supports(test);
    }

    public boolean supports(Archetyped test)
    {
        checkNotNull(test, "archetyped cannot be null");
        return m_locatableSelector.supports(test);
    }

    public String getName()
    {
        return m_name;
    }

    public void initialize() throws IOException
    {
    }

    public boolean supportsTransactions()
    {
        return false;
    }

    public void begin()
            throws TransactionException
    {
    }

    public void commit()
            throws TransactionException
    {
    }

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
            HierObjectID newId = new HierObjectID(uidBasedID.getValue());
            setUid(locatable, newId);
            return newId;
        }
        return (HierObjectID) uidBasedID;
    }

    protected void setUid(Locatable locatable, UIDBasedID uid) throws IllegalArgumentException
    {
        try
        {
            Method[] methods = Locatable.class.getDeclaredMethods();
            Method setter = null;
            for (Method method : methods)
            {
                if ("setUid".equals(method.getName()))
                {
                    setter = method;
                    break;
                }
            }
            if (setter == null)
            {
                throw new NoSuchMethodException("setUid");
            }
            setter.setAccessible(true);
            setter.invoke(locatable, uid);
        }
        catch (NoSuchMethodException|InvocationTargetException |IllegalAccessException e)
        {
            throw new IllegalArgumentException(e);
        }
    }
}
