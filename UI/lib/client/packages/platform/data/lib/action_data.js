define([
  'ember',
  'formatter'
], function(
  Em,
  Formatter
) {
  'use strict';

  return {
    mergeDateAttrs: function(json, obj, names) {
      names.forEach(function(name) {
        var value = obj.get(name);
        if (value) {
          json[name] = Formatter.toUTC8601String(value);
        } else {
          json[name] = null;
        }
      });
    },

    mergeNumberAttrs: function(json, obj, names) {
      names.forEach(function(name) {
        var value = obj.get(name);
        if (value) {
          json[name] = Number(value);
        } else {
          json[name] = null;
        }
      });
    },

    mergeBooleanAttrs: function(json, obj, names) {
      names.forEach(function(name) {
        json[name] = !!obj.get(name);
      });
    }
  };
});
