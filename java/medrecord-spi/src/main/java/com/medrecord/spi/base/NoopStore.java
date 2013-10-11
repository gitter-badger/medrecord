package com.medrecord.spi.base;

import com.medrecord.spi.AuditInfo;
import com.medrecord.spi.AuditedService;
import com.medrecord.spi.VersioningStore;
import com.medrecord.spi.exceptions.DuplicateException;
import com.medrecord.spi.exceptions.NotFoundException;
import com.medrecord.spi.exceptions.NotSupportedException;
import com.medrecord.spi.exceptions.StatusException;
import com.medrecord.spi.exceptions.TransactionException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.changecontrol.VersionedObject;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectVersionID;

import java.io.IOException;
import java.io.OutputStream;

public class NoopStore implements VersioningStore, AuditedService {
    @Override
    public void setAuditInfo(AuditInfo auditInfo) {
        throw new UnsupportedOperationException("todo implement NoopStore.setAuditInfo()");
    }

    @Override
    public Iterable<VersionedObject<? extends Locatable>> getVersionObjects(HierObjectID id)
            throws NotFoundException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.getVersionObjects()");
    }

    @Override
    public void insert(VersionedObject<? extends Locatable> versionedLocatable)
            throws DuplicateException, NotSupportedException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.insert()");
    }

    @Override
    public void update(VersionedObject<? extends Locatable> versionedLocatable)
            throws NotSupportedException, NotFoundException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.update()");
    }

    @Override
    public Iterable<VersionedObject<? extends Locatable>> listVersionObjects(String XQuery)
            throws NotSupportedException, IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("todo implement NoopStore.listVersionObjects()");
    }

    @Override
    public void queryVersionObjects(String XQuery, OutputStream os)
            throws NotSupportedException, IOException, UnsupportedOperationException {
        throw new UnsupportedOperationException("todo implement NoopStore.queryVersionObjects()");
    }

    @Override
    public Locatable get(HierObjectID id)
            throws NotFoundException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.get()");
    }

    @Override
    public Locatable get(ObjectVersionID id)
            throws NotFoundException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.get()");
    }

    @Override
    public Iterable<Locatable> getVersions(HierObjectID id)
            throws NotFoundException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.getVersions()");
    }

    @Override
    public void insert(Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.insert()");
    }

    @Override
    public void update(Locatable locatable)
            throws NotSupportedException, NotFoundException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.update()");
    }

    @Override
    public void delete(HierObjectID id)
            throws NotFoundException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.delete()");
    }

    @Override
    public void delete(ObjectVersionID id)
            throws NotFoundException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.delete()");
    }

    @Override
    public Iterable<HierObjectID> list()
            throws IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.list()");
    }

    @Override
    public Iterable<ObjectVersionID> listVersions()
            throws IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.listVersions()");
    }

    @Override
    public Iterable<Locatable> list(String XQuery)
            throws NotSupportedException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.list()");
    }

    @Override
    public void query(String XQuery, OutputStream os)
            throws NotSupportedException, IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.query()");
    }

    @Override
    public void initialize()
            throws IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.initialize()");
    }

    @Override
    public void clear()
            throws IOException {
        throw new UnsupportedOperationException("todo implement NoopStore.clear()");
    }

    @Override
    public boolean supports(Locatable locatable) {
        throw new UnsupportedOperationException("todo implement NoopStore.supports()");
    }

    @Override
    public boolean supports(Archetyped archetyped) {
        throw new UnsupportedOperationException("todo implement NoopStore.supports()");
    }

    @Override
    public void verifyStatus()
            throws StatusException {
        throw new UnsupportedOperationException("todo implement NoopStore.verifyStatus()");
    }

    @Override
    public String reportStatus()
            throws StatusException {
        throw new UnsupportedOperationException("todo implement NoopStore.reportStatus()");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("todo implement NoopStore.getName()");
    }

    @Override
    public boolean supportsTransactions() {
        throw new UnsupportedOperationException("todo implement NoopStore.supportsTransactions()");
    }

    @Override
    public void begin()
            throws TransactionException {
        throw new UnsupportedOperationException("todo implement NoopStore.begin()");
    }

    @Override
    public void commit()
            throws TransactionException {
        throw new UnsupportedOperationException("todo implement NoopStore.commit()");
    }

    @Override
    public void rollback()
            throws TransactionException {
        throw new UnsupportedOperationException("todo implement NoopStore.rollback()");
    }
}
