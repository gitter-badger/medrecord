// WARNING, THIS FILE IS AUTOMATICALLY GENERATED
// DO NOT MODIFY !

package com.medvision360.medrecord.client.query;

import org.restlet.Client;
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
    @apipath /query/xquery

 */
public class XQueryResource extends ClientResourceBase
{
    /**
     * Constructor.
     *
     * <p>This constructor can be used to create a new client for this resource.</p>
     *
     * @param config_ Configuration object containing the location of the server
     *   this resource sends requests to.
     */
    public XQueryResource(
        final ClientResourceConfig config_
    )
    {
        super(config_, "/query/xquery");
    }

    /**
       XQuery locatable resources.

Executes the provided XQuery against all XQuery-capable storage mechanisms for locatables on the server
(depending on the server configuration, some locatables may be stored in non-xquery-capable stores) and
returns the result.

XQuery is a powerful query language, and accordingly this API provides a very advanced and powerful query
mechanism, but it is important to realize that the trade-off in using it is that writing the queries and
interpreting the results may be harder, and that performance may be lower than for some of the simpler query
options.

The server implementation makes no attempt to do any processing on the results of your query. Depending on the
backing implementation and the queries you write, this may expose you to some implementation specifics in the
query results. It also means that the result may or may not be valid XML, and accordingly,
by default it is returned as a <code>text/plain</code> string. If you wish to retrieve the results in
processed form, like a nested map of path/value locatables, you may wish to use the
<code>/query/xquery/locatable</code> API instead. That API also allows you to limit to a specific EHR. If you
just wish to set the response mime type, declare it in your query, like so:
<pre>
(: set the response media type to xml :)
declare option output:media-type "application/xml";
</pre>
Note that this simply sets the http header; no attempt at data conversion is done.

All kinds of XQuery are supported. You can use XPath expressions such as
<pre>
declare default element namespace "http://schemas.openehr.org/v1";
(: find all archetypes that are in use :)
distinct-values(//archetype_details/archetype_id/value/text()[concat(., ' ')])
</pre>

Or you can use FLWOR queries such as
<pre>
declare default element namespace "http://schemas.openehr.org/v1";
(: dump all compositions (probably will cause OOM) :)
for $x in collection()/composition
return $x
</pre>

or perhaps
<pre>
declare default element namespace "http://schemas.openehr.org/v1";
declare namespace openehr = "http://schemas.openehr.org/v1";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";
(: dump all observations (probably will cause OOM) :)
for $x in collection()//*[contains(@xsi:type,"OBSERVATION")] return $x
</pre>

If you do not specify a default element namespace in your query, it is set to "http://schemas.openehr.org/v1".
The xsi and openehr namespaces are also declared for you unless you have declared them yourself. This means
that the following query has the same result as the one above:
<pre>
for $x in collection()//openehr:*[contains(@xsi:type,"OBSERVATION")] return $x
</pre>



       <p>
       Use the {@link #xQuery(String,XQueryResourceXQueryParams)}
       method to pass additional query arguments.</p>

       @param q An XQuery, written to run against (a) collection(s) that contains openEHR XML dat

       @apiqueryparam q An XQuery, written to run against (a) collection(s) that contains openEHR XML data
[type=string,required,single,default=//archetype_details/archetype_id/value/text()]



     */
    public org.restlet.representation.Representation xQuery(
        final String q
    ) throws
        com.medvision360.medrecord.api.exceptions.InvalidQueryException,
        com.medvision360.medrecord.api.exceptions.UnsupportedQueryException,
        com.medvision360.medrecord.api.exceptions.RecordException,
        com.medvision360.medrecord.api.exceptions.IORecordException
    {
      return xQuery(
        q,
        null
      );
    }

