define([
  'ember',
  'help',
  'desktop',
  'am-desktop',
  'formatter',

  'am-data',
  'packages/am/am-assignable-item-foundation',

  '../views/mobile_policy_edit_policy_assignment_config_profile_view'
], function (Em,
             Help,
             Desktop,
             AmDesktop,
             Formatter,
             AmData,
             AmAssignableItemFoundation,
             MobilePolicyAssignmentConfigProfileView) {

  // Content Item Edit Config Profile Controller
  // ==================================
  //
  // This controller contains the wizard of editing policies.
  return AmDesktop.ModalActionController.extend(Desktop.TransientController, AmAssignableItemFoundation.ContentPolicyAssignmentBase, AmAssignableItemFoundation.AmEditWizard, {

    heading: 'amMobilePolicies.modals.editPolicyAssignmentProperties.heading'.tr(),
    headingIconClass: 'icon-edit-icon',

    actionButtonLabel: 'shared.buttons.save'.tr(),

    tForbidden: 'amMobilePolicies.shared.assignmentRuleOptions.forbidden'.tr(),

    urlForHelp: Help.uri(1030),

    // Views
    // One view per wizard 'step'.
    editView: MobilePolicyAssignmentConfigProfileView,
    editSaveView: AmAssignableItemFoundation.AmEditSaveView,

    submitInProgress: false,

    paused: true,

    defaultAssignmentTypes: function () {
      return Em.A([
        Em.Object.create({name: this.get('tAutoInstall'), type: 1}),
        Em.Object.create({name: this.get('tOnDemand'), type: 2}),
        Em.Object.create({name: this.get('tAutoInstallAutoRemove'), type: 3}),
        Em.Object.create({name: this.get('tOnDemandAutoRemove'), type: 4}),
        Em.Object.create({name: this.get('tForbidden'), type: 0})
      ]);
    }.property(),

    onShowModal: function () {
      var model = this.get('model');

      this._super();

      var policyId = model.policyId;
      var configProfiles = model.configProfiles;
      var firstConfigProfile = configProfiles[0].get('content.data');
      var availabilitySelector = firstConfigProfile.get('availabilitySelectorNumeric');

      this.setProperties({
        policyId: policyId,
        configProfiles: configProfiles,
        modalActionWindowClass: this.get('modalActionWindowClass') + ' summary-list',
        assignmentTypes: this.get('defaultAssignmentTypes'),

        showAvailabilityTime: availabilitySelector !== 0,
        availabilitySelector: availabilitySelector,
        assignedTime: {
          startTime: firstConfigProfile.get('profileStartTime'),
          endTime: firstConfigProfile.get('profileEndTime')
        },
        'selectedAssignmentType.value': firstConfigProfile.get('assignmentRuleNumeric'),

        submitInProgress: false
      });

      this.setProperties({
        isActionBtnDisabled: true,
        availabilityTimeChanged: false,
        originalAssignmentType: firstConfigProfile.get('assignmentRuleNumeric'),
        originalShowAvailabilityTime: this.get('showAvailabilityTime'),

        paused: false
      });

      this.get('wizard').transitionTo('edit');
    },

    buildAction: function () {
      var time = this.get('showAvailabilityTime') ? this.get('formattedTime') : null;

      var policyAssignments = [{
        policyId: Number(this.get('policyId')),
        assignmentType: Number(this.get('selectedAssignmentType.value')),
        availabilitySelector: Number(this.get('availabilitySelector')),
        startTime: time ? time.startTime : null,
        endTime: time ? time.endTime : null
      }];

      return AmData.get('actions.AmMobilePolicyAddConfigProfileAction').create({
        configurationProfileIds: this.get('configProfiles').mapBy('id').map(Number),
        policyAssignments: policyAssignments
      });
    },

    submitUpdates: function (okHandler, errHandler) {
      var self = this;
      this.set('submitInProgress', true);

      var action = this.buildAction().reopen({
        onSuccess: function (data) {
          self.set('submitInProgress', false);
          okHandler(self, data);
        },

        onError: function (ajaxError) {
          self.set('submitInProgress', false);
          errHandler(self, ajaxError);
        }
      });

      this.set('action', action);

      action.invoke();
    }
  });
});
