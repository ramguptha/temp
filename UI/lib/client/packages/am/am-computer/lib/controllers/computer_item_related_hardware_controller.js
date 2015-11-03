define([
  'ember',
  '../namespace',
  'am-data',
  'query',
  'guid'
], function (
  Ember,
  AmComputer,
  AmData,
  Query,
  Guid
  ) {
  'use strict';

  return Em.Controller.extend({
    relatedListTitle: 'amComputer.hardwareTab.title'.tr(),

    lock: Guid.generate(),

    loadHardwareInfo: function(id) {
      var self = this;

      var query = Query.Search.create({
        context: { computerId: id }
      });

      AmData.get('stores.computerItemHardwareStore').acquire(this.get('lock'), query, function(data) {
        self.set('model', data.get('content')[0]);
      });
    }
  });
});
