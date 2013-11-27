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

import java.util.ArrayList;
import java.util.List;

import com.medvision360.medrecord.api.exceptions.TransformException;
import org.openehr.rm.common.archetyped.Locatable;

import static com.google.common.base.Preconditions.checkNotNull;

public class CompositeTransformer implements CompositeService<LocatableTransformer>, LocatableTransformer
{
    private List<LocatableTransformer> m_delegates = new ArrayList<>();
    
    @Override
    public void addDelegate(LocatableTransformer delegate)
    {
        m_delegates.add(checkNotNull(delegate, "delegate cannot be null"));
    }

    @Override
    public void transform(Locatable locatable) throws TransformException
    {
        for (LocatableTransformer delegate : m_delegates)
        {
            delegate.transform(locatable);
        }
    }
}
