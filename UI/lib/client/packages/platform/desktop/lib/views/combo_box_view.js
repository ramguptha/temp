define([
  'ember',
  'select2'
], function(
  Em,
  $
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile('<div class="select2-placeholder"></div>'),

    didInsertElement: function(evt) {
      this.$('.select2-placeholder').select2({
        dropdownContainer: this.$(),

        query: function(query) {
          query.callback({
            results: [
              { id: 0, text: 'zero' },
              { id: 1, text: 'one' }
            ]
          });
        }
      });
    },

    willRemoveElement: function(evt) {
      this.$('.select2-placeholder').select2('destroy');
    }
  });
});
