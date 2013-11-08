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
      if (localStorage.userid != undefined) $('#userid').val(localStorage.userid);
      if (localStorage.systemid != undefined) $('#systemid').val(localStorage.systemid);
    });
    /* ready */

    // For testing only
    function setLoginCodes() {
      localStorage.userid = $('#userid').val();
      localStorage.systemid = $('#systemid').val();
      $("#result").html("User and System id set");
    }
    /* setLoginCodes */

    function clearLoginCodes() {
      localStorage.clear();
      $('#userid').val("");
      $('#systemid').val("");
      $("#result").html("User and System id cleared");
    }
    /* clearLoginCodes */

    function defaultLoginCodes() {
      localStorage.userid = "D749ABB3-09FA-4EDC-8FE7-2FA04985EC4D";
      localStorage.systemid = "794D6B7C-7853-4812-8413-B81B3CD4934A";
      $('#userid').val(localStorage.userid);
      $('#systemid').val(localStorage.systemid);
      $("#result").html("User and System id set");
    }
    /* defaultLoginCodes */
  </script>
</head>

<body>
<h2>Set the user and system id</h2>
<font color="red">The id's are for testing only!</font><br/> <br/>

<div>
  &nbsp; UserId <input type="text" name="userid" id="userid" value="" size="50"/> <br/> &nbsp; SystemId <input
    type="text" name="systemid" id="systemid" value="" size="50"/>

  <p/>
  <input type="submit" value="Set Codes" name="Set Codes" onclick="setLoginCodes();"/> <input type="submit"
                                                                                              value="Clear Codes"
                                                                                              name="Clear Codes"
                                                                                              onclick="clearLoginCodes();"/>
  <input type="submit" value="Default Codes" name="Default Codes" onclick="defaultLoginCodes();"/>
</div>
<br/>

<div id="result"></div>
<p/>
<a href="home">Back to Homepage</a> <br/>
</body>
</html>
