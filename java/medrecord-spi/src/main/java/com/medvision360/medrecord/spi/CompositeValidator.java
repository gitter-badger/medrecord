package com.medvision360.medrecord.spi;

import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ValidationException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class CompositeValidator implements LocatableValidator, CompositeService<LocatableValidator> {
    private List<LocatableValidator> delegates = new LinkedList<>();
    
    public void addDelegate(LocatableValidator delegate) {
        delegates.add(checkNotNull(delegate, "delegate cannot be null"));
    }

    @Override
    public ValidationReport validate(Locatable locatable)
            throws NotSupportedException {
        checkNotNull(locatable, "locatable cannot be null");
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
        checkNotNull(locatable, "locatable cannot be null");
        ValidationReport report = validate(locatable);
        if (!report.isValid()) {
            throw new ValidationException(report);
        }
    }

    @Override
    public boolean supports(Locatable locatable) {
        checkNotNull(locatable, "locatable cannot be null");
        for (LocatableValidator delegate : delegates) {
            if (delegate.supports(locatable)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean supports(Archetyped archetyped) {
        checkNotNull(archetyped, "archetyped cannot be null");
        for (LocatableValidator delegate : delegates) {
            if (delegate.supports(archetyped)) {
                return true;
            }
        }
        return false;
    }
}
