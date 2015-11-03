define([
  'ember',

  './action_item_base_controller'
], function (
  Em,

  ActionItemBaseController
) {

  // Action Properties Update Device Info Controller
  // ==================================
  //
  return ActionItemBaseController.extend({

    isAllSupported: true,

    helpId: 1066,

    // For actions that do not have any dynamic properties, we will not depend the observer on isInitializationDone flag
    dynamicPropertiesChanged: function() {
      this.setProperties({
        isActionBtnDisabled: this.getBasicIsEmpty() || !this.getBasicIsDirty(),
        isSaveAndAssignActionBtnDisabled: this.getBasicIsEmpty()
      });
    }.observes('name',
      'description',
      'isNameDuplicate',
      'iosChecked',
      'androidChecked',
      'windowsChecked')
  });
});
