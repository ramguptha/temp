define([
  'jquery',
  'ember',
  'formatter',
  'packages/platform/enum-type',
  'packages/platform/aggregate'
], function(
  $,
  Em,
  Formatter,
  EnumType,
  Aggregate
) {
  'use strict';

  // Copy from
  // CcSpecBehaviourMixin
  // ====================
  //
  // Encapsulates CC-specific endpoint decoding and presentation. Implemented as a Mixin so that both
  // CcSpec and CcCompositeSpec can use it.
  return Em.Mixin.create({
    PresenterBase: Em.Object.extend({
      content: null,

      escapeHtml: function(val) {
        val = Em.Handlebars.Utils.escapeExpression(val);
        return val && val.htmlSafe ? val.htmlSafe() : val;
      }
    }),

    Presenter: function() {
      var presenter = this.PresenterBase.extend();
      var attrs = {};

      this.get('names').forEach(function(name) {
        attrs[name] = function() {
          return this.escapeHtml(this.get('content.' + name));
        }.property('content', 'content.' + name);
      }, this);

      return presenter.reopen(attrs);
    }.property('names.[]'),

    // @override
    mapRawResultSetData: function(query, rawData) {
      var self = this;

      // Build a mapping from mapped attr index => row attr index
      var mappedAttributes = this.get('resource');

      // Apply our mapping to each row in the raw data
      // make sure that response is array
      if (!Em.isArray(rawData)) {
        rawData = [rawData];
      }
      var attrHashes = rawData.map(function(raw, i) {
        var attrs = {};

        mappedAttributes.forEach(function(mappingSpec, i) {
          var attrName = mappingSpec.attr;
          var attrType = mappingSpec.type;
          var rawAttr = Em.get(raw, self.sourceAttr(mappingSpec));

          if (typeof(rawAttr) !== 'undefined') {
            if (Date === attrType) {
              attrs[attrName] = new Date(rawAttr);
            } else if (Aggregate.OneToMany.detectInstance(attrType)) {
              // If type is Aggregate, invoke mapRawResultSetData on the associated Spec
              attrs[attrName] = attrType.get('Spec').mapRawResultSetData(query, rawAttr);
            } else if (Aggregate.OneToOne.detectInstance(attrType)) {
              // If type is Aggregate, invoke mapRawResultSetData on the associated Spec
              attrs[attrName] = attrType.get('Spec').mapRawResultSetData(query, [rawAttr])[0];
            } else {
              // The middle tier has been known, on occasion, to deliver us data in the wrong format.
              // Such are the wonders of document stores. If this happens, complain and cast it.
              var validateType = function(name, value, type, typeOfValue) {
                if (null !== value && typeof(value) !== typeOfValue) {
                  return type(value);
                }
                return value;
              };

              switch (attrType) {
                case Number:
                  attrs[attrName] = validateType(attrName, rawAttr, Number, 'number');
                  break;
                case String:
                  attrs[attrName] = validateType(attrName, rawAttr, String, 'string');
                  break;
                case Boolean:
                  attrs[attrName] = validateType(attrName, rawAttr, Boolean, 'boolean');
                  break;
                default:
                  attrs[attrName] = rawAttr;
              }
            }
          }
        });

        return attrs
      });

      return attrHashes;
    },

    // @override
    mapRawSingletonData: function(query, rawData) {
      return Em.isEmpty(rawData) ? Em.A() : this.mapRawResultSetData(query, [rawData]);
    },

    // @override
    mapRawCounterData: function(query, rawData) {
      return rawData.count;
    },

    // @override
    // Change the labels for aggregate data
    getPresentationForAttr: function(name) {
      var spec = this.get('resourceByName')[name];
      var formatOptions = this.get('format')[name];

      var format, label;
      if (Em.isNone(formatOptions)) {
        // Enum type field and parent of aggregate fields are some types of attributes that do not have format
        if (name.match(/[\.]/)) {
          var parent = name.split('.')[0];
          var child = name.split('.')[1];
          var parentSpec = this.get('resourceByName')[parent].type.Spec;
          var subFormatOption = parentSpec.get('format')[child];

          if (subFormatOption && subFormatOption.label) {
            label = subFormatOption.label;
          } else {
            label = Formatter.camelCaseToTitleCase(name.split('.')[0]) + ' ' + Formatter.camelCaseToTitleCase(name.split('.')[1]);
          }
        } else {
          label = Formatter.camelCaseToTitleCase(name);
        }
      } else {
        if (!Em.isNone(formatOptions.label)) {
          format = formatOptions.format;
          label = formatOptions.label;
        } else {
          format = formatOptions;
          label = Formatter.camelCaseToTitleCase(name);
        }
      }

      return {
        name: name,
        label: label,
        format: format,
        type: spec.type,
        editable: spec.editable ? spec.editable : false
      }
    },

    sourceAttr: function(spec) {
      return spec.sourceAttr || spec.attr;
    },

    loadEnumOptions: function() {
      this.get('enumColumnTypes').forEach(function(enumType) {
        enumType.get('options');
      });
    }
  });
});
