define([
  'ember'
], function(
  Em
  ) {
  'use strict';

  // EnumType
  // ========
  // Class for handling enumerated objects
  return Em.Object.extend({
    // id is set in the Enumerations based on the type.
    id: null,

    // Setting the options of this enum type based on the data retrieved from the restructured model instance
    options: Em.A(),

    // Sub-classes may load options from an endpoint, in which case they should override these properties to indicate 
    // loading status.
    loadInProgress: false,
    loaded: true,
    failed: false,
    lastLoadError: null
  });
});
