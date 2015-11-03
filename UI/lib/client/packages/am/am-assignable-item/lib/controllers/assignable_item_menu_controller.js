define([
  'ember',
  'query',
  'am-desktop',
  '../namespace'
], function (
  Em,
  Query,
  AmDesktop,
  AmAssignableItem
) {
  'use strict';

  return Em.Controller.extend({
    actions: {
      rowClick: function(row) {
        this.send('gotoNavItem', row.get('node.id'));
      }
    },

    navTitle: 'amAssignableItem.assignableItemsListPage.navTitle'.tr(),

    selectedId: null,

    NavController: AmDesktop.AmNavController.extend({
      content: Em.computed.oneWay('parentController'),

      dataStore: function () {
        return AmAssignableItem.get('assignableItemMenuStore');
      }.property(),

      searchQuery: function() {
        return Query.Search.create({
          sort: Em.A([{
            attr: 'ordering',
            dir: 'asc'
          }])
        });
      }.property()
    }),

    navSizeController: Em.inject.controller('amAssignableNavSize'),

    navController: function() {
      return this.NavController.create({
        parentController: this,
        selectedIdBinding: 'parentController.selectedId'
      });
    }.property()
  });
});
