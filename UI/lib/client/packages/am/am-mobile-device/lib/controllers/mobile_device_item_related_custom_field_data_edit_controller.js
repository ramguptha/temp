define([
  'ember',
  'radioButtonGroup',
  'help',
  'query',
  'guid',
  'jquery',
  'locale',
  '../namespace',
  'desktop',
  'am-desktop',
  'formatter',

  'packages/am/am-data',
  'packages/platform/ip-util',
  'packages/platform/version-util',
  '../views/mobile_device_item_related_custom_field_data_edit_view'
], function (
  Em,
  RadioButtonGroup,
  Help,
  Query,
  Guid,
  $,
  Locale,
  AmMobileDevice,
  Desktop,
  AmDesktop,
  Formatter,

  AmData,
  IpUtil,
  VersionUtil,
  CustomFieldDataEditView
  ) {
  'use strict';

  // Data Types
  // -----
  //
  //var TYPE_STRING = 1;
  var TYPE_NUMBER = 2;
  var TYPE_BOOLEAN = 3;
  var TYPE_DATE = 4;
  var TYPE_FILE_VERSION = 5;
  var TYPE_IP_ADDRESS = 6;
  var TYPE_ENUMERATION = 7;


  return AmDesktop.ModalActionController.extend(Desktop.DateFieldUtilMixin, {
    Desktop: Desktop,

    headingIconClass: 'icon-edit-icon',
    addModalClass: 'custom-information-window',

    actionDescription: 'amMobileDevice.modals.editCustomFieldData.description'.tr('deviceName'),
    actionButtonLabel: 'amMobileDevice.modals.editCustomFieldData.buttons.actionButtonLabel'.tr(),

    heading: 'amMobileDevice.modals.editCustomFieldData.heading'.tr(),

    inProgressMsg: 'amMobileDevice.modals.editCustomFieldData.inProgressMessage'.tr(),
    successMsg: 'amMobileDevice.modals.editCustomFieldData.successMessage'.tr(),
    errorMsg: 'amMobileDevice.modals.editCustomFieldData.errorMessage'.tr(),

    tTrue: 'shared.true'.tr(),
    tFalse: 'shared.false'.tr(),

    confirmationView: CustomFieldDataEditView,

    deviceName: null,
    deviceId: null,
    item: null,
    fieldName: null,
    itemValue: null,

    numberValue: null,
    dataNumberTypeValue: null,
    decimalParsingError: false,

    loaded: false,
    fieldDescription: null,
    fieldDisplayType: null,

    booleanOption: null,
    booleanOptions: function() {
      return Em.A([
        {
          value: '0',
          label: this.get('tFalse'),
          class: 'is-radio-checked-false'
        }, {
          value: '1',
          label: this.get('tTrue'),
          class: 'is-radio-checked-true'
        }
      ]);
    }.property(),

    initProperties: function () {
      var model = this.get('model');
      var fieldName = model.item.get('data.information'),
      dataValue = model.item.get('data.dataValue'), dataType = model.item.get('data.dataTypeNumber'),
      dataDescription = model.item.get('data.description'), dataDisplayType = null;
      var dataNumberTypeValue;

      switch(dataType) {
        case TYPE_BOOLEAN:
          if (!Em.isNone(dataValue)) {
            if (dataValue.toString() === Locale.renderGlobals('shared.true').toString()) {
              dataValue = 1;
            } else {
              dataValue = 0;
            }
          }
          break;
        // 'Date'
        case TYPE_DATE:
          dataValue = dataValue === null ? null : new Date(dataValue);
          dataDisplayType = Locale.renderGlobals('amMobileDevice.modals.editCustomFieldData.types.date').toString();
          break;
        // 'Number'
        case TYPE_NUMBER:
          var fieldDisplayTypeId = model.item.get('data.displayType');
          switch(fieldDisplayTypeId) {
            // decimal
            case 1:
              dataDisplayType = Locale.renderGlobals('amMobileDevice.modals.editCustomFieldData.types.decimal').toString();
              dataNumberTypeValue = 'decimal';
              break;
            // decimalNoSeparators
            case 2:
              dataDisplayType = Locale.renderGlobals('amMobileDevice.modals.editCustomFieldData.types.decimalWithSeparator').toString();
              dataNumberTypeValue = 'decimal-no-separators';
              break;
            // bytes
            case 3:
              dataValue = model.item.get('data.dataNumberValue');
              dataDisplayType = Locale.renderGlobals('amMobileDevice.modals.editCustomFieldData.types.bytes').toString();
              dataNumberTypeValue = 'bytes';
              break;
          }
          break;
        // 'String'
        case 1:
          dataDisplayType = Locale.renderGlobals('amMobileDevice.modals.editCustomFieldData.types.text').toString();
          break;
        // 'File Version'
        case TYPE_FILE_VERSION:
          dataDisplayType = Locale.renderGlobals('amMobileDevice.modals.editCustomFieldData.types.fileVersion').toString();
          break;
        // 'IP Address'
        case TYPE_IP_ADDRESS:
          dataDisplayType = Locale.renderGlobals('amMobileDevice.modals.editCustomFieldData.types.ipAddress').toString();
          break;

      }

      this.resetProperties();

      this.setProperties({
        urlForHelp: Help.uri(1079),
        deviceId: model.deviceId,
        item: model.item,
        deviceName: model.deviceName,
        fieldDescription: dataDescription,
        fieldDisplayType: dataDisplayType,
        fieldName: fieldName,
        dataNumberTypeValue: dataNumberTypeValue,
        itemValue: dataValue,
        originalItemValue: dataValue
      });

    },

    onItemValueBoolValChanged: function () {
      if (this.get('loaded')) {
        this.set('itemValue', parseInt(this.get('booleanOption')));
      }
    }.observes('booleanOption'),

    inverseItemValue: function () {
      return !this.get('itemValue');
    }.property('itemValue'),

    fieldIsNumber: function () {
      return this.get('item.data.dataTypeNumber') === TYPE_NUMBER;
    }.property('item'),

    fieldIsDate: function () {
      return this.get('item.data.dataTypeNumber') === TYPE_DATE;
    }.property('item'),

    fieldIsIpAddress: function () {
      return this.get('item.data.dataTypeNumber') === TYPE_IP_ADDRESS;
    }.property('item'),

    onItemValChanged: function() {
      if(this.get('item.data.dataTypeNumber') === TYPE_ENUMERATION && !Em.isNone(this.get('enumContent'))) {
        this.get('enumContent').removeObject(this.get('enumSelectValObj'));
      }
    }.observes('itemValue'),

    enumSelectValObj:function() {
      return Em.Object.create({ value: null, label: Locale.renderGlobals('desktop.advancedFilterComponent.valuePrompt').toString() });
    }.property(),
    
    fieldIsEnum: function () {
      if (this.get('item.data.dataTypeNumber') === TYPE_ENUMERATION) {
        var query = Query.Search.create({
          context: { customFieldId: this.get('item.data.id') }
        }), self = this;

        AmData.get('stores.customFieldStore').acquireOne(null, query, function (dataSource) {
          var content = dataSource.get('content')[0].get('content.data.enumerationList'),
            contentArr = [];

          if(Em.isNone(self.get('itemValue'))) {
            contentArr.push(self.get('enumSelectValObj'));
          }

          content.forEach(function(item) {
            contentArr.push(Em.Object.create({ value: item, label: item }));
          });

          self.setProperties({
            enumContent: contentArr,
            loaded: true
          });
        });

        return true;
      }

      return false;
    }.property('item'),

    fieldIsBoolean: function () {
      if (this.get('item.data.dataTypeNumber') === TYPE_BOOLEAN) {
        this.setProperties({
          booleanOption: !Em.isNone(this.get('itemValue')) ? this.get('itemValue').toString() : null,
          loaded: true
        });

        return true;
      }

      return false;
    }.property('item'),

    isActionBtnDisabled: function () {
      var itemValue = this.get('itemValue');
      var isValidFileVersion = this.validateFileVersionFormat(itemValue);

      return itemValue === this.get('originalItemValue') ||
             Em.isEmpty(itemValue) ||
             !isValidFileVersion ||
             this.get('decimalParsingError') ||
             this.get('haveDateError');
    }.property('itemValue'),
    
    haveDateError: function() {
      if(this.get('fieldIsDate')) {
        return isNaN( new Date(this.get('itemValue')).getTime() );
      }

      return false;
    }.property('fieldIsDate', 'itemValue'),

    validateFileVersionFormat: function(itemValue) {
      var isValid = true;

      if(itemValue && this.get('item.data.dataTypeNumber') === TYPE_FILE_VERSION) {
        return VersionUtil.validateFileVersion(itemValue)
      }

      return isValid;
    },

    // need to reset the important, watched properties to their defaults
    resetProperties: function () {
      this.setProperties({
        deviceName: null,
        deviceId: null,
        fieldDescription: null,
        fieldDisplayType: null,
        item: null,
        fieldName: null,
        itemValue: null,
        loaded: false,
        dataNumberTypeValue: null,
        decimalParsingError: false,
        booleanOption: null
      });
    },

    buildAction: function () {
      this.set('urlForHelp', null);
      var formattedValue = this.get('itemValue'),
          type = this.get('item.data.dataTypeNumber'),
          dataValueHigh32 = 0,
          dataValueLow32 = 0;

      // special serialization for some types
      switch(type) {
        // 'IP Address'
        case TYPE_IP_ADDRESS:
          formattedValue = IpUtil.IPv4StringToInt(formattedValue);
          break;
        // 'Date'
        case TYPE_DATE:
          formattedValue = this.formatDateToUTCString(new Date(formattedValue));
          break;
        // 'File Version'
        case TYPE_FILE_VERSION:
          var valueArr = VersionUtil.formatFileVersionToInt(formattedValue);
          if(formattedValue = formattedValue.split('.').length === 4) {
            dataValueHigh32 = valueArr[0];
            dataValueLow32 = valueArr[1];
          } else {
            dataValueLow32 = valueArr[0];
          }
          formattedValue = 0;
          break;
        // 'Number'
        case TYPE_NUMBER:
          formattedValue = Formatter.stringToDecimal(formattedValue);
          break;
      }

      return AmData.get('actions.AmMobileDeviceRelatedCustomFieldDataUpdateAction').create({
        deviceId: this.get('deviceId'),
        items: [{
          'type': type,
          'value': formattedValue,
          'id': this.get('item.data.id'),
          'valueHigh32': dataValueHigh32,
          'valueLow32': dataValueLow32
        }]
      });
    }
  });
});
