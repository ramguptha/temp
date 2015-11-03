define([
  'ember'
], function(
  Em
) {
  'use strict';

  return Em.Mixin.create({
    // @override
    mapRawResultSetDataFromSchema: function(query, schema, rows) {
      var store = this.get('query.store');

      // Build a mapping from mapped attr index => row attr index
      var mappedAttributes = this.get('resource');

      var attrMapping = mappedAttributes.reduce(function(mapping, mappingSpec, mappingSpecIdx) {
        var guid = mappingSpec.guid.toLowerCase();
        var mappedIdx = undefined;

        schema.forEach(function(colSpec, i) {
          if (colSpec['InfoItemID'].toLowerCase() === guid) {
            mappedIdx = i;
          }
        });

        if (undefined !== mappedIdx) {
          mapping[mappingSpecIdx] = mappedIdx;
        }

        return mapping;
      }, {});

      // Apply our mapping to each row in the raw data
      return rows.map(function(raw, i) {
        var attrs = {};

        mappedAttributes.forEach(function(mappingSpec, i) {
          var attrName = mappingSpec.attr;
          var attrType = mappingSpec.type;
          var attrIdx = attrMapping[i];
          if (typeof(raw[attrIdx]) !== 'undefined') {
            attrs[attrName] = Date === attrType ? new Date(raw[attrIdx]) : raw[attrIdx];
          }
        });

        return attrs
      });
    },

    // @override
    mapRawResultSetData: function(query, rawData) {
      return this.mapRawResultSetDataFromSchema(query, rawData.metaData.columnMetaData, rawData.rows);
    },

    // @override
    mapRawSingletonData: function(query, rawData) {
      return this.mapRawResultSetData(query, rawData);
    },

    // @override
    mapRawCounterData: function(query, rawData) {
      return rawData.metaData.totalRows;
    }
  });
});
