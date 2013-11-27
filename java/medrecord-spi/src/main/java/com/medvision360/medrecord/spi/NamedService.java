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

/**
 * Simple interface to be implemented by services that have a name. No guarantees are made or expected as to service
 * name uniqueness within a service.
 */
public interface NamedService
{
    /**
     * Get the name of this service. Should be both machine- and human-readable.
     *
     * @return the name of this service.
     */
    public String getName();
}
