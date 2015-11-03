define([
  'ember',
  '../am_spec',
  '../am_spec_behaviour',

  '../am_formats'
], function(
  Em,
  AmSpec,
  AmSpecBehaviour,

  Format
  ) {
  'use strict';

  return AmSpecBehaviour.extend({
    idNames: ['CommandID'],

    format: {
      CommandID: Format.ID,
      Command:  Format.String,
      DeviceType: Format.String,
      Enabled: Format.Boolean
    },

    resource: [
      {
        attr: 'CommandID',
        sourceAttr:'CommandID',
        type: Number
      },
      {
        attr: 'Command',
        sourceAttr:'Command',
        type: String
      },
      {
        attr: 'DeviceType',
        sourceAttr:'DeviceType',
        type: String
      },
      {
        attr: 'Enabled',
        sourceAttr:'Enabled',
        type: Number
      }
    ],

    mapRawResultSetData: function(query, rawData) {
      var transformedNames = rawData ? rawData.map(function(rawValue){
        return {
          CommandID: parseInt(rawValue.CommandID),
          Command: rawValue.Command,
          DeviceType: rawValue.DeviceType,
          Enabled: parseInt(rawValue.Enabled)
        };
      }) : null;

      return this._super(query, transformedNames ? transformedNames : []);
    }
  }).create();
});
