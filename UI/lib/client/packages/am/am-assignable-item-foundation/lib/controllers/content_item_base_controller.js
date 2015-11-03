define([
  'ember',
  'desktop',
  'guid',

  'text!../templates/new_content_drop_zone.handlebars'
], function (
  Em,
  Desktop,
  Guid,

  dropZoneTemplate
) {

  // Content Item Base Controller
  // ==================================
  //
  // This controller contains the common properties/Views/Controllers between
  // all the controllers controlling various actions on the contents.
  // These actions include Add / Edit Properties / Edit Policies

  return Em.Controller.extend(Desktop.TransientController, {

    actions: {
      close: function() {
        this.send('closeModal');
      }
    },

    logDebug: false,
    logWarn: true,
    logError: true,

    paused: null,
    lock: null,
    displayClose: true,

    // View for drag & drop files - First step in Add Content wizard
    // ======================
    //
    newContentDropZoneView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(dropZoneTemplate),

      // Clicking on the drop zone triggers opening the file browser (i.e., the input type="file" element
      click: function (event) {
        $('#browseFiles').trigger('click');
      },

      cancel: function (event) {
        if (event.preventDefault) { event.preventDefault(); }
        return false;
      }
    }),

    ProgressBarView: Desktop.ProgressBarView,

    init: function () {
      this._super();

      this.setProperties({
        paused: true,
        lock: Guid.generate()
      });
    }
  });
});
