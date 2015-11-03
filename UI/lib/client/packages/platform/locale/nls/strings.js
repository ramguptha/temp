define({
  root: {
    shared: {
      homeTitle: 'Home',

      true: 'Yes',
      false: 'No',

      time: {
        now: 'Now',
        today: 'Today',

        am: 'AM',
        amAbbr: 'A',

        pm: 'PM',
        pmAbbr: 'P',

        time: 'Time',
        hour: 'Hour',
        hours: 'Hours',
        minute: 'Minute',
        minutes: 'Minutes',
        day: 'Day',
        days: 'Days',
        second: 'Second',
        millisecond: 'Millisecond',
        microsecond: 'Microsecond',

        timezone: 'Time Zone',
        utc: '(UTC)'
      },

      dayOfTheWeek : {
          sun: 'Sun',
          mon: 'Mon',
          tue: 'Tue',
          wed: 'Wed',
          thu: 'Thu',
          fri: 'Fri',
          sat: 'Sat'
      },

      ordinal: {
          t1st: '1st',
          t2nd: '2nd',
          t3rd: '3rd',
          t4th: '4th',
          t5th: '5th',
          t6th: '6th',
          t7th: '7th',
          t8th: '8th',
          t9th: '9th',
          t10th: '10th',
          t11th: '11th',
          t12th: '12th',
          t13th: '13th',
          t14th: '14th',
          t15th: '15th',
          t16th: '16th',
          t17th: '17th',
          t18th: '18th',
          t19th: '19th',
          t20th: '20th',
          t21st: '21st',
          t22nd: '22nd',
          t23rd: '23rd',
          t24th: '24th',
          t25th: '25th',
          t26th: '26th',
          t27th: '27th',
          t28th: '28th'
      },

      buttons: {
        ok: 'OK',
        done: 'Done',
        cancel: 'Cancel',
        close: 'Close',
        back: 'Back',
        continue: 'Continue',
        edit: 'Edit',
        save: 'Save',
        saveAs: 'Save As',
        saveAndActivate: 'Save and Activate',
        saveAsReport: 'Save as Report',
        delete: 'Delete',
        move: 'Move',
        revert: 'Revert',
        remove: 'Remove',
        tryAgain: 'Try Again',
        addFilter: 'Add Filter',
        activate: 'Activate',
        deactivate: 'Deactivate',
        editFilter: 'Edit Filter',
        actions: 'Actions',
        copy: 'Duplicate',
        showHideColumns: 'Show/Hide Columns',
        overview: 'Overview',
        detailedView: 'Detailed view',
        filterToggleButton: {
          showFilter: 'Show Filter',
          hideFilter: 'Hide Filter'
        },
        activateToggleButton: {
          setAsActive: 'Activate',
          deactivate: 'Deactivate'
        },
        showDetailsToggleButton: {
          showDetails: 'Show Details',
          hideDetails: 'Hide Details'
        },
        advancedSearchButton: {
          activate: 'Advanced Search',
          deactivate: 'Cancel Advanced Search'
        }
      },

      na: 'n/a',

      id: 'ID',
      accountId: 'Account ID',
      identifier: 'Identifier',
      userName: 'User Name',
      username: 'Username',
      workgroup: 'Workgroup',
      serialNumber: 'Serial Number',
      assetNumber: 'Asset Number',
      detectedAssetNumber: 'Detected Asset Number',
      departmentName: 'Department Name',
      deviceName: 'Device Name',
      fullDeviceName: 'Full Device Name',
      make: 'Make',
      model: 'Model',
      assignedEmail: 'Assigned Email',
      assignedUsername: 'Assigned Username',
      phoneNumber: 'Phone Number',
      osVersion: 'OS Version',
      agentStatus: 'Agent Status',
      dateStolen: 'Date Stolen',

      active: 'Active',
      optional: 'optional',
      filters: 'Filters',
      inheritedFilters: 'Inherited Filters:',
      noFiltersSpecified: 'No filters specified',
      or: 'or',
      and: 'and',
      descriptionOptional: 'Description <i>(optional)</i>',
      predefined: 'Predefined',
      customized: 'Customized',
      editProperties: 'Edit Properties',
      baseline: '— —',
      charactersRemainingInField: 'Characters remaining: {{characterCount}}',
      doNotShowAgain: 'Do not show again',
      notDetected: 'Not Detected',
      unknownSystem: 'Unknown System',
      publisher: 'Publisher',
      unknownPublisher: 'Unknown Publisher',
      suite: 'Suite',
      application: 'Application',
      standaloneApplications: 'Standalone Applications',
      selectedDevice: 'Selected: {{deviceCount}}',
      selectedOf: '{{selectedDeviceCount}} selected of {{totalDeviceCount}}',
      noItemsToDisplay: 'There are no items to display',
      loadingMore: 'Loading ...',
      feedback: 'Feedback',
      unknownError: 'Unknown error',

      bytes: 'bytes',

      days: 'Days',
      hours: 'Hours',
      minutes: 'Minutes',
      weeks: 'Weeks',
      months: 'Months',
      years: 'Years',

      formatInterval: {
        yearLowCase: 'year',
        yearsLowCase: 'years',
        monthLowCase: 'month',
        monthsLowCase: 'months',
        dayLowCase: 'day',
        daysLowCase: 'days'
      },

      formatClockSpeed: {
        hertz: 'hertz',
        kHz: 'KHz',
        mHz: 'MHz',
        gHz: 'GHz'
      },

      formatBytes: {
        byte: 'byte',
        bytes: 'bytes',
        kB: 'KB',
        mB: 'MB',
        gB: 'GB',
        tB: 'TB'
      },

      formatSpeed: {
        bits: 'bit/s',
        kbits: 'Kbit/s',
        mbits: 'Mbit/s',
        gbits: 'Gbit/s',
        tbits: 'Tbit/s'
      },

      gettingStartedLink: 'Getting Started',
      reprocessingSoftwareReports: '<strong>Reprocessing</strong> - Software reports are updating.',
      results: 'Results',
      noResults: 'No Results',
      items: 'items',
      totalSummary: 'Result: {{total}} items',

      progressBar: {
        percentComplete: '% Complete'
      },

      search: {
        placeHolder: 'Search',
        caseSensitivePlaceHolder: 'Search is case-sensitive',
        searchDataFieldsPlaceHolder: 'Search data fields',
        globalSearchPlaceholder: 'Search',
        globalSearchDefaultSelectionPlaceholder: 'All',

        options: {
          all: 'All',
          name: 'Name',
          description: 'Description'
        }
      },

      editMenu: {
        label: 'Edit',

        options: {
          editProperties: 'Properties',
          duplicate: 'Duplicate',
          delete: 'Delete',
          deleteDeactivateFirst: 'Delete - Deactivate First'
        }
      },

      tooltips: {
        tooltipRemove: 'Remove this filter',
        appNotDefinedTooltip: 'Application not defined in a software catalog'
      },

      placeholders: {
        enterValue: 'Enter Value'
      },

      validationMessages: {
        requiredOrgName: 'The organization Name cannot be empty.',
        invalidEmailAddress: 'Enter a valid email address.',
        invalidMultiEmailAddress: 'Enter a valid email address. Separate multiple email addresses with a comma ( , ) or a semicolon ( ; ).',
        invalidPhoneNumber: 'Enter a valid phone number.',
        invalidMultiPhoneNumber: 'Enter a valid phone number. Separate multiple phone numbers with a comma ( , ).'
      },

      timeoutWarning: {
        header: 'Session Timeout warning',
        description: 'Your session expires in {{numberOfSeconds}} seconds due to inactivity. Click \'OK\' to keep this session alive.',
        message: 'Your session timed out. Please log in again.'
      },

      errorFormatter: {
        message: 'Message:',
        description: 'Description:',
        server: 'Server:',
        errorCode: 'Error code:'
      },

      errors: {
        usernameOrPasswordIsInvalid: 'The username or password is invalid. Please remember that passwords are case-sensitive.',
        showErrorDetail: 'Show details',
        hideErrorDetail: 'Hide details',
        unableLoadData: 'Unable to load data from the server.',
        nameExists: 'This group name already exists.',
        renameToContinue: 'Enter a unique name to continue.',
        renameToSaveChanges: 'Enter a unique name to save changes.',
        failedToFetchFromServer: 'Error: failed to retrieve the list of group names from the server.'
      },

      features: {
        missingTitle: '{{featureName}} feature is unavailable',
        missingText: 'This feature is not activated for your account. Use the navigation pane to access another feature.',
        flags: {
          software: 'Software',
          dlp: 'Endpoint Data Discovery',
          policyManagement: 'Device Policies'
        }
      },

      modals: {
        inProgressMessage: 'Saving changes ...',
        successMessage: 'Changes successfully saved.',
        errorMessage: 'Changes were not saved due to an error.',
        errors: {
          timeout: 'Timeout error. Try again later, and if the problem persists, contact Absolute Global Support.',
          generic: 'An error occurred. If the problem persists, contact Absolute Global Support.',
          details: 'Error code: {{errorStatusCode}} {{errorStatusText}}',
          url: 'Error URL: {{errorUrl}}'
        }
      }

    }
  },

  // Supported Locales
  // -----------------

  'de-de': true,
  'ja': true
});
