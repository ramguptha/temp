define([
  'ember',
  'help',
  'desktop',
  'am-desktop',
  'am-data'
], function (
    Em,
    Help,
    Desktop,
    AmDesktop,
    AMData
    ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    tPolicyHeading: 'amMobilePolicies.modals.deletePolicy.policyHeading'.tr(),
    tPoliciesHeading: 'amMobilePolicies.modals.deletePolicy.policiesHeading'.tr(),
    tPolicyDescription: 'amMobilePolicies.modals.deletePolicy.policyDescription'.tr(),
    tPoliciesDescription: 'amMobilePolicies.modals.deletePolicy.policiesDescription'.tr(),
    tPolicyButton: 'amMobilePolicies.modals.deletePolicy.buttons.deletePolicy'.tr(),
    tPoliciesButton: 'amMobilePolicies.modals.deletePolicy.buttons.deletePolicies'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),

    headingIconClass: 'icon-square-attention1',
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationViewDisabled: Em.View.extend({
      layout: Desktop.ModalActionLayoutTemplate
    }),

    confirmationView: Desktop.get('ModalActionConfirmView'),

    policies: null,
    urlForHelp: null,

    initProperties: function () {
      this.setProperties({
        urlForHelp: Help.uri(1021),
        policies: this.get('model'),
        numberOfItems: this.get('model').length
      });
    },

    heading: function() {
      return this.get('numberOfItems') > 1 ? this.get('tPoliciesHeading') : this.get('tPolicyHeading');
    }.property('numberOfItems'),

    actionButtonLabel: function() {
      return this.get('numberOfItems') > 1 ? this.get('tPoliciesButton') : this.get('tPolicyButton');
    }.property('numberOfItems'),

    actionDescription: function() {
      return this.get('numberOfItems') > 1 ? this.get('tPoliciesDescription') : this.get('tPolicyDescription');
    }.property('numberOfItems'),

    buildAction: function () {
      this.set('urlForHelp', null);
      return AMData.get('actions.AmMobilePolicyDeletePolicyAction').create({
        policyIds: this.get('policies')
      });
    }
  });
});
