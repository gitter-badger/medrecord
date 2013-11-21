/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import java.io.IOException;
import java.io.InputStream;

import com.medvision360.medrecord.api.exceptions.ParseException;
import org.openehr.rm.common.archetyped.Locatable;

public interface LocatableParser extends LocatableSelector, TypeSelector // todo javadoc / api spec / exceptions
{
    public Locatable parse(InputStream is) throws IOException, ParseException;

    public Locatable parse(InputStream is, String encoding) throws IOException, ParseException;
}
