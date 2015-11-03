define([
  'ember',
  './data_store_base',
  './static_data_store',
  'logger'
], function (
  Em,
  DataStoreBase,
  StaticDataStore,
  logger
) {
  'use strict';

  // A DataStore manages singleton instances of all local copies for a single endPoint
  return DataStoreBase.extend({
    createStaticDataStore: function (content) {
      return StaticDataStore.create({
        Model: this.get('Model'),
        Spec: this.get('Spec'),
        content: content
      });
    }
  });
});
