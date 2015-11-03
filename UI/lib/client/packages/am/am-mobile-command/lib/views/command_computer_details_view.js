define([
  'ember',
  'desktop',
  'ui',

  'text!../templates/command_computer_details.handlebars'
], function(
  Em,
  Desktop,
  UI,

  CommandDetailsTemplate
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(CommandDetailsTemplate),
    layout: Desktop.ModalWizardLayoutTemplate,

    didInsertElement: function() {
      var self = this;
      $('.modal-wizard-body').on('click', '.title-collapsible', (function(event){
        var controller = self.get('controller');
        var keyTag = $(this).parent().attr("data-name");

        switch (keyTag) {
          // For Device details and Command Details
          case 'device-details':
            var slidableElement = $(this).next('.content-collapsible');
            if (slidableElement.is(':visible') ){
              $(this).removeClass('content-opened').addClass('content-closed');
              $(this).parent().removeClass('block-opened').addClass('block-closed');
              slidableElement.slideUp();
            } else {
              $(this).removeClass('content-closed').addClass('content-opened');
              $(this).parent().removeClass('block-closed').addClass('block-opened');
              slidableElement.slideDown();
            }
            break;
          // For Device Freeze message preview and Data delete log file Preview
          case 'show-preview':
            var detailsMessage = $('.command-details-message');
            var slidableElement = detailsMessage.children('.content-collapsible');
            if (slidableElement.is(':visible') ){
              detailsMessage.removeClass('content-opened').addClass('content-closed');
              controller.set('previewTitleText', 'Show Preview');
              slidableElement.slideUp();
            } else {
              detailsMessage.removeClass('content-closed').addClass('content-opened');
              controller.set('previewTitleText', 'Hide Preview');
              slidableElement.slideDown();
            }
            break;
          // For Data delete Custom Delete Option
          case 'show-custom-preview':
            var details = $('.command-details-custom-policy');
            var slidableElement = details.children('.content-collapsible');
            if (slidableElement.is(':visible') ){
              details.removeClass('content-opened').addClass('content-closed');
              controller.set('messageCustomPolicyDetailsTitleText', 'Show Details');
              slidableElement.slideUp();
            } else {
              details.removeClass('content-closed').addClass('content-opened');
              controller.set('messageCustomPolicyDetailsTitleText', 'Hide Details');
              slidableElement.slideDown();
            }
            break;
        }

      }));
    },

    willDestroyElement: function() {
      $('.modal-wizard-body').off();
    }
  });
});
