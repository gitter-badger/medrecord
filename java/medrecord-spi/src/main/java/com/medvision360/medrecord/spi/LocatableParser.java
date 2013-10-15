/**
 * This file is part of MEDrecord
 *
 * @copyright Copyright 2013 by MEDvision360. All rights reserved.
 * @author Leo Simons <leo@medvision360.com>
 * @author Ralph van Etten <ralph@medvision360.com>
 */
package com.medvision360.medrecord.spi;

import org.openehr.rm.common.archetyped.Locatable;

import java.io.InputStream;

public interface LocatableParser extends LocatableService { // todo javadoc / api spec / exceptions
    
    public Locatable parse(InputStream is);
    
    public Locatable parse(InputStream is, String encoding);
    
    public String getMimeType();
    
    public String getFormat();
}
