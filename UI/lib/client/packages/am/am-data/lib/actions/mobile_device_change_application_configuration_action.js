define([
  'ember',
  '../am_action'
], function(
  Em,
  AmAction
) {
  'use strict';

  return AmAction.extend({
    dependentDataStoreNames: [],

    description : 'Change Application Configuration for Mobile Devices',
    endPoint    : 'commands/changeapppconfig',
    deviceIds      : null,
    appname        : null,
    appconfig      : null,
    appdescription : null,


    toJSON: function() {
        var devs   = this.deviceIds.map(function (id) { return Number(id); });
        var appname = this.get('appname');
        var appconfig = this.get('appconfig');
        var appdescription = this.get('appdescription');

        return { deviceIds: devs, appname: appname, appconfig: appconfig, description: appdescription };
    }
  });
});
