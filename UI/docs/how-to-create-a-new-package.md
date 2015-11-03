How to Create a New Package
===========================

In the packages directory, setup the following structure:

    <package name>/
      main.js
      lib/
        controllers
        views
        templates
        specs
        models
        data_stores

Everything under the lib directory is optional. If there is a corresponding module to add, create the corresponding directory and put it in.

main.js is the package entry point. Other packages and your app may _only_ require the package, so define all of the exports there. In general, it will return an Ember object with properties set to everything exported by the package.

In the the require.js configuration section of the application main.js, modify the following parts:

    packages: [
      'packages/platform/logger',
      'packages/platform/guid',
      'packages/platform/uploader',

      ...

      'packages/<your package directory>'
    ]
