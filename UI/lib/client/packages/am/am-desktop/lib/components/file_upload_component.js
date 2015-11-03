define([
  'ember'

], function(
  Em

) {
  'use strict';

  // File upload component:
  // Original source:
  // https://github.com/selvagsz/ember-image-preview-component/blob/master/index.html
  return Em.TextField.extend({

    type: 'file',
    accept: 'image/x-png, image/png, image/jpeg',
    isVisible: false,
    isChanged: false,
    isError: false,

    imageSource: null,

    init: function() {
      this._super();

      this.setProperties({
        imageSource: null,
        isChanged: false,
        isError: false
      });
    },

    change: function(event) {
      var filesList = event.target.files;
      var self = this;

      if(filesList && filesList[0]) {
        var fileReader = new FileReader();
        var inputFormat = filesList[0].type;

        if(inputFormat === 'image/png' || inputFormat === 'image/jpg' || inputFormat === 'image/jpeg') {
          fileReader.readAsDataURL(filesList[0]);

          fileReader.onload = function(e) {
            self.setProperties({
              imageSource: e.target.result,
              isChanged: true,
              isError: false
            });
          }
        } else {
          self.setProperties({
            imageSource: null,
            isError: true,
            isChanged: true
          });
        }
      }
    }
  });
});
