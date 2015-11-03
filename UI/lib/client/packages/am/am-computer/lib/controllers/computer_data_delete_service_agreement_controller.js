define([
  'ember',
  'desktop',
  'am-desktop',

  'text!../templates/computer_data_delete_service_agreement_text.handlebars'

], function(
  Em,
  Desktop,
  AmDesktop,

  ServiceAgreementText
  ) {
  'use strict';

  return AmDesktop.ModalActionController.extend({
    modalWindowClass: 'modal-window-generic',
    title: 'Data Delete Service Agreement',
    agreementText: Em.String.htmlSafe(ServiceAgreementText)
    });
});
