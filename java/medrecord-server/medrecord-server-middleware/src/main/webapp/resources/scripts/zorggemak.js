var middelware_host = null;
var last_error = {"errorcode": 0};

var AuditData = (function() {
  var user_id = "";
  var system_id = "";
  return {
    setUserId:   function(uid) {
      if (uid !== undefined) {
        user_id = uid;
      }
    },
    setSystemId: function(sid) {
      if (sid !== undefined) {
        system_id = sid;
      }
    },
    getHeaders:  function() {
      var hdr = {
        "X-Originating-System-Id": system_id,
        "X-On-Behalf-Of":          user_id
      };
      return(hdr);
    }
  };
}());


function initZorgGemak(host) {
  // sessionStorage.clear();
  jQuery.support.cors = true;
  if (!!host) {
    middelware_host = host;
  }
}

function getLastError(reqid, callback) {
  if (last_error.errorcode !== 0) {
    callback(last_error);
    sessionStorage.last_error = {"errorcode": 0};
  }
  else {
    get_json_data("GET", "v1/system/getlasterror", reqid, { }, callback);
  }
}

function setUserId(userid) {
  AuditData.setUserId(userid);
}

function setSystemId(systemid) {
  AuditData.setSystemId(systemid);
}

function getArchetypeValueByPath(objid, path, callback) {
  var pars = {"path": path};
  get_json_data("GET", "v1/archetype/getvalue", objid, pars, callback);
}

function analyzeDemographicXPathQuery(query, callback) {
  var pars = {"query": query};
  get_json_data("GET", "v1/query/analyzedemographicxpathquery", null, pars, callback);
}

function analyzeDemographicXQueryFLWORNotation(query, callback) {
  var pars = {"query": query};
  get_json_data("GET", "v1/query/analyzedemographicxqueryflwornotation", null, pars, callback);
}

function analyzeEhrXPathQuery(query, callback) {
  var pars = {"query": query};
  get_json_data("GET", "v1/query/analyzeehrxpathquery", null, pars, callback);
}

function analyzeEhrXQueryFLWORNotation(query, callback) {
  var pars = {"query": query};
  get_json_data("GET", "v1/query/analyzeehrxqueryflwornotation", null, pars, callback);
}

function ehrXPathQuery(query, callback) {
  var pars = {"query": query};
  get_json_data("GET", "v1/query/ehrxpathquery", null, pars, callback);
}

function ehrXQueryFLWORNotation(query, callback) {
  var pars = {"query": query};
  get_json_data("GET", "v1/query/ehrxqueryflwornotation", null, pars, callback);
}

function demographicXPathQuery(query, callback) {
  var pars = {"query": query};
  get_json_data("GET", "v1/query/demographicxpathquery", null, pars, callback);
}

function demographicXQueryFLWORNotation(query, callback) {
  var pars = {"query": query};
  get_json_data("GET", "v1/query/demographicxqueryflwornotation", null, pars, callback);
}

function getEHRBySubject(userid, callback) {
  var pars = {"userid": userid};
  get_json_data("GET", "v1/ehr/getehr", null, pars, callback);
}

function createEHR(paths, values, callback) {
  var pars = {"paths": JSON.stringify(paths), "values": JSON.stringify(values)};
  get_json_data("POST", "v1/ehr/createehr", null, pars, callback);
}

function isEHRModifiable(ehrid, callback) {
  var pars = {"ehrid": ehrid};
  get_json_data("GET", "v1/ehr/isehrmodifiable", null, pars, callback);
}

function setEHRModifiable(ehrid, modflag, callback) {
  var pars = {"ehrid": ehrid, "modflag": modflag};
  get_json_data("POST", "v1/ehr/setehrmodifiable", null, pars, callback);
}

function isEHRQueryable(ehrid, callback) {
  var pars = {"ehrid": ehrid};
  get_json_data("GET", "v1/ehr/isehrqueryable", null, pars, callback);
}

function setEHRQueryable(ehrid, queryflag, callback) {
  var pars = {"ehrid": ehrid, "queryflag": queryflag};
  get_json_data("POST", "v1/ehr/setehrqueryable", null, pars, callback);
}

