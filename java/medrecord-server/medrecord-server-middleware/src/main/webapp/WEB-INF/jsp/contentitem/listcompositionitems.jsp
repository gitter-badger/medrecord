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

    function listCompositionItems() {
      $("#result").html("Loading data...");
      setUserId(localStorage.userid);
      setSystemId(localStorage.systemid);
      listContentItemsForComposition($("#compid").val(), setResult);
    }
    /* listCompositionItems */

    function setResult(data) {
      if (data.errorcode != 0) {
        getLastError(data.reqid, setError);
      }
      else {
        //                    var res = data.result.replace(/</g, "&lt;").replace(/>/g, "&gt;");
        //                    $("#result").html("Result of call:<pre>" + res + "</pre>");
        var res = "";
        for (var i = 0; i < data.result.length; i++) {
          res += data.result[i];
          res += '\n';
        }
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
<h2>Call web API function 'listContentItemsForComposition'</h2>
Is passing the call to the openEHR kernel SOAP API CompositionService.listContentItemsForComposition<br/> <a
    href="https://sites.google.com/a/zorggemak.com/archetypegui/middleware/composition-functions/listcontentitemsforcomposition">Description</a><br/>
<br/>

<div>
  &nbsp; Composition GUID <input type="text" name="compid" id="compid" value="1ff2b5ca-7971-4a32-8a29-e4b360ec7282"
                                 size="50"/>

  <p/>
  <input type="submit" value="Get Result" name="GetResult" onclick="listCompositionItems();"/>
</div>
<br/>

<div id="result"></div>
<p/>
<a href="home">Back to Homepage</a> <br/>
</body>
</html>
