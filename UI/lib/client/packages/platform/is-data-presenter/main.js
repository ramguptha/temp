define([
  'ember',
  'locale',
  'formatter',
  'packages/platform/aggregate'
], function(
  Em,
  Locale,
  Formatter,
  Aggregate
) {
  'use strict';

  // Is Data Presenter
  // =================
  //
  // A Mixin for Presenter sub-classes, to facilitate rendering of data model fields and labels.
  return Em.Mixin.create({
    OneToOne: Aggregate.OneToOne,
    OneToMany: Aggregate.OneToMany,

    spec: null,
    labelResource: null,

    // True if the resource for the id is a 1-1
    isOneToOne: function() {
      var id = this.get('id');
      var spec = this.get('spec');
      var resourceByName = this.get('spec.resourceByName');

      return id && resourceByName && this.OneToOne.detectInstance(resourceByName[spec.stripName(id)].type);
    }.property('id', 'spec.resourceByName'),

    // True if the resource for the id is a 1-N
    isOneToMany: function() {
      var id = this.get('id');
      var spec = this.get('spec');
      var resourceByName = this.get('spec.resourceByName');

      return id && resourceByName && this.OneToMany.detectInstance(resourceByName[spec.stripName(id)].type);
    }.property('id', 'spec.resourceByName'),

    // True if there any 1-1 aggregates in the path of the id
    hasOneToOne: function() {
      return this.detect(this.get('id'), this.get('spec'), this.get('spec.resourceByName'), this.OneToOne);
    }.property('id', 'spec.resourceByName'),

    // True if there any 1-N aggregates in the path of the id
    hasOneToMany: function() {
      return this.detect(this.get('id'), this.get('spec'), this.get('spec.resourceByName'), this.OneToMany);
    }.property('id', 'spec.resourceByName'),

    // Returns true if any of the parts of the given name is an instance of klass.
    detect: function(name, spec, resourceByName, klass) {
      var detected = false;

      if (name && spec && resourceByName) {
        var nameSlugs = spec.splitName(name);

        // If there are any one-to-many aggregates in the path of the name, the column is not sortable
        for (var i = 0; !detected && i < nameSlugs.length; i++) {
          var subName = nameSlugs.slice(0, i + 1).join('.');
          var resource = resourceByName[subName];

          detected = this.OneToMany.detectInstance(resource.type);
        }
      }

      return detected;
    },

    renderLabel: function() {
      var name = this.get('id');
      var label = this.get('label');
      var resourceByName = this.get('spec.resourceByName');

      // If we don't have a label and the field is of type Aggregate OneToOne
      // Use the child label if there is any custom one,
      // otherwise combine parent + child
      if (resourceByName) {
        var spec = this.get('spec');
        var nameSlugs = spec.splitName(name);

        var resource = resourceByName[nameSlugs[0]],
          isAggregateOneToOne = this.OneToOne.detectInstance(resource.type);

        //see if the column name contains a digit between periods. This is a nested collection column.
        var parsedIndexArray = name.match(/\.(\d+)\./);

        if ((Em.isNone(label) && isAggregateOneToOne) || parsedIndexArray) {
          var parent = nameSlugs[0];
          var child = nameSlugs[1];
          var childSpec = resource.type.Spec;
          var subFormatOption = childSpec.get('format.' + child);

          if (subFormatOption) {
            labelResource = subFormatOption.labelResource;

            if (!Em.isNone(labelResource)) {
              label = Locale.renderGlobals(labelResource).toString();
            } else if (subFormatOption.label) {
              label = subFormatOption.label;
            } else if (parsedIndexArray){
              //nested collection label
              label = Formatter.camelCaseToTitleCase(Formatter.singularizeNestedLabels(parent)) + ' ' + parsedIndexArray[1] + ' - ' + Formatter.camelCaseToTitleCase(child);
            }
            else {
              label = Formatter.camelCaseToTitleCase(parent) + ' ' + Formatter.camelCaseToTitleCase(child);
            }
          }
        }
      }

      // If we don't have a label yet, try to get it via labelResource.
      if (Em.isNone(label)) {
        var labelResource = this.get('labelResource');

        if (!Em.isNone(labelResource)) {
          label = Locale.renderGlobals(labelResource).toString();
        }
      }

      // If we don't have a label yet, try to get it via format.
      if (spec && Em.isNone(label)) {
        var formatOptions = spec.get('format.' + spec.stripName(name));

        if (formatOptions) {
          labelResource = formatOptions.labelResource;

          if (!Em.isNone(labelResource)) {
            label = Locale.renderGlobals(labelResource).toString();
          } else {
            label = formatOptions.label;
          }
        }
      }

      // If we _still_ don't have a label, mangle the name into something (hopefully) reasonable.
      if (Em.isNone(label)) {
        label = Formatter.camelCaseToTitleCase(name);
      }

      return Em.Handlebars.Utils.escapeExpression(label);
    },

    // Optional valueFormatter(value, model)
    valueFormatter: null,

    renderValue: function(nodeData) {
      var model = nodeData;
      var rendered = null;
      var path = this.get('id');

      var formatter = this.get('valueFormatter');
      if (formatter) {
        var modelData = Em.get(model, 'data');
        var valueData = Em.get(model, 'data.' + path);

        rendered = (formatter && valueData && modelData) ? formatter(valueData, modelData) : '';
      } else {
        rendered = Em.get(model, 'presentation.' + path);
      }

      return rendered;
    },

    width: function() {
      var name = this.get('id');
      var formatOptions = this.getFormatOptions(this.get('spec'), name);

      if (!Em.isNone(formatOptions)) {
        var format = formatOptions.format || formatOptions;
        return format.width;
      }
    }.property('id', 'spec'),

    getFormatOptions: function(spec, name) {
      var formatOptions, traverseList, resourceList;
      var isCompoundObject = /(\w|\d)+(\.(\w|\d)+)+/.test(name);

      if (!isCompoundObject) {
        formatOptions = this.get('spec.format.' + name);
      } else {
        resourceList = spec.resource;
        traverseList = name.split(/\./);

        resourceList.forEach(function(resource, index) {
          if (traverseList[0] === resource.attr) {
            formatOptions = resource.type.get('Spec.format.' + traverseList[1]);
          }
        });
      }

      return formatOptions;
    },

    isSortable: Em.computed.not('hasOneToMany')
  });
});