function retrieveCompositionList(ehruid, callback) {
  var pars = {"ehruid": ehruid};
  get_json_data("GET", "v1/composition/retrievecompositionlist", null, pars, callback);
}

function retrieveComposition(ehruid, xmldata, callback) {
  var pars = {"ehruid": ehruid};
  if (!!xmldata && xmldata === true) {
    get_json_data("GET", "v1/composition/retrievecomposition/xml", null, pars, callback);
  }
  else {
    get_json_data("GET", "v1/composition/retrievecomposition", null, pars, callback);
  }
}

function retrieveCompositionSPN(ehruid, xmldata, callback) {
  var pars = {"ehruid": ehruid};
  if (!!xmldata && xmldata === true) {
    get_json_data("GET", "v1/composition/retrievecomposition/spn/xml", null, pars, callback);
  }
  else {
    get_json_data("GET", "v1/composition/retrievecomposition/spn", null, pars, callback);
  }
}

function retrieveCompositionByItem(itemid, xmldata, callback) {
  var pars = {"itemid": itemid};
  if (!!xmldata && xmldata === true) {
    get_json_data("GET", "v1/composition/retrievecompositionbyitem/xml", null, pars, callback);
  }
  else {
    get_json_data("GET", "v1/composition/retrievecompositionbyitem", null, pars, callback);
  }
}

function createComposition(ehruid, paths, values, callback) {
  var pars = {"ehruid": ehruid, "paths": JSON.stringify(paths), "values": JSON.stringify(values)};
  get_json_data("POST", "v1/composition/createcomposition", null, pars, callback);
}

function modifyComposition(paths, values, callback) {
  var pars = {"paths": JSON.stringify(paths), "values": JSON.stringify(values)};
  get_json_data("POST", "v1/composition/modifycomposition", null, pars, callback);
}

function deleteComposition(compid, callback) {
  var pars = {"compid": compid};
  get_json_data("POST", "v1/composition/deletecomposition", null, pars, callback);
}

function undeleteComposition(compid, callback) {
  var pars = {"compid": compid};
  get_json_data("POST", "v1/composition/undeletecomposition", null, pars, callback);
}

function addPartyRelationship(sourceid, targetid, paths, values, callback) {
  var pars = {"sourceid": sourceid, "targetid": targetid, "paths": JSON.stringify(paths), "values": JSON.stringify(values)};
  get_json_data("POST", "v1/demographic/addrelationship", null, pars, callback);
}

function removePartyRelationship(relationshipid, callback) {
  var pars = {"relationshipid": relationshipid};
  get_json_data("POST", "v1/demographic/removerelationship", null, pars, callback);
}

function listArchetypes(regexpr, callback) {
  var pars = {"regexpr": regexpr};
  get_json_data("GET", "v1/archetype/listarchetypes", null, pars, callback);
}

function archetypeExist(archid, callback) {
  var pars = {"archid": archid};
  get_json_data("GET", "v1/archetype/archetypeexist", null, pars, callback);
}

function retrieveArchetype(archid, pathflag, callback) {
  var pars = {"archid": archid, "pathflag": pathflag};
  get_json_data("GET", "v1/archetype/getarchetype", null, pars, callback);
}

function storeArchetype(archid, archetype, callback) {
  var pars = {"archid": archid, "archetype": archetype};
  get_json_data("POST", "v1/archetype/storearchetype", null, pars, callback);
}

function deleteArchetype(archid, callback) {
  var pars = {"archid": archid};
  get_json_data("POST", "v1/archetype/deletearchetype", null, pars, callback);
}

function makeShortPath(path, callback) {
  var pars = {"longpath": path};
  get_json_data("GET", "v1/archetype/makeshortpath", null, pars, callback);
}

function resolveIdToLongPath(archid, callback) {
  var pars = {"archid": archid};
  get_json_data("GET", "v1/archetype/resolveidtolongpath", null, pars, callback);
}

