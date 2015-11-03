define([
  'ember',
  'desktop',
  'am-desktop',
  'am-data',
  'query',

  './action_policy_assignment_base_controller'
], function(
  Em,
  Desktop,
  AmDesktop,
  AmData,
  Query,

  ActionPolicyAssignmentBaseController
) {
  'use strict';

  var SelectionController = AmDesktop.AmListController.extend(Desktop.ChildController, Desktop.TransientController, {
    selectionEnabled: true,

    // @Override
    getFilteredData: function(data) {
      var excludedIds = this.get('excludedIds');
      if (Em.isEmpty(excludedIds)) { return data; }

      return this.excludeIds(data, excludedIds);
    }
  });

  var PolicySelectionController = SelectionController.extend({
    dataStore: function() {
      return AmData.get('stores.mobilePolicyStore');
    }.property(),

    visibleColumnNames: 'name isSmartPolicy'.w(),

    // @Override
    getFilteredData: function(data) {
      var filteredData = data.filter(function (data) {
        return 255 !== data.get('data.filterType') && data.get('data.isSmartPolicy') === 1;
      });

      return this._super(filteredData);
    }
  });

  var ActionSelectionController = SelectionController.extend({
    dataStore: function() {
      return AmData.get('stores.actionsStore');
    }.property(),

    visibleColumnNames: 'name type'.w()
  });


  // Add Action/Actions to Policies Controller
  // ==================================
  //
  // This controller controls the selection of, either:
  // 1- One or more policies and add the existing action to it
  // 2- One or more actions and add them to the existing policy

  return ActionPolicyAssignmentBaseController.extend({

    tHeadingAddAction: 'amAssignableItem.modals.action.policyAssignment.addActionToPolicies.heading'.tr(),
    tHeadingAddActions: 'amAssignableItem.modals.action.policyAssignment.addActionsToPolicy.heading'.tr('selectedContextName'),

    tHeading: function() {
      return this.get('isSelectingPolicy') ? this.get('tHeadingAddAction') : this.get('tHeadingAddActions');
    }.property('isSelectingPolicy', 'selectedContextName'),

    headingIconClass: 'icon-plus',

    selectedContextLabelAddAction: 'amAssignableItem.modals.actionProperties.actionName'.tr(),
    selectedContextLabelAddActions: 'amAssignableItem.modals.action.policyAssignment.policyName'.tr(),

    // This controller needs to know if user is:
    // 1- Adding actions to policies (selecting actions)
    // 2- Adding action to policies (selecting policies)
    isSelectingPolicy: true,
    isAddAssignmentsMode: true,

    onShowModal: function(model) {
      this.resetSettings();
      var name, id, selectedContextLabel;
      var isSelectingPolicy = model.isSelectingPolicy;
      var selectionController;

      if (isSelectingPolicy) {
        name = model.actionName;
        id = model.actionId;
        selectedContextLabel = this.get('selectedContextLabelAddAction');

        selectionController = PolicySelectionController.create({ parentController: this });
        var assignedPoliciesId = model.assignedPolicies.map(function(data) {
          return data.get('data.id');
        });

        selectionController.set('excludedIds', assignedPoliciesId);

      } else {
        name = model.policyName;
        id = model.policyId;
        selectedContextLabel = this.get('selectedContextLabelAddActions');

        var assignedActionsId = model.assignedActions.map(function(data) {
          return data.get('data.id');
        });

        selectionController = ActionSelectionController.create({ parentController: this });
        selectionController.set('excludedIds', assignedActionsId);
      }

      this.setProperties({
        isSelectingPolicy: isSelectingPolicy,
        selectedContextId: id,
        selectedContextName: name,
        selectedContextLabel: selectedContextLabel,

        selectionController: selectionController
      });

      this._super(model);
    },

    // @override
    buildAction: function() {
      var isSelectingPolicy = this.get('isSelectingPolicy');

      var contextId = Em.A([this.get('selectedContextId')]);
      var assignedIds = this.get('selectionController.selections');

      return this._super({
        actionIds: isSelectingPolicy ? contextId : assignedIds,
        policyIds: isSelectingPolicy ? assignedIds : contextId
      });
    }
  });
});
