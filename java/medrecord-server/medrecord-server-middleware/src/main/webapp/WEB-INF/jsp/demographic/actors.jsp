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

    function cntActors() {
      $("#result").html("Loading data...");
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      countActors(setResult);
    }
    /* cntActors */

    function getActorsList() {
      $("#result").html("Loading data...");
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      listActors($("#offset").val(), $("#limit").val(), setResultList);
    }
    /* getActorsList */

    function delActor() {
      $("#result").html("Loading data...");
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      deleteActor($("#actord").val(), setResult);
    }
    /* delActor */

    function undelActor() {
      $("#result").html("Loading data...");
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      undeleteActor($("#actoru").val(), setResult);
    }
    /* undelActor */

    function setResult(data) {
      if (data.errorcode != 0) {
        getLastError(data.reqid, setError);
      }
      else {
        var res = data.result.replace(/</g, "&lt;").replace(/>/g, "&gt;");
        $("#result").html("Result of call:<pre>" + res + "</pre>");
      }
    }
    /* setResult */

    function setResultList(data) {
      if (data.errorcode != 0) {
        getLastError(data.reqid, setError);
      }
      else {
        // listActors returns an string array with actor id's
        var res = "Actors found: ";
        res += data.result.length;
        res += '\n';
        for (var i = 0; i < data.result.length; i++) {
          res += data.result[i];
          res += '\n';
        }
        $("#result").html("Result of call:<pre>" + res + "</pre>");
      }
    }
    /* setResultList */

    function setError(data) {
      $("#result").html("Error: " + data.errorstr + " (" + data.errorcode + ") <br />RequestId: " + data.reqid +
          "<br />" + data.errormsg + "<br />" + data.timepassed + " msec ago" +
          "<br /><pre>" + data.errordetail + "</pre>");
    }
    /* setError */
  </script>
</head>

<body>
<h2>DemographicService Actor SOAP Calls:</h2>

<div>
  Call DemographicService.countActors<br/> <input type="submit" value="Get Result" name="GetResult"
                                                  onclick="cntActors();"/>
</div>
<p/>

<div>
  Call DemographicService.listActors:<br/> &nbsp; Offset <input type="text" name="offset" id="offset" value="0"
                                                                size="3"/> <br/> &nbsp; Limit <input type="text"
                                                                                                     name="limit"
                                                                                                     id="limit"
                                                                                                     value="5"
                                                                                                     size="3"/> <br/>
  <input type="submit" value="Get Result" name="GetResult" onclick="getActorsList();"/>
</div>
<p/>

<div>
  Call DemographicService.deleteActor<br/> &nbsp; Actor GUID <input type="text" name="actord" id="actord" value=""
                                                                    size="50"/> <br/> <input type="submit"
                                                                                             value="Get Result"
                                                                                             name="GetResult"
                                                                                             onclick="delActor();"/>
</div>
<p/>

<div>
  Call DemographicService.undeleteActor<br/> &nbsp; Actor GUID <input type="text" name="actoru" id="actoru" value=""
                                                                      size="50"/> <br/> <input type="submit"
                                                                                               value="Get Result"
                                                                                               name="GetResult"
                                                                                               onclick="undelActor();"/>
</div>
<p/>

<div id="result"></div>
<p/>
<a href="home">Back to Homepage</a> <br/>
</body>
</html>

