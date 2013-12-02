package com.medvision360.medrecord.tools.cliclient;

import org.restlet.data.CharacterSet;
import org.restlet.data.MediaType;
import org.restlet.representation.ByteArrayRepresentation;
import org.restlet.representation.Representation;
import org.restlet.representation.StringRepresentation;

public class RestletUtil
{
    protected Representation toJsonRequest(String json)
    {
        return new StringRepresentation(json, MediaType.APPLICATION_JSON, null, CharacterSet.UTF_8);
    }

    protected Representation toJsonRequest(byte[] serialized)
    {
        Representation representation = new ByteArrayRepresentation(serialized, MediaType.APPLICATION_JSON, 
                serialized.length);
        return representation;
    }
}
