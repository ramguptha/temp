define([
  'printstacktrace'
], function(
  printStackTrace
) {
  var logger = {
    logDebug: false,
    logWarn: true,
    logError: true,

    lastLog: new Date(),

    padZero: function(num) {
      var numAsStr = num.toString(10);
      return numAsStr.length === 1 ? '0' + numAsStr : numAsStr;
    },

    timestamp: function(now) {
      return [now.getYear() + 1900, this.padZero(now.getMonth() + 1), this.padZero(now.getDate())].join('-') + ' ' +
        [this.padZero(now.getHours()), this.padZero(now.getMinutes()), this.padZero(now.getSeconds())].join(':') + '.' +
        now.getMilliseconds();
    },

    log: function() {
      if (this.logDebug) {
        if ('undefined' !== typeof(console)) {
          this.format(console, console.log, arguments);
        }
      }
    },

    warn: function() {
      if (this.logWarn) {
        if ('undefined' !== typeof(console)) {
          this.format(console, console.warn, arguments);
        }
      }
    },

    error: function() {
      if (this.logError) {
        if ('undefined' !== typeof(console)) {
          this.format(console, console.error, arguments);
        }
      }
    },

    format: function(context, logger, args) {
      // Do nothing in perverse environments that include no logger
      if (!logger) return;

      // Try to group related log entries together by printing a blank line if it has been
      // more than a second since the last entry.
      var now = new Date();
      if (this.lastLog && now.getTime() - this.lastLog.getTime() > 1000) console.log('');
      var timestamp = this.timestamp(now);

      if (false) {
        var stack = printStackTrace();

        // Carve off the top, useless bits of the call stack, leaving only delicious meat.
        for (var i = 0; i < 6; i++) stack.shift();

        // Work around IE related stupidity ("i.e." that methods on window are not standard function objects with
        // support for apply.
        //
        // The sane version: logger.apply(context, $.merge([timestamp, { stack: stack }], args));

        // Disabled for now due to loggers that truncate log strings instead of showing objects inline
        Function.prototype.apply.call(logger, context, $.merge([timestamp, { stack: stack }], args));
      } else {
        Function.prototype.apply.call(logger, context, $.merge([timestamp], args));
      }
      this.lastLog = now;
    }
  };

  return {
    Logger: logger,

    log: function() { return logger.log.apply(logger, arguments); },
    warn: function() { return logger.warn.apply(logger, arguments); },
    error: function() { return logger.error.apply(logger, arguments); }
  };
});
