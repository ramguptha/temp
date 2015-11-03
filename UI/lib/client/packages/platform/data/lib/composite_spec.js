define([
  'jquery',
  'ember',
  './spec'
], function(
  $,
  Em,
  Spec
) {
  'use strict';

  // CompositeSpec
  // =============
  //
  // A CompositeSpec is a spec composed of other specs.
  return Spec.extend({
    // Holds the backing specs, by name. This property, along with the attributeMapping describe the CompositeSpec.
    backingSpecs: {},

    // An array of { name: 'attribute name', map: 'backingSpecsKey.attributeName' } objects. Mapping objects
    // also may be written as { name: 'attribute name', map: 'backingSpecsKey' }, in which case the attribute
    // name is assumed to be the same in the backingSpec.
    attributeMapping: Em.A(),

    format: function() {
      var specs = this.get('backingSpecs');
      var mapping = this.get('attributeMapping');

      var formats = {};
      Em.A(mapping).forEach(function(mapSpec) {
        var attr = mapSpec.name;

        var mapComponents = mapSpec.map.split('.');
        var specName = mapComponents[0];
        var specAttr = mapComponents.length > 1 ? mapComponents[1] : attr;
        var format = specs[specName].get('format')[specAttr];

        formats[attr] = format;
      });

      return formats;
    }.property('backingSpecs', 'attributeMapping.[]'),

    resource: function() {
      var specs = this.get('backingSpecs');
      var resources = {};

      return this.get('attributeMapping').map(function(mapSpec) {
        var attr = mapSpec.name;

        var mapComponents = mapSpec.map.split('.');
        var specName = mapComponents[0];
        var specAttr = mapComponents.length > 1 ? mapComponents[1] : attr;

        var resourceByName = resources[specName];
        if (undefined === resourceByName) {
          resourceByName = resources[specName] = specs[specName].get('resourceByName');
        }

        Em.assert('Resource is set', resourceByName);
        var spec = resourceByName[specAttr];
        Em.assert('Spec is set', spec);

        return $.extend({}, spec, { attr: attr });
      });
    }.property('backingSpecs', 'attributeMapping.[]')
  });
});
