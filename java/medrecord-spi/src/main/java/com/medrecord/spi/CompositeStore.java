package com.medrecord.spi;

import com.google.common.collect.Iterables;
import com.medrecord.spi.exceptions.DuplicateException;
import com.medrecord.spi.exceptions.NotFoundException;
import com.medrecord.spi.exceptions.NotSupportedException;
import com.medrecord.spi.exceptions.StatusException;
import com.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;
import org.openehr.rm.support.identification.UIDBasedID;

import java.io.IOException;
import java.io.OutputStream;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CompositeStore implements LocatableStore, CompositeService<LocatableStore> {
    private List<LocatableStore> delegates = new LinkedList<>();
    private String name;

    public CompositeStore(String name) {
        this.name = checkNotNull(name, "name cannot be null");
    }

    public void addDelegate(LocatableStore delegate) {
        delegates.add(checkNotNull(delegate, "delegate cannot be null"));
    }
    
    @Override
    public Locatable get(HierObjectID id)
            throws NotFoundException, IOException {
        checkNotNull(id, "id cannot be null");
        NotFoundException nfe = null;
        IOException ioe = null;
        for (LocatableStore delegate : delegates) {
            try {
                return delegate.get(id);
            } catch (NotFoundException e) {
                if (nfe == null) {
                    nfe = e;
                }
            } catch (IOException e) {
                if (ioe == null) {
                    ioe = e;
                }
            }
        }
        if (ioe != null) {
            throw ioe;
        }
        if (nfe != null) {
            throw nfe;
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public Locatable get(ObjectVersionID id)
            throws NotFoundException, IOException {
        checkNotNull(id, "id cannot be null");
        NotFoundException nfe = null;
        IOException ioe = null;
        for (LocatableStore delegate : delegates) {
            try {
                return delegate.get(id);
            } catch (NotFoundException e) {
                if (nfe == null) {
                    nfe = e;
                }
            } catch (IOException e) {
                if (ioe == null) {
                    ioe = e;
                }
            }
        }
        if (ioe != null) {
            throw ioe;
        }
        if (nfe != null) {
            throw nfe;
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public Iterable<Locatable> getVersions(HierObjectID id)
            throws NotFoundException, IOException {
        checkNotNull(id, "id cannot be null");
        NotFoundException nfe = null;
        IOException ioe = null;
        for (LocatableStore delegate : delegates) {
            try {
                return delegate.getVersions(id);
            } catch (NotFoundException e) {
                if (nfe == null) {
                    nfe = e;
                }
            } catch (IOException e) {
                if (ioe == null) {
                    ioe = e;
                }
            }
        }
        if (ioe != null) {
            throw ioe;
        }
        if (nfe != null) {
            throw nfe;
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public Locatable insert(Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException {
        checkNotNull(locatable, "locatable cannot be null");
        for (LocatableStore delegate : delegates) {
            if (delegate.supports(locatable)) {
                return delegate.insert(locatable);
            }
        }
        throw new NotSupportedException(String.format("No delegate store supports the locatable %s", locatable));
    }

    @Override
    public Locatable update(Locatable locatable)
            throws NotSupportedException, NotFoundException, IOException {
        checkNotNull(locatable, "locatable cannot be null");
        UIDBasedID uidBasedID = locatable.getUid();
        HierObjectID hierObjectID = uidBasedID instanceof HierObjectID ? (HierObjectID) uidBasedID : null; 
        ObjectVersionID objectVersionID = uidBasedID instanceof ObjectVersionID ? (ObjectVersionID) uidBasedID : null; 
        if (hierObjectID == null && objectVersionID == null) {
            throw new NotSupportedException(
                    String.format("Locable UID of locatable %s has to be a HierObjectID or ObjectVersionID, was %s", 
                            locatable, uidBasedID.getClass().getSimpleName()));
        }
        boolean haveVersion = hierObjectID != null; 
        
        for (LocatableStore delegate : delegates) {
            boolean found = haveVersion ? delegate.has(hierObjectID) : delegate.hasAny(objectVersionID);
            if (found) {
                return delegate.update(locatable);
            }
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", locatable));
    }

    @Override
    public boolean has(HierObjectID id)
            throws IOException {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : delegates) {
            if (delegate.has(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean has(ObjectVersionID id)
            throws IOException {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : delegates) {
            if (delegate.has(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean hasAny(ObjectVersionID id)
            throws IOException {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : delegates) {
            if (delegate.hasAny(id)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void delete(HierObjectID id)
            throws NotFoundException, IOException {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : delegates) {
            if (delegate.has(id)) {
                delegate.delete(id);
                return;
            }
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public void delete(ObjectVersionID id)
            throws NotFoundException, IOException {
        checkNotNull(id, "id cannot be null");
        for (LocatableStore delegate : delegates) {
            if (delegate.has(id)) {
                delegate.delete(id);
                return;
            }
        }
        throw new NotFoundException(String.format("No delegate store contains the locatable %s", id));
    }

    @Override
    public Iterable<HierObjectID> list()
            throws IOException {
        List<Iterable<HierObjectID>> all = new LinkedList<>();
        for (LocatableStore delegate : delegates) {
            all.add(delegate.list());
        }
        Iterable<HierObjectID> result = Iterables.concat(all);
        return result;
    }

    @Override
    public Iterable<ObjectVersionID> listVersions()
            throws IOException {
        List<Iterable<ObjectVersionID>> all = new LinkedList<>();
        for (LocatableStore delegate : delegates) {
            all.add(delegate.listVersions());
        }
        Iterable<ObjectVersionID> result = Iterables.concat(all);
        return result;
    }

    @Override
    public Iterable<Locatable> list(String XQuery)
            throws NotSupportedException, IOException {
        checkNotNull(XQuery, "XQuery cannot be null");
        List<Iterable<Locatable>> all = new LinkedList<>();
        for (LocatableStore delegate : delegates) {
            all.add(delegate.list(XQuery));
        }
        Iterable<Locatable> result = Iterables.concat(all);
        return result;
    }

    @Override
    public void query(String XQuery, OutputStream os)
            throws NotSupportedException, IOException {
        checkNotNull(XQuery, "XQuery cannot be null");
        checkNotNull(os, "os cannot be null");
        for (LocatableStore delegate : delegates) {
            delegate.query(XQuery, os);
        }
    }

    @Override
    public void initialize()
            throws IOException {
        for (LocatableStore delegate : delegates) {
            delegate.initialize();
        }
    }

    @Override
    public void clear()
            throws IOException {
        for (LocatableStore delegate : delegates) {
            delegate.clear();
        }
    }

    @Override
    public boolean supports(Locatable locatable) {
        checkNotNull(locatable, "locatable cannot be null");
        for (LocatableStore delegate : delegates) {
            if (delegate.supports(locatable)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supports(Archetyped archetyped) {
        checkNotNull(archetyped, "archetyped cannot be null");
        for (LocatableStore delegate : delegates) {
            if (delegate.supports(archetyped)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void verifyStatus()
            throws StatusException {
        for (LocatableStore delegate : delegates) {
            delegate.verifyStatus();
        }
    }

    @Override
    public String reportStatus()
            throws StatusException {
        StringBuilder b = new StringBuilder();
        for (LocatableStore delegate : delegates) {
            b.append(delegate.getName());
            b.append(": ");
            b.append(delegate.reportStatus());
            b.append("\n");
        }
        return b.toString();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public boolean supportsTransactions() {
        for (LocatableStore delegate : delegates) {
            if (!delegate.supportsTransactions()) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void begin()
            throws TransactionException {
        for (LocatableStore delegate : delegates) {
            delegate.begin();
        }
    }

    @Override
    public void commit()
            throws TransactionException {
        // todo we _should_ have two-phase commit for this, but, well, we don't
        for (LocatableStore delegate : delegates) {
            delegate.commit();
        }
    }

    @Override
    public void rollback()
            throws TransactionException {
        for (LocatableStore delegate : delegates) {
            delegate.rollback();
        }
    }
}
