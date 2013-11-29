package com.medvision360.medrecord.pv;

import java.io.IOException;
import java.util.Map;

public class PVMapMakerVisitor implements SerializeVisitor
{
    private Map<String, Object> m_map;
    
    public PVMapMakerVisitor(Map<String, Object> map)
    {
        m_map = map;
    }

    @Override
    public void pair(String path, Object value) throws IOException
    {
        m_map.put(path, value);
    }
}
