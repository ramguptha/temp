define([
  'ember',
  'desktop',
  'am-desktop',
  'help',
  'formatter',
  'am-data',
  'guid',
  'query',

  'packages/am/am-assignable-item-foundation',
  './action_properties_mixin'
], function (
  Em,
  Desktop,
  AmDesktop,
  Help,
  Formatter,
  AmData,
  Guid,
  Query,

  AmAssignableItemFoundation,
  ActionsPropertiesMixin
) {

  // Action Item Base Controller
  // ==================================
  //
  // This controller contains the common properties/Views/Controllers between
  // all the controllers controlling various work flows on the Actions.
  // These actions include Add Action / Edit Action / Duplicate Action

  return AmDesktop.ModalActionController.extend(Desktop.TransientController, ActionsPropertiesMixin, {

    // New Action Messages
    tNewAction: 'amAssignableItem.assignableActionsPage.body.actionsMenu.options.newAction'.tr(),
    newHeadingIconClass: 'icon-plus',

    tNewInProgressMessage: 'amAssignableItem.modals.action.create.inProgressMessage'.tr(),
    tNewSuccessMessage: 'amAssignableItem.modals.action.create.successMessage'.tr(),
    tNewErrorMessage: 'amAssignableItem.modals.action.create.errorMessage'.tr(),

    // Edit Action Messages
    tEditAction: 'amAssignableItem.assignableActionsPage.body.actionsMenu.options.editAction'.tr(),
    editHeadingIconClass: 'icon-edit-icon',

    tEditInProgressMessage: 'shared.modals.inProgressMessage'.tr(),
    tEditSuccessMessage: 'shared.modals.successMessage'.tr(),
    tEditErrorMessage: 'shared.modals.errorMessage'.tr(),

    // Duplicate Action Messages
    tDuplicateAction: 'amAssignableItem.assignableActionsPage.body.actionsMenu.options.duplicateAction'.tr(),
    duplicateHeadingIconClass: 'icon-duplicate color-info-icon icon-font-size-l',

    tCopy: 'amDesktop.shared.copy'.tr(),

    // Common final messages
    tInProgressMessage: null,
    tSuccessMessage: null,
    tErrorMessage: null,

    submitStatusMsg: null,
    errorMessage: null,
    errorDetails: null,

    urlForHelp: null,

    isEditMode: false,
    isDuplicateMode: false,

    // By default we are creating a new action
    isDoneDisabled: true,
    submitInProgress: false,

    // The Save button depends on the validation of the properties and any change of them
    isActionBtnDisabled: true,

    // The Save and Assign button depends on only the validation of properties.
    isSaveAndAssignActionBtnDisabled: true,

    // Flag to set if the user wants to add action to policies as the second step
    isAddingActionToPolicies: false,

    // Flag to disable firing the observers on basic properties until they are all set
    isInitializationDone: null,

    id: null,
    heading: null,
    lock: Guid.generate(),

    name: null,
    oldName: null,
    isNameDuplicate: false,

    description: null,
    oldDescription: null,

    typeEnum: null,
    osPlatformEnum: 0,

    actions: {
      cancel: function() {
        this.send('closeModal');
      },

      done: function() {
        this.send('closeModal');
      },

      save: function() {
        this.get('wizard').send('next');
      },

      gotoPreviousStep: function() {
        this.get('wizard').send('prev');
      },

      saveAndAndAssignToPolicies: function() {
        this.set('isAddingActionToPolicies', true);
        this.get('wizard').send('next');
      }
    },
    // Views
    // -----
    //
    // One view per wizard 'step'.

    //propertiesView: 'am-action-send-message',
    propertiesView: 'am-action-base-modal',
    propertiesSaveView: AmAssignableItemFoundation.AmEditSaveView,

    // Wizard State
    // ------------
    //
    // This state manager drives the wizard.

    wizard: function() {
      return Em.StateManager.create({
        initialState: 'setProperties',
        controller: this,

        states: {
          // Edit/Set properties of the action
          setProperties: Em.State.create({
            next: function(manager) {
              manager.transitionTo('status');
            }
          }),

          'status': Em.State.create({
            enter: function() {
              var controller = this.get('parentState.controller');
              controller.set('urlForHelp', null);

              controller.sendActionRequest();
            },

            prev: function(manager) {
              manager.get('controller').set('errorMessage', null);
              manager.transitionTo('setProperties');
            }
          })
        }
      });
    }.property(),

    showingProperties: Em.computed.equal('wizard.currentState.name', 'setProperties'),
    showingStatus: Em.computed.equal('wizard.currentState.name', 'status'),

    // End of the Wizard setup
    // -----------------------

    nameChanged: function() {
      var name = this.get('name');

      if (typeof name !== 'undefined' && name !== null) {
        name = name.toString().replace(/^\s+/, '');
        this.set('name', name);
      }

      Em.run.next(this, function() {
        this.checkForDuplicateNames(name);
      });

    }.observes('name'),

    checkForDuplicateNames: function(name) {
      this.set('isNameDuplicate', false);

      if (!name || Em.isEmpty(name)) { return; }

      // Issue query to check if content with that name already exists on the server
      var query = Query.Search.create({
        searchAttr: 'name',
        searchFilter: Formatter.trim(name).toLowerCase()
      });

      AmData.get('stores.actionsStore').acquire(this.get('lock'), query, function(datasource) {
        var self = this;
        var content = datasource.get('content');
        content.forEach(function(action) {
          // Do not move toLowerCase out from loop - stop working
          // trim so that we also consider it a duplicate name if it only differs by leading or trailing spaces
          var actionName = Formatter.trim(self.get('name')).toLowerCase();
          var name = action.get('data.name');
          var id = action.get('data.id').toString();

          if (id !== self.get('id') && actionName === name.toLowerCase()) {
            self.set('isNameDuplicate', true);
          }
        });

      }, null, this, false);
    },

    onShowModal: function(context) {
      // Reset the name first thing in order to enable the observer for duplicate name work properly
      this.set('name', null);

      var isEditMode = context.isEditMode,
        isDuplicateMode = context.isDuplicateMode;

      // Initialize static properties base on the type of functionality: Edit, Duplicate or New
      // Edit and Duplicate will have existing name and possible description
      var model = context.model || context;
      var data = model.data;
      var name =  model.name ? model.name.toString() : '';
      var description =  data && data.description ? data.description.toString() : '';

      // Titles/messages specific to each type of action
      var heading = this.get('tNewAction'),
        iconClass = this.get('newHeadingIconClass'),
        tInProgressMessage = this.get('tNewInProgressMessage'),
        tSuccessMessage = this.get('tNewSuccessMessage'),
        tErrorMessage = this.get('tNewErrorMessage');

      if (isEditMode === true && !isDuplicateMode) {
        heading = this.get('tEditAction');
        iconClass = this.get('editHeadingIconClass');
        tInProgressMessage = this.get('tEditInProgressMessage');
        tSuccessMessage = this.get('tEditSuccessMessage');
        tErrorMessage = this.get('tEditErrorMessage');

      } else if (isDuplicateMode === true) {
        heading = this.get('tDuplicateAction');
        iconClass = this.get('duplicateHeadingIconClass');
      }

      this.setProperties({
        isEditMode: isEditMode,
        isDuplicateMode: isDuplicateMode,

        // This flag needs to be set to false here (Before any initialization).
        // Do not move it
        isInitializationDone: false,

        isActionBtnDisabled: !isDuplicateMode,
        isSaveAndAssignActionBtnDisabled: !isEditMode,

        heading: heading + ': ' + model.label,
        headingIconClass: iconClass,

        tInProgressMessage: tInProgressMessage,
        tSuccessMessage: tSuccessMessage,
        tErrorMessage: tErrorMessage,

        urlForHelp: Help.uri(this.get('helpId')),

        id: isDuplicateMode || Em.isNone(model.actionId) ?  null : model.actionId.toString(),

        name: isDuplicateMode ? name + ' ' + this.get('tCopy') : name,
        oldName: name,

        description: description,
        oldDescription: description,

        typeEnum: model.typeEnum,
        osPlatformEnum: model.osPlatformEnum,

        isAddingActionToPolicies: false
      });

      this.initialize(model);

      // This flag needs to be set to true here (after basic initialization).
      // Do not move it
      this.set('isInitializationDone', true);

      this.get('wizard').transitionTo('setProperties');
    },

    // Duplicate and Create will not pass any id to prepare data for post
    buildAction: function(id, seed) {
      this.setProperties({
        submitInProgress: true,
        submitStatusMsg: this.get('tInProgressMessage')
      });

      var self = this;
      var hasDynamicProperties = this.get('hasDynamicProperties');
      var typeEnum = self.get('typeEnum');
      var isEditMode = this.get('isEditMode') && !this.get('isDuplicateMode');

      return AmData.get('actions.AmActionAddUpdateAction').create({
        id: isEditMode ? self.get('id') : null,
        seed: isEditMode ? self.get('seed') : null,

        name: self.get('name').trim(),
        actionDescription: self.get('description'),
        typeEnum: typeEnum,
        osPlatformEnum: typeEnum === 5 ? self.get('osPlatformEnum') : self.getPlatformEnum(),
        propertyList: hasDynamicProperties ? self.getFormattedPropertyList() : {}
      });
    },

    mobileDeviceItemController: Em.inject.controller('AmAssignableActionsItem'),
    
    onSuccessCallback: function() {
      var isDoneDisabled = false, controller = this.get('mobileDeviceItemController');

      // If user wants to proceed with assigning policies call the corresponding action.
      if (this.get('isAddingActionToPolicies')) {
        isDoneDisabled = true;
        this.sendAddActionToPolicies();
      }

      this.setProperties({
        submitInProgress: false,
        submitStatusMsg: this.get('tSuccessMessage'),
        errorMessage: null,
        isDoneDisabled: isDoneDisabled
      });

      controller.forceUpdate(this.get('id'));
    },

    onErrorCallback: function() {
      this.setProperties({
        submitInProgress: false,
        submitStatusMsg: null,
        errorMessage: this.get('tErrorMessage')
      });
    },

    sendAddActionToPolicies: function() {
      this.send('closeModal');

      var model = {
        actionId: this.get('id') || null,
        actionName: this.get('name')
      };

      // There are two actions with this name in the router
      // One of them needs the model to be passed another one does not.
      // It depends from which context we are reaching here.
      this.send('gotoAddActionToPolicies', model);
    }
  });
});