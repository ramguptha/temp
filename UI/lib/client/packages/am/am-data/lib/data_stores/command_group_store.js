define([
  'ember',
  'locale',
  'packages/platform/data',
  '../models/command_group',
  '../specs/command_group_spec'
], function(
  Em,
  Locale,
  AbsData,
  CommandGroup,
  CommandGroupSpec
) {
  'use strict';

  return AbsData.get('MockDataStore').extend({
    Model: CommandGroup,
    Spec: CommandGroupSpec,
    MockData: [
      { id: 'computers', name: 'amData.commandGroupStore.computerLabel', endPointName: 'computer' },
      { id: 'mobile_devices', name: 'amData.commandGroupStore.mobileDeviceLabel', endPointName: 'mobile' }
    ].map(function(item){
        return {
          id: item.id,
          name: function () {
            return Locale.renderGlobals(item.name).toString();
          }.property(),
          endPointName: item.endPointName }
      })
  });
});
