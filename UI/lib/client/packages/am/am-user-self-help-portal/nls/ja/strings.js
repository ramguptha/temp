define({
  "amUserSelfServicePortal": {
    "shared": {
        "passcodeErrorMessageMobile": "パスコードは 4 ～ 16 文字としてください。",
        "passcodeErrorMessageComputer": "パスワードは 6 文字以上としてください。",
        "passcodesDontMatchErrorMessageMobile": "パスコードが一致しません",
        "passcodesDontMatchErrorMessageComputer": "パスワードが一致していません",
        "noDataToDisplay": "結果なし"
    },
    "deviceList": {
        "title": "マイデバイス"
    },
    "deviceDetails": {
        "title": "デバイスの詳細",
        "actionsLabel": "コマンド",
        "phoneNumber": "電話番号： {{phoneNumber}}",
        "osText": "OS",
        "osVersion": "OS バージョン",
        "passcodePresent": "パスコード",
        "batteryLevel": "バッテリー",
        "modelNumber": "モデル",
        "identifierUDID": "識別子 (UDID)",
        "serialNumber": "シリアル番号",
        "deviceCapacity": "デバイス容量",
        "pcPlatform": "Windows",
        "appleMacPlatform": "OS X",
        "appleTvPlatform": "Apple TV",
        "androidPhonePlatform": "Android",
        "androidTabletPlatform": "Android",
        "iOsPhonePlatform": "iOS",
        "iOsTabletPlatform": "iOS",
        "windowsPhonePlatform": "Windows Phone",
        "detectedDateTooltip": "<p>検出されたデータ:</p><p>{{batteryLevelDate}}</p>",
        "commands": {
            "deviceLockTitle": "デバイスをロック",
            "resetTrackingPassphraseIOsTitle": "パスコードを消去",
            "resetTrackingPassphraseAndroidTitle": "パスコードを設定",
            "remoteDataDeleteTitle": "デバイスの消去",
            "sendMessageTitle": "メッセージを送信",
            "trackDeviceTitle": "デバイスを追跡",
            "isAndroidAndSupportsNoCommandsMessage": "このデバイスではコマンドは有効ではありません。Android デバイスでコマンドを有効にするには、Absolute Mobile Device Management が構成され、AbsoluteApps がインストールされていることを確認してください。",
            "isIOSAndSupportsNoCommandsMessage": "このデバイスではコマンドは有効ではありません。iOS デバイスでコマンドを有効にするには、Absolute Mobile Device Management が構成されていることを確認してください。",
            "isWinPhoneAndSupportsNoCommandsMessage": "このデバイスではコマンドは有効ではありません。Windows Phone デバイスでコマンドを有効にするには、Absolute Mobile Device Management が構成されていることを確認してください。",
            "userHasNoCommandPermissionsMessage": "現在、このデバイスにコマンドを発行する権限がありません。"
        }
    },
    "modals": {
        "deviceLock": {
            "headingMobile": "デバイスをロック",
            "headingComputer": "コンピューターをロック",
            "enterNewPasscodeMobile": "<label>新しいパスコードを入力</label><div class=\"text-italic\">(オプション)</div>",
            "typePasscodeAgainMobile": "パスコードを再度入力してください",
            "enterNewPasscodeComputer": "<label>新しいパスコードを入力</label>",
            "typePasscodeAgainComputer": "パスワードを再度入力",
            "progressMsgMobileDevice": "デバイスをロックしています...",
            "progressMsgComputer": "コンピューターをロックしています...",
            "successMsg": "デバイスのロックコマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "デバイスロックエラーです。",
            "actionMsg1MobileDevice": "既存のパスコード (あれば) を使用してデバイスをロックします。",
            "actionMsg2MobileDevice": "このデバイスにはパスコードがありません。パスコードを使用してデバイスロックが可能です。",
            "actionMsgOsxComputer": "<p>コンピューターをアンロックするためのパスワードを入力します。パスワードはちょうど 6 文字としてください。</p><p>アンロックするには、このパスワードをロックされたコンピューターにローカルで入力する必要があります。コンピューターを遠隔でアンロックすることはできません。</p><p>このパスワードを使用せずにロックされたコンピューターをアンロックするには (例えばコンピューターを紛失した場合)、必ずApple に連絡する必要があります。</p>",
            "actionAllOtherMsg": "選択したデバイスをロックします。",
            "enterMessage": "<label>メッセージの入力</label><label class=\"text-italic\"> (オプション):</label>",
            "enterPhoneNumber": "<label>電話番号を入力</label><label class=\"text-italic\"> (オプション):</label>",
            "messageSizeNote": "注記: 最大文字数 ({{maxMessageSize}})を超過しました。メッセージが切り詰められます",
            "buttons": {
                "actionButtonLabel": "ロック"
            }
        },
        "remoteDataDelete": {
            "actionMsgOsxComputer": "<p>コンピューターの書き込み可能な内部ドライブおよび外部ドライブから、オペレーティング</p><p>コンピューターをアンロックするには、パスワードを入力します。このパスワードはちょうど 6 文字として、アンロックするコンピューター上でローカルに入力する必要があります。</p><p>このパスワードなしでコンピューターをアンロックするには、必ずApple に連絡する必要があります。</p>",
            "actionMsgMobileDevices": "<p>デバイス上のすべてのユーザーデータと、インストールしたすべてのアプリケーションを消去し、事実上デバイスを工場出荷時の条件にリセットします。</p>",
            "heading": "デバイスの消去",
            "actionWarning": "警告: 消去したデータは復元不可能です。",
            "inProgressMsg": "データを消去しています...",
            "successMsg": "デバイスの消去コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "データの消去エラーです。",
            "buttons": {
                "actionButtonLabel": "デバイスの消去"
            },
            "deleteInternalStorageOnly": "内部ストレージのみ消去",
            "deleteInternalStorageSDCard": "内部ストレージと SD カードを消去"
        },
        "resetTrackingPasscode": {
            "heading": "追跡パスフレーズのリセット",
            "actionWarning": "このデバイスのパスフレーズをリセットしてもよろしいですか？",
            "actionDetails": "パスフレーズをリセットした場合、Absolute App を使用できるようにするには、デバイスのユーザーが新しいパスフレーズを入力する必要があります。",
            "inProgressMsg": "追跡パスフレーズをリセットしています...",
            "successMsg": "追跡パスフレーズのリセットコマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "追跡パスフレーズをリセットしています。",
            "actionButtonLabelClearPasscodes": "パスフレーズのリセット",
            "unsupportedDevicesMessage": "このコマンドは、{{deviceCount}} 個の選択したデバイスの内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、この追跡パスフレーズのリセットリクエストに含まれないデバイスの一覧です。",
            "buttons": {
                "actionButtonLabel": "パスフレーズのリセット"
            }
        },
        "trackDevice": {
            "heading": "デバイスを追跡",
            "inProgressMsg": "選択したモバイルデバイスの追跡を設定しています...",
            "successMsg": "デバイスの追跡コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "選択したモバイルデバイスの追跡を設定しています。",
            "actionButtonLabelClearPasscodes": "デバイスを追跡",
            "unsupportedDevicesMessage": "このコマンドは、{{deviceCount}} 個の選択したデバイスの内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、この追跡パスフレーズのリセットリクエストに含まれないデバイスの一覧です。",
            "labels": {
                "actionDetails": "選択したモバイルデバイスの追跡を設定",
                "trackDevice": "デバイスを追跡",
                "activationPassphrase": "アクティベーションパスフレーズ：",
                "activationPassphrasePlaceholder": "アクティベーションパスフレーズを入力",
                "trackingInterval": "追跡間隔:",
                "trackingIntervalPlaceholder": "追跡間隔を入力:",
                "locationAccuracy": "位置精度:"
            },
            "buttons": {
                "actionButtonLabel": "デバイスを追跡"
            },
            "help": {
                "trackDeviceStrong": "デバイスを追跡:",
                "trackDeviceText": "このオプションにチェックを入れると選択したデバイスの追跡が有効になり、チェックを外すと無効になります。",
                "activationPassphraseStrong": "アクティベーションパスフレーズ：",
                "activationPassphraseText": "選択したモバイルデバイスにアクセスするために必要な PIN。",
                "trackingIntervalStrong": "追跡間隔:",
                "trackingIntervalText": "デバイスの位置記録が記録される間隔。",
                "locationAccuracyStrong": "位置精度:",
                "locationAccuracyText": "デバイスの位置が記録される最大精度。"
            }
        },
        "sendMessage": {
            "heading": "メッセージを送信",
            "enterMessage": "メッセージを入力",
            "inProgressMsg": "メッセージを送信しています...",
            "successMsg": "デバイスに送信するため、メッセージは正常にキューに入りました。",
            "errorMsg": "メッセージの送信エラーです。",
            "messageSizeNote": "注記: 最大文字数 ({{maxMessageSize}})を超過しました。メッセージが切り詰められます",
            "removeMessageAfter": "メッセージを削除するまでの時間",
            "timeOut": "(分:秒)",
            "addCancelButton": "メッセージダイアログにキャンセルボタンを追加",
            "messageWrongTime": "時間の書式が正しくありません",
            "buttons": {
                "actionButtonLabel": "メッセージを送信"
            }
        },
        "clearPasscode": {
            "inProgressMsg": "パスコードを消去しています...",
            "successMsg": "パスコードの消去コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "デバイスのパスコードの消去エラーです。",
            "errorDetailsMsg": "デバイスのパスコードの消去中にエラーが発生しました: ",
            "enterNewPasscode": "新しいパスコードを入力",
            "typeThePasscodeAgain": "パスコードを再度入力してください",
            "clearAllFields": "すべてのフィールドを消去",
            "leaveAllFieldsEmpty": "パスコードを消去するには、すべてのフィールドを空白のままにしてください。",
            "actionButtonLabelClearPasscode": "パスコードを消去",
            "headingClearPasscode": "パスコードを消去"
        },
        "setPasscode": {
            "inProgressMsg": "パスコードを設定中...",
            "successMsg": "パスコードの設定コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "パスコードの設定にエラーが発生しました。",
            "errorDetailsMsg": "デバイスのパスコードの設定中にエラーが発生しました: ",
            "headingSetPasscode": "パスコードを設定",
            "actionButtonLabelSetPasscode": "パスコードを設定"
        }
    }
},
});