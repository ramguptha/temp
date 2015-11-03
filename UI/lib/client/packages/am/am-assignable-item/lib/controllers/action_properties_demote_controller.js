define([
  'ember',

  './action_item_base_controller'
], function (
  Em,

  ActionItemBaseController
) {

  // Action Properties Demote Un-managed Device Controller
  // ==================================
  //
  return ActionItemBaseController.extend({

    isIosAndroidSupported: true,

    helpId: 1072,

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
