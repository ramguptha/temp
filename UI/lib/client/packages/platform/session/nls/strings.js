define({
  root: {
    session: {
      expirationCountdown: '<strong class="is-countdown">{{numberOfSeconds}}</strong> seconds remaining',

      timeoutWarning: {
        header: 'Session Timeout',
        description: 'Your session will expire soon due to inactivity. You will be signed out unless you click "Renew Session"',
        renewButton: 'Renew Session'
      },

      renewalWarning: {
        failedHeader: 'Unable to Renew Session',
        failedDescription: 'There was a problem renewing your session. We will continue to retry until the session expires.',

        succeededHeader: 'Session Renewed',
        succeededDescription: 'Your session has been renewed. Apologies for any inconvenience.'
      }
    }
  }
});
