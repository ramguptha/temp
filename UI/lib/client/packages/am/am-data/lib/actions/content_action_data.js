define([
  'ember',
  'packages/platform/data'
], function(
  Em,
  AbsData
) {
  'use strict';

  /*
   * {
   *  "id":376,  (not specified for new files)
   *  "seed":7,  (not specified for new files)
   *  "fileName":"MyMediaFile.pdf",
   *  "displayName":"My Media File",
   *  "description":"This is a fake media file.",
   *  "category":"Documents",
   *  "fileModDate":"2012-10-12T22:39:31Z",
   *  "fileType":"PDF",
   *  "canLeaveApp":true,
   *  "canEmail":false,
   *  "canPrint":false,
   *  "transferOnWifiOnly":true,
   *  "passphraseHash":"5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8"
   * }
   */
  return Em.Object.extend({
    id: null,
    seed: null,

    fileName: null,
    displayName: null,
    description: null,
    category: null,
    fileModDate: null,
    fileType: null,

    canLeaveApp: null,
    canEmail: null,
    canPrint: null,

    transferOnWifiOnly: null,

    passphrase: null,

    toJSON: function() {
      var json = this.getProperties('fileName displayName description category fileType passphrase'.w());

      'id seed'.w().forEach(function(name) {
        var value = this.get(name);
        if (value) {
          json[name] = Number(value);
        }
      }, this);

      var ActionData = AbsData.get('ActionData');
      ActionData.mergeDateAttrs(json, this, 'fileModDate'.w());
      ActionData.mergeBooleanAttrs(json, this, 'canLeaveApp canEmail canPrint transferOnWifiOnly'.w());

      return json;
    }
  });
});
