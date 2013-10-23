package com.medvision360.medrecord.pv;

import java.io.IOException;

public interface SerializeVisitor
{
    public void pair(String path, Object value) throws IOException;
}
