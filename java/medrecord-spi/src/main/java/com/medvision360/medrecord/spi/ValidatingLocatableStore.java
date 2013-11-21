package com.medvision360.medrecord.spi;

import java.io.IOException;

import com.medvision360.medrecord.spi.base.AbstractLocatableStore;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.IOValidationException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import com.medvision360.medrecord.api.exceptions.StatusException;
import com.medvision360.medrecord.api.exceptions.ValidationException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

import static com.google.common.base.Preconditions.checkNotNull;

public class ValidatingLocatableStore extends AbstractLocatableStore implements LocatableStore
{
    protected LocatableStore m_delegate;
    protected LocatableValidator m_validator;
    protected boolean m_validateOnRetrieve = false;

    public ValidatingLocatableStore(String name, LocatableSelector locatableSelector,
            LocatableStore delegate, LocatableValidator validator)
    {
        super(name, locatableSelector);
        m_delegate = checkNotNull(delegate, "delegate cannot be null");
        m_validator = checkNotNull(validator, "validator cannot be null");
    }

    public ValidatingLocatableStore(String name, LocatableStore delegate, LocatableValidator validator)
    {
        super(name);
        m_delegate = checkNotNull(delegate, "delegate cannot be null");
        m_validator = checkNotNull(validator, "validator cannot be null");
    }

    public void setValidateOnRetrieve(boolean validateOnRetrieve)
    {
        m_validateOnRetrieve = validateOnRetrieve;
    }

    @Override
    public boolean supports(Locatable test)
    {
        return super.supports(test) && m_delegate.supports(test);
    }

    @Override
    public boolean supports(Archetyped test)
    {
        return super.supports(test) && m_delegate.supports(test);
    }

    @Override
    public Locatable get(HierObjectID id) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");
        Locatable result = m_delegate.get(id);
        validateOnRetrieve(result);
        return result;
    }

    @Override
    public Locatable insert(Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException, SerializeException, ValidationException
    {
        checkNotNull(locatable, "locatable cannot be null");
        validate(locatable);
        return m_delegate.insert(locatable);
    }

    @Override
    public Locatable insert(EHR EHR, Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException, SerializeException, ValidationException
    {
        checkNotNull(EHR, "EHR cannot be null");
        checkNotNull(locatable, "locatable cannot be null");
        validate(locatable);
        return m_delegate.insert(EHR, locatable);
    }

    @Override
    public Locatable update(Locatable locatable)
            throws NotSupportedException, NotFoundException, IOException, SerializeException, ValidationException
    {
        checkNotNull(locatable, "locatable cannot be null");
        validate(locatable);
        return m_delegate.update(locatable);
    }

    @Override
    public void delete(HierObjectID id) throws NotFoundException, IOException
    {
        m_delegate.delete(id);
    }

    @Override
    public boolean has(HierObjectID id) throws IOException
    {
        return m_delegate.has(id);
    }

    @Override
    public Iterable<HierObjectID> list() throws IOException
    {
        return m_delegate.list();
    }

    @Override
    public Iterable<HierObjectID> list(EHR EHR) throws IOException, NotFoundException
    {
        return m_delegate.list(EHR);
    }

    @Override
    public Iterable<HierObjectID> list(EHR EHR, String rmEntity) throws IOException, NotFoundException
    {
        return m_delegate.list(EHR, rmEntity);
    }

    @Override
    public void clear() throws IOException
    {
        m_delegate.clear();
    }

    @Override
    public void verifyStatus() throws StatusException
    {
        m_delegate.verifyStatus();
    }

    @Override
    public String reportStatus() throws StatusException
    {
        return m_delegate.reportStatus();
    }

    protected void validate(Locatable locatable) throws ValidationException
    {
        try
        {
            m_validator.check(locatable);
        }
        catch (NotSupportedException e)
        {
            // ignore
        }
    }

    protected void validateOnRetrieve(Locatable result) throws IOValidationException
    {
        try
        {
            m_validator.check(result);
        }
        catch (NotSupportedException e)
        {
            // ignore
        }
        catch (ValidationException e)
        {
            throw new IOValidationException(e);
        }
    }


}
