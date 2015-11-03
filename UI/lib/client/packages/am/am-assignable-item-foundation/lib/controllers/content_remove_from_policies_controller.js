define([
  'ember',
  'desktop',
  'am-desktop',

  'am-data'
], function(
  Em,
  Desktop,
  AmDesktop,

  AmData
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    tHeadingPolicy: 'amMobilePolicies.modals.removeContent.headingPolicy'.tr(),
    tHeadingPolicies: 'amMobilePolicies.modals.removeContent.headingPolicies'.tr(),
    tDescriptionFile: 'amMobilePolicies.modals.removeContent.descriptionSelectedFile'.tr(),
    tDescriptionFiles: 'amMobilePolicies.modals.removeContent.descriptionSelectedFiles'.tr(),
    tDescriptionPolicy: 'amMobilePolicies.modals.removeContent.descriptionPolicy'.tr(),
    tDescriptionPolicies: 'amMobilePolicies.modals.removeContent.descriptionPolicies'.tr(),

    actionDescription: 'amMobilePolicies.modals.removeContent.description'.tr(),

    headingIconClass: 'icon-square-attention1',

    actionButtonLabel: 'amMobilePolicies.modals.removeContent.buttons.removeContent'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    contentIds: null,
    policyId: null,
    policyAssignments: null,

    initProperties: function()  {
      var model = this.get('model');
      var contentIds = model.contentIds;
      var policyId = model.policyId;
      var policyAssignments = model.policyAssignments;
      var heading, actionDescription;

      if (policyId === null) {
        heading = policyAssignments.length > 1 ?
          this.get('tHeadingPolicies') :
          this.get('tHeadingPolicy') + ' ' + policyAssignments[0].get('name');

        actionDescription = policyAssignments.length > 1 ?
          this.get('tDescriptionPolicies'):
          this.get('tDescriptionPolicy');
      }
      else {
        heading = this.get('tHeadingPolicy');
        actionDescription = contentIds.length > 1 ?
          this.get('tDescriptionFiles') :
          this.get('tDescriptionFile');
      }

      this.setProperties({
        heading: heading,
        actionDescription: actionDescription,

        contentIds: contentIds,
        policyId: policyId,
        policyAssignments: policyAssignments
      });
    },

    buildAction: function() {
      var contentIds = this.get('contentIds');
      var policyId = this.get('policyId');
      var policyAssignments = this.get('policyAssignments');
      
      return AmData.get('actions.AmMobilePolicyToContentMapDeleteAction').create({
        contentIds: contentIds,
        mobilePolicyIds: !Em.isNone(policyId) ? Em.A([policyId]) : policyAssignments.mapBy('id')
      });
    }
  });
});
