define([
  'ember',
  'select2',
  'i18n!./nls/strings'
], function(
  Em,
  $,
  strings
) {
  'use strict';

  // Nth Day of Month Picker View
  // ============================
  //
  // Selects nth day of the month. It would be preferred for this to be a component, but unfortunately
  // Em.Select is a view.

  // Create day picker options statically.
  var dayOptions = Array(31);
  for (var i = 0; i < dayOptions.length; i++) {
    var dayOrdinal = i + 1;

    var option = {
      name: dayOrdinal, 
      value: dayOrdinal
    };

    dayOptions[i] = option;
  }

  var NthDayOfMonthPickerView = Em.Select.extend({
    classNames: 'select2-daypicker'.w(),

    t1stDayOfMonth:  'nthDayOfMonthPicker.options.t1stDayOfMonth'.tr(),
    t2ndDayOfMonth:  'nthDayOfMonthPicker.options.t2ndDayOfMonth'.tr(),
    t3rdDayOfMonth:  'nthDayOfMonthPicker.options.t3rdDayOfMonth'.tr(),
    t4thDayOfMonth:  'nthDayOfMonthPicker.options.t4thDayOfMonth'.tr(),
    t5thDayOfMonth:  'nthDayOfMonthPicker.options.t5thDayOfMonth'.tr(),
    t6thDayOfMonth:  'nthDayOfMonthPicker.options.t6thDayOfMonth'.tr(),
    t7thDayOfMonth:  'nthDayOfMonthPicker.options.t7thDayOfMonth'.tr(),
    t8thDayOfMonth:  'nthDayOfMonthPicker.options.t8thDayOfMonth'.tr(),
    t9thDayOfMonth:  'nthDayOfMonthPicker.options.t9thDayOfMonth'.tr(),
    t10thDayOfMonth: 'nthDayOfMonthPicker.options.t10thDayOfMonth'.tr(),
    t11thDayOfMonth: 'nthDayOfMonthPicker.options.t11thDayOfMonth'.tr(),
    t12thDayOfMonth: 'nthDayOfMonthPicker.options.t12thDayOfMonth'.tr(),
    t13thDayOfMonth: 'nthDayOfMonthPicker.options.t13thDayOfMonth'.tr(),
    t14thDayOfMonth: 'nthDayOfMonthPicker.options.t14thDayOfMonth'.tr(),
    t15thDayOfMonth: 'nthDayOfMonthPicker.options.t15thDayOfMonth'.tr(),
    t16thDayOfMonth: 'nthDayOfMonthPicker.options.t16thDayOfMonth'.tr(),
    t17thDayOfMonth: 'nthDayOfMonthPicker.options.t17thDayOfMonth'.tr(),
    t18thDayOfMonth: 'nthDayOfMonthPicker.options.t18thDayOfMonth'.tr(),
    t19thDayOfMonth: 'nthDayOfMonthPicker.options.t19thDayOfMonth'.tr(),
    t20thDayOfMonth: 'nthDayOfMonthPicker.options.t20thDayOfMonth'.tr(),
    t21stDayOfMonth: 'nthDayOfMonthPicker.options.t21stDayOfMonth'.tr(),
    t22ndDayOfMonth: 'nthDayOfMonthPicker.options.t22ndDayOfMonth'.tr(),
    t23rdDayOfMonth: 'nthDayOfMonthPicker.options.t23rdDayOfMonth'.tr(),
    t24thDayOfMonth: 'nthDayOfMonthPicker.options.t24thDayOfMonth'.tr(),
    t25thDayOfMonth: 'nthDayOfMonthPicker.options.t25thDayOfMonth'.tr(),
    t26thDayOfMonth: 'nthDayOfMonthPicker.options.t26thDayOfMonth'.tr(),
    t27thDayOfMonth: 'nthDayOfMonthPicker.options.t27thDayOfMonth'.tr(),
    t28thDayOfMonth: 'nthDayOfMonthPicker.options.t28thDayOfMonth'.tr(),
    lastDayOfMonth:  'nthDayOfMonthPicker.options.lastDayOfMonth'.tr(),

    content: dayOptions,

    optionLabelPath: 'content.name',
    optionValuePath: 'content.value',

    didInsertElement: function() {
      this.$().select2({
        containerCssClass: 'select2-daypicker-container',
        dropdownCssClass: 'select2-daypicker-dropdown',
        formatSelection: this.formatSelectedResult.bind(this)
      });
    },

    willDestroyElement: function() {
      this.$().select2('destroy');
    },

    formatSelectedResult: function(state) {
      var result = '';
      var chosenDay = parseInt(state.id, 10);
      switch (chosenDay) {
        case 1:
          result = this.get('t1stDayOfMonth');
          break;
        case 2:
          result = this.get('t2ndDayOfMonth');
          break;
        case 3:
          result = this.get('t3rdDayOfMonth');
          break;
        case 4:
          result = this.get('t4thDayOfMonth');
          break;
        case 5:
          result = this.get('t5thDayOfMonth');
          break;
        case 6:
          result = this.get('t6thDayOfMonth');
          break;
        case 7:
          result = this.get('t7thDayOfMonth');
          break;
        case 8:
          result = this.get('t8thDayOfMonth');
          break;
        case 9:
          result = this.get('t9thDayOfMonth');
          break;
        case 10:
          result = this.get('t10thDayOfMonth');
          break;
        case 11:
          result = this.get('t11thDayOfMonth');
          break;
        case 12:
          result = this.get('t12thDayOfMonth');
          break;
        case 13:
          result = this.get('t13thDayOfMonth');
          break;
        case 14:
          result = this.get('t14thDayOfMonth');
          break;
        case 15:
          result = this.get('t15thDayOfMonth');
          break;
        case 16:
          result = this.get('t16thDayOfMonth');
          break;
        case 17:
          result = this.get('t17thDayOfMonth');
          break;
        case 18:
          result = this.get('t18thDayOfMonth');
          break;
        case 19:
          result = this.get('t19thDayOfMonth');
          break;
        case 20:
          result = this.get('t20thDayOfMonth');
          break;
        case 21:
          result = this.get('t21stDayOfMonth');
          break;
        case 22:
          result = this.get('t22ndDayOfMonth');
          break;
        case 23:
          result = this.get('t23rdDayOfMonth');
          break;
        case 24:
          result = this.get('t24thDayOfMonth');
          break;
        case 25:
          result = this.get('t25thDayOfMonth');
          break;
        case 26:
          result = this.get('t26thDayOfMonth');
          break;
        case 27:
          result = this.get('t27thDayOfMonth');
          break;
        case 28:
          result = this.get('t28thDayOfMonth');
          break;
        default:
          result = this.get('lastDayOfMonth');
      }

      return result.toString();
    },
  });

  return NthDayOfMonthPickerView.reopenClass({
    appStrings: strings,
    appClasses: {
      NthDayOfMonthPickerView: NthDayOfMonthPickerView
    }
  });
});
