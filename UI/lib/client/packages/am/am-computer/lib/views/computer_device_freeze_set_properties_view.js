define([
  'ember',
  'desktop',
  'ui',

  'wysihtml5',
  'wysihtml5ParserRules',

  'text!../templates/computer_device_freeze_set_properties.handlebars',
  '../templates/modal_device_freeze_data_delete_layout'
], function(
  Em,
  Desktop,
  UI,

  HtmlEditor5,
  HtmlEditor5ParserRules,

  template,
  ModalDeviceFreezeLayout
  ) {
  return Em.View.extend({
    defaultTemplate: Em.Handlebars.compile(template),
    layout: ModalDeviceFreezeLayout,
    editor: null,

    didInsertElement: function() {
      // Setup editor
      var editor = new wysihtml5.Editor("wysihtml5-editor", {
        toolbar:      "wysihtml5-editor-toolbar",
        parserRules:  wysihtml5ParserRules
      });
      this.set('editor', editor);

      // Add event handler for editor 'key' event
      var controller = this.get('context'), onInputFunc = function () {
          controller.set('selectedMessageHtml', editor.getValue());
        };

      // DEATH TO INTERNET EXPLORER
      // This could have been handled by the 'input' event except IE doesn't allow it
      // on elements with the 'contenteditable' property.
      // https://developer.mozilla.org/en-US/docs/Web/Events/input
      // Also the handlers must be inside editor's load event to support FF.
      editor.on("load", function() {
        $('.wysihtml5-sandbox').contents().find('body').on({
          keyup: onInputFunc,
          paste: onInputFunc,
          cut: onInputFunc,
          copy: onInputFunc
        });

        $('#wysihtml5-editor').on('input', onInputFunc);
      });

      // Add event handler for editor 'change' event. Need to refresh html value, which complete only after editor lose focus.
      editor.on('change', function() {
        // Set html value
        controller.set('selectedMessageHtml', editor.getValue());
      });

      this._super();

      // Does not work on current Ember version. Keep it for references
      //Em.run.scheduleOnce('afterRender', this, this.afterRenderEvent);

      Em.run.next(this, this.afterRenderEvent);
    },

    onHtmlEditorVisible: function() {
      var visible = this.get('controller.htmlEditorVisible');
      this.editorVisible(visible);
    }.observes('controller.htmlEditorVisible'),

    onHtmlEditorEnable: function() {
      var enable = this.get('controller.htmlEditorEnable');
      var editor = this.get('editor');
      if(editor) {
        //Read only does not work without focus state first
        editor.focus();
        enable ? editor.enable() : editor.disable();
      }
    }.observes('controller.htmlEditorEnable'),

    onHtmlEditorValue: function() {
      var html = this.get('controller.htmlEditorValue');
      var editor = this.get('editor');
      if(editor) {
        html ? editor.setValue(html) : editor.setValue('');
      }
    }.observes('controller.htmlEditorValue'),


    editorVisible: function(visible) {
      var editor = this.get('editor');
      if(editor) {
        // Does not work without focus
        editor.focus();
        if(editor.currentView) {
          visible ? editor.currentView.show() : editor.currentView.hide();
        }
        if(editor.toolbar) {
          visible ? editor.toolbar.show() : editor.toolbar.hide();
        }
      }
    },

    afterRenderEvent: function() {
      var controller = this.get('context');
      var html = controller.get('selectedMessageHtml');
      var editor = this.get('editor');

      // Default state is a hidden editor
      if(controller.editMode === 'editorHidden') {
        this.editorVisible(false);
      }

      html ? editor.setValue(html) : editor.setValue('');
    },

    willDestroyElement: function() {
      this.get('editor').stopObserving('change');
    }

  });
});
