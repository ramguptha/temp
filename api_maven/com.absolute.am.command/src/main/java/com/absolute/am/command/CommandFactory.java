/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */

package com.absolute.am.command;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.absolute.am.dal.model.CustomFieldActionDefinition;
import com.absolute.am.dal.model.MobileMedia;
import com.absolute.am.dal.model.ConfigurationProfile;
import com.absolute.am.dal.model.ProvisioningProfile;
import com.absolute.am.dal.model.iOSAppStoreApplications;
import com.absolute.am.dal.model.iOSApplications;
import com.absolute.am.model.CustomField;
import com.absolute.am.model.customfieldmobiledevice.CustomFieldItem;
import com.absolute.am.model.command.MobileDevicePerformedAction;
import com.absolute.am.model.policyaction.PolicyUuidActionUuidMapping;
import com.absolute.util.*;

/**
 * @author dlavin
 * 
 */
public class CommandFactory {

	public static CobraAdminMiscDatabaseCommand createGenericCommand(UUID adminUUID, int commandId, Map<String, Object> commandParametersMap) {
		CommandInfoData commandInfoData = new CommandInfoData(commandId, CobraProtocol.kCobraAdminServer);
		PropertyList commandParameters = new PropertyList(commandParametersMap);

		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	// WARNING: the AM Console command takes an array of device ids so we may not need
	// to extend this command to take an array of devices/identifiers too
	public static CobraAdminMiscDatabaseCommand modifyCustomFieldFromDeviceCommand(Long deviceId, String targetIdentifier,
			CustomFieldItem[] items, boolean deleteItems, UUID adminUUID) {
		PropertyList commandParameters = new PropertyList();

		ArrayList<Long> deviceIds = new ArrayList<Long>();
		ArrayList<String> targetIdentifiers = new ArrayList<String>();
		
		deviceIds.add(deviceId);
		targetIdentifiers.add(targetIdentifier);
		
		// deviceType == 2 seems to suggest a mobile device
		commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_DeviceType, 2);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_RecordIDListMobileDevices, deviceIds);
		
