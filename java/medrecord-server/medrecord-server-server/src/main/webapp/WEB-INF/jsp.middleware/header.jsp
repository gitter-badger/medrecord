<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!--
  This file is part of MEDrecord.
  This work is licensed under a Creative Commons Attribution-NonCommercial-ShareAlike 4.0 International License (the
  "License"); you may not use this file except in compliance with the License. You may obtain a copy of the License at

      http://creativecommons.org/licenses/by-nc-sa/4.0/

  Copyright (c) 2013 MEDvision360. All rights reserved.
-->
<html>
<head>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
  <title>MEDrecord EHR Server</title>
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

  <link href="<c:url value='/v2/apidocs/css/hightlight.default.css'/>" media="screen" rel="stylesheet" type="text/css">
  <link href="<c:url value='/v2/apidocs/css/screen.css'/>" media="screen" rel="stylesheet" type="text/css">
</head>

<body>
<div style="margin: 1em;">
  <img src="<c:url value='/v2/apidocs/images/logo_small.png'/>" style="float: left; padding-right: 1em;"/>
  <h1 style="float: left;">MEDrecord: the MEDvision OpenEHR Service</h1>
  <div style="font-size: 80%;">(<span id="version"></span>)</div>
</div>
<hr style="clear: both;"/>
<div style="margin: 1em;">
