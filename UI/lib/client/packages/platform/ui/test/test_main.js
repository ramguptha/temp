define([
  'ember',
  'testing',
  '../lib/application_base',
  '../lib/controllers/modal_container_controller'
], function(
  Em,
  Testing,
  ApplicationBase,
  ModalContainerController
) {
  'use strict';

  return Testing.package('ui', [
    Testing.module('application_base', [
      Testing.test('mergePackage / appStrings', function(assert) {
        var pkg = {
          appStrings: {
            one: '1',
            two: {
              three: '3'
            }
          }
        };

        var app = ApplicationBase.create();
        app.setupForTesting();
        app.mergePackage(pkg);

        var translations = app.get('translations');
        assert.equal(translations.one, pkg.appStrings.one, 'key in core namespace is merged');
        assert.deepEqual(translations.two, pkg.appStrings.two, 'sub-namespace is merged');

        assert.throws(function() { app.mergePackage(pkg); }, 'conflicting keys should throw');
      }),

      Testing.test('locale', function(assert) {
        var app = ApplicationBase.create();
        app.setupForTesting();

        var config = app.get('requireJsConfig');
        assert.strictEqual(typeof(config), 'object', 'require.js config is an object');

        var locale = app.get('locale');
        assert.ok(!Em.isEmpty(locale), 'locale is set');
        assert.strictEqual(typeof(locale), 'string', 'locale is a string');
      }),

      Testing.test('modal', function(assert) {
        var controller = ModalContainerController.create();
        var options = {
          name: 'test',
          controller: Em.Controller.create(),
          viewClass: Em.View
        };

        controller.showModal(options);

        assert.throws(
          function() { controller.showModal(options); },
          'Showing the same modal twice should throw'
        );
      })
    ])
  ]);
});

