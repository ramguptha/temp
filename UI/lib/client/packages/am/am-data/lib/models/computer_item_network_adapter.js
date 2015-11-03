define([
  'ember',
  'packages/platform/data',
  '../specs/computer_item_network_adapter_spec'
], function(
  Em,
  AbsData,
  ComputerNetworkadapterSpec
) {
  'use strict';

  return AbsData.get('Model').extend({
    Spec: ComputerNetworkadapterSpec
  });
});
