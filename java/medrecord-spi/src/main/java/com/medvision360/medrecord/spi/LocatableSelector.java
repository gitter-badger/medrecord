/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
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
