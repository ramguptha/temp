define([
  'ember',
  'help',
  'jquery',
  '../namespace',

  'desktop',
  'am-desktop',
  'am-data',

  '../views/mobile_policy_create_standard_policy_view'
], function (
    Em,
    Help,
    $,
    AmMobileDevice,

    Desktop,
    AmDesktop,
    AMData,

    SetPolicyNameView
    ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    tNotUniqueNameMessage: 'amMobilePolicies.shared.notUniqueNameMessage'.tr(),

    heading: 'amMobilePolicies.modals.editPolicy.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    confirmPrompt: 'amMobilePolicies.modals.editPolicy.description'.tr(),

    actionWarning: '',
    actionButtonLabel: 'amMobilePolicies.modals.editPolicy.buttons.createPolicy'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),
    errorMessage: '',

    confirmationView: SetPolicyNameView,

    policyName: null,
    policyId: null,
    seed: null,

    isActionBtnDisabled: true,
    skipUniqueNameCheck: true,
    policyNameChangedTimeout: null,

    urlForHelp: null,

    initProperties: function () {
      var policy = this.get('model');

      this.setProperties({
        urlForHelp: Help.uri(1022),
        skipUniqueNameCheck: true,
        isActionBtnDisabled: true,
        errorMessage: null,
        policyId: policy.get('id'),
        seed: policy.get('seed'),
        policyName: policy.get('name')
      });

      this.onPolicyNameChanged();
    },

    onPolicyNameChanged: function () {
      if (Em.isNone(this.get('policyName')) || Em.isEmpty(this.get('policyName').trim())) {
        this.set('isActionBtnDisabled', true);
        this.set('errorMessage', '');
      } else {
        // Let's not spam the server with requests.
        // This should send a request not more often than once every second.
        if (!Em.isNone(this.get('policyNameChangedTimeout'))) {
          Em.run.cancel(this.get('policyNameChangedTimeout'));
          this.set('policyNameChangedTimeout', null);
        }

        this.set('policyNameChangedTimeout', Em.run.later(this, this.checkNameUniqueness, this.get('policyName'), this, 100));
      }
    }.observes('policyName'),

    checkNameUniqueness: function (policyName, self) {
      if (!self.get('skipUniqueNameCheck') && !Em.isNone(self.get('policyName')) && !Em.isEmpty(self.get('policyName').trim())) {
        var mobilePolicyStore = AMData.get('stores.mobilePolicyStore');

        mobilePolicyStore.acquire(null,
            { store: mobilePolicyStore, searchAttr: 'name', searchFilter: policyName.trim() },
            function (datasource) {
              for (var i = 0; i < datasource.get('length') ; i++) {
                if (datasource.objectAt(i).get('data.name').trim().toUpperCase() === policyName.trim().toUpperCase()) {
                  self.set('isActionBtnDisabled', true);
                  self.set('errorMessage', self.get('tNotUniqueNameMessage'));

                  return;
                }
              }
            }
        );

        self.set('isActionBtnDisabled', false);
        self.set('errorMessage', '');
      } else {
        self.set('skipUniqueNameCheck', false);
      }
    },

    buildAction: function () {
      this.set('urlForHelp', null);
      return AMData.get('actions.AmMobilePolicyRenamePolicyAction').create({
        policyId: this.get('policyId'),
        newname: this.get('policyName'),
        seed: this.get('seed'),
        endPoint: 'policies/standard/' + this.get('policyId')
      });
    }
  });
});
