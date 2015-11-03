// This file contains the functions for serializing and de-serializing filters for smart policies
// The code may be confusing and should be improved. This is due to the fact that AM server uses the same
// names for different types of operators.
// Example 1: '==' can mean a unary-type 'true' or a binary-type 'equals' number comparison.
// Example 2: '>' can mean a greater than number comparison or an 'after date & time' comparison

define([
  'ember',
  'formatter',
  'desktop',

  'packages/platform/advanced-filter',
  'packages/platform/enum-type',
  'packages/platform/ip-type',
  'packages/platform/interval-type',
  'packages/platform/information-item',
  'packages/platform/ip-util',
  'packages/platform/version-util',
  'packages/platform/interval-util'
], function(
  Em,
  Formatter,
  Desktop,

  AdvancedFilters,
  EnumType,
  IpType,
  IntervalType,
  InformationItem,
  IpUtil,
  VersionUtil,
  IntervalUtil
  ){
  'use strict';

  // The names of these filters must correspond to the defines in CobraAdminFilterKeys.h of Cobra Admin
  var supportedOps = [
    { middleTierName: '==', opClass: AdvancedFilters.EqualsOp, unaryOpClass: AdvancedFilters.TrueOp },
    { middleTierName: '<>', opClass: AdvancedFilters.NotEqualsOp, unaryOpClass: AdvancedFilters.FalseOp },
    { middleTierName: 'Contains', opClass: AdvancedFilters.SubstringOfOp },
    { middleTierName: 'NotContains', opClass: AdvancedFilters.NotSubstringOfOp },
    { middleTierName: 'BeginsWith', opClass: AdvancedFilters.StartsWithOp },
    { middleTierName: 'NotBeginsWith', opClass: AdvancedFilters.NotStartsWithOp },
    { middleTierName: 'EndsWith', opClass: AdvancedFilters.EndsWithOp },
    { middleTierName: 'NotEndsWith', opClass: AdvancedFilters.NotEndsWithOp },
    { middleTierName: 'IsEmpty', unaryOpClass: AdvancedFilters.IsEmptyOp },
    { middleTierName: 'IsNotEmpty', unaryOpClass: AdvancedFilters.IsNotEmptyOp },
    { middleTierName: 'IsNULL', unaryOpClass: AdvancedFilters.IsNAOp },
    { middleTierName: 'IsNotNULL', unaryOpClass: AdvancedFilters.IsNotNAOp },
    { middleTierName: 'Range', opClass: AdvancedFilters.InRangeOp, opDateClass: AdvancedFilters.BetweenDatesOp },
    { middleTierName: 'NotInRange', opClass: AdvancedFilters.NotInRangeOp },

    { middleTierName: '<', opClass: AdvancedFilters.LessThanOp, opDateClass: AdvancedFilters.BeforeDateTimeOp },
    { middleTierName: '<=', opClass: AdvancedFilters.LessEqualsOp },
    { middleTierName: '>', opClass: AdvancedFilters.GreaterThanOp, opDateClass: AdvancedFilters.AfterDateTimeOp  },
    { middleTierName: '>=', opClass: AdvancedFilters.GreaterEqualsOp },

    { middleTierName: 'RelativDateExactly', opDateClass: AdvancedFilters.IsExactly },
    { middleTierName: 'RelativDateLast', opDateClass: AdvancedFilters.LastNOp },
    { middleTierName: 'RelativDateNotLast', opDateClass: AdvancedFilters.NotLastNOp },
    { middleTierName: 'RelativDateNext', opDateClass: AdvancedFilters.InNext },
    { middleTierName: 'RelativDateNotNext', opDateClass: AdvancedFilters.NotInNext },
    { middleTierName: 'RelativDateRange', opDateClass: AdvancedFilters.BetweenOp },
    { middleTierName: 'EqualDate', opDateClass: AdvancedFilters.DateOp },
    { middleTierName: 'BeforeDate', opDateClass: AdvancedFilters.BeforeDateOp },
    { middleTierName: 'AfterDate', opDateClass: AdvancedFilters.AfterDateOp }
  ];

  var opNameMap = {}, nameOpMap = {};

  Em.A(supportedOps).forEach(function(spec) {
    nameOpMap[spec.middleTierName] = [spec.opClass, spec.unaryOpClass, spec.opDateClass];

    if( !Em.isNone(spec.unaryOpClass) ) {
      opNameMap[spec.unaryOpClass.create().get('name')] = spec.middleTierName;
    }
    if( !Em.isNone(spec.opClass) ) {
      opNameMap[spec.opClass.create().get('name')] = spec.middleTierName;
    }
    if( !Em.isNone(spec.opDateClass) ) {
      opNameMap[spec.opDateClass.create().get('name')] = spec.middleTierName;
    }
  });

  return Em.Object.extend(Desktop.DateFieldUtilMixin, {

    serializeToObj: function(advancedFilter, spec, filterType, missingInstalledSelectValue, onOptionChanged) {
      var serialized = {
        CompareValue: []
      };

      // 1 - filter by none, 2 - filter by installed app, 3 - filter by installed config. profile
      if ( filterType === 1 ) {
        serialized.CriteriaFieldType = 0;
      } else {
        serialized.ContainmentOperator = missingInstalledSelectValue;
      }

      var validatedAdvancedFilter = advancedFilter.validate(), self = this;
      if (!validatedAdvancedFilter) {
        return serialized;
      }

      var filterAsJSON = validatedAdvancedFilter.toJSON();

      // This is weird... but we want to display one operator ( for the sake of readability ),
      // but save a different one to AM Server
      if( onOptionChanged === 'allAppMissing' || onOptionChanged === 'allProfilesMissing') {
        serialized.Operator = 'OR';
      } else if (onOptionChanged === 'someAppMissing' || onOptionChanged === 'someProfilesMissing') {
        serialized.Operator = 'AND';
      } else {
        serialized.Operator = filterAsJSON.name;
      }

      filterAsJSON.params.forEach(function(item) {
        var infoItemName = item.params[0].params[0], infoItemGuid = null, itemValue1 = Em.isNone(item.params[1]) ? '' : item.params[1].params[0],
          infoItemType = null, isDateValue = false, itemValue2 = Em.isNone(item.params[2]) ? '' : item.params[2].params[0], units = 'Minutes', // units = "Minutes" because that's what AM server does
          itemValue3 = Em.isNone(item.params[3]) ? '' : item.params[3].params[0], infoItemIsCustomField;

        spec.resource.every(function (resource) {
          if (infoItemName === resource.attr) {
            infoItemGuid = resource.guid;
            infoItemType = resource.type;
            infoItemIsCustomField = resource.isCustomField ? true : false;

            return false;
          }

          return true;
        });

        if (IpType.detectInstance(infoItemType)) { // handle IP addresses
          itemValue1 = IpUtil.IPv4StringToInt(itemValue1);
          itemValue2 = Em.isEmpty(itemValue2) ? "" : IpUtil.IPv4StringToInt(itemValue2);
        } else if (InformationItem.detectInstance(infoItemType) && infoItemType.type === 'version') {
          itemValue1 = VersionUtil.formatVersionToInt(itemValue1);
        } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'percentage' ) {
          itemValue1 = Number(itemValue1.replace('%', '').trim());
        } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'distance' ) {
          itemValue1 = Number(itemValue1.toLowerCase().replace('m', '').trim());
        } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'clock-speed' ) {
          itemValue1 = Number(itemValue1.toLowerCase().replace('hz', '').trim());
        } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'smart-bytes' ) {
          itemValue1 = Number(itemValue1.toLowerCase().replace('byte', '').replace('bytes', '').trim());
        } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'no-thousands-sep' ) {
          itemValue1 = Number(itemValue1);
        } else if (IntervalType.detectInstance(infoItemType)) {
          itemValue1 = Number(IntervalUtil.formatIntervalToInt(itemValue1));
        } else if (infoItemType === Date) { // handle Dates
          // Checking the operator type in a very weird way
          // Note that 2 is the index where opDateClass is stored and 1 is unaryOpClass
          var opType = nameOpMap[opNameMap[item.name]][2] || nameOpMap[opNameMap[item.name]][1];
          opType = opType.create();

          // Don't need extra formatting for unary types
          if (!AdvancedFilters.UnaryOp.detectInstance(opType)) {
            if (AdvancedFilters.LastNOpBase.detectInstance(opType)) {
              units = itemValue2;
              itemValue2 = '';
            } else if (AdvancedFilters.BetweenOp.detectInstance(opType)) {
              units = itemValue3;
            } else if (AdvancedFilters.BetweenDatesOp.detectInstance(opType)) {
              var date1 = itemValue1, date2 = itemValue2;

              // AM Admin seems to be adding 12 hours for this operator so we might as well do it too
              date1.setHours(date1.getHours() + 12);
              date2.setHours(date2.getHours() + 12);

              itemValue1 = self.formatDateToString(date1);
              itemValue2 = self.formatDateToString(date2);
              isDateValue = true;
            } else {
              itemValue1 = self.formatDateToString(new Date(itemValue1));
              itemValue2 = itemValue2 ? self.formatDateToString(itemValue2) : '';
              isDateValue = true;
            }
          }
        } else if (infoItemType === Boolean) { // handle Unary ( aka boolean ) types
          // The value property is hardcoded in the operator definitions.
          // This value is important to send through with CompareValue.
          var opType = nameOpMap[opNameMap[item.name]][1].create();
          itemValue1 = opType.value;

          // HACK: FalseOp operator needs to serialized by sending operator = '==' and value = 'false'
          if (AdvancedFilters.FalseOp.detectInstance(opType)) {
            item.name = AdvancedFilters.TrueOp.create().name;
          }
        }

        serialized.CompareValue.push({
          "CachedInfoItemName": infoItemName,
          "CompareValue": itemValue1,
          "CompareValue2": itemValue2,
          "CompareValueUnits": units,
          "InfoItemID": infoItemGuid,
          "IsCustomField": infoItemIsCustomField,
          "Operator": opNameMap[item.name],
          "UseNativeType": typeof itemValue1 !== "string" || isDateValue
        });
      });

      return serialized;
    },

    serializeToFilter: function(filterCriteria, advancedFilterController) {
      var operands = Em.A(), self = this;

      filterCriteria.CompareValue.forEach(function(item) {
        var itemName = advancedFilterController.get('dataStoreSpec').resource.find(function(target){return target.guid === item.InfoItemID}).attr,
          itemFilterSpec = advancedFilterController.get('supportedColumnSpecs').find(function(target){return target.name === itemName}), opTypeKey = 0;

        // Overly complicated way of finding the key to use for the nameOpMap array
        if ( !Em.isEmpty(item.CompareValue) && itemFilterSpec.type === Date ) {
          opTypeKey = 2;
        } else if ( Em.isEmpty(item.CompareValue) || (typeof item.CompareValue) != 'string' && (typeof item.CompareValue) != 'number' ) {
          opTypeKey = 1;
        }

        // HACK: reverse of the FalseOp from serializeToObject() above
        if ( item.Operator === '==' && item.CompareValue === false ){
          item.Operator = '<>';
        }

        // Need to retrieve the operators as they would be created normally in case some of them have overridden functions
        var opFromAdvancedFilterController = Em.copy(advancedFilterController.getOpsFromResourceType(itemFilterSpec.type).find(function(target){
          return target.template instanceof nameOpMap[item.Operator][opTypeKey]
        }), true);

        var op = opFromAdvancedFilterController.template.set('lval',
          AdvancedFilters.Attr.create({
            value: itemName,
            type: EnumType.detectInstance(itemFilterSpec.type) || IpType.detectInstance(itemFilterSpec.type)
              || InformationItem.detectInstance(itemFilterSpec.type) || IntervalType.detectInstance(itemFilterSpec.type) ? String : itemFilterSpec.type
          })
        );

        // opTypeKey === 1 means we're working with a unary type
        if( opTypeKey === 0 || opTypeKey === 2) {
          // need to check whether we're recreating an enum type for the value
          var itemValue1 = itemFilterSpec.type.options ? itemFilterSpec.type.options.find(function (target) {
            return target.name === item.CompareValue
          }).id : item.CompareValue, itemValue2 = item.CompareValue2, units = item.CompareValueUnits;

          switch (op.get('lval.type')) {
            case Date:
              if ( AdvancedFilters.LastNOpBase.detectInstance(op) ) {
                op.set('rval1', AdvancedFilters.NumberLiteral.create({ value: itemValue1 }));
                op.set('rval2', AdvancedFilters.StringLiteral.create({ value: units}));
              } else if (AdvancedFilters.BetweenOp.detectInstance(op)) {
                op.set('rval1', AdvancedFilters.NumberLiteral.create({ value: itemValue1 }));
                op.set('rval2', AdvancedFilters.NumberLiteral.create({ value: itemValue2}));
                op.set('rval3', AdvancedFilters.StringLiteral.create({ value: units}));
              } else if (AdvancedFilters.DateOp.detectInstance(op)) {
                var hasTime = false;
                // some operands have the hours and minutes as a part of the date integer
                if ( AdvancedFilters.BeforeDateTimeOp.detectInstance(op) || AdvancedFilters.AfterDateTimeOp.detectInstance(op) ) {
                  hasTime = true;
                }
                op.set('rval', AdvancedFilters.StringLiteral.create({ value: self.formatUTCDateToUTCString(new Date(itemValue1), hasTime)}));
              } else if (AdvancedFilters.BetweenDatesOp.detectInstance(op)) {
                op.set('rval1', AdvancedFilters.DateLiteral.create({ value: new Date(itemValue1) }));
                op.set('rval2', AdvancedFilters.DateLiteral.create({ value: new Date(itemValue2) }));
              } else {
                op.set('rval', AdvancedFilters.StringLiteral.create({ value: itemValue1 }));
              }
              break;
            case Number:
              op.set('rval', AdvancedFilters.NumberLiteral.create({ value: Number(itemValue1) }));
              break;
            default:
              var infoItemType = itemFilterSpec.type;

              if ( IpType.detectInstance(infoItemType) ) {
                if ( Em.isEmpty(itemValue2) ) {
                  op.set('rval', AdvancedFilters.StringLiteral.create({ value: IpUtil.IPv4IntToString(itemValue1) }));
                } else {
                  op.setProperties({
                    rval1: AdvancedFilters.StringLiteral.create({ value: IpUtil.IPv4IntToString(itemValue1) }),
                    rval2: AdvancedFilters.StringLiteral.create({ value: IpUtil.IPv4IntToString(itemValue2) })
                  });
                }
              } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'version' ) {
                op.set('rval', AdvancedFilters.StringLiteral.create({ value: VersionUtil.formatIntToVersion(itemValue1) }));
              } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'percentage' ) {
                op.set('rval', AdvancedFilters.StringLiteral.create({ value: (itemValue1 + '%') }));
              } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'distance' ) {
                op.set('rval', AdvancedFilters.StringLiteral.create({ value: (itemValue1 + ' m') }));
              } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'clock-speed' ) {
                op.set('rval', AdvancedFilters.StringLiteral.create({ value: (itemValue1 + ' hz') }));
              } else if ( InformationItem.detectInstance(infoItemType) && infoItemType.type === 'smart-bytes' ) {
                op.set('rval', AdvancedFilters.StringLiteral.create({ value: (itemValue1 + ' bytes') }));
              } else if ( IntervalType.detectInstance(infoItemType) ) {
                op.set('rval', AdvancedFilters.StringLiteral.create({ value: IntervalUtil.formatIntToInterval(itemValue1) }));
              } else {
                op.set('rval', AdvancedFilters.StringLiteral.create({ value: itemValue1 }));
              }
          }
        }

        operands.addObject(AdvancedFilters.WrapOp.create({lval: op}));
      });

      return operands;
    }
  })
});