define([
  'ember',
  './action_item_base_modal_view',

  'text!../templates/action_properties_set_wallpaper.handlebars'

], function(
  Em,
  ActionBaseModalView,

  template
) {
  return ActionBaseModalView.extend({
    defaultTemplate: Em.Handlebars.compile(template),

    didInsertElement: function() {
      this._super();

      $('#openFileButton').on('click', function(event) {
        // Click hidden system button to open file
        $('#selectFileId').trigger('click');
      });

      // Whole area
      $('#clickArea').on('click', function(event) {
        // Click hidden system button to open file
        $('#selectFileId').trigger('click');
      });

    },

    willDestroyElement: function() {
      this.$('#openFileButton').off();
      this.$('#clickArea').off();
    }
  });
});
