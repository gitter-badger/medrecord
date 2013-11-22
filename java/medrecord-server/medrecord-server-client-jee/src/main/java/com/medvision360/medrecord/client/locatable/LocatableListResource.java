// WARNING, THIS FILE IS AUTOMATICALLY GENERATED
// DO NOT MODIFY !

package com.medvision360.medrecord.client.locatable;

import org.restlet.data.Language;
import org.restlet.data.Preference;
import org.restlet.resource.ClientResource;
import org.restlet.resource.ResourceException;


import com.medvision360.lib.client.ClientResourceBase;
import com.medvision360.lib.client.ClientResourceConfig;
import com.medvision360.lib.client.ErrorDocument;
import com.medvision360.lib.common.exceptions.AnnotatedResourceException;
import com.medvision360.lib.common.exceptions.ApiException;

/**
    @apipath /locatable

 */
public class LocatableListResource extends ClientResourceBase
{
    /**
     * Constructor.
     *
     * <p>This constructor can be used to create a new client for this resource.</p>
     *
     * @param config_ Configuration object containing the location of the server
     *   this resource sends requests to.
     */
    public LocatableListResource(
        final ClientResourceConfig config_
    )
    {
        super(config_, "/locatable");
    }

    /**
       Create locatable resource outside EHR.

Store a new locatable from a path/value JSON document. The provided locatable does not need unique identifiers;
if those are not provided they will be added by the server. If you do provide an identifier, it will result in
DUPLICATE_EXCEPTION if the locatable already exists (use PUT to update an existing locatable).

This API call will not associated the locatable with any EHR record. This may be appropriate,
especially for demographics information, but for most locatables, you should use
<code>/ehr/{id}/locatable</code> to store a locatable inside of an EHR record. Note it is not possible to
associate a locatable with an EHR after it has been created.



       <p>
       Use the {@link #postLocatable(org.restlet.representation.Representation,LocatableListResourcePostLocatableParams)}
       method to pass additional query arguments.</p>


       

     */
    public com.medvision360.medrecord.api.ID postLocatable(
        final org.restlet.representation.Representation representation
    ) throws
        com.medvision360.medrecord.api.exceptions.DuplicateException,
        com.medvision360.medrecord.api.exceptions.ClientParseException,
        com.medvision360.medrecord.api.exceptions.NotSupportedException,
        com.medvision360.medrecord.api.exceptions.ValidationException,
        com.medvision360.medrecord.api.exceptions.RecordException,
        com.medvision360.medrecord.api.exceptions.IORecordException
    {
      return postLocatable(
        representation,
        null
      );
    }

    /**
       Create locatable resource outside EHR.

Store a new locatable from a path/value JSON document. The provided locatable does not need unique identifiers;
if those are not provided they will be added by the server. If you do provide an identifier, it will result in
DUPLICATE_EXCEPTION if the locatable already exists (use PUT to update an existing locatable).

This API call will not associated the locatable with any EHR record. This may be appropriate,
especially for demographics information, but for most locatables, you should use
<code>/ehr/{id}/locatable</code> to store a locatable inside of an EHR record. Note it is not possible to
associate a locatable with an EHR after it has been created.



       @param queryParams_ The query parameters to be added to the request.

       
     */
    public com.medvision360.medrecord.api.ID postLocatable(
        final org.restlet.representation.Representation representation,
        final LocatableListResourcePostLocatableParams queryParams_
    ) throws
        com.medvision360.medrecord.api.exceptions.DuplicateException,
        com.medvision360.medrecord.api.exceptions.ClientParseException,
        com.medvision360.medrecord.api.exceptions.NotSupportedException,
        com.medvision360.medrecord.api.exceptions.ValidationException,
        com.medvision360.medrecord.api.exceptions.RecordException,
        com.medvision360.medrecord.api.exceptions.IORecordException
    {
        try
        {
            final ClientResource resource_ = getClientResource();
            if (queryParams_ != null)
            {
                queryParams_.applyTo(resource_);
            }

            final com.medvision360.medrecord.api.locatable.LocatableListResource wrapped_ = resource_.wrap(com.medvision360.medrecord.api.locatable.LocatableListResource.class);
            return wrapped_.postLocatable(
                representation
            );
        }
        catch(final ResourceException e_)
        {
            final ErrorDocument errorDocument_ = getErrorDocument();
            if (errorDocument_ != null)
            {
                switch(errorDocument_.getCode())
                {
                    case "DUPLICATE_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.DuplicateException(errorDocument_.getArguments());
                    case "CLIENT_PARSE_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.ClientParseException(errorDocument_.getArguments());
                    case "NOT_SUPPORTED_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.NotSupportedException(errorDocument_.getArguments());
                    case "VALIDATION_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.ValidationException(errorDocument_.getArguments());
                    case "RECORD_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.RecordException(errorDocument_.getArguments());
                    case "IO_RECORD_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.IORecordException(errorDocument_.getArguments());
                }
            }
            throw e_;
        }
    }

