package com.medvision360.medrecord.basex;

import com.google.common.base.Function;
import org.openehr.rm.support.identification.ArchetypeID;

public class StringToArchetypeIDFunction implements Function<String, ArchetypeID>
{
    private static final StringToArchetypeIDFunction INSTANCE = new StringToArchetypeIDFunction();

    public static StringToArchetypeIDFunction getInstance()
    {
        return INSTANCE;
    }

    @Override
    public ArchetypeID apply(String input)
    {
        if (input == null)
        {
            return null;
        }
        return new ArchetypeID(input);
    }
}
