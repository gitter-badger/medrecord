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
  <link rel="stylesheet" type="text/css" href="<c:url value='css/style.css'/>"/>
  <script type="text/javascript" src="<c:url value='js/jquery-min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='js/underscore-min.js'/>"></script>
  <script type="text/javascript" src="<c:url value='js/underscore.string.js'/>"></script>
  <script type="text/javascript" src="<c:url value='js/log.js'/>"></script>
  <script type="text/javascript" src="<c:url value='js/wslog.js'/>"></script>
</head>

<body>
    <header id="header">
        <img src="<c:url value='/v2/apidocs/images/logo_small.png'/>"/>
        <h1>MEDrecord</h1>
        <a href="//medrecord.test.medvision360.org/medrecord/v2/apidocs/">remove frame</a>
    </header>
    <div id="main">
