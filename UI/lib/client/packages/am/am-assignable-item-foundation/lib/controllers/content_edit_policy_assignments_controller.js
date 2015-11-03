define([
  'ember',
  'help',
  'am-data',

  './content_item_base_controller',
  './content_policy_assignment_base',
  './edit_wizard',

  '../views/content_policy_assignments_view',
  '../views/edit_save_view'
], function (
  Em,
  Help,
  AmData,

  ContentItemBaseController,
  ContentPolicyAssignmentBase,
  EditWizardMixin,

  AmContentPolicyAssignmentsView,
  AmEditSaveView
  ) {

  // Content Item Edit Policy Controller
  // ==================================
  //
  // This controller contains the wizard of editing policies.
  return ContentItemBaseController.extend(ContentPolicyAssignmentBase, EditWizardMixin, {

    tHeadingEditPolicy: 'amMobilePolicies.modals.editContent.headingEditPolicy'.tr('policyName'),
    headingIconClass: 'icon-edit-icon',

    heading: function() {
      return this.get('tHeadingEditPolicy');
    }.property('policyName'),

    urlForHelp: Help.uri(1043),

    // Views
    // -----
    //
    // One view per wizard 'step'.

    editView: AmContentPolicyAssignmentsView,
    editSaveView: AmEditSaveView,

    contentName: null,
    policyName: null,

    onShowModal: function() {
      var model = this.get('model');
      var assignedContent = model.assignedContent;

      this._super();

      this.setProperties({
        urlForHelp: Help.uri(1043),

        policyName: model.policyName,
        contentIds: model.contentIds,
        contentName: model.contentName,
        policyIds: model.policyIds
      });

      this.populateExistingSettings(assignedContent);

      this.setProperties({
        isActionBtnDisabled: true,
        originalAssignmentType: this.get('selectedAssignmentType.value'),
        originalShowAvailabilityTime: this.get('showAvailabilityTime'),

        paused: false
      });

      this.get('wizard').transitionTo('edit');
    },

    // Construct and submit updated policy assignments using /api/policy_content POST request.
    // This request has the form: {"contentIds":[id1, id2, ...], "policyAssignments":[policy_1, ... policyN]},
    // where policy_N is as per the policy_N portion of the /api/content/batch request.
    submitUpdates: function (okHandler, errHandler) {
      var self = this;
      this.set('submitInProgress', true);

      var ActionData = AmData.get('actions.AmMobilePolicyToContentMapActionData');
      var CreateAction = AmData.get('actions.AmMobilePolicyToContentMapCreateAction');

      var policyIds = this.get('policyIds');
      var contentIds = this.get('contentIds');

      var assignmentType = self.get('selectedAssignmentType.value');
      var availability = this.buildAvailabilityActionData();

      var action = CreateAction.create({
        contentIds: [contentIds],
        policyAssignments: policyIds.map(function (id) {
          return ActionData.PolicyAssignment.create({
            policyId: id,
            assignmentType: assignmentType,
            availability: availability
          });
        }),

        onSuccess: function (data) {
          self.set('submitInProgress', false);
          okHandler(self, data);
        },

        onError: function (ajaxError) {
          self.set('submitInProgress', false);
          errHandler(self, ajaxError);
        }
      });

      action.invoke();

      return true;
    }
  });
});
