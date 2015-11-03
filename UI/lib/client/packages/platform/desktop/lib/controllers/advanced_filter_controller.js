define([
  'ember',
  'locale',
  'formatter',
  'packages/platform/advanced-filter',
  'packages/platform/enum-type',
  'packages/platform/ip-type',
  'packages/platform/information-item',
  'packages/platform/interval-type',
  'packages/platform/aggregate',
  'packages/platform/child-controller',

  '../views/advanced_filter_view',
  '../views/advanced_filter_editor_view',
  '../views/advanced_filter_editor_line_item_view',
  '../views/ng_text_field_view',
  '../views/between_date_field_view',
  '../views/within_field_view',

  './advanced_filter_attr_picker_controller',
  '../views/advanced_filter_attr_picker_view'
], function(
  Em,
  Locale,
  Formatter,
  Filter,
  EnumType,
  IpType,
  InformationItem,
  IntervalType,
  Aggregate,
  ChildController,

  AdvancedFilterView,
  AdvancedFilterEditorView,
  AdvancedFilterEditorLineItemView,
  NgTextFieldView,
  BetweenDateView,
  WithinView,

  AdvancedFilterAttrPickerController,
  AdvancedFilterAttrPickerView
) {
  'use strict';

  // Ops by category
  // ---------------

  var OpSpec = Em.Object.extend(Em.Copyable, {
    template: null,
    name: Em.computed.oneWay('template.name'),

    labelResource: null,
    label: null,
    parent: null,

    renderedLabelResource: Locale.translated({ property: 'labelResource' }),

    labelFinal: function() {
      var label = this.get('renderedLabelResource');
      return Em.isNone(label) ? this.get('label') : label;
    }.property('renderedLabelResource', 'label'),

    // make a deep copy of the current OsSpec object
    copy: function() {
      return this.parent.OpSpec.create({
        parent: this.parent,
        labelResource: this.get('labelResource'),
        template: Em.copy(this.get('template'), true)
      });
    }
  });

  var opSpecMapper = function(opSpecTemplate) {
    // Will run in scope of AdvancedFilterController
    return this.OpSpec.create({
      parent: this,
      labelResource: opSpecTemplate.labelResource,
      template: opSpecTemplate.template
    })
  };

  var emptyOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.emptyOp', template: Filter.IsEmptyOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notEmptyOp', template: Filter.IsNotEmptyOp.create() }
  ]);

  var equalityOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.equalOp', template: Filter.EqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notEqualOp', template: Filter.NotEqualsOp.create() }
  ]);

  var stringOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.containsOp', template: Filter.SubstringOfOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notContainsOp', template: Filter.NotSubstringOfOp.create() }
  ]);

  var comparisonOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.greaterOp', template: Filter.GreaterThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOp', template: Filter.LessThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.greaterOrEqualOp', template: Filter.GreaterEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOrEqualOp', template: Filter.LessEqualsOp.create() }
  ]);

  var dateOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.betweenOp', template: Filter.BetweenDatesOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.withinLastOp', template: Filter.LastNOp.create() }, //note the template here will be dynamically changed
    { labelResource: 'desktop.advancedFilterComponent.notWithinLastOp', template: Filter.NotLastNOp.create() } //note the template here will be dynamically changed
  ]);

  // IsFilterNodeController
  // ----------------------
  //
  // A node in the advanced filter controller heirarchy.

  var IsFilterNodeController = Em.Mixin.create({

    // Direct parent in the hierarchy
    parentController: null,

    // Top level controller
    advancedFilterController: null
  });

  // FilterAttrPickerController
  // ===================================
  //
  // An extension of Tree module to show the selection list for filter attributes.
  var FilterAttrPickerController = AdvancedFilterAttrPickerController.extend({
    parentController: null,
    columnSpecs: Em.computed.oneWay('parentController.advancedFilterController.supportedColumnSpecs')
  });

  // FilterOpWrapperController
  // -------------------------
  //
  // Wraps a single line item in the UI - attribute, operation, parameters. A FilterOpController is bound to a 
  // WrapOp, whose lval will conform to one of the following states:
  //
  // - No attribute selected in the UI: lval is null.
  // - Attribute selected, but no op selected: lval is an Attr.
  // - Op selected: lval is the op, and rval (if not a UnaryOp) is the corresponding Literal.

  var FilterOpWrapperController = Em.Object.extend(IsFilterNodeController, {
    NgTextFieldView: NgTextFieldView,
    BetweenDateView: BetweenDateView,
    WithinView: WithinView,

    FilterAttrPickerView: AdvancedFilterAttrPickerView,
    FilterAttrPickerController: FilterAttrPickerController,

    filterAttrPickerController: function() {
      return this.FilterAttrPickerController.create({ parentController: this });
    }.property(),

    init: function() {
      var selectedAttr = this.get('qualifiedAttrName');
      if (!Em.isEmpty(selectedAttr)) {
        this.set('filterAttrPickerController.selectedAttr', selectedAttr);
      }

      this.updateQualifiedAttrResourceOnAttrHierarchyChange();
    },

    resourceByName: function() {
      return this.get('advancedFilterController.dataStoreSpec.resourceByName');
    }.property('advancedFilterController.dataStoreSpec.resourceByName'),

    filter: null,

    // The wrapped lval.
    lval: Em.computed('filter.lval', {
      get: function() {
        return this.get('filter.lval');
      },

      set: function(name, value) {
        this.set('filter.lval', value);
        return value;
      }

    }),

    attr: function() {
      var attr  = this.get('lval');

      if (this.isAttrTypeAggregate(attr)) {
        return attr;
      } else {
        attr = this.get('lval.lval') || this.get('lval');

        if (!Filter.Attr.detectInstance(attr)) {
          throw 'Should have an Attr instance!';
        }
      }
      return attr;
    }.property('lval.lval'),

    aggregateAttr: function() {
      var attr = null;

      var lval = this.get('lval');
      if (Filter.AnyAggregateOp.detectInstance(lval)) {
        attr = lval.get('rval.lval') || lval.get('rval');

        if (!Filter.Attr.detectInstance(attr)) {
          throw 'Should have an Attr!';
        }
      }

      return attr;
    }.property('lval.rval.lval'),

    isAttrTypeAggregate: function(attr) {
      return Filter.AnyAggregateOp.detectInstance(attr);
    },

    qualifiedAttr: function() {
      var attr = this.get('attr');

      if (this.isAttrTypeAggregate(attr)) {
        attr = this.get('aggregateAttr');
      }

      return attr;
    }.property('lval.lval'),

    attrName: Em.computed('resourceByName', 'qualifiedAttrName', {
      get: function() {
        var qualifiedAttrName = this.get('qualifiedAttrName');
        return qualifiedAttrName;
      },

      set: function(name, value) {
        var getAttrName = function(attrName) { return attrName.indexOf('.') !== -1 ? attrName.split('.')[0] : attrName };
        var getAttrAggregateChild = function(attrName) { return attrName.split('.')[1] };
        // setter
        var attr = value,
          resource = this.get('resourceByName')[attr],
          resourceType = resource.type,
          aggregateResource = this.get('resourceByName')[getAttrName(attr)];

        var type = resourceType;

        if (EnumType.detectInstance(resourceType) || IpType.detectInstance(resourceType) ||
          InformationItem.detectInstance(resourceType) || IntervalType.detectInstance(resourceType)) {
          type = String;
        }

        var attrNode = Filter.Attr.create({
          value: Aggregate.OneToMany.detectInstance(aggregateResource.type) ? getAttrName(attr) : attr,
          type: type
        });

        var node = null;
        if (Aggregate.OneToMany.detectInstance(aggregateResource.type)) {
          node = Filter.AnyAggregateOp.create({
            lval: attrNode,
            rval: Filter.Attr.create({ value: getAttrAggregateChild(value), type: resourceType })
          });
          attrNode.type = Filter.TYPE_AGGREGATE_1_TO_N;
        } else {
          node = attrNode;
        }

        this.set('lval', node);
        return value;
      }
    }),

    qualifiedAttrName: function() {
      var attr = this.get('attr');

      if (this.isAttrTypeAggregate(attr)) {
        return attr.get('lval.value') + '.' + this.get('aggregateAttr').value;
      }

      return attr.value;
    }.property('attr', 'aggregateAttr'),

    attrLabel: function() {
      var attrName = this.get('attrName');
      return attrName ? this.get('advancedFilterController.dataStoreSpec').getPresentationForAttr(attrName).label : null;
    }.property('attrName'),

    attrResource: function() {
      var attrName = this.get('attrName');
      var resourceByName = this.get('resourceByName');
      var resource = resourceByName && resourceByName[attrName];

      return Em.isNone(resource) ? null : resource;
    }.property('resourceByName', 'attrName'),

    // We use observers here to simplify / optimize the dependency chain. The backing structure for qualifiedAttr
    // can be any of:
    //
    // - WrapOp => Attr
    // - WrapOp => Op => Attr
    // - WrapOp => Aggregate => Attr
    // - WrapOp => Aggregate => Op => Attr
    //
    // The backing structure can move between these options without changing the real meaning of qualifiedAttr, 
    // but Ember dependencies cannot express that. So, observe and set instead.
    updateQualifiedAttrResourceOnAttrHierarchyChange: function() {
      var storedQualifiedAttrResource = this.get('qualifiedAttrResource');
      var computedQualifiedAttrResource = this.get('attrResource');

      if (storedQualifiedAttrResource !== computedQualifiedAttrResource) {
        this.set('qualifiedAttrResource', computedQualifiedAttrResource);
      }
    }.observes('attrResource'),

    qualifiedAttrResource: null,

    supportedOps: function() {
      var resourceType = this.get('qualifiedAttrResource.type');

      var noOps = Em.A([this.get('advancedFilterController.OpSpec').create({ name: null, label: '' })]);
      var ops = this.get('advancedFilterController').getProperties(
        'allOps emptyOps equalityOps stringOps comparisonOps dateOps'.w()
      );

      switch (resourceType) {
        case null:
          return Em.A();
        case String:
          return Em.A(noOps.concat(ops.allOps).concat(ops.equalityOps).concat(ops.stringOps).concat(ops.emptyOps));
        case Number:
          return Em.A(noOps.concat(ops.allOps).concat(ops.equalityOps).concat(ops.comparisonOps).concat(ops.emptyOps));
        case Date:
          //in case the current operator is of super type WITHIN or NOT WITHIN replace the proper template.
          return Em.A(noOps.concat(ops.allOps).concat(ops.equalityOps).concat(ops.comparisonOps).concat(ops.dateOps).concat(ops.emptyOps));
        default:
          if (EnumType.detectInstance(resourceType)) {
            return Em.A(noOps.concat(ops.allOps).concat(ops.equalityOps).concat(ops.emptyOps));
          } else {
            return Em.A();
          }
      }
    }.property('qualifiedAttrResource', 'attrResource'),

    supportedOpsByName: function() {
      return this.get('supportedOps').reduce(function(map, op) {
        if (op.template) {
          map[op.template.get('name')] = op;
        }

        return map;
      }, {});
    }.property('supportedOps.[]'),

    op: function() {
      var lval = this.get('lval');
      var op = this.isAttrTypeAggregate(lval) ? lval.get('rval') : lval;
      return Filter.OpNode.detectInstance(op) ? op : null;
    }.property('lval.rval'),

    opName: Em.computed('op.name', 'supportedOpsByName.[]', 'op.acceptableRvalTypes', 'qualifiedAttr.attr', 'lval', {
      get: function() {
        var currentOp = this.get('op');

        if (Em.isNone(currentOp)) {
          currentOp = null;
        }

        var currentOpName = Filter.OpNode.detectInstance(currentOp) ? currentOp.get('name') : null;
        return currentOpName;
      },

      set: function(name, value) {
        var currentOp = this.get('op');

        if (Em.isNone(currentOp)) {
          currentOp = null;
        }

        if (Em.isNone(value)) {
          value = null;
        }

        var currentOpName = Filter.OpNode.detectInstance(currentOp) ? currentOp.get('name') : null;
        var newOp = value ? this.get('supportedOpsByName')[value].template.copy() : null;
        var qualifiedAttr = this.get('qualifiedAttr');

        if (newOp) {
          newOp.set('lval', qualifiedAttr);

          if (Filter.BinaryOp.detectInstance(newOp) || Filter.TrinaryOp.detectInstance(newOp) || Filter.QuaternaryOp.detectInstance(newOp)) {
            var literalClass;
            var rvalType = newOp.get('acceptableRvalTypes').find(function(type) {
              return type !== Filter.TYPE_NULL;
            });

            switch (rvalType) {
              case Filter.TYPE_STRING:
                literalClass = Filter.StringLiteral;
                break;
              case Filter.TYPE_INTEGER:
                literalClass = Filter.IntegerLiteral;
                break;
              case Filter.TYPE_NUMBER:
                literalClass = Filter.NumberLiteral;
                break;
              case Filter.TYPE_DATE:
                literalClass = Filter.DateLiteral;
                break;
              case Filter.TYPE_DATETIME:
                //in case the attr type is datetime and operator is equals or not equals, create DateLiteral
                if(value === 'BETWEEN_DATE') {
                  literalClass = Filter.DateLiteral;
                } else {
                  literalClass = Filter.DateTimeLiteral;
                }
                break;
              default:
                throw ['Unsupported rval type required', rvalType];
            }

            if (Filter.BetweenDatesOp.detectInstance(newOp) || Filter.InRangeOp.detectInstance(newOp) || Filter.NotInRangeOp.detectInstance(newOp)){
              newOp.set('rval1', literalClass.create());
              newOp.set('rval2', literalClass.create());
            } else if (Filter.LastNOpBase.detectInstance(newOp)){
              newOp.set('rval1', literalClass.create());
              newOp.set('rval2', Filter.StringLiteral.create()); //This will always be String (for LastNMinutes..LastNYears)
            } else if (Filter.BetweenOp.detectInstance(newOp)){
              newOp.set('rval1', Filter.StringLiteral.create());
              newOp.set('rval2', Filter.StringLiteral.create());
              newOp.set('rval3', Filter.StringLiteral.create());
            } else {
              newOp.set('rval', literalClass.create());
            }
          }
        } else {
          newOp = qualifiedAttr;
        }

        var lval = this.get('lval');
        if (Filter.AnyAggregateOp.detectInstance(lval)) {
          this.set('lval.rval', newOp);
        } else {
          this.set('lval', newOp);
        }
        return value;
      }
    }),

    opLabel: function() {
      var op = this.get('supportedOpsByName')[this.get('opName')];
      if (!op) {
        return null;
      }

      return op.labelResource ? Locale.render(Locale.resolveGlobals(op.labelResource)) : op.label;
    }.property('supportedOpsByName.[]', 'opName'),

    opRvalValue: Em.computed('op.rval.value', {
      get: function(name) {
        return this.getRvalValue('rval');
      },

      set: function(name, value) {
        return this.setRvalValue('rval', value);
      }
    }),

    topRvalValue1: Em.computed('op.rval1.value', {
      get: function(name) {
        return this.getRvalValue('rval1');
      },

      set: function(name, value) {
        return this.setRvalValue('rval1', value);
      }
    }),

    topRvalValue2: Em.computed('op.rval2.value', {
      get: function(name) {
        return this.getRvalValue('rval2');
      },

      set: function(name, value) {
        return this.setRvalValue('rval2', value);
      }
    }),

    topRvalValue3: Em.computed('op.rval3.value', {
      get: function(name) {
        return this.getRvalValue('rval3');
      },

      set: function(name, value) {
        return this.setRvalValue('rval3', value);
      }
    }),

    filterLineAppendWord: Em.computed('op.filterLineAppendWord', {
      get: function(name, value) {
        var line = this.get('op.filterLineAppendWord')
        return line && line.labelResource ? Locale.render(Locale.resolveGlobals(line.labelResource)) : line;
      }
    }),

    warningMessage: Em.computed('op.lval.value', 'advancedFilterController.dataStoreSpec.resourceByName', {
      get: function(name) {
        var resource = this.get('advancedFilterController.dataStoreSpec.resourceByName')[this.get('op.lval.value')];
        var warningMessage = resource ? resource.warningMessage : null;

        return warningMessage ? Locale.renderGlobals(warningMessage) : null;
      }
    }),

    getRvalValue: function(name) {
      return this.get('op.' + name + '.value');
    },

    setRvalValue: function(name, value) {
      var rval = this.get('op.' + name);
      if (rval) {
        rval.set('value', value);
      }

      return value;
    },

    opRvalTypeIsDate: function() {
      return Filter.DateLiteral.detectInstance(this.get('op.rval'));
    }.property('op.rval'),

    opRvalTypeIsDateTime: function() {
      return Filter.DateTimeLiteral.detectInstance(this.get('op.rval'));
    }.property('op.rval'),

    opRvalTypeIsNumber: function() {
      var rval = this.get('op.rval');
      return (Filter.NumberLiteral.detectInstance(rval) || Filter.IntegerLiteral.detectInstance(rval));
    }.property('op.rval'),

    opRvalTypeIsString: function() {
      // Display the text input box only if the operator's rval type is a String and it's not an Enum
      return Filter.StringLiteral.detectInstance(this.get('op.rval')) &&
        !this.get('opRvalTypeIsEnum') && !this.get('opRvalTypeIsIp') && !this.get('opTypeIsSingleDate')
        && !this.get('opTypeIsSingleDateTime');
    }.property('op.rval', 'qualifiedAttrResource.type'),

    opRvalTypeIsIp: function() {
      return !(Filter.InRangeOp.detectInstance(this.get('op')) || Filter.NotInRangeOp.detectInstance(this.get('op'))) &&
        Filter.StringLiteral.detectInstance(this.get('op.rval')) &&
        IpType.detectInstance(this.get('qualifiedAttrResource.type'));
    }.property('op', 'qualifiedAttrResource.type', 'op.rval'),

    opRvalTypeIsEnum: function() {
      return Filter.StringLiteral.detectInstance(this.get('op.rval')) &&
        EnumType.detectInstance(this.get('qualifiedAttrResource.type'));
    }.property('op.rval', 'qualifiedAttrResource.type'),

    opTypeIsBetweenDate: function() {
      return Filter.BetweenDatesOp.detectInstance(this.get('op'));
    }.property('op'),

    opTypeIsLastNOp: function() {
      return Filter.LastNOpBase.detectInstance(this.get('op'));
    }.property('op'),

    opTypeIsLastNOpRange: function() {
      return Filter.BetweenOp.detectInstance(this.get('op'));
    }.property('op'),

    opTypeIsIpRange: function() {
      var op = this.get('op');
      return Filter.InRangeOp.detectInstance(op) || Filter.NotInRangeOp.detectInstance(op);
    }.property('op'),

    opTypeIsSingleDate: function() {
      var op = this.get('op');
      return Filter.DateOp.detectInstance(op) && !this.get('opTypeIsSingleDateTime');
    }.property('op'),

    opTypeIsSingleDateTime: function() {
      var op = this.get('op');
      return Filter.BeforeDateTimeOp.detectInstance(op) || Filter.AfterDateTimeOp.detectInstance(op);
    }.property('op'),

    opIsUnary: function() {
      return Filter.UnaryOp.detectInstance(this.get('op'));
    }.property('op'),

    opIsBinary: function() {
      return Filter.BinaryOp.detectInstance(this.get('op'));
    }.property('op'),

    opIsTrinary: function() {
      return Filter.TrinaryOp.detectInstance(this.get('op'));
    }.property('op'),

    opRvalOptions: function() {
      var options = this.get('attrResource.type.options');
      var selectOptions = Em.A();
      if (this.get('opRvalTypeIsEnum') && !Em.isEmpty(options)) {
        options.forEach(function(option) {
          selectOptions.pushObject({ name: option.name, value: option.id });
        })
      }
      return selectOptions;
    }.property('attrResource.type.options', 'opRvalTypeIsEnum'),

    opRvalPresentation: function() {
      var opRvalValue = this.get('opRvalValue');
      if (this.get('opRvalTypeIsEnum')) {
        this.get('attrResource.type.options').forEach(function(option) {
          if (option.id === opRvalValue) {
            opRvalValue = option.name;
            return;
          }
        })
      } else if (this.get('opRvalTypeIsDateTime')) {
        return Locale.dateTime(opRvalValue);
      } else if (this.get('opRvalTypeIsDate')) {
        return Locale.date(opRvalValue);
      }
      return String(opRvalValue);
    }.property(
      'opRvalTypeIsEnum',
      'opRvalTypeIsDateTime',
      'opRvalTypeIsDate',
      'opRvalValue',
      'attrResource.type.options'
    ),

    isLastOperand: function() {
      return this === this.get('parentController').objectAt(this.get('parentController.length') - 1);
    }.property('parentController.[]')
  });

  // FilterOperandsProxy
  // -------------------
  //
  // Controllers with this behaviour have a filter whose operands array they proxy, but they are expected to 
  // implement the rest of the MutableArray interface.

  var FilterOperandsProxy = Em.ArrayProxy.extend(IsFilterNodeController, {
    filter: null,
    content: Em.computed.alias('filter.operands'),
    hasMultipleOperands: Em.computed.gt('content.length', 1),

    filterToControllerCache: function() {
      return Em.Map.create();
    }.property(),

    objectAtContent: function(idx) {
      // Bounds check
      if (idx >= this.get('length') || idx < 0) {
        return undefined;
      }

      // Maintain and read-through a cache of FILTER => CONTROLLER
      var filter = this.get('content').objectAt(idx);
      var filterToControllerCache = this.get('filterToControllerCache');
      var controller = filterToControllerCache.get(filter);

      if (!controller) {
        controller = this.createControllerForFilter(filter);
        filterToControllerCache.set(filter, controller);
      }

      return controller;
    },

    replaceContent: function(idx, amt, objects) {
      // It doesn't really make sense to use this interface for anything other than removal
      if (controllers.length > 0) {
        throw 'Write directly to the operands array instead.'
      }

      // Assumption: a filter only appears once as a parameter.
      var filterToControllerCache = this.get('filterToControllerCache');

      // Purge controllers for removed filters from cache
      var filtersToBeReplaced = this.get('content').slice(idx, idx + amt);
      filtersToBeReplaced.forEach(function(filter) {
        filterToControllerCache.remove(filter);
      });

      this.get('content').replace(idx, amt, controllers.mapBy('filter'));
    },

    createControllerForFilter: function(filter) {
      throw 'Implement me';
    }
  });

  // OrFilterController
  // ------------------
  //
  // Has a collection of FilterOpWrapperControllers as children.

  var OrFilterController = FilterOperandsProxy.extend({
    createControllerForFilter: function(filter) {
      return this.get('advancedFilterController.FilterOpWrapperController').create({
        advancedFilterController: this.get('advancedFilterController'),
        parentController: this,
        filter: filter
      });
    },

    isLastOperand: function() {
      return this === this.get('parentController').objectAt(this.get('parentController.length') - 1);
    }.property('parentController.[]')
  });

  // AndFilterController
  // -------------------
  //
  // Has a collection of Or filter controllers as children.
  //
  // We maintain a cache of controllers for each filter parameter, and drive array-like behaviour based
  // on the parameters array and the cache.

  var AndFilterController = FilterOperandsProxy.extend({
    createControllerForFilter: function(filter) {
      return this.get('advancedFilterController.OrFilterController').create({
        advancedFilterController: this.get('advancedFilterController'),
        parentController: this,
        filter: filter
      });
    }
  });

  // AdvancedFilterController
  // ------------------------

  var defaultOrFilter = Filter.OrFilter.create();
  var defaultAndFilter = Filter.AndFilter.create();
  defaultAndFilter.get('operands').pushObject(defaultOrFilter);

  var AdvancedFilterController = Em.Controller.extend(ChildController, {
    OpSpec: OpSpec,

    AdvancedFilterView: AdvancedFilterView,
    AdvancedFilterEditorView: AdvancedFilterEditorView,
    AdvancedFilterEditorLineItemView: AdvancedFilterEditorLineItemView,

    FilterOperandsProxy: FilterOperandsProxy,
    FilterOpWrapperController: FilterOpWrapperController,

    OrFilterController: OrFilterController,
    AndFilterController: AndFilterController,

    actions: {
      addAndFilterOperand: function() {
        var filterNodeController = this.get('filterNodeController');

        if (Em.isNone(filterNodeController.get('filter'))) {
          filterNodeController.set('filter', Filter.AndFilter.create());
        }

        filterNodeController.get('filter.operands').pushObject(Filter.OrFilter.create({
          operands: Em.A([
            Filter.WrapOp.create({
              lval: Filter.Attr.create({ value: this.get('supportedColumnSpecs').objectAt(0).attr })
            })
          ])
        }));
      },

      removeAndFilterOperand: function(filter) {
        var filterNodeController = this.get('filterNodeController');
        filterNodeController.get('filter.operands').removeObject(filter);
      },

      addOrFilterOperand: function(orFilterController) {
        orFilterController.get('filter.operands').addObject(
          Filter.WrapOp.create({
            lval: Filter.Attr.create({ value: this.get('supportedColumnSpecs').objectAt(0).attr })
          })
        );
      },

      removeOrFilterOperand: function(orFilterController, filter) {
        orFilterController.get('filter.operands').removeObject(filter);
      }
    },

    emptyOpTemplates: emptyOps,
    emptyOps: Em.computed.map('emptyOpTemplates', opSpecMapper),

    equalityOpTemplates: equalityOps,
    equalityOps: Em.computed.map('equalityOpTemplates', opSpecMapper),

    stringOpTemplates: stringOps,
    stringOps: Em.computed.map('stringOpTemplates', opSpecMapper),

    comparisonOpTemplates: comparisonOps,
    comparisonOps: Em.computed.map('comparisonOpTemplates', opSpecMapper),

    dateOpTemplates: dateOps,
    dateOps: Em.computed.map('dateOpTemplates', opSpecMapper),

    allOps: Em.A(),

    dataStoreSpec: null,

    filter: null,

    defaultAndFilter: defaultAndFilter,
    defaultOrFilter: defaultOrFilter,

    filterNodeController: function() {
      return this.AndFilterController.create({
        advancedFilterController: this,
        filterBinding: 'advancedFilterController.filter'
      });
    }.property(),

    noFiltersSpecified: function() {
      var operands = this.get('filter.operands');
      return Em.isEmpty(operands) || operands.get('length') === 0;
    }.property('filter.operands.[]'),

    tNoFiltersSpecified: 'shared.noFiltersSpecified'.tr(),

    noFiltersSpecifiedMessage: function() {
      return this.get('noFiltersSpecified') ? this.get('tNoFiltersSpecified') : '';
    }.property('noFiltersSpecified'),

    // ColumnSpecs will include:
    // Searchable regular fields, parent of aggregate OneToMany, Searchable children of aggregate OneToOne
    supportedColumnSpecs: function() {
      var getParent = function(attr) { return resources[attr.split('.')[0]]; };

      var columnSpecs = Em.A(),
        spec = this.get('dataStoreSpec');

      if (!Em.isNone(spec)) {
        var resources = spec.get('resourceByName');
        var searchableNames = {};

        spec.get('searchableNames').forEach(function (name) {
          if (name.match(/[.]/)) {
            var parent = getParent(name).attr;
            if (Em.isNone(searchableNames[parent])) {
              searchableNames[parent] = true;
            }
          }
          searchableNames[name] = true;
        });

        for (var key in resources) {
          if (searchableNames[key]) {
            columnSpecs.pushObject(spec.getPresentationForAttr(key));
          }
        }
      }

      // e.g. [ { Spec of identifier }, { Spec of driveVolumes }, { Spec of version.build }, { Spec of version.major } ]
      return columnSpecs;
    }.property('dataStoreSpec.names.[]'),

    enumeratedColumnTypes: function() {
      // return the subset of supportedColumnSpecs whose types are enumerations
      return this.get('supportedColumnSpecs').filter(function(spec) {
        return EnumType.detectInstance(spec.type);
      }).mapBy('type');
    }.property('supportedColumnSpecs.@each.type'),

    // return true if all of the enumeratedColumn are loaded
    isLoaded: function() {
      var enumeratedColumnSpecs = this.get('enumeratedColumnTypes');
      var optionsLoaded = Em.isEmpty(enumeratedColumnSpecs.filter(function(spec) {
        var enumType = spec;
        return Em.isEmpty(enumType.get('options')) && enumType.get('loadInProgress');
      }));
      return optionsLoaded;
    }.property('enumeratedColumnTypes.@each.options.[]', 'enumeratedColumnTypes.@each.loadInProgress'),

    reset: function() {
      this.set('filter', this.get('defaultAndFilter').copy());
    }
  });

  // Make utility classes available at the class level as well, for easy sub-classing.
  AdvancedFilterController.reopenClass({
    opSpecMapper: opSpecMapper,

    IsFilterNodeController: IsFilterNodeController,
    FilterOpWrapperController: FilterOpWrapperController,

    FilterOperandsProxy: FilterOperandsProxy,
    OrFilterController: OrFilterController,
    AndFilterController: AndFilterController
  });

  return AdvancedFilterController;
});
