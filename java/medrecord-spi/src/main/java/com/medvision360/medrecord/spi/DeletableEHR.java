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

import java.util.List;

import org.openehr.rm.Attribute;
import org.openehr.rm.FullConstructor;
import org.openehr.rm.datatypes.quantity.datetime.DvDateTime;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.ObjectRef;

public class DeletableEHR extends EHR implements SoftDeletable
{
    private static final long serialVersionUID = 0x130L;
    
    private boolean m_deleted;
    
    public DeletableEHR(
            HierObjectID systemID,
            HierObjectID ehrID,
            DvDateTime timeCreated,
            List<ObjectRef> contributions,
            ObjectRef ehrStatus,
            ObjectRef directory,
            List<ObjectRef> compositions)
    {
        this(systemID, ehrID, timeCreated, contributions, ehrStatus, directory, compositions, false);
    }

    @FullConstructor
    public DeletableEHR(
            @Attribute(name = "systemID", required = true) HierObjectID systemID,
            @Attribute(name = "ehrID", required = true) HierObjectID ehrID,
            @Attribute(name = "timeCreated", required = true) DvDateTime timeCreated,
            @Attribute(name = "contributions", required = true) List<ObjectRef> contributions,
            @Attribute(name = "ehrStatus", required = true) ObjectRef ehrStatus,
            @Attribute(name = "directory") ObjectRef directory,
            @Attribute(name = "compositions", required = true) List<ObjectRef> compositions,
            @Attribute(name = "deleted", required = true) boolean deleted)
    {
        super(systemID, ehrID, timeCreated, contributions, ehrStatus, directory, compositions);
        m_deleted = deleted;
    }

    public boolean isDeleted()
    {
        return m_deleted;
    }

    public void setDeleted(boolean deleted)
    {
        m_deleted = deleted;
    }
}
