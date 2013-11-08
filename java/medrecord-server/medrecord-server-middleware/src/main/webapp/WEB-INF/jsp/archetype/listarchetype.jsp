<%@page contentType="text/html" pageEncoding="UTF-8" session="false" %>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@include file="../header.jsp" %>

<script type="text/javascript" charset="utf-8">
  function getArchtypes() {
    $("#result").html("Loading data...");
    setUserId(localStorage.userid);
    setSystemId(localStorage.systemid);
    listArchetypes($("#regexpr").val(), setResult);
  }

  function getArchetypeData(archid) {
    $("#result").html("Loading data...");
    setUserId(localStorage.userid);
    setSystemId(localStorage.systemid);
    retrieveArchetype(archid, false, setArchData);
  }

  function setArchData(data) {
    if (data.errorcode != 0) {
      getLastError(data.reqid, setError);
    }
    else {
      $("#result").html("Result of call:<pre>" + data.result + "</pre>");
    }
  }
</script>

<h2>Call web API function 'listArchetypes'</h2>

<div class="docpanel">
  <table xmlns="http://www.w3.org/1999/xhtml" cellspacing="0" class="sites-layout-name-one-column sites-layout-hbox">
    <tbody>
    <tr>
      <td class="sites-layout-tile sites-tile-name-content-1">
        <div dir="ltr"><a name="top"></a>

          <div dir="ltr">
            <table border="1" bordercolor="#888888" cellpadding="5" cellspacing="0"
                   style="border-color:rgb(136,136,136);border-width:1px;border-collapse:collapse" width="100%">
              <tbody>
              <tr>
                <td align="left" bgcolor="#cccccc" style="height:20px" valign="middle">
                  <b>Description</b>
                </td>
              </tr>
              <tr>
                <td style="height:18px">
                  <div class="sites-codeblock sites-codesnippet-block"><code>

                    function listArchetypes(regexpr, callback)

                  </code></div>

                  Returns an array with archetype id's in the ZorgGemak openEHR kernel.<br><br>Can also be directly
                  called as GET (REST) request:<br><code style="color:rgb(0,96,0)">http://&lt;middlewareserver&gt;/middleware/listarchetypes</code><br>Parameters:<span>&nbsp;&nbsp; &nbsp;</span><code>regexpr</code><span><code>
                  &nbsp;&nbsp; &nbsp;<br></code>Return:<span>&nbsp;&nbsp; &nbsp;<span>&nbsp;&nbsp; &nbsp;<span>&nbsp; &nbsp;<code>the
                  data object in JSON format</code></span></span></span></span></td>
              </tr>
              </tbody>
            </table>
            <br>
            <table border="1" bordercolor="#888888" cellpadding="5" cellspacing="0"
                   style="border-color:rgb(136,136,136);border-width:1px;border-collapse:collapse" width="100%">
              <tbody>
              <tr>
                <td bgcolor="#cccccc" style="vertical-align:top;width:537px;height:32px">
                  <b>Parameters</b></td>
              </tr>
              <tr>
                <td style="vertical-align:top;width:537px;height:18px">regexpr<span>&nbsp; &nbsp; A reguliere expressie or 'ALL' to get all archetypes in the kernel</span>&nbsp;&nbsp;<br>callback
                  &nbsp; A callback function returning an object with the data and an error code
                </td>
              </tr>
              </tbody>
            </table>
            <br>
            <table border="1" bordercolor="#888888" cellpadding="5" cellspacing="0"
                   style="border-color:rgb(136,136,136);border-width:1px;border-collapse:collapse" width="100%">
              <tbody>
              <tr>
                <td bgcolor="#cccccc" style="width:537px;height:32px">
                  <b>Return values</b>
                </td>
              </tr>
              <tr>
                <td style="width:537px;height:18px">The callback returns an object with the data and an error code<br>

                  <div class="sites-codeblock sites-codesnippet-block"><code>object = {</code><br><code> <span>&nbsp;&nbsp; &nbsp;</span>result:
                    &nbsp; &nbsp;data, // The result data</code><br><code> <span>&nbsp;&nbsp; &nbsp;</span>errorcode:
                    code &nbsp;// The error code</code><br><code>}</code></div>
                  <br>The result data is an string array with archetype id's
                </td>
              </tr>
              </tbody>
            </table>
            <br>
            <table border="1" bordercolor="#888888" cellpadding="5" cellspacing="0"
                   style="border-color:rgb(136,136,136);border-width:1px;border-collapse:collapse" width="100%">
              <tbody>
              <tr>
                <td bgcolor="#cccccc" style="width:537px;height:32px">
                  <b>Examples</b>
                </td>
              </tr>
              <tr>
                <td style="width:537px;height:18px">
                  <div class="sites-codeblock sites-codesnippet-block"><code>function getArchtype()</code><br><code>
                    {</code><br><code> <span>&nbsp;&nbsp; &nbsp;</span>$("#result").html("Loading
                    data...");</code><br><code><span>&nbsp;&nbsp; &nbsp;</span>listArchetypes($("#regexpr").val(),
                    setResult);</code><br><code> } /* getArchtype */</code><br><br><code> function
                    setResult(data)</code><br><code> {</code><br><code> <span>&nbsp;&nbsp; &nbsp;</span>if
                    (data.errorcode != 0)</code><br><code> <span>&nbsp;&nbsp; &nbsp;</span>{</code><br><code> <span>&nbsp;&nbsp; &nbsp;<span>&nbsp;&nbsp; &nbsp;</span></span>//
                    Handle the error</code><br><code> <span>&nbsp;&nbsp; &nbsp;</span>}</code><br><code> <span>&nbsp;&nbsp; &nbsp;</span>else</code><br><code>
                    <span>&nbsp;&nbsp; &nbsp;</span>{</code><br><code> <span>&nbsp;&nbsp; &nbsp;<span>&nbsp;&nbsp; &nbsp;</span></span>$("#result").html("Result
                    of call:&lt;pre&gt;" + data.result + "&lt;/pre&gt;");</code><br><code>
                    <span>&nbsp;&nbsp; &nbsp;</span>}</code><br><code> } /* setResult */</code></div>
                </td>
              </tr>
              </tbody>
            </table>
          </div>
        </div>
      </td>
    </tr>
    </tbody>
  </table>
</div>

<div>
  &nbsp; Reg. Expr. <input type="text" name="regexpr" id="regexpr" value="" size="50"/> <br/>&nbsp;<i>Note: Input 'ALL'
  or keep empty for list of all archetypes in ZorgGemak kernel.</i> <br/>&nbsp;<i>Note: Regular expressions match whole
  names only, as if starting with ^ and ending with $. Prefix and postfix with <code>.*?</code> to search for
  substrings, for example <code>.*?ADDRESS\.address.*?</code></i>

  <p/>
  <input type="submit" value="Get Result" name="GetResult" onclick="getArchtypes();"/>
</div>

<%@include file="../footer.jsp" %>
