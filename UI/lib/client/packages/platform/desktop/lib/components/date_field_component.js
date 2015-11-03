define([
  'ember',
  '../localized_date_time_pickers',
  'text!../templates/date_field.handlebars'
], function(
  Em,
  LocalizedDateTimePickers,
  template
  ) {
  'use strict';

  return Em.Component.extend({
    tagName: 'div',
    classNames: 'between-operator-block'.w(),
    dateValue: null,
    hasTime: false,
    layout: Em.Handlebars.compile(template),
    placeholder: null,
    tChooseDatePlaceholder: 'desktop.advancedFilterComponent.chooseDatePlaceholder'.tr(),
    tChooseDateAndTimePlaceholder: 'desktop.advancedFilterComponent.chooseDateAndTimePlaceholder'.tr(),

    placeholderDate: function() {
      return this.get('tChooseDatePlaceholder').toString();
    }.property(),

    placeholderDateTime: function() {
      return this.get('tChooseDateAndTimePlaceholder').toString();
    }.property(),

    didInsertElement: function() {
      var self = this;

      if ( this.get('hasTime') ) {
        this.set('placeholder', this.get('placeholderDateTime'));
        this.$('.date-field').datetimepicker(LocalizedDateTimePickers.localize({
          timezone: 0,
          onSelect: function(date) {
            self.set('dateValue', date);
          },
          onClose: function(dateText, inst) {
            function isDonePressed(){
              return ($('#ui-datepicker-div').html().indexOf('ui-datepicker-close ui-state-default ui-priority-primary ui-corner-all ui-state-hover') > -1);
            }

            if(isDonePressed() && Em.isEmpty(self.get('dateValue'))) {
              self.set('dateValue', self.pad(inst.selectedMonth,2) + '/' + self.pad(inst.selectedDay,2) + '/' + inst.selectedYear + ' 00:00');
            }
          }
        }));
        this.$('.date-field').on('focus', function (e) {
          self.$('.datepicker').show();
          $(document).on('mouseup', null, self, self.onOutsideDPHandler);
        });
      } else {
        this.set('placeholder', this.get('placeholderDate'));
        this.$('.date-field').datepicker(LocalizedDateTimePickers.localize({
          onSelect: function(date) {
            self.$('.datepicker').hide();
            self.set('dateValue', date);
          }
        }));
        this.$('.date-field').on('focus', function (e) {
          self.$('.datepicker').show();
          $(document).on('mouseup', null, self, self.onOutsideDPHandler);
        });
      }
    },

    onOutsideDPHandler: function(e) {
      var container = $('div.between-operator-block');

      if (!container.is(e.target) // if the target of the click isn't the container...
        && container.has(e.target).length === 0) {// ... nor a descendant of the container
        e.data.$('.datepicker').hide();
        $(document).off('mouseup', e.data.onOutsideDPHandler); //remove the click outside handler
      }
    },

    pad: function(str, max) {
      str = str.toString();
      return str.length < max ? this.pad("0" + str, max) : str;
    }
  });
});
