package com.medvision360.medrecord.spi;

import org.openehr.rm.common.archetyped.Locatable;

import java.io.InputStream;

public interface LocatableParser extends LocatableService { // todo javadoc / api spec / exceptions
    
    public Locatable parse(InputStream is);
    
    public Locatable parse(InputStream is, String encoding);
    
    public String getMimeType();
    
    public String getFormat();
}
