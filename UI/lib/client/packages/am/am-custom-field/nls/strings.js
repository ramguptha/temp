define({
  root: {
    amCustomField: {
      shared: {
        customFieldsSelected: '{{CustomFieldsCountDetails}} custom fields selected.'
      },

      topNavSpec: {
        administrationTitle: 'Administration',
        customFieldTitle: 'Mobile Custom Fields'
      },

      customFieldListPage: {
        title: 'Mobile Custom Fields',
        commands: {
          addNewCustomField: 'Add Custom Field',
          deleteCustomField: 'Delete Custom Fields',
          editCustomField: 'Edit Custom Field',
          duplicateCustomField: 'Duplicate Custom Field'
        }
      },


      modals: {
        deleteCustomField: {
          heading: 'Delete Mobile Custom Fields',
          description: 'The selected custom fields and any associated data will be permanently deleted.',
          inProgressMessage: 'Deleting custom fields ...',
          successMessage: 'Custom fields successfully deleted.',
          errorMessage: 'Error deleting custom fields',
          buttons: {
            deleteLabel: 'Delete'
          }
        },

        addEditCustomField: {
          fieldNameLabel: 'Field name',
          descriptionLabel: 'Description',
          variableNameLabel: 'Variable name',
          dataTypeLabel: 'Data type',
          formatAsLabel: 'format as',
          indicatesRequiredField: 'Indicates required field',
          enumerationValuePlaceholder: 'Enter Enumeration values (one item per line)...',
          validation: {
            duplicateNameMessage: 'Enter a unique custom field name.',
            duplicateVariableNameMessage: 'Enter a unique variable name.'
          }
        },

        addCustomField: {
          heading: 'Create Mobile Custom Field',
          inProgressMessage: 'Adding custom field ...',
          successMessage: 'New Custom Field successfully added.',
          errorMessage: 'Error adding custom field',
          buttons: {
            actionButtonLabel: 'Add Field'
          }
        },

        editCustomField: {
          heading: 'Edit Mobile Custom Field',
          buttons: {
            actionButtonLabel: 'Save'
          }
        },

        duplicateCustomField: {
          heading: 'Duplicate Mobile Custom Field'
        }

      }
    }
  },

  // Supported Locales
  // -----------------

  'ja': true
});
