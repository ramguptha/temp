define([
  'ember',
  'jqueryui',
  'packages/platform/date-type',
  'text!../templates/between_date_field.handlebars',
  '../views/date_field_util_mixin',
  '../localized_date_time_pickers'
], function(
  Em,
  $,
  DateType,
  template,
  DateFieldUtilMixin,
  LocalizedDateTimePickers
) {
  'use strict';

  return Em.Component.extend(DateFieldUtilMixin, {
    tFrom: 'desktop.datePickerComponent.fromPlaceholder'.tr(),
    tTo: 'desktop.datePickerComponent.toPlaceholder'.tr(),
    tagName: 'div',
    classNames: 'between-operator-block'.w(),

    layout: Em.Handlebars.compile(template),

    // The text form of fromDate, in UTC or browser timezone, depending on isShowingUtc
    value1: null,

    // The text form of toDate, in UTC or browser timezone, depending on isShowingUtc
    value2: null,

    // Beginning of date range, in UTC
    fromDate: null,

    // End of date range, in UTC
    toDate: null,

    App: function() { return window.App; }.property(),

    // Data is rendered / picked in this UTC offset.
    isShowingUtc: Em.computed.oneWay('App.isShowingUtc'),

    placeHolderFrom: function() {
      return this.get('tFrom').toString();
    }.property(),

    placeHolderTo: function() {
      return this.get('tTo').toString();
    }.property(),

    init: function() {
      this._super();
      this.updateValueOnDateChange();
    },

    didInsertElement: function() {
      this._super();

      var self = this;
      var isUpdated = false;
      var offset = this._getOffset();

      this.$('input.date-field-from').datepicker(LocalizedDateTimePickers.localize({
        maxDate: 0,
        yearRange: "c-10:c+0",
        showButtonPanel: true,
        duration: 0,

        beforeShow: function(element, instance) {
          // reset margin offset
          instance.dpDiv.css('margin-left', '0');
          instance.dpDiv.css('margin-top', '0');

          if( Em.isEmpty(self.get('value2')) ) {
            instance.settings.maxDate = null;
          }
        },

        beforeShowDay: function(date) {
          return self.updateDatesRange(date);
        },

        onSelect: function() {
          isUpdated = true;

          // By requirements when user selects a 'from' value we MUST clear 'to' values
          // clear 2d input value;
          self._getToInput().val('');
          self.set('value2', null);
        },

        onClose: function(selectedDate) {
          var input = self._getToInput();
          if(isUpdated) {
            input.datepicker('option', 'minDate', selectedDate);
            self.updateDateOnParsedValueChange();

            input.focus();
          } else {

            // check if there is an incorrect data input
            if(!self.parseDate(selectedDate) && !self.get('fromDate')) {
              self.clearDates();
            }
          }

          isUpdated = false;
        }
      }));

      this.$('input.date-field-to').datepicker(LocalizedDateTimePickers.localize({
        maxDate: 0,
        yearRange: "c-10:c+0",
        showButtonPanel: true,
        duration: 0,

        beforeShow: function(element, instance) {
          instance.dpDiv.css('margin-left', offset[0] + "px");
          instance.dpDiv.css('margin-top', offset[1] + "px");
        },

        beforeShowDay: function(date) {
          return self.updateDatesRange(date);
        },

        onSelect: function() {
          isUpdated = true;
        },

        onClose: function(selectedDate) {
          var input = self._getFromInput();
          if(isUpdated) {
            // check if there is a value 'from' set, otherwise clear selected value.
            var fromInputValue = self._getFromInput().val();
            if(fromInputValue){
              input.datepicker('option', 'maxDate', selectedDate);
            } else {
              // from date is not set, clear selected dates;
              self.clearDates();
            }

            self.updateDateOnParsedValueChange();
            input.focus();
          } else {

            // check if there is an incorrect data input
            if(!self.parseDate(selectedDate) && !self.get('toDate')) {
              self.clearDates();
            }
          }

          isUpdated = false;
        }
      }));
    },

    willDestroyElement: function() {
      this._super();

      this._getFromInput().datepicker('destroy');
      this._getToInput().datepicker('destroy');
    },

    clearDates: function() {
      var input1 = this._getFromInput();
      var input2 = this._getToInput();

      // clear input values 1st as data is parsed from these values;
      input1.val('');
      input2.val('');

      // set '' values -> binding is called
      this.set('value1', null);
      this.set('value2', null);

      // reset date constrains
      input2.datepicker('option', 'minDate', null);
    },

    updateDatesRange: function(date) {
      var date0 = this.parseDate(this.formatDate(date));
      var date1 = this.parseDate(this._getFromInput().val());
      var date2 = this.parseDate(this._getToInput().val());

      if (date0 && date1 && date2 && (date0 >= date1 && date0 <= date2) ) {
        return [true, "ui-state-selection-range"];
      }

      return [true, ""];
    },

    updateDateOnParsedValueChange: function() {
      var isShowingUtc = this.get('isShowingUtc');

      var input = this._getFromInput(), value, unadjustedValue, date;
      if (input) {
        unadjustedValue = this.parseDate(input.val());
        value = isShowingUtc ? unadjustedValue : DateType.incrementByUtcOffset(unadjustedValue);

        date = this.get('fromDate');

        if (this.valuesDiffer(value, date)) {
          this.set('fromDate', value);
        }
      }

      input = this._getToInput();
      if (input) {
        unadjustedValue = this.parseDate(input.val());
        value = isShowingUtc ? unadjustedValue : DateType.incrementByUtcOffset(unadjustedValue);

        date = this.get('toDate');

        if (this.valuesDiffer(value, date)) {
          this.set('toDate', value);
        }
      }

      // Note that updateDateOnParsedValueChange observes isShowingUtc, and updateValueOnDateChange does NOT.
      // Thus we prevent races between the two observers when isShowingUtc changes. We really only want one of 
      // them to fire under this circumstance anyway.
    }.observes('value1', 'value2', 'isShowingUtc'),

    updateValueOnDateChange: function() {
      var isShowingUtc = this.get('isShowingUtc');

      var parsedFromValue = this.parseDate(this.get('value1'));
      var parsedToValue = this.parseDate(this.get('value2'));

      var rval1 = isShowingUtc ? this.get('fromDate') : DateType.decrementByUtcOffset(this.get('fromDate'));
      var rval2 = isShowingUtc ? this.get('toDate') : DateType.decrementByUtcOffset(this.get('toDate'));

      if (this.valuesDiffer(parsedFromValue, rval1)) {
        this.set('value1', this.formatDate(rval1));
      }
      if (this.valuesDiffer(parsedToValue, rval2)) {
        this.set('value2', this.formatDate(rval2));
      }
    }.observes('fromDate', 'toDate'),

    _getFromInput: function() {
      return this.$('input.date-field-from');
    },

    _getToInput: function() {
      return this.$('input.date-field-to');
    },

    _getOffset: function() {
      var posFrom = this._getPos(this._getFromInput()[0]);
      var posTo = this._getPos(this._getToInput()[0]);

      return [posFrom[0] - posTo[0], posFrom[1] - posTo[1]];
    },

    _getPos: function(ele){
      var x=0;
      var y=0;
      while(true){
        x += ele.offsetLeft;
        y += ele.offsetTop;
        if(ele.offsetParent === null){
          break;
        }
        ele = ele.offsetParent;
      }
      return [x, y];
    }
  });
});
