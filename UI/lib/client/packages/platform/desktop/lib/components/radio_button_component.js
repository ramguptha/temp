define([
  'ember'
], function(
  Em
) {
  return Em.Component.extend({
    tagName : 'input',
    type : 'radio',
    attributeBindings: ['type', 'checked:checked', 'value', 'name', 'disabled'],

    checked: function() {
      return this.get('value') === this.get('selection');
    }.property('value', 'selection'),

    click : function() {
      this.set('selection', this.$().val())
    }
  });
});
