define([
  'ember',
  'desktop',

  'packages/platform/enum-type',
  'packages/platform/ip-type',
  'packages/platform/information-item',
  'packages/platform/interval-type',
  'packages/platform/interval-util',

  'packages/platform/advanced-filter',

  '../views/smart_policy_filter_view',
  'text!../templates/smart_policy_filter_editor.handlebars',

  'locale'
], function(
    Em,
    Desktop,

    EnumType,
    IpType,
    InformationItem,
    IntervalType,
    IntervalUtil,

    Filter,

    SmartPolicyFilterView,
    SmartPolicyFilterEditorTemplate,

    Locale
) {
  'use strict';

  /////////////////////
  //FILTER INIT START
  /////////////////////
  var stringOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.equalOp', template: Filter.EqualsOp.create() }, 
    { labelResource: 'desktop.advancedFilterComponent.notEqualOp', template: Filter.NotEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.containsOp', template: Filter.SubstringOfOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notContainsOp', template: Filter.NotSubstringOfOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.beginsWithOp', template: Filter.StartsWithOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notBeginsWithOp', template: Filter.NotStartsWithOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.endsWithOp', template: Filter.EndsWithOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notEndsWithOp', template: Filter.NotEndsWithOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.emptyOp', template: Filter.IsEmptyOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notEmptyOp', template: Filter.IsNotEmptyOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNAOp', template: Filter.IsNAOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNotNAOp', template: Filter.IsNotNAOp.create() }
  ]);

  var ipOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.equalOp', template: Filter.EqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notEqualOp', template: Filter.NotEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOp', template: Filter.LessThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOrEqualOp', template: Filter.LessEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.greaterOp', template: Filter.GreaterThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.greaterOrEqualOp', template: Filter.GreaterEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.inRangeOp', template: Filter.InRangeOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notInRangeOp', template: Filter.NotInRangeOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNAOp', template: Filter.IsNAOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNotNAOp', template: Filter.IsNotNAOp.create() }
  ]);

  var booleanOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.trueOp', template: Filter.TrueOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.falseOp', template: Filter.FalseOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNAOp', template: Filter.IsNAOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNotNAOp', template: Filter.IsNotNAOp.create() }
  ]);

  var numberOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.equalOp', template: Filter.EqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notEqualOp', template: Filter.NotEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOp', template: Filter.LessThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOrEqualOp', template: Filter.LessEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.greaterOp', template: Filter.GreaterThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.greaterOrEqualOp', template: Filter.GreaterEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNAOp', template: Filter.IsNAOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNotNAOp', template: Filter.IsNotNAOp.create() }
  ]);

  // Copy the numberOps from above but have a different validation rule for version operators
  var intervalIsValidFunc = function() {
    return !Em.isEmpty(this.get('rval.value')) && IntervalUtil.formatIntervalToInt(this.get('rval.value'));
  }.property('rval.value');

  var intervalEqualsOp = Filter.EqualsOp.extend({isValid: intervalIsValidFunc, copyPrototype: function() { return intervalEqualsOp; }.property()});
  var intervalNotEqualsOp = Filter.NotEqualsOp.extend({isValid: intervalIsValidFunc, copyPrototype: function() { return intervalNotEqualsOp; }.property()});
  var intervalLessThanOp = Filter.LessThanOp.extend({isValid: intervalIsValidFunc, copyPrototype: function() { return intervalLessThanOp; }.property()});
  var intervalLessEqualsOp = Filter.LessEqualsOp.extend({isValid: intervalIsValidFunc, copyPrototype: function() { return intervalLessEqualsOp; }.property()});
  var intervalGreaterThanOp = Filter.GreaterThanOp.extend({isValid: intervalIsValidFunc, copyPrototype: function() { return intervalGreaterThanOp; }.property()});
  var intervalGreaterEqualsOp = Filter.GreaterEqualsOp.extend({isValid: intervalIsValidFunc, copyPrototype: function() { return intervalGreaterEqualsOp; }.property()});

  var intervalOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.equalOp', template: intervalEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notEqualOp', template: intervalNotEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOp', template: intervalLessThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOrEqualOp', template: intervalLessEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.greaterOp', template: intervalGreaterThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.greaterOrEqualOp', template: intervalGreaterEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNAOp', template: Filter.IsNAOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNotNAOp', template: Filter.IsNotNAOp.create() }
  ]);

  // Copy the numberOps from above but have a different validation rule for default operators
  var defaultIsValidFunc = function() {
    return !Em.isEmpty(this.get('rval.value'));
  }.property('rval.value');

  var defaultEqualsOp = Filter.EqualsOp.extend({isValid: defaultIsValidFunc, copyPrototype: function() { return defaultEqualsOp; }.property()});
  var defaultNotEqualsOp = Filter.NotEqualsOp.extend({isValid: defaultIsValidFunc, copyPrototype: function() { return defaultNotEqualsOp; }.property()});
  var defaultLessThanOp = Filter.LessThanOp.extend({isValid: defaultIsValidFunc, copyPrototype: function() { return defaultLessThanOp; }.property()});
  var defaultLessEqualsOp = Filter.LessEqualsOp.extend({isValid: defaultIsValidFunc, copyPrototype: function() { return defaultLessEqualsOp; }.property()});
  var defaultGreaterThanOp = Filter.GreaterThanOp.extend({isValid: defaultIsValidFunc, copyPrototype: function() { return defaultGreaterThanOp; }.property()});
  var defaultGreaterEqualsOp = Filter.GreaterEqualsOp.extend({isValid: defaultIsValidFunc, copyPrototype: function() { return defaultGreaterEqualsOp; }.property()});

  var defaultOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.equalOp', template: defaultEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notEqualOp', template: defaultNotEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOp', template: defaultLessThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.lessOrEqualOp', template: defaultLessEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.greaterOp', template: defaultGreaterThanOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.greaterOrEqualOp', template: defaultGreaterEqualsOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNAOp', template: Filter.IsNAOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNotNAOp', template: Filter.IsNotNAOp.create() }
  ]);

  var dateIsExactlyOp = Filter.IsExactly.reopen(
          {
            filterLineAppendWord: { labelResource: 'desktop.datePickerComponent.ago' }
          }).create(),

      dateIsBetweenOp = Filter.BetweenOp.reopen(
          {
            filterLineAppendWord: { labelResource: 'desktop.datePickerComponent.ago' }
          }).create();

  var dateOps = Em.A([
    { labelResource: 'desktop.advancedFilterComponent.exactlyOp', template: dateIsExactlyOp },
    { labelResource: 'desktop.advancedFilterComponent.inLastOp', template: Filter.LastNOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.notInLastOp', template: Filter.NotLastNOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.inNextOp', template: Filter.InNext.create() },
    { labelResource: 'desktop.advancedFilterComponent.notInNextOp', template: Filter.NotInNext.create() },
    { labelResource: 'desktop.advancedFilterComponent.betweenOp', template: dateIsBetweenOp},
    { labelResource: 'desktop.advancedFilterComponent.dateOp', template: Filter.DateOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.beforeDateOp', template: Filter.BeforeDateOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.beforeDateTimeOp', template: Filter.BeforeDateTimeOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.afterDateOp', template: Filter.AfterDateOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.afterDateTimeOp', template: Filter.AfterDateTimeOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.dateRangeOp', template: Filter.BetweenDatesOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNAOp', template: Filter.IsNAOp.create() },
    { labelResource: 'desktop.advancedFilterComponent.isNotNAOp', template: Filter.IsNotNAOp.create() }
  ]);

  /////////////////////
  //FILTER INIT END
  /////////////////////
  // Smart Policy Filter Controller
  // ==============================
  //
  // Manages filter hierarchies that conform to Absolute Smart Policy Rules.

  // Smart Policies consist of "OR"ed or "AND"ed operands. As it happens, the OrFilterController
  // in AdvancedFilterController handles this case well.
  return Desktop.AdvancedFilterController.extend({

    actions: {
      addOperand: function(evt) {
        this.get('filter.operands').addObject(
            Filter.WrapOp.create({
              lval: Filter.Attr.create()
            })
        );
        this.set('isActionBtnDisabledForFilters', true);
      },

      removeOperand: function(filter) {
        this.get('filter.operands').removeObject(filter);
        this.set('isActionBtnDisabledForFilters', !this.get('filter.isComplete') || this.get('noFiltersSpecified'));
      }
    },

    name: "smartPolicyFilterController",
    isActionBtnDisabledForFilters: true,

    stringOpTemplates: stringOps,
    stringOps: Em.computed.map('stringOpTemplates', Desktop.AdvancedFilterController.opSpecMapper),

    booleanOpTemplates: booleanOps,
    booleanOps: Em.computed.map('booleanOpTemplates', Desktop.AdvancedFilterController.opSpecMapper),

    numberOpTemplates: numberOps,
    numberOps: Em.computed.map('numberOpTemplates', Desktop.AdvancedFilterController.opSpecMapper),

    ipOpTemplates: ipOps,
    ipOps: Em.computed.map('ipOpTemplates', Desktop.AdvancedFilterController.opSpecMapper),

    dateOpTemplates: dateOps,
    dateOps: Em.computed.map('dateOpTemplates', Desktop.AdvancedFilterController.opSpecMapper),

    intervalOpTemplates: intervalOps,
    intervalOps: Em.computed.map('internalOpTemplates', Desktop.AdvancedFilterController.opSpecMapper),

    defaultOpTemplates: defaultOps,
    defaultOps: Em.computed.map('defaultOpTemplates', Desktop.AdvancedFilterController.opSpecMapper),

    allOps: Em.A(),

    conjunctionNodeController: Desktop.AdvancedFilterController.OrFilterController,
    AdvancedFilterView: SmartPolicyFilterView,
    AdvancedFilterEditorView: Desktop.AdvancedFilterEditorView.extend({ template: Em.Handlebars.compile(SmartPolicyFilterEditorTemplate) }),

    getOpsFromResourceType: function(resourceType) {
      var ops = this.getProperties(
          'stringOps booleanOps dateOps numberOps ipOps intervalOps defaultOps'.w()
      );

      switch (resourceType){
        case null:
          return Em.A();
        case String:
          return Em.A(ops.stringOps);
        case Number:
          return Em.A(ops.numberOps);
        case Date:
          return Em.A(ops.dateOps);
        case Boolean:
          return Em.A(ops.booleanOps);
        default:
          if (EnumType.detectInstance(resourceType)){
            return Em.A(ops.stringOps);
          } else if (IpType.detectInstance(resourceType)){
            return Em.A(ops.ipOps);
          } else if (InformationItem.detectInstance(resourceType)){
            return Em.A(this.createOpsForInformationItem(resourceType));
          } else if (IntervalType.detectInstance(resourceType)){
            return Em.A(ops.intervalOps);
          } else {
            return Em.A();
          }
      }
    },

    // Some info items need special validation rules based on the item's DisplayType
    // Take a look at getResourceType() from ad_hoc_store.js
    createOpsForInformationItem: function(resourceType) {
      var self = this;

      if( resourceType.regex ) {
        var isValidFunc = function () {
          return !Em.isEmpty(this.get('rval.value')) && this.get('rval.value').match(resourceType.regex);
        }.property('rval.value');
      } else {
        var isValidFunc = function () {
          return !Em.isEmpty(this.get('rval.value'));
        }.property('rval.value');
      }

      var equalsOp = Filter.EqualsOp.extend({isValid: isValidFunc, copyPrototype: function() { return equalsOp; }.property()});
      var notEqualsOp = Filter.NotEqualsOp.extend({isValid: isValidFunc, copyPrototype: function() { return notEqualsOp; }.property()});
      var lessThanOp = Filter.LessThanOp.extend({isValid: isValidFunc, copyPrototype: function() { return lessThanOp; }.property()});
      var lessEqualsOp = Filter.LessEqualsOp.extend({isValid: isValidFunc, copyPrototype: function() { return lessEqualsOp; }.property()});
      var greaterThanOp = Filter.GreaterThanOp.extend({isValid: isValidFunc, copyPrototype: function() { return greaterThanOp; }.property()});
      var greaterEqualsOp = Filter.GreaterEqualsOp.extend({isValid: isValidFunc, copyPrototype: function() { return greaterEqualsOp; }.property()});

      var ops = Em.A([
        { labelResource: 'desktop.advancedFilterComponent.equalOp', template: equalsOp.create() },
        { labelResource: 'desktop.advancedFilterComponent.notEqualOp', template: notEqualsOp.create() },
        { labelResource: 'desktop.advancedFilterComponent.lessOp', template: lessThanOp.create() },
        { labelResource: 'desktop.advancedFilterComponent.lessOrEqualOp', template: lessEqualsOp.create() },
        { labelResource: 'desktop.advancedFilterComponent.greaterOp', template: greaterThanOp.create() },
        { labelResource: 'desktop.advancedFilterComponent.greaterOrEqualOp', template: greaterEqualsOp.create() },
        { labelResource: 'desktop.advancedFilterComponent.isNAOp', template: Filter.IsNAOp.create() },
        { labelResource: 'desktop.advancedFilterComponent.isNotNAOp', template: Filter.IsNotNAOp.create() }
      ]);

      return ops.map(function(spec) {
        spec.name = spec.template.get('name');
        return self.OpSpec.extend(Em.Copyable).create(spec, {parent: self});
      });
    },

    FilterOpWrapperController: Desktop.AdvancedFilterController.FilterOpWrapperController.extend({

      init: function() {
        this._super();
        this.get('operatorTypes');
      },

      tMinutes: 'desktop.datePickerComponent.minutes'.tr(),
      tHours: 'desktop.datePickerComponent.hours'.tr(),
      tDays: 'desktop.datePickerComponent.days'.tr(),
      tWeeks: 'desktop.datePickerComponent.weeks'.tr(),
      tMonths: 'desktop.datePickerComponent.months'.tr(),
      tYears: 'desktop.datePickerComponent.years'.tr(),
      tCalendarDays: 'desktop.datePickerComponent.calendarDays'.tr(),
      tCalendarWeeks: 'desktop.datePickerComponent.calendarWeeks'.tr(),
      tCalendarMonths: 'desktop.datePickerComponent.calendarMonths'.tr(),
      tCalendarYears: 'desktop.datePickerComponent.calendarYears'.tr(),

      // enable/disable the continue button on the filter editing page
      onFilterValuesChange: function() {
        Em.run.once(this, 'handleFilterValuesChange');
      }.observes('lval', 'opRvalValue', 'topRvalValue1', 'topRvalValue2', 'topRvalValue3'),

      //show localized operator label on confirmation step
      localizeDateOperators: function(value) {
        var labelResource;
        switch (value) {
          case 'Minutes':
            labelResource = this.get('tMinutes');
            break;
          case 'Hours':
            labelResource = this.get('tHours');
            break;
          case 'Days':
            labelResource = this.get('tDays');
            break;
          case 'Weeks':
            labelResource = this.get('tWeeks');
            break;
          case 'Months':
            labelResource = this.get('tMonths');
            break;
          case 'Years':
            labelResource = this.get('tYears');
            break;
          case 'CalendarDays':
            labelResource = this.get('tCalendarDays');
            break;
          case 'CalendarWeeks':
            labelResource = this.get('tCalendarWeeks');
            break;
          case 'CalendarMonths':
            labelResource = this.get('tCalendarMonths');
            break;
          case 'CalendarYears':
            labelResource = this.get('tCalendarYears');
            break;
          default:
            labelResource = value;
        }
        return labelResource;
      },

      tTopRvalValue2: function() {
        return this.localizeDateOperators(this.get('topRvalValue2'));
      }.property('topRvalValue2'),

      tTopRvalValue3: function() {
        return this.localizeDateOperators(this.get('topRvalValue3'));
      }.property('topRvalValue3'),

      handleFilterValuesChange: function() {
        this.set('advancedFilterController.isActionBtnDisabledForFilters', !this.get('advancedFilterController.filter.isComplete'));
      },

      supportedOps: function() {
        return this.get('advancedFilterController').getOpsFromResourceType(this.get('qualifiedAttrResource.type'));
      }.property('qualifiedAttrResource'),

      opRvalOptions: function() {
        var options = this.get('attrResource.type.options');
        var selectOptions = Em.A();
        if (!Em.isEmpty(options)) {
          options.forEach(function(option) {
            selectOptions.pushObject(Em.Object.create({ name: option.name, value: option.id }));
          })
        }
        return selectOptions;
      }.property('attrResource.type.options'),

      operatorTypes: function() {
        var operatorTypes = Em.A([
          //{ name: null, labelResource: 'desktop.advancedFilterComponent.dataFieldPrompt' },
          { name: 'Days', labelResource: 'desktop.datePickerComponent.days' },
          { name: 'Minutes', labelResource: 'desktop.datePickerComponent.minutes' },
          { name: 'Hours', labelResource: 'desktop.datePickerComponent.hours' },
          { name: 'Weeks', labelResource: 'desktop.datePickerComponent.weeks' },
          { name: 'Months', labelResource: 'desktop.datePickerComponent.months' },
          { name: 'Years', labelResource: 'desktop.datePickerComponent.years' },
          { name: 'CalendarDays', labelResource: 'desktop.datePickerComponent.calendarDays' },
          { name: 'CalendarWeeks', labelResource: 'desktop.datePickerComponent.calendarWeeks' },
          { name: 'CalendarMonths', labelResource: 'desktop.datePickerComponent.calendarMonths' },
          { name: 'CalendarYears', labelResource: 'desktop.datePickerComponent.calendarYears' }
        ]);

        return operatorTypes.map(function(spec) {
          return Em.Object.create({name: spec.name, label: Locale.renderGlobals(spec.labelResource).toString()});
        });
      }.property()
    }),

    filterNodeController: function() {
      return this.get('conjunctionNodeController').create({
        advancedFilterController: this,
        filterBinding: 'advancedFilterController.filter'
      });
    }.property(),

    supportedColumnSpecs: function() {
      var spec = this.get('dataStoreSpec');
      var resources = spec.get('resourceByName'), searchableNames = {}, columnSpecs = Em.A();

      spec.get('searchableNames').forEach(function(name) {
        searchableNames[name] = true;
      });

      for (var key in resources) {
        if (searchableNames[key]) {
          columnSpecs.pushObject(spec.getPresentationForAttr(key));
        }
      }

      return columnSpecs;
    }.property('dataStoreSpec.names.[]')
  });
});
