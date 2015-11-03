define([
  'ember',
  'packages/platform/data'
], function(
  Em,
  AbsData
) {
  'use strict';

  return AbsData.get('DataStore');
});
