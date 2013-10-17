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
