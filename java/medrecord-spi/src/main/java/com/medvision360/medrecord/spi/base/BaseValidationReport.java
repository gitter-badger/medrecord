package com.medvision360.medrecord.spi.base;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;
import com.medvision360.medrecord.spi.ValidationReport;
import com.medvision360.medrecord.spi.ValidationResult;

import static com.google.common.base.Preconditions.checkNotNull;

public class BaseValidationReport implements ValidationReport
{
    private List<ValidationResult> m_list = new ArrayList<>();
    
    public void add(ValidationResult result)
    {
        checkNotNull(result, "result cannot be null");
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
}
