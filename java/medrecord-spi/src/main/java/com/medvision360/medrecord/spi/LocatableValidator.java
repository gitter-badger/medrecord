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

import com.medvision360.medrecord.api.ValidationReport;
import com.medvision360.medrecord.api.exceptions.NotSupportedException;
import com.medvision360.medrecord.api.exceptions.ValidationException;
import org.openehr.rm.common.archetyped.Locatable;

/**
 * Central interface to a validation service that applies validation rules to a particular locatable and produces a
 * report based on those rules. There may be many different kinds of validators that are applicable to a particular
 * locatable. For example, some validators may check just some reference model constraints, some other validators may
 * check referential integrity constraints, and yet other validators may check validity against (an) archetype(s).
 */
public interface LocatableValidator extends LocatableSelector
{

    /**
     * Determine whether the specified locatable is valid according to the rules implemented by this validator.
     *
     * @param locatable the locatable to check the validation rules for
     * @return a report of the validity rules
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws NotSupportedException if the rules this validator implements are not applicable to the provided
     * locatable.
     */
    public ValidationReport validate(Locatable locatable) throws NotSupportedException;

    /**
     * Make sure the specified locatable is valid according to the rules implemented by this validator.
     *
     * @param locatable the locatable to check the validation rules for
     * @throws ValidationException if the locatable is not valid according to the rules implemented by this validator.
     * @throws NullPointerException if any of the provided arguments are null.
     * @throws NotSupportedException if the rules this validator implements are not applicable to the provided
     * locatable.
     */
    public void check(Locatable locatable) throws ValidationException, NotSupportedException;
}
