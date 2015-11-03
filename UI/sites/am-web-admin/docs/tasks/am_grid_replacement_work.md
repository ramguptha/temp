AM Grid Replacement Work
=================================

The underlying library in charge of our data sets presentation on the front end has changed from jqGrid to an [in-house library designed and implemented by Dave Duchene](http://localhost:3001/dev/docs/grid-replacement.md)


Basic Info
----------

The major works needs to be done on the code modules that are extracted from List Controller / Page Controller.

Given the way AM application has been architect, most of the code modules are similar and all extended
from few base custom list classes.
Also, the way data is presented and formatted for some cases has been updated that will need quite a few
code modules of AM that are customized to be updated.

The rest of the classes don't seem to require any updates or just minor ones for that matter.

Affected Code Modules
---------------------

- __am-app-foundation__
  - *C*
    - select\_columns\_controller

- __am-assignable-item__
  - *B*
    - assignable\_list\_base\_controller
    - assignable\_content\_list\_controller

  - *C*
    - assignable\_item\_menu\_controller
    - content\_item\_controller
    - action\_item\_controller
    - main.js
    - \*\_item\_related\_\* code modules
    - assignable\_\*\_list related code modules

- __am-assignable-item-foundation__
  - *C*
    - action\_add\_policy\_assignment
    - content\_policy\_assignment\_base

-
- __am-computer__
  - *B*
    - computer_list_controller
    - computer_summary_list_controller

  - *C*
    - computer_data_delete_controller
    - computer_device_freeze_controller
    - computer_item_controller
    - main.js
    - computer\_item\_related\_\*\_controllers
    - computer_group_list_controller

- __am-custom-field__
  - *C*
    - custom_field_list related code modules
    - custom_field_enum_list
    - main.js
    - custom_field_item_add_edit

- __am-mobile-command__
  - *B*
    - command_computer_history_list_controller
    - command_computer_queue_list_controller

  - *C*
    - command_group_list_controller
    - main.js
    - Possible Grid work and refactoring (to be extended from assignable_list_base e.g.)
      - command_mobile_history_list_controller
      - command_mobile_queue_list_controller

- __am-mobile-device__
  - *C*
    - mobile_device_group_list_controller
    - mobile_device_item_controller
    - mobile_device_list_controller
    - main.js
    - mobile\_device\_install\_\*\_controller
    - mobile_device_item_add_to_policies_controller
    - mobile\_device\_item\_related\_\*\_controller

-
- __am-mobile-policy__
  - *B*
    - mobile_policy_item_controller
    - mobile_policy_list_controller
    - smart_policy_controller
    - smart_policy_devices_list_preview_controller

  - *C*
    - main.js
    - mobile\_policy\_item\_related\_\*\_controller
    - mobile\_policy\_item\_add\_\*\_controller
    - smart_policy_filter_controller

- __am-user-self-help-portal__
  - *B*
    - user_self_help_list_controller

  - *C*
    - main.js
    - user_self_help_item_controller

- __am-desktop__
  - *A*
    - list_controller_columns
    - list_simple_actions code modules

  - *C*
    - list_controller

- __desktop__
  - *A*
    - multi\_select code modules
    - summary\_list code modules
    - related\_list code modules


Grid Work Update
---------------

- Here is the list of areas to be updated for the purpose Grid work:
  - Base class's name
  - Nav related codes
  - visibleColumnSpecs -> Anywhere that has customized formatting of columns
  - More to be added, most likely some property renaming.
  - Removing unused list related code from the code modules such as ListView/NavView if unused.


Prerequisite
-------------

There is a restructuring code that needs to be done for the Grid work.
The packages below have all similar codes which can be simplified into only couple of packages and extend classes.
The result will:
  - Help us to find the codes easily due to a smaller list of packages,
  - Help with the required work for any similar update to these code modules
  - Make our packages clean and simple free of any redundancy

  - Please note that Some of controllers here may need some minor Grid work as well that will be done after the restructuring work.
    - am-mobile-application *C*
    - am-mobile-application-related *C*
    - am-mobile-assigned-apps-related *C*
    - am-content-related
    - am-mobile-administrator-related
    - am-mobile-certificate-related
    - am-mobile-assigned-profiles-related
    - am-mobile-configuration-profile-related
    - am-mobile-custom-field-related
    - am-mobile-device-related
    - am-mobile-policy-related
    - am-mobile-provisioning-profile-related
    - am-mobile-configuration-profile
    - am-mobile-provisioning-profile

Restructuring Implementation
-----------------------

- Required work for __am-multi-select__
  - All the existing controllers in the mentioned packages that are extended from _Desktop.MultiSelectController_ will be moved to:
    _am-desktop/lib/controllers/_

  - A base class should be created to include all similar methods/properties
    - Shared properties that are candidate for being moved to the base class:
      - init (Example)
            Change  AmMobileDeviceRelated.get('storeContext')
            to      this.get('datastoreContext')
    - Changes on a sample inherited class:
      - new property: datastoreContext will be created (Example)
            dataStoreContext: function() {
               return { mobileDeviceListName: AmData.get('specs.AmMobileDeviceGroupSpec.DEFAULT_NAME') };
            }.property()

      - dataStore will be changed to:
            datastore: function() {
              return AmData.get('stores.mobileDeviceStore');
            }.property()

    - defunct Properties/methods to be removed from all these code modules:
      - _columnFormats_
      - _inputsList_

- Required work for __am-mobile-related__
  - All the classes in the mentioned packages that have _related_list_controller_ in their name, if they have similar structure as
   _am-desktop/list_controller_columns_, they will be:
   - Extended from the mentioned base class (ListControllerColumns).
   - Similar work is available on _am-computer_ which contains some inherited classes.
   - These properties that are the same for every inherited class should be removed from any child class (unless the value is different)
      (including existing inherited files from this base class):
     -  navigationEnabled / selectionEnabled / selectColumnsSupported
     - init (if not doing anything specific) / getDataSourceForPage
   - Some of the properties can be simple renamed to the generic ones:
     - e.g. deviceRelatedListColumnsLoading to listColumnsLoading
     - e.g. deviceRelatedListColumnsLock to listColumnsLock
   - Defunct properties that should not be either in the base or inherited classes.
      - List View / columnFormats
   - If they only have one consumer, then the inherited class should be moved to its corresponding package, if not,
     we can keep them in _am-desktop_ and export them from this package to be accessible from inside other packages.

- __NOTE!__
  - After update of each module, any reference to it should be updated too.
  - Please commit after updates on each file, so other people would not have to deal with conflicts.
  - If all files in the package is updated, please add a prefix of 'deprecated' so we can keep track of changes.


