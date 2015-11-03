define([
  'ember',

  'packages/platform/child-controller',

  './lib/views/empty_view',
  './lib/views/content_page_view',
  './lib/views/is_content_page_view_mixin',
  './lib/views/landing_page_view',
  './lib/views/nav_content_page_view',
  './lib/views/nav_tab_page_view',
  './lib/views/nav_page_view',
  './lib/views/tab_item_view',
  './lib/views/tab_compound_item_view',
  './lib/views/progress_bar_view',
  './lib/views/ng_text_field_view',
  './lib/views/searchable_select_view',

  './lib/components/radio_button_component',
  './lib/components/ip_field_component',
  './lib/components/date_time_picker_component',
  './lib/components/date_picker_component',
  './lib/components/time_picker_component',
  './lib/components/date_field_component',
  './lib/components/number_field_component',
  './lib/components/email_field_component',
  './lib/components/phone_field_component',
  './lib/components/ng_text_field_component',
  './lib/components/within_field_component',
  './lib/components/between_date_field_component',
  './lib/components/select_field_component',

  './lib/views/jquery_menu_view',
  './lib/controllers/jquery_menu_controller',

  './lib/views/tab_content_view',

  './lib/views/advanced_filter_view',
  './lib/views/advanced_filter_editor_view',
  './lib/views/advanced_filter_editor_line_item_view',
  './lib/controllers/advanced_filter_controller',
  './lib/controllers/advanced_filter_attr_picker_controller',
  './lib/views/advanced_filter_attr_picker_view',

  './lib/views/breadcrumbs_view',
  './lib/controllers/breadcrumbs_controller',

  './lib/controllers/nav_size_controller',

  './lib/views/tree_view',
  './lib/controllers/tree_controller',
  './lib/views/data_sourced_tree_view',
  './lib/controllers/data_sourced_tree_controller',

  './lib/views/simple_list_view',
  './lib/controllers/simple_list_controller',

  './lib/controllers/modal_column_chooser_controller',
  './lib/views/modal_column_chooser_view',

  './lib/views/combo_box_view',

  './lib/views/landing_view',
  './lib/controllers/landing_controller',

  './lib/transient_controller',

  './lib/layouts/modal_wizard_layout',
  './lib/layouts/modal_action_layout',
  './lib/layouts/modal_intro_layout',
  './lib/layouts/modal_generic_layout',

  './lib/controllers/modal_action_controller',
  'text!./lib/templates/modal_action.handlebars',
  './lib/views/modal_action_confirm_view',
  './lib/views/modal_action_status_view',

  './lib/controllers/modal_intro_controller',
  './lib/views/modal_intro_view',

  './lib/controllers/modal_error_controller',
  'text!./lib/templates/modal_error.handlebars',
  './lib/controllers/modal_communication_error_controller',

  'text!./lib/templates/spinner.handlebars',

  './lib/views/keyboard_events_mixin',
  './lib/views/date_field_util_mixin',
  './lib/views/within_field_view',

  './lib/controllers/modal_show_aggregate_data_controller',
  './lib/views/modal_show_aggregate_data_view',

  'i18n!./nls/strings'
], function(
  Em,

  ChildController,

  EmptyView,
  ContentPageView,
  IsContentPageViewMixin,
  LandingPageView,
  NavContentPageView,
  NavTabPageView,
  NavPageView,
  TabItemView,
  TabCompoundItemView,

  ProgressBarView,
  NgTextFieldView,
  SearchableSelectView,

  RadioButtonComponent,
  IpFieldComponent,
  DateTimePickerComponent,
  DatePickerComponent,
  TimePickerComponent,
  DateFieldComponent,
  NumberFieldComponent,
  EmailFieldComponent,
  PhoneFieldComponent,
  NgTextFieldComponent,
  WithinFieldComponent,
  BetweenDateFieldComponent,
  SelectFieldComponent,

  JqueryMenuView,
  JqueryMenuController,

  TabContentView,

  AdvancedFilterView,
  AdvancedFilterEditorView,
  AdvancedFilterEditorLineItemView,
  AdvancedFilterController,
  AdvancedFilterAttrPickerController,
  AdvancedFilterAttrPickerView,

  BreadcrumbsView,
  BreadcrumbsController,

  NavSizeController,

  TreeView,
  TreeController,
  DataSourcedTreeView,
  DataSourcedTreeController,

  SimpleListView,
  SimpleListController,

  ModalColumnChooserController,
  ModalColumnChooserView,

  ComboBoxView,

  LandingView,
  LandingController,

  TransientController,

  ModalWizardLayout,
  ModalActionLayout,
  ModalIntroLayout,
  ModalGenericLayout,

  ModalActionController,
  modalActionTemplate,
  ModalActionConfirmView,
  ModalActionStatusView,

  ModalIntroController,
  ModalIntroView,

  ModalErrorController,
  modalErrorTemplate,
  ModalCommunicationErrorController,

  SpinnerTemplate,

  KeyboardEventsMixin,
  DateFieldUtilMixin,
  WithinFieldView,

  ModalShowAggregateDataController,
  ModalShowAggregateDataView,

  strings
) {
  'use strict';

  return Em.Object.create({
    appClasses: {
      ModalErrorController: ModalErrorController,
      ModalErrorView: Em.View.extend({ defaultTemplate: Em.Handlebars.compile(modalErrorTemplate) }),

      CommunicationErrorController: ModalCommunicationErrorController,

      DatePickerComponent: DatePickerComponent,
      TimePickerComponent: TimePickerComponent,
      DateTimePickerComponent: DateTimePickerComponent,

      DateFieldComponent: DateFieldComponent,
      NumberFieldComponent: NumberFieldComponent,
      IpFieldComponent: IpFieldComponent,
      EmailFieldComponent: EmailFieldComponent,
      PhoneFieldComponent: PhoneFieldComponent,
      NgTextFieldComponent: NgTextFieldComponent,
      WithinFieldComponent: WithinFieldComponent,
      BetweenDateFieldComponent: BetweenDateFieldComponent,
      SelectFieldComponent: SelectFieldComponent,

      RadioButtonComponent: RadioButtonComponent
    },

    appActions: {
      showCommunicationError: function(settings) {
        this.showModal({ name: 'communication_error', model: settings, viewName: 'modalError' });
      }
    },

    appStrings: strings,

    EmptyView: EmptyView,

    DatePickerComponent: DatePickerComponent,
    TimePickerComponent: TimePickerComponent,
    DateTimePickerComponent: DateTimePickerComponent,
    NumberFieldComponent: NumberFieldComponent,

    ContentPageView: ContentPageView,
    IsContentPageViewMixin: IsContentPageViewMixin,
    LandingPageView: LandingPageView,
    NavContentPageView: NavContentPageView,
    NavTabPageView: NavTabPageView,
    NavPageView: NavPageView,
    TabItemView: TabItemView,
    TabCompoundItemView: TabCompoundItemView,

    JqueryMenuView: JqueryMenuView,
    JqueryMenuController: JqueryMenuController,

    NavSizeController: NavSizeController,

    AdvancedFilterView: AdvancedFilterView,
    AdvancedFilterEditorView: AdvancedFilterEditorView,
    AdvancedFilterEditorLineItemView: AdvancedFilterEditorLineItemView,
    AdvancedFilterController: AdvancedFilterController,
    AdvancedFilterAttrPickerController: AdvancedFilterAttrPickerController,
    AdvancedFilterAttrPickerView: AdvancedFilterAttrPickerView,

    BreadcrumbsView: BreadcrumbsView,
    BreadcrumbsController: BreadcrumbsController,

    TreeView: TreeView,
    TreeController: TreeController,
    DataSourcedTreeView: DataSourcedTreeView,
    DataSourcedTreeController: DataSourcedTreeController,

    ModalColumnChooserController: ModalColumnChooserController,
    ModalColumnChooserView: ModalColumnChooserView,

    ComboBoxView: ComboBoxView,

    LandingView: LandingView,
    LandingController: LandingController,

    ChildController: ChildController,
    TransientController: TransientController,

    TabContentView: TabContentView,

    ModalWizardLayoutTemplate: ModalWizardLayout,
    ModalActionLayoutTemplate: ModalActionLayout,
    ModalActionController: ModalActionController,
    ModalActionView: Em.View.extend({
      defaultTemplate: Em.Handlebars.compile(modalActionTemplate)
    }),
    ModalActionConfirmView: ModalActionConfirmView,
    ModalActionStatusView: ModalActionStatusView,
    ModalIntroLayout: ModalIntroLayout,

    HasIntro: Em.Mixin.create({
      actions: {
        showIntro: function() {
          this.showModal({ name: this.get('introPath') });
        }
      },

      // Path to the modal intro content
      introPath: null,

      renderTemplate: function(controller, model) {
        this._super(controller, model);

        var introController = this.controllerFor(this.get('introPath'));
        if (!introController.get('skipIntro')) {
          // Wait until we have fully transitioned
          Em.run.next(this, function() { this.send('showIntro'); });
        }
      }
    }),
    ModalIntroController: ModalIntroController,
    ModalIntroView: ModalIntroView,

    KeyboardEventsMixin: KeyboardEventsMixin,
    DateFieldUtilMixin: DateFieldUtilMixin,

    SpinnerTemplate:  Em.Handlebars.compile(SpinnerTemplate),

    ProgressBarView: ProgressBarView,
    NgTextFieldView: NgTextFieldView,    
    SimpleListView: SimpleListView,
    SimpleListController: SimpleListController,
    WithinFieldView: WithinFieldView,
    SearchableSelectView: SearchableSelectView,

    ModalShowAggregateDataController: ModalShowAggregateDataController,
    ModalShowAggregateDataView: ModalShowAggregateDataView,

    ModalGenericLayout: ModalGenericLayout,

    RadioButtonComponent: RadioButtonComponent
  });
});
