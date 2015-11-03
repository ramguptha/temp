define({
  "amMobilePolicies": {
    "shared": {
        "notUniqueNameMessage": "一意のポリシー名を入力してください。",
        "assignmentRuleLabel": "割り当てルール:",
        "assignmentRuleLabelAutoInstallTooltip": "<strong>自動インストール:</strong> これらのアプリケーションは、ポリシーに属するデバイスに自動的にインストールされます。",
        "assignmentRuleLabelOnDemandTooltip": "<strong>オンデマンド:</strong> ポリシーに属するデバイスのユーザーは、必要であればこれらのアプリケーションを AbsoluteApps からインストールすることができます。",
        "assignmentRuleLabelAutoInstallAutoRemoveTooltip": "<strong>自動インストール、自動削除:</strong> これらのアプリケーションは、ポリシーに追加されたデバイスに自動的にインストールされ、ポリシーを離れるデバイスから自動的に削除されます。",
        "assignmentRuleLabelOnDemandAutoRemoveTooltip": "<strong>オンデマンド、自動削除:</strong> ポリシーに属するデバイスのユーザーは、必要であればこれらのアプリケーションを AbsoluteApps からインストールすることができます。アプリケーションは、ポリシーから離れるデバイスから自動的に削除されます。",
        "assignmentRuleLabelForbiddenTooltip": "<strong>禁止:</strong> これらのアプリケーションは、ポリシーに属するデバイスにインストールできません。",
        "assignmentRuleLabelProfileAutoInstallTooltip": "<strong>自動インストール:</strong> これらのプロファイルは、ポリシーに属するデバイスに自動的にインストールされます。",
        "assignmentRuleLabeProfilelOnDemandTooltip": "<strong>オンデマンド:</strong> ポリシーに属するデバイスのユーザーは、必要であればこれらのプロファイルを AbsoluteApps からインストールすることができます。",
        "assignmentRuleLabelProfileAutoInstallAutoRemoveTooltip": "<strong>自動インストール、自動削除:</strong> これらのプロファイルは、ポリシーに追加されたデバイスに自動的にインストールされ、ポリシーを離れるデバイスから自動的に削除されます。",
        "assignmentRuleLabelProfileOnDemandAutoRemoveTooltip": "<strong>オンデマンド、自動削除:</strong> ポリシーに属するデバイスのユーザーは、必要であればこれらのプロファイルを AbsoluteApps からインストールすることができます。プロファイルは、ポリシーから離れるデバイスから自動的に削除されます。",
        "assignmentRuleLabelProfileForbiddenTooltip": "<strong>禁止:</strong> これらのプロファイルは、ポリシーに属するデバイスにインストールできません。",
        "assignmentRuleLabelMediaAutoInstallTooltip": "<strong>自動インストール:</strong> これらのメディアデバイスは、デバイスがポリシーに入ると自動的にデバイスにダウンロードされます。デバイスがポリシーを離れてもファイルはデバイスに留まり、デバイスのユーザーが必要であればファイルを手動で削除することができます。",
        "assignmentRuleLabelMediaOnDemandTooltip": "<strong>オンデマンド:</strong> ポリシーに属するデバイスのユーザーが必要であれば、これらのメディアファイルを手動でダウンロードすることができます。デバイスがポリシーを離れてもファイルはデバイスに留まり、デバイスのユーザーが希望すればファイルを手動で削除することができます。",
        "assignmentRuleLabelMediaAutoInstallAutoRemoveTooltip": "<strong>自動インストール、自動削除:</strong> これらのメディアファイルは、ポリシーに追加されたデバイスに自動的にダウンロードされ、ポリシーを離れるデバイスから自動的に削除されます。",
        "assignmentRuleLabelMediaOnDemandAutoRemoveTooltip": "<strong>オンデマンド、自動削除:</strong> ポリシーに属するデバイスのユーザーは、必要であればこれらのメディアファイルを手動でダウンロードすることができます。ファイルは、ポリシーから離れるデバイスから自動的に削除されます。",
        "assignmentRuleOptions": {
            "autoInstall": "自動インストール",
            "onDemand": "オンデマンド",
            "autoInstallAutoRemove": "自動インストール、自動削除",
            "onDemandAutoRemove": "オンデマンド、自動削除",
            "forbidden": "禁止"
        },
        "availabilitySelector": {
            "always": "常時",
            "dailyInterval": "1日ごと",
            "fixedPeriod": "固定期間"
        },
        "setAvailabilityTimeLabel": "利用可能時間を設定",
        "buttons": {
            "addToPolicy": "ポリシーへの追加",
            "addToPolicies": "ポリシーへ追加"
        }
    },
    "models": {},
    "topNavSpec": {
        "mobilePoliciesTitle": "モバイルポリシー",
        "allMobilePoliciesTitle": "すべてのモバイルポリシー"
    },
    "mobilePoliciesListPage": {
        "title": "モバイルポリシー",
        "addPolicyMenu": {
            "label": "ポリシーを追加",
            "options": {
                "newPolicy": "新規ポリシー",
                "newSmartPolicy": "新しいスマートポリシー"
            }
        },
        "actionsMenu": {
            "label": "アクション",
            "options": {
                "deletePolicy": "ポリシーを削除",
                "deletePolicies": "ポリシーを削除",
                "editPolicy": "ポリシーを編集",
                "editSmartPolicy": "スマートポリシーを編集"
            }
        },
        "totalSummary": "結果: {{total}} モバイルポリシー",
        "gridHeaders": {
            "policyName": "ポリシー名",
            "smartPolicy": "スマートポリシー"
        }
    },
    "mobilePolicyDetailsPage": {
        "title": "モバイルポリシーの詳細",
        "navigationPane": {
            "title": "モバイルポリシー",
            "readOnlyPolicy": "読み取り専用ポリシー"
        },
        "body": {
            "tabLabels": {
                "mobileDevices": "モバイルデバイス",
                "inHouseApplications": "インハウスアプリケーション",
                "thirdPartyApplications": "サードパーティアプリケーション",
                "configurationProfiles": "構成プロファイル",
                "content": "コンテンツ",
                "actions": "アクション"
            },
            "tabDescriptions": {
                "mobileDevices": "このモバイルポリシーに関連するデバイス",
                "inHouseApplications": "このモバイルポリシーに追加されたインハウスアプリケーション",
                "thirdPartyApplications": "このモバイルポリシーに追加されたサードパーティアプリケーション",
                "configurationProfiles": "このモバイルポリシーに追加された構成プロファイル",
                "content": "このモバイルポリシーに関連するコンテンツ",
                "actions": "このモバイルポリシーに関連するアクション"
            },
            "buttons": {
                "addMobileDevicesToPolicy": "モバイルデバイスをポリシーに追加",
                "addInHouseApplication": "インハウスアプリケーションを追加",
                "addThirdPartyApplication": "サードパーティアプリケーションを追加",
                "addConfigurationProfile": "構成プロファイルを追加",
                "addContentToPolicy": "コンテンツをポリシーに追加",
                "addActionsToPolicy": "ポリシーにアクションを追加"
            },
            "actionsMenu": {
                "options": {
                    "removeFromPolicy": "ポリシーから削除",
                    "moveToAnotherPolicy": "別のポリシーに移動",
                    "removeInHouseApplication": "ポリシーから削除",
                    "removeThirdPartyApplication": "ポリシーから削除",
                    "editPolicyAssignmentProperties": "ポリシー割り当てプロパティを編集",
                    "editActionAssignmentProperties": "アクション割り当てプロパティの編集",
                    "removeConfigurationProfile": "ポリシーから削除",
                    "removeContentFromPolicy": "ポリシーから削除",
                    "removeActionFromPolicy": "ポリシーから削除",
                    "reexecuteActionOnPolicy": "ポリシーデバイスへの再実行"
                }
            },
            "totalSummary": "結果: {{total}} モバイルデバイス"
        }
    },
    "modals": {
        "createNewPolicy": {
            "heading": "モバイルポリシーを作成",
            "labelForTextField": "ポリシー名",
            "placeholder": "新しいポリシー名",
            "buttons": {
                "createPolicy": "ポリシーを作成"
            },
            "inProgressMessage": "ポリシーを作成しています...",
            "successMessage": "ポリシーが正常に作成されました。",
            "errorMessage": "ポリシーの作成中にエラーが発生しました。"
        },
        "createNewSmartPolicy": {
            "heading": "モバイルスマートポリシーを作成",
            "labelForTextField": "モバイルスマートポリシーの名前",
            "placeholder": "新しいスマートポリシーの名前",
            "stepLabels": {
                "properties": "プロパティを設定",
                "createSmartFilter": "スマートフィルタを作成",
                "verify": "確認して保存"
            },
            "conditionSelectionLabel": "以下に一致するモバイルデバイスを含む:",
            "conditionSelectionOptions": {
                "all": "指定された条件のすべて",
                "any": "指定された条件のいずれか"
            },
            "conditionSelectionAppAndProfilesLabel": "以下の条件のモバイルデバイスを含む:",
            "conditionSelectionAppAndProfilesOptions": {
                "allAppMissing": "指定されたすべてのアプリケーションが無い",
                "allAppInstalled": "指定されたすべてのアプリケーションがインストールされている",
                "someAppMissing": "指定されたアプリケーションの一部が無い",
                "someAppInstalled": "指定されたアプリケーションの一部がインストールされている",
                "allProfilesMissing": "指定されたすべての構成プロファイルが無い",
                "allProfilesInstalled": "指定されたすべての構成プロファイルがインストールされている",
                "someProfilesMissing": "指定された構成プロファイルの一部が無い",
                "someProfilesInstalled": "指定された構成プロファイルの一部がインストールされている"
            },
            "radioButtonLabels": {
                "mobileDevices": "モバイルデバイス",
                "mobileDevicesByInstalledApplications": "インストール済みアプリケーション別モバイルデバイス",
                "mobileDevicesByAddedConfigurationProfiles": "追加された構成プロファイル別モバイルデバイス"
            },
            "buttons": {
                "createPolicy": "ポリシーを作成"
            },
            "filterLabel": "フィルター:",
            "confirmMessage": "下記のフィルターによってスマートポリシーを作成するには、保存をクリックします。",
            "confirmMessageEdit": "下記のフィルターによってスマートポリシーを編集するには、保存をクリックします。",
            "inProgressMessage": "モバイルスマートポリシーを作成しています...",
            "successMessage": "ポリシーが正常に作成されました。",
            "errorMessage": "モバイルスマートポリシーの作成エラーです。",
            "englishOnlyWarningMessage": "このフィルターは英語のエラーメッセージの検索のみに有効です。"
        },
        "deletePolicy": {
            "policyHeading": "ポリシーを削除",
            "policiesHeading": "ポリシーを削除",
            "policyDescription": "選択したポリシーは完全に削除されます。",
            "policiesDescription": "選択したポリシーは完全に削除されます。",
            "buttons": {
                "deletePolicy": "ポリシーを削除",
                "deletePolicies": "ポリシーを削除"
            }
        },
        "editPolicy": {
            "heading": "モバイルポリシーを編集",
            "description": "このポリシーの名前を変更します。",
            "buttons": {
                "createPolicy": "ポリシーを編集"
            }
        },
        "editSmartPolicy": {
            "heading": "スマートモバイルポリシーを編集",
            "description": "このポリシーの名前を変更します。",
            "stepLabels": {
                "properties": "プロパティを編集",
                "editSmartFilter": "スマートフィルタを編集"
            },
            "buttons": {
                "createPolicy": "ポリシーを編集"
            }
        },
        "addMobileDevices": {
            "heading": "<strong>{{policyName}}</strong>にモバイルデバイスを追加"
        },
        "addInHouseApplication": {
            "headingPolicy": "<strong>{{policyName}}</strong> にインハウスアプリケーションを追加",
            "headingPolicies": "選択したポリシーにインハウスアプリケーションを追加"
        },
        "removeInHouseApplication": {
            "heading": "インハウスアプリケーションをポリシーから削除",
            "description": "選択したインハウスアプリケーションをこのポリシーから削除してよろしいですか？",
            "buttons": {
                "removeApplication": "アプリケーションを削除"
            }
        },
        "addThirdPartyApplication": {
            "headingPolicy": "<strong>{{policyName}}</strong>にサードパーティアプリケーションを追加",
            "headingPolicies": "選択したポリシーにサードパーティアプリケーションを追加",
            "selectionOnDemandAndroidWarning": "Android アプリは\"オンデマンド\"でのみインストールできます",
            "selectionOnDemandMultipleWarning": "複数のアプリの一式は\"オンデマンド\"でのみインストールできます"
        },
        "removeThirdPartyApplication": {
            "heading": "サードパーティアプリケーションをポリシーから削除",
            "description": "選択したサードパーティアプリケーションをこのポリシーから削除してよろしいですか？",
            "buttons": {
                "removeApplication": "アプリケーションを削除"
            }
        },
        "addConfigurationProfile": {
            "heading": "<strong>{{policyName}}</strong>に構成プロファイルを追加"
        },
        "removeConfigurationProfile": {
            "heading": "構成プロファイルを削除",
            "description": "このアクションは、選択した構成プロファイルをポリシーから削除します。",
            "descriptionWarning": "このアクションは元に戻せません。",
            "buttons": {
                "removeProfile": "プロファイルを削除"
            }
        },
        "editPolicyAssignmentProperties": {
            "heading": "選択した構成プロファイルのポリシー割り当てプロパティを編集",
            "buttons": {
                "removeProfile": "プロファイルを削除"
            }
        },
        "addContent": {
            "headingPolicy": "<strong>{{selectedContextName}}</strong>へのコンテンツの追加",
            "policyName": "ポリシー名",
            "headingPolicies": "ポリシーへのコンテンツの追加",
            "contentName": "コンテンツ名"
        },
        "editContent": {
            "headingEditContent": "選択したコンテンツのポリシー割り当てプロパティを編集",
            "headingEditPolicy": "<strong>{{policyName}}</strong> の割り当てプロパティを編集",
            "headingEditPolicies": "選択したポリシーの割り当てプロパティを編集",
            "buttons": {
                "editContent": "コンテンツの編集"
            }
        },
        "removeContent": {
            "headingPolicy": "コンテンツをポリシーから削除",
            "headingPolicies": "コンテンツを選択したポリシーから削除",
            "descriptionSelectedFile": "選択したコンテンツファイルをこのポリシーから削除してよろしいですか？",
            "descriptionSelectedFiles": "選択したコンテンツファイルをこのポリシーから削除してよろしいですか？",
            "descriptionPolicy": "コンテンツファイルをこのポリシーから削除してよろしいですか？",
            "descriptionPolicies": "コンテンツファイルをこれらのポリシーから削除してよろしいですか？",
            "buttons": {
                "removeContent": "コンテンツを削除する"
            }
        },
        "moveDevices": {
            "heading": "デバイスを別のポリシーに移動",
            "buttons": {
                "moveMobileDevices": "モバイルデバイスを移動"
            }
        },
        "removeDevices": {
            "headingDevices": "モバイルデバイスをポリシーから削除",
            "actionWarning": "注記: \"自動削除\"コンテンツに加えて、このアクションはポリシーに関連付けられた\"自動削除\"アプリケーション、アクション、プロビジョニングまたは構成プロファイルも選択したデバイスから削除します。",
            "buttons": {
                "removeDevices": "デバイスを削除"
            }
        },
        "reexecuteAction": {
            "heading": "ポリシーデバイスへの再実行",
            "description": "選択したアクションは、このポリシーに関連付け済みのデバイスで再実行されます。",
            "inProgressMessage": "再実行中...",
            "successMessage": "アクションが正常に再実行されました。",
            "errorMessage": "アクションの再実行エラー",
            "buttons": {
                "actionButton": "再実行"
            }
        }
    }
},
});