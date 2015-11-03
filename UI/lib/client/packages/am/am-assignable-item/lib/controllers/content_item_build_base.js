define([
  'ember',
  'desktop',
  'query',
  'formatter',
  'packages/am/am-data'
], function (
  Em,
  Desktop,
  Query,
  Formatter,
  AmData
) {
  'use strict';

  // Content Item Build Base
  // =================
  //
  // This mixin contains properties/functions that are used by both Creation and Edition of Contents.

  return Em.Mixin.create({

    tDuplicateNameErrorMessage: 'amAssignableItem.modals.addContent.assignPropertiesStep.uniqueFileNameError'.tr('nameOfFile'),
    tHintMessage: 'amAssignableItem.modals.addContent.uploadFileStep.hint'.tr(),
    tUploadingMessage: 'amAssignableItem.modals.addContent.uploadFileStep.uploadingMessage'.tr(),
    tExistOnServer: 'amAssignableItem.modals.addContent.uploadFileStep.validation.existOnServer'.tr('nameOfFile'),
    tRenameFileToContinue: 'amAssignableItem.modals.addContent.uploadFileStep.validation.renameFileToContinue'.tr(),
    tRenameItToSave: 'amAssignableItem.modals.addContent.uploadFileStep.validation.renameItToSave'.tr(),
    tNameIsBlank: 'amAssignableItem.modals.addContent.uploadFileStep.validation.nameIsBlank'.tr(),
    tFileNameIsBlank: 'amAssignableItem.modals.addContent.uploadFileStep.validation.fileNameIsBlank'.tr(),
    tUploadFailed: 'amAssignableItem.modals.addContent.uploadFileStep.validation.uploadFailed'.tr('nameOfFile'),
    tSelectFilesNotFolder: 'amAssignableItem.modals.addContent.uploadFileStep.validation.selectFilesNotFolder'.tr(),
    tSecondsLeft: 'amAssignableItem.modals.addContent.uploadFileStep.secondsLeft'.tr(),
    tUploadingOf: 'amAssignableItem.modals.addContent.uploadFileStep.uploadingOf'.tr(),

    tCategoryTextFieldPlaceholder: 'amAssignableItem.modals.addContent.assignPropertiesStep.categoryInputPlaceholder'.tr(),
    tCategoryDescriptionPlaceholder: 'amAssignableItem.modals.addContent.assignPropertiesStep.categoryDescriptionPlaceholder'.tr(),
    tEnterPassphrasePlaceholder: 'amAssignableItem.modals.addContent.assignPropertiesStep.enterPassphrasePlaceholder'.tr(),
    tConfirmPassphrasePlaceholder: 'amAssignableItem.modals.addContent.assignPropertiesStep.confirmPassphrasePlaceholder'.tr(),
    tPassphraseNotMatched: 'amAssignableItem.modals.addContent.assignPropertiesStep.validation.passphraseNotMatched'.tr(),

    tPicturesOption: 'amAssignableItem.modals.addContent.assignPropertiesStep.categoryMenu.options.pictures'.tr(),
    tDocumentsOption: 'amAssignableItem.modals.addContent.assignPropertiesStep.categoryMenu.options.documents'.tr(),
    tMultimediaOption: 'amAssignableItem.modals.addContent.assignPropertiesStep.categoryMenu.options.multimedia'.tr(),
    tOtherOption: 'amAssignableItem.modals.addContent.assignPropertiesStep.categoryMenu.options.other'.tr(),

    successMessage: null,
    errorMessage: null,

    passphraseErrorMessage: null,
    hintMessage: null,

    isSaveDisabled: true,
    isContinueDisabled: true,
    isBackDisabled: false,

    existingContent: null,
    displayName: null,

    newContentFiles: null,
    nameOfFile: null,

    // Properties and functions associated with assigning file properties
    // ===============================

    // The following properties are actually file specific, but via the wizard the user can only
    // specify a single set property values that are initially applied to all the files and may
    // subsequently be overridden due to file type specific constraints.
    categories: Em.A([]),
    savedCategory: null,
    selectedCategory: '',
    description: '',

    isAllowFileToLeaveAbsSafeDisabled: false,
    allowFileToLeaveAbsSafe: false,

    isCanEmailDisabled: true,
    isCanPrintDisabled: true,
    canEmail: false,
    canPrint: false,

    passphrase: '',
    verifyPassphrase: null,
    dummyPassphrase: '    ',
    hashedPassphrase: null,

    wifiOnlyDownload: false,

    isEditing: false,

    initPropertySelections: function () {
      if (this.get('categories').length === 0) { this.set('categories', this.getBuiltInCategories()); }

      // Note: the Ember Select control resets to no selected item when the view is reloaded (e.g., when go Back
      // then Continue). Explicitly resetting the bound selectedCategory value here doesn't seem to have any effect
      // unless a small timeout is used as a workaround.
      setTimeout(this.setSelectedCategory, 20, this);

      if (typeof this.updateSaveContinueButtons === 'function') {
        this.updateSaveContinueButtons();
      }
    },

    categoryTextFieldPlaceholder: function () {
      return this.get('tCategoryTextFieldPlaceholder').toString();
    }.property(),

    categoryDescriptionPlaceholder: function () {
      return this.get('tCategoryDescriptionPlaceholder').toString();
    }.property(),

    getBuiltInCategories: function () {
      return [
        this.get('tDocumentsOption').toString(),
        this.get('tMultimediaOption').toString(),
        this.get('tPicturesOption').toString(),
        this.get('tOtherOption').toString()
      ];
    },

    getCategoryForFile: function (fileName) {
      var documentFileTypes = ['PDF', 'TXT', 'TEXT', 'RTF', 'RTFD', 'DOC', 'DOCX', 'DOCM', 'PAGES', 'HTML', 'HTM',
        'WEBARCHIVE', 'XML', 'XSL', 'XLS', 'XLSX', 'XLSM', 'XLSB', 'NUMBERS', 'PPT', 'PPTX', 'PPS', 'PPSX', 'KEY'];

      var multimediaFileTypes = ['MPG', 'MPEG', 'M4V', 'MOV', 'MQV', 'QT', 'AVI', 'AIF', 'AIFF', 'AIFC', 'CDDA',
        'AAC', 'WAVE', 'WAV', 'BWF', 'M4P', 'M4A', 'M4B', 'MP3', 'MP4', '3GP', '3GPP', '3G2', '3GP2', 'AMR', 'SWA', 'M2V'];

      var pictureFileTypes = ['JPG', 'JPEG', 'PNG', 'TIF', 'TIFF', 'BMP', 'GIF'];

      var extIndex = fileName.lastIndexOf('.');

      if (extIndex !== -1) {
        var fileExt = (fileName.substring(extIndex + 1, fileName.length)).toUpperCase();
        for (var i = 0; i < documentFileTypes.length; i++) {
          if (fileExt === documentFileTypes[i]) { return this.get('tDocumentsOption').toString(); }
        }

        for (var i = 0; i < multimediaFileTypes.length; i++) {
          if (fileExt === multimediaFileTypes[i]) { return this.get('tMultimediaOption').toString(); }
        }

        for (var i = 0; i < pictureFileTypes.length; i++) {
          if (fileExt === pictureFileTypes[i]) { return this.get('tPicturesOption').toString(); }
        }
      }
      return this.get('tOtherOption').toString();
    },

    // Toggle the selected category value to force the Ember Select control to update the displayed
    // selection to the bound selectedCategory value, since otherwise it reverts the selected value
    // to the 1st item in the selection list.
    setSelectedCategory: function (self) {

      // If the user has already selected a category, restore that.
      var category = self.get('savedCategory');
      if (category !== null) {
        self.set('selectedCategory', category);
      } else {

        // Otherwise, set the default selection according to the file type.
        var fileName = self.get('newContentFiles')[0].srcFile.name;
        category = self.getCategoryForFile(fileName);

        if (category !== null) {
          self.set('selectedCategory', category);
        }
      }
    },

    savePropertySelections: function () {
      this.set('savedCategory', this.get('selectedCategory'));
    },

    enterPassphrasePlaceholder: function () {
      return this.get('tEnterPassphrasePlaceholder').toString();
    }.property(),

    confirmPassphrasePlaceholder: function () {
      return this.get('tConfirmPassphrasePlaceholder').toString();
    }.property(),

    onVerifyPassphraseChanged: function () {
      if (typeof this.updateSaveContinueButtons === 'function') {
        this.updateSaveContinueButtons();
      }
    }.observes('verifyPassphrase'),

    onAllowFileToLeaveAbsSafeChanged: function () {
      // Options to allow the user to e-mail file and allow user to print file are only applicable
      // if the file is allowed to leave Absolute Safe.
      if (this.allowFileToLeaveAbsSafe == false) {
        this.set('canEmail', false);
        this.set('canPrint', false);
      }
      this.set('isCanEmailDisabled', !this.allowFileToLeaveAbsSafe);
      this.set('isCanPrintDisabled', !this.allowFileToLeaveAbsSafe);
    }.observes('allowFileToLeaveAbsSafe'),

    // Handler for changes to the display name used when one ot more files have a name that is a duplicate of
    // of another existing content item in AM. A timer is used to debounce edits. When it expires we issue a
    // query to check if the new name is now unique.
    onDisplayNameChanged: function(file) {
      if (!Em.isNone(window.displayNameChangedTimeout)) {
        clearTimeout(window.displayNameChangedTimeout);
      }

      window.displayNameChangedTimeout = setTimeout(this.checkDisplayName, 500, file, this);
    }.observes('displayName'),

    // Check if the new display name is empty. If not, issue a query to search for the edited display name.
    // If the query returns a non-empty results set, then it's a duplicate name.
    //
    // @param file the file whose display name has been changed
    //
    checkDisplayName: function(file, self) {
      var controller = self ? self : this;
      var errorMessage = null;

      if (Em.isEmpty(Formatter.trim(file.displayName))) {
        file.setProperties({
          hasNameError: true,

          // Set background to red
          progressBarStyle: controller.get('progressBarBaseStyle') + 'width: 100%; background-color: ' + controller.ERROR_COLOR
        });

        errorMessage = controller.createNameErrorMessage(file.displayName);

        controller.setProperties({
          isContinueDisabled: true,
          isSaveDisabled: true
        });

      } else if (file.displayName === controller.get('existingContent.data.name')) {

        // Skip the duplicate name check when editing existing content and the name is changed back to its original value
        file.set('hasNameError', false);
        controller.set('isSaveDisabled', Em.isEmpty(controller.get('passphraseErrorMessage')));

      } else {
        // Check for duplicate name in this batch of files for upload
        var duplicateNameInFilesForUpload = false;
        var files = controller.get('files');

        if (!Em.isEmpty(files)) {
          files.forEach(function(fileIn) {
            if (fileIn.displayName === file.displayName && fileIn !== file && controller.get('isEditing') !== true) {
              duplicateNameInFilesForUpload = true;

              file.setProperties({
                hasNameError: true,

                // Set background to red
                progressBarStyle: controller.get('progressBarBaseStyle') + 'width: 100%; background-color: ' + controller.ERROR_COLOR
              });

              controller.setProperties({
                nameOfFile: file.displayName,
                isContinueDisabled: true,
                isSaveDisabled: true
              });

              errorMessage = controller.get('tDuplicateNameErrorMessage');
            }
          });
        }

        if (!duplicateNameInFilesForUpload) {

          // Issue query to check if content with that name already exists on the server
          var query = Query.Search.create({
            searchAttr: 'name',
            searchFilter: Formatter.trim(file.displayName)
            // trim so that we also consider it a duplicate name if it only differs by leading or trailing spaces
          });

          AmData.get('stores.contentStore').acquire(null, query, function(datasource) {
            var searchName = datasource.get('query.searchFilter');
            var duplicateName = null;
            var files = this.get('files');

            files.forEach(function(fileIn) {
              if (Formatter.trim(fileIn.displayName) === searchName) {
                if (datasource.total === 0) {
                  fileIn.set('hasNameError', false);

                  if (Em.isEmpty(fileIn.uploadError) && fileIn.uploader.state === 'Completed') {

                    // Set background to green if file is uploaded
                    fileIn.set('progressBarStyle',
                      controller.get('progressBarBaseStyle') + 'width: 100%; background-color: ' + controller.SUCCESS_COLOR);
                  }
                } else {

                  // Check for a match our search name since the search returns substring matches and not exact matches
                  var foundMatch = false;

                  for (var j = 0; j < datasource.get('length') ; j++) {
                    var searchResult = datasource.objectAt(j);
                    if (searchName.toLowerCase() === (searchResult.get('content.data.name')).toLowerCase()) {
                      foundMatch = true;
                      duplicateName = fileIn.displayName;

                      // Set background to red
                      fileIn.set('progressBarStyle',
                        controller.get('progressBarBaseStyle') + 'width: 100%; background-color: ' + controller.ERROR_COLOR)
                    }
                  }
                  fileIn.set('hasNameError', foundMatch);

                  if (!foundMatch && Em.isEmpty(fileIn.uploadError) && !Em.isEmpty(fileIn.uploader) && fileIn.uploader.state === 'Completed') {

                    // Set background to green if file is uploaded
                    fileIn.set('progressBarStyle',
                      controller.get('progressBarBaseStyle') + 'width: 100%; background-color: ' + controller.SUCCESS_COLOR);
                  }
                }
              } else {
                if (fileIn.hasNameError) {
                  duplicateName = fileIn.displayName;
                }
              }
            });

            // Display the duplicate name error and if none, then any outstanding upload errors
            if (duplicateName !== null) {
              errorMessage = this.createNameErrorMessage(duplicateName);
            } else {
              files.forEach(function(fileIn) {
                if (!Em.isEmpty(fileIn.uploadError)) {
                  errorMessage = fileIn.uploadError;
                }
              })
            }

            controller.setProperties({
              errorMessage: errorMessage,
              isContinueDisabled: errorMessage !== null ||
                controller.get('filesUploadedCount') !== controller.get('files').length,
              isSaveDisabled: errorMessage !== null || Em.isEmpty(controller.get('selectedCategory')) ||
                !Em.isEmpty(controller.get('passphraseErrorMessage'))
            })

          }, null, controller, true); // We need to force this query in case the name is edited back to its original duplicate
        }
      }

      controller.set('errorMessage', errorMessage);
    },

    createNameErrorMessage: function(name) {
      var errorMessage = null;

      // Currently the only two error conditions are that the name is empty, or that its a duplicate
      if (Em.isEmpty(Formatter.trim(name))) {
        errorMessage = Em.isNone(this.get('existingContent')) ? this.get('tFileNameIsBlank') : this.get('tNameIsBlank');
      } else {
        this.set('nameOfFile', name);
        errorMessage = this.get('tExistOnServer') + (Em.isNone(this.get('existingContent')) ? ' ' + this.get('tRenameFileToContinue') : ' ' + this.get('tRenameItToSave'));
      }

      return errorMessage;
    },

    buildContentData: function (fileName, displayName, fileType, lastModifiedDate, category) {
      var ContentData = AmData.get('actions.AmContentActionData');

      var description = this.get('description');

      var pass = this.getProperties('passphrase dummyPassphrase hashedPassphrase'.w());
      var passphrase = pass.passphrase == null ? '' : pass.passphrase;

      // If editing content with an existing passphase that was not changed, then use the
      // original hashed value in the request
      if (pass.passphrase === pass.dummyPassphrase && !Em.isEmpty(pass.hashedPassphrase)) {
        passphrase = pass.hashedPassphrase;
      }

      var canLeaveApp = this.get('allowFileToLeaveAbsSafe');
      var canEmail = this.get('canEmail');
      var canPrint = this.get('canPrint');

      var transferOnWifiOnly = this.get('wifiOnlyDownload');

      var fileTypeFromName = '';
      var extIndex = fileName.lastIndexOf('.');
      if (extIndex !== -1) {
        fileTypeFromName = (fileName.substring(extIndex + 1, fileName.length)).toUpperCase();
      }

      var content = ContentData.create({
        fileName: fileName,
        displayName: displayName.trim(),
        description: description,
        category: category,
        fileModDate: lastModifiedDate,
        fileType: fileTypeFromName,

        canLeaveApp: canLeaveApp,
        canEmail: canEmail,
        canPrint: canPrint,

        transferOnWifiOnly: transferOnWifiOnly,
        passphrase: passphrase
      });

      if (this.get('existingContent')) {
        content.reopen({
          id: this.get('existingContent.data.id'),
          seed: this.get('existingContent.data.seed')
        });
      }

      return content;
    },

    shutdown: function () {
      var existingContent = this.get('existingContent');
      if (existingContent) {
        existingContent.release(this.get('lock'));
      }
    }
  });
});