    /**
       XQuery locatable resources.

Executes the provided XQuery against all XQuery-capable storage mechanisms for locatables on the server
(depending on the server configuration, some locatables may be stored in non-xquery-capable stores) and
returns the result.

XQuery is a powerful query language, and accordingly this API provides a very advanced and powerful query
mechanism, but it is important to realize that the trade-off in using it is that writing the queries and
interpreting the results may be harder, and that performance may be lower than for some of the simpler query
options.

The server implementation makes no attempt to do any processing on the results of your query. Depending on the
backing implementation and the queries you write, this may expose you to some implementation specifics in the
query results. It also means that the result may or may not be valid XML, and accordingly,
by default it is returned as a <code>text/plain</code> string. If you wish to retrieve the results in
processed form, like a nested map of path/value locatables, you may wish to use the
<code>/query/xquery/locatable</code> API instead. That API also allows you to limit to a specific EHR. If you
just wish to set the response mime type, declare it in your query, like so:
<pre>
(: set the response media type to xml :)
declare option output:media-type "application/xml";
</pre>
Note that this simply sets the http header; no attempt at data conversion is done.

All kinds of XQuery are supported. You can use XPath expressions such as
<pre>
declare default element namespace "http://schemas.openehr.org/v1";
(: find all archetypes that are in use :)
distinct-values(//archetype_details/archetype_id/value/text()[concat(., ' ')])
</pre>

Or you can use FLWOR queries such as
<pre>
declare default element namespace "http://schemas.openehr.org/v1";
(: dump all compositions (probably will cause OOM) :)
for $x in collection()/composition
return $x
</pre>

or perhaps
<pre>
declare default element namespace "http://schemas.openehr.org/v1";
declare namespace openehr = "http://schemas.openehr.org/v1";
declare namespace xsi = "http://www.w3.org/2001/XMLSchema-instance";
(: dump all observations (probably will cause OOM) :)
for $x in collection()//*[contains(@xsi:type,"OBSERVATION")] return $x
</pre>

If you do not specify a default element namespace in your query, it is set to "http://schemas.openehr.org/v1".
The xsi and openehr namespaces are also declared for you unless you have declared them yourself. This means
that the following query has the same result as the one above:
<pre>
for $x in collection()//openehr:*[contains(@xsi:type,"OBSERVATION")] return $x
</pre>



       @param queryParams_ The query parameters to be added to the request.
       @param q An XQuery, written to run against (a) collection(s) that contains openEHR XML dat

       @apiqueryparam q An XQuery, written to run against (a) collection(s) that contains openEHR XML data
[type=string,required,single,default=//archetype_details/archetype_id/value/text()]


     */
    public org.restlet.representation.Representation xQuery(
        final String q,
        final XQueryResourceXQueryParams queryParams_
    ) throws
        com.medvision360.medrecord.api.exceptions.InvalidQueryException,
        com.medvision360.medrecord.api.exceptions.UnsupportedQueryException,
        com.medvision360.medrecord.api.exceptions.RecordException,
        com.medvision360.medrecord.api.exceptions.IORecordException
    {
        final ClientResource resource_ = getClientResource();
        try
        {
            if (queryParams_ != null)
            {
                queryParams_.applyTo(resource_);
            }

            resource_.addQueryParameter("q", q);
            final com.medvision360.medrecord.api.query.XQueryResource wrapped_ = resource_.wrap(com.medvision360.medrecord.api.query.XQueryResource.class);
            final org.restlet.representation.Representation result_ = wrapped_.xQuery(
            );

            handleCookies(resource_);

            return result_;
        }
        catch(final ResourceException e_)
        {
            final ErrorDocument errorDocument_ = ErrorDocument.getFrom(resource_);
            if (errorDocument_ != null)
            {
                switch(errorDocument_.getCode())
                {
                    case "INVALID_QUERY_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.InvalidQueryException(errorDocument_.getArguments());
                    case "UNSUPPORTED_QUERY_EXCEPTION":
                        throw new com.medvision360.medrecord.api.exceptions.UnsupportedQueryException(errorDocument_.getArguments());
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
