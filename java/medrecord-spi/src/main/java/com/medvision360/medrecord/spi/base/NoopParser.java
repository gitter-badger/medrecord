package com.medvision360.medrecord.spi.base;

import com.medvision360.medrecord.spi.LocatableParser;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.common.archetyped.Locatable;

import java.io.InputStream;

public class NoopParser implements LocatableParser {
    @Override
    public Locatable parse(InputStream is) {
        throw new UnsupportedOperationException("todo implement NoopParser.parse()");
    }

    @Override
    public Locatable parse(InputStream is, String encoding) {
        throw new UnsupportedOperationException("todo implement NoopParser.parse()");
    }

    @Override
    public String getMimeType() {
        throw new UnsupportedOperationException("todo implement NoopParser.getMimeType()");
    }

    @Override
    public String getFormat() {
        throw new UnsupportedOperationException("todo implement NoopParser.getFormat()");
    }

    @Override
    public boolean supports(Locatable locatable) {
        throw new UnsupportedOperationException("todo implement NoopParser.supports()");
    }

    @Override
    public boolean supports(Archetyped archetyped) {
        throw new UnsupportedOperationException("todo implement NoopParser.supports()");
    }
}
