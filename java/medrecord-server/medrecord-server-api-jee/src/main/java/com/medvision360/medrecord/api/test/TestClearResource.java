package com.medvision360.medrecord.api.test;

import com.medvision360.medrecord.api.exceptions.RecordException;
import org.restlet.resource.Post;

/**
 * @apipath /test/clear
 * @apiqueryparam confirm Set to the value "CONFIRM" to confirm you wish to do this.
 *   [type=string,required,single,default=false]
 */
public interface TestClearResource
{
    /**
     * Clear all databases.
     *
     * Empty out the server databases completely, removing all locatables,  all archetypes, all EHRs, 
     * and any other stored data. This is a <strong>very</strong> destructive method intended for use with 
     * (unit) testing only.
     */
    @Post
    public void clear() throws RecordException;
}
