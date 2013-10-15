package com.medvision360.medrecord.spi;

import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.NotFoundException;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.changecontrol.VersionedObject;
import org.openehr.rm.support.identification.HierObjectID;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Service that implements full openEHR versioning. The OpenEHR reference model defines a versioning model for some 
 * Locatables, for example {@link org.openehr.rm.ehr.VersionedComposition},
 * {@link org.openehr.rm.demographic.VersionedParty}, and {@link org.openehr.rm.common.directory.VersionedFolder}. 
 * This class extends {@link LocatableStore} with explicit support for such versioned objects.
 * 
 * Versioning store implementations SHOULD also implement {@link AuditedService} so that they can write audit entries
 * for all API invocations, not just those that provide {@link VersionedObject}. If they do so, 
 * the audit information provided within {@link VersionedObject} MUST always override the audit information provided 
 * in {@link AuditedService}. 
 */
public interface VersioningStore extends LocatableStore {
    
    /**
     * Retrieve all version objects of the specified locatable.
     * 
     * @param id the identifier of the locatable to retrieve all version objects for.
     * @return an iterable of all version objects of all locatables in this store. Cannot be empty.
     * @throws NotFoundException if the locatable cannot be found in storage.
     * @throws IOException if another error occurs interacting with storage.
     * @see com.google.common.collect.Iterables for convenient utilities to work with Iterables.
     */
    public Iterable<VersionedObject<? extends Locatable>> getVersionObjects(HierObjectID id) throws NotFoundException, 
            IOException;

    /**
     * Store a new locatable with version information.
     * 
     * @param versionedLocatable the locatable (with version information) to store.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws IllegalArgumentException if the provided locatable does not properly implement the rules of the 
     *   openehr object model.
     * @throws NotSupportedException if the provided locatable (probably) does implement the rules of the openehr
     *   object model, but cannot be stored for another reason.
     * @throws DuplicateException if a locatable already exists with the same uid.
     * @throws IOException if another error occurs interacting with storage.
     */
    public void insert(VersionedObject<? extends Locatable> versionedLocatable) throws DuplicateException, 
            NotSupportedException, IOException;

    /**
     * Store a new version of an existing locatable with version information.
     * 
     * @param versionedLocatable the locatable (with version information) to store.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws IllegalArgumentException if the provided locatable does not properly implement the rules of the 
     *   openehr object model.
     * @throws NotSupportedException if the provided locatable (probably) does implement the rules of the openehr
     *   object model, but cannot be stored for another reason.
     * @throws NotFoundException if the locatable cannot be found in storage.
     * @throws IOException if another error occurs interacting with storage.
     */
    public void update(VersionedObject<? extends Locatable> versionedLocatable) throws NotSupportedException, 
            NotFoundException, IOException;

    public Iterable<VersionedObject<? extends Locatable>> listVersionObjects(String XQuery) throws 
            NotSupportedException, IOException, UnsupportedOperationException;
    
    public void queryVersionObjects(String XQuery, OutputStream os) throws NotSupportedException, IOException,
            UnsupportedOperationException;
    
}
