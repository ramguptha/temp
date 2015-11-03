define([
  'ember',
  'formatter',
  'locale'
], function(
  Em,
  Formatter,
  Locale
){
  'use strict';

  return Em.Mixin.create({

    actions: {
      cancel: function() {
        this.set('paused', null);

        if ('function' === typeof(this.clear)) {
          this.clear();
        }
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

    tInProgressMessage: 'shared.modals.inProgressMessage'.tr(),
    tSuccessMessage: 'shared.modals.successMessage'.tr(),
    tErrorMessage: 'shared.modals.errorMessage'.tr(),

    submitStatusMsg: null,
    errorMessage: null,
    errorDetails: null,

    isDoneDisabled: true,

    urlForHelp: null,

    // Views
    // -----
    //
    // One view per wizard 'step'.

    editView: null,
    editSaveView: null,

    // Wizard State
    // ------------
    //
    // This state manager drives the wizard.

    wizard: function() {
      return Em.StateManager.create({
        initialState: 'edit',
        controller: this,

        states: {
          // Edit properties (availability period etc) of selected policies
          // to which the content is assigned or add new policy assignments.
          edit: Em.State.create({
            next: function(manager) {
              manager.transitionTo('save');
            }
          }),

          // Save edited policy assignments
          save: Em.State.create({
            enter: function() {
              this.get('parentState.controller').setProperties({
                urlForHelp: null,
                paused: true
              });

              this.submitUpdates();
            },

            submitUpdates: function() {
              var controller = this.get('parentState.controller');
              controller.set('submitStatusMsg', controller.get('tInProgressMessage'));
              controller.submitUpdates(controller.onUpdateDone, controller.onUpdateError);
            },

            prev: function(manager) {
              manager.get('controller').set('errorMessage', null);
              manager.transitionTo('edit');
            },

            tryAgain: function(manager) {
              manager.get('controller').set('errorMessage', null);
              this.submitUpdates(manager);
            }
          })
        }
      });
    }.property(),

    showingEdit: Em.computed.equal('wizard.currentState.name', 'edit'),
    showingSave: Em.computed.equal('wizard.currentState.name', 'save'),

    // End of the Wizard setup
    // -----------------------

    onUpdateDone: function(self) {
      self.setProperties({
        submitStatusMsg: self.get('tSuccessMessage'),
        errorMessage: null,
        isDoneDisabled: false
      });
    },

    onUpdateError: function(self, details) {
      var jqXHR = details.jqXHR;
      var errorDetails = Formatter.formatErrorResponse(jqXHR.responseText);

      if(Em.isEmpty(errorDetails)) {
        if(jqXHR.statusText === 'timeout') {
          errorDetails = Locale.renderGlobals('shared.modals.errors.timeout').toString();
        } else {
          errorDetails = Locale.renderGlobals('shared.modals.errors.generic').toString();
        }
      }

      self.setProperties({
        submitStatusMsg: null,
        errorMessage: self.get('tErrorMessage'),
        errorDetails: errorDetails
      });
    }
  })
});