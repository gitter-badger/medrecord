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

import java.io.IOException;
import java.io.InputStream;

import com.medvision360.medrecord.api.exceptions.ParseException;

public interface ArchetypeParser extends TypeSelector // todo javadoc / api spec / exceptions
{
    public WrappedArchetype parse(InputStream is)
            throws IOException, ParseException;

    public WrappedArchetype parse(InputStream is, String encoding)
            throws IOException, ParseException;

}
