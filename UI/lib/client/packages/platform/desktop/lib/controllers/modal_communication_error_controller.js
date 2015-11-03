define([
  'ember',
  './modal_error_controller'
], function(
  Em,
  ModalErrorController
) {
  'use strict';

  return ModalErrorController.extend({
    error: Em.computed.oneWay('model.error'),

    errorDescription: function() {
      var attrs = null;

      switch (this.get('error.status')) {
      case 404:
        attrs = {
          title: 'Not found',
          message: 'The requested resource could not be found.'
        };
        break;
      case 500:
        attrs = {
          title: 'Internal server error',
          message: 'The server was unable to fulfill the request due to an error.'
        };
        break;
      default:
        attrs = {
          title: 'Unable to communicate with the server',
          message: 'A connection could not be established with the server, or the server did not respond in a timely manner.'
        }
      }

      return attrs;
    }.property('error'),

    title: Em.computed.oneWay('errorDescription.title'),

    message: function() {
      return this.get('error.detailData.message') || this.get('error.message');
    }.property('error.message', 'error.detailData.message'),

    requestDetail: function() {
      var error = this.get('error');
      var errorStatus = error.get('status') || error.get('lastLoadError.jqXHR.status');
      var textStatus = error.get('textStatus') || error.get('lastLoadError.jqXHR.statusText');
      return 'The server returned ' + errorStatus +
          ' ("' + textStatus + '") when the following URL was queried: ' + error.get('endPoint');
    }.property('error'),

    details: function() {
      var details = Em.A([this.get('requestDetail')]);

      var errorDescription = this.get('error.detailData.errorDescription');
      if (errorDescription) {
        details.pushObject(errorDescription);
      }

      return details;
    }.property('requestDetail', 'error.detailData.errorDescription'),

    resolution: null
  });
});
