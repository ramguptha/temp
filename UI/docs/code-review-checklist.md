Code Review Checklist
=====================

When performing code reviews, please consider the following checklist.

Coding Style
------------

- CamelCase, not under\_scores.
- Formatting.
- Meaningful variable names.
- Refactor copy / pasted code.
- etc, as per [the coding standard](./coding-standard.md).

Architecture
------------

- Thou shalt not place application specific code (CC, AM, etc) in Desktop or UI.

Ember
-----

- Properties:
  - Check dependencies.
- Observers:
  - Are they really needed?
  - Are they _really_ needed?
  - Confirm wether or not .on('init') is applicable, and in turn check usage.
  - Confirm that all dependent properties have corresponding .get()'s in init().
  - If there are multiple, interdependent observers, **refactor**.

Views
-----

- $() versus this.$().
- If using jQuery, need to check this.get('_state') === 'inDOM'.
- Strongly prefer selecting by class name over selecting by id.
- Ensure that all setup done on didInsertElement() is torn down on willDestroyElement.

Handlebars
----------

- HTML is well-formed.
- Avoid triple moustache ({{{foo}}}) if possible. Only justification that comes to mind is locale-specific formatting.
- All HTML needs to be structured to [support UI automation](./supporting-ui-automation.md).
- All HTML needs to conform to the [Style Guide](./style-guide/index.html).
