package com.medrecord.spi;

import com.medrecord.spi.LocatableValidator;
import com.medrecord.spi.ValidationReport;
import com.medrecord.spi.exceptions.NotSupportedException;
import com.medrecord.spi.exceptions.ValidationException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

import java.util.ArrayList;
import java.util.List;

public class CompositeValidator implements LocatableValidator {
    private List<LocatableValidator> delegates = new ArrayList<>();
    
    public void addDelegate(LocatableValidator delegate) {
        delegates.add(delegate);
    }

    @Override
    public ValidationReport validate(Locatable locatable)
            throws NotSupportedException {
        ValidationReport result = null;
        for (LocatableValidator delegate : delegates) {
            if (delegate.supports(locatable)) {
                result = mergeReports(result, delegate.validate(locatable));
            }
        }
        if (result == null) {
            throw new NotSupportedException("None of the delegates support this locatable");
        }
        return result;
    }

    private ValidationReport mergeReports(ValidationReport result, ValidationReport validate) {
        throw new UnsupportedOperationException("todo: implement CompositeValidator.mergeReports()"); // todo
    }

    @Override
    public void check(Locatable locatable)
            throws ValidationException, NotSupportedException {
        ValidationReport report = validate(locatable);
        if (!report.isValid()) {
            throw new ValidationException(report);
        }
    }

    @Override
    public boolean supports(Locatable locatable) {
        for (LocatableValidator delegate : delegates) {
            if (delegate.supports(locatable)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supports(Archetyped archetyped) {
        for (LocatableValidator delegate : delegates) {
            if (delegate.supports(archetyped)) {
                return true;
            }
        }
        return false;
    }
}
