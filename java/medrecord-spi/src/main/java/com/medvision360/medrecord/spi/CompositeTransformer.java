package com.medvision360.medrecord.spi;

import java.util.ArrayList;
import java.util.List;

import com.medvision360.medrecord.api.exceptions.TransformException;
import org.openehr.rm.common.archetyped.Locatable;

import static com.google.common.base.Preconditions.checkNotNull;

public class CompositeTransformer implements CompositeService<LocatableTransformer>, LocatableTransformer
{
    private List<LocatableTransformer> m_delegates = new ArrayList<>();
    
    @Override
    public void addDelegate(LocatableTransformer delegate)
    {
        m_delegates.add(checkNotNull(delegate, "delegate cannot be null"));
    }

    @Override
    public void transform(Locatable locatable) throws TransformException
    {
        for (LocatableTransformer delegate : m_delegates)
        {
            delegate.transform(locatable);
        }
    }
}
