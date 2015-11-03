define([
  'ember',
  'query',
  'guid',
  'am-data',
  'am-desktop',

  './action_item_base_controller',
  '../views/action_properties_set_wallpaper_view'
], function (
  Em,
  Query,
  Guid,
  AmData,
  AmDesktop,

  ActionItemBaseController,
  AmActionsPropertiesSetWallpaperView
) {

  // Action Properties Set Wallpaper Controller
  // ==================================
  //

  return ActionItemBaseController.extend({

    hasDynamicProperties: true,
    propertiesView: AmActionsPropertiesSetWallpaperView,

    helpId: 1063,
    lock: Guid.generate(),

    isIosSupported: true,

    isScreenOptionEmpty: false,

    lockScreenChecked: true,
    oldLockScreenChecked: null,

    homeScreenChecked: true,
    oldHomeScreenChecked: null,

    wallpaper: null,
    oldWallpaper: null,

    isImageError: false,

    wallpaperPreviewStyle: function() {
      return 'background-image:url(' + this.get('wallpaper') + ')';
    }.property('wallpaper'),

    initialize: function(model) {
      this._super(model);

      this.set('isImageError', false);

      // If we are in editing mode, retrieve the possible existing wallpaper picture
      var actionId = model.actionId;
      if (actionId) {
        this.loadWallpaper(model.actionId);
      }

    },

    loadWallpaper: function(actionId) {
      var self = this;

      var queryContext = {
        context: { actionId: actionId }
      };

      AmData.get('stores.actionWallpaperStore').acquire(this.get('lock'), queryContext, function (datasource) {
        var wallpaperData = datasource.get('content').objectAt(0).get('data.wallpaper');

        self.setProperties({
          wallpaper: wallpaperData,
          oldWallpaper: wallpaperData
        });

      }, null, false, true);
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
      'iosChecked',
      'lockScreenChecked',
      'homeScreenChecked',
      'wallpaper',
      'isImageError'),

    getIsEmpty: function() {
      var isScreenOptionValid = this.validateScreenOptions();

      return this.getBasicIsEmpty() || !isScreenOptionValid || !this.get('wallpaper') || this.get('isImageError');
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('lockScreenChecked') !== this.get('oldLockScreenChecked') ||
        this.get('homeScreenChecked') !== this.get('oldHomeScreenChecked') ||
        this.get('wallpaper') !== this.get('oldWallpaper');
    },

    validateScreenOptions: function() {
      var isInvalid = !(this.get('lockScreenChecked') || this.get('homeScreenChecked'));
      this.set('isScreenOptionEmpty', isInvalid);

      return !isInvalid;
    },

    setDynamicProperties: function(data) {
      var lockScreenChecked = data.lockScreenChecked,
        homeScreenChecked = data.homeScreenChecked;

      this.setProperties({
        lockScreenChecked: lockScreenChecked,
        oldLockScreenChecked: lockScreenChecked,

        homeScreenChecked: homeScreenChecked,
        oldHomeScreenChecked: homeScreenChecked
      });
    },

    resetDynamicProperties: function() {
      var properties = {
        lockScreenChecked: true,
        homeScreenChecked: true
      };

      this.setDynamicProperties(properties);

      this.setProperties({
        isScreenOptionEmpty: false,
        isImageError: false,
        wallpaper: null
      });
    },

    getFormattedPropertyList: function() {
      var wallpaper = this.get('wallpaper');

      // Remove the header part (data:image/png,base64,) from the image's data
      wallpaper = wallpaper.substring(wallpaper.indexOf(',') + 1, wallpaper.length);

      return {
        wallpaper: wallpaper,
        lockScreenChecked: this.get('lockScreenChecked'),
        homeScreenChecked: this.get('homeScreenChecked')
      };
    }
  });
});
