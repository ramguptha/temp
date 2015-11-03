define([
  'ember',
  'ui'
], function(
  Em,
  UI
) { 
  'use strict';

  return Em.Object.create({
    // Checks whether the devices have identical fields
    isIdenticalDeviceFields: function (devices, fields) {
      var isTheSame = false;
      for (var i = 0; i < devices.length - 1; i++) {
        if (i < (devices.length - 1)) {
          var dev = devices[i], devNext = devices[i + 1];
          for (var f = 0; f < fields.length; f++) {
            if (dev.get(fields[f]) == devNext.get(fields[f])) {
              isTheSame = true;
            } else {
              return false;
            }
          }
        }
      }
      return isTheSame;
    }
  }); 
});
