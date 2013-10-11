package com.medrecord.spi.base;

import com.medrecord.spi.LocatableSerializer;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

import java.io.OutputStream;

public class NoopSerializer implements LocatableSerializer {
    @Override
    public void serialize(Locatable locatable, OutputStream os) {
        throw new UnsupportedOperationException("todo implement NoopSerializer.serialize()");
    }

    @Override
    public void serialize(Locatable locatable, OutputStream os, String encoding) {
        throw new UnsupportedOperationException("todo implement NoopSerializer.serialize()");
    }

    @Override
    public String getMimeType() {
        throw new UnsupportedOperationException("todo implement NoopSerializer.getMimeType()");
    }

    @Override
    public String getFormat() {
        throw new UnsupportedOperationException("todo implement NoopSerializer.getFormat()");
    }

    @Override
    public boolean supports(Locatable locatable) {
        throw new UnsupportedOperationException("todo implement NoopSerializer.supports()");
    }

    @Override
    public boolean supports(Archetyped archetyped) {
        throw new UnsupportedOperationException("todo implement NoopSerializer.supports()");
    }
}
