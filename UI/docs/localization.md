Localization
============

This document describes the requirements, design, and task breakdown for adding base localization support to the UI layer.

As an example for how another architectural component does localization, see the localized property files for AM Web UI at $/Projects/AMWebAdmin/development/api/com.absolute.am.webapi/resources/webapi.

Contents
--------

- [Actors](#actors)
- [Requirements](#reqs)
- Design
  - [Considerations](#design.considerations)
  - [Workflows](#design.workflows)
  - [Structure of Resource Files](#design.structure-of-resource-files)
    - [Naming Resource Keys](#design.structure-of-resource-files.naming)
    - [Standard Types for Resource Key Names](#design.structure-of-resource-files.types)
    - [High Level Structure - AbsLocale](#design.structure-of-resource-files.top-level.abs-locale)
    - [High Level Structure - Other](#design.structure-of-resource-files.top-level.other)
    - [Writing Localized Strings](#design.structure-of-resource-files.writing-strings)
  - [File Layout](#design.file-layout)
  - [Applications](#design.applications)
  - [The Localization API](#design.api)
  - [Updated Views](#design.updated-views)
  - [Mapping Between String Resource Keys and JSON Property File Keys](#design.name-mapping)
  - [exportLocaleStrings.js - The JSON Properties File Export Script](#design.export-locale-strings)
  - [importLocaleStrings.js - The JSON Properties File Import Script](#design.import-locale-strings)
- Developer Workflows
  - [Adding Localization Support to an Application Package](#workflow.add-localization)
- [Localization Examples](#examples)
- [Tasks](#tasks)

<a name="actors"></a>

Actors
------

- UI Developers: Responsible for UI code.
- Middle Tier Developers: Responsible for session management (including, sometimes, determination of locale), and endpoints.
- The Documentation Team (Technical Writers): Responsible for naming terms and copywriting.
- The Localization Group (Translators): Responsible for the act of translation.
- The End User: Uses it!

<a name="reqs"></a>

Requirements
------------

### R-GEN: General

- R-GEN-1: A "localization in progress" locale to assist translators.
- R-GEN-2: Allow for "fuzzy" locale matches. e.g. it should be possible to fall back to en from en-gb.

### R-INT: Specification of Locale (Integration with Existing Locale Management UX and Code)

The front-end localization system needs to integrate with existing back-end locale support. Absolute Manage Web Admin stores the locale in the session object. Customer Center stores the locale in a cookie. Both allow the locale to be specified on login, with the default locale determined from the browser configuration. Absolute Manage uses the UI layer to manage the login flow, including specification of locale. Customer Center does not.

The UX and capabilities for _specification_ and _storage_ of the current locale are out of scope for this document - that is a matter for the software being integrated with. From the perspective of the UI layer, all that matters is that it can integrate.

- R-INT-1: Integrate with Customer Center's current implementation of locale specification / storage.
- R-INT-2: Integrate with Single Sign-on's current implementation of locale specification / storage.
- R-INT-3: Integrate with Absolute Manage Web Admin's current implementation of locale specification / storage.

### R-FLW: Work Flow & Process

- R-FLW-1: UTF-8 shall be the working encoding for localization.
- R-FLW-2: The localization file structure and tooling must be able to accomodate the localization groups existing tools and workflows.
- R-FLW-3: The resource file format will be simple enough to allow for the documentation group to update resource strings without assistance from developers.

### R-FMT: String Formatting

Localized strings will be able to support basic formatting:

- R-FMT-1: strong
- R-FMT-2: emphasis
- R-FMT-3: superscript
- R-FMT-4: subscript
- R-FMT-5: paragraph
- R-FMT-6: anchor

### R-TYPE: Types

Locale specific parsing / formatting will be supported for the following types:

- R-TYPE-1: numbers 
- R-TYPE-2: dates
- R-TYPE-3: booleans
- R-TYPE-4: nulls
- R-TYPE-5: currency

### R-TOOL: Tools

- R-TOOL-1: Accept JSON files that serialize a single object as key => value pairs (e.g. { "foo": "bar", "fizz": "buzz" }). For clarity, we'll refer to such files as JSON Property Files.
- R-TOOL-2: Support localization on a per-library basis.
- R-TOOL-3: It will be possible to validate the structure (as relates to formatting) of localized files via tooling.

### R-API: API

- R-API-1: It will be simple to safely render formatted content on the page (i.e. to protect against script injection).

### NR: Not Requirements

The following items are **NOT** requirements at this time:

- NR-1: It will not be necessary to localize images. This would come down to theming, which will be orthogonal to localization.
- NR-2: It will not be necessary to localize icon fonts. We will add any localized glyphs necessary to the core font.
- NR-3: It will not be necessary to localize stylesheets. This would come down to theming, which will be orthogonal to localization.
- NR-4: It will not be necessary to change locale without reloading the page.
- NR-5: Timezone-related matters are out of scope.
- NR-6: **Endpoint data, and endpoint-driven UI is out of scope.** While obviously necessary, this work will be done as part of later iterations. Note in particular that this means that the following kinds of information will remain unlocalized at the end of this project:
  - Error messages
  - Data-driven Report Headers
  - Customer Center Policy Group Rules

<a name="design.considerations"></a>

Design - Considerations 
-----------------------

### Core Ember Localization Support

Ember has built-in, simple localization support. Code may invoke String.loc(key, paramsAsArray) to lookup _key_ in Ember.STRINGS, and in turn return _value_.fmt(paramsAsArray). That's it, that's all.

The primary issue with this approach is that it involves using another unified global namespace (Ember.STRINGS), like our Application instance. So we would need to jump through the same prefixing hoops as we do with Application class names, like 'CC\_DEVICES\_HARD\_DRIVE\_FREE\_SPACE'.

We will avoid Core Ember Localization Support in favor of Ember.I18n.

### The Ember.I18n Library

[Ember.I18n](https://github.com/jamesarosen/ember-i18n) ([LICENSE](https://github.com/jamesarosen/ember-i18n/blob/master/LICENSE)) adds robust localization support to Ember, including:

- Namespaced translation tables, which contain compiled Handlebars functions.
- A convenience helper for accessing the translation table directly:

      {{t "foo.bar" tagName="h2"}}

- A convenience helper for translating HTML attributes:

      <a {{translateAttr title="foo.bar"}}>...

We will emulate the behaviour of Ember.I18n with our own implementation, in order to support some additional features.

### <a name="considerations.requirejs"></a>Require.js Localization Support

Require.js has a localization plugin, fully documented [here](http://requirejs.org/docs/api.html#i18n). It supports download of resources specific to the current locale, and fallback from a specific locale to a more general one (e.g. fr-fr would fall back to fr).

Localized modules are arranged as follows:

    nls/
      <module name>.js
      root/<module name>.js
      en-us/<module name>.js

Localized modules are then required as follows:

    define(['i18n!nls/<module name>'], function(...)

Finally, the top level localized module is structured as follows:

    define({
      root: {
        key1: 'value 1',
        key2: 'value 2',
        ...
      },

      'locale1': true,
      'locale2': true,
      ...
    });

Locale is derived from the browser configuration, with the option to override it via require.config:

    requirejs.config({
      config: {
        // Set the config for the i18n module ID.
        i18n: {
          locale: 'fr-fr'
        }
      }
    });

To access the locale from the require.config later (in an [unsupported manner](https://groups.google.com/forum/#!topic/requirejs/Hf-qNmM0ceI)), use:

    window.requirejs.s.contexts._.config

### Determination of Locale in Customer Center

In Customer Center, the locale is set on login. It is available to client code via the cookie CC\_CULTURE\_COOKIE\_KEY. This cookie is set by all login flows, built-in and single sign-on.

### Determination of Locale in Absolute Manage Web Admin

In Absolute Manage Web Admin, the locale is returned with the rest of the session when /api/login is queried via GET (see resultParameters.Locale).

### Future Expectations from the Middle Tier

As indicated in NR-6, localization of endpoint-driven UI is explicitely out of scope for this project. However, the current state of the roadmap for future work is documented here.

#### Customer Center Reports

Currently, a small number of Customer Center Report configurations are driven from metadata downloaded from endpoints (BaseEntities). Currently, the report headings are generated from the corresponding property names via algorithm (e.g. "Always call ESN Identifier, apply a transform to turn this\_kind\_of\_name into This Kind Of Name, etc.).

In a future iteration, BaseEntities will include localized report headings instead.

#### Error Messages

Absolute Manage endpoints already deliver errors in format that is friendly to localization: with an identifiable code and a dictionary of related values which can be inserted into localized strings. In a future iteration, it may be prudent to do the same with Customer Center endpoints.

#### Locale Dependent Data

It will be the responsibility of the related endpoint to tailor locale-dependent data as required.

<a name="design.workflows"></a>

Design - Workflows
------------------

### Responsibilities

**UI Developers** are responsible for creating resource files, for managing their structure, and for writing the original copy for any new resource keys.

**The Documentation Group** is responsible for maintaining the copy within resource files.

**The Localization Group** is responsible for maintaining the corresponding translations for all resources. The Localization Group does not interact with resource files directly. Instead, resource files are transformed to and from an intermediate format that is compatible with the Localization Groups tools. 

### Development Group Workflow - Creation and Maintenance of Resource Keys

1. Developers create a strings.js resource file as part of a new UI package. Like other development artifacts, strings.js files are checked into source control.
2. Over the course of development, developers add and remove resource keys and initial copy as required for any Handlebars templates.
3. New resource keys are provided to the documentation group for review and editing, along with instructions for how to view them in the UI.

### Documentation Group Workflow - Maintainance of Resource Copy

1. Technical Writers are expected to know the UI source code well enough to:
  - Find the strings.js resource file that corresponds to a given UI package (probably less than ten UI packages for the near future).
  - Know where the shared strings.js resource files (such as that in AbsLocale) are located.
  - Understand the naming conventions and resource structure well enough to find a resource key that corresponds to an element in the UI.
  - When necessary, to be able to escape special characters within a javascript string (i.e. to back-slash single quotes).
2. The Documentation Group is notified of new UI content, along with corresponding resource keys as they are created by Developers.
2. Technical Writers review new parts of the UI, updating the corresponding resources and checking them into source control as necessary.

### Localization Group Workflow - Maintenance of Translations

1. On request, Developers use exportLocaleStrings.js to collect all of the strings.js resource files associated with an application into a single JSON file, and deliver it to the Localization Group.
2. The Localization Group translates the strings for the requested locales, and delivers translated JSON files (one per locale) back to the Development Group.
3. Developers use importLocaleStrings.js to transform each localized JSON file into corresponding localized strings.js files. These files are checked into source control.

<a name="design.structure-of-resource-files"></a>

Design - Structure of Resource Files 
------------------------------------

Resource files are standard require.js modules that return a javascript object, as per the require.js i18n plugin. Said object has a _root_ key, which is the top level namespace of the object, and a list of "supported locale" keys, one per locale. See the example with [Require.js Localization Support](#considerations.requirejs):

    define({
      root: {
        key1: 'value 1',
        key2: 'value 2',
        ...
      },

      'locale1': true,
      'locale2': true,
      ...
    });

A resource key may refer to a _localized string_, to a _sub-namespace_, or to a _reference_ to another resource.

    define({

      // This is a simplified sample for demonstration purposes. Real resources have deeper hierarchies.
      root: {
        
        // A localized string
        title: 'Loren Ipsum',

        // A sub-namespace
        buttons: {
          ok: 'Ok',
          cancel: 'Cancel'
        },

        // A reference (means "same as the value with path buttons.ok")
        contrivedExample: { ref: 'buttons.ok' }
      },

      // Supported locales
      'fr-fr': true,
      'en-gb': true
    });

A localized string is a standard javascript string in the target language.

A sub-namespace is used to keep the resource file organized. It's another javascript object full of resource keys. The _path_ to a resource key is obtained by starting at the root namespace (which is not part of the path), then concatenating the names of the sub-namespaces along with the final key for the localized string together with ".".

Given:

    root: {
      one: {
        two: {
          three: 'A localized string'
        }
      }
    }

The path of the key is:

    one.two.three

A reference is used to avoid unnecessary repetition. It is a javascript object with one key, _ref_, that has the path to the key it is referencing:

    root: {
      one: {
        two: {
          three: 'A localized string'
        }
      },

      refToThree: { ref: 'one.two.three' }
    }

<a name="design.structure-of-resource-files.naming"></a>

### Naming Resource Keys

As per the coding standard, resource keys are formatted in camel-case: 

    root: {
      someCamelCaseName: 'Some localized string'
    }

Resource paths include a portion that names the _type_ of thing the key is referring to.

The final part of the path may include the type:

    root: {
      okButton: 'OK',
      cancelButton: 'Cancel'
    }

Alternatively, several keys of the same type may be grouped together in a sub-namespace that includes the name of the type:

    root: {
      footerButtons: {
        ok: 'OK',
        cancel: 'Cancel'
      }
    }

Or just:

    root: {
      buttons: {
        ok: 'OK',
        cancel: 'Cancel'
      }
    }

<a name="design.structure-of-resource-files.types"></a>

### Standard Types for Resource Key Names

#### Page

A Page is always a sub-namespace corresponding to a page of content in the UI. Page namespaces may be further divided into regions:

    fooPage: {

      // Strings for elements within the header of the page

      header: {
      },

      // Strings for elements within the main body of the page

      body: {
      },

      // Strings for elements within the footer of the page

      footer: {
      }
    }

Valid page regions include:

- nav 
- header
- tab
- body
- footer

#### Modal

A Modal is a sub-namespace corresponding to a modal dialog in the UI. In the case of wizards, modal namespaces may be further divided into steps.

    barModal: {

      propertiesStep: {
      },

      confirmationStep: {
      },

      statusStep: {
      }
    }

#### Menu

A menu has a label and a list of options.

- menu
  - option
  - label

For example:

    editMenu: {
      label: 'Edit',
      options: {
        editProperties: 'Properties',
        delete: 'Delete
      }
    }

#### Search Box

A search box has a place-holder which is shown when no value has been entered, and an optional menu for choosing which property to filter-by.

- search
  - placeHolder
  - option

For example:

    search: {
      placeHolder: 'Search is case-sensitive',
      allOption: 'All',
      options: {
        name: 'Name',
        description: 'Description'
      }
    }

#### Button

A button can have a static label, or its label might change depending on wether it is toggled on or off:

- button
  - activate
  - deactivate

For example:

  okButton: 'Ok',
  filterButton: {
    activate: 'Show Filter',
    deactivate: 'Hide Filter'
  }

#### Messages

Messages should be annotated with the kind of information they are meant to convey:

- error
- warning
- information
- confirmation

#### Other Types

- title
- subtitle
- label
- tooltip
- instructions

<a name="design.structure-of-resource-files.top-level.abs-locale"></a>

### High Level Structure - AbsLocale

AbsLocale is special because it will contain commonly used resources for other resources to refer to. It will have the following top level structure:

    define({
      root: {

        // Items used throughout the resource are placed here.

        shared: {
        },
      }
    });

<a name="design.structure-of-resource-files.top-level.other"></a>

### High Level Structure - All Other Packages

With the exception of the global resource file in AbsLocale, all resource files will have the following top level structure:

    define({
      root: {

        // <namespace>
        // ===========
        //
        // All resource files will define their own unique namespace to avoid conflicts with each other.
        //
        // IF USING THIS AS A TEMPLATE, REMOVE UNUSED NAMESPACES.

        <namespace>: {

          // Items used throughout the resource are placed here.

          shared: {
          },

          // Strings related to the _data_ in the package. Property names and so-forth.

          models: {
          },

          // Strings related to a particular page in the package.

          pages: {
          },

          // Strings related to a particular modal in the package.

          modals: {
          }
        }
      }
    });

The "models" resource namespace is worth extra explanation. While most resources in a strings.js correspond directly to UI elements, descriptions of the properties of application data are a cross-cutting concern. To avoid repetition, such resource keys are grouped together in the models namespace.

<a name="design.structure-of-resource-files.writing-strings"></a>

### Writing Localized Strings

#### Resource Strings Are Presentation, Not Data

Do not use the contents of string resources to drive client behaviour.

#### Formatting

Developers are _strongly_ encouraged to leave formatting out of resource strings, but that isn't always possible. When necessary, the following HTML tags may be used:

- strong
- em
- sup
- sub
- p
- a

Don't put _class_ or _id_ attributes in localized HTML strings, use descendent selectors for formatting instead.

HTML in localized strings must be well-formed. In particular, don't open an HTML tag in one string, and close it in another.

    root: {
      valid: 'lorem <strong>ipsum</strong>',

      invalidStart: 'lorem <strong>ipsum',
      invalidEnd: 'dolor</strong> sit amet'
    }

#### Substitutions

Developers are discouraged from including substitutions in localized strings. When substitutions are necessary, they may be implemented as follows, **BUT FIRST, SEE THE RULES AT THE END OF THIS SECTION**.

As per Ember-i18n, it is legal to put Handlebars in localized strings. The values to substitute are included as parameters to the _t_ helper:

    {{t "modals.someDialog.confirmation" count=thingCount}}

And a corresponding localized string:

    root: {
      modals: {
        someDialog: {
          confirmation: 'Are you sure? {{count}} things will be changed.'
        }
      }
    }

The following rules apply with including substitions in resource strings:

- Escaped values only ("{{foo}}").
- No other Handlebars may be used.
- Ensure that the substituted name may be easily understood without the context given by the surrounding UI. In other words, given the localized string, by itself, it must be clear what the meaning of the substituted value is.
- Only use numbers, dates and names of objects with substitutions. If substitutions for other kinds of values are required, work with the localization group to ensure that the strings are localizable. See slides 8 onwards of [this presentation](http://scm/teams/documentation/localization/_layouts/PowerPoint.aspx?PowerPointView=ReadingView&PresentationId=/teams/documentation/localization/Internal%20Processes/LocalizationPresentationMarch2012_final.pptx&Source=http%3A%2F%2Fscm%2Fteams%2Fdocumentation%2Flocalization%2FInternal%2520Processes%2FForms%2FAllItems%2Easpx&DefaultItemOpen=1) for some of the subtleties involved.

#### Structure

If possible, UI elements should map one-to-one with localized strings. If necessary, it is acceptable to use formatting and substitutions to accomplish this.

In other words, _over all else_, prefer this style of Handlebars:

    {{!-- GOOD --}}

    {{t "modals.someDialog.confirmation" count=thingCount}}

And this style of resource:

    // GOOD

    root: {
      modals: {
        someDialog: {
          confirmation: 'Are you sure? {{count}} things will be changed.'
        }
      }
    }

Over this style of Handlebars:

    {{!-- BAD --}}

    {{t "modals.someDialog.confirmationStart"}} {{thingCount}} {{t "modals.someDialog.confirmationEnd"}}

With this style of resource:

    // BAD

    root: {
      modals: {
        someDialog: {
          confirmationStart: 'Are you sure?',
          confirmationEnd: 'things will be changed.'
        }
      }
    }

<a name="design.file-layout"></a>

Design - File Layout
--------------------

String files, for use by client-side code:

    lib/client/packages/<package root>/nls/
      strings.js
      <locale>/strings.js

    sites/<site>/client/<app root>/nls/
      strings.js
      <locale>/strings.js

Workflow scripts:

    scripts/
      importLocaleStrings.js
      exportLocaleStrings.js

Workflow script libraries:

    lib/server/
      localization_support.js

Working files for translation:

    translations/
      UI.properties
      UI_<locale>.properties

<a name="design.applications"></a>

Design - Applications
---------------------

The require.js i18n plugin will need to know the locale before it is used to declare any dependencies. In turn, applications will be modified to obtain the locale (by whatever means) before configuring require.js, and set it in the config.

### General Changes

In order to make jQuery available for Ajax invocations before require.js is configured, it will be changed to a synchronously loading script tag declared before require.js. All application code will be changed to remove the jQuery dependency, and just use Em.$ (for Ajax and other utility stuff) or this.$ (for views).

### Customer Center

Query the CC\_CULTURE\_COOKIE\_KEY and set the require.js config directly.

### Absolute Manage Web Admin

Query the session via Ajax before configuring require.js.

<a name="design.api"></a>

Design - The Localization API
-----------------------------

### String Formatting

String formatting will be via Em.I18n.t().

### Application Package Structure

Application packages will have a new key: appStrings, which is merged into App.translations on Application.mergePackage(), with the usual checks for name collisions.

The expectation is that most package mains implement appStrings as follows:

    define([
      ...
      'i18n!./nls/strings'
    ], function(
      ...
      strings
    ) {
      'use strict';

      return {
        ...

        appStrings: strings
      };
    });

### The UI Package

ApplicationBase will be modified to as follows:

#### translations

The global namespace that holds the resources.

#### locale

The name of the current locale. A computed property that returns window.requirejs.s.contexts.\_.config.

#### mergePackage(package)

In addition to its current behaviour, mergePackage() will check for package.appStrings, and if it is set, add appStrings to the translations object.

### The AbsDate Package

Already exists as Date. Rename. Update isValid(date) to test for the following criteria:

- date instanceof Date
- 0 !== date.valueOf()
- !isNaN(date.valueOf())

### The AbsNumber package

New, similar to the AbsDate package. Has one method, isValid(number), which tests for the following criteria:

- 'number' === typeof(number)
- !isNaN(number)

### The AbsLocale Package

The AbsLocale package will provide services related to parsing and formatting typed data. It will mostly be a facade for [JSWorld](http://software.dzhuvinov.com/jsworld.html) ([LICENSE](http://software.dzhuvinov.com/download/jsworld/LICENSE.txt)).

The AbsLocale package will maintain a JSWorld Locale instance that is synchronized with App.locale.

The AbsLocale package returns a javascript object when required, with the following properties:

#### appStrings

- Globally useful strings, to be merged into the App via mergePackage().
- Strings of note:
  - none: used for null or undefined values.
  - true: used for true boolean values.
  - false: used for false boolean values.
- Other globally shared strings will be added as the need arises.

#### initialize()

The standard package initialization function. Will register the translateHelper and the translateAttrHelper functions with Handlebars. Will add translateProperty to the prototype of the String class, with name "tr".

#### translateHelper(value, options)

A Handlebars helper that returns a translated value for the given key. This helper will be invoked via "t".

    <button>{{t "shared.buttons.ok"}}</button>

Substitution with constants is supported:

    {{t "modals.someDialog.confirmation" count=100}}

Substitution with properties is also supported:

    {{t "modals.someDialog.confirmation" countBinding=thingCount}}

If the context has a _strings_ property set to a property path, translateHelper will take that path as a "base" to start resource resolution from.

If the App is localization mode, then the full property path (including any _strings_ prefix) will be rendered instead.

#### translateAttrHelper(value, options)

Like translateHelper, except that it works on attributes. This helper will be invoked via "tr". It will most commonly be used for tooltips.

    <button {{tr title="modals.someDialog.tooltip"}}>Ok</button>

Substitutions are not supported with this helper. If that is required, use a bind-attr with a translated property.

#### translatedProperty(path, deps)

Returns a new translated property based on the provided path. Also available via the String prototype:

    Em.Object.extend({
      okLabel: 'shared.buttons.ok'.tr()
    });

Substitutions are supported. Include the required property names as parameters to tr():

    Em.Object.extend({
      confirmation: 'modals.someDialog.confirmation'.tr('thingCount')
    });

#### registerHelpers()

Registers translateHelper() and translateAttrHelper() as Handlebars helpers.

#### boolean(boolean)

Format a boolean (there is no corresponding parse method):

    If 'boolean' === typeof(boolean), returns appStrings.true or appStrings.FALSE as appropriate
    Else if Em.isNone(boolean) returns null
    Throws otherwise

#### currency(number)

Formats a currency according to the current locale, including placement of separators like spaces, periods and commas, and denomination, with the same logic as number(number). There is no corresponding parse method.

#### number(number)

Formats a number:

    If AbsNumber.isValid(number), returns the formatted number according to the locale
    Else if Em.isNone(number) returns appStrings.none
    Throws otherwise

#### parseNumber(string)

Parses a number:

    If AbsNumber.isValid(string), returns string (it is really a valid number)
    If 'string' === typeof(string),
      Sets parsed to the parsed number according to the locale
      If AbsNumber.isValid(parsed), returns parsed
      Else returns null
    If Em.isNone(string), returns null
    Throws otherwise

#### date(date)

Formats a date:

    If AbsDate.isValid(date), returns date formatted for the locale
    Else if Em.isNone(date) or date instanceof Date, returns null
    Throws otherwise

#### parseDate(string)

Parses a date:

    If AbsDate.isValid(string), returns string (it is really a valid date)
    Else if 'string' === typeof(string)
      Sets parsed to the parsed date according to the locale
      If AbsDate.isValid(parsed), returns parsed
      Else returns null
    If Em.isNone(string), returns null
    Throws otherwise

#### dayName(weekDayNumberFromSunday)

Returns the string for the name of the weekday, Sunday is 0.

#### dayAbbrName(weekDayNumberFromSunday)

Returns the string for the abbreviated name of the weekday, Sunday is 0.

#### monthName(monthNumber)

Returns the string for the name of the month, January is 0.

#### monthAbbrName(monthNumber)

Returns the string for the abbreviated name of the month, January is 0.

### CcAppFoundation, AmAppFoundation

CcAppFoundation and AmAppFoundation will be modified to contain resource strings that are globally useful in the respective applications. This will be done via the standard appStrings property.

<a name="design.updated-views"></a>

Design - Updated Views
----------------------

The following views will be updated:

### Desktop.NumberFieldView

Update to use AbsNumber.isValid(), AbsLocale.number() and AbsLocale.parseNumber().

### Desktop.DateFieldUtilMixin

Update to use AbsLocale.date() and AbsLocale.parseDate().

### CcDesktop.CcWeekDaysView

Update to use AbsLocale.dayAbbrName().

### Not Updated

The following views depend on jQuery UI's Date/Time picker. We will replace that component with an internally developed one once our menu system has been refactored.

- Desktop.BetweenDateFieldView
- Desktop.DateFieldView
- Desktop.DatePickerView
- Desktop.DateTimePickerView
- Desktop.TimePickerView

<a name="design.name-mapping"></a>

Design - Mapping Between String Resource Keys and JSON Property File Keys
-------------------------------------------------------------------------

Given a string resource in the following format:

       define({
         root: {
           ccDevice: {
             deviceItemSummaryHardware: {
               userInformation: 'User Information',
               username: 'Username',
               assignedUsername: 'Assigned Username'
             }
           }
         }
       });

The corresponding JSON Property file would be:

      {
        "ccDevice.deviceItemSummaryHardware.userInformation": "User Information",
        "ccDevice.deviceItemSummaryHardware.username": "Username",
        "ccDevice.deviceItemSummaryHardware.assignedUsername": "Assigned Username"
      }

<a name="design.export-locale-strings"></a>

Design - exportLocaleStrings.js - The JSON Properties File Export Script
------------------------------------------------------------------------

exportLocaleStrings will be responsible for creating a merged JSON Properties file from all of the separate package and app root strings files. This merged JSON Properties file will be delivered to the localization team for translation.

It will reside in the scripts directory.

It will be a node.js script that accepts the following commandline parameters:

- --help: Show a help message and exit
- --string-search-dirs: Comma separated list of paths to search for 'nls/strings.js'
- --string-exclude-dirs: Comma separated list of root paths to ignore when searching for string files
- --out: Write JSON properties file to this path.

Typical invocation:

    node scripts/exportLocaleStrings.js --string-search-dirs lib/client/packages,sites \
         --string-exclude-dirs lib/client/packages/platform/lib --out translations/UI.properties

### Flow of Execution

When invoked, exportLocaleStrings will recurse through all of the paths specified in --string-search-dirs, looking for files that match 'nls/strings.js' (skipping those with parent directories that match --string-exclude-dirs). It will read these files, merging them into a single javascript object, which will then be serialized into a JSON Properties file for output (if --out was specified).

If exportLocaleStrings sees the same string key in multiple input files, it will print the key and the related files along with a warning.

If exportLocaleStrings sees a string that contains malformed HTML, it will print the key and the related file along with a warning.

<a name="design.import-locale-strings"></a>

Design - importLocaleStrings.js - The JSON Properties File Import Script
--------------------------------------------------------------------

importLocaleStrings will reside in the scripts directory. It will be a node.js script that accepts the following commandline parameters:

- --help: Show a help message and exit
- --string-search-dirs: Comma separated list of paths to search for 'nls/strings.js'
- --string-exclude-dirs: Comma separated list of root paths to ignore when searching for string files
- --translation-search-dirs: Comma separated list of paths to search for JSON Property files
- --validate-only: If specified, the script will not perform any writes to the file system

Typical invocation:

    node scripts/importLocaleStrings.js --string-search-dirs lib/client/packages,sites \
         --string-exclude-dirs lib/client/packages/platform/lib --translation-search-dirs translations

### Flow of Execution

When invoked, importLocaleStrings will recurse through all of the paths specified in --translation-search-dirs, looking for files of the form UI\_&lt;locale&gt;.properties. For require.js compatibility, it will ensure that each locale has a lower case name, and read the corresponding property file into memory.

If a translated JSON Property file contains malformed HTML, it will print the related key and filename along with a warning.

With the translations in memory, the script will recurse through all of the paths specified in --string-search-dirs, looking for files that match 'nls/strings.js' (skipping those with parent directories that match --string-exclude-dirs), and process each string file in turn.

To process a string file _nls/strings.js_, the script will read the keys in the file, and create a corresponding file for each locale in _nls/&lt;locale&gt;/strings.js_, with the locale values for the corresponding keys, and the same object structure as the original string file.

If a key is missing from a locale file, the script will print the file path, key and locale along with a warning.

<a name="workflow.add-localization"></a>

Developer Workflow - Adding Localization Support to an Application Package
--------------------------------------------------------------------------

To add localization support to an application package:

1. Decide on an appropriate prefix for the localized string keys (e.g. ccDevice). The prefix must be unique across all applications and packages.
1. Create:

       <package root>/nls/strings.js

   This file will export a javascript object with keys pointing to strings for translation:

       define({
         root: {
           ccDevice: {
             deviceItemSummaryHardware: {
               userInformation: 'User Information',
               username: 'Username',
               assignedUsername: 'Assigned Username'
             }
           }
         }
       });

1. Add the related file as a dependency to the package main.js, and export it as appStrings:

       define([
         ...
         'i18n!./nls/strings'
       ], function(
         ...
         strings
       ) {
         'use strict';

         return {
           ...

           appStrings: strings
         };
       });

1. For each template, replace each static string with a controller property that will contain the localized string:

       <!-- Device Item Summary Hardware -->

       ...

       <div {{bind-attr class=":title-collapsible controller.slideSettings.userInformation:content-opened:content-closed"}}>
         <h3>User Information</h3>
       </div>
       <div {{bind-attr class=":content-collapsible controller.slideSettings.userInformation:collapsible-is-open"}}>
         <table class="table table-left-header fill-width  device-details-table">
           <tr>
             <td class="text-right">Username</td>
             <td>{{username}}</td>
           </tr>
           <tr>
             <td class="text-right">Assigned Username</td>
             <td>{{assignedUsername}}</td>
           </tr>
           ...

   Becomes ...

       <!-- Device Item Summary Hardware -->

       ...

       <div {{bind-attr class=":title-collapsible controller.slideSettings.userInformation:content-opened:content-closed"}}>
         <h3>{{t "ccDevice.deviceItemSummaryHardware.userInformation"}}</h3>
       </div>
       <div {{bind-attr class=":content-collapsible controller.slideSettings.userInformation:collapsible-is-open"}}>
         <table class="table table-left-header fill-width  device-details-table">
           <tr>
             <td class="text-right">{{t "ccDevice.deviceItemSummaryHardware.username"}}</td>
             <td>{{username}}</td>
           </tr>
           <tr>
             <td class="text-right">{{t "ccDevice.deviceItemSummaryHardware.assignedUsername"}}</td>
             <td>{{assignedUsername}}</td>
           </tr>
           ...

<a name="examples"></a>

Localization Examples
---------------------

### Example: AbsLocale (Globally Shared Resource)

    define({
      root: {
        shared: {
          buttons: {
            ok: 'Ok',
            cancel: 'Cancel',
            back: 'Back',
            continue: 'Continue',
            edit: 'Edit',
            save: 'Save'
          },

          id: 'Id',
          accountId: 'Account Id',
          esn: 'Identifier',
          userName: 'Username',
          optional: 'optional'

          search: {
            placeHolder: 'Search',
            caseSensitivePlaceHolder: 'Search is case-sensitive',

            options: {
              all: 'All'
            }
          }
          
          editMenu: {
            label: 'Edit',

            options: {
              editProperties: 'Properties',
              delete: 'Delete'
            }
          }
        }
      }
    });

### Example: Customer Center Devices

    define({
      root: {
        ccDevice: {
          shared: {

            // Shared Options for the CcDevice Package
            // ---------------------------------------

            deviceGroupSearch: {
              placeHolder: { ref: 'shared.search.placeHolder' }
            },

            deviceSearch: {
              placeHolder: { ref: 'shared.search.caseSensitivePlaceHolder' }
            }
          },

          models: {

            // Device Group Model
            // ------------------

            // Included here for demonstration purposes, but will not be localizing these fields in this 
            // iteration, because they are expected to be provided by the endpoint in localized form.

            deviceGroup: {
              childrenRelations: 'Children Relations',
              description: 'Description',
              deviceGroup: 'Device Group',
              id: { ref: 'shared.id' },
              lastModified: 'Last Modified',
              name: 'Name',
              nodeType: 'Node Type',
              parentRelation: 'Parent Relation'
            }
          },

          pages: {

            // Device Groups List Page
            // -----------------------

            deviceGroupsListPage: {
              header: {
                title: 'Device Groups',
                search: { ref: 'ccDevice.shared.deviceGroupSearch' },

                addDeviceGroupMenu: {
                  label: 'Add Device Group',
                  options: {
                    fixed: 'Fixed Device Group',
                    smart: 'Smart Device Group',
                    folder: 'Folder'
                  }
                }
              },

              body: {
                actionMenuOptions: {
                  delete: { ref: 'shared.editMenu.options.delete' }
                },

                treeMenuOptions: {
                  childFolder: 'Child Folder',
                  childFixedGroup: 'Child Fixed Device Group',
                  childSmartGroup: 'Child Smart Device Group',

                  delete: { ref: 'shared.editMenu.options.delete' },
                  duplicate: 'Duplicate Device Group',
                  editProperties: { ref: 'shared.editMenu.options.editProperties' }
                }
              }
            },

            // Device Group Details Page (Related Devices)
            // -------------------------------------------
            
            deviceGroupDevices: {
              nav: {
                title: 'DEVICE GROUPS',
                search: { ref: 'ccDevice.shared.deviceGroupSearch' }
              },

              header: {
                smart: {
                  filterToggleButton: {
                    activate: 'Show Filter',
                    deactivate: 'Hide Filter'
                  },

                  filter: {
                    editButton: 'Edit Filter'
                  }
                },

                fixed: {
                  addDevicesButton: 'Add Devices'
                },

                editMenu: { ref: 'shared.editMenu' },

                search: { ref: 'ccDevice.shared.deviceSearch' }
              },

              body: {
                // Columns from the model
              }
            },

            // Device Details Page
            // -------------------

            device: {
              buttons: {
                deviceFreeze: 'Device Freeze v5.x',
                dataDelete: 'Data Delete v5.x',
                goToClassic: 'View in v5.x'
              },

              tabLabels: {
                hardware: 'Device Details',
                history: 'Device History',
                locationHistory: 'Location History',
                software: 'Software',
                dlp: 'Data Loss Prevention',
                dlpMatchHistory: 'DLP History',
                policy: 'Policy'
              },

              // Most labels will be derived from Device model properties. Exceptions are here.

              detailsTab: {
                title: 'Device Details',
                hardDriveFreeSpace: 'Hard Drive Free Space',
                userInformation: 'User Information',
                operatingSystem: 'Operating System',
                userDefinedFields: 'User Defined Fields',
                hardware: 'Hardware',
                physicalDrives: 'Physical Drives',
                volumes: 'Volumes',
                networkInformation: 'Network Information',
                mobileBroadbandAdapters: 'Mobile Broadband Adapters'
              },

              historyTab: {
                lastConnectedOn: 'Device last connected on {{lastConnectedTimestamp}}',
                changeHistory: {
                  title: 'Change History',
                  search: {
                    placeHolder: { ref: 'shared.search.caseSensitivePlaceHolder' }
                  }
                }
              },

              locationHistoryTab: {
                title: 'Location History',
                mapAllButton: 'Map All',
                search: {
                  placeHolder: { ref: 'shared.search.caseSensitivePlaceHolder' }
                }
              },

              softwareTab: {
                title: 'Software',
                installedSoftwareTitle: 'Installed Software'
              },

              dlpTab: {
                title: 'Data Loss Prevention Match Scores',
                creditCardNumbersTitle: 'Credit Card Numbers',
                personalHealthInformationTitle: 'Personal Health Information',
                personalFinancialInformationTitle: 'Personal Financial Information',
                socialSecurityNumbersTitle: 'Social Security Numbers',
                profanity: 'Profanity',
                encrypted: 'Encrypted',
                bullying: 'Bullying'
              },

              dlpHistoryTab: {
                title: 'DLP History'
              }
            },
          },

          modals: {

            // Create Fixed Device Group Modal
            // -------------------------------

            createFixedGroup: {
              heading: 'Create a Fixed Device Group',

              stepLabels: {
                properties: 'Set Properties',
                select: 'Search & Select Devices',
                verify: 'Verify & Save'
              },

              propertiesStep: {
                instructions: 'Select devices to add to this Fixed Device Group. You can filter the list to refine the results.',
                advancedSearchButton: {
                  activate: 'Advanced Search',
                  deactivate: 'Cancel Advanced Search'
                }
              },

              verifyStep: {
                instructions: 'To add the selected devices to <strong>{{groupName}}</strong> Device Group, verify and save.'
              },

              statuses: {
                inProgress: 'Creating Device Group ...',
                success: {
                  message: '<strong>{{groupName}}</strong> Device Group created successfully',
                  details: '{{deviceCount}} devices added to Device Group'
                },
                error: {
                  message: 'Error Creating Device Group',
                  details: 'An error occurred while creating the Group'
                }
              }
            }
          }
        }
      },

      // Supported Locales
      // -----------------

      'fr': true,
      'jp': true
    });

<a name="tasks"></a>

Tasks
-----

### T-CORE: Core Localization Support

- T-CORE-1: License JSWorld
- T-CORE-2: Add JSWorld / Em.I18n
- T-CORE-3: AbsDate
- T-CORE-4: AbsNumber
- T-CORE-5: AbsLocale
- T-CORE-6: ApplicationBase.mergePackage() support for appStrings
- T-CORE-7: Change jQuery to load via script tag before require.js. Update dependent code to use Em.$

### T-FLOW: Core Import/Export Workflow Script Support

- T-FLOW-1: Validate structure of an HTML fragment
- T-FLOW-2: Write a JSON Property file (parsing is trivial)
- T-FLOW-3: Search a tree for string.js files, minding an exclusion list

### T-EXP: Implementation of exportLocaleStrings.js

- T-EXP-1: Script stub - accept command line parameters and print help
- T-EXP-2: Read string files into merged object and print warnings as necessary
- T-EXP-3: Write the JSON Property file

### T-IMP: Implementation of importLocaleStrings.js

- T-IMP-1: Script stub - accept command line parameters and print help
- T-IMP-2: Read JSON Property files to internal representation, printing warnings as necessary
- T-IMP-3: Process string files

### T-INT: Localization Integration

- T-INT-1: Desktop.NumberFieldView
- T-INT-2: Desktop.DateFieldUtilMixin
- T-INT-3: CcDesktop.CcWeekDaysView

### T-STR: Core Localized String Integration

- T-STR-1: ui package
- T-STR-2: desktop package
- T-STR-3: login package

### T-CC-STR: Localized String Integration for Customer Center

- T-CC-STR-1: cc-device-groups application
- T-CC-STR-2: cc-reports application
- T-CC-STR-3: cc-software-titling application
- T-CC-STR-4: cc-policies application
- T-CC-STR-5: cc-app-foundation package
- T-CC-STR-6: cc-data package
- T-CC-STR-7: cc-desktop package
- T-CC-STR-8: cc-device package
- T-CC-STR-9: cc-policies package
- T-CC-STR-10: cc-software-titling package
- T-CC-STR-11: cc-reports package

### T-AM-STR: Localized String Integration for Absolute Manage

- T-AM-STR-1: login application
- T-AM-STR-2: am application
- T-AM-STR-3: am-app-foundation package
- T-AM-STR-4: am-data package
- T-AM-STR-5: am-session package
- T-AM-STR-6: am-user-prefs package
- T-AM-STR-7: am-assignable-item package
- T-AM-STR-8: am-assignable-item-foundation package
- T-AM-STR-9: am-content-related package
- T-AM-STR-10: am-mobile-administrator-related package
- T-AM-STR-11: am-mobile-application package
- T-AM-STR-12: am-mobile-application-related package
- T-AM-STR-13: am-mobile-assigned-apps-related package
- T-AM-STR-14: am-mobile-assigned-profiles-related package
- T-AM-STR-15: am-mobile-certificate-related package
- T-AM-STR-16: am-mobile-command package
- T-AM-STR-17: am-mobile-configuration-profile package
- T-AM-STR-18: am-mobile-configuration-profile-related package
- T-AM-STR-19: am-mobile-custom-field-related package
- T-AM-STR-20: am-mobile-device package
- T-AM-STR-21: am-mobile-device-related package
- T-AM-STR-22: am-mobile-policy package
- T-AM-STR-23: am-mobile-policy-related package
- T-AM-STR-24: am-mobile-provisioning-profile package
- T-AM-STR-25: am-mobile-provisioning-profile-related package
