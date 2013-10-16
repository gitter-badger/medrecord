package com.medvision360.medrecord.spi.base;

import com.medvision360.medrecord.spi.LocatableStore;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.UIDBasedID;

import static com.google.common.base.Preconditions.checkNotNull;

public abstract class AbstractLocatableStore implements LocatableStore {
    protected String m_name;

    protected AbstractLocatableStore(String name) {
        m_name = checkNotNull(name, "name cannot be null");
    }

    @Override
    public boolean supports(Locatable locatable) {
        checkNotNull(locatable, "locatable cannot be null");
        return true;
    }

    @Override
    public boolean supports(Archetyped archetyped) {
        checkNotNull(archetyped, "archetyped cannot be null");
        return true;
    }

    @Override
    public String getName() {
        return m_name;
    }

    @Override
    public boolean supportsTransactions() {
        return false;
    }

    @Override
    public void begin()
            throws TransactionException {
    }

    @Override
    public void commit()
            throws TransactionException {
    }

    @Override
    public void rollback()
            throws TransactionException {
    }

    protected NotFoundException notFound(Object obj) {
        return new NotFoundException(String.format("Locatable %s not found", obj));
    }

    protected NotFoundException notFound(Object obj, Throwable cause) {
        return new NotFoundException(String.format("Locatable %s not found", obj), cause);
    }

    protected DuplicateException duplicate(Object obj) {
        return new DuplicateException(String.format("Locatable %s already exists", obj));
    }

    protected HierObjectID getHierObjectID(Locatable locatable) {
        UIDBasedID uidBasedID = locatable.getUid();
        if (!(uidBasedID instanceof HierObjectID)) {
            return new HierObjectID(uidBasedID.getValue());
        }
        return (HierObjectID) uidBasedID;
    }
}
