
# How to apply the UI feature hook to different parts of the application

The structure of the Feature Matrix hook for the UI has been implemented and applied. This document contains instructions on applying any upcoming new feature.

(**The design doc for this project is available [here](http://localhost:3000/dev/cc-classic/docs/tasks/63088-feature_matrix_design.md)**)

Currently, UI interface consists of 4 different applications:

1. View Reports
2. Device Groups
3. Software Catalog
4. Policy Management

**Note**! The very first step is to make sure Feature Matrix's handle is enabled. There is a switch called: ' **isFeatureOn**'. This switch is located at:

 - (local access) cc-classic/client/env.js
 - (non-local environments (e.g. dv2corp3, qacorp,…)) ng/env.js

If the switch is off, the related flags are set to true in cc-app-foundation/features\_controller.js.

For the purpose of the instructions, assume the Software feature is not available (SDC\_GEN: False).

# Case 1: The entire related application will not be available

If there is an entire application that will not be available (for example Software Catalog in this case) then the link on the left navigation menu (Customer Center links) will be hidden.

However, to make sure the user will see the 'Unavailable feature error' massage, these changes need to be applied to the code:

- cc-app-foundation/features\_controller.js
  - Map the flag to a proper property name.
  - Include both the Product and Environment flag to the featuresToEnvFlags map property.

- cc-classic/client/cc-{APPLICATION}/core
  - The IndexRoute will pass the RequiresFeatures mixin.
  - Set the requiredFeatureFlags to the property that the flag is mapped to in the FeaturesController (e.g. 'software'.w() for SDC\_GEN') 
  - Handle feature errors:
    - ErrorRoute: Em.Route,
    - ErrorView: UI.FeaturesErrorView.

- packages/cc/cc--{APPLICATION}/main
  - The main {Application}IndexRoute and necessary child routes will pass the RequiresFeatures mixin.
    - Set the requiredFeatureFlags to the property that the flag is mapped to in the FeaturesController (e.g. 'software'.w() for SDC\_GEN') 

  - For handling the bookmarked pages, make sure the related error route/view have been defined, e.g:
    - Implement ErrorRoute properties to handle feature errors:
      - {Application}IndexErrorRoute: Em.Route,
      - {Application}IndexErrorView: UI.FeaturesErrorView.

# Case 2: The related reports are not available

In View Reports, the related reports should be hidden. Here are the affected code modules:

**Note!** These updates are done temporarily. In future after migration of Report engine to Java, the reports will be filtered in the server side.

- packages/cc/cc-reports/cc\_report\_config\_list\_controller
  - Update pageDataFilter to ensure the particular systemNames are excluded from the list of reports.

# Case 3: The related tabs are hidden in the Device Details page

User can get to the Device Details page by clicking on a Device Identifier through three different locations: Reports / Device Groups / Policy Management (Devices tab). In the Device Details page, the related tabs (e.g. Software tab in this example) need to be hidden.

- packages/cc/cc-device/device\_item\_controller
  - Update the tabList property to include tabs that their corresponding features are available.

Here are the changes on cc-device/main. Similar changes need to be done to cc-reports and cc-policies main files.

- Update packages/cc/cc-device/main
  - In the related tabs' route (e.g. SoftwareRoute), pass the CcAppFoundation.RequiresFeatures mixin
  - Set the requiredFeatureFlags to the corresponding flag of that route (e.g. 'software'.w())
  - Handle feature errors:
    - CcDevicesRelatedDeviceErrorRoute: Em.Route,
    - CcDevicesRelatedDeviceErrorView: UI.FeaturesErrorView.

# Case 4: The related information is hidden in Policy Management's landing page

There are containers in Policy Management's landing page which show if the policy is activated for e.g. Software or DLP. If the related feature is not available, these containers need to be hidden. Here are the updates need to be done:

- packages/cc/cc-policies/cc\_policies\_device\_policies\_controller
  - Update the filteredData property to ensure a policy type that its corresponding feature is not available is excluded from the list of policies

# Case 5: The related information is not available in Policies tabs

In the Policies tab, the related containers to the missing features should be hidden. User can get to the Policies tab by:

1. 1-Clicking on a Device Identifier (Reports/ Device Groups/ Policy Management)
2. 2-Clicking on a Policy Group on the Policy Management landing page.

- packages/cc/cc-device/device\_item\_policy\_controller
  - Create the filteredData property in the PolicyGroupController's class
    - Filter the 'policies' of the content based on the corresponding feature flags and type of policy
    - g. if policyType is 'DLP' and the feature is not enabled, remove it from the list of policies


