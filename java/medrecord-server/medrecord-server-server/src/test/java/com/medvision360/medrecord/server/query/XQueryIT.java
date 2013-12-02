package com.medvision360.medrecord.server.query;

import java.io.InputStream;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.exceptions.RecordException;
import com.medvision360.medrecord.client.locatable.LocatableResource;
import com.medvision360.medrecord.client.query.XQueryResource;
import com.medvision360.medrecord.server.AbstractServerTest;
import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openehr.rm.common.archetyped.Locatable;
import org.restlet.representation.Representation;

@SuppressWarnings("FieldCanBeLocal")
@RunWith(JUnit4.class)
public class XQueryIT extends AbstractServerTest
{
    private XQueryResource m_xQueryResource;
    private Locatable locatable;
    private Representation result;
    private String body;
    private Representation request;
    private ID id;
    private String idString;
    private LocatableResource resource;
    private String q;


    @Before
    public void prepare() throws Exception
    {
        m_xQueryResource = new XQueryResource(m_resourceConfig);

        // clear() means no archetypes, but we need this...
        ensureEHRStatusArchetype();
        ensureCompositionArchetype();
        ensureAdminEntryArchetype();
    }

    @Test
    public void basicXQueries() throws Exception
    {
        for (int i=0; i < 10; i++)
        {
            locatable = makeLocatable();
            request = toJsonRequest(locatable);
            id = m_locatableListResource.postLocatable(request);
            idString = id.getId();
            resource = locatableResource(idString);
            result = resource.getLocatable();
            locatable = fromJsonRequest(result);
        }
        
        q = "(: find all archetypes that are in use :)\n" +
                "distinct-values(//archetype_details/archetype_id/value/text()[concat(., ' ')])";

        body = query();
        assertTrue(body.contains(ADMIN_ENTRY_ARCHETYPE));
        
        q = "(: dump all compositions (probably will cause OOM) :)\n" +
                "for $x in collection()/composition\n" +
                "  return $x";
        body = query();
        assertTrue(body.contains(ADMIN_ENTRY_ARCHETYPE));
        assertTrue(body.contains(idString));
    }

    @Test
    public void basicXQueryWithCustomNamespaces() throws Exception
    {
        locatable = makeLocatable();
        request = toJsonRequest(locatable);
        id = m_locatableListResource.postLocatable(request);
        idString = id.getId();
        resource = locatableResource(idString);
        result = resource.getLocatable();
        locatable = fromJsonRequest(result);
        
        q = "" + 
                "declare default element namespace \"http://example.org/schema\";\n" +
                "declare namespace openehr = \"http://example.org/openehr\";\n" +
                "declare namespace foo = \"http://schemas.openehr.org/v1\";\n" +
                "declare namespace xsi = \"http://example.org/xsi\";\n" +
                "declare namespace bar = \"http://www.w3.org/2001/XMLSchema-instance\";\n" +
                "(: find all archetypes that are in use :)\n" +
                "for $x in" +
                "  //foo:archetype_details/foo:archetype_id/foo:value/text()" +
                "return" +
                "  <openehr:silly><xsi:stupid>{$x}</xsi:stupid></openehr:silly>";
        ;

        body = query();
        assertTrue(body.contains(ADMIN_ENTRY_ARCHETYPE));
    }

    private String query() throws Exception
    {
        Representation representation = m_xQueryResource.xQuery(q);
        InputStream is = representation.getStream();
        body = IOUtils.toString(is);
        System.out.println("xquery result:");
        System.out.println("----");
        if (body.length() > 200)
        {
            System.out.print(body.substring(0, 200));
            System.out.println("...");
        }
        else
        {
            System.out.println(body);
        }
        System.out.println("----");
        return body;
    }
}
