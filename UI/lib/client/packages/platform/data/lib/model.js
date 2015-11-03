define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Object.extend({
    // The spec for the model
    Spec: null,

    // When a model is loaded from an endpoint, the loadedAt timestamp is updated.
    loadedAt: null,

    // Model attributes in a format suitable for data manipulation
    data: null,

    // Model attributes in a format suitable for presentation
    presentation: null,

    // Reference to the owning dataStore. Set on instantiation.
    dataStore: null,

    // NOTE NOTE NOTE: whenever working with id's, expect to get a STRING
    id: function() {
      return this.get('Spec.idNames').map(function(name) { return this.get('data.' + name); }, this).join(':');
    }.property('data', 'data.id'),

    // UI automation implementors sometimes require a different id property to be used. For example, ESN instead
    // of deviceId.
    automationId: function() {
      return this.get('id');
    }.property('id'),

    // Every model instance has a name.
    name: function() {
      return this.get('presentation.name');
    }.property('presentation', 'presentation.name')
  });
});
