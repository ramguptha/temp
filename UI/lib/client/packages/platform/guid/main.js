define([], function() {
  // Guid
  // ====

  var Guid = {

    // Generate a new pseudo random, probably (but not definitely) unique string.
    generate: function() {
      return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function(c) {
        var r = Math.random()*16|0, v = c == 'x' ? r : (r&0x3|0x8);
        return v.toString(16);
      });
    },

    // Test wether the provided value matches the format of a Guid.
    //
    // Via http://stackoverflow.com/a/13653180
    isValid: function(value) {
      return 'string' === typeof(value) &&
        !!value.match(/^[0-9a-f]{8}-[0-9a-f]{4}-[1-5][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}$/i);
    }
  };

  // Generate a property with no dependencies that will return a new Guid on get().
  Guid.property = function() {
    return function() { return Guid.generate(); }.property();
  };

  return Guid;
});
