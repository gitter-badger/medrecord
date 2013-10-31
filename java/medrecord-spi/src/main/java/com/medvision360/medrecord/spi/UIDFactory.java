package com.medvision360.medrecord.spi;

import java.util.UUID;

import org.openehr.rm.support.identification.HierObjectID;

public class UIDFactory
{
    public HierObjectID makeUID()
    {
        return new HierObjectID(makeUUID());
    }

    public String makeUUID()
    {
        return UUID.randomUUID().toString();
    }
}
