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

import org.openehr.am.archetype.Archetype;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Wrapper around {@link Archetype} that can preserve a textual representation.
 * 
 * A round trip of adl parsing/serializing results in randomly-ordered constraints in the output, 
 * and it may lose some formatting/comments/etc. Therefore, it's important that authored ADL text is kept 
 * verbatim. 
 */
public class WrappedArchetype
{
    private String m_asString;
    private Archetype m_archetype;
    private boolean m_locked;

    public WrappedArchetype(String asString, Archetype archetype)
    {
        this(asString, archetype, false);
    }

    public WrappedArchetype(String asString, Archetype archetype, boolean locked)
    {
        m_asString = asString;
        m_archetype = checkNotNull(archetype, "archetype cannot be null");
        m_locked = locked;
    }

    public String getAsString()
    {
        return m_asString;
    }

    public Archetype getArchetype()
    {
        return m_archetype;
    }

    public boolean isLocked()
    {
        return m_locked;
    }
}
