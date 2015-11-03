define([
  'ember',
  'text!../templates/select_field_layout.handlebars'
], function(
  Em,
  template
) {
  'use strict';

  // A component replacement for Em.Select which supports the same options
  return Em.Component.extend({
    tagName: 'select',
    attributeBindings: ['disabled'],

    // this is needed to preserve the passed in value type since all <option> values
    // may only be retrieved as strings
    change: function(event) {
      var optionValuePath = this.get('optionValuePath').split('content.')[1];
      this.set('value', this.get('content')[Number(event.target.value)].get(optionValuePath));
    },

    // yeah yeah, this code sucks and should not be necessary since we can have the
    // selectContent depend on 'value', but FF's <select> doesn't work well when
    // <options> are re-rendered on value change
    onValueChange: function() {
      var selectContent = this.get('content'),
        value = this.get('value'),
        self = this;

      var optionValuePath = this.get('optionValuePath').split('content.')[1];

      for (var key in selectContent) {
        if (selectContent.hasOwnProperty(key) && selectContent[key].get(optionValuePath) === value) {
          self.$().val(key);
          break;
        }
      }
    }.observes('value'),

    layout: Em.Handlebars.compile(template),

    haveGroups: Ember.computed.bool('optionGroupPath'),

    selectContent: function() {
      var content = this.get('content'),
        value = this.get('value'),
        optionGroupPath = this.get('optionGroupPath'),
        optionValuePath = this.get('optionValuePath').split('content.')[1],
        optionLabelPath = this.get('optionLabelPath').split('content.')[1],
        prompt = this.get('prompt'),
        newContent = Em.A();

      if(content) {
        content.forEach(function(item, index) {
          var selected = false, newItem = Em.Object.create();

          if(optionLabelPath) {
            newItem.set('label', item.get(optionLabelPath));
          }

          if(item.get(optionValuePath) === value) {
            selected = true;
          }

          if(!Em.isEmpty(optionGroupPath)) {
            newItem.set('group', item.get(optionGroupPath));
          }

          newItem.set('selected', selected);
          newItem.set('value', index);
          newContent.push(newItem);
        });

        if(!Em.isEmpty(prompt)) {
          newContent.unshift(Em.Object.create({label: prompt, value: null}));
        }
      }

      return newContent;
    }.property('content', 'optionValuePath', 'optionLabelPath'),

    contentGroups: function() {
      var groups = [], optionGroupPath = this.get('optionGroupPath');
      this.get('selectContent').forEach(function(item) {
        var groupName = item.get(optionGroupPath);
        if(!groups.contains(groupName)) {
          groups.pushObject(groupName);
        }
      });

      return groups;
    }.property('selectContent', 'optionGroupPath')
  });
});