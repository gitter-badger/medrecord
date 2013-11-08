<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>Test ZorgGemak Service</title>
  <style type="text/css">
    .docpanel {
      float: right;
      width: 50%;
    }
    pre {
      overflow: auto;
    }
  </style>

  <script type="text/javascript" charset="utf-8" src="<c:url value='/resources/scripts/jquery.js'/>"></script>
  <script type="text/javascript" charset="utf-8" src="<c:url value='/resources/scripts/zorggemak.js'/>"></script>
  <script type="text/javascript" charset="utf-8">
    $(document).ready(function() {
      initZorgGemak();
      getVersion(function(data) {
        $("#version").html(data);
      });
    });

    function setResult(data) {
      if (data.errorcode != 0) {
        setError(data);
      }
      else {
        $("#result").html("Result of call:<pre>" + data.result + "</pre>");
      }
    }

    function setResultList(data) {
      if (data.errorcode != 0) {
        getLastError(data.reqid, setError);
      }
      else {
        var res = "";
        for (var i = 0; i < data.result.length; i++) {
          res += data.result[i];
          res += '\n';
        }
        $("#result").html("Result of call:<pre>" + res + "</pre>");
      }
    }

    function setError(data) {
      $("#result").html("Error: " + data.errorstr + " (" + data.errorcode + ") <br />RequestId: " + data.reqid +
          "<br />" + data.errormsg + "<br />" + data.timepassed + " msec ago" +
          "<br /><pre>" + data.errordetail + "</pre>");
    }
  </script>
</head>

<body>
<h1 style="float: left">ZorgGemak OpenEHR Service</h1><div style="font-size: 80%">(<span id="version"></span>)</div>
<hr style="clear: both"/>
<br/>
