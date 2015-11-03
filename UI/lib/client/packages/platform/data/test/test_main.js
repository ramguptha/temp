define([
  'testing',
  './action_test',
  './odata_test',
  './mock_test'
], function(
  Testing,
  ActionTest,
  OdataTest,
  MockTest
) {
  'use strict';

  return Testing.package('platform/data', [ActionTest, OdataTest, MockTest]);
});
