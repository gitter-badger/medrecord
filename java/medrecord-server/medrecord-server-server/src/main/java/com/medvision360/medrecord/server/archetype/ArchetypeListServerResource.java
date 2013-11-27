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
package com.medvision360.medrecord.server.archetype;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.archetype.ArchetypeListResource;
import com.medvision360.medrecord.api.archetype.ArchetypeRequest;
import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.PatternException;
import com.medvision360.medrecord.engine.MedRecordEngine;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.medrecord.spi.ArchetypeParser;
import com.medvision360.medrecord.spi.WrappedArchetype;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.MissingParameterException;
import com.medvision360.medrecord.api.exceptions.ParseException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import org.apache.commons.io.IOUtils;
import org.openehr.rm.support.identification.ArchetypeID;

public class ArchetypeListServerResource
        extends AbstractServerResource
        implements ArchetypeListResource
{
    @Override
    public void postArchetype(ArchetypeRequest archetype) throws RecordException
    {
        String adl = archetype.getAdl();
        postArchetypeAsText(adl);
    }

    @Override
    public void postArchetypeAsText(String adl) throws RecordException
    {
        try
        {
            if (adl == null || adl.isEmpty())
            {
                throw new MissingParameterException("Provide a non-empty ADL containing the archetype definition");
            }
            
            MedRecordEngine engine = engine();
            ArchetypeParser parser = engine.getArchetypeParser("text/plain", "adl");
            WrappedArchetype archetype;
            try
            {
                archetype = parser.parse(IOUtils.toInputStream(adl, "UTF-8"));
            }
            catch (ParseException e)
            {
                Throwable root = e;
                int limit = 10, i = 0;
                while (e.getCause() != null && i < limit)
                {
                    root = e.getCause();
                    i++;
                }
                throw new ClientParseException(root.getMessage(), e);
            }
            engine.getArchetypeStore().insert(archetype);
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    @Override
    public IDList listArchetypes() throws RecordException
    {
        try
        {
            Pattern pattern = null;
            String q = getQueryValue("q");
            if (q != null && !q.isEmpty())
            {
                if (!q.startsWith("^"))
                {
                    q = "^.*?" + q;
                }
                if (!q.endsWith("$"))
                {
                    q += ".*?$";
                }
                try
                {
                    pattern = Pattern.compile(q);
                }
                catch (PatternSyntaxException e)
                {
                    throw new PatternException(e.getMessage());
                }
            }
            
            Iterable<ArchetypeID> idList = engine().getArchetypeStore().list();
            Iterable<String> stringList = Iterables.transform(idList, new Function<ArchetypeID,String>() {
                @Override
                public String apply(ArchetypeID input)
                {
                    if (input == null)
                    {
                        return null;
                    }
                    return input.getValue();
                }
            });
            if (pattern != null)
            {
                final Pattern finalPattern = pattern;
                stringList = Iterables.filter(stringList, new Predicate<String>()
                {
                    @Override
                    public boolean apply(String input)
                    {
                        return finalPattern.matcher(input).matches();
                    }
                });
            }
            
            IDList result = new IDList();
            result.setIds(Lists.newArrayList(stringList));
            return result;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }
}