function termDefinitionMap(lang, archid, callback) {
  var pars = {"lang": lang, "archid": archid};
  get_json_data("GET", "v1/archetype/termdefinitionmap", null, pars, callback);
}

function termDefinitionLanguages(archid, callback) {
  var pars = {"archid": archid};
  get_json_data("GET", "v1/archetype/termdefinitionlanguages", null, pars, callback);
}

function adlToXpath(adlpath, callback) {
  var pars = {"adlpath": adlpath};
  get_json_data("GET", "v1/archetype/adltoxpath", null, pars, callback);
}

function listContentItemsForComposition(compid, callback) {
  var pars = {"compid": compid};
  get_json_data("GET", "v1/composition/listitemsforcomposition", null, pars, callback);
}

function listSectionsForComposition(compid, callback) {
  var pars = {"compid": compid};
  get_json_data("GET", "v1/composition/listsectionsforcomposition", null, pars, callback);
}

function listContentItemsForSection(sectid, callback) {
  var pars = {"sectid": sectid};
  get_json_data("GET", "v1/composition/listitemsforsection", null, pars, callback);
}

function listSectionsForSection(sectid, callback) {
  var pars = {"sectid": sectid};
  get_json_data("GET", "v1/composition/listsectionsforsection", null, pars, callback);
}

function addContentItemToComposition(compid, archid, paths, values, callback) {
  var pars = {"compid": compid, "archid": archid, "paths": JSON.stringify(paths), "values": JSON.stringify(values)};
  get_json_data("POST", "v1/composition/addcontentitemtocomposition", null, pars, callback);
}

function addContentItemToSection(sectid, archid, paths, values, callback) {
  var pars = {"sectid": sectid, "archid": archid, "paths": JSON.stringify(paths), "values": JSON.stringify(values)};
  get_json_data("POST", "v1/composition/addcontentitemtosection", null, pars, callback);
}

function retrieveArchetypeIDForContentItemOrComposition(cuid, callback) {
  var pars = {"cuid": cuid};
  get_json_data("GET", "v1/composition/retrievearchetypeidforcontentitemorcomposition", null, pars, callback);
}

function retrieveEhrIdForComposition(cuid, callback) {
  var pars = {"cuid": cuid};
  get_json_data("GET", "v1/composition/retrieveehridforcomposition", null, pars, callback);
}

function moveContentItem(contid, toid, callback) {
  var pars = {"contid": contid, "toid": toid};
  get_json_data("POST", "v1/composition/movecontentitem", null, pars, callback);
}

function removeItemFromEHR(ehrid, itemid, callback) {
  var pars = {"ehrid": ehrid, "itemid": itemid};
  get_json_data("POST", "v1/composition/removeitemfromehr", null, pars, callback);
}

function countActors(callback) {
  var pars = { };
  get_json_data("GET", "v1/demographic/countactors", null, pars, callback);
}

function listActors(offset, limit, callback) {
  var pars = {"offset": offset, "limit": limit};
  get_json_data("GET", "v1/demographic/getactors", null, pars, callback);
}

function getActor(guid, callback) {
  var pars = {"guid": guid};
  get_json_data("GET", "v1/demographic/getactor", null, pars, callback);
}

function createActor(paths, values, callback) {
  var pars = {"paths": JSON.stringify(paths), "values": JSON.stringify(values)};
  get_json_data("POST", "v1/demographic/createactor", null, pars, callback);
}

function modifyActor(actorid, paths, values, callback) {
  var pars = {"paths": JSON.stringify(paths), "values": JSON.stringify(values)};
  get_json_data("POST", "v1/demographic/modifyactor", actorid, pars, callback);
}

function deleteActor(guid, callback) {
  var pars = {"guid": guid};
  get_json_data("POST", "v1/demographic/deleteactor", null, pars, callback);
}

function undeleteActor(guid, callback) {
  var pars = {"guid": guid};
  get_json_data("POST", "v1/demographic/undeleteactor", null, pars, callback);
}

function retrieveDemographic(guid, ignoredel, callback) {
  var pars = {"guid": guid, "ignoredel": ignoredel};
  get_json_data("GET", "v1/demographic/retrievedemographic", null, pars, callback);
}

