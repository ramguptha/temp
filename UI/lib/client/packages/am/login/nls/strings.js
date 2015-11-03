define({
  root: {
    login: {
      heading: 'Login',

      localeField: {
        label: 'Locale',
        placeholder: 'Select Locale'
      },

      portField: {
        label: 'Port',
        placeholder: 'Port'
      },

      serverField: {
        label: 'Server Name',
        placeholder: 'Server Name'
      },

      usernameField: {
        label: 'Username',
        placeholder: 'Username'
      },

      passwordField: {
        label: 'Password',
        placeholder: 'Password'
      },

      checkboxRememberMe: 'Remember Me',
      buttonLogIn: 'Log In',

      links: {
        forgotYourPassword: 'Forgot your password?',
        serviceAgreement: 'Service Agreement',
        privacyPolicy: 'Privacy Policy',
        userLogin: 'Self-Service Portal'
      },

      accountLockedTitle: 'Account Locked.',
      accountLockedDescription: '<p>You have exceeded the maximum allowed failed login attempts. For your security, your account has been locked.</p><p>Please contact your Absolute Manage administrator to re-activate your account</p>',

      sessionTimeoutMessage: 'Your session timed out. Please log in again.',
      loginMessage: 'Please log in.',
      loggedOutMessage: 'You have been logged out.',
      forgotPasswordDescription: 'Please contact your Absolute Manage administrator to have your password reset.',
      javascriptOffTitle: 'It looks like you have JavaScript turned off.',
      javascriptOffDescription: 'You must enable JavaScript in your browser to use this product.',

      errors: {
        invalidUserOrPassword: 'The username or password is invalid. Please remember that passwords are case-sensitive.',
        serverUnavailable: 'The server is unavailable. Please check that the server name and port number are correct.',
        unsufficientAccessRights: 'You have insufficient access rights. Please contact your administrator. <p>The minimum access rights required are: Modify Mobile Media, Manage Mobile Devices, and Modify Mobile Device Policies.</p>',
        statusNameAndCode: '{{name}}: {{code}}',
        errorText: '{{text}}'
      }
    }
  },

  'ja': true
});
