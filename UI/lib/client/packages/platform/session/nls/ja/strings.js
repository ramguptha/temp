define({
  "session": {
    "expirationCountdown": "残り<strong class=\"is-countdown\">{{numberOfSeconds}}</strong>秒",
    "timeoutWarning": {
        "header": "セッションタイムアウト",
        "description": "セッションが非アクティブなため、まもなく期限がきれます。\"セッションを更新\"をクリックしないと、サインアウトします。",
        "renewButton": "セッションを更新"
    },
    "renewalWarning": {
        "failedHeader": "セッションを更新できません",
        "failedDescription": "セッションの更新時に問題が発生しました。セッションの期限がきれるまで、再試行を続けます。",
        "succeededHeader": "セッションを更新しました",
        "succeededDescription": "セッションを更新しました。ご不便をお掛けしまして申し訳ございません。"
    }
},
});