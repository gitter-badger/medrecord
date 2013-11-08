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

    function retrieveError() {
      $("#result").html("Loading data...");
      getLastError($("#reqid").val(), setError);
    }
    /* retrieveError */

    function setError(data) {
      $("#result").html("Error: " + data.errorstr + " (" + data.errorcode + ") <br />RequestId: " + data.reqid +
          "<br />" + data.errormsg + "<br />" + data.timepassed + " msec ago" +
          "<br /><pre>" + data.errordetail + "</pre>");
    }
    /* setError */

    // For testing only
    function getErrorCodes() {
      $("#result").html("Loading data...");
      $.get('v1/test/geterrortable', function(data) {
        $("#result").html(data);
      });
    }
    /* getErrorCodes */
  </script>
</head>

<body>
<h2>Call web API function 'getLastError'</h2>
<strong>deprecated:</strong> Error information is now always provided directly in the response.<br/> <br/> Return the
last Middleware error information.<br/> <a
    href="https://sites.google.com/a/zorggemak.com/archetypegui/middleware/system-functions/getlasterror">Description</a><br/>
<br/>

<div>
  &nbsp; RequestId <input type="text" name="reqid" id="reqid" value="" size="50"/>

  <p/>
  <input type="submit" value="Get Result" name="GetResult" onclick="retrieveError();"/> <input type="submit"
                                                                                               value="Get Error Codes"
                                                                                               name="GetErrorCodes"
                                                                                               onclick="getErrorCodes();"/>
</div>
<br/>

<div id="result"></div>
<p/>
<a href="home">Back to Homepage</a> <br/>
</body>
</html>
