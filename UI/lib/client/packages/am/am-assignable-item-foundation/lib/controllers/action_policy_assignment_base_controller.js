define([
  'ember',
  'desktop',
  'am-desktop',
  'am-data',
  'formatter',
  'help',
  'guid',

  '../views/action_policy_assignment_view',
  '../views/edit_save_view'
], function (
  Em,
  Desktop,
  AmDesktop,
  AmData,
  Formatter,
  Help,
  Guid,

  ActionPolicyAssignmentView,
  AmEditSaveView
) {

  // Action Policy Assignment Base Controller
  // ==================================
  //
  // This controller contains the base properties/methods of the
  // controllers that control the add/edit of content to policy or vice versa.

  return AmDesktop.ModalActionController.extend({

    tInProgressMessage: 'shared.modals.inProgressMessage'.tr(),
    tSuccessMessage: 'shared.modals.successMessage'.tr(),
    tErrorMessage: 'shared.modals.errorMessage'.tr(),

    tMinutes: 'desktop.datePickerComponent.minutes'.tr(),
    tHours: 'desktop.datePickerComponent.hours'.tr(),
    tDays: 'desktop.datePickerComponent.days'.tr(),

    // Properties for selecting policies/actions to add to actions/policies
    // =================
    //
    selectedContextId: null,
    selectedAContextName: null,
    selectedContextLabel: null,

    selectionController: null,
    SelectionView: AmDesktop.AmSelectionListView,

    urlForHelp: null,

    headingIconClass: null,
    heading: null,
    tHeading: null,

    // This controller needs to know if user is:
    // 1- Adding new assignments
    // 2- Editing the existing assignment
    isAddAssignmentsMode: false,

    isActionBtnDisabled: null,

    lock: Guid.generate(),

    // Properties/methods related to the interval settings
    // =================
    //
    DigitalFieldView: AmDesktop.DigitalFieldView.extend({
      tPlaceholder: null
    }),

    delayChecked: false,
    delayNotChecked: Em.computed.not('delayChecked'),
    initialDelay: 1,
    delaySelectorId: 'hours',

    repeatChecked: false,
    repeatNotChecked: Em.computed.not('repeatChecked'),
    repeatInterval: 1,
    repeatCount: 1,
    repeatSelectorId: 'hours',

    // Old Interval Settings
    oldDelayChecked: false,
    oldInitialDelay: 1,
    oldDelaySelectorId: 'hours',

    oldRepeatChecked: false,
    oldRepeatInterval: 1,
    oldRepeatCount: 1,
    oldRepeatSelectorId: 'hours',

    isInitialDelayNaN: false,
    isRepeatIntervalNaN: false,
    isRepeatCountNaN: false,

    intervalOptions: function() {
      return Em.A([
        Em.Object.create({ name: this.get('tMinutes'), value: 'minutes' }),
        Em.Object.create({ name: this.get('tHours'), value: 'hours' }),
        Em.Object.create({ name: this.get('tDays'), value: 'days' })
      ]);
    }.property(),

    actions: {
      cancel: function() {
        this.clear();
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
      }
    },

    // Views
    // -----
    //
    // One view per wizard 'step'.

    assignmentView: ActionPolicyAssignmentView,
    assignmentSaveView: AmEditSaveView,

    // Wizard State
    // ------------
    //
    // This state manager drives the wizard.

    wizard: function() {
      return Em.StateManager.create({
        initialState: 'assignment',
        controller: this,

        states: {
          // Assign action/policy and set the time
          assignment: Em.State.create({
            next: function(manager) {
              manager.transitionTo('status');
            }
          }),

          'status': Em.State.create({
            enter: function() {
              var controller = this.get('parentState.controller');

              controller.setProperties({
                urlForHelp: null,
                isDoneDisabled: true,
                submitInProgress: true,
                submitStatusMsg: controller.get('tInProgressMessage')
              });

              // If there is no policy to be added or removed just go to confirmation view
              if (controller.shouldSendAction()) {
                controller.sendActionRequest();
              } else {
                controller.setProperties({
                  isDoneDisabled: false,
                  submitInProgress: false,
                  submitStatusMsg: controller.get('tSuccessMessage')
                });
              }
            },

            prev: function(manager) {
              manager.get('controller').set('errorMessage', null);
              manager.transitionTo('assignment');
            }
          })
        }
      });
    }.property(),

    showingAssignment: Em.computed.equal('wizard.currentState.name', 'assignment'),
    showingStatus: Em.computed.equal('wizard.currentState.name', 'status'),

    // End of the Wizard setup
    // -----------------------

    init: function() {
      this._super();

      // For observers
      this.get('isSelectionListEmpty');
    },

    onShowModal: function() {
      this.set('heading', this.get('tHeading'));

      this.setProperties({
        urlForHelp: Help.uri(1074)
      });

      this.get('wizard').transitionTo('assignment');
    },

    isSelectionListEmpty: function() {
      var selectionController = this.get('selectionController');
      if (!selectionController) { return false; }

      return selectionController.get('selections').length === 0;

    }.property('selectionController.selections.[]',
      'selectionController.isExistingDataLoaded'),

    updateActionBtnDisabled: function() {
      var isIntervalInvalid = this.validateIntervalSettings();
      var isActionBtnDisabled = isIntervalInvalid;

      if (this.get('isSelectionListEmpty')) {
        this.setProperties({
          delayChecked: false,
          repeatChecked: false
        });

      } else {

        // If we are editing assignment properties
        if ('function' === typeof(this.getIsIntervalSettingsDirty)) {
          isActionBtnDisabled = isIntervalInvalid || !this.getIsIntervalSettingsDirty();
        }
      }

      this.set('isActionBtnDisabled', isActionBtnDisabled);

    }.observes('isSelectionListEmpty',
      'delayChecked',
      'initialDelay',
      'delaySelectorId',
      'repeatChecked',
      'repeatInterval',
      'repeatCount',
      'repeatSelectorId'),

    // By using DigitalFieldView we are disallowing entering any character except a digit
    // This validation is only a backup plan in case the library misbehaves or doesn't work in a browser
    validateIntervalSettings: function() {
      var isInitialDelayNaN = isNaN(this.get('initialDelay')),
        isRepeatIntervalNaN = isNaN(this.get('repeatInterval')),
        isRepeatCountNaN = isNaN(this.get('repeatCount'));

      this.setProperties({
        isInitialDelayNaN: isInitialDelayNaN,
        isRepeatIntervalNaN: isRepeatIntervalNaN,
        isRepeatCountNaN: isRepeatCountNaN
      });

      // Return false if any of these properties has a non-numeric value
      return isInitialDelayNaN || isRepeatIntervalNaN || isRepeatCountNaN;
    },

    // Populate the potential existing interval settings of the policy assignment
    populateExistingIntervalSettings: function(settings) {
      var initialDelay = 1, repeatInterval = 1, repeatCount = 1;
      var delayChecked = false, repeatChecked = false;
      var delaySelectorId = 'hours', repeatSelectorId = 'hours';
      var formattedInterval;

      if (settings.initialDelay > 0) {
        delayChecked = true;

        formattedInterval = Formatter.parseIntervalInDaysHoursOrMinutes(settings.initialDelay);
        initialDelay = formattedInterval.interval.toString();
        delaySelectorId = formattedInterval.frequency;
      }

      if (settings.repeatInterval > 0) {
        repeatChecked = true;

        formattedInterval = Formatter.parseIntervalInDaysHoursOrMinutes(settings.repeatInterval);
        repeatInterval = formattedInterval.interval.toString();
        repeatSelectorId = formattedInterval.frequency;
        repeatCount = settings.repeatCount.toString();
      }

      this.setProperties({
        delayChecked: delayChecked,
        oldDelayChecked: delayChecked,

        initialDelay: initialDelay,
        oldInitialDelay: initialDelay,

        delaySelectorId: delaySelectorId,
        oldDelaySelectorId: delaySelectorId,

        repeatChecked: repeatChecked,
        oldRepeatChecked: repeatChecked,

        repeatInterval: repeatInterval,
        oldRepeatInterval: repeatInterval,

        repeatSelectorId: repeatSelectorId,
        oldRepeatSelectorId: repeatSelectorId,

        repeatCount: repeatCount,
        oldRepeatCount: repeatCount
      });
    },

    formatIntervalToSecs: function(interval, selectorId) {
      return Formatter.formatIntervalDaysHoursOrMinutesInSecs(interval, selectorId);
    },

    // Do not send the action request if nothing has been selected to be assigned
    shouldSendAction: function() {
      if (!this.get('isAddAssignmentsMode')) {
        return true;
      } else {
        return !this.get('isSelectionListEmpty');
      }
    },

    // Default action is add/update policies
    // AddActionPolicyAssignmentController will override this in case a policy needs to be removed first.
    buildAction: function(currentIds) {
      // Set the default values for intervals
      var initialDelay = 0, repeatInterval = 0, repeatCount = 1;

      if (this.get('delayChecked')) {
        initialDelay = this.formatIntervalToSecs(this.get('initialDelay'), this.get('delaySelectorId'));
      }

      if (this.get('repeatChecked')) {
        repeatInterval = this.formatIntervalToSecs(this.get('repeatInterval'), this.get('repeatSelectorId'));
        repeatCount = this.get('repeatCount');
      }

      return AmData.get('actions.AmActionAddUpdatePolicyAssignmentAction').create({
        content: {
          actionIds: currentIds.actionIds,
          policyIds: currentIds.policyIds,

          initialDelay: initialDelay,
          repeatInterval: repeatInterval,
          repeatCount: repeatCount
        }
      });
    },

    onSuccessCallback: function() {
      this.setProperties({
        submitInProgress: false,
        submitStatusMsg: this.get('tSuccessMessage'),
        errorMessage: null,
        isDoneDisabled: false
      });
    },

    onErrorCallback: function() {
      var endPoint = this.get('action.endPoint');
      var errorDetails = this.get('errorDetails');

      this.setProperties({
        submitInProgress: false,
        submitStatusMsg: null,
        errorMessage: this.get('tErrorMessage'),
        errorDetails: errorDetails ? errorDetails + ' - ' + endPoint : endPoint
      });
    },

    clear: function () {
      var controller = this.get('selectionController');

      if (controller) {
        controller.resetController();
      }
    },

    // Reset interval settings every time a modal is showing
    resetSettings: function() {
      this.setProperties({
        initialDelay: 1,
        delaySelectorId: 'hours',

        repeatInterval: 1,
        repeatCount: 1,
        repeatSelectorId: 'hours'
      })
    }
  });
});
