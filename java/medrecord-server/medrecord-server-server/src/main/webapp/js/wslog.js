/**
 * Generic library that allows retrieving logs and events from backend servers
 * over web sockets. Pairs up with a specific server-side library. Looks for
 * ***EventUrl and ***LogUrl entries in the environment and assumes those point
 * to websocket endpoints for it to connect to.
 */
var WsLog = (function(log) {
  "use strict";
  _.mixin(_.str.exports());

  //noinspection JSDeclarationsAtScopeStart
  var wslog,
      installed = false,
      // WARNING firefox doesn't like ws:// urls on https:// pages (fair enough), so *don't enable by default*
      eventsEnabled = true, // true here to connect on page load
      logsEnabled = true, // true here to connect on page load
      
      eventSockets = {}, // "dgs": new WebSocket(eventUrls[dgs]);
      eventUrls = {}, // "dgs": "ws://" + window.location.host + "/dgs/wslog/events"
      logSockets = {}, // "dgs": new WebSocket(logUrls[dgs]);
      logUrls = {}; // "dgs" : "ws://" + window.location.host + "/dgs/wslog/logs"

  function forget() {
    eventUrls = {};
    logUrls = {};
  }

  wslog = {
    setEventUrl: function (service, url) {
      eventUrls[service] = url;
    },

    setLogUrl: function (service, url) {
      logUrls[service] = url;
    },

    init: function () {
      if (eventsEnabled) {
        this.connectEvents();
      } else {
        this.disconnectEvents();
      }

      if (logsEnabled) {
        this.connectLogs();
      } else {
        this.disconnectLogs();
      }
    },

    handleEvent: function (service, message) {
      var event;
      try {
        event = $.parseJSON(message.data);
        event.service = service;
        log.event(event);
      } catch (e) {
        console.log(_.sprintf("%s parsing event: %s:", e.name, e.message));
        console.log(message.data);
        console.log(e);
      }
    },

    handleLog: function (service, message) {
      var line = message.data,
          re = new RegExp(_.sprintf("^%s:? *", service), "i");
      line = line.replace(re, "");
      //log.log(_.sprintf("%s: %s", service.toUpperCase(), line));
      log.log(line);
    },

    createWebSocket: function (url) {
      if ("WebSocket" in window) {
        return new WebSocket(url);
      } else if ("MozWebSocket" in window) {
        //noinspection JSUnresolvedFunction
        return new MozWebSocket(url);
      } else {
        log.error("WSLOG ERROR: WebSocket not supported");
        return null;
      }
    },

    connectSockets: function (holder, urls, type, handler) {
      var wslog = this;

      $.each(urls, function (service, url) {
        var socket;
        
        if ($.inArray(holder, service) !== -1) {
          log.error(_.sprintf(
              "WSLOG: %s socket for %s already connected",
              type, service));
          return;
        }

        socket = wslog.createWebSocket(url);
        if (socket) {
          socket.onopen = function () {
            log.log(_.sprintf(
                "WSLOG: %s socket for %s opened",
                type, service));
          };
          socket.onclose = function () {
            log.log(_.sprintf(
                "WSLOG: %s socket for %s closed",
                type, service));
          };
          socket.onmessage = function (message) {
            handler(service, message);
          };
          holder[service] = socket;
        }
      });
    },

    disconnectSockets: function (holder) {
      $.each(holder, function (i, socket) {
        socket.close();
        delete holder[socket];
      });
    },

    connectEvents: function () {
      this.connectSockets(eventSockets, eventUrls, "event",
          this.handleEvent);
    },

    disconnectEvents: function () {
      this.disconnectSockets(eventSockets);
    },

    connectLogs: function () {
      this.connectSockets(logSockets, logUrls, "log",
          this.handleLog);
    },

    disconnectLogs: function () {
      this.disconnectSockets(logSockets);
    }
  };
  
  $(function () {
//    var protocol = _.startsWith(window.location.protocol, "https") ?
//            "wss" : "ws",
    var protocol = "ws",
        host = window.location.hostname,
//        port = window.location.port === "" ?
//            (protocol === "wss" ?
//                ":48123" : ":8123") :
//            ":" + window.location.port,
        port = window.location.port === "" ?
            ":8123" : ":" + window.location.port,
        basePath = _.startsWith(window.location.pathname, "/medrecord") ?
            "/medrecord/wslog" : "/wslog",
        baseUrl = _.sprintf("%s://%s%s%s", protocol, host, port, basePath),
        eventUrl = _.sprintf("%s/events", baseUrl),
        logUrl = _.sprintf("%s/logs", baseUrl);

    if (_.startsWith(window.location.protocol, "https")) {
      log.log("The websocket log that provides server logging requires direct " +
          "socket access to tomcat, rather than going through our proxy. However, " +
          "accessing tomcat directly over SSL requires a client certificate, " +
          "which you probably do not have in your web browser. Therefore, we cannot " +
          "provide the logs.\n" +
          "If you need log access to work here, you can try to use an " +
          "insecure URL: try replacing the 'https://' in the URL of the current page " +
          "with 'http://'.\n" +
          "If insecure access is not available for this medrecord " +
          "installation, see our SSL documentation at " +
          "https://zorggemak.atlassian.net/wiki/pages/viewpage.action?pageId=13074565 " +
          "for help with SSL certificates.\n" +
          "You'll likely need to connect to port 48123.");
    } else {
      console.log(eventUrl);
      console.log(logUrl);
      eventUrls["medrecord"] = eventUrl;
      logUrls["medrecord"] = logUrl;
      wslog.init();
    }
  });

  return wslog;
})(Log);
