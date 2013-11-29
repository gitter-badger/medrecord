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

<iframe src="<c:url value='/v2/apidocs/'/>" id="api"></iframe>
<script type="text/javascript">
    $(function() {
        var otherContent = 182; // combined height of other page elements
        var frame = document.getElementById('api');
        
        function resizeIFrame() {
            var height = document.documentElement.clientHeight;
            height -= otherContent;
            frame.style.height = (height < 100)? 'auto' : height + 'px';
        }
        
        if (frame.attachEvent) {
            frame.attachEvent("onload", resizeIFrame);
        } else {
            frame.onload=resizeIFrame;
        }
        window.onresize = resizeIFrame;
        
        resizeIFrame();
    });
</script>

<%@include file="footer.jsp" %>
