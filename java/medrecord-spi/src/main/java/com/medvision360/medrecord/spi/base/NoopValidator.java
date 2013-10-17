/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi.base;

import com.medvision360.medrecord.spi.LocatableValidator;
import com.medvision360.medrecord.spi.ValidationReport;
import com.medvision360.medrecord.spi.exceptions.NotSupportedException;
import com.medvision360.medrecord.spi.exceptions.ValidationException;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

public class NoopValidator implements LocatableValidator {
    @Override
    public ValidationReport validate(Locatable locatable)
            throws NotSupportedException {
        throw new UnsupportedOperationException("todo implement NoopValidator.validate()");
    }

    @Override
    public void check(Locatable locatable)
            throws ValidationException, NotSupportedException {
        throw new UnsupportedOperationException("todo implement NoopValidator.check()");
    }

    @Override
    public boolean supports(Locatable test) {
        throw new UnsupportedOperationException("todo implement NoopValidator.supports()");
    }

    @Override
    public boolean supports(Archetyped test) {
        throw new UnsupportedOperationException("todo implement NoopValidator.supports()");
    }
}
