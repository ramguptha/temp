define([
  'ember',
  'desktop',
  'guid',
  'am-data',
  'formatter',

  'text!../templates/action_properties_os_platforms.handlebars'
], function (
  Em,
  Desktop,
  Guid,
  AmData,
  Formatter,

  platformTemplate
) {
  'use strict';

  // Assignable Actions Base
  // =======================
  //
  // This mixin contains properties/functions that are used by:
  // ActionItemEditController - ActionItemCreateController - ActionItemDuplicateController

  var osPlatformsList = Em.A([
    { key: 0, value: { ios: false, android: false, windows: false } },
    { key: 1, value: { ios: true,  android: false, windows: false } },
    { key: 2, value: { ios: false, android: true,  windows: false } },
    { key: 3, value: { ios: true,  android: true,  windows: false } },
    { key: 4, value: { ios: false, android: false, windows: true } },
    { key: 5, value: { ios: true,  android: false, windows: true } },
    { key: 6, value: { ios: false, android: true,  windows: true } },
    { key: 7, value: { ios: true,  android: true,  windows: true } }
  ]);

  return Em.Mixin.create({

    helpId: null,
    MAX_TEXT_SIZE: 140,

    lock: Guid.generate(),
    paused: true,
    seed: null,

    // Common properties between all actions
    // ----------
    //
    SupportedPlatformsView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(platformTemplate)
    }),

    hasDynamicProperties: false,

    // Some of the actions have a drop down list that will be populated
    // with the result set of a specific endpoint
    // This flag shows a message if the result set is empty
    isListOfOptionsEmpty: false,

    // OS Platform related properties
    // ---------------
    isSupportedPlatformEmpty: false,

    noPlatformSupported: false,
    isIosSupported: false,
    isAndroidSupported: false,
    isIosAndroidSupported: false,
    isAllSupported: false,

    iosChecked: false,
    oldIosChecked: false,
    androidChecked: false,
    oldAndroidChecked: false,
    windowsChecked: false,
    oldWindowsChecked: false,

    dataStore: function() {
      return AmData.get('stores.actionsStore');
    }.property(),

    // Load and initialize properties for selected action
    initialize: function(model) {
      // Reset dynamic properties only if a new action is created
      if (!this.get('isEditMode') && !this.get('isDuplicateMode')) {
        this.resetDynamicProperties(model);
      }

      this.initAllContent(model);
    },

    // Race condition is happened sometimes. Init need to be called after loadCustomFields is done (probably it is only the one place)
    // Created a separate method to call it manually
    initAllContent: function(model) {
      var actionId = model.actionId;

      // Each action has a specific requirement for OS platform
      this.initOsPlatform(model.osPlatformEnum);

      // Only Edit and Duplicate action will have an existing actionId/content
      if (actionId) {
        this.loadContent(actionId);
      }
    },

    initOsPlatform: function(osPlatformEnum) {
      var platform = osPlatformsList.findBy('key', osPlatformEnum);
      platform = platform ? platform.value : null;

      this.setProperties({
        isSupportedPlatformEmpty: false,

        // Check the OS platform check boxes for the specific action id
        iosChecked: platform ? platform.ios : false,
        oldIosChecked: platform ? platform.ios : false,

        androidChecked: platform ? platform.android : false,
        oldAndroidChecked: platform ? platform.android : false,

        windowsChecked: platform ? platform.windows : false,
        oldWindowsChecked: platform ? platform.windows : false
      });
    },

    // Reset any dynamic properties if there is one
    resetDynamicProperties: function() {
      // Overwrite by inherited classes if necessary
    },

    // This function is used by Edit and Duplicate Controllers to load existing data
    loadContent: function(actionId) {
      var self = this;

      self.setProperties({
        isInitializationDone: false,
        paused: true
      });

      this.get('dataStore').acquireOne(this.get('lock'), actionId, function (datasource) {
        var data = datasource.get('content').objectAt(0).get('data');

        if (self.get('hasDynamicProperties')) {
          self.setDynamicProperties(data);
        }

        self.setProperties({
          isInitializationDone: true,
          seed: data.seed,
          paused: false
        });

      }, null, false, false);
    },

    // Return true if
    // the basic required field (name) is empty or
    // no supported platform is selected
    getBasicIsEmpty: function() {
      var isNameValid = !this.getIsEmptyField(this.get('name')) && !this.get('isNameDuplicate');

      var isSupportedPlatformValid = this.validateSupportedPlatform();

      return !isNameValid || !isSupportedPlatformValid;
    },

    // Return true if there is a change in the basic fields common between all actions
    // These fields are: name, description, checkboxes for iOS, Android, Windows phones.
    getBasicIsDirty: function() {
      return this.get('name').trim() !== this.get('oldName').trim() ||
        this.get('description') !== this.get('oldDescription') ||
        this.get('iosChecked') !== this.get('oldIosChecked') ||
        this.get('androidChecked') !== this.get('oldAndroidChecked') ||
        this.get('windowsChecked') !== this.get('oldWindowsChecked');
    },

    validateSupportedPlatform: function() {
      var isSupportedPlatformEmpty = false;

      if (this.get('isAllSupported')) {
        isSupportedPlatformEmpty = !(this.get('iosChecked') || this.get('androidChecked') || this.get('windowsChecked'));
      } else if (this.get('isIosAndroidSupported')) {
        isSupportedPlatformEmpty = !(this.get('iosChecked') || this.get('androidChecked'));
      }

      this.set('isSupportedPlatformEmpty', isSupportedPlatformEmpty);

      return !isSupportedPlatformEmpty;
    },

    // Return the latest selected checkboxes for supported platforms
    getPlatformEnum: function() {
      var iosChecked = this.get('iosChecked');
      var androidChecked = this.get('androidChecked');
      var windowsChecked = this.get('windowsChecked');

      var osType = osPlatformsList.filter(function(os) {
        os = os.value;
        return os.ios === iosChecked && os.android === androidChecked && os.windows === windowsChecked;
      });

      return osType ? osType[0].key : 0;
    },

    // Some textarea fields display the number of remaining characters user can type
    getLengthRemaining: function(message) {
      return this.get('MAX_TEXT_SIZE') - (message ? message.toString().length : 0);
    },

    getIsEmptyField: function(value) {
      return Em.isEmpty(value) || Em.isEmpty(Formatter.trim(value))
    },

    // Properties/methods related to the Register and Retire User Actions
    // -----------------
    //
    vppAccounts: Em.A(),
    vppAccountOptions: function() {
      var vppAccounts = this.get('vppAccounts');
      if (Em.isEmpty(vppAccounts)) { return; }

      Em.SelectOption.reopen({
        attributeBindings: ['optionClass'],
        optionClass: function() {
          return 'is-option-for-vpp-'+ this.get('content.class');
        }.property('content')
      });

      return vppAccounts;
    }.property('vppAccounts'),

    loadVppAccounts: function() {
      var self = this;
      this.set('paused', true);

      AmData.get('stores.vppAccountsStore').acquireAll(this.get('lock'), function(datasource) {
        var data = datasource.get('content');
        var isListOfOptionsEmpty = true;
        var vppAccountOptions = Em.A();

        if (data.length > 0) {
          isListOfOptionsEmpty = false;

          vppAccountOptions = data.map(function (content) {
            var account = content.get('data');

            return Em.Object.create({
              name: account.name,
              type: parseInt(account.id),
              uuid: account.uuid,
              class: account.name
            });
          });

          self.setProperties({
            vppRecordId: vppAccountOptions[0].get('type'),
            vppAccounts: vppAccountOptions
          });
        }

        self.setProperties({
          isListOfOptionsEmpty: isListOfOptionsEmpty,
          paused: false
        });

      }, null);
    },

    // Set the default account on New Register/Retire actions
    vppRecordIdChanged: function() {
      var vppAccountOptions = this.get('vppAccountOptions');
      if (!vppAccountOptions || Em.isEmpty(vppAccountOptions)) { return; }

      var option = vppAccountOptions.filterBy('type', this.get('vppRecordId'));
      if (!Em.isEmpty(option)) {
        this.set('vppUniqueId', option[0].get('uuid'));
      }
    }.observes('vppRecordId')
    //
    // End of Properties/methods of Register/Retire User Actions
  });
});
