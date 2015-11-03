Require.js and Circular Dependencies
====================================

Introduction
------------

Require.js (requirejs.org) is used everywhere in our code. This document describes how we work with it.

Application Entry
-----------------

Every application has an initial entry point. This is the main application js file. It will be referenced in the html as:

    <script data-main="<application entry>" src="<path to require.js>"></script>.

Code Modules
------------

A javascript source file is called a module. In our case, it is expected to have the following format:

    define(
      [<module or package dependency list>],
      function(<module or package dependency aliases as function parameters, corresponding to the entries in the list>) {
        // When possible
        'use strict';

        return <the interface that is presented to consumers of the module>;
      }
    );

The code in a module is guaranteed to be evaluated exactly once, so it can be used to easily implement singletons.

Code Packages
-------------

Most of our code is organized into code packages, which are just directories with the following structure:

    <package name>/
      main.js
      lib/
        <everything else>

Packages must be declared in the configuration for require.js (see CONFIGURATION). When packages are listed in module dependencies, only the package name is used. Code modules within a package must not include that same package as a dependency, as it will most likely be circular and break everything.

Configuration
-------------

At the top level (in our application main.js) we configure require.js via require.config:

    require.config({ 
      baseUrl: '..',
      waitSeconds: 120,

      paths: {
        jquery: 'packages/platform/lib/jquery-1.7.2',
        jqueryui: 'packages/platform/lib/jquery-ui-1.10.3.custom',

        // ...

        text: 'packages/platform/lib/text'
      },

      packages: [
        'packages/platform/logger',

        // ...

        'packages/platform/enum-util'
      ],

      map: {
        '*': {
          logger: 'packages/platform/logger',

          // ...

          formats: 'packages/platform/formats'
        }
      },

      shim: {
        jquery: {
          exports: '$'
        },
        jqueryui: {
          deps: ['jquery'],
          exports: '$'
        },

        // ...

        timepicker: {
          deps: ['jqueryui']
        }
      }
    });

More on Application Entry
-------------------------

Unlike code modules, the top level application entry starts with a require() call, which follows the same calling convention as define():

    require([
      'cc-device-groups/core',
      'cc-device-groups/router',

      // ...

      'logger'
    ], function(
      App,
      Router,

      // ...

      logger
    ) {
      // APPLICATION ENTRY HERE
    });

Circular Dependencies
---------------------

When module A depends on some other chain of modules that eventually depend back on Module A (e.g. Module A => Module B => Module A), we have a circular dependency. In order to enable really bad software architecture, Require.js allows for circular dependencies. See the website for how to do this. For our particular usage pattern (require.js can be used in many ways), we will see module parameters to our "define()" calls be undefined if there is a circular dependency present.

In order to help diagnose module dependencies, Madge (https://github.com/pahen/node-madge) is included in the package.json. The binary is at: 

    node_modules/madge/bin/madge

Madge is capable of creating reports on the dependencies within an application, but it is crucual to understand its limitations, the most significant of which is that it doesn't read or understand the require.js configuration file. In turn, this means that it doesn't understand mappings or packages. Even given that, it is still a helpful tool. It can be invoked as follows:

    node_modules/madge/bin/madge -f amd -o json client/packages/<package name> # To examine the source files within a specific package

or

    node_modules/madge/bin/madge -f amd -o json client # To examine the source files across all applications in the repository

To diagnose circular dependencies, use the "-c" parameter. Again, remember that Madge doesn't know about packages, so if there is a circular dependency via a package (e.g. define(['desktop', ...], function(Desktop, ...) {}) from within the Desktop package), Madge will not detect it.

There are lots of ways to resolve a circular dependency. One common way is to extract commonality to a third module, and have both modules in the circular chain depend on that one instead of each other (Module A => Module B => Module A is resolved to Module A => Module C and Module B => Module C).

Troubleshooting
---------------

Problem: *My module dependency is set to undefined!*

Possible Reasons:

- There is a circular dependency
- The module path is wrong and the file was not found (look for a 404 in the browser log)
- There is a mismatch between the dependency list and the parameter list 

      define(['a', 'b', 'c'], function(A, C, B) { ... })

Problem: *My module dependencies are acting in completely bizarre ways!*

Possible Reason:
- There is a mismatch between the dependency list and the parameter list

      define(['a', 'b', 'c'], function(A, C, B) { ... })
  
