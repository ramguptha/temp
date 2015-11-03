Supporting UI Automation
========================

We need to mark up important parts of the DOM in our templates, so that QA is able to automate interactions with it in a black box manner. To that end, our templates need certain elements to have special class names. In general, a DOM element of interest needs to have a class name that is unique to its parent "scope".

The Rules:

- Every top level container within a template needs a class name unique to the template.
- Every top level container within a dynamic content block (#if, #unless, #each) needs a class name unique to the template.
- Every form button and field needs a unique class name within the scope of its container.
- Every piece of content worth examining directly (dynamic icons, counters, and so-on) needs a unique class name within the scope of its container.
- Class names for automation take the following form:
  - is-container-for-{container name} - any DOM container
  - is-button-for-confirm - ok button
  - is-button-for-cancel - cancel button
  - is-button-for-{action name}
  - is-button-for-{name}
  - is-input-for-{name} - an form input
  - is-checkbox-for-select-row - a checkbox used to mark a row of data as selected for further action
  - is-checkbox-for-{name} - a checkbox
  - is-{name} - anything else
- List and Tree data rows have their top level item containers marked up with both a data attribute for the id and a data attribute for the automationId, which may or may not be the same as the id.
  - data-is-container-for-id - DOM attribute set to the id of the object, i.e. the Guid.
  - data-is-container-for-automation-id - DOM attribute set to the automationId of the object, i.e. the ESN of the device (for example).
