define([
  'ember',
  'guid',
  'select2'
], function(
    Em,
    Guid,
    $
) {
  'use strict';

  // SearchableSelectView
  // ====================
  //
  // A "select-like" component that can be searched. Intended for interactions where there are a large number of 
  // options to choose from.
  return Em.View.extend({
    tagName: 'input',
    attributeBindings: 'type'.w(),
    type: 'hidden',
    disabled: false,

    //we can have several select2 boxes on the page, so we need a way to differentiate them
    componentId: null,

    //show the check mark icon near the selected item
    showSelectionIcons: true,

    //show selected item in the dropdown container.
    //This is the switch between the "nav theme" and the "global search theme".
    showSelectionText: true,

    containerCssClass: null,

    // The options for the select.
    content: null,

    tPlaceholder: 'desktop.advancedFilterComponent.dataFieldPrompt'.tr(),
    placeholder: function() {
      return this.get('tPlaceholder').toString();
    }.property(),

    // Path to value / label in each object in options. To facilitate swapping this for Em.Select, paths must
    // begin with "content.".
    optionValuePath: null,
    optionLabelPath: null,

    value: null,

    // The content, transformed to select2's liking.
    select2Content: function() {
      var pathIndex = 'content.'.length;
      var valuePath = this.get('optionValuePath').substring(pathIndex);
      var labelPath = this.get('optionLabelPath').substring(pathIndex);

      var content = this.get('content');
      if (!Em.Enumerable.detect(content)) {
        content = Em.A();
      }

      return content.map(function(obj) {
        return {
          id: Em.get(obj, valuePath),
          text: Em.get(obj, labelPath).toString()
        }
      });
    }.property('optionValuePath', 'optionLabelPath', 'content.[]'),

    init: function () {
      this._super();
      //in case we have several different searchable select components on the page mark each one with random id.
      this.set('componentId', 'data-component-id-'+Guid.generate());

      if(!this.get('showSelectionText')) {
        //apply styles to the drop down button to hide the selected item text and change the background
        this.set('containerCssClass', 'hide-selection-text is-option-for-search')
      }
    },

    didInsertElement: function() {
      this._super();
      var self = this;
      var containerCssClass = this.get('containerCssClass');

      var $select2 = this.$().select2({
        dropdownCssClass: 'select-generic long-list search-enabled '+this.get('componentId'),
        containerCssClass: containerCssClass?containerCssClass:'is-option-for-search',
        dropdownAutoWidth: 'true',
        minimumResultsForSearch: 10,

        placeholder: self.get('placeholder'),

        formatResult: self.formatItemContent.bind(self),

        initSelection: function(element, callback) {
          Em.run(function() {
            var val = element.val();
            var select2Obj = self.get('select2Content').find(function(obj) {
              return obj.id === val;
            });
            callback(select2Obj);
          });
        },

        query: function(options) {
          Em.run(function() {
            var lowercaseTerm = options.term.toLowerCase();
            var results = self.get('select2Content').filter(function(select2Obj) {
              return -1 !== select2Obj.text.toLowerCase().indexOf(lowercaseTerm);
            });

            options.callback({
              more: false,
              results: results
            });
          });
        },

        formatResultCssClass: function(result) {
          var id = !Em.isEmpty(result.id) ? result.id : 'All';
          return this.containerCssClass + '-' + Em.String.dasherize(id);
        }
      });

      $select2.select2('val', self.get('value'));
      $select2.prop('tabindex', 0);
      $select2.select2('focus');

      $select2.on('change', function(e) {
        $(this).siblings('div.column-search-input').find('input').focus();
        if (self.get('value') !== e.val) {
          self.set('value', e.val);
        }
      }).on('open', function() {
        $('.select2-container').select2('close');
      });
    },

    willDestroyElement: function() {
      this.$().select2('destroy');
    },

    formatItemContent: function(item) {
      var result, componentId, iconClass, data;
      //don't show the icons
      if(!this.get('showSelectionIcons')) {
        result = item.text;
      }
      else {
        //show the check mark icon near the chosen item
        componentId = this.get('componentId');
        data = $('div.'+componentId).select2('data');
        //once selection is made "data" is not null. Put the check mark beside the chosen item
        if(data) {
          iconClass = (data.text === item.text) ? 'icon-checkmark icon-font-size-m' : 'icon-placeholder';
        }
        else {
          //When no selections were made just put a check mark beside the "All" item. (whose item.id is undefined)
          iconClass = (!item.id) ? 'icon-checkmark icon-font-size-m' : 'icon-placeholder';
        }
        result = '<b class="' + iconClass + '"></b>' + item.text;
      }
      return result;
    },

    updateSelect2OnValueChange: function() {
      var viewValue = this.get('value');
      var select2Value = this.$().select2('val');

      if (select2Value !== viewValue) {
        this.$().select2('val', viewValue);
      }
    }.observes('value')
  });
});
