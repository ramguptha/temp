define([
  'ember-core',
  './resolve_globals',
  './render'
], function(
  Em,
  resolveGlobals,
  render
) {
  'use strict';

  // translated(options)
  // -------------------
  //
  // Create a dynamic property which returns a rendered resource template as a Handlebars SafeString.
  //
  // options:
  //
  // - resource: the path to the resource to render
  // - property: get the path to the resource to render from the given property
  // - deps: names of dependent properties. These will be used as the context for the template at render time.
  return function(options) {
    if ('object' !== typeof(options)) {
      throw ['Expected a javascript object', options];
    }

    var propertyNames = options.deps || [];
    var deps = Em.copy(propertyNames);
    deps.push('App.isLocalizing');

    if (options.property) {
      var propertyPath = options.property;
      deps.push(propertyPath);

      var propertyFunction = function() {
        var resourcePath = this.get(propertyPath);
        if (Em.isEmpty(resourcePath)) {
          return null;
        }

        var resource = resolveGlobals(resourcePath);

        var properties = this.getProperties(propertyNames);
        return render(resource, properties);
      };

      return propertyFunction.property.apply(propertyFunction, deps);
    } else if (options.resource) {
      var resourcePath = options.resource;

      var propertyFunction = function() {
        var resource = resolveGlobals(resourcePath);
        var properties = this.getProperties(propertyNames);
        return render(resource, properties);
      };

      return propertyFunction.property.apply(propertyFunction, deps);
    }
  }
});
