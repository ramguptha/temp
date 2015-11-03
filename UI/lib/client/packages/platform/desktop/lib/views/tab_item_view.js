define([
  'ember',
  'locale',
   'text!../templates/tab_item.handlebars'
], function (
  Em,
  Locale,
  template
) {
  'use strict';

  return Em.View.extend({
    tagName: 'li',
    classNameBindings: ['isActive'],
    defaultTemplate: Em.Handlebars.compile(template),

    tab: null,
    activeTab: null,

    isActive: function() {
      return this.get('tab.item') === this.get('activeTab');
    }.property('tab.item', 'activeTab'),

    contextDidChange: function() {
      this.set('isActive', this.get('tab.item') === this.get('context.activeTab'));
    }.observes('context.activeTab'),

    labelFromResource: Locale.translated({ property: 'tab.labelResource' }),

    label: function() {
      var name = this.get('tab.name');
      var label = this.get('labelFromResource');
      return name || label;
    }.property('tab.name', 'labelFromResource')
  });
});
