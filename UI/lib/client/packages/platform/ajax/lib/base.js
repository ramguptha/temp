define([], function() {
  'use strict';

  return {
    get: function(desc, url, vars, okHandler, errHandler, headers) {
      return this.invoke('GET', undefined, desc, url, vars, okHandler, errHandler, headers);
    },

    post: function(desc, url, contentType, vars, okHandler, errHandler, headers) {
      return this.invoke('POST', contentType, desc, url, vars, okHandler, errHandler, headers);
    },

    put: function(desc, url, contentType, vars, okHandler, errHandler, headers) {
      return this.invoke('PUT', contentType, desc, url, vars, okHandler, errHandler, headers);
    },

    del: function(desc, url, contentType, vars, okHandler, errHandler, headers) {
      return this.invoke('DELETE', contentType, desc, url, vars, okHandler, errHandler, headers);
    }
  };
});
