package com.medvision360.medrecord.basex;

import com.google.common.base.Function;
import org.openehr.rm.support.identification.HierObjectID;

class StringToHierObjectIDFunction implements Function<String, HierObjectID>
{
    private static final StringToHierObjectIDFunction instance = new StringToHierObjectIDFunction();

    public static StringToHierObjectIDFunction getInstance()
    {
        return instance;
    }

    @Override
    public HierObjectID apply(String input)
    {
        if (input == null)
        {
            return null;
        }
        return new HierObjectID(input);
    }
}
