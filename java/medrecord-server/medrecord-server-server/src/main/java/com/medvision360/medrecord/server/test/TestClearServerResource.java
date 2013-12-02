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
package com.medvision360.medrecord.server.test;

import java.io.IOException;

import com.medvision360.medrecord.api.exceptions.AnnotatedIllegalArgumentException;
import com.medvision360.medrecord.api.exceptions.IORecordException;
import com.medvision360.medrecord.api.exceptions.InitializationException;
import com.medvision360.medrecord.api.exceptions.MissingParameterException;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.api.test.TestClearResource;
import com.medvision360.medrecord.server.AbstractServerResource;
import com.medvision360.wslog.Events;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestClearServerResource
        extends AbstractServerResource
        implements TestClearResource
{
    private final static Logger log = LoggerFactory.getLogger(TestClearServerResource.class);

    @Override
    public void clear() throws RecordException
    {
        String confirmation = null;
        try
        {
            confirmation = getRequiredQueryValue("confirm");
            if (!confirmation.equals("CONFIRM"))
            {
                throw new AnnotatedIllegalArgumentException("confirm should be set to CONFIRM");
            }

            log.warn("Clearing out the server!");

            try
            {
                engine().getArchetypeStore().clear();
                engine().getLocatableStore().clear();
                engine().getEHRStore().clear();
                Events.append(
                        "CLEAR",
                        "all",
                        "ALL",
                        "clear",
                        String.format(
                                "Cleared everything (confirm: %s)",
                                confirmation));
            }
            catch (IOException e)
            {
                throw new IORecordException(e.getMessage(), e);
            }
        }
        catch (RecordException|RuntimeException e)
        {
            Events.append(
                    "ERROR",
                    "all",
                    "ALL",
                    "clearFailure",
                    String.format(
                            "Failed to clear everything (confirm: %s): %s",
                            confirmation,
                            e.getMessage()));
            throw e;
        }
    }
}
