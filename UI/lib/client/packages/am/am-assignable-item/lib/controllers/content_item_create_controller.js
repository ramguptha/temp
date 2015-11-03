define([
  'ember',
  'desktop',
  'formatter',
  'uploader',
  'help',
  'locale',

  'am-data',
  'packages/am/am-assignable-item-foundation',
  './content_item_build_base',

  '../views/new_content_upload_files_step_view',
  '../views/new_content_assign_properties_step_view',
  '../views/new_content_assign_to_policies_step_view',
  '../views/new_content_finish_view'
], function(
  Em,
  Desktop,
  Formatter,
  Uploader,
  Help,
  Locale,

  AmData,
  AmAssignableItemFoundation,
  ContentItemBuildBase,

  AmNewContentUploadFilesStepView,
  AmNewContentAssignPropertiesStepView,
  AmNewContentAssignToPoliciesStepView,
  AmNewContentFinishView
) {

  // Content Item Create Content Controller
  // ==================================
  //
  // This controller contains the wizard for creating new contents.

  return AmAssignableItemFoundation.ContentItemBaseController.extend(ContentItemBuildBase, AmAssignableItemFoundation.ContentPolicyAssignmentBase, {

    heading: 'amAssignableItem.modals.addContent.heading'.tr(),
    headingIconClass: 'icon-add-files',

    ERROR_COLOR: '#F2DEDE',     // red
    SUCCESS_COLOR: '#ECFFF0',   // green
    PROGRESS_COLOR: '#d9edf7',  // blue

    tInProgressMessage: 'amAssignableItem.modals.addContent.inProgressMessage'.tr(),
    tSuccessMessage: 'amAssignableItem.modals.addContent.successMessage'.tr(),
    tErrorMessage: 'amAssignableItem.modals.addContent.errorMessage'.tr(),

    tSuccessUploadMessage: 'amAssignableItem.modals.addContent.uploadFileStep.successUploadMessage'.tr('numberOfUploadedFiles', 'numberOfTotalFiles'),

    numberOfUploadedFiles: null,
    numberOfTotalFiles: null,

    urlForHelp: Help.uri(1033),

    actions: {
      cancel: function() {
        this.cancelAll();
        this.send('closeModal');
      },

      done: function() {
        this.clear();
        this.send('closeModal');
      },

      onCancelFileUpload: function(context) {
        this.cancelFileUpload(context);
      },

      onConfirmAction: function() {
        this.get('wizard').send('next');
      },

      gotoPreviousStep: function() {
        this.get('wizard').send('prev');
      },

      gotoNextStep: function() {
        this.get('wizard').send('next');
      }
    },

    // Views
    // -----
    //
    // One view per wizard 'step'.

    uploadFilesStepView: AmNewContentUploadFilesStepView,
    assignPropertiesStepView: AmNewContentAssignPropertiesStepView,
    assignToPoliciesStepView: AmNewContentAssignToPoliciesStepView,
    finishView: AmNewContentFinishView,

    // Wizard State
    // ------------
    //
    // This statemanager drives the wizard.

    wizard: function() {
      return Em.StateManager.create({
        initialState: 'step1',
        controller: this,

        states: {
          step1: Em.State.create({
            next: function(manager) {
              manager.transitionTo('step2');
            }
          }),

          // Step 2 - Assign properties - category, passphrase, permissions etc
          step2: Em.State.create({
            enter: function() {
              // Populate the category selection list and set the default selected category according to the file type
              this.get('parentState.controller').initPropertySelections();
              this.get('parentState.controller').set('paused', false);
            },

            next: function(manager) {
              manager.reopen();
              this.get('parentState.controller').savePropertySelections();
              this.get('parentState.controller').set('paused', false);

              manager.transitionTo('step3');
            },

            prev: function(manager) {
              this.get('parentState.controller').savePropertySelections();
              this.get('parentState.controller').set('isContinueDisabled', false);

              manager.transitionTo('step1');
            }
          }),

          // Step 3 - Assign to policies and specify availability
          step3: Em.State.create({
            next: function(manager) {
              manager.transitionTo('finish');
            },

            prev: function(manager) {
              this.set('parentState.controller.assignedTime', this.get('parentState.controller.formattedTime'));
              manager.transitionTo('step2');
            }
          }),

          // Finish - Send new mobile content items with their properties and policy assignments to the server
          finish: Em.State.create({
            enter: function(manager) {
              this.get('parentState.controller').set('urlForHelp', null);

              this.submitNewContent();
            },

            submitNewContent: function() {
              // Use animation gif for now instead of Spinner ...
              var controller = this.get('parentState.controller');
              controller.set('submitStatusMsg', controller.get('tInProgressMessage'));
              controller.submitNewContent(controller.onAddContentDone, controller.onAddContentError);
            },

            prev: function(manager) {
              manager.get('controller').set('errorMessage', null);
              manager.transitionTo('step3');
            },

            tryAgain: function(manager) {
              manager.get('controller').set('errorMessage', null);
              this.submitNewContent(manager);
            }
          })
        }
      });
    }.property(),

    showingStep1: Em.computed.equal('wizard.currentState.name', 'step1'),
    showingStep2: Em.computed.equal('wizard.currentState.name', 'step2'),
    showingStep3: Em.computed.equal('wizard.currentState.name', 'step3'),
    showingFinish: Em.computed.equal('wizard.currentState.name', 'finish'),

    // End of the Wizard setup
    // -----------------------

    onShowModal: function() {
      this._super();

      this.set('selectionController', this.PolicySelectionController.create({ parentController: this }));

      this.get('wizard').transitionTo('step1');
    },

    // Properties and functions associated with uploading files
    // ============================================
    //
    uploadFormClass: 'upload-nofiles',
    progressBarBaseStyle: 'position:absolute; top: 0; left: 0; height:100%; ',   // TODO - move to css ?

    content: Em.A([
      Em.Object.create({ name: 'newContentFiles' })
    ]),

    files: Em.A([]),
    filesUploadedCount: 0,

    inputFilesForUpload: null,
    inputFileCount: 0,

    pendingUploads: Em.A([]),

    // Functions associated with display and uploading of selected files
    // =========================

    // Initiate upload a batch of one or more input files selected either by drag & drop
    // (in which case we first test if they are actually uploadable - i.e., not directories), or via browse.
    //
    // @param inputFiles the selected input files to be uploaded
    // @param testUploadable whether to test if the files are uploadable first
    //
    uploadFiles: function(inputFiles, testUploadable) {

      // Filter out duplicate files
      var inputFilesForUpload = Em.A([]);
      var files = this.get('files');
      var self = this;

      for (var i = 0; i < inputFiles.length; i++) {
        var duplicateFile = false;
        if (inputFilesForUpload.contains(inputFiles[i])) {
          duplicateFile = true;
        }
        else {
          files.forEach(function(file) {
            duplicateFile = file.srcFile.name === inputFiles[i].name;
          })
        }

        if (!duplicateFile) {
          inputFilesForUpload.pushObject(inputFiles[i]);
        }
      }

      if (!Em.isEmpty(inputFilesForUpload)) {
        this.get('pendingUploads').clear();
        this.setProperties({
          inputFilesForUpload: inputFilesForUpload,
          inputFileCount: 0,
          nameOfFile: null
        });

        if (testUploadable) {

          // kick off test of the first one.
          this.testFileIsUploadable(inputFilesForUpload[0]);
        }
        else {

          // Skip test by just returning uploadable is true for all files
          inputFilesForUpload.forEach(function(file) {
            self.onFileUploadableResult(file, true, false);
          });
        }
      }
    },

    // Process results of the test of whether the file is uploadable. If so we create Chunck Uploader, start the
    // upload and add it to our array of 'newContentFiles' and create an observer for edits to its display name.
    //
    // @param file          The input files that has been tested
    // @param isUploadable  The result of the test - whether it is uploadable or not
    // @param testNext      Whether we need to test the next file in the batch
    //
    onFileUploadableResult: function(file, isUploadable, testNext) {
      var self = this;

      var pendingUploads = this.get('pendingUploads');
      var inputFileCount = this.get('inputFileCount') + 1;
      var inputFilesForUpload = this.get('inputFilesForUpload');

      if (isUploadable) {
        pendingUploads.pushObject(file);
      }

      this.set('inputFileCount', inputFileCount);

      if (inputFileCount === inputFilesForUpload.length) {

        pendingUploads.forEach(function(pendingUpload) {
          // Disable the Continue button while file upload is in progress
          self.set('isContinueDisabled', true);

          var file = pendingUpload;
          var uploader = new Uploader.ChunkedFileUploader(
            file, {}, self, self.onFileUploaded, self.onUploadError, self.onUploadProgress);

          // The default new content display name is the file name without the extension.
          var extIndex = file.name.lastIndexOf('.');
          var displayName = extIndex !== -1 ? file.name.substring(0, extIndex) : file.name;

          var newFile = Em.Object.create({
            fileTypeIcon: self.getIcon(file),
            displayName: displayName,
            hasNameError: false,
            formattedSize: Formatter.formatBytes(file.size),
            fileInnerContainerClass: "file-inner-container",
            progressBarStyle: self.progressBarBaseStyle + 'width: 0%; background-color: ' + self.PROGRESS_COLOR,
            secsLeft: "",
            srcFile: file,
            uploader: uploader,
            uploadError: null,
            controller: self
          });

          Em.addObserver(newFile, 'displayName', self, self.onDisplayNameChanged);
          self.get('files').pushObject(newFile);

          // Check if there is any existing content with the same name
          self.checkDisplayName(newFile);

          // Start upload immediately
          uploader.start();
        });

        var selectionErrorMessage = this.get('tSelectFilesNotFolder');

        if (!Em.isEmpty(pendingUploads.length)) {
          this.set('newContentFiles', this.get('files'));

          if (!Em.isEmpty(this.get('files'))) {
            this.setProperties({
              hintMessage: this.get('tHintMessage'),
              uploadFormClass: "upload-withfiles"
            });

            if (this.get('successMessage') === null) {
              this.set('successMessage', this.get('tUploadingMessage'));
            }

            if (this.get('errorMessage') === selectionErrorMessage) {
              this.set('errorMessage', null);
            }
          }

          // Force scroll to bottom so that newly added files are visible
          setTimeout(function() {
            var fileTableWrapper = document.getElementById("fileTableWrapper");
            fileTableWrapper.scrollTop = fileTableWrapper.scrollHeight;
          }, 200);

          this.get('pendingUploads').clear();

        } else if (this.get('errorMessage') === null) {
          this.set('errorMessage', selectionErrorMessage);
        }

      } else if (testNext) {

        // kick off the next one.
        this.testFileIsUploadable(inputFilesForUpload[inputFileCount]);
      }
    },

    // Test if the 'file' object is a readable file for upload, as opposed to a directory.
    //
    // Unfortunately the Javascript File API object doesn't have any indication of whether it is
    // a file or a directory. The 'type' property is empty for directories, but can also be empty
    // for other files which have no extension or extensions that it doesn't recognize, so this is insufficient.
    //
    // Instead we test if we can actually read the 1st byte of data using a FileReader object. This
    // returns an error code for directories.
    //
    // WARNING: Don't trust the description above. Folder uploading is not a web standard and is only ( badly ) implemented
    // by some browsers ( chrome ). As such, there's no good/correct way to distinguish files from directories. The code
    // below only ( kind of ) works because directories have a size of 0 and thus cannot be 'loaded' into the Filereader.
    // Consequently we're incorrectly flagging empty files as directories.
    //
    // @param file            The 'file' object to be uploaded
    //
    testFileIsUploadable: function(file) {

      if (Em.isNone(file)) {

        // Handles IE 10 case where selecting a directory returns a null file
        this.onFileUploadableResult(file, false, true);
      } else {

        var slice_method = 'slice';
        if ('mozSlice' in file) {
          slice_method = 'mozSlice';
        } else if ('webkitSlice' in file) {
          slice_method = 'webkitSlice';
        }

        // Read in 1st byte of the file as a binary string.
        var byte = file[slice_method](0, 1);
        var reader = new FileReader();

        var self = this;
        var testedFile = file;

        reader.onload = function(evt) {
          self.onFileUploadableResult(testedFile, evt.loaded === 1, true);
        };

        reader.onerror = function(evt) {
          self.onFileUploadableResult(testedFile, false, true);
        };

        try {
          reader.readAsArrayBuffer(byte);
        } catch (NS_ERROR_FILE_ACCESS_DENIED) { // added for firefox support
          self.onFileUploadableResult(testedFile, false, true);
        }
      }
    },

    // Handler for notification that a file upload has completed.
    //
    // @param self        This wizard
    // @param file        The file that has been uploaded
    // @param statusCode  The HTTP status code that was returned
    //
    onFileUploaded: function(self, file) {
      var files = self.get('files');

      // Clear status of number of bytes uploaded and estimated time remaining and update background to green
      files.forEach(function(fileIn) {
        if (file === fileIn.srcFile) {
          var color = fileIn.hasNameError ? self.ERROR_COLOR : self.SUCCESS_COLOR;

          fileIn.setProperties({
            formattedSize: Formatter.formatBytes(file.size),
            secsLeft: '',
            uploadError: null,
            fileInnerContainerClass: "file-inner-container",
            progressBarStyle: self.get('progressBarBaseStyle') + 'width: 100%; background-color: ' + color
          });
        }
      });

      // Update status message for the number of files successfully uploaded
      self.filesUploadedCount++;
      self.updateFilesUploadedMessage();

      // Continue button is only enabled once all selected files have been uploaded and there are no naming errors
      var hasNameError = false;

      files.forEach(function(fileIn) {
        if (fileIn.hasNameError) {
          hasNameError = true;
        }
      });

      if (hasNameError === false && self.get('filesUploadedCount') === files.length) {
        self.set('isContinueDisabled', false);
      }
    },

    // Handler for notification of a file upload error.
    onUploadError: function(self, file, details, textStatus, errorThrown) {
      var jqXHR = details.jqXHR;

      // Update background to red and display error message
      self.get('files').forEach(function(fileIn) {
          if (file === fileIn.srcFile) {
            var responseMessage = Formatter.formatErrorResponse(jqXHR.responseText);
            self.set('nameOfFile', file.name);
            var errorMessage = self.get('tUploadFailed') + ' ' + details.status + " - " +
              errorThrown + ' : ' + responseMessage;

            fileIn.setProperties({
              fileInnerContainerClass: "file-inner-container",
              progressBarStyle: self.get('progressBarBaseStyle') + 'width: 100%; background-color: ' + self.ERROR_COLOR,
              uploadError: errorMessage
            });

            self.setProperties({
              nameOfFile: file.name,
              errorMessage: errorMessage,
              successMessage: null
            })
          }
       });

      // If session timed out, go to login screen
      if (details.status === 401) {
        document.location = '../login/?sessionTimedOut=true';
      }
    },

    // Handler for notification of a file upload progress.
    onUploadProgress: function(self, file, bytesUploaded, secsLeft) {
      // Update progress bar and status to show amount uploaded and estimated seconds left, unless it's
      // almost complete
      if (secsLeft > 0.5) {
        self.get('files').forEach(function(fileIn) {
          if (file === fileIn.srcFile) {
            var percentComplete = (bytesUploaded / fileIn.srcFile.size) * 100;
            var color = fileIn.hasNameError ? self.ERROR_COLOR : self.PROGRESS_COLOR;

            fileIn.setProperties({
              fileInnerContainerClass: "file-inner-container-uploading",
              formattedSize: Formatter.formatBytes(bytesUploaded) + self.get('tUploadingOf') + Formatter.formatBytes(fileIn.srcFile.size),
              secsLeft: secsLeft.toFixed(0) + ' ' + self.get('tSecondsLeft'),
              progressBarStyle: self.get('progressBarBaseStyle') + 'width: ' + percentComplete + '%; background-color: ' + color
            })
          }
        });
      }
    },

    cancelFileUpload: function(uploader) {
      // Remove the file from the array of files for upload
      var fileIndex = -1;
      var files = this.get('files');

      for (var i = 0; i < files.length; i++) {
        if (uploader.file === files[i].srcFile) {
          fileIndex = i;
          break;
        }
      }

      if (fileIndex !== -1) {
        Em.removeObserver(files[fileIndex], 'displayName', this, this.onDisplayNameChanged);
        files.removeAt(fileIndex);
      }

      this.set('newContentFiles', files);

      // Clear any error messages etc if there are no remaining files for upload
      if (files.length === 0) {
        this.setProperties({
          hintMessage: null,
          successMessage: null,
          errorMessage: null,
          isContinueDisabled: true,
          savedCategory: null,
          uploadFormClass: "upload-nofiles"
        });

      }

      // Cancel in progress upload
      if (uploader.state === 'In progress')
        uploader.cancel();

      // If already uploaded, update count
      else if (uploader.state === 'Completed') {
        this.filesUploadedCount--;
        this.updateFilesUploadedMessage();
      }

      this.updateErrors();
    },

    // update the error messages based on the current state
    updateErrors: function() {
      var files = this.get('files'), fileNameCount ={}, self = this, errorMessage = null;

      // calculate how many occurences we have of each file
      files.forEach(function(file) {
        var displayName = file.displayName;

        if(fileNameCount[displayName]) {
          fileNameCount[displayName]++;
        } else {
          fileNameCount[displayName] = 1;
        }
      });

      // remove resolved errors
      files.forEach(function(file) {
        var displayName = file.displayName;

        if(fileNameCount[displayName] === 1 && file.hasNameError) {
          file.set('hasNameError', false);
          file.set('progressBarStyle', self.get('progressBarBaseStyle') + 'width: 100%; background-color: ' + self.SUCCESS_COLOR);
        }
      });

      // try to find any remaining errors to display
      files.forEach(function(fileIn) {
        if (fileIn.hasNameError) {
          errorMessage = self.createNameErrorMessage(fileIn.displayName);
        }
      });

      if (errorMessage === null) {
        files.forEach(function(fileIn) {
          if (!Em.isEmpty(fileIn.uploadError)) {
            errorMessage = fileIn.uploadError;
          }
        });
      }

      this.set('errorMessage', errorMessage);

      // Enable the Continue button if cancelling this upload means that all the remaining files have been uploaded
      // and there are no outstanding errors
      if (files.length > 0 && (files.length === this.get('filesUploadedCount')) && errorMessage === null) {
        this.set('isContinueDisabled', false);
      }
    },

    cancelAll: function() {
      // Cancel any uploads that are in progress.
      // (The server will do server-side clean up to purge any uploaded or partially uploaded files)
      for (var i = 0, file; file = this.get('files')[i]; i++) {
        if (file.uploader.state === 'In progress')
          file.uploader.cancel();
      }

      this.clear();
    },

    updateFilesUploadedMessage: function() {
      if (this.get('filesUploadedCount') > 0) {
        this.setProperties({
          numberOfUploadedFiles: this.get('filesUploadedCount'),
          numberOfTotalFiles: this.get('files').length
        });
        // Do not move this line up to 'this.setProperties', Ember does not display the right successMessage based on numberOfUploadedFiles and numberOfTotalFiles
        this.set('successMessage', this.get('tSuccessUploadMessage'));
      } else {
        this.setProperties({
          numberOfUploadedFiles: null,
          numberOfTotalFiles: null,
          successMessage: null
        });
      }
    },

    getIcon: function(file) {
      var fileExt = '.';
      var extIndex = file.name.lastIndexOf('.');

      if (extIndex !== -1) {
        fileExt = (file.name.substring(extIndex + 1, file.name.length)).toLowerCase();
      }

      var iconPath = AmData.get('urlRoot') + '/api/content/icons/32-' + fileExt + '.png';
      var category = this.getCategoryForFile(file.name);

      if (category !== null) {
        iconPath += '?category=' + category;
      }

      return iconPath;
    },

    // Properties and functions associated with submitting content to server
    // =================================
    //
    submitStatusMsg: null,
    submitInProgress: false,
    isDoneDisabled: true,
    errorDetails: null,

    progressBarStyle: '',
    progressPercentComplete: 0,

    onSubmitNewContentProgressUpdate: function(percentComplete) {
      this.setProperties({
        progressBarStyle: this.get('progressBarBaseStyle') + 'width: ' + percentComplete + '%; background-color: ' + this.SUCCESS_COLOR,
        progressPercentComplete: percentComplete
      });
    },

    // Construct and submit new content using /api/content/batch POST request.
    /* This request has the form:
     * {
          *  "newFiles":[{file_1},...{file_N}],
          *  "assignToPolicies":[{policyAssignment_1},...{policyAssignment_N}]
          * }
     * Where file_N is e.g.:
     * {
     *  "id":376,  (not specified for new files)
     *  "Seed":7,  (not specified for new files)
     *  "fileName":"MyMediaFile.pdf",
     *  "displayName":"My Media File",
     *  "description":"This is a fake media file.",
     *  "category":"Documents",
     *  "fileModDate":"2012-10-12T22:39:31Z",
     *  "fileType":"PDF",
     *  "canLeaveApp":true,
     *  "canEmail":false,
     *  "canPrint":false,
     *  "transferOnWifiOnly":true,
     *  "passphraseHash":"5baa61e4c9b93f3f0682250b6cf8331b7ee68fd8"
     * }
     * and policyAssignment_N is e.g.:
          * {
     *  "uniqueID": "BCB892A4-CE3E-4763-99B9-A990E20A1E1E",
          *  "assignmentType": 1,
          *  "availabilitySelector": 2,
          *  "startTime":"2012-10-18T19:01:00Z",
          *  "endTime":"2012-10-19T20:12:00Z"
          * }
     */
    submitNewContent: function(okHandler, errHandler) {
      var self = this;

      this.setProperties({
        submitInProgress: true,
        progressBarStyle: this.get('progressBarBaseStyle') + 'width: 0%; background-color: ' + this.SUCCESS_COLOR,
        progressPercentComplete: 0
      });

      var PolicyToContentMapData = AmData.get('actions.AmMobilePolicyToContentMapActionData'),
        CreateAction = AmData.get('actions.AmContentCreateBatchAction');

      var policyIds = this.get('selectionController.selections');
      var assignmentType = self.get('selectedAssignmentType.value'), availability = this.buildAvailabilityActionData();

      var description = this.get('description'), selectedCategory = this.get('selectedCategory');

      var pass = this.getProperties('passphrase dummyPassphrase hashedPassphrase'.w());
      var passphrase = pass.passphrase === null ? '' : pass.passphrase;

      // If editing content with an existing passphase that was not changed, then use the
      // original hashed value in the request
      if (pass.passphrase === pass.dummyPassphrase && !Em.isEmpty(pass.hashedPassphrase)) {
        passphrase = pass.hashedPassphrase;
      }

      var canEmail = this.get('canEmail'), canPrint = this.get('canPrint');

      var action = CreateAction.create({
        content: this.get('newContentFiles').map(function(file) {
          var fileName = file.srcFile.name;

          var fileType = '';
          var extIndex = fileName.lastIndexOf('.');
          if (extIndex !== -1) {
            fileType = (fileName.substring(extIndex + 1, fileName.length)).toUpperCase();
          }

          return self.buildContentData(fileName, file.displayName, fileType, file.srcFile.lastModifiedDate, selectedCategory);
        }),

        policyAssignments: policyIds.map(function(id) {
          return PolicyToContentMapData.PolicyAssignment.create({
            policyId: id,
            assignmentType: assignmentType,
            availability: availability
          });
        }),

        onSuccess: function(data) {
          self.set('submitInProgress', false);
          okHandler(self, data);
        },

        onError: function(ajaxError) {
          self.set('submitInProgress', false);
          errHandler(self, ajaxError);
        },

        progressUpdateContext: self,
        progressUpdateCallback: this.onSubmitNewContentProgressUpdate
      });

      action.invoke();

      return true;
    },

    onAddContentDone: function(self) {
      self.setProperties({
        submitStatusMsg: self.get('tSuccessMessage'),
        errorMessage: null,
        isDoneDisabled: false
      });
    },

    onAddContentError: function(self, details) {
      var jqXHR = details.jqXHR;
      var errorDetails = Formatter.formatErrorResponse(jqXHR.responseText);

      if(Em.isEmpty(errorDetails)) {
        if(jqXHR.statusText === 'timeout') {
          errorDetails = Locale.renderGlobals('shared.modals.errors.timeout').toString();
        } else {
          errorDetails = Locale.renderGlobals('shared.modals.errors.generic').toString();
        }
      }

      self.setProperties({
        submitStatusMsg: null,
        errorMessage: self.get('tErrorMessage'),
        errorDetails: errorDetails
      });
    },

    // Update checkboxes / continue buttons on some fields changes
    updateSaveContinueButtons: function() {
      if(this.get('paused')) {
        return;
      }

      var passPhraseEmpty = Em.isEmpty(this.get('passphrase')) && Em.isEmpty(this.get('verifyPassphrase')),
        passPhraseMatches = this.get('passphrase') === this.get('verifyPassphrase');

      var categoryIsEmpty = Em.isEmpty(this.get('selectedCategory'));

      if ((passPhraseEmpty || passPhraseMatches) && !categoryIsEmpty) {
        this.setProperties({
          isContinueDisabled: false,
          isSaveDisabled: this.get('files.length') > 0 && this.get('files')[0].hasNameError,
          passphraseErrorMessage: null
        });
      } else {
        this.setProperties({
          isContinueDisabled: true,
          isSaveDisabled: true,
          passphraseErrorMessage: (!passPhraseEmpty && !passPhraseMatches) ? this.get('tPassphraseNotMatched') : ''
        });
      }
      if (passPhraseEmpty) {
        this.set('isAllowFileToLeaveAbsSafeDisabled', false);
      } else {
        this.setProperties({
          allowFileToLeaveAbsSafe: false,
          isAllowFileToLeaveAbsSafeDisabled: true
        });
      }
    }.observes('passphrase', 'selectedCategory'),

    clear: function () {
      this.get('files').clear();
      this.set('filesUploadedCount', 0);

      this._super();
    }
  });
});
