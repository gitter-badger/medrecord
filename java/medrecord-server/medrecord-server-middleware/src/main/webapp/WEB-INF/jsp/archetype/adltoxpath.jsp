<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="../header.jsp" %>

<script type="text/javascript" charset="utf-8">
  function performAdlToXpath() {
    $("#result").html("Loading data...");
    setUserId(localStorage.userid);
    setSystemId(localStorage.systemid);
    adlToXpath($("#adl").val(), setResult);
  }
</script>

<h2>Call web API function 'adlToXpath'</h2>
Is passing the call to the openEHR kernel SOAP API ArchetypeService.adlToXpath<br/> <br/>

<div>
  &nbsp; Adl <input type="text" name="adl" id="adl" value="" size="50"/>

  <p></p>
  <input type="submit" value="Get Result" name="GetResult" onclick="performAdlToXpath();"/>
</div>

<%@include file="../footer.jsp" %>
