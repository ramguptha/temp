How Tos
=======

Get the current application state
---------------------------------

    App.get('router.currentState');

Get the full application state heirarchy
----------------------------------------

    App.get('router.stateChain');

Implement an end to end feature that calls the backend API using a modal popup
------------------------------------------------------------------------------

1. Inside the given Route, implement a connectOutlets function that wires up the Controller and the View classes associated with the route.

       item: Em.Route.extend({
         route: '/:deviceGroupId',

         connectOutlets: function(router, context) {
           var parentController = DeviceGroup.get('parentController');
           var deviceGroupItemController = DeviceGroup.get('deviceGroupItemController');

           deviceGroupItemController.loadDeviceGroup(context.deviceGroupId);

           parentController.connectOutlet({
             controller: deviceGroupItemController.get('relatedDeviceListController'),
             viewClass: Desktop.get('NavContentPageView')
           });
         },

         gotoParent: function(router, evt) {
           router.transitionTo('tree');
         },

         selectionAction: function(router, evt) {

           var groupId = DeviceGroup.get('deviceGroupItemController').id;
           var contentListController = DeviceGroup.get('deviceGroupItemController').relatedDeviceListController;
           var selectedItems = contentListController.get('selectionsList');
         }
        
         ...

2. Implement the handlebars file.

       <button class="btn-dropdown show-delete-cmd" {{action selectionAction}} data-action='delete'>Delete</button>

3. Under the connectOutlets() function implements the UI button handlers to: 
  
       // NOTE NOTE NOTE:
       //
       // This recipe is broken for the general case. Usually, it is possible to examine a "context" property
       // in the evt directly and use that. Speak to a UI developer for more info.
       //
       // TODO: Update this example with something more canonical.
       selectionAction: function(router, evt) {
         var contentListController = DeviceGroup.get('deviceGroupItemController').relatedDeviceListController;
         var selectedItems = contentListController.get('selectionsList');

         // The button has a custom data-action="delete" attribute in this case
         switch (evt.target.attributes['data-action'].value) {
         case 'edit':
           var id =  selectedItems[0].get('id');
           logger.log('AM_CONTENT: CONTENT: selectionAction: edit id:', id);
           router.get('modalController').show(AmContentItemEditRoute.create(), { contentId: id });
           break;

         case 'delete':
           var ids = Em.A([]);
           for (var i = 0; i < selectedItems.length; i++) { ids.pushObject(selectedItems[i].get('id')); }

           var modalRoute = Desktop.get('ModalActionRoute').create({
             init: function() {
               this._super();
               this.set('actionController', DeviceGroupItemDeleteController.create({ids: ids})); //params to actionController
             }
           });

           router.get('modalController').show(modalRoute, {ids: ids}); //params to modalRoute
           break;

         default:
           console.log('The selected option '+evt.target.firstChild.nodeValue.toLowerCase()+' is not implemented.');
           break;
         }
       }  

4. For modal popups, implement this modal invocation:

       var modalRoute = Desktop.get('ModalActionRoute').create({
         init: function() {
           this._super()
           this.set('actionController', DeviceGroupItemDeleteController.create({ids: ids}))
         }
      
         ...
       });

       router.get('modalController').show(modalRoute, {ids: ids});

5. Implement the actionController. In this case: DeviceGroupItemDeleteController.

   **Make sure you implement buildAction() and onSuccessCallback() functions.**

6. Create a new action class in cc-data/lib/actions
7. Register a new action in cc-data/main.js
