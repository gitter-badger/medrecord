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
package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.RuntimeRecordException;
import org.openehr.rm.Attribute;
import org.openehr.rm.FullConstructor;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectRef;

/**
 * An EHR record backed by a store.
 */
public class StoredEHR extends DeletableEHR
{
    private static final long serialVersionUID = 0x130L;
    private final static ArrayList<ObjectRef> EMPTY_LIST = new ArrayList<>();
    
    private LocatableStore m_compositionStore;
    
    @FullConstructor
    public StoredEHR(
            @Attribute(name = "ehrStore", required = true) EHRStore ehrStore,
            @Attribute(name = "compositionStore", required = true) LocatableStore compositionStore,
            @Attribute(name = "ehrID", required = true) HierObjectID ehrID,
            @Attribute(name = "timeCreated", required = true) DvDateTime timeCreated,
            @Attribute(name = "contributions", required = true) List<ObjectRef> contributions,
            @Attribute(name = "ehrStatus", required = true) ObjectRef ehrStatus,
            @Attribute(name = "directory") ObjectRef directory,
            @Attribute(name = "compositions", required = true) List<ObjectRef> compositions,
            @Attribute(name = "deleted", required = true) boolean deleted)
    {
        super(ehrStore.getSystemID(), ehrID, timeCreated, contributions, ehrStatus, directory, compositions, deleted);
        m_compositionStore = compositionStore;
    }

    public StoredEHR(
            EHRStore ehrStore,
            LocatableStore compositionStore,
            HierObjectID ehrID,
            ObjectRef ehrStatus)
    {
        this(ehrStore, compositionStore, ehrID, new DvDateTime(), EMPTY_LIST, ehrStatus, null, EMPTY_LIST, false);
    }

    public StoredEHR(
            EHRStore ehrStore,
            LocatableStore compositionStore,
            UIDFactory uidFactory,
            ObjectRef ehrStatus)
    {
        this(ehrStore, compositionStore, uidFactory.makeUID(), ehrStatus);
    }

    @Override
    public List<ObjectRef> getContributions()
    {
        return new ArrayList<>();
    }

    @Override
    public List<ObjectRef> getCompositions()
    {
        try
        {
            List<ObjectRef> result = new ArrayList<>();
            Iterable<HierObjectID> list = m_compositionStore.list(this, "COMPOSITION");
            for (HierObjectID id : list)
            {
                result.add(new ObjectRef(id, "LOCAL", "COMPOSITION"));
            }
            return result;
        }
        catch (IOException|RecordException e)
        {
            throw new RuntimeRecordException(e);
        }
    }
}
