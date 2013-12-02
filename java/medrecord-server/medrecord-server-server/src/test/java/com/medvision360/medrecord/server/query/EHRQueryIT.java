package com.medvision360.medrecord.server.query;

import com.medvision360.medrecord.api.ID;
import com.medvision360.medrecord.api.IDList;
import com.medvision360.medrecord.api.exceptions.InvalidRangeException;
import com.medvision360.medrecord.client.ehr.EHRListResourceListEHRsParams;
import com.medvision360.medrecord.client.ehr.EHRResource;
import com.medvision360.medrecord.client.query.QueryEHRResource;
import com.medvision360.medrecord.client.query.QueryEHRResourceEhrQueryParams;
import com.medvision360.medrecord.server.AbstractServerTest;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.openehr.rm.common.archetyped.Archetyped;
import org.openehr.rm.datastructure.itemstructure.ItemStructure;
import org.openehr.rm.ehr.EHRStatus;
import org.openehr.rm.support.identification.ArchetypeID;
import org.restlet.representation.Representation;

@SuppressWarnings("FieldCanBeLocal")
@RunWith(JUnit4.class)
public class EHRQueryIT extends AbstractServerTest
{
    private final static String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZ";
    private QueryEHRResource m_queryEHRResource;
    private IDList list;
    private IDList result;
    private Representation request;
    private EHRListResourceListEHRsParams params;
    private QueryEHRResourceEhrQueryParams queryParams;
    private StringBuffer buf;
    private ID id;
    private String idString;
    
    @Before
    public void prepare() throws Exception
    {
        m_queryEHRResource = new QueryEHRResource(m_resourceConfig);

        // clear() means no archetypes, but we need this...
        ensureEHRStatusArchetype();
    }

    @Test
    public void emptyQueryIsEqualToList() throws Exception
    {
        // POST to make sure list is non-empty
        EHRStatus status = makeStatus(4);
        request = toJsonRequest(status);
        ID id = m_ehrListResource.postEHR(request);
        String idString = id.getId();
        
        // LIST
        list = m_ehrListResource.listEHRs();
        assertNotNull(list);
        assertNotNull(list.getIds());
        //assertTrue(list.getIds().size() > 0);
        //assertTrue(list.getIds().contains(idString));

        // QUERY
        result = m_queryEHRResource.ehrQuery();
        assertEquals(list.getIds().size(), result.getIds().size());
        assertTrue(list.getIds().contains(idString));
        
        // LIST excludeDeleted=true
        params = new EHRListResourceListEHRsParams();
        params.setExcludeDeleted("true");
        list = m_ehrListResource.listEHRs(params);

        // QUERY excludeDeleted=true
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setExcludeDeleted("true");
        result = m_queryEHRResource.ehrQuery(queryParams);
        assertEquals(list.getIds().size(), result.getIds().size());

        // LIST excludeDeleted=false
        params = new EHRListResourceListEHRsParams();
        params.setExcludeDeleted("false");
        list = m_ehrListResource.listEHRs(params);

        // QUERY excludeDeleted=false
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setExcludeDeleted("false");
        result = m_queryEHRResource.ehrQuery(queryParams);
        assertEquals(list.getIds().size(), result.getIds().size());
    }

