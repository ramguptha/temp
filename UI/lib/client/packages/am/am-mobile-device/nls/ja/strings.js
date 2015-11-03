define({
  "amMobileDevice": {
    "shared": {
        "passcodeErrorMessage": "パスコードは 4 ～ 16 文字としてください。",
        "passcodesDontMatchErrorMessage": "パスコードが一致しません",
        "devicesSelected": "{{deviceCountDetails}} 個のデバイスが選択されました。"
    },
    "topNavSpec": {
        "mobileDevicesTitle": "モバイルデバイス",
        "allMobileDevicesTitle": "すべてのモバイルデバイス"
    },
    "commandsLabel": "コマンド",
    "mobileDevicesListPage": {
        "mobileDevicesTitle": "モバイルデバイス",
        "totalSummary": "結果: {{total}} モバイルデバイス",
        "navTitle": "モバイルデバイス",
        "commands": {
            "lockDevice": "デバイスをロック",
            "setActivationLock": "起動ロックオプションを設定",
            "clearPasscode": "パスコードを消去",
            "clearPasscodeMultiple": "パスコードの消去と設定",
            "remoteErase": "デバイスの消去",
            "sendMessage": "メッセージを送信",
            "updateDeviceInfo": "デバイス情報を更新",
            "setRoamingOptions": "ローミングオプションを設定",
            "installApplication": "アプリケーションをインストール",
            "installConfigProfile": "構成プロファイルをインストール",
            "installProvisioningProfile": "プロビジョニングプロファイルをインストール",
            "setDeviceName": "デバイス名を設定",
            "setDeviceOrganizationInfo": "組織情報を設定",
            "setDeviceOwnership": "デバイスオーナーシップを設定",
            "setDeviceEnrollmentUser": "デバイス登録ユーザーを設定",
            "retryAllCommand": "すべてを再試行..."
        }
    },
    "devicePage": {
        "title": "モバイルデバイスの詳細",
        "unmanagedDeviceMessage": "このデバイスは管理されていません。コマンドが有効ではありません。",
        "deviceDescription": "モデル: {{modelUI}} | オーナーシップ: {{ownershipUI}} | ID: {{udidUI}} | シリアル番号: {{serialNumberUI}}",
        "commands": {
            "deviceLockTitle": "デバイスロック",
            "deviceLockDescription": "{{deviceName}}にデバイスロックを発行",
            "setPasscodeTitle": "パスコードの消去と設定",
            "clearPasscodeTitle": "パスコードの消去",
            "setPasscodeDescription": "{{deviceName}}のパスコードをリセット",
            "remoteDataDeleteTitle": "デバイスの消去",
            "remoteDataDeleteDescription": "{{deviceName}}からすべてのデータを消去",
            "sendMessageTitle": "メッセージを送信",
            "sendMessageDescription": "{{deviceName}}にメッセージを送信",
            "updateDeviceInfoTitle": "デバイス情報を更新",
            "updateDeviceInfoDescription": "{{deviceName}}がそのデバイス情報を再送信するようにリクエスト",
            "setRoamingOptionsTitle": "ローミングオプションを設定",
            "setRoamingOptionsDescription": "{{deviceName}}にデータと音声のローミングオプションを設定",
            "installApplicationTitle": "アプリケーションをインストール",
            "installApplicationDescription": "インハウスまたはサードパーティアプリケーションを{{deviceName}}にインストール",
            "installConfigurationProfileTitle": "構成プロファイルをインストール",
            "installConfigurationProfileDescription": "{{deviceName}}に構成プロファイルをインストール",
            "installProvisioningProfileTitle": "プロビジョニングプロファイルをインストール",
            "installProvisioningProfileDescription": "{{deviceName}}にプロビジョニングプロファイルをインストール",
            "setDeviceOwnershipTitle": "デバイスオーナーシップを設定",
            "setDeviceOwnershipDescription": "{{deviceName}}にデバイスオーナーシップを設定",
            "setDeviceEnrollmentUserTitle": "デバイス登録ユーザーを設定",
            "setDeviceEnrollmentUserDescription": "{{deviceName}}にデバイス登録ユーザーを設定",
            "setDeviceNameTitle": "デバイス名を設定",
            "setDeviceNameDescription": "{{deviceName}}にデバイス名を設定",
            "setDeviceOrganizationInfoTitle": "組織情報を設定",
            "setDeviceOrganizationInfoDescription": "{{deviceName}}に組織情報を設定",
            "retryAllCommandTitle": "すべてを再試行...",
            "retryAllCommandDescription": "{{deviceName}}ですべてのコマンドを再試行",
            "setActivationLockTitle": "起動ロックオプションを設定",
            "setActivationLockDescription": "{{deviceName}}で起動ロックオプションを設定",
            "isAndroidAndSupportsNoCommandsMessage": "このデバイスではコマンドは有効ではありません。Android デバイスでコマンドを有効にするには、Absolute Mobile Device Management が構成され、AbsoluteApps がインストールされていることを確認してください。",
            "isIOSAndSupportsNoCommandsMessage": "このデバイスではコマンドは有効ではありません。iOS デバイスでコマンドを有効にするには、Absolute Mobile Device Management が構成されていることを確認してください。",
            "isWinPhoneAndSupportsNoCommandsMessage": "このデバイスではコマンドは有効ではありません。Windows Phone デバイスでコマンドを有効にするには、Absolute Mobile Device Management が構成されていることを確認してください。",
            "userHasNoCommandPermissionsMessage": "現在、このデバイスにコマンドを発行する権限がありません。"
        },
        "tabLabels": {
            "aboutDevice": "デバイスについて",
            "mobilePolicies": "モバイルポリシー",
            "applications": "アプリケーション",
            "certificates": "証明書",
            "configurationProfiles": "構成プロファイル",
            "provisioningProfiles": "プロビジョニングプロファイル",
            "assignedItems": "割り当てられたアイテム",
            "assignedItemsThirdPartyApps": "サードパーティーアプリ",
            "assignedItemsInHouseApps": "インハウスアプリ",
            "assignedItemsContent": "コンテンツ",
            "assignedItemsConfigurationProfiles": "構成プロファイル",
            "administrators": "アドミニストレータ",
            "user": "ユーザー",
            "actions": "実施したアクション",
            "customFields": "カスタムフィールド"
        },
        "aboutDeviceTab": {
            "title": "デバイスの詳細",
            "phoneNumber": "電話番号： {{phoneNumber}}",
            "osVersion": "OS バージョン",
            "lastContact": {
                "value": "最終アクセス日時",
                "comment": "JA - last accessed date and time"
            },
            "passcodePresent": "パスコードあり",
            "batteryLevel": "バッテリーレベル",
            "modelNumber": "モデル番号",
            "serialNumber": "シリアル番号",
            "identifierUDID": "識別子 (UDID)",
            "deviceGUID": "デバイスの GUID",
            "identity": "ID",
            "osBuildNumber": "OS ビルド番号",
            "osLanguage": "OS の言語",
            "ownership": "オーナーシップ",
            "isManaged": "管理中",
            "jailBrokenLabelRooted": "ルート化",
            "jailBrokenLabelJailBroken": "Jailbreak 済",
            "recordCreated": "記録作成日",
            "absoluteAppsVersion": "AbsoluteApps のバージョン",
            "absoluteAppsBuildNo": "AbsoluteApps のビルド番号",
            "hasPersistence": "パーシスタンスをサポート",
            "isMdmProfileUpToDate": "MDM プロファイルが最新",
            "productionDate": "製造日",
            "age": "製造年数",
            "warrantyInfo": "保証情報",
            "warrantyEndDate": "保証終了",
            "isPasscodeCompliant": "パスコードの準拠",
            "isPasscodeCompliantWithProfiles": "パスコードのプロファイルとの準拠",
            "storageCapacity": "ストレージ容量",
            "deviceCapacity": "デバイス容量",
            "usedCapacity": "使用済み容量",
            "availableCapacity": "利用可能な容量",
            "storage": "ストレージ",
            "storageType": "タイプ",
            "storageTotalSpace": "合計空き領域",
            "storageAvailableSpace": "使用可能な空き領域",
            "internalStorage": "内部ストレージ",
            "sDCard1NonRemovable": "SD カード 1 (ノンリムーバブル)",
            "sDCard2Removable": "SD カード 2 (リムーバブル)",
            "networking": "ネットワーキング",
            "isGpsCapable": "GPS 対応",
            "wifiNetwork": "Wi-Fi ネットワーク",
            "homeNetwork": "ホームネットワーク",
            "publicIpAddress": "パブリック IP アドレス",
            "cellIpAddress": "セル IP アドレス",
            "wifiIpAddress": "Wi-Fi IP アドレス",
            "wifiMacAddress": "Wi-Fi MAC アドレス",
            "bluetoothMacAddress": "Bluetooth MAC アドレス",
            "hardware": "ハードウェア",
            "tablet": "タブレット",
            "manufacturer": "製造業者",
            "cpuName": "CPU 名",
            "cpuSpeed": "CPU 速度",
            "displayResolution": "ディスプレイ解像度",
            "board": "ボード",
            "kernelVersion": "カーネルバージョン",
            "deviceInfo": "デバイス情報",
            "hardwareEncryption": "ハードウェア暗号化",
            "systemMemory": "システムメモリ",
            "memoryTotal": "合計 RAM",
            "memoryAvailable": "利用可能な RAM",
            "cacheTotal": "合計キャッシュ",
            "cacheAvailable": "利用可能なキャッシュ",
            "cellularInformation": "携帯電話情報",
            "currentCarrierNetwork": "現在のキャリアネットワーク",
            "cellularTechnology": "携帯電話技術",
            "isRoaming": "ローミング",
            "cellularNetworkType": "携帯電話データネットワークのタイプ",
            "imei": "IMEI/MEID",
            "simIccId": "SIM ICC 識別子",
            "currentMcc": "現在のモバイル国コード",
            "currentMnc": "現在のモバイルネットワークコード",
            "homeMcc": "ホームモバイル国コード",
            "homeMnc": "ホームモバイルネットワークコード",
            "imeiSv": "モバイルデバイスの IMEISV",
            "dataRoamingEnabled": "データローミングが有効",
            "voiceRoamingEnabled": "音声ローミングが有効",
            "carrierSettingsVersion": "キャリア設定バージョン",
            "modemFirmwareVersion": "モデムファームウェアバージョン",
            "enableOutboundSMS": "アウトバウンド SMS を有効化",
            "remoteWipe": "遠隔一掃",
            "remoteWipeSupported": "一掃に対応",
            "remoteWipeStatus": "一掃ステータス",
            "remoteWipeStatusNote": "一掃ステータスのメモ",
            "wipeRequestTime": "一掃リクエスト時刻",
            "wipeSentTime": "一掃送信時刻",
            "wipeAckTime": "一掃承認時刻",
            "lastWipeRequestor": "最後の一掃リクエスト担当者",
            "exchangeServer": "エクスチェンジサーバー",
            "accessState": "アクセス状態",
            "accessStateReason": "アクセス状態の理由",
            "numberOfFoldersSynched": "同期したフォルダの数",
            "organizationalInfo": "組織情報",
            "organizationName": "名前",
            "organizationPhone": "電話",
            "organizationEMail": "E メール",
            "organizationAddress": "住所",
            "organizationCustom": "カスタム",
            "lastChangedItems": "最後に変更されたアイテム",
            "lastInfoUpdate": "デバイス情報",
            "lastInstalledSwUpdate": "インストール済みソフトウェア",
            "lastConfigProfileUpdate": "インストール済み構成プロファイル",
            "lastCertificateUpdate": "インストール済み証明書",
            "lastProvisioningProfileUpdate": "インストール済みプロビジョニングプロファイル",
            "lastPolicyUpdate": "ポリシーの更新"
        },
        "mobilePoliciesTab": {
            "title": "このモバイルデバイスに関連するモバイルポリシー",
            "buttons": {
                "addMobileDevice": "モバイルデバイスをポリシーに追加",
                "removeMobileDevice": "モバイルデバイスをポリシーから削除"
            }
        },
        "applicationsTab": {
            "title": "このモバイルデバイスにインストール済みのアプリケーション",
            "buttons": {
                "installApplication": "アプリケーションをインストール",
                "uninstallApplication": "アプリケーションをアンインストール"
            }
        },
        "certificatesTab": {
            "title": "このモバイルデバイスに関連付けられた証明書"
        },
        "configurationProfilesTab": {
            "title": "このモバイルデバイスにインストール済みの証明書プロファイル",
            "buttons": {
                "installConfigurationProfile": "構成プロファイルをインストール",
                "uninstallConfigurationProfile": "デバイスからアンインストール"
            }
        },
        "provisioningProfilesTab": {
            "title": "このモバイルデバイスにインストール済みのプロビジョニングプロファイル",
            "buttons": {
                "installProvisioningProfile": "プロビジョニングプロファイルをインストール",
                "uninstallProvisioningProfile": "プロビジョニングプロファイルをアンインストール"
            }
        },
        "assignedThirdPartyApplicationsTab": {
            "title": "このモバイルデバイスに割り当てられたサードパーティアプリケーション"
        },
        "assignedInHouseApplicationsTab": {
            "title": "このモバイルデバイスに割り当てられたインハウスアプリケーション"
        },
        "assignedContentTab": {
            "title": "このモバイルデバイスに割り当てられたコンテンツ"
        },
        "assignedConfigurationProfilesTab": {
            "title": "このモバイルデバイスに割り当てられた構成プロファイル"
        },
        "customFieldsTab": {
            "title": "このモバイルデバイスのカスタムフィールドデータ",
            "commands": {
                "deleteCustomFieldData": "カスタムフィールドデータを削除",
                "editCustomFieldData": "カスタムフィールド値を編集"
            }
        },
        "administratorsTab": {
            "title": "このモバイルデバイスのアドミニストレータ"
        },
        "userTab": {
            "title": "アクティブ/オープンディレクトリのユーザー詳細",
            "displayName": "ディスプレイ名",
            "firstName": "名",
            "lastName": "姓",
            "logonName": "ログイン名",
            "email": "E メール",
            "phoneNumber": "電話番号",
            "organizationalUnit": "組織単位",
            "organizationalUnitPath": "組織単位のパス",
            "memberOf": "メンバー",
            "company": "会社",
            "department": "部署",
            "office": "オフィス",
            "street": "番地",
            "city": "市区町村",
            "state": "都道府県",
            "zipCode": "郵便番号",
            "country": "国",
            "mDmServerEnrollment": "MDM サーバー登録",
            "enrollmentUserName": "ユーザー名",
            "enrollmentDomain": "ドメイン",
            "noDataToDisplay": "表示するユーザーデータがありません。"
        },
        "performedActionsTab": {
            "title": "このモバイルデバイスで実施したアクション",
            "commands": {
                "removeActionCommand": "リストから削除",
                "reapplyActionCommand": "デバイス上で再実行"
            }
        }
    },
    "modals": {
        "installApplication": {
            "headingOneDevice": "<strong>{{deviceName}}</strong>にアプリケーションをインストール",
            "headingManyDevices": "選択したデバイスにアプリケーションをインストール",
            "inHouseAppCaption": "インハウスアプリケーション:",
            "thirdPartyAppCaption": "サードパーティアプリケーション:",
            "inProgressMsg": "アプリケーションをインストールしています...",
            "successMsg": "アプリケーションインストールコマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "アプリケーションのインストールエラーです。",
            "errorMsgMultiplePlatforms": "アプリケーションは、異なるオペレーティング・システムを実行する複数のデバイスにインストールすることはできません。",
            "errorMsgMultiplePlatformsDetails": "選択したすべてのデバイスが同じオペレーティング・システムを実行していることを確認してください。",
            "buttons": {
                "actionButtonLabel": "インストール"
            }
        },
        "deviceLock": {
            "headingOneDevice": "デバイスのロック",
            "headingManyDevices": "デバイスをロック",
            "enterNewPasscode": "<label>新しいパスコードを入力</label><div class=\"text-italic\">(オプション)</div>",
            "typePasscodeAgain": "パスコードを再度入力してください",
            "lockingOneDeviceProgressMsg": "{{deviceCount}} 個のデバイスをロックしています...",
            "lockingManyDevicesProgressMsg": "{{deviceCount}} 個のデバイスをロックしています...",
            "successMsg": "デバイスのロックコマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "デバイスロックエラーです。",
            "actionMsg1": "選択したデバイスは既存のパスコード (あれば) を使用してロックされます。",
            "actionMsg1Singular": "このデバイスは既存のパスコード (あれば) を使用してロックされます。",
            "actionMsg2": "選択したデバイスにはパスコードがありません。パスコードを使用してデバイスロックが可能です。",
            "actionMsg2Singular": "このデバイスにはパスコードがありません。パスコードを使用してデバイスロックが可能です。",
            "actionMsg3": "選択したデバイスの一部にパスコードがありません。パスコードを使用してデバイスロックが可能です。その他のデバイスは既存のパスコードを使用してロックされます。",
            "actionMsg4": "<p>選択した Android デバイスの一部にパスコードがありません。パスコードを使用してデバイスロックの選択ができます。その他の Android デバイスは既存のパスコードを使用してロックされます。</p> <p>iOS デバイスは既存のパスコード (あれば) を使用してロックされます。</p>",
            "actionMsg5": "<p>選択した Android デバイスにパスコードがありません。パスコードを使用してデバイスロックの選択ができます。</p><p>iOS デバイスは既存のパスコード (あれば) を使用してロックされます。</p>",
            "actionAllOtherMsg": "選択したデバイスをロックします。",
            "unsupportedDevicesMessage": "このコマンドは、選択したデバイスの{{deviceCount}} 個の内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、このデバイスのロックリクエストに含まれないデバイスの一覧です。",
            "buttons": {
                "actionButtonLabel": "ロック"
            }
        },
        "clearAndSetPasscode": {
            "heading": "パスコードの消去と設定",
            "inProgressMsg": "パスコードの消去と設定を処理しています...",
            "successMsg": "パスコードの消去と設定コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "パスコードの消去と設定のエラーです。",
            "actionButtonLabel": "パスコードを消去",
            "actionButtonLabelSetNewPasscodeEnabled": "パスコードの消去と設定",
            "setNewPasscodeCheckBoxTitleMixed": "新しいパスコードの設定 (Android デバイスのみ)",
            "setNewPasscodeCheckBoxTitleAndroid": "新しいパスコードを設定",
            "enterNewPasscode": "新しいパスコード (4-16 文字)",
            "typeThePasscodeAgain": "新しいパスコードを確認"
        },
        "clearPasscode": {
            "heading": "パスコードを消去",
            "inProgressMsg": "パスコードを消去しています...",
            "successMsg": "パスコードの消去コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "デバイスのパスコードの消去エラーです。",
            "actionButtonLabel": "パスコードを消去",
            "actionWarningiOsDevice": "選択したデバイスのパスコードを消去してもよろしいですか？この操作は元に戻すことができません。",
            "actionWarningiOsDeviceSingular": "このデバイスのパスコードを消去してもよろしいですか？この操作は元に戻せません。",
            "unsupportedDevicesMessage": "このコマンドは、{{deviceCount}} 個の選択したデバイスの内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、このリクエストに含まれないデバイスの一覧です。"
        },
        "remoteDataDelete": {
            "deleteInternalStorageOnly": "内部ストレージのみ消去",
            "deleteInternalStorageSDCard": "内部ストレージと SD カードを消去",
            "heading": "デバイスの消去",
            "actionWarning": "警告: 消去したデータは復元不可能です。",
            "inProgressMsg": "データを消去しています...",
            "successMsg": "デバイスの消去コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "データの消去エラーです。",
            "errorDetailsMsg": "データの消去中にエラーが発生しました: ",
            "confirmPromptOneDevice": "選択した {{deviceCountDetails}} 個のデバイスからデータを消去します。",
            "confirmPromptManyDevices": "選択した {{deviceCountDetails}} 個のデバイスからデータを消去します。",
            "unsupportedDevicesMessage": "このコマンドは、{{deviceCount}} 個の選択したデバイスの内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、このデバイスの消去リクエストに含まれないデバイスの一覧です。",
            "buttons": {
                "actionButtonLabel": "デバイスの消去"
            }
        },
        "sendMessage": {
            "headingOneDevice": "<strong>{{deviceName}}</strong>へメッセージを送信",
            "headingManyDevices": "選択したデバイスへメッセージを送信",
            "enterMessage": "メッセージを入力",
            "inProgressMsg": "メッセージを送信しています...",
            "successMsg": "デバイスに送信するため、メッセージは正常にキューに入りました。",
            "errorMsg": "メッセージの送信エラーです。",
            "errorDetailsMsg": "メッセージの送信中にエラーが発生しました。 ",
            "messageSizeNote": "注記: 最大文字数 ({{maxMessageSize}})を超過しました。メッセージが切り詰められます",
            "unsupportedDevicesMessage": "このコマンドは、選択したデバイスの{{deviceCount}} 個の内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、このメッセージの送信リクエストに含まれないデバイスの一覧です。",
            "buttons": {
                "actionButtonLabel": "メッセージを送信"
            }
        },
        "updateDeviceInfo": {
            "heading": "選択したデバイスのデバイス情報を更新します",
            "inProgressMsg": "デバイス情報を更新しています...",
            "successMsg": "デバイス情報の更新コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "デバイス情報の更新エラーです。",
            "errorDetailsMsg": "デバイス情報の更新中にエラーが発生しました: ",
            "unsupportedDevicesMessage": "このコマンドは、選択したデバイスの{{deviceCount}} 個の内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、このデバイス情報の更新リクエストに含まれないデバイスの一覧です。",
            "buttons": {
                "actionButtonLabel": "情報の更新"
            }
        },
        "installConfigurationProfile": {
            "headingOneDevice": "<strong>{{deviceName}}</strong>に構成プロファイルをインストール",
            "headingManyDevices": "選択したデバイスに構成プロファイルをインストール",
            "inProgressMsg": "構成プロファイルをインストールしています...",
            "successMsg": "構成プロファイルのインストールコマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorDetailsMsg": "構成プロファイルのインストール中にエラーが発生しました: ",
            "errorMsg": "構成プロファイルのインストールエラーです。",
            "errorMsgMultiplePlatforms": "構成プロファイルは、異なるオペレーティング・システムを実行する複数のデバイスにインストールすることはできません。",
            "errorMsgMultiplePlatformsDetails": "選択したすべてのデバイスが同じオペレーティング・システムを実行していることを確認してください。",
            "buttons": {
                "actionButtonLabel": "インストール"
            }
        },
        "setDeviceOwnership": {
            "isUndefined": "未定義",
            "isCompany": "会社",
            "isUser": "ユーザー (個人のデバイス)",
            "isGuest": "ゲスト",
            "heading": "デバイスオーナーシップを設定",
            "confirmPrompt": "このデバイスにオーナーシップタイプを設定します。",
            "inProgressMsg": "デバイスオーナーシップを設定しています...",
            "successMsg": "オーナーシップが正常に更新されました。",
            "errorMsg": "オーナーシップの設定エラーです。",
            "errorDetailsMsg": "デバイスオーナーシップの設定中にエラーが発生しました: ",
            "buttons": {
                "actionButtonLabel": "オーナーシップを設定"
            }
        },
        "setDeviceEnrollmentUser": {
            "userName": "ユーザー名",
            "domain": "ドメイン",
            "clearAllFields": "すべてのフィールドを消去",
            "leaveAllFieldsEmpty": "登録ユーザーを削除するには、すべてのフィールドを空欄のままにしてください。",
            "heading": "デバイス登録ユーザーを設定",
            "confirmPromptOneDevice": "デバイスをどのユーザーにも割り当てないようにするには、両方のフィールドを空欄のままにしてください。",
            "confirmPromptManyDevices": "選択したデバイスの新しい登録ユーザー:",
            "actionWarningManyDevices": "登録ユーザーが更新されます。選択したデバイスをどのユーザーにも割り当てないようにするには、両方のフィールドを空欄のままにしてください。",
            "inProgressMsg": "登録ユーザーを設定しています...",
            "successMsg": "登録ユーザーが正常に更新されました。",
            "errorMsg": "登録ユーザーの設定エラーです。",
            "errorMessageUserNameBlank": "ドメインの設定時に、ユーザー名を空欄のままにすることはできません。",
            "errorDetailsMsg": "デバイス登録ユーザーの設定中にエラーが発生しました: ",
            "buttons": {
                "actionButtonLabel": "ユーザーを設定"
            }
        },
        "setDeviceName": {
            "newName": "新しい名前:",
            "heading": "デバイス名を設定",
            "inProgressMsg": "デバイス名を設定しています...",
            "successMsg": "デバイス名を設定コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "デバイス名の設定エラーです。",
            "errorMsgUniqueDeviceName": "一意のモバイルデバイス名を入力してください。",
            "errorDetailsMsg": "デバイス名の設定中にエラーが発生しました:",
            "buttons": {
                "actionButtonLabel": "名前を設定"
            }
        },
        "retryAll": {
            "heading": "すべてを再試行...",
            "inProgressMsg": "再試行しています...",
            "successMsg": "\"すべてを再試行\" コマンドは正常にキューに入りました",
            "errorMsg": "すべてを再試行のエラーです。",
            "errorDetailsMsg": "すべてを再試行中にエラーが発生しました: ",
            "btnProfiles": "失敗したプロファイルをすべて再試行",
            "btnApps": "失敗したアプリケーションをすべて再試行",
            "btnToken": "フリート管理トークンのプッシュを再試行",
            "actionDescriptionProfile": "このコマンドは、次回デバイスがレポートしたときに、ポリシーを通じてデバイスに割り当てられた構成またはプロビジョニングプロファイルで失敗したインストールを再試行します。",
            "headingProfile": "失敗したプロファイルをすべて再試行",
            "actionWarningProfile": "選択したデバイスで前回失敗したすべてのプロファイルのインストールを再試行しますか？",
            "actionDescriptionApps": "このコマンドは、次回デバイスがコールしたときに、ポリシーを通じてデバイスに割り当てられたアプリケーションのインストールの失敗後、再試行します。",
            "headingApps": "失敗したアプリケーションをすべて再試行",
            "actionWarningApps": "選択したデバイスで前回失敗したすべてのアプリケーションのインストールを再試行しますか？",
            "actionDescriptionToken": "その結果...",
            "headingToken": "フリート管理トークンのプッシュを再試行",
            "actionWarningToken": "再試行してもよろしいですか？",
            "buttons": {
                "actionButtonLabel": "再試行"
            }
        },
        "setRoamingOptions": {
            "heading": "ローミングオプションを設定",
            "voiceRoamingEnabled": "音声ローミングを有効化 (iOS 7+ のみ)",
            "dataRoamingEnabled": "データローミングを有効化",
            "inProgressMsg": "ローミングオプションを設定しています...",
            "successMsg": "ローミングオプションを設定コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "ローミングオプションの設定エラーです。",
            "errorDetailsMsg": "ローミングオプションの設定中にエラーが発生しました: ",
            "unsupportedDevicesMessage": "このコマンドは、選択したデバイスの{{deviceCount}} 個の内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、このローミングオプションの設定リクエストに含まれないデバイスの一覧です。",
            "unsupportedAttentionMessage": "音声ローミングオプションはすべての選択したデバイスに<strong>適用されない</strong>ことがあります。",
            "buttons": {
                "actionButtonLabel": "ローミングオプションを設定"
            }
        },
        "installProvisioningProfile": {
            "headingOneDevice": "<strong>{{deviceName}}</strong>にプロビジョニングプロファイルをインストール",
            "headingManyDevices": "選択したデバイスにプロビジョニングプロファイルをインストール",
            "inProgressMsg": "プロビジョニングプロファイルをインストールしています...",
            "successMsg": "プロビジョニングプロファイルのインストールコマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorDetailsMsg": "プロビジョニングプロファイルのインストール中にエラーが発生しました: ",
            "errorMsg": "プロビジョニングプロファイルのインストールエラーです。",
            "errorMsgMultiplePlatforms": "プロビジョニングプロファイルは、異なるオペレーティング・システムを実行する複数のデバイスにインストールすることはできません。",
            "errorMsgMultiplePlatformsDetails": "選択したすべてのデバイスが同じオペレーティング・システムを実行していることを確認してください。",
            "buttons": {
                "actionButtonLabel": "インストール"
            }
        },
        "setOrganizationInfo": {
            "heading": "組織情報を設定",
            "inProgressMsg": "組織情報を設定しています...",
            "successMsg": "組織情報を設定コマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "組織情報の設定エラーです。",
            "errorDetailsMsg": "組織情報の設定中にエラーが発生しました: ",
            "name": "名前",
            "phoneNumber": "電話番号",
            "email": "E メール",
            "address": "住所",
            "comments": "コメント",
            "clearAllFields": "すべてのフィールドを消去",
            "leaveAllFieldsEmpty": "すべての組織情報を削除するには、すべてのフィールドを空欄のままにしてください。",
            "buttons": {
                "actionButtonLabel": "情報を設定"
            }
        },
        "addMobileDeviceToPolicies": {
            "heading": "モバイルデバイスをポリシーに追加",
            "buttons": {
                "actionButtonLabel": "ポリシーを追加"
            }
        },
        "removeMobileDeviceFromPolicy": {
            "headingOneDevice": "モバイルデバイスをポリシーから削除",
            "headingManyDevices": "モバイルデバイスをポリシーから削除",
            "actionWarning": "注記: \"自動削除\"コンテンツに加えて、このアクションは選択したポリシーに関連付けられた\"自動削除\"アプリケーション、アクション、プロビジョニングまたは構成プロファイルもデバイスから削除します。",
            "buttons": {
                "actionButtonLabel": "削除"
            }
        },
        "uninstallApplications": {
            "heading": "アプリケーションをアンインストール",
            "descriptionOneDevice": "このアクションは、選択したアプリケーションをデバイスから削除するよう試みます。",
            "descriptionManyDevices": "このアクションは、選択したアプリケーションをデバイスから削除するよう試みます。",
            "actionWarning": "このアクションは元に戻せません。",
            "inProgressMsg": "アプリケーションをアンインストールしています...",
            "successMsg": "アプリケーションのアンインストールコマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "アプリケーションのアンインストールエラーです。",
            "errorDetailsMsg": "アプリケーションのアンインストール中にエラーが発生しました: ",
            "buttons": {
                "actionButtonLabel": "アンインストール"
            }
        },
        "uninstallConfigurationProfile": {
            "headingOneProfile": "構成プロファイルをアンインストール",
            "headingManyProfiles": "構成プロファイルをアンインストール",
            "descriptionOneDevice": "このアクションは、選択した構成プロファイルをデバイスから削除するよう試みます。",
            "descriptionManyDevices": "このアクションは、選択した構成プロファイルをデバイスから削除するよう試みます。",
            "actionWarning": "このアクションは元に戻せません。",
            "inProgressMsg": "構成プロファイルをアンインストールしています...",
            "successMsg": "構成プロファイルのアンインストールコマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "構成プロファイルのアンインストールエラーです。",
            "errorDetailsMsg": "構成プロファイルのアンインストール中にエラーが発生しました: ",
            "buttons": {
                "actionButtonLabel": "アンインストール"
            }
        },
        "uninstallProvisioningProfile": {
            "headingOneProfile": "プロビジョニングプロファイルをアンインストール",
            "headingManyProfiles": "プロビジョニングプロファイルをアンインストール",
            "descriptionOneDevice": "このアクションは、選択したプロビジョニングプロファイルをデバイスから削除するよう試みます。",
            "descriptionManyDevices": "このアクションは、選択したプロビジョニングプロファイルをデバイスから削除するよう試みます。",
            "actionWarning": "このアクションは元に戻せません。",
            "inProgressMsg": "プロビジョニングプロファイルのアンインストールを開始しています...",
            "successMsg": "プロビジョニングプロファイルのアンインストールコマンドが正常にキューに入り、次回このデバイスがオンラインになると実行されます。",
            "errorMsg": "プロビジョニングプロファイルのアンインストールエラーです。",
            "errorDetailsMsg": "プロビジョニングプロファイルのアンインストール中にエラーが発生しました: ",
            "buttons": {
                "actionButtonLabel": "アンインストール"
            }
        },
        "setActivationLock": {
            "heading": "起動ロックオプションを設定",
            "confirmPrompt": "選択したデバイスでアクティベーションロック機能を許可しますか？",
            "actionDescription0": "「アクティベーションを許可」をクリックすると、アクティベーションロック機能が「iPhone を探す」を使用してアクティブ化および非アクティブ化されます。",
            "actionDescription1": "「アクティベーションを許可しない」をクリックすると、「iPhone を探す」をオンにしてもアクティベーションロック機能が有効になりません (ただし、「iPhone を探す」がオフになると無効になります)。",
            "actionDescription2": "注記: このコマンドでは以前に有効にしたアクティベーションロック保護は無効にならず、次回「iPhone を探す」がオフになるまで有効な状態が続きます。",
            "actionButtonLabel": "設定",
            "inProgressMsg": "起動ロックオプションを設定しています...",
            "successMsg": "新しい起動ロックオプションが正常に送信されました。",
            "errorMsg": "起動ロックオプションの設定エラーです。",
            "errorDetailsMsg": "起動ロックオプションの設定中にエラーが発生しました: ",
            "unsupportedDevicesMessage": "このコマンドは、選択したデバイスの{{deviceCount}} 個の内、{{unsupportedDeviceCount}} 個には適用されません。以下の表は、この起動ロックオプションの設定リクエストに含まれないデバイスの一覧です。",
            "buttons": {
                "allowActivation": "アクティベーションを許可",
                "disallowActivation": "アクティベーションを許可しない"
            }
        },
        "deleteCustomFieldData": {
            "heading": "カスタムフィールドデータを削除",
            "description": "選択したカスタムフィールドのデータはデバイスから削除されます。",
            "inProgressMessage": "カスタムフィールドのデータを削除しています...",
            "successMessage": "カスタムフィールドのデータが正常に削除されました。",
            "errorMessage": "カスタムフィールドデータの削除エラーです。",
            "buttons": {
                "deleteLabel": "削除"
            }
        },
        "editCustomFieldData": {
            "heading": "モバイルカスタムフィールドの値を編集",
            "description": "デバイス{{deviceName}}のカスタムフィールド値を編集",
            "inProgressMessage": "カスタムフィールド値を編集しています...",
            "successMessage": "フィールド値が正常に編集されました。",
            "errorMessage": "カスタムフィールド値の編集エラー",
            "buttons": {
                "actionButtonLabel": "保存"
            },
            "types": {
                "text": "テキスト",
                "date": "日付",
                "decimal": "10進法",
                "decimalWithSeparator": "10進法 (区切り記号なし)",
                "bytes": "バイト",
                "fileVersion": "ファイルバージョン",
                "ipAddress": "IP アドレス"
            }
        },
        "removePerformedAction": {
            "heading": "リストから削除",
            "description": "選択した実施済みアクションはリストから削除されます。",
            "buttons": {
                "deleteLabel": "削除"
            }
        },
        "reapplyPerformedAction": {
            "heading": "デバイス上で再実行",
            "description": "選択したアクションは、このデバイスで再実行されます。",
            "inProgressMessage": "再実行中...",
            "successMessage": "アクションが正常に再実行されました。",
            "errorMessage": "アクションの再実行エラー",
            "buttons": {
                "deleteLabel": "再実行"
            }
        }
    }
},
});