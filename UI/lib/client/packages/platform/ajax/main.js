define([
  'ember',
  'packages/platform/send-ember-action',

  './lib/base',
  './lib/ajax_error_detail',
  'logger'
], function(
  Em,
  sendEmberAction,

  AjaxBase,
  AjaxErrorDetail,
  logger
) {
  'use strict';

  var logDebug = true;

  return Em.$.extend({
    AjaxErrorDetail: AjaxErrorDetail,

    invoke: function(verb, contentType, desc, url, vars, okHandler, errHandler, headers) {
      var self = this;

      verb = verb.toUpperCase();

      if (logDebug) {
        logger.log('AJAX ' + verb + ' (start: ' + desc + ')', [url, vars]);
      }

      sendEmberAction('ajaxInvoked', verb, contentType, desc, url, vars, okHandler, errHandler, headers);

      var request = Em.$.ajax(Em.$.extend(contentType ? { contentType: contentType } : {}, {
        url: url,
        data: vars,
        dataType: 'json',
        type: verb,
        timeout: 120000,
        cache: false,
        headers: headers
      }));

      request.done(function(rsp) {
        if (logDebug) {
          logger.log('AJAX ' + verb + ' (success: ' + desc + ')', [url, vars, rsp]);
        }

        sendEmberAction('ajaxSucceeded', verb, contentType, desc, url, vars, okHandler, errHandler, headers, rsp);

        if (typeof okHandler === 'function') {
          okHandler(rsp);
        }
      });

      request.fail(function(jqXHR, textStatus, errorThrown) {
        if (logDebug) {
          logger.log('AJAX ' + verb + ' (failed: ' + desc + ')', [url, vars, jqXHR, textStatus, errorThrown]);
        }

        var detail = self.AjaxErrorDetail.create({
          url: url,
          verb: verb,

          jqXHR: jqXHR,
          textStatus: textStatus,
          errorThrown: errorThrown
        });

        sendEmberAction('ajaxFailed', verb, contentType, desc, url, vars, okHandler, errHandler, headers, detail);

        if (typeof errHandler === 'function') {
          errHandler(detail);
        }
      });

      return request;
    }
  }, AjaxBase);
});
