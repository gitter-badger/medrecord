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

    function isModifiable() {
      $("#result").html("Loading data...");
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      isEHRModifiable($("#ehridmi").val(), setResult);
    }
    /* isModifiable */

    function setModifiable() {
      $("#result").html("Loading data...");
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      setEHRModifiable($("#ehridms").val(), $("#modflag").prop("checked"), setResult);
    }
    /* setModifiable */

    function isQueryable() {
      $("#result").html("Loading data...");
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      isEHRQueryable($("#ehridqi").val(), setResult);
    }
    /* isQueryable */

    function setQueryable() {
      $("#result").html("Loading data...");
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      setEHRQueryable($("#ehridqs").val(), $("#queryflag").prop("checked"), setResult);
    }
    /* setQueryable */

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

    function setError(data) {
      $("#result").html("Error: " + data.errorstr + " (" + data.errorcode + ") <br />RequestId: " + data.reqid +
          "<br />" + data.errormsg + "<br />" + data.timepassed + " msec ago" +
          "<br /><pre>" + data.errordetail + "</pre>");
    }
    /* setError */
  </script>
</head>

<body>
<h2>EHRService SOAP Calls:</h2>
<a href="https://sites.google.com/a/zorggemak.com/archetypegui/middleware/ehr-functions">Description</a><br/> <br/>

<div>
  Call EHRService.isModifiable<br/> &nbsp; EHR Id <input type="text" name="ehridmi" id="ehridmi" value="" size="50"/>
  <br/> <input type="submit" value="Get Result" name="GetResult" onclick="isModifiable();"/>
</div>
<p/>

<div>
  Call EHRService.setEHRModifiable<br/> &nbsp; EHR Id <input type="text" name="ehridms" id="ehridms" value=""
                                                             size="50"/> <br/> &nbsp; Modifiable flag <input
    type="checkbox" name="modflag" id="modflag"/> <br/> <input type="submit" value="Get Result" name="GetResult"
                                                               onclick="setModifiable();"/>
</div>
<p/>

<div>
  Call EHRService.isQueryable<br/> &nbsp; EHR Id <input type="text" name="ehridqi" id="ehridqi" value="" size="50"/>
  <br/> <input type="submit" value="Get Result" name="GetResult" onclick="isQueryable();"/>
</div>
<p/>

<div>
  Call EHRService.setEHRQueryable<br/> &nbsp; EHR Id <input type="text" name="ehridqs" id="ehridqs" value="" size="50"/>
  <br/> &nbsp; Queryable flag <input type="checkbox" name="queryflag" id="queryflag"/> <br/> <input type="submit"
                                                                                                    value="Get Result"
                                                                                                    name="GetResult"
                                                                                                    onclick="setQueryable();"/>
</div>
<p/>

<div id="result"></div>
<p/>
<a href="home">Back to Homepage</a> <br/>
</body>
</html>

