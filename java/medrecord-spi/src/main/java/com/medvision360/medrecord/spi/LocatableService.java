package com.medvision360.medrecord.spi;

import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

/**
 * Basic interface implemented by all services that operate on Locatables.
 */
public interface LocatableService {
    /**
     * Checks whether this service knows how to deal with this particular locatable.
     * 
     * @param locatable the locatable to check for support.
     * @return true if this locatable is (most likely) supported by this service, false otherwise.
     * @throws NullPointerException if any of the provided arguments are null.
     */
    public boolean supports(Locatable locatable);

    /**
     * Checks whether this service knows how to deal with locatables of this particular type.
     * 
     * @param archetyped the type to check for support.
     * @return true if this type of locatable is (most likely) supported by this service, false otherwise.
     * @throws NullPointerException if any of the provided arguments are null.
     */
    public boolean supports(Archetyped archetyped);
}
