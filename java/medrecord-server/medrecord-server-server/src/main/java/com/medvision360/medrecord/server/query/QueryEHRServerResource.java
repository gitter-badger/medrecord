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
package com.medvision360.medrecord.server.query;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.google.common.collect.Iterables;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InvalidDateTimeException;
import com.medvision360.medrecord.api.exceptions.InvalidRangeException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.query.QueryEHRResource;
import com.medvision360.medrecord.server.AbstractServerResource;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.openehr.rm.support.identification.HierObjectID;
import org.restlet.data.Form;

public class QueryEHRServerResource
        extends AbstractServerResource
        implements QueryEHRResource
{
    @Override
    public IDList ehrQuery() throws RecordException // todo efficient/fully indexed ehrQuery()
    {
        String excludeDeletedString = getQueryValue("excludeDeleted");
        boolean hasDeleted = excludeDeletedString != null;
        boolean excludeDeleted = hasDeleted && "true".equals(excludeDeletedString);
        String excludeEmptyString = getQueryValue("excludeEmpty");
        boolean excludeEmpty = "true".equals(excludeEmptyString);
        List<String> systemIDList = getQueryValues("systemID");
        boolean hasSystemID = systemIDList.size() > 0;
        String subjectString = getQueryValue("subject");
        boolean hasSubject = subjectString != null;
        DateTime createdBefore = getDateTimeQueryValue("createdBefore");
        boolean hasCreatedBefore = createdBefore != null;
        DateTime createdAfter = getDateTimeQueryValue("createdAfter");
        boolean hasCreatedAfter = createdAfter != null;
        
        if (hasCreatedBefore && hasCreatedAfter && createdBefore.isBefore(createdAfter))
        {
            throw new InvalidRangeException(String.format(
                    "createdBefore %s < createdAfter %s, cannot ever match anything",
                    createdBefore.toString(),
                    createdAfter.toString()));
        }

        try
        {
            Iterable<HierObjectID> list;
            if (hasDeleted)
            {
                list = engine().getEHRStore().list(excludeDeleted);
            }
            else
            {
                list = engine().getEHRStore().list();
            }
            
            // filters organized by guess of which filter is cheaper
            
            // note this assumes store implementations do some caching (like BaseXEHRStore does),
            // otherwise, this kind of code could get pretty expensive in terms of # of database queries

            if (hasSubject || hasCreatedBefore || hasCreatedAfter || hasSystemID)
            {
                list = Iterables.filter(list, new EHRPropertyPredicate(
                        engine(), subjectString, createdBefore, createdAfter, systemIDList));
            }
            if (excludeEmpty)
            {
                list = Iterables.filter(list, new NonEmptyEHRPredicate(engine()));
            }
            
            IDList result = toIdList(list);
            return result;
        }
        catch (IOException e)
        {
            throw new IORecordException(e.getMessage(), e);
        }
    }

    protected List<String> getQueryValues(String name)
    {
        Form query = getQuery();
        String[] valuesArray = query.getValuesArray(name);
        return Arrays.asList(valuesArray);
    }
    
    protected DateTime getDateTimeQueryValue(String name) throws InvalidDateTimeException
    {
        String valueString = getQueryValue(name);
        if (valueString == null)
        {
            return null;
        }
        try
        {
            DateTime parsed = ISODateTimeFormat.dateTimeParser()
                    .withZoneUTC().withOffsetParsed().parseDateTime(valueString);
            return parsed;
        }
        catch (IllegalArgumentException e)
        {
            throw new InvalidDateTimeException(e.getMessage(), e);
        }
        
    }

}
