define([
  'ember',
  'am-data',

  './action_item_base_controller',
  '../views/action_item_base_modal_view',

  'text!../templates/action_properties_remove_configuration.handlebars'
], function (
  Em,
  AmData,

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

    helpId: 1071,

    noPlatformSupported: true,

    tIosProfilesTitle: 'amAssignableItem.modals.actionProperties.iosProfilesTitle'.tr(),
    tAndroidProfilesTitle: 'amAssignableItem.modals.actionProperties.androidProfilesTitle'.tr(),

    profileId: null,
    oldProfileId: null,

    profilesList: Em.A(),

    profileOptions: function() {
      var profilesList = this.get('profilesList');
      if (Em.isEmpty(profilesList)) { return; }

      Em.SelectOption.reopen({
        attributeBindings: ['optionClass'],
        optionClass: function() {
          return 'is-option-for-'+ this.get('content.class');
        }.property('content')
      });

      return profilesList;
    }.property('profilesList'),


    initialize: function(model) {
      this.loadProfiles(model);
    },

    loadProfiles: function(model) {
      var self = this;
      var iosProfilesTitle = this.get('tIosProfilesTitle').toString(),
        androidProfilesTitle = this.get('tAndroidProfilesTitle').toString();

      this.set('paused', true);

      AmData.get('stores.configurationProfileStore').acquireAll(this.get('lock'), function(datasource) {
        var data = datasource.get('content');

        var isListOfOptionsEmpty = true;
        var iosProfiles = Em.A(), androidProfiles = Em.A();

        if (data.length > 0) {
          isListOfOptionsEmpty = false;

          data.forEach(function (content) {
            var profile = content.get('data');
            var osType = profile.osPlatformEnum;

            // Group the profile options in the Select drop down based on the type of OS
            if (osType === 10) {
              iosProfiles.push(
                Em.Object.create({
                  name: profile.name,
                  id: profile.identifier,
                  group: iosProfilesTitle,
                  osPlatformEnum: 1,
                  class: profile.name
                })
              );

            } else if (osType === 11) {
              androidProfiles.push(
                Em.Object.create({
                  name: profile.name,
                  id: profile.identifier,
                  group: androidProfilesTitle,
                  osPlatformEnum: 2
                })
              );
            }
          });

          var profilesList = iosProfiles.concat(androidProfiles);
          var currentProfilesList = self.get('profilesList');

          if (Em.isEmpty(currentProfilesList)) {
            self.set('profilesList', profilesList);
          }

          if (self.get('isEditMode') === false) {
            self.set('profileId', profilesList[0].id);
          }

          self.initAllContent(model);
        }

        self.setProperties({
          isListOfOptionsEmpty: isListOfOptionsEmpty,
          paused: false
        });

      }, null);
    },

    profileOptionsChanged: function() {
      var self = this;
      var profileId = this.get('profileId'),
        profilesList = this.get('profilesList');

      if (profilesList && profilesList.length > 0) {
        profilesList.forEach(function (profile) {
          if (profile.id === profileId) {
            self.set('osPlatformEnum', profile.osPlatformEnum);
          }
        })
      }
    }.observes('profileId', 'profilesList.[]'),

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
      'profileId'),

    getIsEmpty: function() {
      return this.getBasicIsEmpty() || Em.isEmpty(this.get('profileId'));
    },

    getIsDirty: function() {
      return this.getBasicIsDirty() ||
        this.get('profileId') !== this.get('oldProfileId');
    },

    setDynamicProperties: function(data) {
      var profilesIdList = this.get('profilesList').mapBy('id'),
        profileId = data.profileId,
        oldProfileId = data.profileId;

      // If the original profile has been removed elsewhere and
      // the list of available profiles doesn not include it anymore
      // set the id to the first one in the list but keep the old one
      // so we can spot the change and enable the save button
      if (!profilesIdList.contains(profileId)) {
        profileId = profilesIdList[0];
      }

      this.setProperties({
        profileId: profileId,
        oldProfileId: oldProfileId
      });
    },

    resetDynamicProperties: function(model) {
      var properties = { profileId: null };

      this.setDynamicProperties(properties);
      this.loadProfiles(model);
    },

    getFormattedPropertyList: function() {
      return this.getProperties('profileId'.w());
    },

    // make sure to update the osPlatformEnum for the selected profile in case the first profile
    // is auto selected for the user
    sendActionRequest: function() {
      this.profileOptionsChanged();
      
      this._super();
    }
  });
});
