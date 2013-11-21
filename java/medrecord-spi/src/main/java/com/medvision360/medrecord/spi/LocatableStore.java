/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import java.io.IOException;

import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import com.medvision360.medrecord.api.exceptions.ValidationException;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;

/**
 * Central interface to a storage system for OpenEHR {@link Locatable} instances. Provides basic CRUD operations.
 * <p/>
 * Since storage facilities for {@link Locatable} implementations typically need to provide strong ACID guarantees, the
 * <code>LocatableStore</code> interface extends from {@link TransactionalService} and clients SHOULD always use
 * transaction semantics. However, basic/test/dummy/research implementations <em>only</em> may choose to ignore the
 * transactions and return true from {@link #supportsTransactions()}.
 * <p/>
 * Store implementations should be robust against transient un-availability of underlying storage mechanisms and provide
 * insight into storage mechanism availability to clients. To that end, they SHOULD properly implement the {@link
 * StatusService} API}.
 * <p/>
 * The kinds of {@link Locatable} sub-types that are supported may be limited by implementations. For example, a
 * particular store implementation may (be configured to) support only {@link org.openehr.rm.demographic.Party} or
 * {@link org.openehr.rm.composition.Composition} instances.
 * <p/>
 * Store implementations are typically <em>not</em> expected to implement consistency checks or validation checks on
 * Locatables beyond those checks that are needed to implement their functionality. For example, the openEHR reference
 * model <em>implies</em> that a store for demographic information maintain {@link
 * org.openehr.rm.demographic.PartyRelationship} links between {@link org.openehr.rm.demographic.Party} objects, but the
 * store is not required to implement or enforce that rule. Similarly, constraints defined in archetypes rather than in
 * the reference model itself are not expected to be implemented by stores.
 */
public interface LocatableStore extends LocatableSelector, TransactionalService, StatusService
{

    /**
     * Retrieve the latest version of the specified locatable.
     *
     * @param id the identifier of the locatable to retrieve.
     * @return the latest version of the specified locatable.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws NotFoundException if the locatable cannot be found in storage.
     * @throws IOException if another error occurs interacting with storage.
     */
    public Locatable get(HierObjectID id) throws NotFoundException, IOException, ParseException;

    /**
     * Store a new locatable.
     *
     * @param locatable the locatable to store.
     * @return the stored locatable with any modified attributes modified.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws IllegalArgumentException if the provided locatable does not properly implement the rules of the openehr
     * object model.
     * @throws NotSupportedException if the provided locatable (probably) does implement the rules of the openehr object
     * model, but cannot be stored for another reason.
     * @throws DuplicateException if a locatable already exists with the same uid.
     * @throws IOException if another error occurs interacting with storage.
     * @throws ValidationException if the provided locatable is not valid.
     */
    public Locatable insert(Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException, SerializeException, ValidationException;

    /**
     * Store a new locatable into an EHR.
     *
     * @param locatable the locatable to store.
     * @param EHR the EHR to store this locatable in.
     * @return the stored locatable with any modified attributes modified.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws IllegalArgumentException if the provided locatable does not properly implement the rules of the openehr
     * object model.
     * @throws NotSupportedException if the provided locatable (probably) does implement the rules of the openehr object
     * model, but cannot be stored for another reason.
     * @throws DuplicateException if a locatable already exists with the same uid.
     * @throws IOException if another error occurs interacting with storage.
     * @throws ValidationException if the provided locatable is not valid.
     */
    public Locatable insert(EHR EHR, Locatable locatable)
            throws DuplicateException, NotSupportedException, IOException, SerializeException, ValidationException;

    /**
     * Store a new version of an existing locatable.
     *
     * @param locatable the locatable to store
     * @return the stored locatable with any modified attributes modified.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws IllegalArgumentException if the provided locatable does not properly implement the rules of the openehr
     * object model.
     * @throws NotSupportedException if the provided locatable (probably) does implement the rules of the openehr object
     * model, but cannot be stored for another reason.
     * @throws NotFoundException if the locatable cannot be found in storage.
     * @throws IOException if another error occurs interacting with storage.
     * @throws ValidationException if the provided locatable is not valid.
     */
    public Locatable update(Locatable locatable)
            throws NotSupportedException, NotFoundException, IOException, SerializeException, ValidationException;

    /**
     * Mark all versions of a {@link Locatable} as deleted.
     *
     * @param id the locatable to mark as deleted.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws NotFoundException if the locatable cannot be found in storage.
     * @throws IOException if another error occurs interacting with storage.
     */
    public void delete(HierObjectID id) throws NotFoundException, IOException;

    /**
     * Determine whether there is any version of the specified locatable in this store.
     *
     * @param id the identifier of the locatable to check for.
     * @return true if the locatable is stored, false otherwise.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws IOException if another error occurs interacting with storage.
     */
    public boolean has(HierObjectID id) throws IOException;

    /**
     * Return a naturally ordered set of the latest versions of all the locatables that exist in this store. The
     * returned result is immutable. The returned result is empty if there are 0 results.
     *
     * @return an iterable of all locatables in this store. Can be empty.
     * @throws IOException if an error occurs interacting with storage.
     * @see com.google.common.collect.Iterables for convenient utilities to work with Iterables.
     */
    public Iterable<HierObjectID> list() throws IOException;
    
    /**
     * Return a naturally ordered set of the latest versions of all the locatables that exist in this store that 
     * belong to a particular EHR record. The returned result is immutable. The returned result is empty if there are
     * 0 results.
     *
     * @return an iterable of all locatables in this store that belong to a particular EHR record. Can be empty.
     * @throws IOException if an error occurs interacting with storage.
     * @see com.google.common.collect.Iterables for convenient utilities to work with Iterables.
     */
    public Iterable<HierObjectID> list(EHR EHR) throws IOException, NotFoundException;

    /**
     * Return a naturally ordered set of the latest versions of all the locatables that exist in this store that 
     * belong to a particular EHR record and are of a particular rmEntity. The returned result is immutable. The 
     * returned result is empty if there are 0 results.
     *
     * @return an iterable of all locatables in this store that belong to a particular EHR record and are of a 
     * particular rmEntity. Can be empty.
     * @throws IOException if an error occurs interacting with storage.
     * @see com.google.common.collect.Iterables for convenient utilities to work with Iterables.
     */
    public Iterable<HierObjectID> list(EHR EHR, String rmEntity) throws IOException, NotFoundException;

    /**
     * Prepares the store for operation. Ensure that any and all custom configuration of the underlying storage has been
     * taken care of. After this invocation completes successfully, the store should be ready to accept other calls.
     * Typically this will be called once on application startup, and regularly during (unit) tests.
     * <p/>
     * Whether instances of this class will function correctly if <code>initialize()</code> is not called is not
     * defined.
     *
     * @throws IOException if an error occurs interacting with storage.
     */
    public void initialize() throws IOException;

    /**
     * Removes all data from the store. Structure and index configuration may remain This is an operation typically only
     * invoked during debugging/testing, not during normal operation. Calling clear() on an empty store has no effect.
     * After <code>clear()</code> has been called, {@link #initialize()} should be called before this store can be used
     * again.
     *
     * @throws IOException if an error occurs interacting with storage.
     */
    public void clear() throws IOException;
}
