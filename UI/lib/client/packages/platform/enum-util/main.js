define([
  'ember',
], function(
  Em
) {
  'use strict';

  var buildMapper = function(param) {
    if (null === param) {
      return function(item) { return item; };
    } else if ('string' === typeof(param)) {
      return function(item) {
        return !Em.isNone(item[param]) ? item[param] : item.get(param);
      };
    } else if ('object' === typeof(param)) {
      return param;
    }
  };

  return {

    intersect: function(inputArray, inputIdMapper, intersectedArray, intersectedIdMapper) {
      if (null === intersectedArray || undefined === intersectedArray) {
        return inputArray;
      }

      inputIdMapper = buildMapper(inputIdMapper);
      intersectedIdMapper = buildMapper(intersectedIdMapper);

      var selectedDevices = {};

      intersectedArray.forEach(function(item) {
        selectedDevices[intersectedIdMapper(item)] = true;
      });

      return inputArray.filter(function(item) {
        if (inputIdMapper(item) in selectedDevices) {
          return true;
        }
      });
    },

    exclude: function(inputArray, inputIdMapper, excludedArray, excludedIdMapper) {
      if (null === excludedArray || undefined === excludedArray) {
        return inputArray;
      }

      inputIdMapper = buildMapper(inputIdMapper);
      excludedIdMapper = buildMapper(excludedIdMapper);

      var existingDevices = {};

      excludedArray.forEach(function(item) {
        existingDevices[excludedIdMapper(item)] = true;
      });

      return inputArray.filter(function(item) {
        if (!(inputIdMapper(item) in existingDevices)) {
          return true;
        }
      });
    }
  }
});



