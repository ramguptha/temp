define([
  'ember',
  'ui',
  'help',
  'guid',
  'formatter',
  'am-desktop'
], function (
  Em,
  UI,
  Help,
  Guid,
  Formatter,
  AmDesktop
) {
  'use strict';

  return AmDesktop.ListControllerColumns.extend({

    name: function() {
      return this.get('tHeader').toString();
    }.property(),

    helpUri: null,
    urlForHelp: function() {
      var helpUri = this.get('helpUri');

      return Help.uri(helpUri);
    }.property('helpUri'),

    breadcrumb: function() {
      var path = this.get('path');
      var titleResource = this.get('titleResource');

      return UI.Breadcrumb.create({
        path: path,
        titleResource: titleResource
      });
    }.property()
  });
});
