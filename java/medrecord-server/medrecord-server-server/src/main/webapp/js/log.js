/**
 * Helper for logging to console.log as well as to a HTML
 * logging console embedded in the web page.
 */
var Log = (function() {
  "use strict";
  _.mixin(_.str.exports());

  //noinspection JSDeclarationsAtScopeStart
  var log,
      list = (function () {
        // little temp object to allow logging before page is loaded
        return {
          cache:  [],
          append: function (msg) {
            this.cache.push(msg);
          },
          scrollTop: function () {}
        };
      })();

  function scroll() {
    list.scrollTop(list.prop('scrollHeight'));
  }

  function formatEventType(event) {
    //noinspection JSUnresolvedVariable
    return event.subjectType;
  }

  function formatEventSubject(event) {
    //noinspection JSUnresolvedVariable
    return event.subject;
  }

  log = {
    /**
     * Log the specified message.
     * @param {*} msg the string or object to log.
     * @param {string} [type] the log type
     */
    log: function (msg, type) {
      if (msg === null || msg === undefined) {
        return;
      }
      if (typeof msg === 'object') {
        try {
          msg = JSON.stringify(msg, null, 4);
        } catch(err) {
          // log the object...
        }
      }
      if (type === null) {
        type = "info";
      }
      console.log(msg);
      list.append(_.sprintf(
          '<li><span class="logLine type">%s</span></li>', _.escape(msg)));
      scroll();
    },

    /**
     * Log the specified message as an error and show the in-page console.
     * @param msg the string or object to log
     */
    error: function (msg) {
      this.log(msg, "error");
    },

    /**
     * Log the specified event object (see wslog for expected form).
     * @param event the event object to log.
     */
    event: function (event) {
      list.append(
          '<li>' +
              _.sprintf('<span class="logService">%s</span>',
                  _.escape(event.service.toUpperCase())) +
              _.sprintf('<span class="logType">%s</span>',
                  _.escape(event.type)) +
              _.sprintf('<span class="logSubjectType">%s</span>',
                  _.escape(formatEventType(event))) +
              _.sprintf('<span class="logSubject">%s</span>',
                  _.escape(formatEventSubject(event))) +
              _.sprintf('<span class="logDetail">%s</span>',
                  _.escape(event.detail)) +
              '</li>'
      );
      scroll();
    }

  };

  $(function () {
    var newList = $("#logList");

    // replace temp log cache with real deal and flush log
    $.each(list.cache, function (i, msg) {
      newList.append(msg);
    });
    list = newList;
  });

  return log;
})();
