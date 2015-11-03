define([
  'ember',
  'help',
  'query',
  'guid',
  'jquery',
  'formatter',

  '../namespace',
  'desktop',
  'am-desktop',

  'packages/am/am-data',

  '../views/custom_field_item_add_edit_view'
], function(
  Em,
  Help,
  Query,
  Guid,
  $,
  Formatter,

  AmMobileDevice,
  Desktop,
  AmDesktop,

  AmData,

  CustomFieldAddEditView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    // Add labels
    tHeadingAdd: 'amCustomField.modals.addCustomField.heading'.tr(),
    tInProgressMsgAdd: 'amCustomField.modals.addCustomField.inProgressMessage'.tr(),
    tSuccessMsgAdd: 'amCustomField.modals.addCustomField.successMessage'.tr(),
    tErrorMsgAdd: 'amCustomField.modals.addCustomField.errorMessage'.tr(),
    tActionButtonLabelAdd: 'amCustomField.modals.addCustomField.buttons.actionButtonLabel'.tr(),

    // Edit labels
    tHeadingEdit: 'amCustomField.modals.editCustomField.heading'.tr(),
    tInProgressMsgEdit: 'shared.modals.inProgressMessage'.tr(),
    tSuccessMsgEdit: 'shared.modals.successMessage'.tr(),
    tErrorMsgEdit: 'shared.modals.errorMessage'.tr(),
    tActionButtonLabelEdit: 'amCustomField.modals.editCustomField.buttons.actionButtonLabel'.tr(),

    // Duplicate labels
    tHeadingDuplicate: 'amCustomField.modals.duplicateCustomField.heading'.tr(),

    tCopy: 'amDesktop.shared.copy'.tr(),

    tEnumerationValuePlaceholder: 'amCustomField.modals.addEditCustomField.enumerationValuePlaceholder'.tr(),

    confirmationView: CustomFieldAddEditView,

    // Fields
    fieldName: null,
    description: null,
    variableName: null,
    selectedDataTypeId: 1,
    selectedFormatAsId: null,
    enumListTextBox: null,

    formatAsVisible: false,
    enumIsVisible: false,

    currentMode: null,
    fieldId: null,
    isActionBtnDisabled: true,
    urlForHelp: null,

    // Fields to catch all changes and enable save button
    fieldNameOld: null,
    descriptionOld: null,
    variableNameOld: null,
    selectedDataTypeIdOld: null,
    selectedFormatAsIdOld: null,
    enumListTextBoxOld: null,

    lock: Guid.generate(),
    lockDuplicate: Guid.generate(),
    loadInProgress: false,
    isNameDuplicate: null,
    isVariableNameDuplicate: null,
    // Need it to verify unique name
    id: null,

    // Disable data type drop down in the edit mode
    dataTypeListDisabled: false,
    // Disable 'format as' for 'edit mode' && number type to prevent mixing decimal, byte and simple numeric data
    formatAsDisabled: false,

    // Need it to disable onFieldChanged event during loading data for edit
    paused: true,

    initProperties: function()  {
      var model = this.get('model');

      this.setProperties({
        paused: true,
        loadInProgress: false,
        currentMode: model.currentMode,
        fieldId:  model.fieldId,
        isActionBtnDisabled: true,
        isNameDuplicate: false,
        isVariableNameDuplicate: false,
        id: null,

        // for trim working = '', isDurty does not fork with null
        fieldName: '',
        description: '',
        variableName: '',
        selectedDataTypeId: 1,
        selectedFormatAsId: null,
        enumListTextBox: null,

        fieldNameOld: '',
        descriptionOld: '',
        variableNameOld: '',
        selectedDataTypeIdOld: 1,
        selectedFormatAsIdOld: null,
        enumListTextBoxOld: null
      });

      switch(this.get('currentMode')) {
        case 'EditMode':
        case 'DuplicateMode':
          // Load field information for current customFieldId
          this.loadCustomField(this.get('fieldId'));
          break;
        default:
          break;
      }

      this.loadElements();
    },

    loadElements: function() {
      switch(this.get('currentMode')) {
        case 'EditMode':
          this.setProperties({
            urlForHelp: Help.uri(1081),
            heading: this.get('tHeadingEdit'),
            inProgressMsg: this.get('tInProgressMsgEdit'),
            successMsg: this.get('tSuccessMsgEdit'),
            errorMsg: this.get('tErrorMsgEdit'),
            actionButtonLabel: this.get('tActionButtonLabelEdit'),
            dataTypeListDisabled: true,
            headingIconClass: 'icon-edit2'
          });
          break;
        case 'DuplicateMode':
          this.setProperties({
            urlForHelp: Help.uri(1080),
            heading: this.get('tHeadingDuplicate'),
            inProgressMsg: this.get('tInProgressMsgEdit'),
            successMsg: this.get('tSuccessMsgEdit'),
            errorMsg: this.get('tErrorMsgEdit'),
            actionButtonLabel: this.get('tActionButtonLabelEdit'),
            dataTypeListDisabled: false,
            headingIconClass: 'icon-duplicate'
          });
          break;
        default:
          // AddMode
          this.setProperties({
            urlForHelp: Help.uri(1077),
            heading: this.get('tHeadingAdd'),
            inProgressMsg: this.get('tInProgressMsgAdd'),
            successMsg: this.get('tSuccessMsgAdd'),
            errorMsg: this.get('tErrorMsgAdd'),
            actionButtonLabel: this.get('tActionButtonLabelAdd'),
            dataTypeListDisabled: false,
            formatAsDisabled: false,
            headingIconClass: 'icon-plus',
            paused: false
          });
          this.dataValidation();
          break;
      }

    },

    loadCustomField: function(id) {
      var self = this;
      this.set('loadInProgress', true);

      var query = Query.Search.create({
        context: { customFieldId: id }
      });

      AmData.get('stores.customFieldStore').acquire(this.get('lock'), query, function(data) {
        var content =  data.get('content')[0];

        self.setProperties({
          // id is used for name verification. Verification ignores the current id in the edit mode
          id: self.get('currentMode') === 'EditMode' ? content.get('data.id') : null,
          selectedDataTypeId: content.get('data.dataTypeNumber'),
          selectedFormatAsId: content.get('data.displayTypeNumber'),
          // Right now the data validation observes 'fieldName'
          fieldName: content.get('data.name'),
          description: content.get('data.description') === null ? '' : content.get('data.description'),
          variableName: self.get('currentMode') === 'DuplicateMode' ? null : content.get('data.variableName')
        });

        var enumList = content.get('data.enumerationList');
        self.setProperties({
          enumListTextBox: enumList ? enumList.join('\n') : null,
          // For the type 'number' drop down 'format as' is disabled only for Edit mode
          formatAsDisabled: self.get('selectedDataTypeId') === 2 && self.get('currentMode') === 'EditMode',
          loadInProgress: false
        });

        // Old value saving
        var fieldNameOld = self.get('fieldName') === null ? '' : self.get('fieldName');
        var variableNameOld = self.get('variableName') === null ? '' : self.get('variableName');

        self.setProperties({
          fieldNameOld: fieldNameOld.trim(),
          descriptionOld: self.get('description'),
          variableNameOld: variableNameOld.trim(),
          selectedDataTypeIdOld: self.get('selectedDataTypeId'),
          selectedFormatAsIdOld: self.get('selectedFormatAsId'),
          enumListTextBoxOld: self.get('enumListTextBox'),
          paused: false
        });

        // Make modification for duplicate mode
        if (self.get('currentMode') === 'DuplicateMode') {
          self.setProperties({
            fieldName: self.get('fieldName') + ' ' + self.get('tCopy'),
            fieldId: null
          });

          if(!Em.isNone(self.get('variableName'))) {
            self.set('variableName', self.get('variableName') + ' ' + self.get('tCopy'));
          }
        }

      });
    },

    nameChanged: function() {
      if(this.get('paused')) {
        return;
      }

      var fieldName = this.get('fieldName');

      if(!Em.isNone(fieldName)) {
        Em.run.next(this, function () {
          this.checkForDuplicateFields(fieldName, 'isNameDuplicate', 'name');
        });
      }
    }.observes('fieldName'),

    variableNameChanged: function() {
      if(this.get('paused')) {
        return;
      }

      var variableName = this.get('variableName');

      if(!Em.isNone(variableName)) {
        Em.run.next(this, function() {
          this.checkForDuplicateFields(variableName, 'isVariableNameDuplicate', 'variableName');
        });
      }
    }.observes('variableName'),

    checkForDuplicateFields: function(nameParam, failVarPropertyName, field) {
      this.set(failVarPropertyName, false);

      if (!nameParam || Em.isEmpty(nameParam.trim())) { return; }

      // trim so that we also consider it a duplicate name if it only differs by leading or trailing spaces
      var fieldName = Formatter.trim(nameParam).toLowerCase();

      // Issue query to check if content with that name already exists on the server
      var query = Query.Search.create({
        searchAttr: field,
        searchFilter: fieldName
      });

      AmData.get('stores.customFieldStore').acquire(this.get('lockDuplicate'), query, function(datasource) {
        var self = this;
        var content = datasource.get('content');
        content.forEach(function(action) {
          var name = action.get('data.' + field);

          // to make working trim() and toLowerCase()
          if(name === null) {
            name = '';
          }

          var id = action.get('data.id').toString();

          if (id !== self.get('id') && fieldName === name.trim().toLowerCase()) {
            self.set(failVarPropertyName, true);
          }
        });

      }, null, this, false);
    },

    dataValidation: function() {
      if(this.get('paused')) {
        return;
      }

      // Disable the action button in some cases

      // 7 - enumeration
      var isValidEnumField = this.get('selectedDataTypeId') === 7 ? !Em.isEmpty(this.get('enumListTextBox')) : true;

      return !Em.isEmpty(this.get('fieldName').trim()) &&
        !this.get('isVariableNameDuplicate') &&
        !this.get('isNameDuplicate') &&
        isValidEnumField;
    },

    onFieldChanged: function() {
      if(this.get('paused')) {
        return;
      }

      var variableName = this.get('variableName');
      variableName = Em.isNone(variableName) ? "" : variableName.trim();

      var isDirty = this.get('fieldNameOld') !== this.get('fieldName').trim() ||
        this.get('descriptionOld') !== this.get('description') ||
        this.get('variableNameOld') !== variableName ||
        this.get('selectedDataTypeIdOld') !== this.get('selectedDataTypeId') ||
        this.get('selectedFormatAsIdOld') !== this.get('selectedFormatAsId') ||
        this.get('enumListTextBoxOld') !== this.get('enumListTextBox');

      var isValid = this.dataValidation();
      var warningIsActive = this.get('isVariableNameDuplicate') || this.get('isNameDuplicate');

      // Analyzed only if no error, otherwise disable Save button
      this.set('isActionBtnDisabled', !isValid || !isDirty || warningIsActive);
    }.observes('fieldName', 'description', 'variableName', 'selectedDataTypeId', 'selectedFormatAsId',
               'enumListTextBox', 'isVariableNameDuplicate', 'isNameDuplicate'),

    onValidationStatusChanged: function() {
      this.dataValidation();
    }.observes('isNameDuplicate', 'isVariableNameDuplicate'),

    enumerationValuePlaceholder: function () {
      return this.get('tEnumerationValuePlaceholder').toString();
    }.property(),

    dataTypeList: function() {
      return AmData.get('stores.customFieldDataTypeStore').materializedObjects;
    }.property(),

    formatAsList: function() {
      return AmData.get('stores.customFieldFormatAsStore').materializedObjects;
    }.property(),

    onSelectedDataTypeIdChanged: function() {
      this.setProperties({
        // For the type 'number' drop down 'format as' is visible
        formatAsVisible: this.get('selectedDataTypeId') === 2,
        // For the type 'enumeration' grid is visible (right now the text box instead)
        enumIsVisible: this.get('selectedDataTypeId') === 7
      });
    }.observes('selectedDataTypeId'),

    buildAction: function() {
      this.set('urlForHelp', null);
      var variableName = this.get('variableName');

      if(!Em.isNone(variableName)) {
        variableName = variableName.trim();
      }

      return AmData.get('actions.AmCustomFieldUpdateAction').create({
        customFieldId: this.get('fieldId'),
        fieldName: Formatter.trim(this.get('fieldName')),
        description: this.get('description'),
        variableName: Em.isEmpty(variableName) ? null : variableName,
        dataType: this.get('selectedDataTypeId'),
        displayType: Em.isNone(this.get('selectedFormatAsId')) ? 1 : this.get('selectedFormatAsId'),
        enumerationList: Em.isBlank(this.get('enumListTextBox')) ? null : this.get('enumListTextBox').split('\n')
      });
    }
  });
});
