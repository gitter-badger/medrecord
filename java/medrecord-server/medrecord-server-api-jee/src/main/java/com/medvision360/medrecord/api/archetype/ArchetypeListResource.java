package com.medvision360.medrecord.api.archetype;

import java.io.IOException;

import com.medvision360.medrecord.api.exceptions.ClientParseException;
import com.medvision360.medrecord.api.exceptions.PatternException;
import com.medvision360.medrecord.spi.exceptions.DuplicateException;
import com.medvision360.medrecord.spi.exceptions.IORecordException;
import com.medvision360.medrecord.spi.exceptions.MissingParameterException;
import com.medvision360.medrecord.spi.exceptions.ParseException;
import com.medvision360.medrecord.spi.exceptions.RecordException;
import org.restlet.resource.Get;
import org.restlet.resource.Post;

/**
 * @apipath /archetype
 */
@SuppressWarnings("DuplicateThrows")
public interface ArchetypeListResource
{
    /**
     * Create archetype resource.
     * 
     * Store an archetype from an ADL string (plain text). Will result in DUPLICATE_EXCEPTION if the archetype 
     * already exists. If you want to update an archetype that's unused, you can delete it first and then re-upload 
     * it. Updating archetypes once they are in use is not possible.
     * 
     * Note that for non-web-based tools, simply using the plain text API is probably much easier, i.e. something like
     * <code>curl -X POST -T foo.adl -H "Content-Type: text/plain" $URL/medrecord/v2/archetype</code>
     * works fine.
     * 
     * Store an archetype encapsulated in JSON.
     */
    @Post("json")
    public void postArchetype(ArchetypeRequest archetype)
            throws DuplicateException, ClientParseException, MissingParameterException,
            RecordException, IORecordException;
    
    /**
     * Create archetype resource.
     * 
     * @apiacceptvariant postArchetype
     */
    @Post("txt")
    public void postArchetypeAsText(String adl)
            throws DuplicateException, ClientParseException, MissingParameterException,
            RecordException, IORecordException;

    /**
     * List archetype resources.
     * 
     * Retrieve a list of archetype IDs known to the server encapsulated in JSON.
     * 
     * @apiqueryparam q A regular expression to limit the returned archetypes by their name.
     *   [type=string,single,default=openEHR-EHR.*]
     */
    @Get
    public ArchetypeList listArchetypes()
            throws PatternException, ParseException, RecordException, IORecordException;
}
