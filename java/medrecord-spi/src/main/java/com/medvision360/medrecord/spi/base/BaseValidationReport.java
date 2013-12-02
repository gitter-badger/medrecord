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
package com.medvision360.medrecord.spi.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.medvision360.medrecord.api.ValidationReport;
import com.medvision360.medrecord.api.ValidationResult;

import static com.google.common.base.Preconditions.checkNotNull;

public class BaseValidationReport implements ValidationReport
{
    private List<ValidationResult> m_list = new ArrayList<>();
    
    public void add(ValidationResult result)
    {
        checkNotNull(result, "result cannot be null");
        m_list.add(result);
    }
    
    public BaseValidationResult newResult(String path)
    {
        BaseValidationResult result = new BaseValidationResult(path);
        add(result);
        return result;
    }
    
    @Override
    public boolean isValid()
    {
        for (ValidationResult result : m_list)
        {
            if (!result.isValid())
            {
                return false;
            }
        }
        return true;
    }

    @Override
    public Iterable<ValidationResult> getReport()
    {
        return Collections.unmodifiableList(m_list);
    }

    @Override
    public Iterable<ValidationResult> getErrors()
    {
        return Iterables.filter(getReport(), new Predicate<ValidationResult>()
        {
            @Override
            public boolean apply(ValidationResult input)
            {
                return !input.isValid();
            }
        });
    }
    
    @Override
    public String toString()
    {
        StringBuilder str = new StringBuilder();
        str.append("ValidationReport:");
        str.append(isValid() ? "valid" : "invalid");
        int total = m_list.size();
        Iterable<ValidationResult> errors = getErrors();
        int inValid = Iterables.size(errors);
        str.append(",total=").append(total);
        str.append(",valid=").append(total-inValid);
        str.append(",invalid=").append(inValid);
        str.append(",\n");
        for (ValidationResult result : errors)
        {
            str.append("  ");
            str.append(result);
            str.append(",\n");
        }
        return str.toString();
    }
    
    public void addAll(ValidationReport report)
    {
        if (report == null)
        {
            return;
        }
        Iterable<ValidationResult> results = report.getReport();
        for (ValidationResult result : results)
        {
            if (!m_list.contains(result))
            {
                m_list.add(result);
            }
        }
    }
}