		if(deleteItems){		
			ArrayList<String> itemsList = new ArrayList<String>();
			for (CustomFieldItem item : items){
				itemsList.add(item.id);
			}
			
			commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_SharedDynamicFields, new ArrayList<String>());
			commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_RemovedFields, itemsList);
		} else {
			HashMap<String, Object> dynamicField;
			ArrayList<HashMap<String, Object>> itemsList = new ArrayList<HashMap<String, Object>>();
			String itemValueType = CobraCommandDefs.kCobra_Admin_CustomField_Value;
			
			for (CustomFieldItem item : items){
				dynamicField = new HashMap<String, Object>();
				dynamicField.put(CobraCommandDefs.kCobra_Admin_CustomField_FieldID, item.id);
				// Data Type: 1=String, 2=Number, 3=Boolean, 4=Date, 5=File Version, 6=IP Address, 7=Enumeration
				if(item.type == 3 || item.type == 2 || item.type == 6 || item.type == 5){
					itemValueType = CobraCommandDefs.kCobra_Admin_CustomField_NumberValue;
				} else if(item.type == 4){
					itemValueType = CobraCommandDefs.kCobra_Admin_CustomField_DateValue;
				}
				
				dynamicField.put(itemValueType, item.value);
				itemsList.add(dynamicField);
			}
			
			commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_SharedDynamicFields, itemsList);
			commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_RemovedFields, new ArrayList<String>());
		}
		
		commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_TargetIdentifierList, targetIdentifiers);
		
		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_ModifyCustomFieldFromDevice_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return iOSDatabaseCommand;
	}
	
	public static CobraAdminMiscDatabaseCommand updateCustomFieldCommand(String id, CustomField newCustomField,
			ArrayList<com.absolute.am.dal.model.CustomField> allCustomFields, UUID adminUUID) throws IOException {
		
		PropertyList commandParameters = new PropertyList();
		PropertyList dynamicFields = new PropertyList();
		PropertyList fieldActionsData = new PropertyList();

		for (com.absolute.am.dal.model.CustomField existingCustomField : allCustomFields) {
			if (id.equals(existingCustomField.id)) {
				existingCustomField.name = newCustomField.name == null ? existingCustomField.name : newCustomField.name;
				existingCustomField.description = newCustomField.description;
				existingCustomField.variableName = newCustomField.variableName;
				existingCustomField.dataType = newCustomField.dataType == null ? existingCustomField.dataType : newCustomField.dataType;
				existingCustomField.displayType = newCustomField.displayType == null ? existingCustomField.displayType
						: newCustomField.displayType;
				if(newCustomField.enumerationList != null){
					String enums = "<array>";
					for(String enumItem : newCustomField.enumerationList){
						enums += "<string>" + enumItem + "</string>\n";
					}
					enums += "</array>";
					existingCustomField.enumerationList = StringUtilities.serializeObjToBytes(enums);
				}
				existingCustomField.seed++;
			}

			addCustomFieldToSet(dynamicFields, fieldActionsData, existingCustomField);
		}

		commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_DynamicFields, dynamicFields);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_FieldActions_Param, fieldActionsData);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_PushChangesToServer_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand deleteCustomFieldCommand(String ids[],
			ArrayList<com.absolute.am.dal.model.CustomField> allCustomFields, UUID adminUUID) {
		PropertyList commandParameters = new PropertyList(), dynamicFields = new PropertyList(), fieldActionsData = new PropertyList();

		for (com.absolute.am.dal.model.CustomField existingCustomField : allCustomFields) {
			if (!Arrays.asList(ids).contains(existingCustomField.id)) {
				addCustomFieldToSet(dynamicFields, fieldActionsData, existingCustomField);
			}
		}

		commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_DynamicFields, dynamicFields);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_FieldActions_Param, fieldActionsData);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_PushChangesToServer_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand createCustomFieldCommand(CustomField field,
			ArrayList<com.absolute.am.dal.model.CustomField> allCustomFields, UUID adminUUID) {
		
		PropertyList commandParameters = new PropertyList();
		PropertyList dynamicFields = new PropertyList(); 
		PropertyList dynamicFieldsData = new PropertyList(); 
		PropertyList fieldActionsData = new PropertyList();

		field.uniqueId = UUID.randomUUID().toString();

		// Add two new dummy field actions for the new custom field with populating data to the table [custom_field_action_definitions]
		field.customFieldActionDefinitionIds.add(UUID.randomUUID().toString());
		field.customFieldActionDefinitionIds.add(UUID.randomUUID().toString());
		String[] actionUuidList = field.customFieldActionDefinitionIds.toArray(new String[field.customFieldActionDefinitionIds.size()]);
		dynamicFieldsData.put(iOSDevicesDefines.kCobra_iOS_ActionList_Param, actionUuidList);
		
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_AutoAssignToAllMachines, field.defaultAutoAssignToAllMachines);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_DataType, field.dataType);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_Description, field.description);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_DeviceType, field.defaultDeviceType);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_DisplayType, field.displayType);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_EvaluationMethod, field.defaultEvaluationMethod);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_FieldID, field.uniqueId);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_Name, field.name);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_Seed, field.defaultSeed);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_VariableName, field.variableName);
		
		for (int i = 0; i < actionUuidList.length; i++) {
			fieldActionsData.put(actionUuidList[i], createCustomFieldActionsPlist(actionUuidList[i], field.name, i + 1));
		}

		if (field.enumerationList != null) {
			dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_EnumerationList, field.enumerationList);
		}

		dynamicFields.put(field.uniqueId, dynamicFieldsData);

		for (com.absolute.am.dal.model.CustomField existingCustomField : allCustomFields) {
			addCustomFieldToSet(dynamicFields, fieldActionsData, existingCustomField);
		}

		commandParameters.put(CobraCommandDefs.kCobra_Admin_CustomField_DynamicFields, dynamicFields);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_FieldActions_Param, fieldActionsData);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_PushChangesToServer_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return iOSDatabaseCommand;
	}
	
	private static void addCustomFieldToSet(PropertyList dynamicFields, PropertyList fieldActionsData, com.absolute.am.dal.model.CustomField existingCustomField) {
		PropertyList dynamicFieldsData = new PropertyList();

		ArrayList<String> actionIds = new ArrayList<String>();
		for (CustomFieldActionDefinition def : existingCustomField.customFieldActionDefinitions) {
			actionIds.add(def.id);
		}
		
		dynamicFieldsData.put(iOSDevicesDefines.kCobra_iOS_ActionList_Param, actionIds.toArray());
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_AutoAssignToAllMachines,
				existingCustomField.autoAssignToAllMachines);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_DataType, existingCustomField.dataType);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_Description, existingCustomField.description);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_DeviceType, existingCustomField.deviceType);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_DisplayType, existingCustomField.displayType);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_EvaluationMethod, existingCustomField.evaluationMethod);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_FieldID, existingCustomField.id);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_Name, existingCustomField.name);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_Seed, existingCustomField.seed);
		dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_VariableName, existingCustomField.variableName);

		if (existingCustomField.enumerationList != null) {
			dynamicFieldsData.put(CobraCommandDefs.kCobra_Admin_CustomField_EnumerationList,
					StringUtilities.extractArrayAsStringFromPlist(new String(existingCustomField.enumerationList)));
		}

		// 'FieldActions' plist to the existing custom fields 
		for (CustomFieldActionDefinition def : existingCustomField.customFieldActionDefinitions) {
			fieldActionsData.put(def.id, createCustomFieldActionsPlist(def));
		}
		
		dynamicFields.put(existingCustomField.id, dynamicFieldsData);
	}

	private static PropertyList createCustomFieldActionsPlist(String actionUuid, String customFieldName, int platform)
	{
		PropertyList pl = new PropertyList();
		
		pl.put(iOSDevicesDefines.kCobra_iOS_ActionID_Param, actionUuid);
		pl.put(iOSDevicesDefines.kCobra_iOS_ExecutableOptions_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_ExecutablePartialPath_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_ExecutableTypeSelector_Param, 0);
		pl.put(iOSDevicesDefines.kCobra_iOS_ExecuteOnlyWithFullInventory_Param, false);
		pl.put(iOSDevicesDefines.kCobra_iOS_CustomFieldName_Param, customFieldName);
		pl.put(iOSDevicesDefines.kCobra_iOS_PlistDomain_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_PlistKey_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_PlistLocationSelector_Param, 1);
		pl.put(iOSDevicesDefines.kCobra_iOS_CustomeFieldPlatForm_Param, platform);
		pl.put(iOSDevicesDefines.kCobra_iOS_RegistryPath_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_ReplaceLineFeeds_Param, true);
		pl.put(iOSDevicesDefines.kCobra_iOS_RequiresAdminPrivileges_Param, 0);
		pl.put(iOSDevicesDefines.kCobra_iOS_ReturnExecutionErrors_Param, false);
		pl.put(iOSDevicesDefines.kCobra_iOS_ScriptText_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_CustomFieldSeed_Param, 1);
		pl.put(iOSDevicesDefines.kCobra_iOS_SourceFile_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_SourceFileChecksum_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_SourceTypeSelector_Param, 0);
		pl.put(iOSDevicesDefines.kCobra_iOS_TransferExecutableFolder_Param, 0);
		pl.put(iOSDevicesDefines.kCobra_iOS_UserContextr_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_UserContextPassword_Param, "");
		pl.put(iOSDevicesDefines.kCobra_iOS_UserContextSelector_Param, 1);
		
		return pl;
	}
	
	private static PropertyList createCustomFieldActionsPlist(CustomFieldActionDefinition def)
	{
		PropertyList pl = new PropertyList();
		
		pl.put(iOSDevicesDefines.kCobra_iOS_ActionID_Param, def.id);
		pl.put(iOSDevicesDefines.kCobra_iOS_ExecutableOptions_Param, def.executableOptions);
		pl.put(iOSDevicesDefines.kCobra_iOS_ExecutablePartialPath_Param, def.executablePartialPath);
		pl.put(iOSDevicesDefines.kCobra_iOS_ExecutableTypeSelector_Param, def.executableTypeSelector);
		pl.put(iOSDevicesDefines.kCobra_iOS_ExecuteOnlyWithFullInventory_Param, (def.executeOnlyWithFullInventory != 0));
		pl.put(iOSDevicesDefines.kCobra_iOS_CustomFieldName_Param, def.name);
		pl.put(iOSDevicesDefines.kCobra_iOS_PlistDomain_Param, def.pListDomain);
		pl.put(iOSDevicesDefines.kCobra_iOS_PlistKey_Param, def.pListKey);
		pl.put(iOSDevicesDefines.kCobra_iOS_PlistLocationSelector_Param, def.pListLocationSelector);
		pl.put(iOSDevicesDefines.kCobra_iOS_CustomeFieldPlatForm_Param, def.platform);
		pl.put(iOSDevicesDefines.kCobra_iOS_RegistryPath_Param, def.registryPath);
		pl.put(iOSDevicesDefines.kCobra_iOS_ReplaceLineFeeds_Param, (def.replaceLineFeeds != 0));
		pl.put(iOSDevicesDefines.kCobra_iOS_RequiresAdminPrivileges_Param, def.requiresAdminPrivileges);
		pl.put(iOSDevicesDefines.kCobra_iOS_ReturnExecutionErrors_Param, (def.returnExecutionErrors != 0));
		pl.put(iOSDevicesDefines.kCobra_iOS_ScriptText_Param, def.scriptText);
		pl.put(iOSDevicesDefines.kCobra_iOS_CustomFieldSeed_Param, def.seed);
		pl.put(iOSDevicesDefines.kCobra_iOS_SourceFile_Param, def.sourceFile);
		pl.put(iOSDevicesDefines.kCobra_iOS_SourceFileChecksum_Param, def.sourceFileChecksum);
		pl.put(iOSDevicesDefines.kCobra_iOS_SourceTypeSelector_Param, def.sourceTypeSelector);
		pl.put(iOSDevicesDefines.kCobra_iOS_TransferExecutableFolder_Param, def.transferExecutableFolder);
		pl.put(iOSDevicesDefines.kCobra_iOS_UserContextr_Param, def.userContext);
		pl.put(iOSDevicesDefines.kCobra_iOS_UserContextPassword_Param, def.userContextPassword);
		pl.put(iOSDevicesDefines.kCobra_iOS_UserContextSelector_Param, def.userContextSelector);
		
		return pl;
	}
	
	public static CobraAdminMiscDatabaseCommand createLoginCommand(String userName, String password) throws IOException,
			GeneralSecurityException {

		PropertyList loginCommandParameters = new PropertyList();
		loginCommandParameters.put(CobraCommandDefs.kCobra_AdminLogin_AdminName_Param, userName);
		CPLATPassword cplatPassword = new CPLATPassword(password);

		loginCommandParameters.put(CobraCommandDefs.kCobra_AdminLogin_AdminPassword_Param,
				cplatPassword.Encrypt(CobraCommandDefs.kBlowfishAdminLogingPasswordKey.getBytes("UTF-8")));

		CommandInfoData inCommandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_Login_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand loginCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, null, loginCommandParameters);

		return loginCommand;
	}

	public static CobraUserCommand createSSPLoginCommand(String userName, String password, String domain) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList(), commandData = new PropertyList();

		if (domain != null) {
			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_Domain_Param,
					new CPLATPassword(domain).Encrypt(CobraCommandDefs.kSelfServiceLoginCredentialsKey.getBytes("UTF-8")));
		}

		if (password != null) {
			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_Password_Param,
					new CPLATPassword(password).Encrypt(CobraCommandDefs.kSelfServiceLoginCredentialsKey.getBytes("UTF-8")));
		} else {
			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_Password_Param, "");
		}

		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_Username_Param,
				new CPLATPassword(userName).Encrypt(CobraCommandDefs.kSelfServiceLoginCredentialsKey.getBytes("UTF-8")));

		commandParameters.put(CobraCommandDefs.kCobra_Data_Param, commandData);
		commandParameters.put(CobraCommandDefs.kCobra_OperationType_Param,
				CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin);

		CobraUserCommand loginCommand = new CobraUserCommand(new CommandInfoData(CobraCommandDefs.kCobra_SelfService_Command,
				CobraProtocol.kCobraAdminServer), commandParameters);

		return loginCommand;
	}

	public static CobraUserCommand createSSPGetUserDeviceCommand(String sessionToken) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList(), commandData = new PropertyList();

		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam, sessionToken);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_GetDeviceListForUser_ReturnDesktopDevices_Param, true);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_GetDeviceListForUser_ReturnMobileDevices_Param, true);

		commandParameters.put(CobraCommandDefs.kCobra_Data_Param, commandData);
		commandParameters.put(CobraCommandDefs.kCobra_OperationType_Param,
				CobraCommandDefs.kCobra_SelfService_OperationType_GetDeviceListForUser);

		CobraUserCommand command = new CobraUserCommand(new CommandInfoData(CobraCommandDefs.kCobra_SelfService_Command,
				CobraProtocol.kCobraAdminServer), commandParameters);

		return command;
	}

	public static CobraUserCommand createSSPSendMessageCommand(String message, boolean withCancel, int timeout, String deviceIdentifier,
			String agentSerial, int deviceType, String sessionToken, String headerText, String timeoutCounterText) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList(), commandData = new PropertyList(), commandDataCommandParameters = new PropertyList();

		if (deviceIdentifier != null) { // create a command for a mobile device
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_MessageText_Param, message);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Timestamp_Param, new Date());

			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceTargetIdentifier_Param,
					deviceIdentifier);
			commandData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
					CobraCommandDefs.kCobra_Admin_SendMDMCommand_ShowMessage);
		} else { // create a command for a desktop device
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_AgentSendMessage_Text_Param, message);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_AgentSendMessage_Timeout_Param, timeout);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_AgentSendMessage_WithCancel_Param, withCancel);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_AgentSendMessage_HeaderText_Param, headerText);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_AgentSendMessage_TimeoutCounterText_Param, timeoutCounterText);

			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param, deviceType);
			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_AgentSerial_Param, agentSerial);
			commandData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param, CobraCommandDefs.kCobra_AgentSendMessage_Command);
		}

		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam, sessionToken);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param,
				commandDataCommandParameters);

		commandParameters.put(CobraCommandDefs.kCobra_Data_Param, commandData);
		commandParameters.put(CobraCommandDefs.kCobra_OperationType_Param,
				CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);

		CobraUserCommand command = new CobraUserCommand(new CommandInfoData(CobraCommandDefs.kCobra_SelfService_Command,
				CobraProtocol.kCobraAdminServer), commandParameters);

		return command;
	}

	public static CobraUserCommand createSSPLockDevicesCommand(String passcode, String message, String phoneNumber,
			String deviceIdentifier, String agentSerial, int deviceType, String sessionToken) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList(), commandData = new PropertyList(), commandDataCommandParameters = new PropertyList();
		if (deviceIdentifier != null) { // create a command for a mobile device
			if (passcode != null) {
				if (passcode.isEmpty()) {
					commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param, "");
				} else {
					CPLATPassword cplatPassword = new CPLATPassword(passcode);
					commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param,
							cplatPassword.Encrypt(CobraCommandDefs.kBlowfishMDMDeviceLockPasswordKey.getBytes("UTF-8")));
				}
			} else {
				commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param, "");
			}

			if (message != null) {
				commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_LockMDMCommand_RequestData_Message_Param, message);
			}

			if (phoneNumber != null) {
				commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_LockMDMCommand_RequestData_PhoneNumber_Param, phoneNumber);
			}

			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceTargetIdentifier_Param,
					deviceIdentifier);
			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandID_Param,
					CobraCommandDefs.kCobra_Admin_SendMDMCommand_DeviceLock);
		} else { // create a command for a desktop device
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_Param,
					CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_DeviceLock);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_IsDeviceCommand_Param,
					true);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_PIN_Param, passcode);

			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_AgentSerial_Param, agentSerial);
			commandData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param, CobraCommandDefs.kCobra_AgentLock_Command);
		}

		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam, sessionToken);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param, deviceType);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param,
				commandDataCommandParameters);

		commandParameters.put(CobraCommandDefs.kCobra_Data_Param, commandData);
		commandParameters.put(CobraCommandDefs.kCobra_OperationType_Param,
				CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);

		CobraUserCommand command = new CobraUserCommand(new CommandInfoData(CobraCommandDefs.kCobra_SelfService_Command,
				CobraProtocol.kCobraAdminServer), commandParameters);

		return command;
	}

	public static CobraUserCommand createSSPRemoteEraseCommand(boolean includeSsdCard, String passcode, String deviceIdentifier,
			String agentSerial, int deviceType, String sessionToken) throws IOException, GeneralSecurityException {
		PropertyList commandParameters = new PropertyList(), commandData = new PropertyList(), commandDataCommandParameters = new PropertyList();

		if (deviceIdentifier != null) { // create a command for a mobile device
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_EraseSDCard_Param, includeSsdCard);

			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceTargetIdentifier_Param,
					deviceIdentifier);
			commandData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
					CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoteErase);
		} else { // create a command for a desktop device
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_Param,
					CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_EraseDevice);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_IsDeviceCommand_Param,
					true);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_PIN_Param, passcode);

			commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_AgentSerial_Param, agentSerial);
			commandData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param, CobraCommandDefs.kCobra_AgentErase_Command);
		}

		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam, sessionToken);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param,
				commandDataCommandParameters);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param, deviceType);

		commandParameters.put(CobraCommandDefs.kCobra_Data_Param, commandData);
		commandParameters.put(CobraCommandDefs.kCobra_OperationType_Param,
				CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);

		CobraUserCommand command = new CobraUserCommand(new CommandInfoData(CobraCommandDefs.kCobra_SelfService_Command,
				CobraProtocol.kCobraAdminServer), commandParameters);

		return command;
	}

	public static CobraUserCommand createSSPClearPasscodeCommand(String passcode, String deviceIdentifier, int deviceType,
			String sessionToken) throws IOException, GeneralSecurityException {
		PropertyList commandParameters = new PropertyList(), commandData = new PropertyList(), commandDataCommandParameters = new PropertyList();

		if (passcode.isEmpty()) {
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param, "");
		} else {
			CPLATPassword cplatPassword = new CPLATPassword(passcode);
			commandDataCommandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param,
					cplatPassword.Encrypt(CobraCommandDefs.kBlowfishMDMDeviceLockPasswordKey.getBytes("UTF-8")));
		}

		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam, sessionToken);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param,
				commandDataCommandParameters);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceTargetIdentifier_Param,
				deviceIdentifier);
		commandData.put(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param, deviceType);
		commandData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_ClearPasscode);

		commandParameters.put(CobraCommandDefs.kCobra_Data_Param, commandData);
		commandParameters.put(CobraCommandDefs.kCobra_OperationType_Param,
				CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);

		CobraUserCommand command = new CobraUserCommand(new CommandInfoData(CobraCommandDefs.kCobra_SelfService_Command,
				CobraProtocol.kCobraAdminServer), commandParameters);

		return command;
	}

	public static CobraAdminMiscDatabaseCommand createInstallInHouseApplicationCommand(UUID adminUUID, int[] deviceIds, boolean isAndroid,
			iOSApplications inHouseAppDetails) throws IOException, GeneralSecurityException {

		PropertyList requestDataParams = createPropertiesFromiOSApplications(inHouseAppDetails, isAndroid);

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_InstallApplicationFromRepository);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestDataParams);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand createInstallThirdPartyApplicationCommand(UUID adminUUID, int[] deviceIds,
			iOSAppStoreApplications thirdPartyAppDetails) throws IOException, GeneralSecurityException {

		PropertyList requestDataParams = createPropertiesFromiOSAppStoreApplications(thirdPartyAppDetails);

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_InstallApplicationFromRepository);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestDataParams);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand createDeleteApplicationCommand(UUID adminUUID, long deviceId, long[] applicationIds)
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoveApplication);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, applicationIds);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;

	}

	public static CobraAdminMiscDatabaseCommand createAddMediaCommand(MobileMedia mediaInfo, UUID adminUUID) throws IOException,
			GeneralSecurityException {

		PropertyList newDataProperties = createPropertiesFromMobileMedia(mediaInfo);

		// File list stuff
		newDataProperties.put(iOSDevicesDefines.kCobra_iOS_AttachedFileTypeList_Param,
				new int[] { iOSDevicesDefines.kCobra_iOS_AttachedFileType_MediaFile });
		newDataProperties.put(iOSDevicesDefines.kCobra_iOS_AttachedFileNameList_Param,
				new UUID[] { UUID.fromString(mediaInfo.getUniqueId()) });
		newDataProperties.put(iOSDevicesDefines.kCobra_iOS_AttachedFileSourcePathList_Param, new String[] { mediaInfo.getFilename() });

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddMedia);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param, newDataProperties);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand createUpdateMediaCommand(MobileMedia oldMediaInfo, MobileMedia newMediaInfo, UUID adminUUID)
			throws IOException, GeneralSecurityException {

		PropertyList newDataProperties = createPropertiesFromMobileMedia(newMediaInfo);
		PropertyList oldDataProperties = createPropertiesFromMobileMedia(oldMediaInfo);

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddMedia);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param, newDataProperties);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OldData_Param, oldDataProperties);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;
	}

	/**
	 * Creates a RemoveMedia command based on the file id.
	 * 
	 * @param fileId
	 *            - the Id of the file to remove.
	 * @param adminUUID
	 *            - the AdminUUID of the currently logged in user.
	 * @return a CobraCommand ready for sending.
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createRemoveMediaCommandForFileIds(int[] fileIds, UUID adminUUID) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveMedia);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, fileIds);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;
	}

	/**
	 * Creates an Assign Media To Policy command, based on the input parameters.
	 * 
	 * @param mediaUUIDs
	 *            The list of media to assign to the policy.
	 * @param policyUUID
	 *            The UUID of the policy to assign the media to.
	 * @param assignmentType
	 *            The type of assignment, e.g.
	 *            kCobra_iOS_Policy_MediaFile_PolicyOptional
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createAssignMediaToPolicyCommand(UUID[] mediaUUIDs, UUID policyUUID, int assignmentType,
			UUID adminUUID) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddMediaToPolicy);

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AssignmentType_Param, assignmentType);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_MediaList_Param, mediaUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;
	}

	/**
	 * Creates a Remove Media From Policy command, based on the input
	 * parameters.
	 * 
	 * @param mediaUUIDs
	 *            The list of media to remove from the policy.
	 * @param policyUUID
	 *            The UUID of the policy to remove the media from.
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createRemoveMediaFromPolicyCommand(UUID[] mediaUUIDs, UUID policyUUID, UUID adminUUID)
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveMediaFromPolicy);

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_MediaList_Param, mediaUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;

	}

	/**
	 * Creates an Assign Mobile Devices To Policy command, based on the input
	 * parameters.
	 * 
	 * @param mobileDeviceUUIDs
	 *            The list of mobile devices to assign to the policy.
	 * @param policyUUID
	 *            The UUID of the policy to assign mobile devices to.
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createAssignDevicesToPolicyCommand(UUID[] mobileDeviceUUIDs, UUID policyUUID, UUID adminUUID)
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddDevicesToPolicy);

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_PolicyDeviceList_Param, mobileDeviceUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;

	}

	/**
	 * Creates a Remove Mobile Devices From Policy command, based on the input
	 * parameters.
	 * 
	 * @param mobileDeviceUUIDs
	 *            The list of mobile devices to remove from the policy.
	 * @param policyUUID
	 *            The UUID of the policy to remove the media from.
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createRemoveDevicesFromPolicyCommand(UUID[] mobileDeviceUUIDs, UUID policyUUID,
			UUID adminUUID) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveDevicesFromPolicy);

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_PolicyDeviceList_Param, mobileDeviceUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;

	}

	/**
	 * Creates a SetAvailability command to control when media is available to
	 * members of the Policy
	 * 
	 * @param mediaUUIDs
	 *            The list of media files to restrict.
	 * @param policyUUID
	 *            The UUID of the policy to place the restriction on (TODO: what
	 *            happens if this doesn't exist.).
	 * @param availabilitySelector
	 *            Controls when the media is available, e.g.
	 *            kCobra_iOS_Policy_AvailabilitySelector_DailyInterval
	 * @param startTime
	 *            When the availabilitySelector is not set to 'Always' this is
	 *            the start time for the period.
	 * @param endTime
	 *            When the availabilitySelector is not set to 'Always' this is
	 *            the end time for the period.
	 * @param adminUUID
	 *            The AdminUUID of the currently logged on user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createSetAvailabilityTimeForPolicyMediaCommand(UUID[] mediaUUIDs, UUID policyUUID,
			int availabilitySelector, String startTime, String endTime, UUID adminUUID) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_SetAvailabilityTimeForMediaInPolicy);

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_Param, availabilitySelector);
		if (availabilitySelector != iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_Always) {
			dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityStartTime_Param, startTime);
			dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityEndTime_Param, endTime);
		}
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_MediaList_Param, mediaUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;
	}

	/**
	 * Creates a SetAvailability command to control when configuration profiles
	 * are available to members of the Policy
	 * 
	 * @param configProfileUUIDs
	 *            The list of configuration profiles to restrict.
	 * @param policyUUID
	 *            The UUID of the policy to place the restriction on (TODO: what
	 *            happens if this doesn't exist.).
	 * @param availabilitySelector
	 *            Controls when the media is available, e.g.
	 *            kCobra_iOS_Policy_AvailabilitySelector_DailyInterval
	 * @param startTime
	 *            When the availabilitySelector is not set to 'Always' this is
	 *            the start time for the period.
	 * @param endTime
	 *            When the availabilitySelector is not set to 'Always' this is
	 *            the end time for the period.
	 * @param adminUUID
	 *            The AdminUUID of the currently logged on user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createSetAvailabilityTimeForPolicyConfigProfileCommand(UUID[] configProfileUUIDs,
			UUID policyUUID, int availabilitySelector, String startTime, String endTime, UUID adminUUID) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_SetAvailabilityTimeForConfProfilesInPolicy);

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_Param, availabilitySelector);
		if (availabilitySelector != iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_Always) {
			dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityStartTime_Param, startTime);
			dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityEndTime_Param, endTime);
		}
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfileList_Param, configProfileUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);

		return iOSDatabaseCommand;
	}

	/**
	 * Returns a "lockDevice" command for a list of device ids.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param deviceIds
	 *            - an array of device ids identifying the devices to be locked.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createLockDevicesCommand(UUID adminUUID, int[] deviceIds, String passcode)
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);
		if (passcode != null) {
			PropertyList requestData = new PropertyList();
			requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_AndroidRecordIDList_Param, deviceIds);
			if (passcode.isEmpty()) {
				requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param, "");
			} else {
				CPLATPassword cplatPassword = new CPLATPassword(passcode);
				requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param,
						cplatPassword.Encrypt(CobraCommandDefs.kBlowfishMDMDeviceLockPasswordKey.getBytes("UTF-8")));
			}
			commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestData);
		}
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_DeviceLock);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	/**
	 * Returns a "lockDevice" command for a list of device ids.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param deviceIds
	 *            - an array of device ids identifying the devices to be locked.
	 * @param activationLock
	 *            - the value of the action lock to set for the devices
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createSetActivationLockOptionsCommand(UUID adminUUID, int[] deviceIds, int activationLock)
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);

		PropertyList requestData = new PropertyList();
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_AndroidRecordIDList_Param, deviceIds);

		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_ChangeActivationLock_Param, activationLock);

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestData);

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_SetActivationLockOptions);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	/**
	 * Returns a "delete history commands" command for a list of command ids.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param commandIds
	 *            - an array of command ids identifying the commands to be
	 *            deleted.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createDeleteHistoryCommandsCommand(UUID adminUUID, int[] commandIds) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		commandParameters.put(CobraCommandDefs.kCobra_Data_Param, commandIds);
		commandParameters.put(CobraCommandDefs.kCobra_OperationType_Param, CobraCommandDefs.kCobra_DeleteCommands_History_OperationType);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_DeleteCommands_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	/**
	 * Returns a "delete queued commands" command for a list of command ids.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param commandIds
	 *            - an array of command ids identifying the commands to be
	 *            deleted.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createDeleteQueuedCommandsCommand(UUID adminUUID, int[] commandIds) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		commandParameters.put(CobraCommandDefs.kCobra_Data_Param, commandIds);
		commandParameters.put(CobraCommandDefs.kCobra_OperationType_Param, CobraCommandDefs.kCobra_DeleteCommands_Queued_OperationType);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_DeleteCommands_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	/**
	 * Returns a "sendMessage" command with the given message and device ids.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param message
	 *            - the message to send
	 * @param deviceIds
	 *            - an array of device ids identifying the devices to be locked.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createSendMessageCommand(UUID adminUUID, String message, int[] deviceIds)
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);

		PropertyList requestData = new PropertyList();
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_MessageText_Param, message);
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Timestamp_Param, new Date());
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestData);

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_ShowMessage);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	/**
	 * Returns a "ClearPasscode" command with the given device ids and pass
	 * code.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param androidDeviceIds
	 *            - an array of android device ids to which to send clear pass
	 *            command
	 * @param androidDeviceIds
	 *            - an array of IOS device ids to which to send clear pass
	 *            command
	 * @param newPasscode
	 *            - the pass code to set
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createClearPasscodeCommand(UUID adminUUID, int[] androidDeviceIds, int[] iosDeviceIds,
			String newPasscode) throws IOException, GeneralSecurityException {
		PropertyList commandParameters = new PropertyList();

		// Algorithm:
		// For iOS devices: You can only clear the passcode. You can not set it.
		// For android devices: You can clear and set the passcode.
		// RecordIDList_Param: contains the android and iOs device Id's
		// If there are android devices, these alone go into
		// AndroidRecordIDList_Param
		// Clearing iOS passcode: NewLockPassword_Param is ommitted from the
		// message
		// Clearing android passcode: NewLockPassword_Param is present and set
		// to ""

		if (androidDeviceIds == null)
			androidDeviceIds = new int[0];

		if (iosDeviceIds == null)
			iosDeviceIds = new int[0];

		int[] allDeviceIds = new int[androidDeviceIds.length + iosDeviceIds.length];
		System.arraycopy(androidDeviceIds, 0, allDeviceIds, 0, androidDeviceIds.length);
		System.arraycopy(iosDeviceIds, 0, allDeviceIds, androidDeviceIds.length, iosDeviceIds.length);

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, allDeviceIds);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_ClearPasscode);

		PropertyList requestData = new PropertyList();
		if (androidDeviceIds.length > 0) {
			// for android devices, you can set or clear the passcode
			requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_AndroidRecordIDList_Param, androidDeviceIds);
			if (newPasscode.isEmpty()) {
				requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param, "");
			} else {
				CPLATPassword cplatPassword = new CPLATPassword(newPasscode);
				requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param,
						cplatPassword.Encrypt(CobraCommandDefs.kBlowfishMDMDeviceLockPasswordKey.getBytes("UTF-8")));
			}
		}
		// for iOS devices you can only clear the passcode and the
		// passcode_param is ommitted.

		// <key>iOSRecordIDList</key>
		// <array/>

		if (iosDeviceIds.length > 0) {
			requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_iOSRecordIDList_Param, iosDeviceIds);
		}

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestData);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	private static PropertyList createPropertiesFromMobileMedia(MobileMedia mediaInfo) throws IOException, GeneralSecurityException {
		PropertyList properties = new PropertyList();

		// last_modified isn't copied to properties
		// It is set by the server.

		if (mediaInfo.getId() > 0) {
			properties.put(iOSDevicesDefines.kCobra_MobileMediaID_Param, mediaInfo.getId());
		}
		putIfNotNull(properties, iOSDevicesDefines.kCobra_MobileMediaUniqueID_Param, mediaInfo.getUniqueId());
		properties.put(iOSDevicesDefines.kCobra_MobileMediaSeed_Param, mediaInfo.getSeed());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_MobileMediaDisplayName_Param, mediaInfo.getDisplayName());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_MobileMediaCategory_Param, mediaInfo.getCategory());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_MobileMediaFilename_Param, mediaInfo.getFilename());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_MobileMediaFileModDate_Param, mediaInfo.getFileModDate());
		properties.put(iOSDevicesDefines.kCobra_MobileMediaFileSize_Param, mediaInfo.getFileSize());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_MobileMediaFileType_Param, mediaInfo.getFileType());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_MobileMediaFileMD5_Param, mediaInfo.getFileMD5());
		properties.put(iOSDevicesDefines.kCobra_MobileMediaDescription_Param, mediaInfo.getDescription());
		properties.put(iOSDevicesDefines.kCobra_MobileMediaCanLeaveApp_Param, mediaInfo.getCanLeaveApp());
		properties.put(iOSDevicesDefines.kCobra_MobileMediaCanEmail_Param, mediaInfo.getCanEmail());
		properties.put(iOSDevicesDefines.kCobra_MobileMediaCanPrint_Param, mediaInfo.getCanPrint());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_MobileMediaEncryptionKey_Param, mediaInfo.getEncryptionKey());
		properties.put(iOSDevicesDefines.kCobra_MobileMediaIcon_Param, mediaInfo.getIcon());
		properties.put(iOSDevicesDefines.kCobra_MobileMediaTransferOnWifiOnly_Param, mediaInfo.getTransferOnWifiOnly());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_MobileMediaPassphraseHash_Param, mediaInfo.getPassPhraseHash());

		return properties;
	}

	private static PropertyList createPropertiesFromConfigurationProfile(ConfigurationProfile configurationProfileDetails)
			throws IOException, GeneralSecurityException {
		PropertyList properties = new PropertyList();

		// last_modified isn't copied to properties
		// It is set by the server.

		if (configurationProfileDetails.getId() > 0) {
			properties.put(iOSDevicesDefines.kCobra_iOS_ConfigurationProfileID_Param, configurationProfileDetails.getId());
		}
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfileBinaryPackageMD5_Param,
				configurationProfileDetails.getBinaryPackageMD5());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfileBinaryPackageName_Param,
				configurationProfileDetails.getBinaryPackageName());
		properties.put(iOSDevicesDefines.kCobra_iOS_ConfigurationProfileConfigurationType_Param,
				configurationProfileDetails.getConfigurationType());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfileID_Param, configurationProfileDetails.getUniqueId());
		properties.put(iOSDevicesDefines.kCobra_iOS_ConfigurationProfileSeed_Param, configurationProfileDetails.getSeed());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfileOriginalFileName_Param,
				configurationProfileDetails.getOriginalFileName());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadDescription_Param,
				configurationProfileDetails.getPayloadDescription());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadIdentifier_Param,
				configurationProfileDetails.getPayloadIdentifier());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadName_Param,
				configurationProfileDetails.getPayloadName());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadOrganization_Param,
				configurationProfileDetails.getPayloadOrganization());
		properties.put(iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadRemovalOptions_Param,
				configurationProfileDetails.getPayloadRemovalOptions());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadUUID_Param,
				configurationProfileDetails.getPayloadUUID());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ConfigurationProfileVariablesUsed_Param,
				configurationProfileDetails.getVariablesUsed());
		properties.put(iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePlatformType_Param, configurationProfileDetails.getPlatformType());
		return properties;
	}

	private static PropertyList createPropertiesFromProvisioningProfile(ProvisioningProfile provisioningProfileDetails) throws IOException,
			GeneralSecurityException {
		PropertyList properties = new PropertyList();

		// last_modified isn't copied to properties
		// It is set by the server.

		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ProvisioningProfileUUID_Param, provisioningProfileDetails.getProfileUUID());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ProvisioningProfileName_Param, provisioningProfileDetails.getProfileName());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_ProvisioningProfileExpiryDate_Param,
				provisioningProfileDetails.getProfileExpiryDate());
		return properties;
	}

	private static PropertyList createPropertiesFromiOSApplications(iOSApplications inHouseAppDetails, boolean isAndroid)
			throws IOException, GeneralSecurityException {
		PropertyList properties = new PropertyList();

		properties.put(iOSDevicesDefines.kCobra_iOS_AppAppSize_Param, inHouseAppDetails.getAppSize());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppBinaryPackageMD5_Param, inHouseAppDetails.getBinaryPackageMD5());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppBinaryPackageName_Param, inHouseAppDetails.getBinaryPackageName());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppBundleIdentifier_Param, inHouseAppDetails.getBundleIdentifier());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppDisplayName_Param, inHouseAppDetails.getDisplayName());
		if (isAndroid) {
			putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppEncryptionKey_Param, inHouseAppDetails.getEncryptionKey());
		}
		properties.put(iOSDevicesDefines.kCobra_iOS_AppMinOSVersion_Param, inHouseAppDetails.getMinOSVersion());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppName_Param, inHouseAppDetails.getName());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppOriginalFileName_Param, inHouseAppDetails.getOriginalFileName());
		properties.put(iOSDevicesDefines.kCobra_iOS_AppPlatformType_Param, inHouseAppDetails.getPlatformType());
		properties.put(iOSDevicesDefines.kCobra_iOS_AppPreventAppDataBackup_Param, inHouseAppDetails.getPreventAppDataBackup());
		properties.put(iOSDevicesDefines.kCobra_iOS_AppRemoveWhenMDMIsRemoved_Param, inHouseAppDetails.getRemoveWhenMDMIsRemoved());
		properties.put(iOSDevicesDefines.kCobra_iOS_AppSeed_Param, inHouseAppDetails.getSeed());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppID_Param, inHouseAppDetails.getUniqueID());
		return properties;
	}

	private static PropertyList createPropertiesFromiOSAppStoreApplications(iOSAppStoreApplications thirdPartyAppDetails)
			throws IOException, GeneralSecurityException {
		PropertyList properties = new PropertyList();

		// See Work Ticket #52686: Mobile Devices - iOS - Getting Bad Request
		// error (400) when attempting to install a third party application on
		// an iOS device from the Web Admin (installing 3rd party apps from the
		// Admin Console works with no issues)
		// The app store ID is a string, however the server expects a
		// <real></real> value in the command, which is a double.
		// Also - Work Ticket 53473: web UI - Receiving Error when installing
		// Third Party Applications:Error Installing Application
		// Message:Unexpected Error.......
		// appStoreId is only numeric for iOS devices, otherwise it is a string
		String appStoreId = thirdPartyAppDetails.getAppStoreID();
		if (StringUtilities.isStringNumeric(appStoreId)) {
			double appStoreIDAsDouble = Double.parseDouble(appStoreId);
			properties.put(iOSDevicesDefines.kCobra_iOS_AppStoreAppStoreID_Param, appStoreIDAsDouble);
		} else {
			properties.put(iOSDevicesDefines.kCobra_iOS_AppStoreAppStoreID_Param, appStoreId);
		}

		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppStoreAppURL_Param, thirdPartyAppDetails.getAppStoreURL());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppStoreAppCategory_Param, thirdPartyAppDetails.getCategory());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppStoreAppLongDescription_Param, thirdPartyAppDetails.getLongDescription());
		properties.put(iOSDevicesDefines.kCobra_iOS_AppStoreAppMinOSVersion_Param, thirdPartyAppDetails.getMinOSVersion());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppStoreAppName_Param, thirdPartyAppDetails.getName());
		properties.put(iOSDevicesDefines.kCobra_iOS_AppStorePlatformType_Param, thirdPartyAppDetails.getPlatformType());
		properties.put(iOSDevicesDefines.kCobra_iOS_AppStoreAppSeed_Param, thirdPartyAppDetails.getSeed());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppStoreAppShortDescription_Param, thirdPartyAppDetails.getShortDescription());
		putIfNotNull(properties, iOSDevicesDefines.kCobra_iOS_AppStoreAppID_Param, thirdPartyAppDetails.getUniqueID());

		return properties;
	}

	private static void putIfNotNull(PropertyList properties, String propName, String property) {
		if (property != null && property.length() > 0) {
			properties.put(propName, property);
		}
	}

	/**
	 * Returns a "RemoteErase" command with the given device ids.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param androidDeviceIds
	 *            - an array of android device ids to which to Remote Erase pass
	 *            command
	 * @param androidDeviceIds
	 *            - an array of IOS device ids to which to send Remote Erase
	 *            command
	 * @param eraseSDCard
	 *            - boolean flag to determine whether to also erase SD card if
	 *            present. Applies only to Android devices.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createRemoteEraseCommand(UUID adminUUID, int[] androidDeviceIds, int[] iosDeviceIds,
			boolean eraseSDCard) throws IOException, GeneralSecurityException {
		PropertyList commandParameters = new PropertyList();

		if (androidDeviceIds == null)
			androidDeviceIds = new int[0];

		if (iosDeviceIds == null)
			iosDeviceIds = new int[0];

		int[] allDeviceIds = new int[androidDeviceIds.length + iosDeviceIds.length];
		System.arraycopy(androidDeviceIds, 0, allDeviceIds, 0, androidDeviceIds.length);
		System.arraycopy(iosDeviceIds, 0, allDeviceIds, androidDeviceIds.length, iosDeviceIds.length);

		// <key>RecordIDList</key>
		// <array>
		// <integer>2</integer>
		// </array>
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, allDeviceIds);

		// <key>CommandID</key>
		// <integer>7</integer>
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoteErase);

		PropertyList requestData = new PropertyList();
		// <key>AndroidRecordIDList</key>
		// <array>
		// <integer>2</integer>
		// </array>
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_AndroidRecordIDList_Param, androidDeviceIds);

		// <key>EraseSDCard</key>
		// <false/> [false | true]
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_EraseSDCard_Param, eraseSDCard);

		// <key>iOSRecordIDList</key>
		// <array/>
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_iOSRecordIDList_Param, iosDeviceIds);

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestData);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	/**
	 * Returns a "UpdateDeviceInfo" command with the given device ids and pass
	 * code.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param deviceIds
	 *            - an array of device ids to which to send update device info
	 *            command
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createUpdateDeviceInfoCommand(UUID adminUUID, int[] deviceIds) throws IOException,
			GeneralSecurityException {
		PropertyList commandParameters = new PropertyList();

		// <key>RecordIDList</key>
		// <array>
		// <integer>2</integer>
		// </array>
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);

		// <key>CommandID</key>
		// <integer>8</integer>
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_UpdateDeviceInfo);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	/**
	 * Returns a "UpdateDeviceInfo" command with the given device ids and pass
	 * code.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param deviceIds
	 *            - an array of device ids to which to send update device info
	 *            command
	 * @return
	 * @throws Exception
	 */
	public static CobraAdminMiscDatabaseCommand createSetRoamingOptionsCommand(UUID adminUUID, int[] deviceIds, Boolean voiceRoaming,
			Boolean dataRoaming) throws Exception {
		PropertyList commandParameters = new PropertyList();

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_ChangeRoamingOptions);

		PropertyList requestData = new PropertyList();

		if (dataRoaming != null) {
			requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_DataRoaming_Param, dataRoaming);
		}
		if (voiceRoaming != null) {
			requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_VoiceRoaming_Param, voiceRoaming);
		}

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestData);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);

		return command;
	}

	public static CobraRegisterSyncSvcCertCommand createRegisterSyncSvcCertCommand(String userName, String password, byte[] certData)
			throws UnsupportedEncodingException, IOException, GeneralSecurityException {
		PropertyList commandParameters = new PropertyList();

		commandParameters.put(CobraCommandDefs.kCobra_RegisterSyncSvcCert_AdminName_Param, userName);
		CPLATPassword cplatPassword = new CPLATPassword(password);

		commandParameters.put(CobraCommandDefs.kCobra_RegisterSyncSvcCert_AdminPassword_Param,
				cplatPassword.Encrypt(CobraCommandDefs.kBlowfishAdminLogingPasswordKey.getBytes("UTF-8")));
		commandParameters.put(CobraCommandDefs.kCobra_RegisterSyncSvcCert_CertificateData_Param, certData);
		commandParameters.put(CobraCommandDefs.kCobra_RegisterSyncSvcCert_CertificateDataFormat_Param,
				SyncServicesCommandDefines.kCobra_SyncServices_StartSync_ServerCertificateFormat_DER);

		CommandInfoData inCommandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_RegisterSyncSvcCert_Command,
				CobraProtocol.kCobraAdminServer);

		CobraRegisterSyncSvcCertCommand registerSyncSvcCmd = new CobraRegisterSyncSvcCertCommand(inCommandInfoData, null, commandParameters);

		return registerSyncSvcCmd;
	}

	public static CobraAdminMiscDatabaseCommand createInstallConfigurationProfileCommand(UUID adminUUID, long[] deviceIds,
			ConfigurationProfile configurationProfileDetails) throws IOException, GeneralSecurityException {
		PropertyList requestDataParams = createPropertiesFromConfigurationProfile(configurationProfileDetails);

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_InstallProfileFromRepository);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestDataParams);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand createInstallProvisioningProfileCommand(UUID adminUUID, long[] deviceIds,
			ProvisioningProfile provisioningProfileDetails) throws IOException, GeneralSecurityException {
		PropertyList requestDataParams = createPropertiesFromProvisioningProfile(provisioningProfileDetails);

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_InstallProvisioningProfileFromRepository);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestDataParams);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand createRemoveConfigurationProfileFromDeviceCommand(UUID adminUUID,
			long[] configurationProfileAssociationIds) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoveProfile);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, configurationProfileAssociationIds);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;

	}

	public static CobraAdminMiscDatabaseCommand createRemoveProvisioningProfileFromDeviceCommand(UUID adminUUID,
			long[] provisioningProfileAssociationIds) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoveProvisioningProfile);
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, provisioningProfileAssociationIds);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;

	}

	public static CobraAdminMiscDatabaseCommand setDevicesOwnershipCommand(UUID adminUUID, int[] deviceIds, int deviceOwnershipType)
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		PropertyList dataParameter = new PropertyList();

		// <key>RecordIDList</key>
		// <array>
		// <integer>2</integer>
		// </array>
		dataParameter.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_DeviceOwnership_Param, deviceOwnershipType);
		dataParameter.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_DeviceRecordIDs_Param, deviceIds);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_SetDeviceOwnership);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand setEnrollmentUserCommand(UUID adminUUID, int[] deviceIds, String username, String domain)
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		PropertyList dataParameter = new PropertyList();

		dataParameter.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_EnrollmentDomain_Param, domain);
		dataParameter.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_EnrollmentUser_Param, username);
		// <key>RecordIDList</key>
		// <array>
		// <integer>2</integer>
		// </array>
		dataParameter.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_DeviceRecordIDs_Param, deviceIds);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		// <key>OperationType</key>
		// <integer>50</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_SetEnrollmentUser);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand setDeviceNameCommand(UUID adminUUID, int deviceId, String name) throws IOException,
			GeneralSecurityException {

		// note: only one device id in the array
		int[] deviceIdAsArray = new int[] { deviceId };

		PropertyList commandParameters = new PropertyList();

		PropertyList requestData = new PropertyList();

		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_DeviceName_Param, name);

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestData);

		// <key>CommandID</key>
		// <integer>22</integer>
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_ChangeDeviceName);

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIdAsArray);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand setOrganizationInfoCommand(UUID adminUUID, int[] deviceIds, String name, String phone,
			String email, String address, String custom) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		PropertyList requestData = new PropertyList();

		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_OrganizationName_Param, name);
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_OrganizationPhone_Param, phone);
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_OrganizationEmail_Param, email);
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_OrganizationAddress_Param, address);
		// note that <key>OrganizationMagic</key> corresponds to "Custom" field
		// in the UI
		requestData.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_OrganizationMagic_Param, custom);

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param, requestData);

		// <key>CommandID</key>
		// <integer>19</integer>
		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param,
				CobraCommandDefs.kCobra_Admin_SendMDMCommand_SetOrganizationInfo);

		commandParameters.put(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param, deviceIds);

		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_Admin_SendMDMCommand_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand retryAllFailedProfilesCommand(UUID adminUUID, UUID[] uniqueID) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		// <key>Data</key>
		// <array>
		// <string>3D44599A-0C1B-4D2C-9FE7-35C8D9842B82</string>
		// </array>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, uniqueID);

		// <key>OperationType</key>
		// <integer>23</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_ResetFailedProfiles);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand retryAllFailedApplicationsCommand(UUID adminUUID, UUID[] uniqueID) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		// <key>Data</key>
		// <array>
		// <string>3D44599A-0C1B-4D2C-9FE7-35C8D9842B82</string>
		// </array>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, uniqueID);

		// <key>OperationType</key>
		// <integer>52</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_ResetFailedInhouseApplications);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand createStandardPolicyCommand(UUID adminUUID, String policyName, int seed, UUID policyUuid)
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		PropertyList newDataParameters = new PropertyList();

		// <key>NewData</key>
		// <dict>
		// <key>Name</key>
		// <string>greg_test</string>
		// <key>Seed</key>
		// <integer>1</integer>
		// <key>UniqueID</key>
		// <string>CCFF47CE-3BA0-4C59-B9D2-A4349AB657AD</string>
		// </dict>

		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyName_Param, policyName);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicySeed_Param, seed);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyID_Param, policyUuid);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param, newDataParameters);

		// <key>OperationType</key>
		// <integer>9</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddPolicy);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand createSmartPolicyCommand(UUID adminUUID, String policyName, int seed, int schemaVersion,
			int filterType, PropertyList filterCriteria, UUID policyUuid) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		PropertyList newDataParameters = new PropertyList();

		// <key>NewData</key>
		// <dict>
		// <key>FilterCriteria</key>
		// <dict>
		// <key>FilterQuery</key>
		// <string>...</String>
		// <key>FilterTables</key>
		// <string>admin_appointments_admins,admin_appointments_mobile_devices,admins,iOS_appstore_applications,iOS_appstore_applications_vpp,iphone_info</string>
		// <key>FilterType</key>
		// <integer>1</integer>
		// <key>Name</key>
		// <string>my_test_smart_policy</string>
		// <key>SchemaVersion</key>
		// <integer>69</integer>
		// <key>Seed</key>
		// <integer>1</integer>
		// <key>UniqueID</key>
		// <string>CCFF47CE-3BA0-4C59-B9D2-A4349AB657AD</string>
		// </dict>

		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyFilterCriteria_Param, filterCriteria);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyFilterType_Param, filterType);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyName_Param, policyName);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyFilterSchemaVersion_Param, schemaVersion);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicySeed_Param, seed);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyID_Param, policyUuid);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param, newDataParameters);

		// <key>OperationType</key>
		// <integer>9</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddPolicy);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand updateSmartPolicyCommand(UUID adminUUID, PropertyList newFilterCriteria, int newFilterType,
			String newPolicyName, int newSchemaVersion, int newSeed, PropertyList oldFilterCriteria, int oldFilterType,
			String oldPolicyName, int oldSchemaVersion, int oldSeed, UUID policyUuid) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		PropertyList newDataParameters = new PropertyList();
		PropertyList oldDataParameters = new PropertyList();

		// <key>NewData</key>
		// <dict>
		// FilterCriteria,FilterQuery, FilterTables, FilterType, Name,
		// SchemaVersion, Seed, UniqueID
		// </dict>

		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyFilterCriteria_Param, newFilterCriteria);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyFilterType_Param, newFilterType);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyName_Param, newPolicyName);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyFilterSchemaVersion_Param, newSchemaVersion);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicySeed_Param, newSeed);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyID_Param, policyUuid);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param, newDataParameters);

		oldDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyFilterCriteria_Param, oldFilterCriteria);
		oldDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyFilterType_Param, oldFilterType);
		oldDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyName_Param, oldPolicyName);
		oldDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyFilterSchemaVersion_Param, oldSchemaVersion);
		oldDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicySeed_Param, oldSeed);
		oldDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyID_Param, policyUuid);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OldData_Param, oldDataParameters);

		// <key>OperationType</key>
		// <integer>9</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddPolicy);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand createDeletePoliciesCommand(UUID adminUUID, UUID[] policyUuid) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		PropertyList dataParameters = new PropertyList();

		// <key>Data</key>
		// <dict>
		// <key>PolicyIDs</key>
		// <array>
		// <string>A7B428A2-6162-4103-BDC0-1153A792FD7E</string>
		// <string>49740086-8E2F-4A9E-96ED-700186A31147</string>
		// </array>
		// <key>RemovePolicyLockedProfiles</key>
		// <false/>
		// </dict>

		dataParameters.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyIDs_Param, policyUuid);
		dataParameters.put(iOSDevicesDefines.kCobra_iOS_Policy_RemovePolicyLockedProfiles_Param, false);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameters);

		// <key>OperationType</key>
		// <integer>10</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemovePolicy);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	public static CobraAdminMiscDatabaseCommand renameStandardPolicyCommand(UUID adminUUID, String oldPolicyName, int oldSeed,
			UUID oldPolicyUuid, String newPolicyName, int newSeed, UUID newPolicyUuid) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		PropertyList oldDataParameters = new PropertyList();
		PropertyList newDataParameters = new PropertyList();

		// <key>CommandParameters</key>
		// <dict>
		// <key>NewData</key>
		// <dict>
		// <key>Name</key>
		// <string>MyPolicy4</string>
		// <key>Seed</key>
		// <integer>1</integer>
		// <key>UniqueID</key>
		// <string>8F146CD5-ABC2-485E-9F74-90A3E40B28B5</string>
		// </dict>
		// <key>OldData</key>
		// <dict>
		// <key>Name</key>
		// <string>MyPolicy3</string>
		// <key>Seed</key>
		// <integer>1</integer>
		// <key>UniqueID</key>
		// <string>8F146CD5-ABC2-485E-9F74-90A3E40B28B5</string>
		// </dict>
		// <key>OperationType</key>
		// <integer>9</integer>
		// </dict>

		oldDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyName_Param, oldPolicyName);
		oldDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicySeed_Param, oldSeed);
		oldDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyID_Param, oldPolicyUuid);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OldData_Param, oldDataParameters);

		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyName_Param, newPolicyName);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicySeed_Param, newSeed);
		newDataParameters.put(iOSDevicesDefines.kCobra_iOS_PolicyID_Param, newPolicyUuid);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param, newDataParameters);

		// <key>OperationType</key>
		// <integer>9</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddPolicy);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}

	/**
	 * Creates an Assign Configuration Profile To Policy command, based on the
	 * input parameters.
	 * 
	 * @param configurationProfileUUIDs
	 *            The list of configuration profiles to assign to the policy.
	 * @param policyUUID
	 *            The UUID of the policy to assign the media to.
	 * @param assignmentType
	 *            The type of assignment, e.g.
	 *            kCobra_iOS_Policy_ConfigProfile_PolicyOptional
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createAssignConfigurationProfileToPolicyCommand(UUID[] configurationProfileUUIDs,
			UUID policyUUID, int assignmentType, UUID adminUUID) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddConfigProfilesToPolicy);

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AssignmentType_Param, assignmentType);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfileList_Param, configurationProfileUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;
	}

	/**
	 * Creates a Remove Configuration Profile From Policy command, based on the
	 * input parameters.
	 * 
	 * @param configurationProfileUUIDs
	 *            The list of configuration profiles to remove from the policy.
	 * @param policyUUID
	 *            The UUID of the policy to remove the media from.
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createRemoveConfigurationProfileFromPolicyCommand(UUID[] configurationProfileUUIDs,
			UUID policyUUID, UUID adminUUID) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveConfigProfilesFromPolicy);

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfileList_Param, configurationProfileUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;

	}

	/**
	 * Creates an Assign In-House Applications To Policy command, based on the
	 * input parameters.
	 * 
	 * @param inHouseAppUUIDs
	 *            The list of in-house applications to assign to the policy.
	 * @param policyUUID
	 *            The UUID of the policy to assign the applications to.
	 * @param assignmentType
	 *            The type of assignment, e.g.
	 *            kCobra_iOS_Policy_AppState_Required
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createAssignInHouseAppToPolicyCommand(UUID[] inHouseAppUUIDs, UUID policyUUID,
			int assignmentType, UUID adminUUID) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		// for assignmentType == Forbidden, operation type = 15, otherwise 13
		if (assignmentType == 0) {
			commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
					iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddForbiddenAppsToPolicy);
		} else {
			commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
					iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddAllowedAppsToPolicy);
		}

		PropertyList dataParameter = new PropertyList();

		if (assignmentType != 0) {
			dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AssignmentType_Param, assignmentType);
		}
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_ApplicationList_Param, inHouseAppUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;
	}

	/**
	 * Creates a Remove In-House Application From Policy command, based on the
	 * input parameters.
	 * 
	 * @param inHouseAppUUIDs
	 *            The list of in-house applications to remove from the policy.
	 * @param policyUUID
	 *            The UUID of the policy to remove the application from.
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createRemoveInHouseAppFromPolicyCommand(UUID[] inHouseAppUUIDs, UUID policyUUID,
			UUID adminUUID) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveAllowedAppsFromPolicy);
		// TODO: figure out when
		// kCobra_iOS_DatabaseOperation_OperationType_RemoveForbiddenAppsFromPolicy
		// should be used

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_ApplicationList_Param, inHouseAppUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;

	}

	/**
	 * Creates an Assign Third-Party Applications To Policy command, based on
	 * the input parameters.
	 * 
	 * @param thirdPartyAppUUIDs
	 *            The list of third-party applications to assign to the policy.
	 * @param policyUUID
	 *            The UUID of the policy to assign the applications to.
	 * @param assignmentType
	 *            The type of assignment, e.g.
	 *            kCobra_iOS_Policy_AppState_Required
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createAssignThirdPartyAppToPolicyCommand(UUID[] thirdPartyAppUUIDs, UUID policyUUID,
			int assignmentType, UUID adminUUID) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddRecommendedAppstoreAppsToPolicy);

		PropertyList dataParameter = new PropertyList();

		if (assignmentType != 0) {
			dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_AssignmentType_Param, assignmentType);
		}
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_ApplicationList_Param, thirdPartyAppUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;
	}

	/**
	 * Creates a Remove Third-Party Application From Policy command, based on
	 * the input parameters.
	 * 
	 * @param thirdPartyAppUUIDs
	 *            The list of third-party applications to remove from the
	 *            policy.
	 * @param policyUUID
	 *            The UUID of the policy to remove the applications from.
	 * @param adminUUID
	 *            The AdminUUID of the currently logged in user.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createRemoveThirdPartyAppFromPolicyCommand(UUID[] thirdPartyAppUUIDs, UUID policyUUID,
			UUID adminUUID) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveRecommendedAppstoreAppsFromPolicy);

		PropertyList dataParameter = new PropertyList();
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_ApplicationList_Param, thirdPartyAppUUIDs);
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUUID);

		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameter);

		CommandInfoData inCommandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(inCommandInfoData, adminUUID,
				commandParameters);
		return iOSDatabaseCommand;

	}

	public static CobraAdminMiscDatabaseCommand createActionCommand(
            UUID adminUUID,
            String actionUuid,
            int seed,
            int actionType,
            int supportedPlatforms,
            String actionName,
            String description,
            PropertyList actionData) throws IOException, GeneralSecurityException {
		
		PropertyList commandParameters = new PropertyList();
		PropertyList newDataParameters = new PropertyList();
		
		//<dict>
		//<key>NewData</key>
		//<dict>
		//	<key>ActionData</key>
		//	<dict>
		//		<key>IsReenrollment</key>
		//		<false/>
		//		<key>MessageText</key>
		//		<string>message text</string>
		//		<key>Timestamp</key>
		//		<date>2015-03-26T20:55:45Z</date>
		//	</dict>
		//	<key>ActionType</key>
		//	<integer>1</integer>
		//	<key>Description</key>
		//	<string>action description</string>
		//	<key>DisplayName</key>
		//	<string>send message test</string>
		//	<key>Seed</key>
		//	<integer>1</integer>
		//	<key>SupportedPlatforms</key>
		//	<integer>3</integer>
		//	<key>UniqueID</key>
		//	<string>6C34E436-BF57-489F-842C-93C43C9799D2</string>
		//</dict>
		
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionUniqueID_Param, actionUuid);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionSeed_Param, seed);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionActionType_Param, actionType);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionSupportedPlatforms_Param, supportedPlatforms);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionDisplayName_Param, actionName);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionDescription_Param, description);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionActionData_Param, actionData);
		
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param, newDataParameters);
		
		//<key>OperationType</key>
		//<integer>43</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param, 
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddAction);
		
		CommandInfoData commandInfoData = new CommandInfoData(
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(
				commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}
	
	public static CobraAdminMiscDatabaseCommand updateActionCommand(UUID adminUUID,
			int actionId,
			String actionUuid,
            int seed,
            int newActionType,
            int newSupportedPlatforms,
            String newActionName,
            String newDescription,
            PropertyList newActionData,
            
            int oldActionType,
            int oldSupportedPlatforms,
            String oldActionName,
            String oldDescription
			) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		PropertyList newDataParameters = new PropertyList();
		PropertyList oldDataParameters = new PropertyList();

		//<key>NewData</key>
		//<dict>
		//	<key>ActionData</key>
		//	<dict>
		//		<key>DataRoaming</key>
		//		<integer>1</integer>
		//		<key>VoiceRoaming</key>
		//		<integer>1</integer>
		//	</dict>
		//	<key>ActionType</key>
		//	<integer>2</integer>
		//	<key>Description</key>
		//	<string>action description</string>
		//	<key>DisplayName</key>
		//	<string>set roaming options test</string>
		//	<key>Seed</key>
		//	<integer>1</integer>
		//	<key>SupportedPlatforms</key>
		//	<integer>1</integer>
		//	<key>UniqueID</key>
		//	<string>64851483-5A5D-4CD5-822D-EA41120F51F7</string>
		//	<key>id</key>
		//	<integer>21</integer>
		//	<key>last_modified</key>
		//	<string>2015-03-26T21:07:25Z</string>
		//</dict>
		//<key>OldData</key>
		//<dict>
		//	<key>ActionData</key>
		//	<dict>
		//		<key>DataRoaming</key>
		//		<integer>1</integer>
		//		<key>VoiceRoaming</key>
		//		<integer>1</integer>
		//	</dict>
		//	<key>ActionType</key>
		//	<integer>2</integer>
		//	<key>Description</key>
		//	<string>action description</string>
		//	<key>DisplayName</key>
		//	<string>set roaming options</string>
		//	<key>Seed</key>
		//	<integer>1</integer>
		//	<key>SupportedPlatforms</key>
		//	<integer>1</integer>
		//	<key>UniqueID</key>
		//	<string>64851483-5A5D-4CD5-822D-EA41120F51F7</string>
		//	<key>id</key>
		//	<integer>21</integer>
		//	<key>last_modified</key>
		//	<string>2015-03-26T21:07:25Z</string>
		//</dict>

		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionID_Param, actionId);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionUniqueID_Param, actionUuid);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionSeed_Param, seed);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionActionType_Param, newActionType);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionSupportedPlatforms_Param, newSupportedPlatforms);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionDisplayName_Param, newActionName);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionDescription_Param, newDescription);
		newDataParameters.put(iOSDevicesDefines.kCobra_MobileActionActionData_Param, newActionData);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param, newDataParameters);
		
		oldDataParameters.put(iOSDevicesDefines.kCobra_MobileActionID_Param, actionId);
		oldDataParameters.put(iOSDevicesDefines.kCobra_MobileActionUniqueID_Param, actionUuid);
		oldDataParameters.put(iOSDevicesDefines.kCobra_MobileActionSeed_Param, seed);
		oldDataParameters.put(iOSDevicesDefines.kCobra_MobileActionActionType_Param, oldActionType);
		oldDataParameters.put(iOSDevicesDefines.kCobra_MobileActionSupportedPlatforms_Param, oldSupportedPlatforms);
		oldDataParameters.put(iOSDevicesDefines.kCobra_MobileActionDisplayName_Param, oldActionName);
		oldDataParameters.put(iOSDevicesDefines.kCobra_MobileActionDescription_Param, oldDescription);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OldData_Param, oldDataParameters);
		
		// <key>OperationType</key>
		// <integer>43</integer>
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddAction);

		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}
	
	
	public static CobraAdminMiscDatabaseCommand createDeleteActionsCommand(UUID adminUUID, int[] actioIds) 
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		
		//<dict>
		//	<key>Data</key>
		//	<array>
		//		<integer>16</integer>
		//	</array>
		//	<key>OperationType</key>
		//	<integer>44</integer>
		//</dict>
		
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, actioIds);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveAction);
		
		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);
		
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(
				commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}
	
	public static CobraAdminMiscDatabaseCommand createAssignActionsToPolicyCommand(
            UUID adminUUID,
            String[] actionUuids,
            int initialDelay,
            int repeatInterval,
            int repeatCount,
            String policyUuid) throws IOException, GeneralSecurityException {
		
		PropertyList commandParameters = new PropertyList();
		PropertyList dataParameters = new PropertyList();
		
		//<dict>
		//	<key>Data</key>
		//	<dict>
		//		<key>ActionList</key>
		//		<array>
		//			<string>1B024729-41BF-46ED-BFF6-DE3F75EF5DEF</string>
		//		</array>
		//		<key>InitialDelay</key>
		//		<integer>3600</integer>
		//		<key>PolicyID</key>
		//		<string>8589646F-1A5E-48D3-8C62-285147824396</string>
		//		<key>RepeatCount</key>
		//		<integer>1</integer>
		//		<key>RepeatInterval</key>
		//		<integer>3600</integer>
		//	</dict>
		//	<key>OperationType</key>
		//	<integer>45</integer>
	    //</dict>
		
		dataParameters.put(iOSDevicesDefines.kCobra_iOS_ActionList_Param, actionUuids);
		dataParameters.put(iOSDevicesDefines.kCobra_iOS_InitialDelay_Param, initialDelay);
		dataParameters.put(iOSDevicesDefines.kCobra_iOS_RepeatInterval_Param, repeatInterval);
		dataParameters.put(iOSDevicesDefines.kCobra_iOS_RepeatCount_Param, repeatCount);
		dataParameters.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUuid);
		
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameters);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param, 
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddActionToPolicy);
		
		CommandInfoData commandInfoData = new CommandInfoData(
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(
				commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}
	
	public static CobraAdminMiscDatabaseCommand createRemoveActionsFromPolicyCommand(
            UUID adminUUID,
            String[] actionUuids,
            String policyUuid) throws IOException, GeneralSecurityException {
		
		PropertyList commandParameters = new PropertyList();
		PropertyList dataParameters = new PropertyList();
		
		//<dict>
		//<key>Data</key>
		//<dict>
		//	<key>ActionList</key>
		//	<array>
		//		<string>1B024729-41BF-46ED-BFF6-DE3F75EF5DEF</string>
		//	</array>
		//	<key>PolicyID</key>
		//	<string>8589646F-1A5E-48D3-8C62-285147824396</string>
		//</dict>
		//<key>OperationType</key>
		//<integer>46</integer>
		//</dict>
		
		dataParameters.put(iOSDevicesDefines.kCobra_iOS_ActionList_Param, actionUuids);
		dataParameters.put(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param, policyUuid);
		
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, dataParameters);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param, 
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveActionFromPolicy);
		
		CommandInfoData commandInfoData = new CommandInfoData(
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(
				commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}
	
	public static CobraAdminMiscDatabaseCommand createDeleteMobileDevicePerformedActionsCommand(UUID adminUUID, long[] mobileDevicePerformedActionHistoryIds) 
			throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		
		//<dict>
		//	<key>Data</key>
		//	<array>
		//		<integer>11</integer>
		//		<integer>12</integer>
		//	</array>
		//	<key>OperationType</key>
		//	<integer>47</integer>
		//</dict>
		
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, mobileDevicePerformedActionHistoryIds);
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveActionHistoryRecords);
		
		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);
		
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(
				commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}
	
	public static CobraAdminMiscDatabaseCommand createReExecuteMobileDevicePerformedActionsCommand(UUID adminUUID,
			MobileDevicePerformedAction[] performedActions, boolean executeImmediately) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		PropertyList actionToResetParameters = new PropertyList();
		List<PropertyList> performedActionParameters = new ArrayList<PropertyList>();
		//<dict>
		//	<key>Data</key>
		//	<dict>
		//		<key>ActionsToReset</key>
		//		<array>
		//			<dict>
		//				<key>ActionUniqueID</key>
		//				<string>5712C25C-B48D-4F4F-8804-442ED088F131</string>
		//				<key>iphone_info_record_id</key>
		//				<integer>8</integer>
		//			</dict>
		//		</array>
		//		<key>SendMDMPush</key>
		//		<true/>
		//	</dict>
		//	<key>OperationType</key>
		//	<integer>48</integer>
		//</dict>
		
		for(MobileDevicePerformedAction pa : performedActions) {
			PropertyList pl = new PropertyList();
			pl.put(iOSDevicesDefines.kCobra_iOS_ActionUniqueID_Param, pa.getActionUniqueID());
			pl.put(iOSDevicesDefines.kCobra_iOS_iPhoneInfoRecordId_Param, pa.getMobileDeviceId());
			performedActionParameters.add(pl);
		}
		actionToResetParameters.put(iOSDevicesDefines.kCobra_iOS_ActionToReset_Param, 
				performedActionParameters.toArray());
		actionToResetParameters.put(iOSDevicesDefines.kCobra_iOS_SendMDMPush_Param, 
				executeImmediately);
		
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, 
				actionToResetParameters);		
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_ResetActions);
		
		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);
		
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(
				commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}
	
	public static CobraAdminMiscDatabaseCommand createReExecuteActionsForPoliciesCommand(UUID adminUUID,
			PolicyUuidActionUuidMapping[] policyUuidActionUuidMappings, boolean executeImmediately) throws IOException, GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();
		PropertyList dataParameter = new PropertyList();
		List<PropertyList> actionToResetParameters = new ArrayList<PropertyList>();;
		
		//<dict>
		//	<key>Data</key>
		//	<dict>
		//		<key>ActionsToReset</key>
		//		<array>
		//			<dict>
		//				<key>ActionUniqueID</key>
		//				<string>0BDE1CF9-53F5-43AA-B409-7BD3065A0F0A</string>
		//				<key>PolicyUniqueID</key>
		//				<string>248787BC-B4AA-4160-9C42-90D2837EB387</string>
		//			</dict>
		//		</array>
		//		<key>SendMDMPush</key>
		//		<true/>
		//	</dict>
		//	<key>OperationType</key>
		//	<integer>48</integer>
		//</dict>
		
		for (PolicyUuidActionUuidMapping map : policyUuidActionUuidMappings) {
			PropertyList mapParameter = new PropertyList();
			mapParameter.put("ActionUniqueID", map.getActionUuid());
			mapParameter.put("PolicyUniqueID", map.getPolicyUuid());
			actionToResetParameters.add(mapParameter);
		}

		dataParameter.put("ActionsToReset", actionToResetParameters.toArray());
		dataParameter.put(iOSDevicesDefines.kCobra_iOS_SendMDMPush_Param,  executeImmediately);
		
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param, 
				dataParameter);
		
		commandParameters.put(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param,
				iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_ResetActions);
		
		CommandInfoData commandInfoData = new CommandInfoData(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Command,
				CobraProtocol.kCobraAdminServer);
		
		CobraAdminMiscDatabaseCommand iOSDatabaseCommand = new CobraAdminMiscDatabaseCommand(
				commandInfoData, adminUUID, commandParameters);
		return iOSDatabaseCommand;
	}
}
