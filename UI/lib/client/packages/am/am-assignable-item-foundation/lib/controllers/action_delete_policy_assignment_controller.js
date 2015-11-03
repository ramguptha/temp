define([
  'ember',
  'help',
  'desktop',
  'am-desktop',

  'am-data'
], function(
  Em,
  Help,
  Desktop,
  AmDesktop,

  AmData
) {
  'use strict';

  // Action Delete Policy Assignment Controller
  // ==================================
  //
  // This controller based on the context of the action, either:
  // 1- Removes selected Policy/Policies from the context, Action
  // 2- Removes selected Action/Actions from the context, Policy

  return AmDesktop.ModalActionController.extend({
    tHeadingPolicy: 'amAssignableItem.modals.action.policyAssignment.remove.headingPolicy'.tr(),
    tHeadingPolicies: 'amAssignableItem.modals.action.policyAssignment.remove.headingPolicies'.tr(),
    tHeadingAction: 'amAssignableItem.modals.action.policyAssignment.remove.headingAction'.tr(),

    headingIconClass: 'icon-square-attention1',

    tDescriptionActions: 'amAssignableItem.modals.action.policyAssignment.remove.descriptionActions'.tr(),
    tDescriptionPolicies: 'amAssignableItem.modals.action.policyAssignment.remove.descriptionPolicies'.tr(),

    tButtonLabelActions: 'amAssignableItem.modals.action.policyAssignment.remove.buttonActions'.tr(),
    tButtonLabelAction: 'amAssignableItem.modals.action.policyAssignment.remove.buttonAction'.tr(),

    inProgressMsg: 'shared.modals.inProgressMessage'.tr(),
    successMsg: 'shared.modals.successMessage'.tr(),
    errorMsg: 'shared.modals.errorMessage'.tr(),

    confirmationView: Desktop.ModalActionConfirmView,

    // TODO needs to be filled with the proper id after doc team is done
    //urlForHelp: Help.uri(1056),

    // This flag is set based on the type of functionality in this modal to decide about the content to be posted.
    isRemovingPolicy: null,

    contextId: null,
    contextName: null,
    assignedIds: null,

    initProperties: function()  {
      var model = this.get('model');

      var isRemovingPolicy = model.isRemovingPolicy, contextId = model.contextId;
      var assignments = model.assignments, assignedIds = assignments.mapBy('id'),
        heading, actionDescription, actionButtonLabel;

      if (isRemovingPolicy) {
        heading = this.get('tHeadingPolicies');
        actionDescription = this.get('tDescriptionPolicies');
        actionButtonLabel = this.get('tButtonLabelAction');

        if (assignedIds.length === 1) {
          heading = this.get('tHeadingPolicy');
        }

      } else {
        heading = this.get('tHeadingAction');
        actionDescription = this.get('tDescriptionActions');
        actionButtonLabel = this.get('tButtonLabelActions');
      }

      this.setProperties({
        heading: heading,
        actionDescription: actionDescription,
        actionButtonLabel: actionButtonLabel,

        contextId: contextId,
        assignedIds: assignedIds,

        isRemovingPolicy: isRemovingPolicy
      });
    },

    // Set the content to delete based on the type of the functionality of this modal.
    buildAction: function() {
      var isRemovingPolicy = this.get('isRemovingPolicy');

      var contextIds = Em.A([this.get('contextId')]);
      var assignedIds = this.get('assignedIds');

      return AmData.get('actions.AmActionDeletePolicyAssignmentAction').create({
        content: {
          actionIds: isRemovingPolicy ? contextIds : assignedIds,
          policyIds: isRemovingPolicy ? assignedIds : contextIds
        }
      });
    }
  });
});
