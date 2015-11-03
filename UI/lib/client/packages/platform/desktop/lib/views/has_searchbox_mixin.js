define([
  'ember'

], function(
  Em

  ) {
  "use strict";
  //This mixin is required for the regular, unmodified select2 components that we use on Nav lists
  return Em.Mixin.create({

    didInsertElement: function() {
      this._super();

      Em.run.scheduleOnce('afterRender', this, function () {
        this.$('.filter-column').select2({
          dropdownCssClass: 'filter-column-selects',
          containerCssClass: 'filter-column-select2-drop',
          dropdownAutoWidth: 'true'
        });
      });

      this.updateSelectFirstOptionValue();
    },

    willDestroyElement: function() {
      this._super();
      // Tear down controls and event handlers
      this.$('.filter-column').select2('destroy');
    },

    // Set a value to the fist option using its text.
    // After switch to modern Ember (1.5.1) you can't set selected='selected'.
    // In that case the 1st option doesn't have neither value nor selected='selected' attributes.
    // As we push 1st option using controller with name=null and label=All, the value is not set.
    // And if value is undefined, select2.js adds 'select2-result-unselectable' class to that option and prevents selection.
    updateSelectFirstOptionValue: function() {
      var firstOption = this.$('.filter-column > option:nth(0)');
      firstOption.attr('value', firstOption.text().toLowerCase());
    }
  });
});
