package com.medvision360.medrecord.spi;

import com.medvision360.medrecord.spi.exceptions.TransformException;
import org.openehr.rm.common.archetyped.Locatable;

public interface LocatableTransformer
{
    public void transform(Locatable locatable) throws TransformException;
}
