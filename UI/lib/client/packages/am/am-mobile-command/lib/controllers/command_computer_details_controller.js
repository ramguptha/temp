define([
  'ember',
  'jquery',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-computer-formatter',
  'guid',

  'am-data',
  'packages/platform/storage',
  'query',

  '../views/command_computer_details_view'
], function(
  Em,
  $,
  AmComputer,
  Desktop,
  AmDesktop,
  AmComputerFormatter,
  Guid,

  AmData,
  Storage,
  Query,

  AmCommandDetailsView
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    confirmationView: AmCommandDetailsView,

    previewTitleText: 'Show Preview',
    messageCustomPolicyDetailsTitleText: 'Show Details',

    commandDetailsLock: null,

    // Device Freeze
    forceRebootEnabledDisplay: '--',
    messageNameDisplay: '',
    messageHtmlDisplay: '',
    preSetPasswordDisplay: '',

    // Data Delete
    logFileShortTextDisplay: '',

    initProperties: function()  {
      var content = this.get('model.content');
      var deviceData = null;
      var commandId= null;


      if (!Em.isNone(content)) {
        deviceData = content.get('content');
        commandId = this.get('model.commandId');
      }

      var controller = this;

      var pageContent = Em.Object.create({
        deviceData: deviceData,
        commandData: null,
        logFileShortText: ''
      });

      this.setProperties({
        commandDetailsLock: Guid.generate(),
        content: pageContent,
        previewTitleText: 'Show Preview',
        messageCustomPolicyDetailsTitleText: 'Show Details'
      });

      if (Em.isNone(commandId)) {
        // Get it from Device Data

        // TODO hardcoded for now, get it from deviceData
        commandId = 330; // Verify 73, 74 also
      }

      controller.set('paused', true);

      // Get all command details
      AmData.get('stores.commandDetailStore').acquireOne(this.get('commandDetailsLock'), commandId, function () {
        controller.set('content.commandData', this.get('content.content'));
        controller.setProperties({
          paused: false
        });
      }, null, true, true);


      this.setProperties({
        modalActionWindowClass: this.get('modalActionWindowClass'),
        headingIconClass: "icon-file-text",
        actionDescription: '',
        actionButtonLabel: "OK",
        isActionBtnDisabled: true,
        logFileShortTextDisplay: ''
      });
    },

    // Format.BooleanOrNA does not work here
    lockAgentOptionsChanged: function() {
      this.set('forceRebootEnabledDisplay', this.get('content.commandData.data.details.LockAgentOptions') === true ? 'Yes' : 'No');
    }.observes('content.commandData.data.details.LockAgentOptions'),

    preSetPasswordDisplayChanged: function() {
      var preSetPassword = this.get('content.commandData.data.details.PreSetPassword');
      this.set('preSetPasswordDisplay', preSetPassword ? Em.String.htmlSafe(preSetPassword) : '');
    }.observes('content.commandData.data.details.PreSetPassword'),

    messageHtmlDisplayChanged: function() {
      var displayMessage = this.get('content.commandData.data.details.DisplayMessage');
      this.set('messageHtmlDisplay', displayMessage ? Em.String.htmlSafe(displayMessage) : '');
    }.observes('content.commandData.data.details.DisplayMessage'),

    // TODO change to MessageName from endpoint, after it be available. Remove displaying first symbols. Display MessageName field instead.
    messageNameDisplayChanged: function() {
      var name = this.get('content.commandData.data.details.DisplayMessage');
      if(name) {
        name = name.replace(/<\/?[^>]+(>|$)/g, "").trim();
        name = name.length > 29 ?  name.substring(0, 29) :  name.substring(0, name.length);
        this.set('messageNameDisplay', name + '...' );
      } else {
        this.set('messageNameDisplay', '');
      }
    }.observes('content.commandData.data.details.DisplayMessage'),

    // Dialog Title
    heading: function() {
      var commandData = this.get('content.commandData');
      if (commandData) {
        return 'Command Name - ' + this.get('commandTypeDisplay');
      }
    }.property('content.commandData'),

    commandTypeDisplay: function() {
      var commandType = this.get('commandData.data.commandType');
      var lockOrUnlockAgent = this.get('commandData.data.details.LockOrUnlockAgent');
      var displayText = '';
      switch (commandType) {
        // Device Freeze / Unfreeze
        case 2888:
          displayText = lockOrUnlockAgent ? 'Device Unfreeze' : 'Device Freeze';
          break;
        // Data Delete
        case 2889:
          displayText = 'Data Delete';
          break;
        // Data delete generate two commands till executed. Displayed in the Queued Commands grid differently
        // Data Delete from Queued grid
        case 2007:
          displayText = 'Data Delete';
          break;
        // Data Delete from Queued grid
        case 2005:
          displayText = 'Transfer File/Folder';
          break;
      }
      return displayText;
    }.property('commandData.data.details.DataDeleteType', 'commandData.data.details.LockOrUnlockAgent'),

    isCommandsDetailsAvailable: function() {
      return this.get('isCommandTypeDeviceFreeze') ||
             this.get('isCommandTypeDataDelete')
    }.property('isCommandTypeDeviceFreeze', 'isCommandTypeDataDelete'),

    osIconPath: function() {
      return AmComputerFormatter.getIconPathOsPlatform('16', this.get('deviceData.data.osPlatformNumber'));
    }.property('deviceData.data.osPlatformNumber'),

    isComputerEsnEmpty: function() {
      var esn = this.get('deviceData.data.computerEsn');
      return esn === null || Em.isEmpty(esn);
    }.property('deviceData.data.computerEsn'),

    makeInfo: function() {
      return this.get('deviceData.data.computerManufacturer');
    }.property('deviceData.data.computerManufacturer'),

    // Device Freeze
    deviceFreezeStatusClass: function() {
      return AmComputerFormatter.getIconClassFreezeStatus(this.get('deviceData.data.computerDeviceFreezeStatusNumber'));
    }.property('deviceData.data.computerDeviceFreezeStatusNumber'),

    isCommandTypeDeviceFreeze: function() {
      var commandType = this.get('commandData.data.commandType');
      var lockOrUnlockAgent = this.get('commandData.data.details.LockOrUnlockAgent');

      // Enumeration is in the database
      return commandType === 2888 &&
             !lockOrUnlockAgent;
    }.property('commandData.data.commandType'),

    // Data Delete
    isCommandTypeDataDelete: function() {
      var commandType = this.get('commandData.data.commandType');
      // Enumeration is in the database
      return commandType === 2889;
    }.property('commandData.data.commandType'),

    isDataDeleteTypeEraseAllFilesAndOS: function() {
      var dataDeleteType = this.get('commandData.data.details.DataDeleteType');
      return dataDeleteType ? dataDeleteType === 0 : false;
    }.property('commandData.data.details.DataDeleteType'),

    isDataDeleteTypeEraseAllFiles: function() {
      var dataDeleteType = this.get('commandData.data.details.DataDeleteType');
      return dataDeleteType ? dataDeleteType  === 1 : false;
    }.property('commandData.data.details.DataDeleteType'),

    isDataDeleteTypeCustomRules: function() {
      var dataDeleteType = this.get('commandData.data.details.DataDeleteType');
      return dataDeleteType ? dataDeleteType === 2 : false;
    }.property('commandData.data.details.DataDeleteType'),

    dataDeleteTypeDisplay: function() {
      var deleteType = this.get('commandData.data.details.DataDeleteType');
      var displayText = '';
      switch (deleteType) {
        case 0:
          displayText = 'Erase All Files and Operating System';
          break;
        case 1:
          displayText = 'Erase All Files';
          break;
        case 2:
          displayText = 'Custom Rules';
          break;
      }
      return displayText;
    }.property('commandData.data.details.DataDeleteType'),

    reasonList: function() {
      // Fixme: This store doesn't exsit
      // return AmData.get('stores.computerDataDeleteReasonStore').materializedObjects;
    }.property(),

    reasonTypeDisplay: function() {
      var reasonType = this.get('commandData.data.details.Reason');
      var findRecordByIdArray = this.get('reasonList').filter(function(v){
        return v.data.id === reasonType;
      });
      var displayText = findRecordByIdArray.length === 1 ? findRecordByIdArray[0].data.dataDeleteReasonTitle : '';
      return displayText;
    }.property('commandData.data.details.Reason'),

    commentDisplay: function() {
      var comment = this.get('commandData.data.details.Comment');
      return comment ? comment : '--';
    }.property('commandData.data.details.Comment'),

    customPolicyDisplay: function() {
      var customPolicy = '';
      var customPolicyArray = this.get('commandData.data.details.HelpmgrItems');
      if(customPolicyArray) {
        customPolicyArray.map(function (item){
          customPolicy += item.Name + '<br//>';
        });
      }
      return customPolicy ? Em.String.htmlSafe('<b>Custom directories and files paths</b><br>' + customPolicy) : '--';

    }.property('commandData.data.details.HelpmgrItems.[]'),

    logFileDisplayTitle: function() {
      // TODO change to real status: pending or ready to view
      return 'Ready to view';
    }.property('commandData.data.statusNumber'),

    isLogFileDisplay: function() {
      // Only if 'Finished successfully'
      return this.get('commandData.data.statusNumber') === 2;
    }.property('commandData.data.statusNumber'),

    logFileShortTextDisplayChanged: function() {
      var self = this;
      var commandUUID = this.get('commandData.data.commandUUID');
      var commandType = this.get('commandData.data.commandType');

      if (this.paused   ||
          !commandUUID  ||
          !commandType  ||
           // For some reasons it does not work from property: isCommandTypeDataDelete
           commandType != 2889 ||
          !this.get('isLogFileDisplay')) {
        return;
      }

      var logText = '';
      var action = AmData.get('actions.AmComputerCommandDownloadLogAction').create({
        commandUUID: commandUUID,
        isPreview: true
      }).reopen({
        onSuccess: function(rsp) {
          logText = rsp;
          if(logText) {
            self.set('logFileShortTextDisplay', Em.String.htmlSafe('<i>Preview may only show partial data. To view the entire log file click Download log.</i><br>' + logText.replace(/\n/g, '<br//>')) );
          }
        },

        onError: function(jqXHR) {
          switch(jqXHR.status) {
            case 404:
              self.set('logFileShortTextDisplay', Em.String.htmlSafe('<i>Unable to locate the log file. File may not exist</i>') );
              break;
            default:
              self.set('logFileShortTextDisplay', Em.String.htmlSafe('<i>Loading file error:</i><br>' + jqXHR.errorThrown + ' ' +  jqXHR.textStatus) );
              break;
          }
        }
      });
      action.invoke();

    }.observes('commandData.data.commandUUID'),

    logFileDownloadLink: function() {
      var commandUUID = this.get('commandData.data.commandUUID');
      // preview=false - full file download
      return commandUUID ? AmData.urlRoot + '/api/computercommands/datadelete/logs/' + commandUUID + '?preview=false' : '';
    }.property('commandData.data.commandUUID')

  });
});
