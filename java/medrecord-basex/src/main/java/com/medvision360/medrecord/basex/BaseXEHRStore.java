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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import com.google.common.base.Predicate;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.collect.Iterables;
import com.medvision360.medrecord.api.exceptions.DeletedException;
import com.medvision360.medrecord.api.exceptions.DisposalException;
import com.medvision360.medrecord.spi.DeletableEHR;
import com.medvision360.medrecord.spi.EHRParser;
import com.medvision360.medrecord.spi.EHRSerializer;
import com.medvision360.medrecord.spi.EHRStore;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import org.basex.core.Context;
import org.openehr.rm.ehr.EHR;
import org.openehr.rm.support.identification.HierObjectID;
import org.openehr.rm.support.identification.UIDBasedID;

import static com.google.common.base.Preconditions.checkNotNull;

public class BaseXEHRStore extends AbstractBaseXStore implements EHRStore
{
    private final Cache<HierObjectID, EHR> m_cache = CacheBuilder.newBuilder()
            .maximumSize(1000)
            .expireAfterAccess(10, TimeUnit.MINUTES)
            .build();
    private HierObjectID m_systemID;

    protected EHRParser m_parser;
    protected EHRSerializer m_serializer;

    public BaseXEHRStore(Context ctx, EHRParser parser, EHRSerializer serializer, HierObjectID systemID, String path)
    {
        super(ctx, systemID.getValue(), path);
        m_parser = checkNotNull(parser, "parser cannot be null");
        m_serializer = checkNotNull(serializer, "serializer cannot be null");
        m_systemID = checkNotNull(systemID, "systemID cannot be null");
    }

    @Override
    public void dispose() throws DisposalException
    {
        m_cache.invalidateAll();
        super.dispose();
    }

    @Override
    public void clear() throws IOException
    {
        super.clear();
        m_cache.invalidateAll();
    }

    @Override
    public HierObjectID getSystemID()
    {
        return m_systemID;
    }

    @Override
    public EHR get(HierObjectID id) throws NotFoundException, IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");

        EHR result = cachedGet(id);
        
        if (result instanceof DeletableEHR)
        {
            DeletableEHR deletableEHR = (DeletableEHR) result;
            if (deletableEHR.isDeleted())
            {
                throw new DeletedException(String.format(
                        "The EHR %s has been deleted", id), result);
            }
        }
        
        return result;
    }

    private EHR cachedGet(HierObjectID id) throws IOException, NotFoundException, ParseException
    {
        EHR result = m_cache.getIfPresent(id);
        if (result == null)
        {
            String path = fullPath(id);
            result = get(path, id);
        }
        return result;
    }

    @Override
    public EHR insert(EHR EHR) throws DuplicateException, NotSupportedException, IOException, SerializeException
    {
        checkNotNull(EHR, "EHR cannot be null");
        String path = fullPath(EHR);
        if (has(path))
        {
            throw duplicate(EHR);
        }
        EHR result = replace(EHR, path);
        return result;
    }

    @Override
    public void delete(HierObjectID id) throws NotFoundException, IOException, ParseException, SerializeException
    {
        checkNotNull(id, "id cannot be null");
        String path = fullPath(id);
        EHR EHR = get(path, id);
        
        if (!(EHR instanceof DeletableEHR))
        {
            throw new IllegalStateException("EHRStore configured with a parser that does not return DeletableEHR");
        }
        DeletableEHR deletableEHR = (DeletableEHR) EHR;
        if (deletableEHR.isDeleted())
        {
            return;
        }
        
        deletableEHR.setDeleted(true);
        replace(deletableEHR, path);
        m_cache.invalidate(id);
    }

    @Override
    public void undelete(HierObjectID id) throws NotFoundException, IOException, ParseException, SerializeException
    {
        checkNotNull(id, "id cannot be null");
        String path = fullPath(id);
        EHR EHR = get(path, id);
        
        if (!(EHR instanceof DeletableEHR))
        {
            return;
        }
        DeletableEHR deletableEHR = (DeletableEHR) EHR;
        if (!deletableEHR.isDeleted())
        {
            return;
        }
        
        deletableEHR.setDeleted(false);
        replace(deletableEHR, path);
        m_cache.invalidate(id);
    }

    @Override
    public boolean has(HierObjectID id) throws IOException, ParseException
    {
        checkNotNull(id, "id cannot be null");

        String path = fullPath(id);
        EHR result;
        try
        {
            result = get(path, id);
        }
        catch (NotFoundException e)
        {
            return false;
        }
        if (result instanceof DeletableEHR)
        {
            DeletableEHR deletableEHR = (DeletableEHR) result;
            if (deletableEHR.isDeleted())
            {
                return false;
            }
        }
        
        return true;
    }

    @Override
    public Iterable<HierObjectID> list() throws IOException
    {
        String path = fullPath("ehr");
        return list(path);
    }

    @Override
    public Iterable<HierObjectID> list(boolean excludeDeleted) throws IOException
    {
        Iterable<HierObjectID> list = list();

        if (excludeDeleted)
        {
            return Iterables.filter(list, new Predicate<HierObjectID>()
            {
                @Override
                public boolean apply(HierObjectID id)
                {
                    try
                    {
                        EHR ehr = cachedGet(id);
                        if (ehr == null)
                        {
                            return false;
                        }
                        if (ehr instanceof DeletableEHR)
                        {
                            if (((DeletableEHR) ehr).isDeleted())
                            {
                                return false;
                            }
                        }
                        return true;
                    }
                    catch (NotFoundException|IOException|ParseException e)
                    {
                        return false;
                    }
                }
            });
        }
        else
        {
            return list;
        }
    }

    ///
    /// Helpers
    ///

    @Override
    protected String path(UIDBasedID id)
    {
        return "ehr/" + hPath(id);
    }

    protected EHR get(String path, HierObjectID argument) throws IOException, NotFoundException, ParseException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        get(path, argument, os); // calls initialize()
        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        EHR EHR = m_parser.parse(is);
        m_cache.put(EHR.getEhrID(), EHR);
        return EHR;
    }

    protected EHR replace(EHR EHR, String path) throws IOException, SerializeException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        m_serializer.serialize(EHR, os);
        byte[] buffer = os.toByteArray();
        ByteArrayInputStream is = new ByteArrayInputStream(buffer);
        replace(path, is); // calls initialize()
        m_cache.put(EHR.getEhrID(), EHR);
        return EHR;
    }
}
