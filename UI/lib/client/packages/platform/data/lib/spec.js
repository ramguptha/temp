define([
  'jquery',
  'ember',
  'formatter',
  'packages/platform/aggregate',
  'packages/platform/enum-type'
], function(
  $,
  Em,
  Formatter,
  Aggregate,
  EnumType
) {
  // TODO: 'use strict'; is not here - why?

  var escapeHtml = function(val) {
    val = Em.Handlebars.Utils.escapeExpression(val);
    return val && val.htmlSafe ? val.htmlSafe() : val;
  };

  var Presenter = Em.Object.extend({
    content: null
  });

  return Em.Object.extend({
    Presenter: function() {
      var presenter = Presenter.extend();
      var format = this.get('format');
      var attrs = {};

      this.get('names').forEach(function(name) {
        var attrFormat = format[name];
        if (attrFormat && attrFormat.format) {
          attrFormat = attrFormat.format;
        }

        if (attrFormat && attrFormat.formatter) {
          attrs[name] = function() {
            if(typeof attrFormat.formatter(this.get('content.' + name)) === 'string') {
              return escapeHtml(attrFormat.formatter(this.get('content.' + name)));
            }
            else { //do not escape
              return attrFormat.formatter(this.get('content.' + name));
            }
          }.property('content', 'content.' + name);
        } else {
          attrs[name] = function() {
            return escapeHtml(this.get('content.' + name));
          }.property('content', 'content.' + name);
        }
      }, this);

      return presenter.reopen(attrs);
    }.property('format', 'names.[]'),

    names: function() {
      return this.get('resource').mapBy('attr');
    }.property('resource.[]'),

    idNames: 'id'.w(),

    // When displaying data in a grid, some columns must always be shown. They are listed here.
    mandatoryNames: Em.A(),

    format: {},

    resource: Em.A(),

    resourceByName: function() {
      var byName = {};

      Em.A(this.get('resource')).forEach(function(spec) {
        var type = spec.type;
        var attr = spec.attr;
        var sourceAttr = spec.sourceAttr;

        if (Aggregate.Base.detectInstance(type)) {
          var subSpecs = type.get('Spec');
          subSpecs.get('resource').forEach(function(subSpec) {
            var newSubSpec = {
              attr: attr + '.' + subSpec.attr,
              sourceAttr: sourceAttr + '.' + subSpec.sourceAttr,
              type: subSpec.type
            }
            var field = spec.attr + '.' + subSpec.attr;
            byName[field] = newSubSpec;
          })
        }
        // Make sure the parent is also included
        byName[spec.attr] = spec;
      });

      return byName;
    }.property('resource'),

    // This property includes resources of the subSpecs of aggregate data
    resourceBySourceName: function() {
      var self = this;
      var byName = {};

      Em.A(this.get('resource')).forEach(function(spec) {
        var type = spec.type;
        var attr = spec.attr;
        var sourceAttr = spec.sourceAttr;

        if (Aggregate.Base.detectInstance(type)) {
          var subSpecs = type.get('Spec');
          subSpecs.get('resource').forEach(function(subSpec) {
            var newSubSpec = {
              attr: attr + '.' + subSpec.attr,
              sourceAttr: sourceAttr + '.' + subSpec.sourceAttr,
              type: subSpec.type
            }
            var field = self.sourceAttr(spec) + '.' + self.sourceAttr(subSpec);
            byName[field] = newSubSpec;
          })
        }
        // Make sure the parent is also included
        byName[self.sourceAttr(spec)] = spec;
      });

      return byName;
    }.property('resource'),

    enumColumnTypes: function() {
      return this.get('resource').filter(function(resource) {
        return EnumType.detectInstance(resource.type);
      }).map(function(resource) {
        return resource.type;
      });
    }.property('resource.[]'),

    // Fields that backend support search on them
    // Note! Search is not supported on parent of OneToMany and OneToOne aggregate data on the backend
    searchableNames: function() {
      var resourceByName = this.get('resourceByName');
      var searchableNames = Em.A();

      for (var resource in resourceByName) {
        if (!Aggregate.Base.detectInstance(resourceByName[resource].type)) {
          searchableNames.pushObject(resource);
        }
      }
      return searchableNames;
    }.property('names'),

    // Filter out the fields that are not search supported on the backend,
    // Also filter out the fields that are not supported on the front end(e.g. EnumType fields, dataTime)
    // Children of OneToMany are already taken out from visibleColumnNames. Children of OneToOne will be included
    filterSearchableNames: function(visibleColumnNames) {
      var self = this;
      var formats = this.get('format');
      var resourceByName = this.get('resourceByName');

      var isFormatAvailable = function(formats, name) {
        return formats[name] && formats[name].format ? formats[name].format : formats[name];
      };

      var specSearchableName = {};
      this.get('searchableNames').forEach(function(name) {
        specSearchableName[name] = true;
      });

      var adhocSearchableNames = visibleColumnNames.filter(function(name) {
        var strippedName = self.stripName(name);
        var nameSlugs = self.splitName(name);

        if (nameSlugs.length > 1) {
          var parent = nameSlugs[0];
          var type = resourceByName[parent].type;

          if (Aggregate.OneToOne.detectInstance(type)) {
            var spec = type.get('Spec');
            var format = isFormatAvailable(spec.get('format'), nameSlugs[1]);
          }
        } else {
          var format = isFormatAvailable(formats, strippedName);
        }

        var supportedFormat = format && format.searchable;
        return specSearchableName[strippedName] === true && supportedFormat;
      });

      return adhocSearchableNames;
    },

    getPresentationForAttr: function(name) {
      var spec = this.getResource(name);
      var formatOptions = this.get('format')[name];

      var format, label, labelResource;
      if (Em.isNone(formatOptions)) {
        // Enumerated is one type of attributes that do not have format
        label = Formatter.camelCaseToTitleCase(name);
      } else {
        if (!Em.isNone(formatOptions.format)) {
          format = formatOptions.format;
          label = formatOptions.label;
          labelResource = formatOptions.labelResource;
        } else {
          format = formatOptions;
          label = Formatter.camelCaseToTitleCase(name);
        }
      }

      return {
        name: name,
        label: label,
        labelResource: labelResource,
        format: format,
        type: spec.type,
        editable: spec.editable ? spec.editable : false
      }
    },

    // Given a resource name, return the related resource. Smart enough to strip out indexes in paths for
    // 1-N properties. Null-safe.
    getResource: function(name) {
      var stripped = this.stripName(name);
      return stripped ? this.get('resourceByName')[stripped] : null;
    },

    // Strip out indices from a property name.
    stripName: function(name) {
      return 'string' === typeof(name) ? name.replace(/\.\d+\./g, '.') : null;
    },

    // Return the slugs in a property name as an array
    splitName: function(name) {
      return 'string' === typeof(name) ? this.stripName(name).split('.') : null;
    },

    // Maps json returned from a SearchQuery to an array of objects in the format expected by the DataStore / Model.
    mapRawResultSetData: function(query, rawData) {
      return rawData;
    },

    //  Maps json returned from a SingletonQuery to a SINGLE ELEMENT ARRAY of objects in the format expected by the DataStore / Model.
    mapRawSingletonData: function(query, rawData) {
      return rawData;
    },

    // Maps json returned from a CounterQuery to an integer, the total.
    mapRawCounterData: function(query, rawData) {
      return rawData;
    }
  });
});
