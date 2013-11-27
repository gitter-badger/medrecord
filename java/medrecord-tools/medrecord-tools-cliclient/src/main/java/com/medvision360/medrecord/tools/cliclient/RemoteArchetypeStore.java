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
package com.medvision360.medrecord.tools.cliclient;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.medvision360.lib.client.ClientResourceConfig;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.archetype.ArchetypeRequest;
import com.medvision360.medrecord.api.archetype.ArchetypeResult;
import com.medvision360.medrecord.api.exceptions.DisposalException;
import com.medvision360.medrecord.api.exceptions.DuplicateException;
import com.medvision360.medrecord.api.exceptions.InUseException;
import com.medvision360.medrecord.api.exceptions.NotFoundException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.exceptions.SerializeException;
import com.medvision360.medrecord.api.exceptions.StatusException;
import com.medvision360.medrecord.api.exceptions.TransactionException;
import com.medvision360.medrecord.client.archetype.ArchetypeListResource;
import com.medvision360.medrecord.client.archetype.ArchetypeResource;
import com.medvision360.medrecord.riio.RIAdlConverter;
import com.medvision360.medrecord.spi.ArchetypeParser;
import com.medvision360.medrecord.spi.ArchetypeSerializer;
import com.medvision360.medrecord.spi.ArchetypeStore;
import com.medvision360.medrecord.spi.WrappedArchetype;
import org.apache.commons.io.IOUtils;
import org.openehr.am.archetype.Archetype;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.support.identification.ArchetypeID;

import static com.google.common.base.Preconditions.checkNotNull;

public class RemoteArchetypeStore implements ArchetypeStore
{
    private String m_name;
    private ClientResourceConfig m_resourceConfig;
    private ArchetypeListResource m_archetypeListResource;
    private ArchetypeParser m_archetypeParser;
    private ArchetypeSerializer m_archetypeSerializer;

    public RemoteArchetypeStore(String name, String baseUrl)
    {
        m_name = checkNotNull(name);
        m_resourceConfig = new ClientResourceConfig(
               checkNotNull(baseUrl) + "/v2"
           );
        m_archetypeListResource = new ArchetypeListResource(m_resourceConfig);
        RIAdlConverter converter = new RIAdlConverter(true, true);
        m_archetypeParser = converter;
        m_archetypeSerializer = converter;
    }

    @Override
    public WrappedArchetype get(Archetyped archetypeDetails) throws NotFoundException, IOException, ParseException
    {
        return get(archetypeDetails.getArchetypeId());
    }

    @Override
    public WrappedArchetype get(ArchetypeID archetypeID) throws NotFoundException, IOException, ParseException
    {
        String archetypeIdString = archetypeID.getValue();
        ArchetypeResource resource = resource(archetypeIdString);
        String archetypeAsString;
        try
        {
            ArchetypeResult result = resource.getArchetype();
            archetypeAsString = result.getAdl();
        }
        catch (RecordException e)
        {
            if (e instanceof NotFoundException)
            {
                throw (NotFoundException)e;
            }
            if (e instanceof ParseException)
            {
                throw (ParseException)e;
            }
            throw new IOException(e);
        }
        WrappedArchetype archetype = m_archetypeParser.parse(IOUtils.toInputStream(archetypeAsString, "UTF-8"));
        return archetype;
    }

    @Override
    public boolean has(Archetyped archetypeDetails) throws IOException
    {
        return has(archetypeDetails.getArchetypeId());
    }

    @Override
    public boolean has(ArchetypeID archetypeID) throws IOException
    {
        String archetypeIdString = archetypeID.getValue();
        ArchetypeResource resource = resource(archetypeIdString);
        try
        {
            resource.getArchetype();
        }
        catch (RecordException e)
        {
            if (e instanceof NotFoundException)
            {
                return false;
            }
            throw new IOException(e);
        }
        return true;
    }

    @Override
    public WrappedArchetype insert(WrappedArchetype archetype)
            throws DuplicateException, IOException, SerializeException
    {
        String archetypeAsString = archetype.getAsString();
        ArchetypeRequest request = new ArchetypeRequest();
        request.setAdl(archetypeAsString);
        try
        {
            m_archetypeListResource.postArchetype(request);
            return archetype;
        }
        catch (RecordException e)
        {
            if (e instanceof DuplicateException)
            {
                throw (DuplicateException)e;
            }
            if (e instanceof SerializeException)
            {
                throw (SerializeException)e;
            }
            throw new IOException(e);
        }
    }

    @Override
    public WrappedArchetype insert(Archetype archetype) throws DuplicateException, IOException, SerializeException
    {
        WrappedArchetype wrapped = new WrappedArchetype(null, archetype);
        wrapped = m_archetypeSerializer.serialize(wrapped, new OutputStream()
        {
            @Override
            public void write(int b) {}
        });
        return insert(wrapped);
    }

    @Override
    public void delete(ArchetypeID archetypeID) throws InUseException, IOException, NotFoundException, ParseException
    {
        String archetypeIdString = archetypeID.getValue();
        ArchetypeResource resource = resource(archetypeIdString);
        try
        {
            resource.deleteArchetype();
        }
        catch (RecordException e)
        {
            if (e instanceof InUseException)
            {
                throw (InUseException)e;
            }
            if (e instanceof NotFoundException)
            {
                throw (NotFoundException)e;
            }
            if (e instanceof ParseException)
            {
                throw (ParseException)e;
            }
            throw new IOException(e);
        }
    }

    @Override
    public void lock(ArchetypeID archetypeID) throws NotFoundException, IOException, ParseException
    {
        throw new UnsupportedOperationException("RemoteArchetypeStore does not support lock()");
    }

    @Override
    public Iterable<ArchetypeID> list() throws IOException
    {
        try
        {
            IDList list = m_archetypeListResource.listArchetypes();
            List<String> ids = list.getIds();
            Iterable<ArchetypeID> result = Iterables.transform(ids, new Function<String, ArchetypeID>()
            {
                @Override
                public ArchetypeID apply(String input)
                {
                    return new ArchetypeID(input);
                }
            });
            return result;
        }
        catch (RecordException e)
        {
            throw new IOException(e);
        }
    }

    @Override
    public void initialize() throws IOException
    {
    }

    @Override
    public void dispose() throws DisposalException
    {
    }

    @Override
    public void clear() throws IOException
    {
        throw new UnsupportedOperationException("RemoteArchetypeStore does not support clear()");
    }

    @Override
    public void verifyStatus() throws StatusException
    {
    }

    @Override
    public String reportStatus() throws StatusException
    {
        return "OK";
    }

    @Override
    public String getName()
    {
        return m_name;
    }

    @Override
    public boolean supportsTransactions()
    {
        return false;
    }

    @Override
    public void begin() throws TransactionException
    {
    }

    @Override
    public void commit() throws TransactionException
    {
    }

    @Override
    public void rollback() throws TransactionException
    {
    }

    private ArchetypeResource resource(String id)
    {
        return new ArchetypeResource(m_resourceConfig, id);
    }
}
