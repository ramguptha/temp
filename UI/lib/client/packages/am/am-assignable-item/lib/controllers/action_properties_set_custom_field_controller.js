define([
  'ember',
   'am-data',
  'desktop',
  'am-desktop',
  'formatter',
  'packages/platform/ip-util',
  'packages/platform/version-util',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_set_custom_field.handlebars'
], function (
  Em,
  AmData,
  Desktop,
  AmDesktop,
  Formatter,
  IpUtil,
  VersionUtil,

  ActionItemBaseController,
  ActionBaseModalView,

  template
) {

  // Data Types
  // -----
  //
  var TYPE_STRING = 1;
  var TYPE_NUMBER = 2;
  var TYPE_BOOLEAN = 3;
  var TYPE_DATE = 4;
  var TYPE_FILE_VERSION = 5;
  var TYPE_IP_ADDRESS = 6;
  var TYPE_ENUMERATION = 7;

  // Action Properties Set Custom Field Controller
  // ==================================
  //

  return ActionItemBaseController.extend(Desktop.DateFieldUtilMixin, {
    Desktop: Desktop,

    hasDynamicProperties: true,
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    tSetValue: 'amAssignableItem.modals.actionProperties.setValue'.tr(),
    tRemoveValue: 'amAssignableItem.modals.actionProperties.removeValue'.tr(),
    tTrue: 'shared.true'.tr(),
    tFalse: 'shared.false'.tr(),

    tDecimal: 'amData.customFieldsFormatAsStore.decimal'.tr(),
    tDecimalNoSeparators: 'amData.customFieldsFormatAsStore.decimalNoSeparators'.tr(),
    tBytes: 'amData.customFieldsFormatAsStore.bytes'.tr(),

    tEnumSelectPlaceHolder: 'desktop.advancedFilterComponent.valuePrompt'.tr(),

    helpId: 1065,
    isAllSupported: true,

    fieldId: null,
    oldFieldId: null,

    fieldName: null,
    dataType: null,
    dataNumberTypeValue: null,

    dataValue: null,
    oldDataValue: null,

    removeValue: '0',
    oldRemoveValue: '0',
    isRemoveValueChecked: Em.computed.equal('removeValue', '1'),

    enumContent: Em.A(),
    enumLoaded: false,

    forceIpDataUpdate: null,

    // Flags for type of fields
    fieldIsNumber: false,
    fieldIsDate: false,
    fieldIsIpAddress: false,
    fieldIsEnum: false,
    fieldIsBoolean: false,
    fieldIsFileVersion: false,
    fieldIsString: false,

    // Message flags
    isFileVersionFormatWrong: false,

    // Hide the bullet for required field if:
    // - The type of field is boolean
    // - User selects Remove option
    isBulletHidden: function() {
      return this.get('fieldIsBoolean') || this.get('isRemoveValueChecked');
    }.property('fieldIsBoolean', 'isRemoveValueChecked'),

    dataTypeList: function() {
      var mappedList = {};

      AmData.get('stores.customFieldDataTypeStore').materializedObjects.forEach(function(type) {
        var id = type.get('data.id');

        mappedList[id] = type.get('data.dataTypeTitle');
      });

      return mappedList;
    }.property(),

    setAndRemoveOptions: function() {
      return Em.A([
        {
          value: '0',
          label: this.get('tSetValue'),
          class: 'is-radio-checked-on'
        },
        {
          value: '1',
          label: this.get('tRemoveValue'),
          class: 'is-radio-checked-off'
        }
      ]);
    }.property(),

    customFields: Em.A(),

    customFieldOptions: function() {
      var customFields = this.get('customFields');
      if (Em.isEmpty(customFields)) { return Em.A(); }

      Em.SelectOption.reopen({
        attributeBindings: ['optionClass'],
        optionClass: function() {
          return 'is-option-for-field'+ this.get('content.class');
        }.property('content')
      });

      return customFields;
    }.property('customFields'),

    // If the data type is Boolean, display radio button for each option
    booleanOptions: function() {
      return Em.A([
        {
          value: 'true',
          label: this.get('tTrue'),
          class: 'is-radio-checked-on'
        },
        {
          value: 'false',
          label: this.get('tFalse'),
          class: 'is-radio-checked-off'
        }
      ]);
    }.property(),

    initialize: function(model) {
      // Race condition is here. Init need to be called after loadCustomFields is done
      var self = this;

      // Make sure the old values are not restored.
      // Resetting feildId will reset all other related data to the customFieldOptions
      this.setProperties({
        dataValue: null,
        oldDataValue: null,

        fieldId: null
      });

      this.loadCustomFields(function() {
        self.initAllContent(model);
      });
    },

    // Load custom fields to populate the drop down options for custom fields
    loadCustomFields: function(completeLoad) {
      var self = this;
      this.set('paused', true);

      AmData.get('stores.customFieldStore').acquireAll(this.get('lock'), function(datasource) {
        var data = datasource.get('content');
        var isListOfOptionsEmpty = true;
        var customFieldOptions = Em.A();

        if (data.length > 0) {
          isListOfOptionsEmpty = false;

          customFieldOptions = data.map(function (content) {
            var field = content.get('data');

            return Em.Object.create({
              name: field.name,
              id: field.id,
              dataTypeNumber: field.dataTypeNumber,
              dataTypeString: self.customFieldFormatAsDisplay(field.dataTypeNumber, field.dataType, field.displayTypeNumber),
              class: field.id,
              displayTypeId: field.displayTypeNumber
            });
          });

        }

        self.setProperties({
          customFields: customFieldOptions,
          isListOfOptionsEmpty: isListOfOptionsEmpty,
          paused: false
        });

        if ('function' === typeof completeLoad) {
          completeLoad();
        }

      }, null);
    },

    customFieldFormatAsDisplay: function(dataTypeNumber, dataTypeString, displayTypeNumber) {
      var displayStr = dataTypeString;

      // Additional formatting for Number type
      if(dataTypeNumber == 2) {
        switch(displayTypeNumber) {
          case 1:
            displayStr = this.get('tDecimal');
            break;
          case 2:
            displayStr = this.get('tDecimalNoSeparators');
            break;
          case 3:
            displayStr = this.get('tBytes');
            break;
        }
      }
      return displayStr;
    },

    loadEnumContent: function() {
      if (this.get('dataType') === TYPE_ENUMERATION) {
        this.set('enumLoaded', false);

        var self = this;
        var queryContext = { context: { customFieldId: this.get('fieldId') }};

        AmData.get('stores.customFieldStore').acquire(this.get('lock'), queryContext, function (dataSource) {
          var content = dataSource.get('content')[0].get('data.enumerationList'),
            contentArr = Em.A();

          if(!Em.isEmpty(content)) {
            content.forEach(function(item) {
              contentArr.push(Em.Object.create({ value: item }));
            });
          }

          self.setProperties({
            enumContent: contentArr,
            enumLoaded: true
          });
        });
      }
    }.observes('fieldId', 'dataType'),

    customFieldsLoaded: function() {
      var fieldsOptions = this.get('customFields');
      if (Em.isEmpty(fieldsOptions)) { return; }

      // Set the default custom field on New Set Custom Field action
      if (!this.get('isEditMode')) {
        this.set('fieldId', fieldsOptions[0].get('id'));
      }

    }.observes('customFields.[]'),

    getNumberDisplayType: function (dataType, displayTypeId) {
      // This variable is binding decimal control to display 3 different types: decimal, decimal no-separators and bytes
      var dataNumberTypeValue = null;

      switch (dataType) {
        case TYPE_NUMBER:
          switch (displayTypeId) {
            // decimal
            case 1:
              dataNumberTypeValue = 'decimal';
              break;
            // decimalNoSeparators
            case 2:
              dataNumberTypeValue = 'decimal-no-separators';
              break;
            // bytes
            case 3:
              dataNumberTypeValue = 'bytes';
              break;
          }
          break;
      }
      return dataNumberTypeValue;
  },

    dataTypeString: function() {
      var fieldId = this.get('fieldId'),
        customFieldOptions = this.get('customFieldOptions'),
        selectedOption = customFieldOptions.filterBy('id', fieldId),
        value = '';

      if(!Em.isEmpty(selectedOption)) {
        value = selectedOption[0].get('dataTypeString');
      }

      return value;
    }.property('fieldId', 'customFieldOptions'),

    // Set the data type and field name upon changing the custom field's option
    fieldIdChanged: function() {
      var fieldId = this.get('fieldId');
      var customFieldOptions = this.get('customFieldOptions');
      var selectedOption = customFieldOptions.filterBy('id', fieldId);

      if (this.get('paused')) { return; }

      if (!Em.isEmpty(selectedOption)) {
        var displayTypeId = selectedOption[0].get('displayTypeId');
        var dataType = selectedOption[0].get('dataTypeNumber');
        var dataTypeString = selectedOption[0].get('dataTypeString');
        var dataNumberTypeValue = this.getNumberDisplayType(dataType, displayTypeId);

        this.setProperties({
          fieldName: selectedOption[0].get('name'),
          dataType: dataType,
          removeValue: '0',
          dataNumberTypeValue: dataNumberTypeValue
        });

        var oldDataValue = this.get('oldDataValue');
        var dataValue = (oldDataValue && fieldId === this.get('oldFieldId')) ? oldDataValue : null;

        this.set('dataValue', dataValue);

        this.setBooleanFieldDefault();

      } else if(customFieldOptions.length > 0) {
        // If the selected fieldId is not available in the list of custom fields anymore,
        // reset the list so it looks like the situation in the New Action
        this.set('fieldId', customFieldOptions[0].get('id'));
      }

    }.observes('fieldId', 'customFieldOptions.[]'),

    // If the field is boolean type and user selects the 'Set' option,
    // set the default of this field to 'true'
    setBooleanFieldDefault: function() {
      var dataValue = this.get('dataValue');

      if (this.get('fieldIsBoolean') && Em.isEmpty(dataValue)) {
        this.set('dataValue', 'true');
      }
    },

    // If there is an existing original data value for the selected field, reset it
    // If the 'Set' option is selected set the Boolean field's default
    removeValueChanged: function() {
      if (this.get('isRemoveValueChecked')) {
        this.set('dataValue', this.get('fieldId') === this.get('oldFieldId') ? this.get('oldDataValue') : null);

        // IP Field Component does not get updated unless the dataValue is changed
        // and dataValue does not get changed until all 4 octets get filled and validated
        // This is to force the IP component to clear the data upon selecting Remove
        if (Em.isEmpty(this.get('dataValue'))) {
          this.set('forceIpDataUpdate', true);
        }

      } else {
        this.set('forceIpDataUpdate', false);
        this.setBooleanFieldDefault();
      }
    }.observes('removeValue'),

    // Set the flag for the selected data type to be used in the template
    setFieldFlags: function () {
      this.resetFieldFlags();

      switch(this.get('dataType')) {
        case TYPE_NUMBER:
          this.set('fieldIsNumber', true);
          break;
        case TYPE_DATE:
          this.set('fieldIsDate', true);
          break;
        case TYPE_IP_ADDRESS:
          this.set('fieldIsIpAddress', true);
          break;
        case TYPE_ENUMERATION:
          this.set('fieldIsEnum', true);
          break;
        case TYPE_BOOLEAN:
          this.set('fieldIsBoolean', true);
          break;
        case TYPE_FILE_VERSION:
          this.set('fieldIsFileVersion', true);
          this.set('fieldIsString', true);
          break;
        case TYPE_STRING:
          this.set('fieldIsString', true);
          break;
      }
    }.observes('dataType'),

    // Reset data type flags
    resetFieldFlags: function() {
      this.setProperties({
        fieldIsNumber: false,
        fieldIsDate: false,
        fieldIsIpAddress: false,
        fieldIsEnum: false,
        fieldIsBoolean: false,
        fieldIsFileVersion: false,
        fieldIsString: false,

        isFileVersionFormatWrong: false
      })
    },

    dynamicPropertiesChanged: function() {
      if (!this.get('isInitializationDone')) { return; }

      this.setProperties({
        isActionBtnDisabled: this.getIsEmpty() || !this.getIsDirty(),
        isSaveAndAssignActionBtnDisabled: this.getIsEmpty()
      });

    }.observes('name',
      'description',
      'isNameDuplicate',
      'isInitializationDone',
      'iosChecked',
      'androidChecked',
      'windowsChecked',
      'fieldId',
      'dataValue',
      'removeValue'),

    getIsEmpty: function() {
      var setValueChecked = this.get('removeValue') === '0';
      var isDataFieldEmpty = false;
      var isRequiredFieldEmpty = this.get('isListOfOptionsEmpty') || Em.isEmpty(this.get('fieldId'));
      var dateValid = true;

      if (!isRequiredFieldEmpty) {
        // If a field with any of these types is selected it can not be set to an empty value
        isDataFieldEmpty = setValueChecked && (Em.isNone(this.get('dataValue')) || Em.isEmpty(this.get('dataValue').toString().trim())) &&
        (this.get('fieldIsEnum') || this.get('fieldIsBoolean') || this.get('fieldIsFileVersion') || this.get('fieldIsIpAddress') ||
        this.get('fieldIsNumber') || this.get('fieldIsDate') || this.get('fieldIsString'));
      }

      // in case the user enters all spaces for the data, the year gets funny
      if(this.get('fieldIsDate') && !Em.isNone(this.get('dataValue')) && this.get('dataValue') instanceof Date) {
        dateValid = this.get('dataValue').getYear() > 0;
      }

      var isFileVersionFormatValid = this.validateFileVersionFormat();

      return this.getBasicIsEmpty() || isRequiredFieldEmpty || !isFileVersionFormatValid || isDataFieldEmpty || !dateValid;
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('fieldId') !== this.get('oldFieldId') ||
        this.get('dataValue') !== this.get('oldDataValue') ||
        this.get('removeValue') !== this.get('oldRemoveValue');
    },

    validateFileVersionFormat: function() {
      var isValid = true;

      // Only validate if Set option is selected
      if(this.get('fieldIsFileVersion') && !this.get('isRemoveValueChecked')) {
        var dataValue = this.get('dataValue');
        isValid = dataValue ? VersionUtil.validateFileVersion(dataValue) : true;
      }

      this.set('isFileVersionFormatWrong', !isValid);

      return isValid;
    },

    setDynamicProperties: function(data) {
      var dataTypeList = this.get('dataTypeList'),
        customFieldsIdList = this.get('customFieldOptions') || Em.A();

      var fieldId = data.fieldId;

      // Handle the case that a custom field is deleted but the server returns
      // the old fieldId as part of the action's result-set.
      // FieldId is set to null by default, but we want to keep the original id to enable the save button
      if (!Em.isEmpty(customFieldsIdList) && !customFieldsIdList.mapBy('id').contains(fieldId)) {
        this.set('oldFieldId', fieldId);
        return;
      }

      var fieldName = data.fieldName,
        dataType = data.dataType,
        formattedValue = data.dataValue,
        dataValueHigh32 = data.dataValueHigh32,
        dataValueLow32 = data.dataValueLow32,
        removeValue = data.removeValue;

      // Rise field change event before type analyzing to save dataNumberTypeValue type
      this.setProperties({
        fieldId: fieldId,
        oldFieldId: fieldId
      });

      // special formatting for some data types
      if (!Em.isNone(formattedValue)) {
        switch (dataType) {
          case TYPE_NUMBER:
            switch(this.get('dataNumberTypeValue')) {
              case 'decimal':
                formattedValue = Formatter.decimalToString(formattedValue);
                break;
              default:
                break;
            }
            break;
          case TYPE_DATE:
            formattedValue = new Date(formattedValue);
            break;
          case TYPE_IP_ADDRESS:
            formattedValue = !Em.isEmpty(formattedValue) ? Formatter.formatIPv4Address(formattedValue) : formattedValue;
            break;
          case TYPE_BOOLEAN:
            formattedValue = formattedValue.toString();
            break;
          case TYPE_FILE_VERSION:
            formattedValue = removeValue === '1' ? null : VersionUtil.formatIntToFileVersion(dataValueHigh32, dataValueLow32);
            break;
        }
      }

      this.setProperties({
        fieldName: fieldName,

        dataType: dataType,

        dataValue: formattedValue,
        oldDataValue: formattedValue,

        removeValue: removeValue,
        oldRemoveValue: removeValue
      });
    },

    getFormattedPropertyList: function() {
      var removeValue = parseInt(this.get('removeValue'));

      var formattedValue = this.get('dataValue'),
        dataType = this.get('dataType'),
        dataTypeList = this.get('dataTypeList'),
        dataValueHigh32 = null,
        dataValueLow32 = null;

      if(!removeValue) {
        // special formatting for some data types
        switch (dataType) {
          case TYPE_NUMBER:
            formattedValue = Formatter.stringToDecimal(String(formattedValue));
            break;
          case TYPE_IP_ADDRESS:
            formattedValue = IpUtil.IPv4StringToInt(formattedValue);
            break;
          case TYPE_DATE:
            formattedValue = this.formatDateToUTCString(formattedValue);
            break;
          case TYPE_BOOLEAN:
            formattedValue = Formatter.stringToBoolean(formattedValue);
            break;
          case TYPE_FILE_VERSION:
            var valueArr = VersionUtil.formatFileVersionToInt(formattedValue);
            if (formattedValue = formattedValue.split('.').length === 4) {
              dataValueHigh32 = valueArr[0];
              dataValueLow32 = valueArr[1];
            } else {
              dataValueLow32 = valueArr[0];
            }
            formattedValue = 0;
            break;
        }

        // Server needs the corresponding number for the specific type
        for (var typeNum in dataTypeList) {
          if (dataTypeList[typeNum] === dataType) {
            dataType = parseInt(typeNum);
          }
        }

      } else {
        // special formatting for some data types
        switch (dataType) {
          case TYPE_NUMBER:
            formattedValue = 0;
            break;
          case TYPE_DATE:
            formattedValue = formattedValue === null ?
              this.formatDateToUTCString(new Date()) : this.formatDateToUTCString(formattedValue);
            break;
          case TYPE_IP_ADDRESS:
            formattedValue = null;
            break;
        }
      }

      return {
        fieldId: this.get('fieldId'),
        fieldName: this.get('fieldName'),
        dataType: dataType,
        dataValue: Em.isNone(formattedValue) ? "" : formattedValue,
        dataValueHigh32: dataValueHigh32,
        dataValueLow32: dataValueLow32,
        removeValue: parseInt(this.get('removeValue'))
      }
    }
  });
});
