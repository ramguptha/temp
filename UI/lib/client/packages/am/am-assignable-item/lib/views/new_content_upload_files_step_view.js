define([
  'ember',
  'desktop',

  'text!../templates/new_content_upload_files_step.handlebars'
], function (
  Em,
  Desktop,
  template
) {

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),

    layout: Desktop.ModalWizardLayoutTemplate,

    canDrop: false,

    didInsertElement: function () {
      var self = this;

      $('.modal-wizard-window').on({
        dragend: function (e) {
          e.preventDefault();
          return false;
        },
        dragenter: function (e) {
          e.preventDefault();
          return false;
        },
        dragover: function (e) {
          $("#fileDropZone").addClass('drag-over');
          self.set('canDrop', true);
          e.preventDefault();

          return false;
        },
        dragleave: function (e) {
          $("#fileDropZone").removeClass('drag-over');
          self.set('canDrop', false);
          e.preventDefault();

          return false;
        }
      });

      $('.modal-window').on({
        dragend: function (e) {
          e.preventDefault();
          return false;
        },
        dragenter: function (e) {
          e.preventDefault();
          return false;
        },
        drop: function (e) {
          if (self.get('canDrop') === false) {
            e.preventDefault();
            return false;
          }
        },
        dragover: function (e) {
          if (self.get('canDrop') === false) {
            e.preventDefault();
            return false;
          }
        },
        dragleave: function (e) {
          if (self.get('canDrop') === false) {
            e.preventDefault();
            return false;
          }
        }
      });
    },

    dragenter: function (e) {
      e.preventDefault();
      return false;
    },

    drop: function (e) {
      $("#fileDropZone").removeClass('drag-over');
      this.set('canDrop', false);

      var inputFiles = e.dataTransfer.files;
      if (inputFiles !== undefined) {
        this.get('controller').uploadFiles(inputFiles, true);
      }

      e.preventDefault();
      return false;
    },

    // Event handler for file selection via input type="file" element
    change: function(e) {
      var inputFiles = e.target.files;
      if (!Em.isNone(inputFiles)) {
        this.get('controller').uploadFiles(inputFiles, false);
      }
      // need to reset the input value in case the user tries to upload the same file twice in a row
      Em.$('#browseFiles').val('');
    }
  });
});
