define([
  'ui',
  'logger'
], function(
  UI,
  logger
) {

  return (function () {

  //This class is a singleton
  function GlobalMenuManager()  {

    // set some properties for our singleton
    this.name = "GlobalMenuManager";
    this.openMenu = null; //ButtonGroupView object.

    // Figure out what type of menu is it
    // @param theMenu
    this.markOpenedMenu = function(menuBtn, menu) {
      if(menuBtn.hasOwnProperty('showingChildren')) {
        this.handleButtonGroupObj(menuBtn);
      }
      else {
        this.handleCustomMenu(menuBtn, menu);
      }

      // Close any open menus in the new menu manager
      UI.MenuController.lookup().hide();
    };

    // Close the menu and remove the reference to it from this global singleton.
    this.closeMarkedMenu = function(jqueryObj, force) {
      var target = (jqueryObj && jqueryObj.hasOwnProperty('target')) ? jqueryObj.target : jqueryObj;

      if(this.openMenu != null) {
        if(this.openMenu.hasOwnProperty('showingChildren')) {
          if(this.openMenu.get('showingChildren') === true) {
            this.openMenu.toggleProperty('showingChildren');
          }
        }
        else {
          this.openMenu.removeClass('btn-menu-active');
          this.openMenu.next().remove();
        }
        this.openMenu = null; //kill reference
      }
    };

    // These menus are part of Dave's "button factory" framework. Most drop-own menus are of this kind.
    //  @param buttonGroupViewObj
    this.handleButtonGroupObj = function(buttonGroupViewObj) {
      // TODO to remove this code whenever the work on the device group jquery menu is done
      //close custom menus if they are open
      if(this.openMenu != null && !this.openMenu.hasOwnProperty('showingChildren')) {
        this.closeMarkedMenu();
      }

      if(this.openMenu != null) {

        if(this.openMenu === buttonGroupViewObj) {  //if pressing on the same menu button as before
          this.openMenu.toggleProperty('showingChildren');
        }
        else {
          //if pressing on a different menu button than before
          this.closeMarkedMenu(); //close previous
          this.openMenu = buttonGroupViewObj;
          this.openMenu.toggleProperty('showingChildren');
        }
      }
      else {
        this.openMenu = buttonGroupViewObj;
        this.openMenu.toggleProperty('showingChildren');
      }
    };

    // Custom [popup/context] menu is used on the tree_view and list_view. It is not part of Dave's "button factory" framework.
    // @param menuBtnJQueryObj
    this.handleCustomMenu = function(menuBtnJQueryObj, menuJQueryObj) {

      //close buttonGroupViewObj menus if they are open
      if(this.openMenu != null && this.openMenu.hasOwnProperty('showingChildren') ) {
        this.closeMarkedMenu();
      }

      if(this.openMenu != null) {
        //if pressing on a different menu button than before
        this.closeMarkedMenu(menuBtnJQueryObj); //close previous
        this.openMenu = menuBtnJQueryObj;
        menuJQueryObj.toggle();
      }
      else {
        this.openMenu = menuBtnJQueryObj;
        menuJQueryObj.toggle();
      }
    };
  };



  // our instance holder
  var instance;

  // an emulation of static variables and methods
  var _static  = {

    name:  "GlobalMenuManager",

    // Method for getting an instance. It returns
    // a singleton instance of a singleton object
    getInstance:  function() {

      if( instance  ===  undefined )  {
        window.GlobalMenuMgr = instance = new GlobalMenuManager();
      }

      return  instance;
    }
  };

  return  _static;

})()});

