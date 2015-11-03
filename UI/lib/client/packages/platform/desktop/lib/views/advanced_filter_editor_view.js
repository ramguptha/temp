define([
  'jquery',
  'ember',
  'text!../templates/advanced_filter_editor.handlebars'
], function(
  $,
  Em,
  template
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    classNames: 'advanced-search',

    didInsertElement: function() {
      Em.run.scheduleOnce('afterRender', this, function() {
        if (this.get('_state') === 'inDOM') {
          this.$('.column-spec-selector a').first().focus();
        }

        this.$('.filters-main-content').on('click', '.remove-filter', function() {
          $('.tipsy').remove();
          $(this).parents('.add-filter-container').find('button.add-filter-button').focus();
        }).on('mouseenter mouseleave focus blur', '.remove-or-block', function() {
          $(this).parent('.add-filter-container').toggleClass('or-block-hover');
        }).on('click', '.remove-or-block', function () {
          $(this).parents('.filters-main-content').find('button.add-single-filter-block-trigger').focus();
        });

        $(document).tipsy({live: '.remove-filter', trigger: 'focus', opacity: 1});
      });
    },

    willDestroyElement: function() {
      this.$('.filters-main-content').off();
      $('.tipsy').remove();
    }
  });
});
