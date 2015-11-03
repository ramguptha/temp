File Naming Standard
====================

Please consider the following guidelines when naming files, and when reviewing code.

1. Classes exported from application packages into the application global namespace (i.e. via the appClasses property) (e.g. cc-policies, am-mobile-devices) _must_ have a unique prefix.

  For example:

  - <em>ccPolicies</em>ItemController
  - <em>ccPolicies</em>ItemTabsDeviceListView
  - <em>amMobileDevice</em>GroupsController
  - <em>amMobileDevice</em>sRelatedShowDeviceView

1. Code modules that are exported into the application namespace _must_ have filenames that are the underscored version of the exported name.

  For example:

  - cc/cc-policies/lib/controllers/cc\_policies\_item\_controller.js
  - cc/cc-policies/lib/views/cc\_policies\_item\_tabs\_device\_list\_view.js
  - am/am-mobile-device/lib/controllers/am\_mobile\_device\_groups\_controller.js
  - am/am-mobile-device/lib/views/am\_mobile\_devices\_related\_show\_device\_view.js

1. Other code modules _must_ have short, meaningful names. Other code modules _should not_ have the same prefix as exported code modules (and often should not have any kind of special prefix at all). Tightly coupled modules (e.g. corresponding controller / views) _must_ have corresponding names.

  For example:

  - cc/cc-reports/lib/controllers/has\_result\_value\_views.js
  - cc/cc-reports/lib/views/result\_value\_views.js
