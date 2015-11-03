define([
  'ember',
  'desktop',
  'help',
  'formatter',

  'packages/am/am-data',
  '../namespace',

  'packages/am/am-assignable-item-foundation',
  './content_item_build_base',

  '../views/content_edit_properties_view'
], function (
  Em,
  Desktop,
  Help,
  Formatter,

  AmData,
  AmContent,

  AmAssignableItemFoundation,
  ContentItemBuildBase,

  AmContentEditPropertiesView
) {

  // Content Item Edit Properties Content Controller
  // ==================================
  //
  // This controller contains the wizard of editing properties of a content.

  return AmAssignableItemFoundation.ContentItemBaseController.extend(ContentItemBuildBase,
    AmAssignableItemFoundation.AmEditWizard, {

    heading: 'amAssignableItem.modals.editContent.heading'.tr(),
    headingIconClass: 'icon-edit-icon',

    urlForHelp: Help.uri(1035),

    isEditing: true,

    // Old values
    displayNameOld: null,
    selectedCategoryOld: null,
    descriptionOld: null,
    allowFileToLeaveAbsSafeOld: null,
    canEmailOld: null,
    canPrintOld: null,
    wifiOnlyDownloadOld: null,

    // Views
    // -----
    //
    // One view per wizard 'step'.

    editView: AmContentEditPropertiesView,
    editSaveView: AmAssignableItemFoundation.AmEditSaveView,

    onShowModal: function() {
      this.loadContent(this.get('model.contentId'));

      this.get('wizard').transitionTo('edit');
    },


    // Properties and functions associated with editing content
    // =============================
    //
    loadContent: function (contentId) {
      var self = this;

      self.setProperties({
        paused: true,
        description: null,
        selectedCategory: null,
        customCategory: null,
        errorMessage: null
      });

      this.set('existingContent', AmContent.get('store').acquireOne(null, contentId, function (datasource) {
        var data = datasource.get('content').objectAt(0).get('content.data');

        self.setProperties({
          displayNameOld: data.get('name'),
          selectedCategoryOld: data.get('mediaCategory') ? data.get('mediaCategory') : '' ,
          descriptionOld: data.get('description') ? data.get('description') : '' ,
          allowFileToLeaveAbsSafeOld: data.get('canLeaveAbsSafe') === 1,
          canEmailOld: data.get('canBeEmailed') === 1,
          canPrintOld: data.get('canBePrinted') === 1,
          wifiOnlyDownloadOld: data.get('wifiOnly') === 1,
          hashedPassphraseOld: data.get('hashedPassword'),
          passphraseOld: data.get('hashedPassword') ? self.get('dummyPassphrase') : null,

          // Initial state for editing
          isSaveDisabled: true,
          paused: false
        });

      }, null, false, false));

    },

    onEditDisplayName: function () {
      if (!Em.isEmpty(this.get('files'))) {
        Em.set(this.get('files')[0], 'displayName', this.get('displayName'));
      }
    }.observes('displayName'),

    onFieldChanged: function() {
      if(this.get('paused')) {
        return;
      }

      var passPhraseEmpty = Em.isEmpty(this.get('passphrase')) && Em.isEmpty(this.get('verifyPassphrase')),
        passPhraseMatches = this.get('passphrase') === this.get('verifyPassphrase');

      var passPhraseIsValid = passPhraseEmpty || passPhraseMatches;
      this.set('passphraseErrorMessage', passPhraseIsValid ? '' : this.get('tPassphraseNotMatched'));

      if (passPhraseEmpty) {
        this.set('isAllowFileToLeaveAbsSafeDisabled', false);
      } else {
        this.setProperties({
          allowFileToLeaveAbsSafe: false,
          isAllowFileToLeaveAbsSafeDisabled: true
        });
      }

      var isRequiredEmpty = Em.isEmpty(this.get('displayName')) || Em.isEmpty(this.get('selectedCategory'));

      var isDirty = this.get('displayNameOld') !== this.get('displayName') ||
                    this.get('selectedCategoryOld') !== this.get('selectedCategory') ||
                    this.get('descriptionOld') !== this.get('description') ||
                    this.get('allowFileToLeaveAbsSafeOld') !== this.get('allowFileToLeaveAbsSafe') ||
                    this.get('canEmailOld') !== this.get('canEmail') ||
                    this.get('canPrintOld') !== this.get('canPrint') ||
                    this.get('wifiOnlyDownloadOld') !== this.get('wifiOnlyDownload') ||
                    this.get('hashedPassphraseOld') !== this.get('hashedPassphrase') ||
                    this.get('passphraseOld') !== this.get('passphrase') ;

      // Analyzed only if no error, otherwise disable Save button
      //this.set('isSaveDisabled', !this.get('passphraseErrorMessage') ? (!isDirty || isEmpty || !passPhraseIsValid) : true);
      this.set('isSaveDisabled', !this.get('passphraseErrorMessage') ? (!isDirty || isRequiredEmpty || !passPhraseIsValid) : true);
    }.observes('displayName', 'selectedCategory', 'description',
      'allowFileToLeaveAbsSafe', 'canEmail', 'canPrint', 'wifiOnlyDownload',
      'hashedPassphrase', 'passphrase', 'verifyPassphrase'),

    onContentLoaded: function () {
      var data = this.get('existingContent.data');
      if (Em.isNone(data)) {
        return;
      }

      this.setProperties({
        displayName: data.get('name'),
        description: Em.isNone(data.get('description')) ? '' : data.get('description'),
        savedCategory: data.get('mediaCategory'),
        allowFileToLeaveAbsSafe: data.get('canLeaveAbsSafe') === 1,
        canEmail: data.get('canBeEmailed') === 1,
        canPrint: data.get('canBePrinted') === 1,
        wifiOnlyDownload: data.get('wifiOnly') === 1,
        hashedPassphrase: data.get('hashedPassword'),
        isAllowFileToLeaveAbsSafeDisabled: data.get('hashedPassword'),
        passphraseErrorMessage: null,

        // Ignore if paused, stay disabled
        isSaveDisabled: this.get('paused') ? true : false,
        verifyPassphrase: null,
        passphrase: null
      });

      // We get back the has hashed passphrase / password, rather than the originally entered passphrase.
      // Rather than displaying the full (long) hashed value, we just display dummy 4 character long
      // passphrase, which is what the AM Admin application currently does.
      if (!Em.isEmpty(this.get('hashedPassphrase'))) {
        this.set('passphrase', this.get('dummyPassphrase'));
        this.set('verifyPassphrase', this.get('dummyPassphrase'));
      } else {
        this.set('passphrase', null);
        this.set('verifyPassphrase', null);
      }

      this.initPropertySelections();

      // We create a simplified 'file' object so that we can re-use the same logic used when adding new content to
      // check for empty or duplicate names
      var fileToEdit = Em.Object.create({
        displayName: data.get('name'),
        hasNameError: false,
        controller: this
      });

      Em.addObserver(fileToEdit, 'displayName', this, this.onDisplayNameChanged);
      this.set('files', Em.A([fileToEdit]));

    }.observes('existingContent.content'),

    // Construct and submit updated content attributes using /api/content/{id} POST request.
    // This request has the form: {file_1}, where file_1 is as per the file_N portion
    // of the /api/content/batch request.
    submitUpdates: function (okHandler, errHandler) {
      Em.removeObserver(this.get('files')[0], 'displayName', this, this.onDisplayNameChanged);

      var self = this;
      this.set('submitInProgress', true);

      var data = this.get('existingContent.data');

      var action = AmData.get('actions.AmContentUpdateAction').create({
        content: this.buildContentData(
          data.get('mediaFileName'),
          self.get('displayName'),
          data.get('type'),
          data.get('modified'),
          self.get('selectedCategory')
        ),

        onSuccess: function (rsp) {
          self.set('submitInProgress', false);
          self.loadContent(self.get('model.contentId'));
          okHandler(self, rsp);
        },

        onError: function (ajaxError) {
          self.set('submitInProgress', false);
          errHandler(self, ajaxError);
        }
      });

      action.invoke();

      return true;
    },

    clear: function () {
      if (!Em.isEmpty(this.get('files')[0])) {
        Em.removeObserver(this.get('files')[0], 'displayName', this, this.onDisplayNameChanged);
      }
      this.get('files').clear();
    }
  });
});
