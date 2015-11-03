define([
  'ember',
  'am-data',
  'env',

  'packages/platform/data-table-component',
  'am-computer-formatter'
], function(
  Em,
  AmData,
  Env,

  DataTableComponent,
  AmComputerFormatter
) {
  'use strict';

  // AM Result set Value Components
  // ============================

  var baseValueComponent = DataTableComponent.ValueComponent.extend({
    apiBase: Em.computed.oneWay('presenter.apiBase'),

    value: function() {
      var node = this.get('node'),  presenter = this.get('presenter');

      return (!presenter || node.isDeferred) ? '' : new Handlebars.SafeString(this.get('formattedTemplate'));
    }.property('presenter', 'node', 'refreshedAt')
  });

  // This component is used to format the Third party applications icons
  var FormattedIconComponent = baseValueComponent.extend({
    formattedTemplate: function() {
      var id = this.get('nodeData.data.id');
      var template = '';

      if (id) {
        template = this.getIconTemplate(id);
      }

      return template;
    }.property('nodeData.data.id'),

    getIconTemplate: function(id) {
      var pathUrl = AmData.get('urlRoot') + this.get('apiBase') + id + '/icon';
      return '<img class="app-icon" src="' + pathUrl + '" />';
    }
  });

  // This component is used to format the look of OS Platform into icons
  var FormattedOsPlatformComponent = baseValueComponent.extend({
    formattedTemplate: function() {
      var osPlatform = this.get('nodeData.data.osPlatform'),
        template = '';

      if (osPlatform) {
        template = this.getAndroidAndiOsTemplate(osPlatform);
      }

      return template;
    }.property('nodeData.data.osPlatform'),

    getAndroidAndiOsTemplate: function(cellValue) {
      var osTemplate = cellValue;

      switch (osTemplate) {
        case 'iOS':
          osTemplate = '<img src="' + '/packages/platform/desktop/icons/os-logos/16-apple-ios-b.png' + '" />';
          break;
        case 'Android':
          osTemplate = '<img src="' + '/packages/platform/desktop/icons/os-logos/16-android.png' + '" />';
          break;
      }

      return osTemplate;
    }
  });

  // This component is used to format the look of OS Platform into icons + the version in string
  // This component is only used by Computer related grids
  var ComputerOsPlatformComponent = baseValueComponent.extend({
    formattedTemplate: function () {
      var osPlatform = this.get('nodeData.data.osPlatform'),
        template = '';

      if (osPlatform) {
        var valueArray = osPlatform.toString().split('|');
        var id = parseInt(valueArray[0]);
        var stringValue = valueArray[1];

        template = '<img src="' + AmComputerFormatter.getIconPathOsPlatform('16', id) + '"/> ' + stringValue;
      }

      return template;
    }.property('nodeData.data.osPlatform')
  });

  // Format command status as an icon for Computer Command History / Computer Queued Commands
  var ComputerCommandStatusIconComponent = baseValueComponent.extend({
    formattedTemplate: function () {
      var status = this.get('nodeData.data.status'),
        template = '';

      if (status) {
        var valueArray = status.toString().split('|');
        var statusId = parseInt(valueArray[0]);
        var statusStringValue = valueArray[1];

        template = '<span class="' + AmComputerFormatter.getIconClassCommandStatus(statusId) + " tooltip-s" + '" data-tooltip-attr="title" data-sticky-tooltip="true" title="' + statusStringValue + '"/>';
      }

      return template;
    }.property('nodeData.data.osPlatform')
  });


  // This component is used to format the look of content types into icons
  var FormattedTypeComponent = baseValueComponent.extend({
    formattedTemplate: function() {
      var type = this.get('nodeData.data.type'),
        template = '123';

      if (type) {
        template = this.getTypeTemplate(type);
      }

      return template;
    }.property('nodeData.data.type', 'node.isDeferred'),

    getTypeTemplate: function (cellValue) {
      var valueArray = cellValue.toString().split('[****]');
      var contentName = valueArray[0];
      var contentType = valueArray[1];

      return '<div title="' + contentType + '"><img src="' + this.getIconUrl(contentName, 16) + '" /></div>';
    },

    getIconUrl: function(filename, size) {
      var fileExtension = this.getFileExtension(filename);
      var endPoint = '/com.absolute.am.webapi/api/content/icons/';
      var iconUrl = endPoint + size + '-default.png';

      if(fileExtension) {
        if($.inArray(fileExtension.toLowerCase(),
            ['pdf', 'txt', 'xml', 'mov', 'svg', 'png', 'js', 'jpg', 'jpeg', 'html', 'htm', 'mp4', 'ai', 'm4v', 'mp4', 'wmv',
              'mp3', 'doc', 'docx', 'ppt', 'pptx', 'gif', 'bmp', 'tiff', 'xls', 'xlsx', 'avi', 'pages', 'numbers', 'webarchive', 'zip', 'video']) > -1) {
          iconUrl = endPoint + size + '-' + fileExtension + '.png';
        } else if ($.inArray(fileExtension,['mkv', 'm2v' ]) > -1) {
          iconUrl = endPoint + size + '-' + fileExtension + '.png' + '?category=Multimedia';

        }
      }

      return iconUrl;
    },

    getFileExtension: function(filename) {
      return filename.split('.').pop();
    }
  });

  // Two types of icons for yes and no
  var AgentAvailabilityIconFormatterComponent = baseValueComponent.extend({
    formattedTemplate: function() {
      var agent = this.get('nodeData.data.agentAvailability');

      if (!agent) { return ''; }

      if (agent === 'machine_available') {
        return '<span class="icon-status status-yes"></span>';
      } else {
        return '<span class="icon-status status-no"></span>';
      }

    }.property('nodeData.data.agentAvailability')
  });

  return {
    AmFormattedIconComponent: FormattedIconComponent,
    AmFormattedOsPlatformComponent: FormattedOsPlatformComponent,
    AmComputerFormattedOsPlatformComponent: ComputerOsPlatformComponent,
    AmComputerCommandStatusIconComponent: ComputerCommandStatusIconComponent,
    AmFormattedTypeComponent: FormattedTypeComponent,
    AmAgentAvailabilityIconFormatterComponent: AgentAvailabilityIconFormatterComponent
  }
});
