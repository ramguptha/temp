define([
  'jquery'
], function(
  $
) {
  'use strict';

  return {
    package: function(name, modules) {
      return {
        name: name,
        run: function() {
          var name = this.name;
          $.each(modules, function(idx, module) {
            module.name = name + ':' + module.name;
            module.run();
          });
        }
      };
    },

    module: function(name, tests) {
      return {
        name: name,
        run: function() {
          QUnit.module(this.name);
          $.each(tests, function(idx, test) {
            if (test.async) {
              QUnit.asyncTest(test.name, function(assert) {
                Em.run(function() {
                  test.testBody(assert, function() { QUnit.start(); });
                });
              });
            } else {
              QUnit.test(test.name, function(assert) {
                Em.run(function() {
                  test.testBody(assert);
                });
              });
            }
          });
        }
      };
    },

    test: function(name, testBody) {
      return { name: name, async: false, testBody: testBody };
    },

    asyncTest: function(name, testBody) {
      return { name: name, async: true, testBody: testBody };
    }
  };
});
