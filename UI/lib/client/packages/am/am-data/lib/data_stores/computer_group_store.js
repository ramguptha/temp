define([
  'ember',
  'packages/platform/data',
  '../models/computer_group',
  '../specs/computer_group_spec',

  'locale'
], function(
  Em,
  AbsData,
  ComputerGroup,
  ComputerGroupSpec,

  Locale
) {
  'use strict';

  return AbsData.get('MockDataStore').extend({
    Model: ComputerGroup,
    Spec: ComputerGroupSpec,
    MockData: [
      { id: 1, name: 'amData.computerGroupStore.allComputers', endPointName: 'all' },
      { id: 2, name: 'amData.computerGroupStore.allPCs', endPointName: 'pconly' },
      { id: 3, name: 'amData.computerGroupStore.allMACs', endPointName: 'maconly' }
    ].map(function(item){
        return {
          name: function () {
            return Locale.renderGlobals(item.name).toString();
          }.property(),
          id: item.id,
          endPointName: item.endPointName
        }
      })
  });
});
