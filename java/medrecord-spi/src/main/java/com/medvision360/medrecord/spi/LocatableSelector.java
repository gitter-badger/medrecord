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

import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

/**
 * Capability-negotiation interface that defines whether a component supports a particular locatable, or not.
 */
public interface LocatableSelector
{
    /**
     * Checks whether this service knows how to deal with this particular locatable.
     *
     * @param test the locatable to check for support.
     * @return true if this locatable is (most likely) supported by this service, false otherwise.
     * @throws NullPointerException if any of the provided arguments are null.
     */
    public boolean supports(Locatable test);

    /**
     * Checks whether this service knows how to deal with locatables of this particular type.
     *
     * @param test the type to check for support.
     * @return true if this type of locatable is (most likely) supported by this service, false otherwise.
     * @throws NullPointerException if any of the provided arguments are null.
     */
    public boolean supports(Archetyped test);
}
