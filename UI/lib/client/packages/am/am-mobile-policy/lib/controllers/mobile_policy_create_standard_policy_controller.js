define([
  'ember',
  'help',
  '../namespace',
  'guid',

  'desktop',
  'am-desktop',
  'am-data',

  '../views/mobile_policy_create_standard_policy_view'
], function (
  Em,
  Help,
  AmMobileDevice,
  Guid,

  Desktop,
  AmDesktop,
  AmData,

  SetPolicyNameView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    tPlaceholder: 'amMobilePolicies.modals.createNewPolicy.placeholder'.tr(),
    tNotUniqueNameMessage: 'amMobilePolicies.shared.notUniqueNameMessage'.tr(),

    heading: 'amMobilePolicies.modals.createNewPolicy.heading'.tr(),
    headingIconClass: 'icon-square-attention1',

    actionButtonLabel: 'amMobilePolicies.modals.createNewPolicy.buttons.createPolicy'.tr(),

    inProgressMsg: 'amMobilePolicies.modals.createNewPolicy.inProgressMessage'.tr(),
    errorMsg: 'amMobilePolicies.modals.createNewPolicy.errorMessage'.tr(),
    successMsg: 'amMobilePolicies.modals.createNewPolicy.successMessage'.tr(),
    errorMessage: null,

    confirmationView: SetPolicyNameView,

    isActionBtnDisabled: true,
    policyNameChangedTimeout: null,

    urlForHelp: null,

    initProperties: function () {
      this.setProperties({
        policyName: '',
        urlForHelp: Help.uri(1024)
      });
    },

    placeholder: function () {
      return this.get('tPlaceholder').toString();
    }.property(),

    onPolicyNameChanged: function (router, event) {
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
      if (!Em.isNone(self.get('policyName')) && !Em.isEmpty(self.get('policyName').trim())) {
        var mobilePolicyStore = AmData.get('stores.mobilePolicyStore');

        mobilePolicyStore.acquire(Guid.generate(),
          { store: mobilePolicyStore, searchAttr: 'name', searchFilter: policyName.trim() },
          function (datasource) {
            for (var i = 0; i < datasource.get('length'); i++) {
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
      }
    },

    buildAction: function () {
      this.set('urlForHelp', null);
      return AmData.get('actions.AmMobilePolicySetPolicyNameAction').create({
        name: this.get('policyName')
      });
    }
  });
});
