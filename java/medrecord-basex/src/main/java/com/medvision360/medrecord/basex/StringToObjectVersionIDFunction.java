package com.medvision360.medrecord.basex;

import com.google.common.base.Function;
import com.sun.istack.internal.Nullable;
import org.openehr.rm.support.identification.ObjectVersionID;

class StringToObjectVersionIDFunction implements Function<String, ObjectVersionID>
{
    private static final StringToObjectVersionIDFunction instance = new StringToObjectVersionIDFunction();
    
    public static StringToObjectVersionIDFunction getInstance() {
        return instance;
    }
    
    @Override
    public ObjectVersionID apply(@Nullable String input)
    {
        if (input == null)
        {
            return null;
        }
        return new ObjectVersionID(input);
    }
}
