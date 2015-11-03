define([
  'jquery',
  './lib/base',
  './lib/ajax_error_detail'
], function(
  $,
  AjaxBase,
  AjaxErrorDetail
) {
  'use strict';

  return {
    AjaxErrorDetail: AjaxErrorDetail,

    createJson: function(code, json, callback) {
      return this.createStateMachine({
        default: {
          '*': {
            '*': { code: code, body: json, callback: callback }
          }
        }
      });
    },

    createStateMachine: function(states) {
      return $.extend({}, AjaxBase, {
        currentStateName: 'default',

        states: states,

        invoke: function(verb, contentType, desc, url, vars, okHandler, errHandler) {
          var state = this.states[this.currentStateName];
          var responseByVerb = state[url] || state['*'];
          var response = responseByVerb[verb] || responseByVerb['*'];

          setTimeout(function() {
            if (response.code < 400) {
              okHandler(response.body);
            } else {
              errHandler(AjaxErrorDetail.create({
                url: url,
                verb: verb,
                detail: response.body
              }));
            }

            if (response.callback) {
              response.callback(this, verb, contentType, desc, url, vars, response);
            }
          }, 0);
        }
      });
    }
  };
});
