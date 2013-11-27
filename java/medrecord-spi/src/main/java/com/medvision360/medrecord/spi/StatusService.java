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

import com.medvision360.medrecord.api.exceptions.StatusException;

/**
 * Basic/generic interface to be implemented by components that may be transiently non-functioning and are capable of
 * informing their clients of their status.
 */
public interface StatusService extends NamedService
{
    /**
     * Check that this service is operating properly.
     *
     * @throws StatusException if there is a problem with service operation.
     */
    public void verifyStatus() throws StatusException;

    /**
     * Ask this service for a human-readable report on its current status. If the service is not functioning properly,
     * will throw a StatusException rather than returning a report.
     *
     * @return a human-readable report on the service its current status.
     * @throws StatusException if there is a problem with service operation.
     */
    public String reportStatus() throws StatusException;
}
