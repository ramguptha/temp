define([
  'ember'
], function(
  Em
  ) {
  'use strict';

  // IpType
  // ========
  // Class for handling ip addresses
  // As of now, only IPv4 addresses are handled
  return Em.Object.extend({
    type: 'IPv4'
  });
});