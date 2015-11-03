define({
  "amAssignableItem": {
    "shared": {},
    "models": {},
    "topNavSpec": {
        "assignableItemsTitle": "割り当て可能なアイテム",
        "list": {
            "content": "モバイルコンテンツ",
            "inHouseApplications": "インハウスアプリケーション",
            "thirdPartyApplications": "サードパーティアプリケーション",
            "bookstoreBooks": "ブックストアのブック",
            "configurationProfiles": "構成プロファイル",
            "provisioningProfiles": "プロビジョニングプロファイル",
            "actions": "アクション"
        }
    },
    "assignableItemsListPage": {
        "navTitle": "割り当て可能なアイテム",
        "body": {
            "totalSummary": "結果: {{total}} アイテム"
        }
    },
    "assignableContentsPage": {
        "title": "割り当て可能なコンテンツ",
        "header": {
            "buttons": {
                "addContent": "コンテンツの追加"
            }
        },
        "body": {
            "actionsMenu": {
                "options": {
                    "editContent": "コンテンツの編集",
                    "deleteContent": "コンテンツの削除"
                }
            }
        }
    },
    "assignableActionsDetailsPage": {
        "title": "割り当て可能なアクションの詳細",
        "tabList": {
            "details": "詳細",
            "policies": "ポリシー"
        },
        "tabDetails": {
            "header": "アクションの詳細",
            "type": "タイプ"
        },
        "tabPolicies": {
            "header": "このアクションに関連するスマートポリシー",
            "buttons": {
                "addActionToPolicies": "ポリシーにアクションを追加"
            },
            "actionsMenu": {
                "options": {
                    "editPolicyAssignmentProperties": "ポリシー割り当てプロパティを編集",
                    "removeActionFromPolicies": "ポリシーからアクションを削除"
                }
            }
        },
        "navigationPane": {
            "title": "すべてのアクション"
        }
    },
    "assignableContentDetailsPage": {
        "title": "割り当て可能なコンテンツの詳細",
        "navigationPane": {
            "title": "すべてのコンテンツ"
        },
        "tabList": {
            "details": "詳細",
            "policies": "ポリシー",
            "mobileDevices": "モバイルデバイス"
        },
        "tabDetails": {
            "header": "コンテンツの詳細",
            "leaveAbsoluteSafeLabel": "AbsoluteSafe から出る",
            "noPasscodePresentLabel": "パスコードあり",
            "wiFiDownloadOnlyLabel": "Wi-Fi ダウンロードのみ",
            "fileEmailAllowed": "ファイルをメール送信可能",
            "filePrintAllowed": "ファイルを印刷可能",
            "filenameHeader": "ファイル名",
            "fileTypeHeader": "ファイルのタイプ",
            "categoryHeader": "カテゴリ",
            "filesizeHeader": "ファイルのサイズ",
            "lastModifiedHeader": "最終更新日",
            "descriptionHeader": "説明"
        },
        "tabPolicies": {
            "header": "このコンテンツに関連するモバイルポリシー",
            "buttons": {
                "addContentToPolicies": "ポリシーへのコンテンツの追加"
            },
            "actionsMenu": {
                "options": {
                    "editPolicyAssignmentProperties": "ポリシー割り当てプロパティを編集",
                    "removeContentFromPolicies": "ポリシーをコンテンツから削除"
                }
            }
        },
        "tabMobileDevices": {
            "header": "このコンテンツに関連するモバイルデバイス"
        }
    },
    "assignableInHouseApplicationsPage": {
        "title": "割り当て可能なインハウスアプリケーション",
        "breadcrumbsTitle": "割り当て可能なインハウスアプリ"
    },
    "assignableThirdPartyApplicationsPage": {
        "title": "割り当て可能なサードパーティアプリケーション",
        "breadcrumbsTitle": "割り当て可能なサードパーティアプリ"
    },
    "assignableBookstoreBooksPage": {
        "title": "割り当て可能なブックストアのブック"
    },
    "assignableConfigurationProfilesPage": {
        "title": "割り当て可能な構成プロファイル"
    },
    "assignableProvisioningProfilesPage": {
        "title": "割り当て可能なプロビジョニングプロファイル"
    },
    "assignableActionsPage": {
        "title": "割り当て可能なアクション",
        "header": {
            "buttons": {
                "addActionMenu": {
                    "label": "アクションを追加",
                    "divider": "-----------------------------------------------",
                    "options": {
                        "sendMessage": "デバイスへメッセージを送信",
                        "setRoamingOptions": "ローミングオプションを設定",
                        "sendEmail": "メールを送信",
                        "demoteToUnmanagedDevice": "非管理中デバイスへの降格",
                        "removeConfigurationProfile": "構成プロファイルを削除",
                        "sendSms": "SMS を送信",
                        "freezeDevice": "デバイスをフリーズ",
                        "updateDeviceInfo": "デバイス情報のアップデート",
                        "setWallpaper": "壁紙を設定",
                        "setActivationLockOptions": "起動ロックオプションを設定",
                        "sendVppInvitation": "VPP招待を送る",
                        "registerUserInVpp": "VPP でのユーザー登録",
                        "retireUserFromVpp": "VPP からのユーザー退任",
                        "setDeviceName": "デバイス名を設定",
                        "setCustomFieldValue": "カスタムフィールド値の設定",
                        "attentionMode": "アテンションモードの設定"
                    }
                }
            }
        },
        "body": {
            "actionsMenu": {
                "options": {
                    "newAction": "新しいアクション",
                    "editAction": "アクションの編集",
                    "deleteAction": "アクションの削除",
                    "duplicateAction": "アクションの複製"
                }
            }
        }
    },
    "modals": {
        "addContent": {
            "heading": "新しいモバイルコンテンツの追加",
            "labelForTextField": "モバイルスマートポリシーの名前",
            "placeholder": "新しいスマートポリシーの名前",
            "stepLabels": {
                "uploadFiles": "ファイルのアップロード",
                "assignProperties": "プロパティの割り当て",
                "assignPolicy": "ポリシーの割り当て",
                "finish": "終了"
            },
            "uploadFileStep": {
                "dropFilesAreaDescription": "ここにファイルをドロップするか、クリックでファイルを選択...",
                "successUploadMessage": "{{numberOfTotalFiles}} 個のファイルのうち、{{numberOfUploadedFiles}} 個を正常にアップロードしました",
                "hint": "ヒント: ファイル名は編集できます",
                "uploadingMessage": "ファイルをアップロードしています...",
                "secondsLeft": "秒（残り）",
                "uploadingOf": " / ",
                "validation": {
                    "existOnServer": "\"{{nameOfFile}}\" はサーバー上にすでに存在します。",
                    "renameFileToContinue": "続行するには、下のファイル名を変更してください。",
                    "renameItToSave": "変更を保存するには、ファイル名を変更してください。",
                    "fileNameIsBlank": "下のファイルの名前を入力します。",
                    "nameIsBlank": "名前を入力します。",
                    "uploadFailed": "以下のエラーにより、ファイル \"{{nameOfFile}}\" のアップロードに失敗しました:",
                    "selectFilesNotFolder": "1 つ以上の読み取り可能なファイル (ディレクトリではなく) を択します。"
                }
            },
            "assignPropertiesStep": {
                "categoryLabel": "カテゴリーを入力します：",
                "categoryMenu": {
                    "options": {
                        "documents": "ドキュメント",
                        "multimedia": "マルチメディア",
                        "pictures": "ピクチャ",
                        "other": "その他"
                    }
                },
                "categoryInputPlaceholder": "カテゴリーを入力します",
                "categoryDescriptionPlaceholder": "説明を追加...",
                "assignPermissionsLabel": "{{numberOfFiles}} 個のコンテンツファイルに許可を割り当て:",
                "canLeaveCheckbox": "ファイルをAbsoluteSafe から出すことが可能",
                "canEmailCheckbox": "ユーザーによるファイルのメール送信可能",
                "canPrintCheckbox": "ユーザーによるファイル印刷可能",
                "downloadOnlyOverWiFiCheckbox": "Wi-Fi のみでのファイルダウンロード可能",
                "passphraseLabel": "パスフレーズ：",
                "enterPassphrasePlaceholder": "パスフレーズを入力",
                "confirmPassphrasePlaceholder": "パスフレーズを再度入力",
                "validation": {
                    "passphraseNotMatched": "パスフレーズが一致しません"
                },
                "uniqueFileNameError": "以下のファイル\"{{displayName}}\"に一意の名前を入力してください。"
            },
            "buttons": {
                "addContent": "コンテンツの追加"
            },
            "inProgressMessage": "新しいコンテンツを追加しています...",
            "successMessage": "新しいコンテンツが正常に追加されました",
            "errorMessage": "編集の保存エラー"
        },
        "deleteActions": {
            "heading": "アクションの削除",
            "description": "選択したアクションは完全に削除されます。",
            "buttons": {
                "deleteAction": "アクションの削除"
            }
        },
        "deleteAction": {
            "heading": "アクションの削除",
            "description": "選択したアクションは完全に削除されます。",
            "buttons": {
                "deleteAction": "アクションの削除"
            }
        },
        "deleteContent": {
            "heading": "コンテンツの削除",
            "description": "選択したコンテンツは完全に削除されます。",
            "buttons": {
                "deleteContent": "コンテンツの削除"
            }
        },
        "editContent": {
            "heading": "コンテンツの編集",
            "labelForName": "名前：",
            "labelForPermissions": "許可："
        },
        "actionProperties": {
            "indicatesRequiredField": "必須入力項目を示します",
            "charactersRemainingInField": "残り{{characterCount}}文字",
            "customField": "カスタムフィールド",
            "actionName": "アクション名",
            "actionType": "アクションタイプ",
            "name": "名前",
            "description": "説明",
            "actionMessage": "ターゲットデバイスのユーザーは、これらの設定を変更することができます。",
            "targetPlatforms": "ターゲットプラットフォーム",
            "ios": "iOS",
            "iosOnly": "iOS のみ",
            "android": "Android",
            "windows": "Windows Phone",
            "iosAndroid": "iOS, Android",
            "iosAndroidWindows": "iOS, Android, Windows Phone",
            "iosWindows": "iOS, Windows Phone",
            "androidWindows": "Android, Windows Phone",
            "none": "なし",
            "lastModified": "最終更新日",
            "messageText": "メッセージの本文",
            "voiceRoaming": "音声ローミング",
            "dataRoaming": "データローミング",
            "on": "オン",
            "off": "オフ",
            "leaveAsIs": "そのままにする",
            "emailTo": "宛先",
            "emailCc": "CC",
            "emailSubject": "件名",
            "message": "メッセージ",
            "phoneNumber": "電話番号",
            "activationLock": "アクティベーションロック",
            "disallow": "禁止",
            "allow": "許可",
            "disallowed": "禁止",
            "allowed": "許可",
            "deviceName": "デバイス名",
            "dataType": "データ型",
            "dataValue": "値",
            "setValue": "次の値に設定",
            "removeValue": "削除",
            "attentionMode": "アテンションモード",
            "enable": "有効化",
            "disable": "無効化",
            "enabled": "有効",
            "disabled": "無効",
            "attentionMessage": "アテンションメッセージ",
            "newPasscode": "新しいパスコード",
            "verification": "新しいパスコードを確認入力",
            "passphraseNotMatched": "パスコードが一致しません",
            "passphraseErrorMessage": "パスコードは 4 ～ 16 文字としてください。",
            "image": "画像",
            "imageDimensions": "画像のサイズ",
            "wallpaperOptions": "壁紙のオプション",
            "wallpaperPicture": "壁紙の写真",
            "lockScreen": "ロック画面",
            "homeScreen": "ホーム画面",
            "selectImage": "画像を選択",
            "imageFormat": "PNG または JPEG フォーマット",
            "noImageSelected": "画像が選択されていません。",
            "readingImage": "画像を読み込んでいます…",
            "wallpaperInfoTitleTooltip": "iOS 画面の解像度基準",
            "wallpaperInfoiPhone6PlusTooltip": "<strong>iPhone 6 Plus:</strong> 2208 x 1242 ピクセル (回転をサポートするには 2208 x 2208)",
            "wallpaperInfoiPhone6Tooltip": "<strong>iPhone 6:</strong> 1134 x 750",
            "wallpaperInfoiPhone5Tooltip": "<strong>iPhone 5:</strong> 1136 x 640",
            "wallpaperInfoiPhone4Tooltip": "<strong>iPhone 4:</strong> 960 x 640",
            "wallpaperInfoiPadRetinaTooltip": "<strong>iPad retina モデル:</strong> 2048 x 1536 (回転をサポートするには 2048 x 2048)",
            "wallpaperInfoiPadTooltip": "<strong>iPad:</strong> 1024 x 768 (1024 x 1024)",
            "vppAccount": "VPP アカウント",
            "registerOptions": "オプションの登録",
            "registerOnly": "ユーザー登録のみ",
            "registerAndInvite": "ユーザー登録と招待",
            "registerOnlyMessage": "このオプションを選択した場合、プログラムによりアプリをダウンロードして使用できるようになるには、ユーザーは後に招待されApple ID とVolume Purchase Program (VPP) を関連付けなければなりません。",
            "sendInvitation": "招待を送る",
            "mdm": "MDM ダイアログ",
            "webClip": "Web クリップ (iOS ホーム画面のブックマーク)",
            "email": "E メール",
            "absoluteMessage": "AbsoluteApps メッセージ",
            "sms": "SMS",
            "smsText": "SMS のテキスト",
            "text": "テキスト",
            "subject": "件名",
            "registerEmailSubject": "Apple Volume Purchase Program (VPP) に登録",
            "registerSmsText": "会社支給のアプリを受け取るには、この URL にアクセスして、お使いの Apple ID を${MDU_Company} VPP アカウントに登録してください: ${MD_VPPInviteURL}",
            "registerEmailDisplayTitle": "${MDU_DisplayName}:",
            "registerEmailDisplayText": "下の URL にアクセスして、お使いの Apple ID を${MDU_Company} Apple Volume Purchase Program (VPP) アカウントに登録してください:",
            "registerEmailUrlTitle": "${MD_VPPInviteURL}",
            "registerEmailUrlText": "これにより、会社支給のアプリをお使いの Apple デバイスで費用負担なしで受け取ることができ、会社はこれらのアプリをお客様に自動的に割り当てられるようになります。",
            "profile": "プロフィール",
            "iosProfilesTitle": "iOS 構成プロファイル",
            "androidProfilesTitle": "Android 構成プロファイル",
            "validation": {
                "requiredPlatformMessage": "少なくとも一つのプラットフォームを選択する必要があります。",
                "duplicateNameMessage": "固有のアクション名を入力してください。",
                "requiredScreenOptionMessage": "少なくとも一つのオプションを選択する必要があります。",
                "requiredRoamingOptionMessage": "両方のオプションを\"そのままにする\"ことはできません (実施するアクションがないため)。",
                "fileVersionWrongMessage": "ファイルバージョンが無効です。",
                "emptyListOfOptionsMessage": {
                    "configProfiles": "構成プロフィールがありません。",
                    "customFields": "カスタムフィールドがありません。",
                    "vppAccounts": "VPPアカウントがありません。"
                }
            }
        },
        "action": {
            "saveAndAssignButton": "保存してポリシーに割り当て...",
            "create": {
                "inProgressMessage": "新しいアクションを追加しています...",
                "successMessage": "新しいアクションが正常に追加されました。",
                "errorMessage": "新しいアクションの保存エラー"
            },
            "duplicate": {
                "inProgressMessage": "アクションを複製しています...",
                "successMessage": "新しいアクションが正常に追加されました。",
                "errorMessage": "アクションの複製エラー"
            },
            "policyAssignment": {
                "policyName": "ポリシー名",
                "message": "アクションはスマートポリシーにのみ割り当てできます。",
                "addActionToPolicies": {
                    "heading": "ポリシーにアクションを追加"
                },
                "addActionsToPolicy": {
                    "heading": "<strong>{{selectedContextName}}</strong> にアクションを追加"
                },
                "timeComponent": {
                    "delayTitle": "アクション開始遅延",
                    "repeatTitle": "アクションのリピート",
                    "repeat": "リピート",
                    "frequency": "頻度",
                    "numberOfTimes": "回数",
                    "numberValidation": "このフィールドに数値を入力してください。"
                },
                "edit": {
                    "heading": "<strong>{{policyName}}</strong> の割り当てプロパティを編集"
                },
                "remove": {
                    "headingPolicy": "ポリシーからアクションを削除",
                    "headingPolicies": "選択したポリシーからアクションを削除",
                    "descriptionPolicies": "アクションをこれらのポリシーから削除してよろしいですか？",
                    "headingAction": "ポリシーからアクションを削除",
                    "descriptionActions": "選択したアクションをこのポリシーから削除してよろしいですか？",
                    "buttonActions": "アクションの削除",
                    "buttonAction": "アクションの削除"
                }
            }
        }
    }
},
});