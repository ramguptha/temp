Coding Standard
===============

Here is our UI coding standard. Flagrant violations of it should fail code review.

General
-------

- text files should be in UTF-8, with UNIX style end-of-lines ("\n" i.e. 0xA, NOT "\r\n" or "\r")
- under\_score for all file names
- 'use strict'; when possible (i.e. when third party modules don't fail strict evaluation)
- 2 space indents. Use space ' ', not tab "\t"
- Prefer single quotes ('foo') over double quotes ("foo")

HTML / CSS
----------

- Class names can be semantic or functional.

      <div class="foo-container hidden">Bar</div>

- Class names should be hyphenated, and lower case.

      <div class="this-is-the-class-name-format"></div>

- Element ids should be CamelCase.

      <div id="ThisIsTheIdFormat"></div>
      
- All HTML needs to be structured to [support UI automation](./supporting-ui-automation.md).
- All HTML needs to conform to the [Style Guide](./style-guide/index.html).

Modules
-------

Modules are written for [require.js](http://requirejs.org). They are defined like this:

    define([
      'ember',
      'module_that_returns_class',
      'module_that_returns_instance'
    ], function(
      Em,
      ModuleThatReturnsClass, // Starts with capital letter
      moduleThatReturnsInstance // Starts with lower case letter
    ) {
      'use strict';

      return Em.View.extend();
    });

If a module has many dependencies, then group similar dependencies (e.g. views, datastores) together using empty lines to separate them:

    define([
      'ember',

      'module_group_one_this',
      'module_group_one_that',

      'module_group_two_this',
      'module_group_two_that'
    ], function(
      Em,

      ModuleGroupOneThis,
      ModuleGroupOneThat,

      ModuleGroupTwoThis,
      ModuleGroupTwoThat
    ) {
      'use strict';

      return Em.View.extend();
    });

Ember
-----

- Classes should declare sub classes and related constants first, then properties, then methods. Dependencies should be stored and used as properties of a class instead of being captured through lamnda, so that they can be easily mocked for unit testing.

      define([
        'bar',
        'baz'
      ], function(
        Bar,
        Baz
      ) {
        'use strict';

        var FooClass = Em.Object.extend({
          Bar: Bar,

          someProperty: 0,

          someOtherProperty: function() {
            return this.get('someProperty') + 1;
          }.property('someProperty'),

          createBar: function() {
            // Here, we scope Bar from _this_, which allows unit tests to inject their own implementation of
            // Bar easily.
            return this.Bar.create();
          },

          createBaz: function() {
            // Here, we capture Baz through lamnda, which prevents unit tests from being able to inject their own implementation. Don't do this.
            return Baz.create();
          }
        });
      });

- Actions responsible for a router state transition should be name gotoFoo. Actions responsible for showing a modal dialog should be called showFoo.

Blocks, Loops, Conditionals
---------------------------

- If a function or object is trivial (i.e. consists of a single statement), it's ok to write
  it in a single line.

      function foo() { return 'bar'; }

- K&R bracket style for all blocks:

      // open parenth on the same line - good
      function foo() {
        return 'bar';
      }

      // open parenth on the same line - good
      Em.Object.extend({
        foo: 'bar'
      });

      // NOT like this, with open parenth on a new line
      function foo() 
      {
        return 'bar';
      }

- Function invocations look like this:

      // No space between function name and '('
      // ', ' between function parameters
      foo.bar(0, 1, 2);

- Loops and branches look like this:

      // Space between key word and '('
      // ' ' after ';' and between operators / operands
      if (foo && true) {
      }

      switch (bar) {
      case 0:
        ...
        break;
      }

      while (baz || biz) {
      }

      for (var count = 0; count < thing.length; count++) {
      }     

Variables
---------

- CamelCase for all names in code. Variables, functions and properties should start with a lowercase letter. Classes and constants should start with an uppercase letter. Alternatively, constants can be written in ALL\_CAPS\_WITH\_UNDERSCORES.

      var someVariable = true;

      var SomeClass = Em.Object.extend();

      var SomeConstant = 1024;

      var SOME_OTHER_CONSTANT = 2048;

- When capturing 'this' for use in lambda scope, use 'self' for the variable name. This should
  be the first statement in the related block of code.

      var self = this;

- No global variables, please
- Variable names should be brief, but explanatory:

      var tryToAvoidVariableNamesLikeThis;
      var mm; // Probably not enough
      var justRight;

- If variable stores a value in some unit of measure, include that unit of measure in its name:

      var cookieDurationInSeconds;
      var heightInMeters;

- If a variable stores a boolean value, prefer for its meaning to be in the _positive_. Keep in mind that this will not always be possible because Handlebars has no concept of negation.

      // Prefer this
      var sessionDidExpire;

      // Over this
      var sessionHasNotExpired;

Comments
--------

When commenting, write them in a [literate style](http://en.wikipedia.org/wiki/Literate_programming). Comments may be formatted in [markdown](http://daringfireball.net/projects/markdown/syntax).

    // Code comments use double //, like this one. The comment should generally be above
    // the related code, and the nature of comments should be about the structure and 
    // general meaning of the code.
    var foo = 'bar';

    var bar = 'baz'; // Avoid comments like this, at the end of the line.

    /* And also avoid comments like this. Instead, reserve use of this style of comment
       for temporarily disabling blocks of code during debugging. */
    var baz = 'biz';

Code modules should start with a header that describes what the module is. The code module header should be the only top level header within the module.

Major sections of code may be delineated with a second level header.

Comment code sparingly. Most code should be self documenting. Remember that, like code, 
comments are an artifact with their own lifecycle cost - they must be maintained
just like everything else. And so, in turn, they should only exist with justification.

When it is necessary to write comments for code, focus on the "why" and the "how".

Comment data structures robustly, as there may be little code "close" to its 
declaration to indicate its proper usage.

Examples
--------

Here's a snippet of code with a fairly high comment density, on the upper end of what's appropriate:

    define([
      'packages/platform/regex'
    ], function(
      Regex
    ) {
      'use strict';

      // Cookie
      // ======
      //
      // A simple cookie management module.
      return {

        // Confirm that a key or value meets cookie formatting requirements
        isValid: function(str) {
          return ('string' === typeof(str)) && !str.match(/[ ;]/);
        },

        // Get the value corresponding to the provided cookie
        read: function(key) {
          if (!this.isValid(key)) {
            throw ['Invalid cookie key', key];
          }

          var match = document.cookie.match(new RegExp('[^;]\s*' + Regex.esc(key) + '=([^; ]*)'));

          return (null === match || undefined === match) ? undefined : match[1];
        },

        // Write a value for the given key. Returns what was written.
        //
        // Options: 
        //
        // - durationInSeconds: if specified and greater than zero, cookie will have that duration. Otherwise it will last for the browser session.
        // - path: used if specified, otherwise "/"
        write: function(key, value, options) {
          if (!this.isValid(key) || !this.isValid(value)) {
            throw ['Invalid key or value', key, value, options];
          }

          if (null === options || undefined === options) {
            options = {};
          }

          // Your basic cookie.
          var serializedPair = key + '=' + value;

          // Optional duration.
          var durationInSeconds = options.durationInSeconds;
          if (durationInSeconds > 0) {
            serializedPair = serializedPair + '; max-age=' + durationInSeconds;
          }

          // Optional path.
          var path = options.path;
          if (null === path || undefined === path) {
            path = '/';
          }
          serializedPair = serializedPair + '; path=' + path; 

          // If the protocol is secure, so should the cookie be.
          if ('https:' === location.protocol) {
            serializedPair = serializedPair + '; secure';
          }

          document.cookie = serializedPair;

          return value;
        },

        // Buh-bye. Note that to clear a cookie, the exact same path and domain must be used.
        clear: function(key, options) {
          if (!this.isValid(key)) {
            throw ['Invalid cookie key', key];
          }

          if (null === options || undefined === options) {
            options = {};
          }

          var serializedPair = key + '=; max-age=0';

          // Optional path.
          var path = options.path;
          if (null === path || undefined === path) {
            path = '/';
          }
          serializedPair = serializedPair + '; path=' + path; 

          document.cookie = serializedPair;

          return key;
        }
      };
    });

Here's a snippet of thoroughly commented code. This particular code module is relatively fragile and complex, so it warrants this level of detail.

    // populate virtual pages using raw pages. 
    // PRE: this.virtualPages, this.pageSize, and this.rawPages are initialized.
    // POST: this.virtualPages has sufficient pages in it to consume all of this.rawPages. Each virtualPage is exactly pageSize,
    //       except possibly the last one, which MAY be less than pageSize if ALL rawPages for the given query are loaded.
    updateVirtualPages: function() {
      logger.log('DESKTOP: PAGED_CONTROLLER: updateVirtualPages');

      // maximum size for virtual pages. All virtual pages will be exactly this size, except for the last
      // one in the whole resultset for the query.
      var pageSize = this.get('pageSize');

      // previously existing virtual pages
      var virtualPages = this.get('virtualPages');

      // the last virtual page, drawn from BOTH existing and new virtual pages
      var lastVirtualPage = virtualPages.objectAt(virtualPages.get('length') - 1);

      // get index of last rawPage referenced by virtual pages. 
      var rawPageStartIdx = 0;
      if (lastVirtualPage) {
        rawPageStartIdx = lastVirtualPage.get('lastFragment.page.pageNumber');
      }

      // step through rawPages, consuming them to produce virtual pages
      var rawPages = this.get('rawPages');
      var virtualPageInProgress = this.get('VirtualPage').create();
      var newVirtualPages = Em.A();
      for (var i = rawPageStartIdx; i < rawPages.get('length'); i++) {
        var rawPage = rawPages.objectAt(i);

        // the beginning and end of the unconsumed data in the rawPage
        var fragmentStartIdx = 0;
        var fragmentEndIdx = rawPage.get('filteredData.length') - 1;

        // if lastVirtualPage is set and it uses data from this rawPage, update fragmentStartIdx accordingly
        if (lastVirtualPage && lastVirtualPage.get('lastFragment.page') === rawPage) {
          fragmentStartIdx = lastVirtualPage.get('lastFragment.end') + 1;
        }

        // build virtualPages by consuming data from rawPage until it is all gone
        while (fragmentStartIdx <= fragmentEndIdx) {
          if (null === virtualPageInProgress) {
            virtualPageInProgress = this.get('VirtualPage').create();
          }

          // amount needed to complete virtualPageInProgress
          var maxFragmentLength = pageSize - virtualPageInProgress.get('length');

          virtualPageInProgress.get('fragments').pushObject(this.get('RawPageFragment').create({
            page: rawPage,
            start: fragmentStartIdx,
            end: Math.min(fragmentStartIdx + maxFragmentLength - 1, fragmentEndIdx)
          }));

          // remember what we've consumed
          fragmentStartIdx += virtualPageInProgress.get('lastFragment.length');

          if (virtualPageInProgress.get('length') === pageSize) {
            newVirtualPages.pushObject(virtualPageInProgress);
            lastVirtualPage = virtualPageInProgress;
            virtualPageInProgress = null;
          }
        }
      }

      // if we have processed every rawPage in the resultset for the query, include the last virtualized page
      // even if it isn't a full page
      if (virtualPageInProgress && !this.canRequestMoreRawPages()) {
        newVirtualPages.pushObject(virtualPageInProgress);
      }

      if (newVirtualPages.get('length') > 0) {
        virtualPages.pushObjects(newVirtualPages);
      }

      // after we have updated our virtual pages, grab some more raw ones if necessary
      Em.run.next(this, this.fulfillPageRequests);
    },

Here's a sample data structure, appropriately commented:

    define([
      'ember',
      'logger'
    ], function(
      Em,
      logger
    ) {
      'use strict';

      // AjaxErrorDetail
      // ===============
      //
      // Encapsulates a jQuery AJAX error. See the "error" function at http://api.jquery.com/jQuery.ajax/
      return Em.Object.extend({

        // The URL that was queried,
        url: null,

        // The endPoint that was queried.
        endPoint: function() {
          return this.get('url').replace(/\?.*/, '');
        }.property('url'),

        // The jQuery jqXHR object.
        jqXHR: null,

        // The HTTP status code.
        status: function() {
          return this.get('jqXHR').status;
        }.property().volatile(),

        // This is the "textStatus" parameter of the jQuery error callback.
        // It is set when the HTTP request was unable to complete at all. It will be one of:
        //
        // - timeout
        // - error
        // - abort
        // - parsererror
        //
        // See the is* methods for tests for each of the possible values.
        textStatus: null,

        isTimeout: function() {
          return this.get('textStatus') === 'timeout';
        }.property('textStatus'),

        isError: function() {
          return this.get('textStatus') === 'error';
        }.property('textStatus'),

        isAbort: function() {
          return this.get('textStatus') === 'abort';
        }.property('textStatus'),

        isParserError: function() {
          return this.get('textStatus') === 'parsererror';
        }.property('textStatus'),

        // This is the "errorThrown" parameter of the jQuery error callback. It is the textual portion
        // of the HTTP status code.
        errorThrown: null,

        // Deprecated: this the "errorThrown" parameter of the jQuery error callback.
        message: function() {
          logger.warn(['DEPRECATION WARNING: use errorThrown instead of message', this]);
          return this.get('errorThrown');
        }.property('errorThrown'),

        // The raw content returned by the endpoint.
        detail: function() {
          return this.get('jqXHR').responseText;
        }.property().volatile(),

        // If the detail property consists of JSON, this is the parsed data. Otherwise undefined.
        detailData: function() {
          try {
            return JSON.parse(this.get('detail'));
          } catch(e) {
            return undefined;
          }
        }.property('detail')
      });
    });
