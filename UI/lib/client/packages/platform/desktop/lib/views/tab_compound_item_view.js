define([
  'ember',
  'locale',
  './tab_item_view'
], function (
  Em,
  Locale,
  TabItemView
) {
  'use strict';

  return Em.CollectionView.extend({
    tagName: 'ul',
    itemViewClass: TabItemView.extend({
      defaultTemplate: Ember.Handlebars.compile('<a href="#" class={{view.tab.itemClass}} {{action "gotoTab" view.tab.item target=view.parentView.context}}>{{view.label}}</a>'),
      tab: Em.computed.oneWay('content'),
      activeTab: Em.computed.oneWay('parentView.activeTab')
    }),
    classNames: ['sub-menu'],

    tab: null,
    activeTab: null,

    content: Em.computed.oneWay('tab'),

    didInsertElement: function () {
      this.$('li .is-active').parent().show().parent().removeClass('menu-closed').addClass('menu-opened');
    }
  });
});
