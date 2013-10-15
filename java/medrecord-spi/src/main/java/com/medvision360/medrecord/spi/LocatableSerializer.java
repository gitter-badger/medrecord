package com.medvision360.medrecord.spi;

import org.openehr.rm.common.archetyped.Locatable;

import java.io.OutputStream;

public interface LocatableSerializer extends LocatableService { // todo javadoc / api spec / exceptions
    
    public void serialize(Locatable locatable, OutputStream os);
    
    public void serialize(Locatable locatable, OutputStream os, String encoding);
    
    public String getMimeType();
    
    public String getFormat();
}
