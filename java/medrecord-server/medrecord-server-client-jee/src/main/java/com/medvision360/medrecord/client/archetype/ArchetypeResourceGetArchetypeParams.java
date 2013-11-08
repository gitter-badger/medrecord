// WARNING, THIS FILE IS AUTOMATICALLY GENERATED
// DO NOT MODIFY !

package com.medvision360.medrecord.client.archetype;

import java.util.ArrayList;
import java.util.List;

import org.restlet.resource.ClientResource;

import com.medvision360.lib.client.MethodParameterBase;

/**
 * Class for adding query parameters to the request.
 *
 * <p>This class holds the query parameters used when making requests using the
 * {@link ArchetypeResource#getArchetype(ArchetypeResourceGetArchetypeParams) getArchetype()} method of the <code>ArchetypeResource</code>
 * class.
 */
public class ArchetypeResourceGetArchetypeParams extends MethodParameterBase
{
    /**
     * Gets the value of the <code>id</code> query parameter.
     *
     * <p>The default value for the query parameter is <code>null</code>
     * meaning the parameter will not be included in the request.</p>
     *
     * <p><b>Description:</b> <i>An OpenEHR ArchetypeID value</i></p>
     *
     * @return The value of the <code>id</code> query parameter
     *   or <code>null</code> when the query parameter is not set.
     */
    public String getId()
    {
        return getQueryArgument("id");
    }

    /**
     * Sets the <code>id</code> query parameter.
     *
     * <p><b>Description:</b> <i>An OpenEHR ArchetypeID value</i></p>
     *
     * <b>This parameter is required and must be set before making a request!</b>
     *
     * @param value The new value for the <code>id</code> query
     *   parameter. Use <code>null</code> (which also is the default value)
     *   to make sure the parameter is not added to the request.
     */
    public void setId(final String value)
    {
        setQueryArgument("id", value);
    }

}
