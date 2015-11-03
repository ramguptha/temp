define([], function() {
  'use strict';

  return {
    /* this method is used to truncate long name, which took all or almost all allowed space for a textfield, so
     predefined string as ' - Copy' can be added at the end */
    annotate: function(str, annotation, max) {
      str = str.trim();
      var strSize = str.length;
      var annotationSize = annotation.length;
      var allowedSize = max - annotationSize;

      if (strSize > allowedSize) {
        str = str.substring(0, allowedSize);
      }
      return str + annotation;
    }
  };
});
