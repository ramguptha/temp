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

  '../views/computer_device_freeze_set_properties_view',
  '../views/computer_device_freeze_request_view',

  'text!../templates/computer_device_freeze_default_html_message.handlebars'
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

  DeviceFreezeSetPropertiesView,
  DeviceFreezeRequestView,
  DefaultHtmlMessageTemplate
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

      onVerifyPasswordAndFreezeDeviceAction: function() {
        this.get('wizard').send('finish');
      }
    },

    computerListController: Em.inject.controller('AmComputerGroupsShowGroup'),

    heading: function() {
      var computers = this.get('computers');
      if (computers.length === 1) {
        // use jQuery to decode the name in case it has special characters. Do not change 'deviceName' to computerName

        var computer = computers[0];
        var agentName = computer.get('data.agentName');
        this.set('deviceName', $('<textarea />').html(agentName).val());
        return 'Freeze ';
      } else {
        this.set('deviceName', '');
        return 'Device freeze for the Selected Computers';
      }
    }.property('computers.[]'),

    headingIconClass: "icon-file-text",

    actionButtonLabel: null,
    isActionBtnDisabled: true,
    isActionBtnVisible: false,

    inProgressMsg: "Device Freeze ...",
    successMsg: "Device Freeze has been requested.",
    errorMsg: "Device Freeze could not be requested.",
    actionWarning: '',
    showTryAgainButton: true,
    validationPasswordWarningMessage: '',


    // When it initialized here, the editor is not available yet. Observer triggered only if a variable was changed
    htmlEditorVisible: true,
    htmlEditorEnable: true,
    htmlEditorValue: '',

    // If not pass validation, disable continue button
    isContinueDisabled: true,

    // To display on second page from first page data

    // to pass to saving/updating/deleting message action
    selectedMessageTitle: '',
    forceRebootEnabledDisplay: 'No',

    // Fields on 1 page
    requestName: '',
    selectedMessageId: -1,
    selectedMessageHtml: DefaultHtmlMessageTemplate,
    generatePassword: true,
    specifyPassword: false,
    forceRebootEnabled: false,
    passwordDevice: '',
    editMode: 'editorHidden',

    // Fields on 2 page
    passwordCommand: '',

    route: null,

    computers: null,

    DigitalFieldView: AmDesktop.DigitalFieldView,

    // Views
    // -----
    //
    // One view per wizard 'step'.

    setPropertiesStepView: DeviceFreezeSetPropertiesView,
    confirmationView: DeviceFreezeRequestView,

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

          // Step 2 - Review & Request
          step2: Em.State.create({
            enter: function(manager) {
              manager.get('controller').dataValidationSecondPage();
            },

            finish: function(manager) {
              manager.transitionTo('status');
            },

            prev: function(manager) {
              manager.get('controller').set('errorMessage', null);
              manager.transitionTo('step1');
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
        if (!computerListCtrl.supportsDeviceFreezeCommands(computers[i])) {
          unsupportedComputers.pushObject(computers[i]);
        }
      }

      var modalActionWindowClass = this.get('modalActionWindowClass');
      var unsupportedComputersMessage = null;
      var unsupportedComputerListController = null;
      var actionButtonLabel = "Device Freeze";

      if (unsupportedComputers.length > 0) {
        unsupportedComputersMessage = "This command does not apply to " + unsupportedComputers.length + " of the " + computers.length + " selected computers. The following table shows a list of computers that are not included in this Device Freeze request.";
        unsupportedComputerListController = AmComputerSummaryListController.create({
          dataStore: AmData.get('stores.computerStore').createStaticDataStore(unsupportedComputers)
        });

        modalActionWindowClass += " summary-list";
      }

      this.setProperties({
        computers: computers,

        modalActionWindowClass: modalActionWindowClass,
        actionButtonLabel: actionButtonLabel,

        selectedMessageHtml: DefaultHtmlMessageTemplate,
        generatePassword: true,
        passwordCommand: '',
        validationPasswordWarningMessage: '',
        forceRebootEnabled: false,

        unsupportedComputers: unsupportedComputers.length > 0,
        unsupportedComputersMessage: unsupportedComputersMessage,
        unsupportedComputerListController: unsupportedComputerListController
      });

      // TODO remove this line in the next release
      this.set('editMode', 'addMode');

      // TODO enable it in the next release
/*
      var query = Query.Search.create({
        autoRefresh: true
      });
      // TODO add property to the content: content.messageStore
      this.set('content', AmComputer.get('AmData.stores.computerFreezeMessageStore').acquire(this.get('lock'), query, null, null, null, true).get('content'))
*/
      // After properties are set, now go to step1 on wizard
      this.get('wizard').transitionTo('step1');
    },

    // Data changed events - First page
    radioButtonPasswordTypeChanged: function(router, event) {
      if(this.get('paused')) {
        return;
      }
      this.set('paused', true);
      switch(event) {
        case "generatePassword":
          this.set('specifyPassword', false);
          break;
        case "specifyPassword":
          this.set('generatePassword', false);
          break;
      }
      this.set('paused', false);
     }.observes('generatePassword', 'specifyPassword'),

    onDataChangedFirstPage: function() {
      // Data validation
      this.dataValidationFirstPage();

      // TODO enable it in the next release
/*
      // Save MessageTitle and send html to editor
      var messagesList =  this.get('messagesList');
      var selectedMessageId = this.get('selectedMessageId');
      var findRecordByIdArray = messagesList.filter(function(v){
        return v["content"].data.id === selectedMessageId;
      });
      if(messagesList &&
        selectedMessageId &&
        findRecordByIdArray.length === 1) {
        this.set('selectedMessageTitle', findRecordByIdArray[0].content.data.deviceFreezeTitle);

        var html = findRecordByIdArray[0].content.data.deviceFreezeMessage;
        this.set('htmlEditorValue', html);
        // trigger setHtmlEditorValue do not fire change event in editor, so save html manually instead
        this.set('selectedMessageHtml', html);
      }

      // Show editor in 'Edit', 'Add' or 'ReadOnly' modes
      this.set('htmlEditorVisible', this.get('isEditOrAddMode') || this.get('isReadonlyMode'));

      // Editor is in read-only mode
      if(this.get('isReadonlyMode')) {
        this.set('htmlEditorEnable', false);
      }
*/

    }.observes('requestName', 'selectedMessageId', 'passwordDevice', 'specifyPassword', 'selectedMessageHtml', 'selectedMessageTitle'),

    dataValidationFirstPage: function() {
      // Set to default
      this.setProperties({
        isActionBtnDisabled: false,
        isContinueDisabled: false,
        actionWarning: ''
      });

      // Disable the action button in some cases

      // TODO enable it in the next release
/*    // Verify Request Name
      var requestName = this.get('requestName');
      if(Em.isEmpty(requestName)) {
        this.setProperties({
          isContinueDisabled: true,
          actionWarning: 'Request Name field cannot be empty'
        });
      }

       // Verify if requestName is unique TODO

      // Verify selected message
      if(this.get('selectedMessageId') === -1) {
        this.setProperties({
          isContinueDisabled: true,
          actionWarning: 'Please select Message'
        });
      }
*/

      // TODO Probably remove this line in the next release
      if(Em.isEmpty(this.get('selectedMessageHtml'))) {
        this.setProperties({
          isContinueDisabled: true,
          actionWarning: 'Message Text field cannot be empty'
        });
      }

      // Verify password field for 'Specify 8-digit password for selected devices' option
      if(this.get('specifyPassword')) {
        var password = this.get('passwordDevice');
        if (Em.isEmpty(password)) {
          this.setProperties({
            isContinueDisabled: true,
            actionWarning: 'Password field cannot be empty'
          });
        } else if (password.length != 8) {
          this.setProperties({
            isContinueDisabled: true,
            actionWarning: 'Password field must be 8 symbols'
          });
        }
      }

    },

    forceRebootEnabledChange: function() {
      this.set('forceRebootEnabledDisplay', this.get('forceRebootEnabled') == true ? 'Yes' : 'No');
    }.observes('forceRebootEnabled'),

    messagesList: function() {
      return this.get('content');
      // TODO try to use observes or find a bug in framework. Property is not refreshing after delete or save messages for some reasons.
      // Drop-down is refreshing now by handlebars somehow!!! Refresh store option exist in the action.
    }.property('content.data.[]'),

    // Data changed events - Second page
    onDataChangedSecondPage: function() {
      // Data validation
      this.dataValidationSecondPage();
    }.observes('passwordCommand'),

    dataValidationSecondPage: function() {
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
    },

    cancelAll: function() {
      this.setProperties({
        requestName: '',
        selectedMessageId: -1,
        isContinueDisabled: true
      });
    },

    // Password generating can be a part of framework
    passwordGenerate: function(length) {
      // From: http://www.mediacollege.com/internet/javascript/number/random.html
      // For symbols another good way: http://www.designchemical.com/blog/index.php/jquery/random-password-generator-using-jquery/
      var chars = "0123456789";
      var password = '';
      for (var i=0; i<length; i++) {
        var rnum = Math.floor(Math.random() * chars.length);
        password += chars.substring(rnum, rnum + 1);
      }
      return password;
    },

    onGeneratePasscode: function() {
      this.set('passwordDevice', this.passwordGenerate(8));
    },

    //Edit mode
    isHiddenMode: function () {
      return this.get('editMode') === 'editorHidden';
    }.property('editMode'),

    isReadonlyMode: function () {
      return this.get('selectedMessageId') !== -1 && this.get('editMode') !== 'editMode';
    }.property('selectedMessageId', 'editMode'),

    // Have to use combination Edit AddMode for using it in the handlebar
    isEditOrAddMode: function () {
      return this.get('editMode') === 'editMode' ||
             this.get('editMode') === 'addMode';
    }.property('editMode'),

    isEditMode: function () {
      return this.get('editMode') === 'editMode';
    }.property('editMode'),

    isAddMode: function () {
      return this.get('editMode') === 'addMode';
    }.property('editMode'),

    isHtmlEditorVisible : function () {
      return this.get('htmlEditorVisible');
    }.property('htmlEditorVisible'),

    // Button actions
    onEditMessageAction: function() {
      this.setProperties({
        editMode: 'editMode',
        htmlEditorEnable: true
      });
    },

    onAddMessageAction: function() {
      this.setProperties({
        selectedMessageId: -1,
        selectedMessageTitle: '',
        editMode: 'addMode',
        htmlEditorValue: '',
        htmlEditorVisible: true,
        htmlEditorEnable: true
      });
    },

    onCancelMessageAction: function() {
      this.setProperties({
        editMode: 'editorHidden',
        htmlEditorEnable: false
      });

      // Refresh to clean up variables
      this.onDataChangedFirstPage();

      if(!this.get('isReadonlyMode')) {
        this.set('htmlEditorVisible', false);
      }
    },

    onSaveMessageAction: function() {
      this.setProperties({
        editMode: 'editorHidden',
        htmlEditorEnable: false
      });

      if(!this.get('isReadonlyMode')) {
        this.set('htmlEditorVisible', false);
      }

      var selectedMessageId = this.get('selectedMessageId');

      // TODO to be implemented after port to Modern Ember is done and after the buttons are hooked up
      /*router.get('modalLayerController.target.modalManager').show(DeviceFreezeSaveMessageRoute.create(), {
        actionContext: Em.Object.create({
          messageId: selectedMessageId === -1 ? null : selectedMessageId,
          messageTitle: this.get('selectedMessageTitle'),
          messageBody: this.get('selectedMessageHtml')
        })
      });*/
    },

    onDeleteMessageAction: function() {
      this.setProperties({
        editMode: 'editorHidden',
        htmlEditorEnable: false
      });

      if(!this.get('isReadonlyMode')) {
        this.set('htmlEditorVisible', false);
      }

      // TODO to be implemented after port to Modern Ember is done and after the buttons are hooked up
      /*router.get('modalLayerController.target.modalManager').show(DeviceFreezeDeleteMessageRoute.create(), {
        actionContext: Em.Object.create({
          messageId: this.get('selectedMessageId'),
          messageName: this.get('selectedMessageTitle')
        })
      });*/

      // TODO Need to refresh after deleting instead, for some reasons messagesList property is not refreshed after delete action. Refresh store option exist in the action
      this.set('selectedMessageId', -1);
    },

    injectHtmlBegin: function() {
      return '<div style="display:table; -webkit-box-shadow: inset 0 0 150px rgba(0,0,0,.20); -moz-box-shadow: inset 0 0 150px rgba(0,0,0,.20); box-shadow: inset 0 0 150px rgba(0,0,0,.20); background-image: -webkit-linear-gradient(bottom, #dbdbdb, #ffffff); background-image: -moz-linear-gradient(bottom, #dbdbdb, #ffffff); background-image: -o-linear-gradient(bottom, #dbdbdb, #ffffff); background-image: linear-gradient(to top, #dbdbdb, #ffffff); background-color: #F0F0F0; width:100%; height:100%; font-family: Helvetica, Arial, Verdana !important;" >' +
             '  <div style="display:table-cell; width:100%; text-align:center; vertical-align:middle;">';
    },

    injectHtmlEnd: function() {
      return '</div>' +
        '  </div>';
    },

    transformHtml: function(html) {
      // User can paste any tag, so support as much as possible
      var result = html.replace(new RegExp('class="wysiwyg-color-black"', "g"), 'style="color:black"');
      result = result.replace(new RegExp('class="wysiwyg-color-silver"', "g"), 'style="color:silver"');
      result = result.replace(new RegExp('class="wysiwyg-color-gray"', "g"), 'style="color:gray"');
      result = result.replace(new RegExp('class="wysiwyg-color-maroon"', "g"), 'style="color:maroon"');
      result = result.replace(new RegExp('class="wysiwyg-color-red"', "g"), 'style="color:#B90003;"');
      result = result.replace(new RegExp('class="wysiwyg-color-purple"', "g"), 'style="color:purple"');
      result = result.replace(new RegExp('class="wysiwyg-color-green"', "g"), 'style="color:green"');
      result = result.replace(new RegExp('class="wysiwyg-color-olive"', "g"), 'style="color:olive"');
      result = result.replace(new RegExp('class="wysiwyg-color-navy"', "g"), 'style="color:navy"');
      result = result.replace(new RegExp('class="wysiwyg-color-blue"', "g"), 'style="color:blue"');
      result = result.replace(new RegExp('class="wysiwyg-color-orange"', "g"), 'style="color:orange"');
      result = result.replace(new RegExp('class="wysiwyg-color-lime"', "g"), 'style="color:lime"');
      result = result.replace(new RegExp('class="wysiwyg-color-aqua"', "g"), 'style="color:aqua"');
      result = result.replace(new RegExp('class="wysiwyg-color-orange"', "g"), 'style="color:orange"');
      result = result.replace(new RegExp('class="wysiwyg-color-white"', "g"), 'style="color:white"');
      result = result.replace(new RegExp('class="wysiwyg-color-teal"', "g"), 'style="color:teal"');

      result = result.replace(new RegExp('class="wysiwyg-text-align-center"', "g"), 'style="text-align:center"');
      result = result.replace(new RegExp('class="wysiwyg-text-align-justify"', "g"), 'style="text-align:justify"');
      result = result.replace(new RegExp('class="wysiwyg-text-align-left"', "g"), 'style="text-align:left"');
      result = result.replace(new RegExp('class="wysiwyg-text-align-right"', "g"), 'style="text-align:right"');

      result = result.replace(new RegExp('class="wysiwyg-font-size-xx-small"', "g"), 'style="font-size:xx-small"');
      result = result.replace(new RegExp('class="wysiwyg-font-size-medium"', "g"), 'style="font-size:medium"');
      result = result.replace(new RegExp('class="wysiwyg-font-size-large"', "g"), 'style="font-size:large"');
      result = result.replace(new RegExp('class="wysiwyg-font-size-x-large"', "g"), 'style="font-size:x-large"');
      result = result.replace(new RegExp('class="wysiwyg-font-size-xx-large"', "g"), 'style="font-size:xx-large"');
      result = result.replace(new RegExp('class="wysiwyg-font-size-x-large"', "g"), 'style="font-size:x-large"');

      result = this.injectHtmlBegin() + result + this.injectHtmlEnd()

      return result;
    },

    // Call password verification action, next device freeze
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
          // Send Device Freeze command after password verification
          AmData.get('actions.AmComputerDeviceFreezeAction').create().reopen({
            serialNumbers: computers.filter(function(computer) {
              return computerListCtrl.supportsDeviceFreezeCommands(computer);
            }).mapBy('data.agentSerialNumber'),

            forceRebootEnabled: self.get('forceRebootEnabled'),
            selectedMessageHtml: self.transformHtml(self.get('selectedMessageHtml')),
            specifyPassword: self.get('specifyPassword'),
            passwordDevice: self.get('passwordDevice'),

            // TODO enable it in the next release
            //requestName: this.get('requestName'),

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
          this.get('wizard').transitionTo('step2');
          break;

        default:
          self.set('errorMsg', 'Password verification error.' + jqXHR.errorThrown + ' ' +  jqXHR.textStatus);
          break;
      }
    }
  });
});
