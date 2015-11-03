define([
  'ember',
  'ui',

  '../namespace',
  './assignable_list_base_controller'
], function (
  Em,
  UI,

  AmAssignableItem,
  AssignableListBaseController
) {
  'use strict';

  return AssignableListBaseController.extend({

    helpUri: 1040,

    tHeader: 'amAssignableItem.assignableBookstoreBooksPage.title'.tr(),

    apiBase: '/api/books/',
    path: 'am_assignable_list.books',
    titleResource: 'amAssignableItem.assignableBookstoreBooksPage.title',

    userPrefsEndpointName: 'assignableBooksListColumns',

    dataStore: function () {
      return AmAssignableItem.get('assignedBooksStore');
    }.property(),

    visibleColumnNames: 'icon name shortDescription category'.w()
  });
});
