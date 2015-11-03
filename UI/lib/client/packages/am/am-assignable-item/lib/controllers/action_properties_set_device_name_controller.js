define([
  'ember',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_set_device_name.handlebars'
], function (
  Em,

  ActionItemBaseController,
  ActionBaseModalView,

  template
) {

  // Action Properties Set Device Name Controller
  // ==================================
  //
  return ActionItemBaseController.extend({

    hasDynamicProperties: true,
    propertiesView: ActionBaseModalView.extend({
      defaultTemplate: Em.Handlebars.compile(template)
    }),

    helpId: 1064,

    isIosAndroidSupported: true,

    deviceName: null,
    oldDeviceName: null,

    isReenrollment: false,

    dynamicPropertiesChanged: function() {
      if (!this.get('isInitializationDone')) { return; }

      var isInvalid = this.getIsEmpty();

      this.setProperties({
        isActionBtnDisabled: isInvalid || !this.getIsDirty(),
        isSaveAndAssignActionBtnDisabled: isInvalid
      });

    }.observes('name',
      'description',
      'isNameDuplicate',
      'iosChecked',
      'androidChecked',
      'deviceName'),

    getIsEmpty: function() {
      return this.getBasicIsEmpty() || this.getIsEmptyField(this.get('deviceName'));
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('deviceName') !== this.get('oldDeviceName');
    },

    setDynamicProperties: function(data) {
      var deviceName = data.deviceName;

      this.setProperties({
        deviceName: deviceName,
        oldDeviceName: deviceName
      });
    },

    resetDynamicProperties: function() {
      var properties = { deviceName: null };

      this.setDynamicProperties(properties);
    },

    getFormattedPropertyList: function() {
      return this.getProperties('deviceName'.w());
    }
  });
});
