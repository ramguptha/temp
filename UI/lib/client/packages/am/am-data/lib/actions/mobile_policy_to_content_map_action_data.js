define([
  'jquery',
  'ember',
  'formatter'
], function(
  $,
  Em,
  Formatter
) {
  'use strict';

  return {
    ContentAvailabilityDateRange: Em.Object.extend({
      startTime: null,
      endTime: null,
      availabilitySelector: null,

      toJSON: function() {
        return {
          startTime: this.get('startDate'),
          endTime: this.get('endDate'),
          availabilitySelector: this.get('availabilitySelector')
        };
      }
    }),

    PolicyAssignment: Em.Object.extend({
      policyId: null,
      assignmentType: null,
      availability: null,
      availabilitySelector: null,

      toJSON: function() {
        var attrs = this.getProperties('policyId assignmentType availability availabilitySelector'.w());

        var json = {
          policyId: attrs.policyId,
          assignmentType: Number(attrs.assignmentType),
          availabilitySelector: attrs.availabilitySelector
        };

        if (attrs.availability) {
          $.extend(json, attrs.availability.toJSON());
        }

        return json;
      }
    })
  };
});
