define([
  'ember',
  'desktop',
  'formatter',
  'help',
  'am-data',

  './content_item_base_controller',
  './content_policy_assignment_base',
  './edit_wizard',

  '../views/content_policy_assignments_view',
  '../views/edit_save_view'
], function (
  Em,
  Desktop,
  Formatter,
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

    tHeadingPolicy: 'amMobilePolicies.modals.addContent.headingPolicy'.tr('selectedContextName'),
    tHeadingPolicies: 'amMobilePolicies.modals.addContent.headingPolicies'.tr(),
    headingIconClass: 'icon-plus',

    selectedContextLabelAddContents: 'amMobilePolicies.modals.addContent.policyName'.tr(),
    selectedContextLabelAddContent: 'amMobilePolicies.modals.addContent.contentName'.tr(),

    // Views
    // -----
    //
    // One view per wizard 'step'.

    editView: AmContentPolicyAssignmentsView,
    editSaveView: AmEditSaveView,

    isSelectingPolicy: false,
    isAddingMode: true,

    selectedContextName: null,
    selectedContextLabel: null,

    onShowModal: function() {
      var model = this.get('model'),
        allowSaveOnNoSelection = this.get('allowSaveOnNoSelection'),
        isActionBtnDisabled = false;

      this._super();

      this.setProperties({
        isSelectingPolicy: model.isSelectingPolicy,

        contentIds: model.contentIds,
        policyIds: model.policyIds,

        selectedContextName: model.selectedContextName
      });

      this.loadPolicyAssignmentsToEdit(model.assignedContents, model.assignedPolicies);

      if(!this.get('allowSaveOnNoSelection')) {
        isActionBtnDisabled = true;
      }

      this.setProperties({
        isActionBtnDisabled: isActionBtnDisabled,
        isDoneDisabled: true,
        errorMessage: false,

        originalAssignmentType: this.get('selectedAssignmentType.value'),
        originalShowAvailabilityTime: this.get('showAvailabilityTime'),

        paused: false
      });

      this.get('wizard').transitionTo('edit');
    },

    loadPolicyAssignmentsToEdit: function (assignedContents, assignedPolicies) {
      // We're adding new content-policy assignments
      if (this.get('isSelectingPolicy')) {

        var assignedPoliciesContent = assignedPolicies.get('content');
        var assignedPoliciesId = assignedPoliciesContent.map(function(data) {
          return data.get('content.data.id');
        });

        // We're assigning a particular content item to additional policies
        this.setProperties({
          heading: this.get('tHeadingPolicies'),
          urlForHelp: Help.uri(1037),
          selectedContextLabel: this.get('selectedContextLabelAddContent'),

          selectionController: this.PolicySelectionController.create({ parentController: this }),
          'selectionController.excludedIds': assignedPoliciesId
        });
      } else {
        var assignedContentsContent = assignedContents.get('content');
        var assignedContentsId = assignedContentsContent.map(function(data) {
          return data.get('content.data.id');
        });

        // We're assigning additional content to a particular policy
        this.setProperties({
          heading: this.get('tHeadingPolicy'),
          urlForHelp: Help.uri(1031),
          selectedContextLabel: this.get('selectedContextLabelAddContents'),

          selectionController: this.ContentSelectionController.create({ parentController: this }),
          'selectionController.excludedIds': assignedContentsId
        });
      }
    },

    // Construct and submit updated policy assignments using /api/policy_content POST request.
    // This request has the form: {"contentIds":[id1, id2, ...], "policyAssignments":[policy_1, ... policyN]},
    // where policy_N is as per the policy_N portion of the /api/content/batch request.
    submitUpdates: function (okHandler, errHandler) {

      this.setProperties({
        submitInProgress: true,
        errorMessage: false
      });

      var ActionData = AmData.get('actions.AmMobilePolicyToContentMapActionData'), CreateAction = AmData.get('actions.AmMobilePolicyToContentMapCreateAction');

      var getAddAction = function() {
        return CreateAction.create({
          contentIds: contentIds,
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
      };

      var self = this;

      var assignedIds = this.get('selectionController.selections');
      var policyIds = this.get('policyIds') ? this.get('policyIds') : assignedIds;
      var contentIds = this.get('contentIds') ? this.get('contentIds') : assignedIds;

      var assignmentType = self.get('selectedAssignmentType.value');
      var availability = this.buildAvailabilityActionData();

      // then if there is any selected assignment, call the add action.
     if (!Em.isEmpty(policyIds)) {

        // If there is no assignment to remove but some to be added, just call the add action.
        var addAction = getAddAction();
        addAction.invoke();

      } else {
        // If user is not adding any assignment, just set the proper messages and enable the Done button.
        self.setProperties({
          submitStatusMsg: self.get('tSuccessMessage'),
          submitInProgress: false,
          isDoneDisabled: false
        });
      }

      return true;
    }
  });
});
