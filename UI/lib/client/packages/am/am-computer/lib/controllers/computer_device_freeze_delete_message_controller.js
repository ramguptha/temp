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

    initProperties: function(context)  {

      this.setProperties({
        // Have to pass something, otherwise  it is an exception about no content
        model: context,
        messageId: context.messageId,
        messageName: context.messageName
      });

      var modalActionWindowClass = this.get('modalActionWindowClass');

      this.setProperties({
        modalActionWindowClass: modalActionWindowClass,
        confirmationView: Desktop.get('ModalActionConfirmView'),
        headingIconClass: "icon-message",
        actionDescription: new Handlebars.SafeString("Confirm you want to delete <b>" + Handlebars.Utils.escapeExpression(this.get('messageName')) + "</b>. You cannot recover after deletion."),
        actionButtonLabel: "Delete",
        isActionBtnDisabled: true,
        inProgressMsg: "Deleting message...",
        successMsg: "The message was successfully deleted.",
        errorMsg: "Error deleting message for Device Freeze command."
      });
    },

    buildAction: function() {
      return AmData.get('actions.AmComputerDeviceFreezeDeleteMessageAction').create({
        messageId: this.get('messageId')
      });
    }
  });
});
