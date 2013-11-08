<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">

<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Test ZorgGemak Service</title>

  <script type="text/javascript" charset="utf-8" src="<c:url value='/resources/scripts/jquery.js'/>"></script>
  <script type="text/javascript" charset="utf-8" src="<c:url value='/resources/scripts/zorggemak.js'/>"></script>
  <script type="text/javascript" charset="utf-8">
    $(document).ready(function() {
      initZorgGemak();
    });
    /* ready */

    function getActorPaths() {
      $("#result").html("Loading data...");
      $('#input_list li:not(#input_template)').remove();
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      getActor($("#guid").val(), setActorData);
    }
    /* getActorPaths */

    function setActorData(data) {
      if (data.errorcode != 0) {
        getLastError(data.reqid, setError);
      }
      else {
        setUserId(localStorage.userid);
        setSystemId(localStorage.systemid);
        $('#objecttag').removeAttr('style').find("#objectid").text(data.actorid);
        for (var i = 0; i < data.result.length; i++) {
          var entry = $('#input_template').clone();
          entry.appendTo('#input_list');
          entry.attr('id', "pathid" + i);
          entry.find("#path").val(data.result[i]);
          entry.find("#thevalue").val("");
          getArchetypeValueByPath(data.actorid, data.result[i], setPathValue);
          entry.removeAttr('style');
        }
        $("#addinputline").removeAttr('style');
        $("#getresult").removeAttr('style');
        $("#result").html("");
      }
    }
    /* setActorData */

    function setPathValue(data) {
      if (data.errorcode != 0) {
        getLastError(data.reqid, setError);
      }
      else {
        $("#input_list").find("#path").filter(function() {
          return ($(this).val() == data.path)
        }).parent().find("#thevalue").val(data.result);
      }
    }
    /* setPathValue */

    function addInputLine() {
      var entry = $('#input_template').clone();
      entry.appendTo('#input_list');
      entry.find("#path").val("");
      entry.find("#thevalue").val("");
      entry.removeAttr('style');
    }
    /* addInputLine */

    function updateActor() {
      var path = new Array();
      var value = new Array();

      $("#result").html("Loading data...");

      $.map($("li"), function(item, index) {
        path[index] = $(item).find("#path").val();
        value[index] = $(item).find("#thevalue").val();
      });

      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      modifyActor($("#objectid").text(), path, value, setResult);
    }
    /* updateActor */

    function setResult(data) {
      if (data.errorcode != 0) {
        getLastError(data.reqid, setError);
      }
      else {
        $("#result").html("Result of call:<pre>" + data.result + "</pre>");
      }
    }
    /* setResult */

    function setError(data) {
      $("#result").html("Error: " + data.errorstr + " (" + data.errorcode + ") <br />RequestId: " + data.reqid +
          "<br />" + data.errormsg + "<br />" + data.timepassed + " msec ago" +
          "<br /><pre>" + data.errordetail + "</pre>");
    }
    /* setError */
  </script>
</head>

<body>
<h2>Call web API function 'modifyActor'</h2>
Is passing the call to the openEHR kernel SOAP API DemographicService.saveDemographicModified<br/> <a
    href="https://sites.google.com/a/zorggemak.com/archetypegui/middleware/demographic-functions/modifyactor">Description</a><br/>

<div>
  <p/>
  &nbsp; Actor GUID <input type="text" name="guid" id="guid" value="713159c5-878f-4aaf-b8b9-70882747a5c3" size="50"/>
  <br/> <input type="submit" value="Get Actor" name="GetActor" onclick="getActorPaths();"/>

  <p/>

  <div id="objecttag" style="display: none;">Actor ObjectId: <span id="objectid"></span></div>
  <ol id="input_list">
    <li id="input_template" style="display: none;">
      <span id="inlist">&nbsp; Path <input type="text" name="path" id="path" value="" size="100"/> &nbsp; Value <input
          type="text" name="thevalue" id="thevalue" value="" size="50"/></span>
    </li>
  </ol>
  <input type="submit" value="Add Path/Value" id="addinputline" name="AddInputLine" onclick="addInputLine();"
         style="display: none;"/>

  <p/>
  <input type="submit" value="Save Result" id="getresult" name="GetResult" onclick="updateActor();"
         style="display: none;"/>
</div>
<p/>

<div id="result"></div>
<p/>
<a href="home">Back to Homepage</a> <br/>
</body>
</html>