    @Test
    public void dateTimePredicates() throws Exception
    {
        // POST 1
        // t: start
        DateTime start = DateTime.now();
        EHRStatus status = makeStatus(1);
        request = toJsonRequest(status);
        id = m_ehrListResource.postEHR(request);
        String beforeBefore = id.getId();
        Thread.sleep(1001);

        // t: before
        DateTime before = DateTime.now();
        buf = new StringBuffer();
        String beforeString = DateTimeFormat.forPattern(pattern).print(before);
        System.out.println("before: " + beforeString);
        Thread.sleep(1001);
        
        // POST 2
        // t: created
        DateTime created = DateTime.now();
        EHRStatus status2 = makeStatus(2);
        request = toJsonRequest(status2);
        id = m_ehrListResource.postEHR(request);
        idString = id.getId();
        Thread.sleep(1001);
        
        // t: after
        DateTime after = DateTime.now();
        buf = new StringBuffer();
        String afterString = DateTimeFormat.forPattern(pattern).print(after);
        System.out.println("after: " + afterString);
        Thread.sleep(1001);

        // POST 3
        // t: end
        EHRStatus status3 = makeStatus(3);
        request = toJsonRequest(status3);
        id = m_ehrListResource.postEHR(request);
        String afterAfter = id.getId();
        DateTime end = DateTime.now();
        
        if (before.isBefore(start) || created.isBefore(before) || after.isBefore(before) ||
                after.isBefore(created) || end.isBefore(after))
        {
            System.err.println("The clock is not running forward according to our assumptions, cannot continue test");
            return;
        }

        // QUERY     (1 included) < before
        //                          before < (2 excluded) 
        //                          before < (3 excluded) 
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setCreatedBefore(beforeString);
        result = m_queryEHRResource.ehrQuery(queryParams);
        assertTrue(result.getIds().contains(beforeBefore));
        assertFalse(result.getIds().contains(idString));
        assertFalse(result.getIds().contains(afterAfter));

        // QUERY     (1 excluded) < after
        //           (2 excluded) < after 
        //                          after < (3 included) 
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setCreatedAfter(afterString);
        result = m_queryEHRResource.ehrQuery(queryParams);
        assertFalse(result.getIds().contains(beforeBefore));
        assertFalse(result.getIds().contains(idString));
        assertTrue(result.getIds().contains(afterAfter));

        // QUERY     (1 excluded) < before
        //                          before < (2 included) < after
        //                                                  after < (3 excluded) 
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setCreatedAfter(beforeString);
        queryParams.setCreatedBefore(afterString);
        result = m_queryEHRResource.ehrQuery(queryParams);
        assertFalse(result.getIds().contains(beforeBefore));
        assertTrue(result.getIds().contains(idString));
        assertFalse(result.getIds().contains(afterAfter));

        // QUERY     before == after -- no results
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setCreatedAfter(afterString);
        queryParams.setCreatedBefore(afterString);
        result = m_queryEHRResource.ehrQuery(queryParams);
        assertFalse(result.getIds().contains(beforeBefore));
        assertFalse(result.getIds().contains(idString));
        assertFalse(result.getIds().contains(afterAfter));

        // QUERY     before > after -- X
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setCreatedAfter(afterString);
        queryParams.setCreatedBefore(beforeString);
        try
        {
            m_queryEHRResource.ehrQuery(queryParams);
            fail("Expected error if before > after");
        }
        catch (InvalidRangeException e)
        {
            // good
        }
    }

    @Test
    public void excludeEmpty() throws Exception
    {
        EHRStatus status = makeStatus(5);
        request = toJsonRequest(status);
        id = m_ehrListResource.postEHR(request);
        idString = id.getId();
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setExcludeEmpty("true");
        result = m_queryEHRResource.ehrQuery(queryParams);
        assertFalse(result.getIds().contains(idString));
    }

    @Test
    public void excludeDeleted() throws Exception
    {
        EHRStatus status = makeStatus(6);
        request = toJsonRequest(status);
        id = m_ehrListResource.postEHR(request);
        idString = id.getId(); 
        
        EHRResource resource = ehrResource(idString);
        resource.deleteEHR();
        
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setExcludeDeleted("true");

        result = m_queryEHRResource.ehrQuery(queryParams);
        assertFalse(result.getIds().contains(idString));
    }

    @Test
    public void subjectPredicate() throws Exception
    {
        EHRStatus status = makeStatus(7);
        request = toJsonRequest(status);
        id = m_ehrListResource.postEHR(request);
        idString = id.getId();
        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setSubject(m_parent.getSubject().getExternalRef().getId().getValue());

        result = m_queryEHRResource.ehrQuery(queryParams);
        assertTrue(result.getIds().contains(idString));
    }

    @Test
    public void complexPredicate() throws Exception
    {
        // t: before
        DateTime before = DateTime.now();
        String beforeString = DateTimeFormat.forPattern(pattern).print(before);
        Thread.sleep(1001);
        
        // POST
        // t: created
        EHRStatus status = makeStatus(8);
        request = toJsonRequest(status);
        id = m_ehrListResource.postEHR(request);
        idString = id.getId();
        Thread.sleep(1001);
        
        // t: after
        DateTime after = DateTime.now();
        String afterString = DateTimeFormat.forPattern(pattern).print(after);
        Thread.sleep(1001);
        
        
        String systemID = ehrResource(idString).getEHR().getSystemId();

        queryParams = new QueryEHRResourceEhrQueryParams();
        queryParams.setCreatedAfter(beforeString);
        queryParams.setCreatedBefore(afterString);
        queryParams.setExcludeEmpty("false");
        queryParams.setExcludeDeleted("false");
        queryParams.setSystemID(systemID);
        queryParams.setSubject(m_parent.getSubject().getExternalRef().getId().getValue());
        result = m_queryEHRResource.ehrQuery(queryParams);
        assertTrue(result.getIds().contains(idString));
    }

    private EHRStatus makeStatus(int no)
    {
        ItemStructure otherDetails = list(String.format("EHRStatus details %s", no));
        Archetyped arch = new Archetyped(new ArchetypeID("unittest-EHR-EHRSTATUS.ehrstatus.v1"), "1.0.2");
        return new EHRStatus(makeUID(), "at0001", text(String.format("EHR Status %s", no)),
                        arch, null, null, null, subject, true, true, otherDetails);
    }
}
