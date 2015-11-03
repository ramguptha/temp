define([], function() {
  'use strict';

  // resolve(resources, path, isLocalizing)
  // ======================================
  // 
  // This is the core lookup implementation. Given resources and a path, it will return the resource at that path.
  //
  // If the resource at the given path is a string, resolve will compile it to a Handlebars template, in place, before
  // returning it.
  //
  // If isLocalizing is truthy, resolve will return the path instead.
  //
  // If the path refers to a non-existent resource, resolve will return undefined.
  var resolve = function(resources, path, isLocalizing) {
    if (isLocalizing) {
      return path;
    }

    var findAndCompile = function(namespace, path) {
      if (!namespace) {
        return undefined;
      }

      var idx = path.indexOf('.');
      if (-1 === idx) {

        // This is the last step on the path, examine the corresponding value.
        var value = namespace[path];
        if (!value) {

          // Path not found.
          return undefined;
        } else if (value.ref) {

          // This is a ref key - look that up instead.
          return resolve(resources, value.ref, isLocalizing);
        } else {

          // Compile on demand
          if ('string' === typeof(value)) {
            value = Handlebars.compile(value);
            namespace[path] = value;
          }

          return value;
        }
      } else {

        // Strip off the top slug from the path and recurse into the resources.
        return findAndCompile(namespace[path.slice(0, idx)], path.slice(idx + 1));
      }
    };

    // some of our translated strings are stored as objects with 'value' and 'comment' properties, so lets make sure we extract the value here
    var translation = findAndCompile(resources, path);

    return typeof(translation) === 'object' && !Em.isNone(translation.value) ? translation.value : translation;
  };

  return resolve;
});
