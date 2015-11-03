define({
  "amComputer": {
    "shared": {
        "computersSelected": "{{deviceCountDetails}} 個のコンピューターが選択されました。"
    },
    "computerListPage": {
        "title": "コンピューター",
        "totalSummary": "結果: {{total}} 個のコンピューター",
        "computersTitle": "コンピューター",
        "allComputersTitle": "すべてのコンピューター",
        "snapContainerSubTitle": "コンピューターのタイプ: {{machineModel}}",
        "commandsTitle": "コマンド",
        "groupNavTitle": "コンピューター",
        "commands": {
            "sendMessage": "メッセージを送信",
            "sendMessageDescription": "{{agentName}}にメッセージを送信",
            "gatherInventory": "インベントリの収集",
            "gatherInventoryDescription": "{{agentName}}からインベントリーを収集する",
            "deviceFreeze": "デバイスフリーズ",
            "deviceFreezeDescription": "{{agentName}}のデバイスフリーズ",
            "deviceUnfreeze": "デバイスアンフリーズ",
            "deviceUnfreezeDescription": "{{agentName}}のデバイスアンフリーズ",
            "dataDelete": "データ削除",
            "dataDeleteDescription": "{{agentName}}のデータ削除"
        }
    },
    "tabLabels": {
        "aboutComputer": "コンピューターについて",
        "hardware": "ハードウェア",
        "cpu": "CPU",
        "systemSoftware": "システムソフトウェア",
        "memory": "メモリー",
        "volume": "ボリューム",
        "networkAdapter": "ネットワークアダプター",
        "missingPatch": "不足しているパッチ",
        "installedSoftware": "インストール済みソフトウェア",
        "installedProfiles": "インストール済みプロファイル"
    },
    "aboutComputerTab": {
        "title": "コンピューターの詳細",
        "commandsTitle": "コマンド"
    },
    "hardwareTab": {
        "title": "ハードウェアの詳細"
    },
    "cpuTab": {
        "title": "CPU の詳細"
    },
    "systemSoftwareTab": {
        "title": "システムソフトウェア"
    },
    "memoryTab": {
        "title": "メモリー"
    },
    "volumeTab": {
        "title": "ボリューム"
    },
    "networkAdapterTab": {
        "title": "ネットワークアダプター"
    },
    "missingPatchTab": {
        "title": "不足しているパッチ"
    },
    "installedSoftwareTab": {
        "title": "インストール済みソフトウェア"
    },
    "installedProfileTab": {
        "title": "インストール済みプロファイル"
    },
    "modals": {
        "sendMessage": {
            "headingOneDevice": "<strong>{{deviceName}}</strong>へメッセージを送信",
            "headingManyDevices": "選択したコンピューターへメッセージを送信",
            "enterMessage": "メッセージを入力",
            "inProgressMsg": "メッセージを送信しています...",
            "successMsg": "コンピューターに送信するため、メッセージが正常にキューに入りました。",
            "errorMsg": "メッセージの送信エラーです。",
            "messageSizeNote": "注記: 最大文字数 ({{maxMessageSize}})を超過しました。メッセージが切り詰められます",
            "unsupportedDevicesMessage": "このコマンドは、選択したコンピューター{{deviceCount}} 個の内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、このメッセージの送信リクエストに含まれないコンピューターの一覧です。",
            "buttons": {
                "actionButtonLabel": "メッセージを送信"
            }
        },
        "gatherInventory": {
            "headingOneDevice": "<strong>{{deviceName}}</strong>からインベントリーを収集する",
            "headingManyDevices": "選択したコンピューターからインベントリーを収集する",
            "inProgressMsg": "インベントリーを収集しています...",
            "successMsg": "インベントリーの収集コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "インベントリーの収集コマンドの送信エラーです。",
            "unsupportedDevicesMessage": "このコマンドは、選択したコンピューター{{deviceCount}} 個の内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、このインベントリーの収集リクエストに含まれないコンピューターの一覧です。",
            "forceFullInventoryCheckbox": "フルインベントリーを強制実行",
            "includeFontInformationCheckbox": "フォント情報を含む",
            "includePrinterInformationCheckbox": "プリンター情報を含む",
            "includeStartupItemInformationCheckbox": "スタートアップアイテム情報を含む",
            "includeServiceInformationCheckbox": "サービス情報を含む",
            "includeStartupItemInformationCheckboxOsxOnly": "スタートアップアイテム情報を含む (OS X のみ)",
            "includeServiceInformationCheckboxWindowsOnly": "サービス情報を含む (Windows のみ)",
            "warningMessage": "最後のアップデートから変更されているコンピューター情報は、これらのオプションを選択していない場合でも必ず収集されます。",
            "buttons": {
                "actionButtonLabel": "インベントリの収集"
            }
        }
    }
},
});