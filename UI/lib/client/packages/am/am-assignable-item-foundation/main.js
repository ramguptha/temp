define([
  'ember',
  './lib/controllers/content_item_base_controller',
  './lib/controllers/content_policy_assignment_base',
  './lib/controllers/content_add_policy_assignments_controller',
  './lib/controllers/content_edit_policy_assignments_controller',
  './lib/controllers/content_remove_from_policies_controller',

  './lib/controllers/action_add_policy_assignment_controller',
  './lib/controllers/action_edit_policy_assignment_controller',
  './lib/controllers/action_delete_policy_assignment_controller',

  './lib/controllers/edit_wizard',

  './lib/views/edit_save_view',
  './lib/views/edit_flow_view',

  'text!./lib/templates/action_policy_assignment_work_flow.handlebars'
], function(
  Em,
  ContentItemBaseController,
  ContentPolicyAssignmentBase,
  ContentAddPolicyAssignmentsController,
  EditPolicyAssignmentsController,
  RemoveContentFromPoliciesController,

  AddActionPolicyAssignmentController,
  EditActionPolicyAssignmentController,
  DeleteActionPolicyAssignmentController,

  EditWizard,

  EditSaveView,
  EditFlowView,

  ActionPolicyAssignmentWorkFlow
) {
  'use strict';

  return {
    ContentItemBaseController: ContentItemBaseController,
    ContentPolicyAssignmentBase: ContentPolicyAssignmentBase,
    ContentAddPolicyAssignmentsController: ContentAddPolicyAssignmentsController,
    EditPolicyAssignmentsController: EditPolicyAssignmentsController,
    RemoveContentFromPoliciesController: RemoveContentFromPoliciesController,

    AddActionPolicyAssignmentController: AddActionPolicyAssignmentController,
    EditActionPolicyAssignmentController: EditActionPolicyAssignmentController,
    DeleteActionPolicyAssignmentController: DeleteActionPolicyAssignmentController,

    AmEditWizard: EditWizard,

    AmEditSaveView: EditSaveView,
    AmEditFlowView: EditFlowView,

    AmActionPolicyAssignmentFlowView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(ActionPolicyAssignmentWorkFlow)
    })
  };
});
