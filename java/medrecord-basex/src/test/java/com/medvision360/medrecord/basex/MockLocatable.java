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
package com.medvision360.medrecord.basex;

import java.util.List;
import java.util.Set;

import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.FeederAudit;
import org.openehr.rm.common.archetyped.Link;
import org.openehr.rm.common.archetyped.Locatable;
import org.openehr.rm.common.archetyped.Pathable;
import org.openehr.rm.datatypes.text.DvText;
import org.openehr.rm.support.identification.UIDBasedID;

class MockLocatable extends Locatable
{
    private static final long serialVersionUID = 0x130L;

    MockLocatable(UIDBasedID uid, String archetypeNodeId,
            DvText name, Archetyped archetypeDetails,
            FeederAudit feederAudit,
            Set<Link> links, Pathable parent)
    {
        super(uid, archetypeNodeId, name, archetypeDetails, feederAudit, links, parent);
    }

    @Override
    public String pathOfItem(Pathable pathable)
    {
        throw new UnsupportedOperationException("todo implement .pathOfItem()");
    }

    @Override
    public List<Object> itemsAtPath(String s)
    {
        throw new UnsupportedOperationException("todo implement .itemsAtPath()");
    }

    @Override
    public boolean pathExists(String s)
    {
        throw new UnsupportedOperationException("todo implement .pathExists()");
    }

    @Override
    public boolean pathUnique(String s)
    {
        throw new UnsupportedOperationException("todo implement .pathUnique()");
    }
}
