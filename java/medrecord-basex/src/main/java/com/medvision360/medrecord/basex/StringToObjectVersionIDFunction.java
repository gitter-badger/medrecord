package com.medvision360.medrecord.basex;

import com.google.common.base.Function;
import org.openehr.rm.support.identification.ObjectVersionID;

class StringToObjectVersionIDFunction implements Function<String, ObjectVersionID>
{
    private static final StringToObjectVersionIDFunction INSTANCE = new StringToObjectVersionIDFunction();

    public static StringToObjectVersionIDFunction getInstance()
    {
        return INSTANCE;
    }

    @Override
    public ObjectVersionID apply(String input)
    {
        if (input == null)
        {
            return null;
        }
        return new ObjectVersionID(input);
    }
}