function retrieveDemographicSPN(guid, ignoredel, callback) {
  var pars = {"guid": guid, "ignoredel": ignoredel};
  get_json_data("GET", "v1/demographic/retrievedemographic/spn", null, pars, callback);
}

function getAuditInformation(auditid, callback) {
  var pars = {"auditid": auditid};
  get_json_data("GET", "v1/audit/auditinformation", null, pars, callback);
}

function getAuditUUIDsForAuditableObject(objectuid, begindate, enddate, callback) {
  var pars = {"objectuid": objectuid, "begindate": begindate, "enddate": enddate};
  get_json_data("GET", "v1/audit/audituuidsforauditableobject", null, pars, callback);
}

function getAuditUUIDsForPerson(personid, begindate, enddate, callback) {
  var pars = {"personid": personid, "begindate": begindate, "enddate": enddate};
  get_json_data("GET", "v1/audit/audituidsforperson", null, pars, callback);
}

function getVersionUIDsOfVersionedObject(objectuid, begindate, enddate, callback) {
  var pars = {"objectuid": objectuid, "begindate": begindate, "enddate": enddate};
  get_json_data("GET", "v1/audit/versionuidsofversionedobject", null, pars, callback);
}

function getVersionedObject(objectuid, callback) {
  var pars = {"objectuid": objectuid};
  get_json_data("GET", "v1/audit/versionedobject", null, pars, callback);
}

function getContributionUUIDsForCommitter(personuid, begindate, enddate, callback) {
  var pars = {"personuid": personuid, "begindate": begindate, "enddate": enddate};
  get_json_data("GET", "v1/audit/contributionuuidsforcommitter", null, pars, callback);
}

function getContributionUUIDsForObject(objectuid, begindate, enddate, callback) {
  var pars = {"objectuid": objectuid, "begindate": begindate, "enddate": enddate};
  get_json_data("GET", "v1/audit/contributionuuidsforobject", null, pars, callback);
}

function getVersion(callback) {
  var url;

  if (middelware_host !== null) {
    url = middelware_host + "/v1/test/getversion";
  }
  else {
    url = "v1/test/getversion";
  }

  jQuery.get(url, function(data) {
    callback(data);
  });
}

// Local functions

function get_json_data(reqtype, url, reqid, pars, callback) {
  if (middelware_host !== null) {
    url = middelware_host + "/" + url;
  }
  if (reqid !== null) {
    url += "/" + reqid;
  }
  syslog("get_json_data: url=" + url + " pars=" + pars);

  jQuery.ajax({
    headers:     AuditData.getHeaders(),
    type:        reqtype,
    url:         url,
    dataType:    'json',
    data:        pars,
    crossDomain: true,
    error:       function(xhr, stat, err) {
      handle_error(xhr, stat, err, callback);
    },
    success:     function(data, stat, xhr) {
      handle_complete(data, stat, xhr, callback);
    }
  });
}

function handle_complete(data, stat, xhr, callback) {
  syslog("handle_complete: data=" + data);
  syslog("handle_complete: stat=" + stat);
  syslog("handle_complete: data.errcode=" + data.errorcode);

  if (!!callback) {
    callback(eval(data));
  }
}

function handle_error(xhr, stat, err, callback) {
  syslog("handle_error: err=" + err);
  syslog("handle_error: stat=" + stat);

  var msg = stat;
  if (err !== "") {
    msg += ": " + err;
  }
  if (xhr.status !== 0) {
    msg += " (" + xhr.status + ")";
  }

  last_error = {
    reqid:      0,
    errorcode:  2,
    errorstr:   "Ajax Error",
    errormsg:   msg,
    errordetail: null,
    timepassed: 0
  };
  var result = {
    "reqid": 0,
    "result": "Ajax Error",
    "errorcode": 2,
    "errorstr": "Ajax Error",
    "errormsg": "",
    "errordetail": "",
    timepassed: 0
  };
  if (!!callback) {
    callback(result);
  }
}

function syslog(msg) {
  if (!!window.console) {
    window.console.log(msg);
  }
}
