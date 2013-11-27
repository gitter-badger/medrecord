<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%--
  This file is part of MEDrecord.
  This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
  "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

      http://creativecommons.org/licenses/by-nc-sa/4.0/

  Copyright (c) 2013 MEDvision360. All rights reserved.
--%>
<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="header.jsp" %>

<h2><a href="<c:url value='/v2/apidocs/'/>">Version 2 API</a></h2>
<a href="<c:url value='/v2/apidocs/'/>" style="padding-left: 5px;">Version 2 API</a>

<h2>Version 1 API</h2>
<table border="0" cellpadding="5" cellspacing="0">
  <tr>
    <td valign="top">
      <h4>Generic system functions</h4>
      <ul>
        <li><a href="./system:login.page">Audit parameters</a></li>
        <li><a href="./system:lasterror.page">getLastError</a> <em>deprecated</em></li>
      </ul>
    </td>
    <td valign="top">
      <h4>Archetype Functions</h4>
      <ul>
        <li><a href="./archetype:listarchetype.page">listArchetypes</a></li>
        <li><a href="./archetype:archetypeexist.page">archetypeExist</a></li>
        <li><a href="./archetype:getarchetype.page">retrieveArchetype</a></li>
        <li><a href="./archetype:savearchetype.page">storeArchetype</a></li>
        <li><a href="./archetype:delarchetype.page">deleteArchetype</a></li>
        <!--<li><a href="./archetype:makeshortpath.page">makeShortPath</a></li>-->
        <!--<li><a href="./archetype:resolveidtolongpath.page">resolveIdToLongPath</a></li>-->
        <li><a href="./archetype:termdefinitionmap.page">termDefinitionMap</a></li>
        <li><a href="./archetype:termdefinitionlanguages.page">termDefinitionLanguages</a></li>
        <li><a href="./archetype:adltoxpath.page">adlToXpath</a></li>
      </ul>
    </td>
    <td valign="top">
      <h4>Query Functions</h4>
      <ul>
        <li><a href="./query:ehrxpathquery.page">ehrXPathQuery</a></li>
        <li><a href="./query:ehrxqueryflwornotation.page">ehrXQueryFLWORNotation</a></li>
        <li><a href="./query:demographicxpathquery.page">demographicXPathQuery</a></li>
        <li><a href="./query:demographicxqueryflwornotation.page">demographicXQueryFLWORNotation</a></li>
      </ul>
      <ul>
        <li><a href="./query:analyzedemographicxpathquery.page">analyzeDemographicXPathQuery</a></li>
        <li><a href="./query:analyzedemographicxqueryflwornotation.page">analyzeDemographicXQueryFLWORNotation</a></li>
        <li><a href="./query:analyzeehrxpathquery.page">analyzeEhrXPathQuery</a></li>
        <li><a href="./query:analyzeehrxqueryflwornotation.page">analyzeEhrXQueryFLWORNotation</a></li>
      </ul>
    </td>
  </tr>

  <tr>
    <td valign="top">
      <h4>Demographic Functions</h4>
      <ul>
        <li><a href="./demographic:actors.page">listActors</a></li>
        <li><a href="./demographic:actors.page">countActors</a></li>
        <li><a href="./demographic:createactor.page">createActor</a></li>
        <li><a href="./demographic:modifyactor.page">modifyActor</a></li>
        <li><a href="./demographic:actors.page">deleteActor</a></li>
        <li><a href="./demographic:actors.page">undeleteActor</a></li>
        <li><a href="./demographic:addpartyrelationship.page">addPartyRelationship</a></li>
        <li><a href="./demographic:delpartyrelationship.page">removePartyRelationship</a></li>
        <li><a href="./demographic:retrievedemographic.page">retrieveDemographic</a></li>
      </ul>
    </td>
    <td valign="top">
      <h4>EHR Functions</h4>
      <ul>
        <li><a href="./ehr:getehrbyuserid.page">getEHRBySubject</a></li>
        <li><a href="./ehr:createehr.page">createEHR</a></li>
        <li><a href="./ehr:ehrfunctions.page">isEHRModifiable</a></li>
        <li><a href="./ehr:ehrfunctions.page">setEHRModifiable</a></li>
        <li><a href="./ehr:ehrfunctions.page">isEHRQueryable</a></li>
        <li><a href="./ehr:ehrfunctions.page">setEHRQueryable</a></li>
      </ul>
    </td>
    <td valign="top">
      <h4>Composition Functions</h4>
      <ul>
        <li><a href="./composition:getcomplist.page">retrieveCompositionList</a></li>
        <li><a href="./composition:getcomposition.page">retrieveComposition</a></li>
        <li><a href="./composition:getcompositionbyitem.page">retrieveCompositionByItem</a></li>
        <li><a href="./composition:createcomposition.page">createComposition</a></li>
        <li><a href="./composition:modifycomposition.page">modifyComposition</a></li>
        <li><a href="./composition:delcomposition.page">deleteComposition</a></li>
        <li><a href="./composition:undelcomposition.page">undeleteComposition</a></li>

        <li><a href="./composition:retrieveehridforcomposition.page">retrieveEhrIdForComposition</a></li>
        <li><a href="./composition:retrievearchidforcomposition.page">retrieveArchetypeIDForComposition</a></li>
      </ul>
    </td>
  </tr>

  <tr>
    <td valign="top">
      <h4>ContentItem Functions</h4>
      <ul>
        <li><a href="./contentitem:listcompositionitems.page">listContentItemsForComposition</a></li>
        <li><a href="./contentitem:listsectionitems.page">listContentItemsForSection</a></li>
        <li><a href="./contentitem:addcontentitemtocomposition.page">addContentItemToComposition</a></li>
        <li><a href="./contentitem:addcontentitemtosection.page">addContentItemToSection</a></li>
        <li><a href="./contentitem:modifycontentitem.page">modifyContentItem</a></li>
        <li><a href="./contentitem:movecontentitem.page">moveContentItem</a></li>
        <li><a href="./contentitem:delitemfromehr.page">removeItemFromEHR</a></li>

        <li><a href="./contentitem:retrievearchidforcontent.page">retrieveArchetypeIDForContentItem</a></li>
      </ul>
    </td>
    <td valign="top">
      <h4>Section Functions</h4>
      <ul>
        <li><a href="./section:listcompositionsections.page">listSectionsForComposition</a></li>
        <li><a href="./section:listsectionsections.page">listSectionsForSection</a></li>
      </ul>
    </td>
    <td valign="top">
      <h4>Audit Functions</h4>
      <ul>
        <li><a href="./audit:getauditinformation.page">getAuditInformation</a></li>
        <li><a href="./audit:getauditinformationforobject.page">getAuditUUIDsForAuditableObject</a></li>
        <li><a href="./audit:getaudituuidsforperson.page">getAuditUUIDsForPerson</a></li>
        <li><a href="./audit:getversionuidsofversionedobject.page">getVersionUIDsOfVersionedObject</a></li>
        <li><a href="./audit:getversionedobject.page">getVersionedObject</a></li>
        <li><a href="./audit:contributionuuidsforcommitter.page">getContributionUUIDsForCommitter</a></li>
        <li><a href="./audit:contributionuuidsforobject.page">getContributionUUIDsForObject</a></li>
      </ul>
    </td>
  </tr>
</table>

<%@include file="footer.jsp" %>
