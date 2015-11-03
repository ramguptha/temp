define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: 'customFieldStore'.w(),
    refreshDelay: 2500,

    endPoint: function() {
      var customFieldId = this.get('customFieldId');
      return customFieldId ? 'customfields/' + customFieldId : 'customfields/';
    }.property('content.id'),

    verb: 'post',

    customFieldId: null,
    fieldName: null,
    description: null,
    variableName: null,
    dataType: null,
    displayType: null,
    enumerationList: null,

    toJSON: function() {
      return {
        name: this.get('fieldName'),
        description: this.get('description'),
        variableName: this.get('variableName'),
        dataType: this.get('dataType'),
        displayType: this.get('displayType'),
        enumerationList: this.get('enumerationList')
      }
    }
  });
});
