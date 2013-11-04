/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.OutputStream;

import com.medvision360.medrecord.spi.exceptions.SerializeException;
import org.openehr.rm.common.archetyped.Locatable;

public interface LocatableSerializer extends LocatableSelector, TypeSelector // todo javadoc / api spec / exceptions
{
    public void serialize(Locatable locatable, OutputStream os) throws IOException, SerializeException;

    public void serialize(Locatable locatable, OutputStream os, String encoding) throws IOException, SerializeException;
}
