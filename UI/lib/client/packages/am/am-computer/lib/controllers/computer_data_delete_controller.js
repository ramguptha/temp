define([
  'ember',
  'jquery',
  'guid',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data',
  'query',

  './computer_summary_list_controller',

  '../views/computer_data_delete_set_properties_view',
  '../views/computer_data_delete_options_view',
  '../views/computer_data_delete_request_view',

  'text!../templates/computer_data_delete_service_agreement_text.handlebars'
], function(
  Em,
  $,
  Guid,
  AmComputer,
  Desktop,
  AmDesktop,
  AmData,
  Query,

  AmComputerSummaryListController,

  DataDeleteSetPropertiesView,
  DataDeleteOptionsView,
  DataDeleteRequestView,
  ServiceAgreementText
) {
  'use strict';

  return AmDesktop.ModalActionController.extend(Ember.Evented, {
    actions: {
      cancel: function() {
        this.cancelAll();
        this.send('closeModal');
      },

      gotoNextStep: function() {
        this.get('wizard').send('next');
      },

      gotoPreviousStep: function() {
        this.get('wizard').send('prev');
      },

      onVerifyPasswordAndDataDeleteAction: function() {
        this.get('wizard').send('finish');
      },

      seviceAgreementHandler: function() {
        this.send('showSeviceAgreementModal');
      }
    },

    computerListController: Em.inject.controller('AmComputerGroupsShowGroup'),

    heading: function() {
      var computers = this.get('computers');
      if (computers.length === 1) {
        // use jQuery to decode the name in case it has special characters. Do not change 'deviceName' to computerName

        var agentName = computers[0].get('agentName');
        this.set('deviceName', $('<textarea />').html(agentName).val());
        return 'Data Delete ';
      } else {
        this.set('deviceName', '');
        return 'Data Delete for the Selected Computers';
      }
    }.property('computers.[]'),

    headingIconClass: 'icon-file-text',

    actionButtonLabel: 'Data Delete',
    isActionBtnDisabled: true,
    isActionBtnVisible: true,

    inProgressMsg: 'Data Delete ...',
    successMsg: 'Data Delete has been requested.',
    errorMsg: 'Data Delete could not be requested.',
    actionWarning: '',
    showTryAgainButton: true,
    validationPasswordWarningMessage: '',

    // If not pass validation, disable continue button
    isContinueDisabled: true,

    // To display on second page from first page data

    // Fields on 1 page
    selectedReasonId: -1,
    reasonTitle: null,
    comment: '',

    // Fields on 2 page
    dataDeleteType: 0,
    customPolicy: '',
    // RadioButton - dataDeleteType
    eraseAllFilesAndOsOption: true,
    eraseAllFilesOption: false,
    customRulesOption: false,

    // Fields on 3 page
    passwordCommand: '',
    agreeAcceptAgreement: null,
    agreementText: ServiceAgreementText,

    // TODO all to the next release
    dataDeleteComment: null,
    requestName: '',
    overwrites: null,
    hardDriveSerial: null,
    perpetualDelete: null,
    ignoreFileAttributes: null,
    agreeAcceptEmail: null,

    route: null,

    // Views
    // -----
    //
    // One view per wizard 'step'.

    setPropertiesStepView: DataDeleteSetPropertiesView,
    optionsStepView: DataDeleteOptionsView,
    confirmationView: DataDeleteRequestView,

    // Wizard State
    // ------------
    //
    // This statemanager drives the wizard.

    wizard: function() {
      return Em.StateManager.create({
        initialState: 'step1',
        controller: this,

        states: {
          // Step 1 - Set Properties
          step1: Em.State.create({
            enter: function(manager) {
              manager.get('controller').dataValidationFirstPage();
            },

            next: function(manager) {
              manager.transitionTo('step2');
            }
          }),

          // Step 2 - Delete Options
          step2: Em.State.create({
            enter: function(manager) {
              manager.get('controller').dataValidationSecondPage();
            },

            next: function(manager) {
              manager.transitionTo('step3');
            },

            prev: function(manager) {
              manager.get('controller').set('errorMessage', null);
              manager.transitionTo('step1');
            }
          }),

          // Step 3 - Review & Request
          step3: Em.State.create({
            enter: function(manager) {
              manager.get('controller').dataValidationRequestPage();
            },

            finish: function(manager) {
              manager.transitionTo('status');
            },

            prev: function(manager) {
              manager.get('controller').set('errorMessage', null);
              manager.transitionTo('step2');
            }
          }),

          'status': Em.State.create({
            enter: function(manager) {
              manager.get('controller').sendActionRequest();
            }
          })
        }
      });
    }.property(),

    showingStep1: function() {
      return 'step1' === this.get('wizard.currentState.name');
    }.property('wizard.currentState.name'),

    showingStep2: function() {
      return 'step2' === this.get('wizard.currentState.name');
    }.property('wizard.currentState.name'),

    showingStep3: function() {
      return 'step3' === this.get('wizard.currentState.name');
    }.property('wizard.currentState.name'),

    showingStatus: function() {
      return 'status' === this.get('wizard.currentState.name');
    }.property('wizard.currentState.name'),

    initProperties: function()  {
      var computers = this.get('model');

      var computerListCtrl = this.get('computerListController');
      var unsupportedComputers = Em.A([]);
      var ids = '';
      for (var i=0; i < computers.length; i++) {
        ids += computers[i].get('id') + ', ';
        if (!computerListCtrl.supportsDataDeleteCommands(computers[i])) {
          unsupportedComputers.pushObject(computers[i]);
        }
      }

      var modalActionWindowClass = this.get('modalActionWindowClass');
      var unsupportedComputersMessage = null;
      var unsupportedComputerListController = null;
      var actionButtonLabel = "Data Delete";

      if (unsupportedComputers.length > 0) {
        unsupportedComputersMessage = "This command does not apply to " + unsupportedComputers.length + " of the " + computers.length + " selected computers. The following table shows a list of computers that are not included in this Data Delete request.";
        unsupportedComputerListController = AmComputerSummaryListController.create({
          dataStore: AmData.get('stores.computerStore').createStaticDataStore(unsupportedComputers)
        });

        modalActionWindowClass += " summary-list";
      }

      this.setProperties({
        computers: computers,

        modalActionWindowClass: modalActionWindowClass,
        actionButtonLabel: actionButtonLabel,

        selectedReasonId: -1,
        comment: '',
        eraseAllFilesAndOsOption: true,
        customPolicy: '',
        passwordCommand: '',
        agreeAcceptAgreement: null,

        unsupportedComputers: unsupportedComputers.length > 0,
        unsupportedComputersMessage: unsupportedComputersMessage,
        unsupportedComputerListController: unsupportedComputerListController
      });

      this.dataValidationFirstPage();
      // After properties are set, now go to step1 on wizard
      this.get('wizard').transitionTo('step1');
   },

    reasonList: function() {
      // Fixme: This store doesn't exsit
      // return AmData.get('stores.computerDataDeleteReasonStore').materializedObjects;
    }.property(),

    // Data changed events - First page
    onDataChangedFirstPage: function() {
      var reasonList =  this.get('reasonList');
      var selectedReasonId = this.get('selectedReasonId');
      var findRecordByIdArray = reasonList.filter(function(v){
        return v.data.id === selectedReasonId;
      });
      if(reasonList &&
        selectedReasonId &&
        selectedReasonId != -1 &&
        findRecordByIdArray.length === 1) {
        this.set('reasonTitle', findRecordByIdArray[0].data.dataDeleteReasonTitle);
      }

      // Data validation
      this.dataValidationFirstPage();
    }.observes('selectedReasonId', 'comment'),

    dataValidationFirstPage: function() {
      // Set to default
      this.setProperties({
        isActionBtnDisabled: false,
        isContinueDisabled: false,
        actionWarning: ''
      });

      // Disable the action button in some cases
      if(this.get('selectedReasonId') === -1) {
        this.setProperties({
          isContinueDisabled: true,
          actionWarning: 'Please select the reason for this request'
        });
      }
    },

    // Data changed events - Second page (Delete Options)
    onDataChangedSecondPage: function() {
      // Data validation
      this.dataValidationSecondPage();

      this.set('dataDeleteType', this.get('dataDeleteOptionChosen'));
    }.observes('eraseAllFilesAndOsOption', 'eraseAllFilesOption', 'customRulesOption', 'customPolicy'),

    dataValidationSecondPage: function() {
      // Set to default
      this.setProperties({
        isActionBtnDisabled: false,
        isContinueDisabled: false,
        actionWarning: ''
      });

      // Disable the action button in some cases
      if(this.get('customRulesOption') && Em.isEmpty(this.get('customPolicy'))) {
        this.setProperties({
          isContinueDisabled: true,
          actionWarning: 'Please specify full path to a file or a folder'
        });
      }
    },

    radioButtonPasswordTypeChanged: function(router, event) {
      if(this.get('paused')) {
        return;
      }
      this.set('paused', true);
      switch(event) {
        case 'eraseAllFilesAndOsOption':
          this.setProperties({
            eraseAllFilesOption: false,
            customRulesOption: false
          });
          break;
        case 'eraseAllFilesOption':
          this.setProperties({
            eraseAllFilesAndOsOption: false,
            customRulesOption: false
          });
          break;
        case 'customRulesOption':
          this.setProperties({
            eraseAllFilesOption: false,
            eraseAllFilesAndOsOption: false
          });
          break;
      }
      this.set('paused', false);
    }.observes('eraseAllFilesAndOsOption', 'eraseAllFilesOption', 'customRulesOption'),

    // Data changed events - Request page (3 page)
    onDataChangedRequestPage: function() {
      // Data validation
      this.dataValidationRequestPage();
    }.observes('passwordCommand', 'agreeAcceptAgreement'),

    dataValidationRequestPage: function() {
      // Set to default
      this.setProperties({
        isActionBtnDisabled: false,
        actionWarning: ''
      });

      // Disable the action button in some cases
      var password = this.get('passwordCommand');
      if(Em.isEmpty(password)) {
        this.setProperties({
          isActionBtnDisabled: true,
          actionWarning: 'Password field cannot be empty',
          validationPasswordWarningMessage: ''
        });
      }

      if(!this.get('agreeAcceptAgreement')) {
        this.setProperties({
          isActionBtnDisabled: true,
          actionWarning: 'In order to perform a Data Delete, you must agree to the Data Delete Agreement'
        });
      }
    },

    dataDeleteOptionChosen: function() {
      var selectedDataDeleteOption = 0;
      if(this.get('eraseAllFilesOption')) {
        selectedDataDeleteOption = 1;
      } else if (this.get('customRulesOption')) {
        selectedDataDeleteOption = 2;
      }
      return selectedDataDeleteOption;
    }.property('eraseAllFilesAndOsOption', 'eraseAllFilesOption', 'customRulesOption'),

    dataDeleteOptionChosenTitle: function() {
      var selectedDataDeleteOptionTitle = 'Erase All Files and Operating System';
      if(this.get('eraseAllFilesOption')) {
        selectedDataDeleteOptionTitle = 'Erase All Files';
      } else if (this.get('customRulesOption')) {
        selectedDataDeleteOptionTitle = 'Custom Rules';
      }
      return selectedDataDeleteOptionTitle;
    }.property('eraseAllFilesAndOsOption', 'eraseAllFilesOption', 'customRulesOption'),

    cancelAll: function() {
      this.setProperties({
        requestName: '',
        isContinueDisabled: true
      });
    },

    // Call password verification action, next device delete
    sendActionRequest: function() {
      var self = this;
      var computerListCtrl = this.get('computerListController');
      var computers = this.get('computers');

      this.setProperties({
        actionInProgress: true,
        confirmingAction: false,
        statusMsg: this.get('inProgressMsg')
      });

      var verificationAction = AmData.get('actions.AmComputerPasswordVerificationAction').create().reopen({
        password: self.get('passwordCommand'),

        onSuccess: function(response) {
          // Send Device Delete command after password verification
          AmData.get('actions.AmComputerDataDeleteAction').create().reopen({
            serialNumbers: computers.filter(function(computer) {
              return computerListCtrl.supportsDataDeleteCommands(computer);
            }).mapBy('data.agentSerialNumber'),

            requestName: self.get('requestName'),
            acceptAgreement: self.get('agreementText'),
            dataDeleteComment: self.get('dataDeleteComment'),
            comment: self.get('comment'),
            customPolicy: self.get('customPolicy'),
            dataDeleteType: self.get('dataDeleteType'),
            reason: self.get('selectedReasonId'),
            overwrites: self.get('overwrites'),
            hardDriveSerial: self.get('hardDriveSerial'),
            agreeAcceptAgreement: self.get('agreeAcceptAgreement'),
            perpetualDelete: self.get('perpetualDelete'),
            ignoreFileAttributes: self.get('ignoreFileAttributes'),
            agreeAcceptEmail: self.get('agreeAcceptEmail'),

            onSuccess: function(response) {
              self.set('actionInProgress', false);
              self.actionDidSucceed(response);
            },

            onError: function(ajaxError) {
              self.set('actionInProgress', false);
              self.actionDidFail(ajaxError);
            }
          }).invoke();
        },

        onError: function(ajaxError) {
          self.set('actionInProgress', false);
          self.actionDidFail(ajaxError);
        }
      })

      this.set('action', verificationAction);
      verificationAction.invoke();

      return true;
    },

    onErrorCallback: function(ajaxError) {
      var self = this;
      var jqXHR = ajaxError.get('jqXHR');

      switch(jqXHR.status) {
        case 401:
          // Custom approach for invalid password
          var warningMessage = 'The password is invalid';
          self.set('validationPasswordWarningMessage', warningMessage);

          // Go back to the main wizard.step2 and just display the error message
          this.get('wizard').transitionTo('step3');
          break;

        default:
          self.set('errorMsg', 'Password verification error.' + jqXHR.errorThrown + ' ' +  jqXHR.textStatus);
          break;
      }
    }
  });
});