    /**
       List locatable resources.

Retrieve a list of all locatable IDs known to the server encapsulated in JSON. This API call is of very limited
use in most practical scenarios, because the result can become very large, and because typically you will want
to constrain the results by some kind of selection or query, like looking only for locatables associated with a
particular patient or caregiver. Use the <code>/ehr/{id}/locatable</code> API to constrain the list to a
particular EHR, or use the <code>/query</code> API for more complex queries.

However, this API call is provided nonetheless, for API consistency, completeness and ease of testing.



       <p>
       Use the {@link #listLocatables(LocatableListResourceListLocatablesParams)}
       method to pass additional query arguments.</p>


       

     */
    public com.medvision360.medrecord.api.IDList listLocatables(
    ) throws
        com.medvision360.medrecord.api.exceptions.ParseException,
        com.medvision360.medrecord.api.exceptions.RecordException,
        com.medvision360.medrecord.api.exceptions.IORecordException
    {
      return listLocatables(
        null
      );
    }

    /**
       List locatable resources.

Retrieve a list of all locatable IDs known to the server encapsulated in JSON. This API call is of very limited
use in most practical scenarios, because the result can become very large, and because typically you will want
to constrain the results by some kind of selection or query, like looking only for locatables associated with a
particular patient or caregiver. Use the <code>/ehr/{id}/locatable</code> API to constrain the list to a
particular EHR, or use the <code>/query</code> API for more complex queries.

However, this API call is provided nonetheless, for API consistency, completeness and ease of testing.



       @param queryParams_ The query parameters to be added to the request.

       
     */
    public com.medvision360.medrecord.api.IDList listLocatables(
        final LocatableListResourceListLocatablesParams queryParams_
    ) throws
        com.medvision360.medrecord.api.exceptions.ParseException,
        com.medvision360.medrecord.api.exceptions.RecordException,
        com.medvision360.medrecord.api.exceptions.IORecordException
    {
        try
        {
            final ClientResource resource_ = getClientResource();
            if (queryParams_ != null)
            {
                queryParams_.applyTo(resource_);
            }

            final com.medvision360.medrecord.api.locatable.LocatableListResource wrapped_ = resource_.wrap(com.medvision360.medrecord.api.locatable.LocatableListResource.class);
            return wrapped_.listLocatables(
            );
        }
        catch(final ResourceException e_)
        {
            final ErrorDocument errorDocument_ = getErrorDocument();
            if (errorDocument_ != null)
            {
                switch(errorDocument_.getCode())
                {
                    case "PARSE_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.ParseException(errorDocument_.getArguments());
                    case "RECORD_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.RecordException(errorDocument_.getArguments());
                    case "IO_RECORD_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.IORecordException(errorDocument_.getArguments());
                }
            }
            throw e_;
        }
    }

}