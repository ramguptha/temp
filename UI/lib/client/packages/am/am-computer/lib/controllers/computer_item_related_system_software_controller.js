define([
  'ember',
  '../namespace',
  'am-data',
  'query',
  'guid',
  'formatter',
  'am-computer-formatter'
], function (
  Ember,
  AmComputer,
  AmData,
  Query,
  Guid,
  Formatter,
  AmComputerFormatter
  ) {
  'use strict';

  return Em.Controller.extend({
    relatedListTitle: 'amComputer.systemSoftwareTab.title'.tr(),

    lock: Guid.generate(),

    loadSoftwareInfo: function(id) {
      var self = this;

      var query = Query.Search.create({
        context: { computerId: id }
      });

      AmData.get('stores.computerItemSystemSoftwareStore').acquire(this.get('lock'), query, function(data) {
        self.set('model', data.get('content')[0]);
      });
    },

    computerUpTimeCalculated: function() {
      // This is a number type, can be null
      var computerUpTime = this.get('model.data.computerUpTime'),computerBootTime = this.get('model.data.computerBootTime');

      return computerUpTime ? AmComputerFormatter.formatIntervalInSecsCombined(computerUpTime) : AmComputerFormatter.formatIntervalInSecsFromCurrentDate(computerBootTime) ;
    }.property('model.data.computerUpTime', 'model.data.computerBootTime')

  });
});
