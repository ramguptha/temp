define([
  'jquery',
  'ember',
  '../layouts/modal_wizard_layout',
  'text!../templates/modal_column_chooser.handlebars',
  'logger'
], function(
    $,
    Em,
    ModalWizardLayout,
    template,
    logger
) {
  'use strict';

  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: ModalWizardLayout,

    MANDATORY_STR: ' &mdash; mandatory column',
    GROUP_STR: ' &mdash; grouped column',

    didInsertElement: function() {
      this._super();

      this.setupDragAndDropColumns();

      this.populateVisibleSortableColumn();
      this.populateAvailableSortableColumn();
    },

    willDestroyElement: function() {
      this.$('.available-columns-list').sortable('destroy');
      this.$('.include-columns-list').sortable('destroy').off('click');
    },

    setupDragAndDropColumns: function() {
      var self = this;

      var sourceContainer = this.$('.available-columns-block');
      var targetContainer = this.$('.include-columns-block');
      var sourceList = this.$('.available-columns-list');
      var targetList = this.$('.include-columns-list');

      this.$(sourceList).sortable({
        connectWith: targetList,
        placeholder: 'add-column-list-highlight',
        distance: 5,
        revert: 200,
        appendTo: targetList,
        helper: 'clone',
        start: function() {
          self.$(targetContainer).addClass('add-column-highlight');
        },
        stop: function(event, ui) {
          self.$(targetContainer).removeClass('add-column-highlight');

          // disable sorting columns on the same list
          if (ui.item.get(0).parentElement === sourceList.get(0)) {
            self.$(sourceList).sortable('cancel');
          }
          self.pushVisibleColumnLabelsToController();
        }
      }).disableSelection();

      this.$(targetList).sortable({
        connectWith: sourceList,
        placeholder: 'add-column-list-highlight',
        distance: 5,
        revert: 200,
        items: 'li:not(.ui-state-disabled)',
        start: function() {
          self.$(sourceContainer).addClass('add-column-highlight');
        },
        stop: function(event, ui) {
          self.$(sourceContainer).removeClass('add-column-highlight');
          self.pushVisibleColumnLabelsToController();
        },
        create: function(event, ui) {
          // The user pressed on the red remove icon
          self.$(targetList).on('click', 'a', function() {
            var parentLi = self.$(this).parent();
            parentLi.appendTo(sourceList);

            // sort 'available columns' list in alphabetical order
            var listItems = sourceList.find('li');
            listItems.sort(function(a, b) {
              return self.$(a).text().toUpperCase().localeCompare(self.$(b).text().toUpperCase());
            });
            $.each(listItems, function(index, item) { sourceList.append(item); });
            self.pushVisibleColumnLabelsToController();
          });
        }
      }).disableSelection();
    },

    populateVisibleSortableColumn: function() {
      var includeUL = this.$('.include-columns-list');

      var mandatoryColumns = this.get('controller.mandatoryNames');
      if (Em.isNone(mandatoryColumns)) {
        mandatoryColumns = Em.A();
      }

      var groupedColumns = this.get('controller.groupedColumnNames');
      if (Em.isNone(groupedColumns)) {
        groupedColumns = Em.A();
      }

      var disabledClass = 'class="ui-state-disabled"';
      var isGroupingColumn = false;
      var isMandatoryColumn = false;
      var grpStr = this.get('GROUP_STR');
      var mndtStr = this.get('MANDATORY_STR');
      var view = this;
      var label = '';
      this.get('controller.visibleColumnNames').forEach(function(name) {

        isGroupingColumn = (groupedColumns.contains(name)) ? true : false;
        isMandatoryColumn = (mandatoryColumns.contains(name)) ? true : false;
        label = view.get('controller').name2Label(name);

        if (isGroupingColumn) {
          includeUL.append('<li data-column-name="'+name+'"'+disabledClass+'>'+label+' <span>'+grpStr+'</span></li>');
        }
        else if(isMandatoryColumn) {
          includeUL.append('<li data-column-name="'+name+'"'+disabledClass+'>'+label+' <span>'+mndtStr+'</span></li>');
        } else {
          includeUL.append('<li data-column-name="'+name+'">'+label+'<a class="icon-remove-column icon-minus hide-block"></a></li>');
        }
      });
    },

    populateAvailableSortableColumn: function() {
      if (this.get('_state') === 'preRender') {
        return;
      }
      var view = this;
      var availableUL = this.$('.available-columns-list');
      availableUL.empty();

      this.get('controller.availableColumnNames').forEach(function(name) {
        availableUL.append('<li data-column-name="'+name+'">'+view.get('controller').name2Label(name)+'<a class="icon-remove-column icon-minus hide-block"></a></li>');
      });
    }.observes('controller.availableColumnNames.[]'),

    pushVisibleColumnLabelsToController: function() {
      var self = this;

      var list = this.$('.include-columns-list li').map(function(idx, item) {
        return self.$(item).attr('data-column-name');
      });

      // this.$ does not have makeArray, and jQuery arrays fool Em.A()
      this.set('controller.visibleColumnNames', $.makeArray(list));
    }
  });
});
