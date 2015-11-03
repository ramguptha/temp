define([
  'ember',
  'logger'
], function(
  Em,
  logger
) {
  'use strict';

  // AjaxErrorDetail
  // ===============
  //
  // Encapsulates a jQuery AJAX error. See the "error" function at http://api.jquery.com/jQuery.ajax/
  return Em.Object.extend({

    // The URL that was queried,
    url: null,

    // The endPoint that was queried.
    endPoint: function() {
      return this.get('url').replace(/\?.*/, '');
    }.property('url'),

    // When the error occurred.
    failedAt: null,

    // The jQuery jqXHR object.
    jqXHR: null,

    // The HTTP status code.
    status: function() {
      return this.get('jqXHR').status;
    }.property().volatile(),

    // This is the "textStatus" parameter of the jQuery error callback.
    // It is set when the HTTP request was unable to complete at all. It will be one of:
    //
    // - timeout
    // - error
    // - abort
    // - parsererror
    //
    // See the is* methods for tests for each of the possible values.
    textStatus: null,

    isTimeout: function() {
      return this.get('textStatus') === 'timeout';
    }.property('textStatus'),

    isError: function() {
      return this.get('textStatus') === 'error';
    }.property('textStatus'),

    isAbort: function() {
      return this.get('textStatus') === 'abort';
    }.property('textStatus'),

    isParserError: function() {
      return this.get('textStatus') === 'parsererror';
    }.property('textStatus'),

    // This is the "errorThrown" parameter of the jQuery error callback. It is the textual portion
    // of the HTTP status code.
    errorThrown: null,

    // Deprecated: this the "errorThrown" parameter of the jQuery error callback.
    message: function() {
      logger.warn(['DEPRECATION WARNING: use errorThrown instead of message', this]);
      return this.get('errorThrown');
    }.property('errorThrown'),

    // The raw content returned by the endpoint.
    detail: function() {
      return this.get('jqXHR').responseText;
    }.property().volatile(),

    // If the detail property consists of JSON, this is the parsed data. Otherwise undefined.
    detailData: function() {
      try {
        return JSON.parse(this.get('detail'));
      } catch(e) {
        return undefined;
      }
    }.property('detail'),

    init: function() {
      this._super();

      this.set('failedAt', new Date());
    }
  });
});
