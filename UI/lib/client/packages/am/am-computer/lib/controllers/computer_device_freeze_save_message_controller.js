define([
  'ember',
  'jquery',
  '../namespace',
  'desktop',
  'am-desktop',
  'am-data'
], function(
  Em,
  $,
  AmComputer,
  Desktop,
  AmDesktop,
  AmData
) {
  'use strict';

  return AmDesktop.ModalActionController.extend({

    messageId: '',
    messageTitle: '',
    messageBody: '',

    initProperties: function(context)  {

      this.setProperties({
        // Have to pass something, otherwise  it is an exception about no content
        model: context,
        messageId: context.messageId,
        messageTitle: context.messageTitle,
        messageBody: context.messageBody
      });

      var modalActionWindowClass = this.get('modalActionWindowClass');

      this.setProperties({
        modalActionWindowClass: modalActionWindowClass,
        // No confirmation view to continue action without any stop
        confirmationView: null,
        headingIconClass: "icon-message",
        actionDescription: null,
        actionButtonLabel: "",
        isActionBtnDisabled: true,
        inProgressMsg: "Saving message...",
        successMsg: this.get('messageId') ? "The message was successfully updated." : "The message was successfully created.",
        errorMsg: this.get('messageId') ? "Error updating message for Device Freeze command." : "Error creating message for Device Freeze command."
      });
    },

    buildAction: function() {
      return AmData.get('actions.AmComputerDeviceFreezeSaveMessageAction').create({
        messageId: this.get('messageId'),
        messageTitle: this.get('messageTitle'),
        messageBody: this.get('messageBody')
      });
    }
  });
});
