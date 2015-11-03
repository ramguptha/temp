define([
  './lib/controllers/am_list_controller',
  './lib/controllers/am_nav_controller',
  './lib/controllers/list_controller_columns',
  './lib/controllers/modal_action_controller',

  './lib/views/am_list_view',
  './lib/views/am_selection_list_view',
  './lib/views/am_nav_tab_page_view',
  './lib/views/am_value_components',
  './lib/views/saves_column_width',

  './lib/views/digital_field_view',
  './lib/components/decimal_field_component',
  './lib/components/file_upload_component',
  './lib/components/multi_phone_field_component',
  './lib/components/multi_email_field_component',
  './lib/components/am_date_time_picker_component',

  'i18n!./nls/strings'
], function(
  AmListController,
  AmNavController,
  AmListControllerColumns,
  AmModalActionController,

  AmListView,
  AmSelectionListView,
  AmNavTabPageView,
  AmValueComponents,
  SavesColumnWidth,

  DigitalFieldView,
  DecimalFieldComponent,
  FileUploadComponent,
  MultiPhoneFieldComponent,
  MultiEmailFieldComponent,
  AmDateTimePickerComponent,

  strings
) {
  'use strict';

  // AmDesktop
  // =========
  //
  // Absolute Manage related customizations of the Desktop package.
  //
  return {
    appClasses: {
      AmFormattedIconComponent: AmValueComponents.AmFormattedIconComponent,
      AmFormattedOsPlatformComponent: AmValueComponents.AmFormattedOsPlatformComponent,
      AmComputerFormattedOsPlatformComponent: AmValueComponents.AmComputerFormattedOsPlatformComponent,
      AmComputerCommandStatusIconComponent: AmValueComponents.AmComputerCommandStatusIconComponent,
      AmFormattedTypeComponent: AmValueComponents.AmFormattedTypeComponent,
      AmAgentAvailabilityIconFormatterComponent: AmValueComponents.AmAgentAvailabilityIconFormatterComponent,
      AmDateTimePickerComponent: AmDateTimePickerComponent,
      DecimalFieldComponent: DecimalFieldComponent,
      FileUploadComponent: FileUploadComponent,
      MultiPhoneFieldComponent: MultiPhoneFieldComponent,
      MultiEmailFieldComponent: MultiEmailFieldComponent
    },

    AmListController: AmListController,
    AmNavController: AmNavController,
    ListControllerColumns: AmListControllerColumns,
    ModalActionController: AmModalActionController,

    AmListView: AmListView,
    AmSelectionListView: AmSelectionListView,

    AmNavTabPageView: AmNavTabPageView,

    DigitalFieldView: DigitalFieldView,

    AmDateTimePickerComponent: AmDateTimePickerComponent,

    SavesColumnWidth: SavesColumnWidth,

    appStrings: strings
  };
});
