define([
  'ember'
], function(
  Em
) {
  'use strict';

  // Tracer
  // ======
  //
  // Stores tracing status. Outputs trace information to the console, when enabled.

  return {
    Root: Em.Object.extend({
      tracing: false,

      trace: function() {
        if (!this.get('tracing')) {
          return;
        }

        console.log.apply(console, arguments);
      },
    }),

    Child: Em.Object.extend({
      parent: null,
      name: null,
      colour: null,

      tracing: Em.computed.oneWay('parent.tracing'),

      trace: function() {
        var args = [this.get('name')];
        for (var i = 0; i < arguments.length; i++) {
          args.push(arguments[i]);
        }

        var parent = this.get('parent');
        parent.trace.apply(parent, args);
      }
    }),

    IsTraced: Em.Mixin.create({
      tracer: null,

      trace: function() {
        var tracer = this.get('tracer');
        if (tracer) {
          tracer.trace.apply(tracer, arguments);
        }
      }
    })
  };
});
