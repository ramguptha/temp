package test.com.absolute.am.command;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import com.absolute.am.command.CPLATPassword;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraCommandDefs;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.CobraUserCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.command.iOSDevicesDefines;
import com.absolute.am.dal.model.ConfigurationProfile;
import com.absolute.am.dal.model.CustomField;
import com.absolute.am.dal.model.CustomFieldActionDefinition;
import com.absolute.am.dal.model.MobileMedia;
import com.absolute.am.dal.model.ProvisioningProfile;
import com.absolute.am.dal.model.iOSAppStoreApplications;
import com.absolute.am.dal.model.iOSApplications;
import com.absolute.am.model.command.MobileDevicePerformedAction;
import com.absolute.am.model.customfieldmobiledevice.CustomFieldItem;
import com.absolute.am.model.policyaction.PolicyUuidActionUuidMapping;
import com.absolute.util.FileUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

public class CommandFactoryTest {
	private static final String SESSION_TOKEN = "JSESSIONID=AA95AB555E580EC456E3C19FB9F42DD3";
	private static final UUID LOGIN_RETURNED_ADMIN_UUID = UUID.randomUUID();
	
	// =============== Generic Command Tests ===============
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_new_generic_command() throws IOException, GeneralSecurityException {
		int commandId = 1;
		String key = "test_key";
		String value = "test_value";
		Map<String, Object> commandParametersMap = new HashMap<String, Object>();
		commandParametersMap.put(key, value);
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createGenericCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				commandId,
				commandParametersMap);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		String value2 = (String) param.get(key);
		assertTrue(value.compareToIgnoreCase(value2) == 0);		
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_login_command() throws IOException, GeneralSecurityException {
		String username = "test_user";
		String password = "test_pass";
		String codedPassword = (new CPLATPassword(password)).Encrypt(CobraCommandDefs.kBlowfishAdminLogingPasswordKey.getBytes("UTF-8"));
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createLoginCommand(
				username,
				password);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
	
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		
		// check login info
		String username2 = (String) param.get(CobraCommandDefs.kCobra_AdminLogin_AdminName_Param);
		String password2 = (String) param.get(CobraCommandDefs.kCobra_AdminLogin_AdminPassword_Param);
		assertTrue(username.compareToIgnoreCase(username2) == 0);
		assertTrue(codedPassword.compareToIgnoreCase(password2) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_delete_history_commands_command() throws IOException, GeneralSecurityException {
		int[] historyCommandIds = {1};
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createDeleteHistoryCommandsCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				historyCommandIds);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		
		// check history command ids
		int[] historyCommandIds2 = (int[]) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(Arrays.equals(historyCommandIds, historyCommandIds2));
		assertTrue(operationType == CobraCommandDefs.kCobra_DeleteCommands_History_OperationType);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_delete_queued_commands_command() throws IOException, GeneralSecurityException {
		int[] queuedCommandIds = {1};
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createDeleteQueuedCommandsCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				queuedCommandIds);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		
		// check history command ids
		int[] queuedCommandIds2 = (int[]) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(Arrays.equals(queuedCommandIds, queuedCommandIds2));
		assertTrue(operationType == CobraCommandDefs.kCobra_DeleteCommands_Queued_OperationType);
	}
	// =============== End of Generic Command Tests ===============
	
	
	// =============== Action Tests ===============
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_new_action_command() throws IOException, GeneralSecurityException {
		String actionUuid = UUID.randomUUID().toString();
		int seed = 1;
		String actionName = "unit_test_action_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		int actionType = 8; //UpdateDeviceInformation
		int supportedPlatforms = 1;
		CobraAdminMiscDatabaseCommand command = CommandFactory.createActionCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				actionUuid,
				seed,
				actionType,
				supportedPlatforms, actionName, "", null);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
				
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the action data
		PropertyList action = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param);
		assertNotNull(action);
		String actionUuid2 = (String) action.get(iOSDevicesDefines.kCobra_MobileActionUniqueID_Param);
		String actionName2 = (String) action.get(iOSDevicesDefines.kCobra_MobileActionDisplayName_Param);
		long actionType2 = (long) action.get(iOSDevicesDefines.kCobra_MobileActionActionType_Param);
		long seed2 = (long) action.get(iOSDevicesDefines.kCobra_MobileActionSeed_Param);
		long supportedPlatforms2 = (long) action.get(iOSDevicesDefines.kCobra_MobileActionSupportedPlatforms_Param);
		assertTrue(actionUuid.compareToIgnoreCase(actionUuid2) == 0);
		assertTrue(actionName.compareToIgnoreCase(actionName2) == 0);
		assertTrue(actionType == actionType2);
		assertTrue(seed == seed2);
		assertTrue(supportedPlatforms == supportedPlatforms2);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddAction);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_update_action_command() throws IOException, GeneralSecurityException {
		int actionId = 1;
		String actionUuid = UUID.randomUUID().toString();
		int seed = 1;
		String actionName = "unit_test_action_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		String description = "description";
		String newActionName = actionName + " (new)";
		String newDescription = description +" (new)";
		int actionType = 8; //UpdateDeviceInformation
		int supportedPlatforms = 1;
		CobraAdminMiscDatabaseCommand command = CommandFactory.updateActionCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				actionId,
				actionUuid,
				seed,
				actionType,
				supportedPlatforms, 
				newActionName, 
				newDescription, 
				null,
				actionType,
				supportedPlatforms,
				actionName,
				description
				);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
				
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the new action data
		PropertyList newAction = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param);
		assertNotNull(newAction);
		String actionUuid2 = (String) newAction.get(iOSDevicesDefines.kCobra_MobileActionUniqueID_Param);
		String actionName2 = (String) newAction.get(iOSDevicesDefines.kCobra_MobileActionDisplayName_Param);
		String descrption2 = (String) newAction.get(iOSDevicesDefines.kCobra_MobileActionDescription_Param);
		long actionType2 = (long) newAction.get(iOSDevicesDefines.kCobra_MobileActionActionType_Param);
		long seed2 = (long) newAction.get(iOSDevicesDefines.kCobra_MobileActionSeed_Param);
		long supportedPlatforms2 = (long) newAction.get(iOSDevicesDefines.kCobra_MobileActionSupportedPlatforms_Param);
		assertTrue(actionUuid.compareToIgnoreCase(actionUuid2) == 0);
		assertTrue(newActionName.compareToIgnoreCase(actionName2) == 0);
		assertTrue(newDescription.compareToIgnoreCase(descrption2) == 0);
		assertTrue(actionType == actionType2);
		assertTrue(seed == seed2);
		assertTrue(supportedPlatforms == supportedPlatforms2);
		
		// check the old action data
		PropertyList oldAction = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OldData_Param);
		assertNotNull(oldAction);
		
		String oldActionName2 = (String) oldAction.get(iOSDevicesDefines.kCobra_MobileActionDisplayName_Param);
		long oldActionType2 = (long) oldAction.get(iOSDevicesDefines.kCobra_MobileActionActionType_Param);
		long oldSeed2 = (long) oldAction.get(iOSDevicesDefines.kCobra_MobileActionSeed_Param);
		long oldSupportedPlatforms2 = (long) oldAction.get(iOSDevicesDefines.kCobra_MobileActionSupportedPlatforms_Param);
		
		assertTrue(actionName.compareToIgnoreCase(oldActionName2) == 0);
		assertTrue(actionType == oldActionType2);
		assertTrue(seed == oldSeed2);
		assertTrue(supportedPlatforms == oldSupportedPlatforms2);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddAction);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_delete_action_command() throws IOException, GeneralSecurityException {
		int[] actionIds = {1};

		CobraAdminMiscDatabaseCommand command = CommandFactory.createDeleteActionsCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				actionIds);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
				
		// check the action ids
		int[] actionIds2 = (int[]) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(actionIds2);
		assertTrue(Arrays.equals(actionIds, actionIds2));
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveAction);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_assign_action_to_policy_command() throws IOException, GeneralSecurityException {
		String[] actionUuids = {"3372FA64-0EF0-4599-ADBB-4D3543715864"};
		int initialDelay = 60;
        int repeatInterval = 0;
        int repeatCount = 0;
        String policyUuid = "977725F5-BCE0-4D8C-BB24-7834C445E3D6";

		CobraAdminMiscDatabaseCommand command = CommandFactory.createAssignActionsToPolicyCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				actionUuids,
				initialDelay,
				repeatInterval,
				repeatCount,
				policyUuid);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
				
		// check the policy-action assignment data
		PropertyList polictActionAssignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(polictActionAssignments);
		String[] actionUuids2 = (String[]) polictActionAssignments.get(iOSDevicesDefines.kCobra_iOS_ActionList_Param);
		long initialDelay2 = (long) polictActionAssignments.get(iOSDevicesDefines.kCobra_iOS_InitialDelay_Param);
		long repeatInterval2 = (long) polictActionAssignments.get(iOSDevicesDefines.kCobra_iOS_RepeatInterval_Param);
		long repeatCount2 = (long) polictActionAssignments.get(iOSDevicesDefines.kCobra_iOS_RepeatCount_Param);
        String policyUuid2 = (String) polictActionAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
        assertTrue(Arrays.equals(actionUuids, actionUuids2));
        assertTrue(initialDelay == initialDelay2);
        assertTrue(repeatInterval == repeatInterval2);
        assertTrue(repeatCount == repeatCount2);
		assertTrue(policyUuid.compareToIgnoreCase(policyUuid2) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddActionToPolicy);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remove_action_from_policy_command() throws IOException, GeneralSecurityException {
		String[] actionUuids = {"3372FA64-0EF0-4599-ADBB-4D3543715864"};
        String policyUuid = "977725F5-BCE0-4D8C-BB24-7834C445E3D6";

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveActionsFromPolicyCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				actionUuids,
				policyUuid);
		
		PropertyList pl = command.buildCommandDictionary();
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the policy/action ids
		PropertyList polictActionAssignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(polictActionAssignments);
		String[] actionUuids2 = (String[]) polictActionAssignments.get(iOSDevicesDefines.kCobra_iOS_ActionList_Param);
		String policyUuid2 = (String) polictActionAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
		assertTrue(Arrays.equals(actionUuids, actionUuids2));
		assertTrue(policyUuid.compareToIgnoreCase(policyUuid2) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveActionFromPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_delete_mobile_device_performed_action_command() throws IOException, GeneralSecurityException {
		long[] mobileDevicePerformedActionHistoryIds = {1};

		CobraAdminMiscDatabaseCommand command = CommandFactory.createDeleteMobileDevicePerformedActionsCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				mobileDevicePerformedActionHistoryIds);
		
		PropertyList pl = command.buildCommandDictionary();
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the mobile device performed action ids
		long[] mobileDevicePerformedActionHistoryIds2 = (long[]) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(mobileDevicePerformedActionHistoryIds2);
		assertTrue(Arrays.equals(mobileDevicePerformedActionHistoryIds, mobileDevicePerformedActionHistoryIds2));
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveActionHistoryRecords);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_reexcute_mobile_device_performed_action_command() throws IOException, GeneralSecurityException {
		MobileDevicePerformedAction mobileDevicePerformedAction = new MobileDevicePerformedAction();
		mobileDevicePerformedAction.setActionUniqueID("3372FA64-0EF0-4599-ADBB-4D3543715864");
		mobileDevicePerformedAction.setMobileDeviceId(1);
		MobileDevicePerformedAction[] performedActions = {mobileDevicePerformedAction};
		boolean executeImmediately = true;

		CobraAdminMiscDatabaseCommand command = CommandFactory.createReExecuteMobileDevicePerformedActionsCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				performedActions, executeImmediately);
		
		PropertyList pl = command.buildCommandDictionary();
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the re-executed mobile device performed action data
		PropertyList reexcuteMobileDevicePerformedActions = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(reexcuteMobileDevicePerformedActions);
		
		Object[] actionsToReset =(Object[]) reexcuteMobileDevicePerformedActions.get(iOSDevicesDefines.kCobra_iOS_ActionToReset_Param);
		assertNotNull(actionsToReset);
		assertTrue(actionsToReset.length == performedActions.length);
		
		for (int i=0; i <performedActions.length; i++) {
			PropertyList actionToReset = (PropertyList) actionsToReset[i]; 
			String actionUuid = (String) actionToReset.get(iOSDevicesDefines.kCobra_iOS_ActionUniqueID_Param);
			long mobileDeviceId = (long) actionToReset.get(iOSDevicesDefines.kCobra_iOS_iPhoneInfoRecordId_Param);
			
			assertTrue(performedActions[i].getActionUniqueID().compareToIgnoreCase(actionUuid) == 0);
			assertTrue(performedActions[i].getMobileDeviceId() == mobileDeviceId);
		}
		
		boolean executeImmediately2 = (boolean) reexcuteMobileDevicePerformedActions.get(iOSDevicesDefines.kCobra_iOS_SendMDMPush_Param);
		assertTrue(executeImmediately = executeImmediately2);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_ResetActions);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_reexcute_actions_for_policies_command() throws IOException, GeneralSecurityException {
		PolicyUuidActionUuidMapping policyUuidActionUuidMapping = new PolicyUuidActionUuidMapping();
		policyUuidActionUuidMapping.setActionUuid("3372FA64-0EF0-4599-ADBB-4D3543715864");
		policyUuidActionUuidMapping.setPolicyUuid("977725F5-BCE0-4D8C-BB24-7834C445E3D6");
		PolicyUuidActionUuidMapping[] policyUuidActionUuidMappings = {policyUuidActionUuidMapping};
		boolean executeImmediately = true;

		CobraAdminMiscDatabaseCommand command = CommandFactory.createReExecuteActionsForPoliciesCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				policyUuidActionUuidMappings, executeImmediately);
		
		PropertyList pl = command.buildCommandDictionary();
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the re-executed action data
		PropertyList reexcuteMobileDevicePerformedActions = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(reexcuteMobileDevicePerformedActions);
		
		Object[] actionsToReset =(Object[]) reexcuteMobileDevicePerformedActions.get(iOSDevicesDefines.kCobra_iOS_ActionToReset_Param);
		assertNotNull(actionsToReset);
		assertTrue(actionsToReset.length == policyUuidActionUuidMappings.length);
		
		for (int i=0; i <policyUuidActionUuidMappings.length; i++) {
			PropertyList actionToReset = (PropertyList) actionsToReset[i]; 
			String actionUuid = (String) actionToReset.get(iOSDevicesDefines.kCobra_iOS_ActionUniqueID_Param);
			String policyUuid = (String) actionToReset.get("PolicyUniqueID");
			
			assertTrue(policyUuidActionUuidMappings[i].getActionUuid().compareToIgnoreCase(actionUuid) == 0);
			assertTrue(policyUuidActionUuidMappings[i].getPolicyUuid().compareToIgnoreCase(policyUuid) == 0);
		}
		
		boolean executeImmediately2 = (boolean) reexcuteMobileDevicePerformedActions.get(iOSDevicesDefines.kCobra_iOS_SendMDMPush_Param);
		assertTrue(executeImmediately = executeImmediately2);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_ResetActions);
	}
	
	// =============== End of Action Tests ===============
	
	
	// =============== Application Tests (in-house application and 3rd party application) ===============	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_install_in_house_application_command() throws IOException, GeneralSecurityException {
		int[] deviceIds = {1};
		boolean isAndriod = true;
		iOSApplications inHouseAppDetails = new iOSApplications();
		inHouseAppDetails.setAppSize(67119);
		inHouseAppDetails.setBinaryPackageMD5("be9d6acd30b6117d9ae57f3845c41b6f");
		inHouseAppDetails.setBinaryPackageName("com.absolute.android.persistencetestinstaller");
		inHouseAppDetails.setBundleIdentifier("com.absolute.android.persistencetestinstaller");
		inHouseAppDetails.setDisplayName("ABTTestInstaller");
		inHouseAppDetails.setEncryptionKey("5B06E272846FE552A33F0F7BE8235E5CCB560A88484EDCA0682FD26A05350C1D899200491E617F60");
		inHouseAppDetails.setMinOSVersion(50364416);
		inHouseAppDetails.setName("ABTTestInstaller");
		inHouseAppDetails.setOriginalFileName("ABTTestInstaller_min11.apk");
		inHouseAppDetails.setPlatformType(11);
		inHouseAppDetails.setPreventAppDataBackup(false);
		inHouseAppDetails.setRemoveWhenMDMIsRemoved(false);
		inHouseAppDetails.setSeed(1);
		inHouseAppDetails.setUniqueID("9FF7797F-4D53-48F6-A015-F2DE4909EDEE");

		CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallInHouseApplicationCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				deviceIds, isAndriod,
				inHouseAppDetails);
		
		PropertyList pl = command.buildCommandDictionary();
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		
		// check the in-house application data
		long commandId = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		int[] deviceIds2 = (int[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		PropertyList requestData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		
		assertNotNull(deviceIds2);
		assertNotNull(requestData);
		assertTrue(deviceIds.length == deviceIds2.length);
		assertTrue(deviceIds[0] == deviceIds2[0]);
		assertTrue(commandId == CobraCommandDefs.kCobra_Admin_SendMDMCommand_InstallApplicationFromRepository);
		
		// check in-house application details
		int appSize = (int) requestData.get(iOSDevicesDefines.kCobra_iOS_AppAppSize_Param);
		String binaryPackageMD5 = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppBinaryPackageMD5_Param);
		String binaryPackageName = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppBinaryPackageName_Param);
		String bundleIdentifier = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppBundleIdentifier_Param);
		String displayName = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppDisplayName_Param);
		String encryptionKey = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppEncryptionKey_Param);
		int minOSVersion = (int) requestData.get(iOSDevicesDefines.kCobra_iOS_AppMinOSVersion_Param);;
		String name = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppName_Param);
		String originalFileName = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppOriginalFileName_Param);
		int platformType = (int) requestData.get(iOSDevicesDefines.kCobra_iOS_AppPlatformType_Param);
		boolean preventAppDataBackup = (boolean) requestData.get(iOSDevicesDefines.kCobra_iOS_AppPreventAppDataBackup_Param);
		boolean removeWhenMDMIsRemoved = (boolean) requestData.get(iOSDevicesDefines.kCobra_iOS_AppRemoveWhenMDMIsRemoved_Param);
		int seed = (int) requestData.get(iOSDevicesDefines.kCobra_iOS_AppSeed_Param);
		String uniqueID = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppID_Param);
		
		assertTrue(inHouseAppDetails.getAppSize() == appSize);
		assertTrue(inHouseAppDetails.getBinaryPackageMD5().compareToIgnoreCase(binaryPackageMD5) == 0);
		assertTrue(inHouseAppDetails.getBinaryPackageName().compareToIgnoreCase(binaryPackageName) == 0);
		assertTrue(inHouseAppDetails.getBundleIdentifier().compareToIgnoreCase(bundleIdentifier) == 0);
		assertTrue(inHouseAppDetails.getDisplayName().compareToIgnoreCase(displayName) == 0);
		assertTrue(inHouseAppDetails.getEncryptionKey().compareToIgnoreCase(encryptionKey) == 0);
		assertTrue(inHouseAppDetails.getMinOSVersion() == minOSVersion);
		assertTrue(inHouseAppDetails.getName().compareToIgnoreCase(name) == 0);
		assertTrue(inHouseAppDetails.getOriginalFileName().compareToIgnoreCase(originalFileName) == 0);
		assertTrue(inHouseAppDetails.getPlatformType() == platformType);
		assertTrue(inHouseAppDetails.getPreventAppDataBackup() == preventAppDataBackup);
		assertTrue(inHouseAppDetails.getRemoveWhenMDMIsRemoved() == removeWhenMDMIsRemoved);
		assertTrue(inHouseAppDetails.getSeed() == seed);
		assertTrue(inHouseAppDetails.getUniqueID().compareToIgnoreCase(uniqueID) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_install_third_party_application_command() throws IOException, GeneralSecurityException {
		int[] deviceIds = {1};
		
		iOSAppStoreApplications thirdPartyAppDetails = new iOSAppStoreApplications();
		thirdPartyAppDetails.setAppStoreID("com.metago.astro");
		thirdPartyAppDetails.setAppStoreURL("https://play.google.com/store/apps/details?id=com.metago.astro");
		thirdPartyAppDetails.setCategory("Utilities");
		// WARNING: had to correct a badly pasted non-UTF-8 character in the string below and as such the below test may fail
		thirdPartyAppDetails.setLongDescription("ASTRO helps organize & view your pictures, music, video, document & other files. ASTRO File Manager has 30 million downloads on the Android Market and 250,000 reviews! It's like Windows Explorer or Mac's Finder for your phone or tablet and allows you to easily browse and organize all of your pictures, music, videos and documents. It also gives you the ability to stop processes that burn battery life and backup your apps in case you lose or change phones.");
		thirdPartyAppDetails.setMinOSVersion(35684352);
		thirdPartyAppDetails.setName("Astro File Manager / Browser");
		thirdPartyAppDetails.setPlatformType(11);
		thirdPartyAppDetails.setSeed(1);
		thirdPartyAppDetails.setShortDescription("Astro File Manager");
		thirdPartyAppDetails.setUniqueID("0DCC956E-22BA-4D8B-8C13-032E09173673");
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallThirdPartyApplicationCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				deviceIds,
				thirdPartyAppDetails);
		
		PropertyList pl = command.buildCommandDictionary();
	
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
	
		// check the in-house application data
		long commandId = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		int[] deviceIds2 = (int[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		PropertyList requestData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		
		assertNotNull(deviceIds2);
		assertNotNull(requestData);
		assertTrue(deviceIds.length == deviceIds2.length);
		assertTrue(deviceIds[0] == deviceIds2[0]);
		assertTrue(commandId == CobraCommandDefs.kCobra_Admin_SendMDMCommand_InstallApplicationFromRepository);
		
		// check 3rd party application details
		String appStoreID = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStoreAppStoreID_Param);
		String appStoreURL = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStoreAppURL_Param);
		String category = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStoreAppCategory_Param);
		String longDescription = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStoreAppLongDescription_Param);		
		int minOSVersion = (int) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStoreAppMinOSVersion_Param);;
		String name = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStoreAppName_Param);
		int platformType = (int) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStorePlatformType_Param);
		int seed = (int) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStoreAppSeed_Param);
		String shortDescription = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStoreAppShortDescription_Param);
		String uniqueID = (String) requestData.get(iOSDevicesDefines.kCobra_iOS_AppStoreAppID_Param);
		
		assertTrue(thirdPartyAppDetails.getAppStoreID().compareToIgnoreCase(appStoreID) == 0);
		assertTrue(thirdPartyAppDetails.getAppStoreURL().compareToIgnoreCase(appStoreURL) == 0);
		assertTrue(thirdPartyAppDetails.getCategory().compareToIgnoreCase(category) == 0);
		assertTrue(thirdPartyAppDetails.getLongDescription().compareToIgnoreCase(longDescription) == 0);
		assertTrue(thirdPartyAppDetails.getMinOSVersion() == minOSVersion);
		assertTrue(thirdPartyAppDetails.getName().compareToIgnoreCase(name) == 0);
		assertTrue(thirdPartyAppDetails.getPlatformType() == platformType);
		assertTrue(thirdPartyAppDetails.getSeed() == seed);
		assertTrue(thirdPartyAppDetails.getShortDescription().compareToIgnoreCase(shortDescription) == 0);
		assertTrue(thirdPartyAppDetails.getUniqueID().compareToIgnoreCase(uniqueID) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_delete_application_command() throws IOException, GeneralSecurityException {
		long deviceId = 1;
		long[] applicationIds = {2};

		CobraAdminMiscDatabaseCommand command = CommandFactory.createDeleteApplicationCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				deviceId, applicationIds);
		
		PropertyList pl = command.buildCommandDictionary();
	
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
				
		// check the application ids
		long[] applicationIds2 = (long[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		assertNotNull(applicationIds2);
		assertTrue(Arrays.equals(applicationIds, applicationIds2));

		// check command id
		long commandId = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(commandId ==CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoveApplication);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_retry_all_failed_applications_command() throws IOException, GeneralSecurityException {
		UUID[] applicationUuids = {UUID.fromString("3DAA2B77-950E-42B5-B390-BAC09FBA1207")};

		CobraAdminMiscDatabaseCommand command = CommandFactory.retryAllFailedApplicationsCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				applicationUuids);
		
		PropertyList pl = command.buildCommandDictionary();
	
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
	
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);

		
		// check the application unique ids
		UUID[] applicationUuids2 = (UUID[]) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(applicationUuids2);
		assertTrue(applicationUuids.length == applicationUuids2.length);
		assertTrue(Arrays.equals(applicationUuids, applicationUuids2));

		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_ResetFailedInhouseApplications);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_assign_in_house_applications_to_policy_command_for_auto_install() throws IOException, GeneralSecurityException {
        int assignmentType = 1;	//Auto-install
        int expectedOperationType = iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddAllowedAppsToPolicy;
        can_create_assign_in_house_applications_to_policy_command(assignmentType, expectedOperationType);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_assign_in_house_applications_to_policy_command_for_forbidden() throws IOException, GeneralSecurityException {
        int assignmentType = 0;	//Forbidden
        int expectedOperationType = iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddForbiddenAppsToPolicy;
        can_create_assign_in_house_applications_to_policy_command(assignmentType, expectedOperationType);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remove_in_house_applications_from_policy_command() throws IOException, GeneralSecurityException {
		UUID[] applicationUuids = {UUID.fromString("3372FA64-0EF0-4599-ADBB-4D3543715864")};
        UUID policyUuid = UUID.fromString("977725F5-BCE0-4D8C-BB24-7834C445E3D6");

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveInHouseAppFromPolicyCommand(
				applicationUuids, policyUuid,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
	
		// check the policy-application assignment data
		PropertyList polictApplicationAssignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(polictApplicationAssignments);
		UUID[] applicationUuids2 = (UUID[]) polictApplicationAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_ApplicationList_Param);
        UUID policyUuid2 = (UUID) polictApplicationAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
        assertTrue(Arrays.equals(applicationUuids, applicationUuids2));
		assertTrue(policyUuid.toString().compareToIgnoreCase(policyUuid2.toString()) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveAllowedAppsFromPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_assign_third_party_applications_to_policy_command() throws IOException, GeneralSecurityException {
		UUID[] applicationUuids = {UUID.fromString("3372FA64-0EF0-4599-ADBB-4D3543715864")};
        UUID policyUuid = UUID.fromString("977725F5-BCE0-4D8C-BB24-7834C445E3D6");
        int assignmentType = 1;	//auto-install
        
		CobraAdminMiscDatabaseCommand command = CommandFactory.createAssignThirdPartyAppToPolicyCommand(
				applicationUuids, policyUuid, assignmentType,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));

	
		// check the policy-application assignment data
		PropertyList polictApplicationAssignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(polictApplicationAssignments);
		UUID[] applicationUuids2 = (UUID[]) polictApplicationAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_ApplicationList_Param);
        UUID policyUuid2 = (UUID) polictApplicationAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
        assertTrue(Arrays.equals(applicationUuids, applicationUuids2));
		assertTrue(policyUuid.toString().compareToIgnoreCase(policyUuid2.toString()) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddRecommendedAppstoreAppsToPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remove_third_party_applications_from_policy_command() throws IOException, GeneralSecurityException {
		UUID[] applicationUuids = {UUID.fromString("3372FA64-0EF0-4599-ADBB-4D3543715864")};
        UUID policyUuid = UUID.fromString("977725F5-BCE0-4D8C-BB24-7834C445E3D6");

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveThirdPartyAppFromPolicyCommand(
				applicationUuids, policyUuid,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
	
		// check the policy-application assignment data
		PropertyList polictApplicationAssignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(polictApplicationAssignments);
		UUID[] applicationUuids2 = (UUID[]) polictApplicationAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_ApplicationList_Param);
        UUID policyUuid2 = (UUID) polictApplicationAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
        assertTrue(Arrays.equals(applicationUuids, applicationUuids2));
		assertTrue(policyUuid.toString().compareToIgnoreCase(policyUuid2.toString()) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveRecommendedAppstoreAppsFromPolicy);
	}
	
	private void can_create_assign_in_house_applications_to_policy_command(int assignmentType, int expectedOperationType) throws IOException, GeneralSecurityException {
		UUID[] applicationUuids = {UUID.fromString("3372FA64-0EF0-4599-ADBB-4D3543715864")};
        UUID policyUuid = UUID.fromString("977725F5-BCE0-4D8C-BB24-7834C445E3D6");

		CobraAdminMiscDatabaseCommand command = CommandFactory.createAssignInHouseAppToPolicyCommand(
				applicationUuids, policyUuid, assignmentType,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));

	
		// check the policy-application assignment data
		PropertyList polictApplicationAssignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(polictApplicationAssignments);
		UUID[] applicationUuids2 = (UUID[]) polictApplicationAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_ApplicationList_Param);
        UUID policyUuid2 = (UUID) polictApplicationAssignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
        assertTrue(Arrays.equals(applicationUuids, applicationUuids2));
		assertTrue(policyUuid.toString().compareToIgnoreCase(policyUuid2.toString()) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == expectedOperationType);

	}
	
	// =============== End of Application Tests ===============
	
	
	// =============== SSP-Related Tests ===============
	// all the test cases are Self Service Portal (SSP) related
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_ssp_login_command() throws IOException, GeneralSecurityException {
		String username = "ssp_test_user";
		String password = "ssp_test_pass";
		String domain = "ssp_test";
		String codedUsername = (new CPLATPassword(username)).Encrypt(CobraCommandDefs.kSelfServiceLoginCredentialsKey.getBytes("UTF-8"));
		String codedPassword = (new CPLATPassword(password)).Encrypt(CobraCommandDefs.kSelfServiceLoginCredentialsKey.getBytes("UTF-8"));
		String codedDomain = (new CPLATPassword(domain)).Encrypt(CobraCommandDefs.kSelfServiceLoginCredentialsKey.getBytes("UTF-8"));
		
		CobraUserCommand command = CommandFactory.createSSPLoginCommand(
				username,
				password,
				domain);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(CobraCommandDefs.kCobra_OperationType_Param));
	
		// check the SSP login data
		PropertyList sspLoginData = (PropertyList) param.get(CobraCommandDefs.kCobra_Data_Param);
		assertNotNull(sspLoginData);
		String username2 = (String) sspLoginData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_Username_Param);
		String password2 = (String) sspLoginData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_Password_Param);
		String domain2 = (String) sspLoginData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_Domain_Param);
		assertTrue(codedUsername.compareToIgnoreCase(username2) == 0);
		assertTrue(codedPassword.compareToIgnoreCase(password2) == 0);
		assertTrue(codedDomain.compareToIgnoreCase(domain2) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(CobraCommandDefs.kCobra_OperationType_Param);
		assertTrue(operationType == CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_ssp_get_user_device_command() throws IOException, GeneralSecurityException {
		CobraUserCommand command = CommandFactory.createSSPGetUserDeviceCommand(SESSION_TOKEN);
		
		PropertyList pl = command.buildCommandDictionary();

		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(CobraCommandDefs.kCobra_OperationType_Param));
	
		// check the command data
		PropertyList sspLoginData = (PropertyList) param.get(CobraCommandDefs.kCobra_Data_Param);
		assertNotNull(sspLoginData);
		String sessionToken2 = (String) sspLoginData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam);
		boolean returnDeskTopDevices = (boolean) sspLoginData.get(CobraCommandDefs.kCobra_SelfService_OperationType_GetDeviceListForUser_ReturnDesktopDevices_Param);
		boolean returnMobileDevices = (boolean) sspLoginData.get(CobraCommandDefs.kCobra_SelfService_OperationType_GetDeviceListForUser_ReturnMobileDevices_Param);
		assertTrue(SESSION_TOKEN.compareToIgnoreCase(sessionToken2) == 0);
		assertTrue(returnDeskTopDevices);
		assertTrue(returnMobileDevices);
		
		// check the Operation Type 
		long operationType = (long) param.get(CobraCommandDefs.kCobra_OperationType_Param);
		assertTrue(operationType == CobraCommandDefs.kCobra_SelfService_OperationType_GetDeviceListForUser);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_ssp_send_message_command_for_mobile_device() throws IOException, GeneralSecurityException {
		String message = "unit test message";
		boolean withCancel = false;
		int timeout = 0;
		String deviceIdentifier = "012641000005741";	// it's mobile device when deviceIdentifier is NOT null
		String agentSerial = null;
		int deviceType = 0;
		String headerText = null;
		String timeoutCounterText = null;
	
		CobraUserCommand command = CommandFactory.createSSPSendMessageCommand(
				message,
				withCancel,
				timeout,
				deviceIdentifier,
				agentSerial,
				deviceType,
				SESSION_TOKEN,
				headerText,
				timeoutCounterText
				);
		
		PropertyList pl = command.buildCommandDictionary();

		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(CobraCommandDefs.kCobra_OperationType_Param));
	
		// check the command data
		PropertyList sspCommandData = (PropertyList) param.get(CobraCommandDefs.kCobra_Data_Param);
		assertNotNull(sspCommandData);
		String sessionToken2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam);
		String deviceIdentifier2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceTargetIdentifier_Param);
		long commandId2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(SESSION_TOKEN.compareToIgnoreCase(sessionToken2) == 0);
		assertTrue(deviceIdentifier.compareToIgnoreCase(deviceIdentifier2) == 0);
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_ShowMessage);
		
		// check the command parameter data inside of the command data
		PropertyList sspCommandParameterDataOfCommandData = (PropertyList) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param);
		assertNotNull(sspCommandParameterDataOfCommandData);
		String message2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_MessageText_Param);
		assertTrue(sspCommandParameterDataOfCommandData.containsKey(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Timestamp_Param));
		assertTrue(message.compareToIgnoreCase(message2) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(CobraCommandDefs.kCobra_OperationType_Param);
		assertTrue(operationType == CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_ssp_send_message_command_for_desktop_device() throws IOException, GeneralSecurityException {
		String message = "unit test message";
		boolean withCancel = false;
		int timeout =60;
		String deviceIdentifier = null;	// it's desktop device when deviceIdentifier is null
		String agentSerial = "58337408-7334-4AA3-ACAE-0FA62737BCFA";
		int deviceType = 1;
		String headerText = "for unit test";
		String timeoutCounterText = "timeout counter";
	
		CobraUserCommand command = CommandFactory.createSSPSendMessageCommand(
				message,
				withCancel,
				timeout,
				deviceIdentifier,
				agentSerial,
				deviceType,
				SESSION_TOKEN,
				headerText,
				timeoutCounterText
				);
		
		PropertyList pl = command.buildCommandDictionary();

		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(CobraCommandDefs.kCobra_OperationType_Param));
	
		// check the command data
		PropertyList sspCommandData = (PropertyList) param.get(CobraCommandDefs.kCobra_Data_Param);
		assertNotNull(sspCommandData);
		String sessionToken2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam);
		long deviceType2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param);
		String agentSerial2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_AgentSerial_Param);
		long commandId2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(SESSION_TOKEN.compareToIgnoreCase(sessionToken2) == 0);
		assertTrue(deviceType == deviceType2);
		assertTrue(agentSerial.compareToIgnoreCase(agentSerial2) == 0);
		assertTrue(commandId2 == CobraCommandDefs.kCobra_AgentSendMessage_Command);
		
		// check the command parameter data inside of the command data
		PropertyList sspCommandParameterDataOfCommandData = (PropertyList) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param);
		assertNotNull(sspCommandParameterDataOfCommandData);
		String message2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_MessageText_Param);
		long timeout2 = (long) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_AgentSendMessage_Timeout_Param);
		boolean withCancel2 = (boolean) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_AgentSendMessage_WithCancel_Param);
		String headerText2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_AgentSendMessage_HeaderText_Param);
		String timeoutCounterText2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_AgentSendMessage_TimeoutCounterText_Param);
		assertTrue(message.compareToIgnoreCase(message2) == 0);
		assertTrue(timeout == timeout2);
		assertTrue(withCancel == withCancel2);
		assertTrue(headerText.compareToIgnoreCase(headerText2) == 0);
		assertTrue(timeoutCounterText.compareToIgnoreCase(timeoutCounterText2) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(CobraCommandDefs.kCobra_OperationType_Param);
		assertTrue(operationType == CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_ssp_lock_device_command_for_mobile_device() throws IOException, GeneralSecurityException {
		String passcode = "unit_text_passcode";
		String message = "unit test message";
		String phoneNumber = "604-123-4567";
		String deviceIdentifier = "012641000005741";	// it's mobile device when deviceIdentifier is NOT null
		String agentSerial = null;
		int deviceType = 1;
		String codedPasscode = (new CPLATPassword(passcode)).Encrypt(CobraCommandDefs.kBlowfishMDMDeviceLockPasswordKey.getBytes("UTF-8"));
		
		CobraUserCommand command = CommandFactory.createSSPLockDevicesCommand(
				passcode, 
				message,
				phoneNumber,
				deviceIdentifier,
				agentSerial,
				deviceType,
				SESSION_TOKEN
				);
		
		PropertyList pl = command.buildCommandDictionary();

		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
 		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(CobraCommandDefs.kCobra_OperationType_Param));
	
		// check the command data
		PropertyList sspCommandData = (PropertyList) param.get(CobraCommandDefs.kCobra_Data_Param);
		assertNotNull(sspCommandData);
		String sessionToken2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam);
		String deviceIdentifier2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceTargetIdentifier_Param);
		long commandId2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandID_Param);
		long deviceType2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param);
		assertTrue(SESSION_TOKEN.compareToIgnoreCase(sessionToken2) == 0);
		assertTrue(deviceIdentifier.compareToIgnoreCase(deviceIdentifier2) == 0);
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_DeviceLock);
		assertTrue(deviceType == deviceType2);
		
		// check the command parameter data inside of the command data
		PropertyList sspCommandParameterDataOfCommandData = (PropertyList) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param);
		assertNotNull(sspCommandParameterDataOfCommandData);
		String passcode2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param);
		String message2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_Admin_LockMDMCommand_RequestData_Message_Param);
		String phoneNumber2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_Admin_LockMDMCommand_RequestData_PhoneNumber_Param);
		assertTrue(message.compareToIgnoreCase(message2) == 0);
		assertTrue(codedPasscode.compareToIgnoreCase(passcode2) == 0);
		assertTrue(phoneNumber.compareToIgnoreCase(phoneNumber2) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(CobraCommandDefs.kCobra_OperationType_Param);
		assertTrue(operationType == CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_ssp_lock_device_command_for_desktop_device() throws IOException, GeneralSecurityException {
		String passcode = "unit_text_passcode";
		String message = "unit test message";
		String phoneNumber = "604-123-4567";
		String deviceIdentifier = null;	// it's desktop device when deviceIdentifier is null
		String agentSerial = "58337408-7334-4AA3-ACAE-0FA62737BCFA";
		int deviceType = 1;
		
		CobraUserCommand command = CommandFactory.createSSPLockDevicesCommand(
				passcode, 
				message,
				phoneNumber,
				deviceIdentifier,
				agentSerial,
				deviceType,
				SESSION_TOKEN
				);
		
		PropertyList pl = command.buildCommandDictionary();

		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(CobraCommandDefs.kCobra_OperationType_Param));
	
		// check the command data
		PropertyList sspCommandData = (PropertyList) param.get(CobraCommandDefs.kCobra_Data_Param);
		assertNotNull(sspCommandData);
		String sessionToken2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam);
		String agentSerial2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_AgentSerial_Param);
		long commandId2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		long deviceType2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param);
		assertTrue(SESSION_TOKEN.compareToIgnoreCase(sessionToken2) == 0);
		assertTrue(agentSerial.compareToIgnoreCase(agentSerial2) == 0);
		assertTrue(commandId2 == CobraCommandDefs.kCobra_AgentLock_Command);
		assertTrue(deviceType == deviceType2);
		
		// check the command parameter data inside of the command data
		PropertyList sspCommandParameterDataOfCommandData = (PropertyList) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param);
		assertNotNull(sspCommandParameterDataOfCommandData);
		String requestType = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_Param);
		String passcode2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_PIN_Param);
		boolean isDeviceCommand = (boolean) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_IsDeviceCommand_Param);
		assertTrue(passcode.compareToIgnoreCase(passcode2) == 0);
		assertTrue(requestType.compareToIgnoreCase(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_DeviceLock) == 0);
		assertTrue(isDeviceCommand);
		
		// check the Operation Type 
		long operationType = (long) param.get(CobraCommandDefs.kCobra_OperationType_Param);
		assertTrue(operationType == CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_ssp_clean_passcode_command() throws IOException, GeneralSecurityException {
		String passcode = "unit_text_passcode";
		String deviceIdentifier = "012641000005741";	// it's desktop device when deviceIdentifier is null
		int deviceType = 1;
		String codedPasscode = (new CPLATPassword(passcode)).Encrypt(CobraCommandDefs.kBlowfishMDMDeviceLockPasswordKey.getBytes("UTF-8"));
		
		CobraUserCommand command = CommandFactory.createSSPClearPasscodeCommand(
				passcode, 
				deviceIdentifier,
				deviceType,
				SESSION_TOKEN
				);
		
		PropertyList pl = command.buildCommandDictionary();

		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(CobraCommandDefs.kCobra_OperationType_Param));
	
		// check the command data
		PropertyList sspCommandData = (PropertyList) param.get(CobraCommandDefs.kCobra_Data_Param);
		assertNotNull(sspCommandData);
		String sessionToken2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam);
		String deviceIdentifier2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceTargetIdentifier_Param);
		long commandId2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		long deviceType2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param);
		assertTrue(SESSION_TOKEN.compareToIgnoreCase(sessionToken2) == 0);
		assertTrue(deviceIdentifier.compareToIgnoreCase(deviceIdentifier2) == 0);
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_ClearPasscode);
		assertTrue(deviceType == deviceType2);
		
		// check the command parameter data inside of the command data
		PropertyList sspCommandParameterDataOfCommandData = (PropertyList) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param);
		assertNotNull(sspCommandParameterDataOfCommandData);
		String passcode2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param);
		assertTrue(codedPasscode.compareToIgnoreCase(passcode2) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(CobraCommandDefs.kCobra_OperationType_Param);
		assertTrue(operationType == CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_ssp_remote_erase_command_for_mobile_device() throws IOException, GeneralSecurityException {
		boolean includeSsdCard = true;
		String passcode = "unit_text_passcode";
		String deviceIdentifier = "012641000005741";	// it's mobile device when deviceIdentifier is NOT null
		String agentSerial = null;
		int deviceType = 1;
		
		CobraUserCommand command = CommandFactory.createSSPRemoteEraseCommand(
				includeSsdCard,
				passcode, 
				deviceIdentifier,
				agentSerial,
				deviceType,
				SESSION_TOKEN
				);
		
		PropertyList pl = command.buildCommandDictionary();

		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(CobraCommandDefs.kCobra_OperationType_Param));
	
		// check the command data
		PropertyList sspCommandData = (PropertyList) param.get(CobraCommandDefs.kCobra_Data_Param);
		assertNotNull(sspCommandData);
		String sessionToken2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam);
		String deviceIdentifier2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceTargetIdentifier_Param);
		long commandId2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		long deviceType2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param);
		assertTrue(SESSION_TOKEN.compareToIgnoreCase(sessionToken2) == 0);
		assertTrue(deviceIdentifier.compareToIgnoreCase(deviceIdentifier2) == 0);
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoteErase);
		assertTrue(deviceType == deviceType2);
		
		// check the command parameter data inside of the command data
		PropertyList sspCommandParameterDataOfCommandData = (PropertyList) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param);
		assertNotNull(sspCommandParameterDataOfCommandData);
		boolean includeSsdCard2 = (boolean) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_EraseSDCard_Param);
		assertTrue(includeSsdCard == includeSsdCard2);
		
		// check the Operation Type 
		long operationType = (long) param.get(CobraCommandDefs.kCobra_OperationType_Param);
		assertTrue(operationType == CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_ssp_remote_erase_command_for_desktop_device() throws IOException, GeneralSecurityException {
		boolean includeSsdCard = true;
		String passcode = "unit_text_passcode";
		String deviceIdentifier = null;	// it's desktop device when deviceIdentifier is null
		String agentSerial = "58337408-7334-4AA3-ACAE-0FA62737BCFA";
		int deviceType = 1;
		
		CobraUserCommand command = CommandFactory.createSSPRemoteEraseCommand(
				includeSsdCard,
				passcode, 
				deviceIdentifier,
				agentSerial,
				deviceType,
				SESSION_TOKEN
				);
		
		PropertyList pl = command.buildCommandDictionary();

		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
 		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(CobraCommandDefs.kCobra_OperationType_Param));
	
		// check the command data
		PropertyList sspCommandData = (PropertyList) param.get(CobraCommandDefs.kCobra_Data_Param);
		assertNotNull(sspCommandData);
		String sessionToken2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_SelfServiceLogin_SessionToken_ResultParam);
		String agentSerial2 = (String) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_AgentSerial_Param);
		long commandId2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		long deviceType2 = (long) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_DeviceType_Param);
		assertTrue(SESSION_TOKEN.compareToIgnoreCase(sessionToken2) == 0);
		assertTrue(agentSerial.compareToIgnoreCase(agentSerial2) == 0);
		assertTrue(commandId2 == CobraCommandDefs.kCobra_AgentErase_Command);
		assertTrue(deviceType == deviceType2);
		
		// check the command parameter data inside of the command data
		PropertyList sspCommandParameterDataOfCommandData = (PropertyList) sspCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_CommandParameters_Param);
		assertNotNull(sspCommandParameterDataOfCommandData);
		String requestType = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_Param);
		String passcode2 = (String) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_PIN_Param);
		boolean isDeviceCommand = (boolean) sspCommandParameterDataOfCommandData.get(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_IsDeviceCommand_Param);
		assertTrue(requestType.compareToIgnoreCase(CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand_RequestType_EraseDevice) == 0);
		assertTrue(passcode.compareToIgnoreCase(passcode2) == 0);
		assertTrue(isDeviceCommand);
		
		// check the Operation Type 
		long operationType = (long) param.get(CobraCommandDefs.kCobra_OperationType_Param);
		assertTrue(operationType == CobraCommandDefs.kCobra_SelfService_OperationType_PerformDeviceCommand);
	}
	
	// =============== End of SSP-Related Tests ===============

	
	// =============== Media Tests ===============
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_add_media_command() throws IOException, GeneralSecurityException {

		MobileMedia mediaInfo = populateMobileMedia();
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createAddMediaCommand(
				mediaInfo,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the media data
		PropertyList media = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param);
		assertNotNull(media);
		int[] attachedFileTypeList = (int[]) media.get(iOSDevicesDefines.kCobra_iOS_AttachedFileTypeList_Param);
		UUID[] attachedFileNameList =  (UUID[]) media.get(iOSDevicesDefines.kCobra_iOS_AttachedFileNameList_Param);
		String[] attachedFileSourcePathList = (String[]) media.get(iOSDevicesDefines.kCobra_iOS_AttachedFileSourcePathList_Param);
		
		assertTrue(Arrays.equals(attachedFileTypeList, new int[] { iOSDevicesDefines.kCobra_iOS_AttachedFileType_MediaFile }));
		assertTrue(Arrays.equals(attachedFileNameList, new UUID[] { UUID.fromString(mediaInfo.getUniqueId()) }));
		assertTrue(Arrays.equals(attachedFileSourcePathList, new String[] { mediaInfo.getFilename() }));
		
		// assert media detail data
		verifyMediaInfoWithPlist(mediaInfo, media);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddMedia);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_update_media_command() throws IOException, GeneralSecurityException {

		MobileMedia oldMediaInfo = populateMobileMedia();
		MobileMedia newMediaInfo = populateMobileMedia();
		newMediaInfo.setDisplayName("webAPIUnitTestCommand(new).PDF");
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createUpdateMediaCommand(
				oldMediaInfo, newMediaInfo,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the media data
		PropertyList newMediaPlist = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param);
		PropertyList oldMediaPlist = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OldData_Param);
		assertNotNull(newMediaPlist);
		assertNotNull(oldMediaPlist);
		
		verifyMediaInfoWithPlist(newMediaInfo, newMediaPlist);
		verifyMediaInfoWithPlist(oldMediaInfo, oldMediaPlist);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddMedia);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remove_media_command_for_file_ids() throws IOException, GeneralSecurityException {
		int[] fileIds = {1};
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveMediaCommandForFileIds(
				fileIds,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the file ids
		int[] fileIds2 = (int[]) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertTrue(Arrays.equals(fileIds, fileIds2));
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveMedia);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_assign_media_to_policy_command() throws IOException, GeneralSecurityException {
		UUID[] mediaUuids = {UUID.fromString("3372FA64-0EF0-4599-ADBB-4D3543715864")};
        UUID policyUuid = UUID.fromString("977725F5-BCE0-4D8C-BB24-7834C445E3D6");
        int assignmentType = 1;	//On-demand, Auto-remove

		CobraAdminMiscDatabaseCommand command = CommandFactory.createAssignMediaToPolicyCommand(
				mediaUuids, policyUuid, assignmentType,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
				
		// check the policy-action assignment data
		PropertyList assignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(assignments);
		UUID[] mediaUuids2 = (UUID[]) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_MediaList_Param);
		UUID policyUuid2 = (UUID) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
		long assignmentType2 = (long) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_AssignmentType_Param);
		assertTrue(Arrays.equals(mediaUuids, mediaUuids2));
		assertTrue(policyUuid.toString().compareToIgnoreCase(policyUuid2.toString()) == 0);
		assertTrue(assignmentType == assignmentType2);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddMediaToPolicy);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remove_media_from_policy_command() throws IOException, GeneralSecurityException {
		UUID[] mediaUuids = {UUID.fromString("3372FA64-0EF0-4599-ADBB-4D3543715864")};
        UUID policyUuid = UUID.fromString("977725F5-BCE0-4D8C-BB24-7834C445E3D6");

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveMediaFromPolicyCommand(
				mediaUuids, policyUuid,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		 
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
				
		// check the policy-action assignment data
		PropertyList assignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(assignments);
		UUID[] mediaUuids2 = (UUID[]) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_MediaList_Param);
		UUID policyUuid2 = (UUID) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
		assertTrue(Arrays.equals(mediaUuids, mediaUuids2));
		assertTrue(policyUuid.toString().compareToIgnoreCase(policyUuid2.toString()) == 0);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveMediaFromPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_set_availablity_time_command_for_polic_media() throws IOException, GeneralSecurityException {
		// 'Always' available
		can_create_set_availablity_time_command_for_polic_media(0);
		// with 'Start Time' and 'End Time' specified
		can_create_set_availablity_time_command_for_polic_media(1);
	}
	
	private void can_create_set_availablity_time_command_for_polic_media(int availabilitySelector) throws IOException, GeneralSecurityException {
		UUID[] mediaUuids = {UUID.fromString("3372FA64-0EF0-4599-ADBB-4D3543715864")};
        UUID policyUuid = UUID.fromString("977725F5-BCE0-4D8C-BB24-7834C445E3D6");
        String startTime = "2015-05-01T23:46:09Z";
        String endTime = "2030-05-01T23:46:09Z";

		CobraAdminMiscDatabaseCommand command = CommandFactory.createSetAvailabilityTimeForPolicyMediaCommand(
				mediaUuids, policyUuid, availabilitySelector, startTime, endTime,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
				
		// check the policy-action assignment data
		PropertyList assignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(assignments);
		UUID[] mediaUuids2 = (UUID[]) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_MediaList_Param);
		UUID policyUuid2 = (UUID) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
		long availabilitySelector2 = (long) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_Param);
		String startTime2 = (String) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityStartTime_Param);
		String endTime2 = (String) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityEndTime_Param);
		assertTrue(Arrays.equals(mediaUuids, mediaUuids2));
		assertTrue(policyUuid.toString().compareToIgnoreCase(policyUuid2.toString()) == 0);
		assertTrue(availabilitySelector == availabilitySelector2);
		// if not 'Always'
		if (availabilitySelector != 0) {
			assertTrue(startTime.compareToIgnoreCase(startTime2) == 0);
			assertTrue(endTime.compareToIgnoreCase(endTime2) == 0);
		}
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_SetAvailabilityTimeForMediaInPolicy);
	}
	
	private MobileMedia populateMobileMedia() throws IOException, NoSuchAlgorithmException
	{
		String fileUrlLocation = MediaTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();
		String sourceFile = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\java\\test\\com\\absolute\\am\\command\\webAPIUnitTestCommand.pdf";
		String iconFile = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\java\\test\\com\\absolute\\am\\command\\pdf.png";
		
		File theFile = new File(sourceFile);
		String fileModifiedDate = StringUtilities.toISO8601W3CString(theFile.lastModified());
		long fileSize = theFile.length();
		
		MobileMedia mediaInfo = new MobileMedia();
		mediaInfo.setSeed(1);
		mediaInfo.setUniqueId(UUID.randomUUID().toString());
		mediaInfo.setFilename(sourceFile.substring(1));
		mediaInfo.setDescription("This is a test text file.");
		mediaInfo.setFileType("PDF");
		mediaInfo.setFileModDate(fileModifiedDate);
		mediaInfo.setFileSize(fileSize);
		mediaInfo.setDisplayName("webAPIUnitTestCommand.PDF");
		mediaInfo.setIcon(FileUtilities.loadFile(iconFile));
		mediaInfo.setFileMD5(StringUtilities.toHexString(FileUtilities.hashFile(sourceFile, "MD5")));
		mediaInfo.setEncryptionKey(StringUtilities.generateRandomString(StringUtilities.DEFAULT_RANDOM_PASSWORD_CHARSET, 16));
		mediaInfo.setCategory("Documents");
		mediaInfo.setPassPhraseHash("");
		mediaInfo.setCanEmail(true);
		mediaInfo.setCanPrint(false);
		mediaInfo.setCanLeaveApp(true);
		mediaInfo.setTransferOnWifiOnly(false);
		mediaInfo.setPassPhraseHash(UUID.randomUUID().toString());
		
		return mediaInfo;
	}
	
	private void verifyMediaInfoWithPlist(MobileMedia mediaInfo, PropertyList mediaPlist){
		long id2 = (long) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaID_Param);
		String uniqueId2 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaUniqueID_Param);
		int seed2 = (int) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaSeed_Param);
		String displayName2 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaDisplayName_Param);
		String category2 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaCategory_Param);
		String fileName2 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaFilename_Param);
		String fileModDate2 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaFileModDate_Param);
		long fileSize2 = (long) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaFileSize_Param);
		String fileType2 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaFileType_Param);
		String fileMD52 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaFileMD5_Param);
		String description2 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaDescription_Param);
		boolean canLeaveApp2 = (boolean) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaCanLeaveApp_Param);
		boolean canEmail2 = (boolean) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaCanEmail_Param);
		boolean canPrint2 = (boolean) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaCanPrint_Param);
		String encryptionKey2 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaEncryptionKey_Param);
		byte[] iconAsByte2 = (byte[]) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaIcon_Param);
		boolean transferOnWifiOnly2 = (boolean) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaTransferOnWifiOnly_Param);
		String passPhraseHash2 = (String) mediaPlist.get(iOSDevicesDefines.kCobra_MobileMediaPassphraseHash_Param);
		
		assertTrue(mediaInfo.getId() == id2);
		assertTrue(mediaInfo.getUniqueId().compareToIgnoreCase(uniqueId2) == 0);
		assertTrue(mediaInfo.getSeed() == seed2);
		assertTrue(mediaInfo.getDisplayName().compareToIgnoreCase(displayName2) == 0);
		assertTrue(mediaInfo.getCategory().compareToIgnoreCase(category2) == 0);
		assertTrue(mediaInfo.getFilename().compareToIgnoreCase(fileName2) == 0);
		assertTrue(mediaInfo.getFileModDate().compareToIgnoreCase(fileModDate2) == 0);
		assertTrue(mediaInfo.getFileSize() == fileSize2);
		assertTrue(mediaInfo.getFileType().compareToIgnoreCase(fileType2) == 0);
		assertTrue(mediaInfo.getFileMD5().compareToIgnoreCase(fileMD52) == 0);
		assertTrue(mediaInfo.getDescription().compareToIgnoreCase(description2) == 0);
		assertTrue(mediaInfo.getCanLeaveApp() == canLeaveApp2);
		assertTrue(mediaInfo.getCanEmail() == canEmail2);
		assertTrue(mediaInfo.getCanPrint() == canPrint2);
		assertTrue(mediaInfo.getEncryptionKey().compareToIgnoreCase(encryptionKey2) == 0);
		assertTrue(Arrays.equals(mediaInfo.getIcon(), iconAsByte2));
		assertTrue(mediaInfo.getTransferOnWifiOnly() == transferOnWifiOnly2);
		assertTrue(mediaInfo.getPassPhraseHash().compareToIgnoreCase(passPhraseHash2) == 0);
	}
	// =============== End of Media Tests ===============

	
	// =============== Policy Tests (Including standard policy and smart policy) ===============
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_new_standard_policy_command() throws IOException, GeneralSecurityException {
		UUID policyUuid = UUID.randomUUID();
		int seed = 1;
		String policyName = "unit_test_policy_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

		CobraAdminMiscDatabaseCommand command = CommandFactory.createStandardPolicyCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				policyName, seed, policyUuid);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the policy data
		PropertyList policy = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param);
		assertNotNull(policy);
		UUID policyUuid2 = (UUID) policy.get(iOSDevicesDefines.kCobra_iOS_PolicyID_Param);
		String policyName2 = (String) policy.get(iOSDevicesDefines.kCobra_iOS_PolicyName_Param);
		long seed2 = (long) policy.get(iOSDevicesDefines.kCobra_iOS_PolicySeed_Param);

		assertTrue(policyUuid.compareTo(policyUuid2) == 0);
		assertTrue(policyName.compareToIgnoreCase(policyName2) == 0);
		assertTrue(seed == seed2);

		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_new_smart_policy_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		UUID policyUuid = UUID.randomUUID();
		int seed = 1;
		String policyName = "unit_test_policy_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		int schemaVersion = 1;
		int filterType = 1; //
		PropertyList filterCriteria = populateFilterCriteriaFromXmlString();

		CobraAdminMiscDatabaseCommand command = CommandFactory.createSmartPolicyCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				policyName, seed, schemaVersion, filterType, filterCriteria, policyUuid);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the policy data
		PropertyList policy = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param);
		assertNotNull(policy);
		verifyPolicyPlist(policy, policyUuid, policyName, seed, filterType, filterCriteria);
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_update_smart_policy_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		UUID policyUuid = UUID.randomUUID();
		int newSeed = 2;
		String newPolicyName = "unit_test_policy_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + "_new";
		int newSchemaVersion = 2;
		int newFilterType = 2; 
		PropertyList newFilterCriteria = populateFilterCriteriaFromXmlString();
		
		int oldSeed = 1;
		String oldPolicyName = "unit_test_policy_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		int oldSchemaVersion = 1;
		int oldFilterType = 1; //
		PropertyList oldFilterCriteria = populateFilterCriteriaFromXmlString();
		

		CobraAdminMiscDatabaseCommand command = CommandFactory.updateSmartPolicyCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				newFilterCriteria, newFilterType, newPolicyName, newSchemaVersion, newSeed,
				oldFilterCriteria, oldFilterType, oldPolicyName, oldSchemaVersion, oldSeed, 
				policyUuid);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the policy data
		PropertyList newPolicy = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param);
		PropertyList oldPolicy = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OldData_Param);
		assertNotNull(newPolicy);
		assertNotNull(oldPolicy);
		verifyPolicyPlist(newPolicy, policyUuid, newPolicyName, newSeed, newFilterType, newFilterCriteria);
		verifyPolicyPlist(oldPolicy, policyUuid, oldPolicyName, oldSeed, oldFilterType, oldFilterCriteria);

		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_delete_smart_policy_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		UUID[] policyUuids = { UUID.randomUUID() };

		CobraAdminMiscDatabaseCommand command = CommandFactory.createDeletePoliciesCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				policyUuids);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		 
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the policy data
		PropertyList policyData = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		UUID[] policUuids2 = (UUID[]) policyData.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyIDs_Param);
		assertNotNull(policUuids2);
		assertTrue(Arrays.equals(policyUuids, policUuids2));

		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemovePolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_rename_smart_policy_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		UUID policyUuid = UUID.randomUUID();
		int newSeed = 2;
		String newPolicyName = "unit_test_policy_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()) + "_new";
		
		int oldSeed = 1;
		String oldPolicyName = "unit_test_policy_" + new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());

		CobraAdminMiscDatabaseCommand command = CommandFactory.renameStandardPolicyCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				oldPolicyName, oldSeed, policyUuid,
				newPolicyName, newSeed, policyUuid);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the policy data
		PropertyList newPolicy = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_NewData_Param);
		PropertyList oldPolicy = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OldData_Param);
		assertNotNull(newPolicy);
		assertNotNull(oldPolicy);
		verifyPolicyPlist(newPolicy, policyUuid, newPolicyName, newSeed, -1, null);
		verifyPolicyPlist(oldPolicy, policyUuid, oldPolicyName, oldSeed, -1, null);

		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_assign_device_to_policy_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		UUID[] mobileDeviceUuids = { UUID.randomUUID() };
		UUID policyUuid = UUID.randomUUID();

		CobraAdminMiscDatabaseCommand command = CommandFactory.createAssignDevicesToPolicyCommand(
				mobileDeviceUuids,
				policyUuid,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the assignment data
		PropertyList assignmentData = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(assignmentData);
		UUID policyUuid2 = (UUID) assignmentData.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
		UUID[] mobileDeviceUuids2 = (UUID[]) assignmentData.get(iOSDevicesDefines.kCobra_iOS_PolicyDeviceList_Param);
		assertTrue(Arrays.equals(mobileDeviceUuids, mobileDeviceUuids2));
		assertTrue(policyUuid.equals(policyUuid2));

		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddDevicesToPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remove_device_from_policy_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		UUID[] mobileDeviceUuids = { UUID.randomUUID() };
		UUID policyUuid = UUID.randomUUID();

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveDevicesFromPolicyCommand(
				mobileDeviceUuids,
				policyUuid,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));

		// check the assignment data
		PropertyList assignmentData = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(assignmentData);
		UUID policyUuid2 = (UUID) assignmentData.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
		UUID[] mobileDeviceUuids2 = (UUID[]) assignmentData.get(iOSDevicesDefines.kCobra_iOS_PolicyDeviceList_Param);
		assertTrue(Arrays.equals(mobileDeviceUuids, mobileDeviceUuids2));
		assertTrue(policyUuid.equals(policyUuid2));

		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveDevicesFromPolicy);
	}
	
	private PropertyList populateFilterCriteriaFromXmlString() 
			throws UnsupportedEncodingException, ParserConfigurationException, SAXException, IOException {
		String xmlString = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + 
					 "<!DOCTYPE plist PUBLIC \"-//Apple Computer//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">" +
					 "<plist version=\"1.0\">" +
					 "<dict>" +
					 "	<key>CompareValue</key>" +
					 "	<array>" +
					 "		<dict>" +
					 "			<key>CompareValue</key>" +
					 "			<array>" +
					 "				<dict>" +
					 "					<key>CompareValue</key>" +
					 "					<string>Toshiba Tablet</string>" +
					 "					<key>InfoItemID</key>" +
					 "					<string>FE5A9F56-228C-4BDA-99EC-8666292CB5C1</string>" +
					 "					<key>Operator</key>" +
					 "					<string>==</string>" +
					 "					<key>UseNativeType</key>" +
					 "					<false/>" +
					 "				</dict>" +
					 "			</array>" +
					 "			<key>CriteriaFieldType</key>" +
					 "			<integer>0</integer>" +
					 "			<key>Operator</key>" +
					 "			<string>OR</string>" +
					 "		</dict>" +
					 "		<dict>" +
					 "			<key>CompareValue</key>" +
					 "			<true/>" +
					 "			<key>InfoItemID</key>" +
					 "			<string>26B03C68-0BF5-41ED-AD06-85903D5FBDFE</string>" +
					 "			<key>Operator</key>" +
					 "			<string>==</string>" +
					 "			<key>UseNativeType</key>" +
					 "			<true/>" +
					 "		</dict>" +
					 "	</array>" +
					 "	<key>Operator</key>" +
					 "	<string>AND</string>" +
					 "	<key>SmartPolicyUserEditableFilter</key>" +
					 "	<dict>" +
					 "		<key>CompareValue</key>" +
					 "		<array>" +
					 "			<dict>" +
					 "				<key>CompareValue</key>" +
					 "				<string>Toshiba Tablet</string>" +
					 "				<key>InfoItemID</key>" +
					 "				<string>FE5A9F56-228C-4BDA-99EC-8666292CB5C1</string>" +
					 "				<key>Operator</key>" +
					 "				<string>==</string>" +
					 "				<key>UseNativeType</key>" +
					 "				<false/>" +
					 "			</dict>" +
					 "		</array>" +
					 "		<key>CriteriaFieldType</key>" +
					 "		<integer>0</integer>" +
					 "		<key>Operator</key>" +
					 "		<string>OR</string>" +
					 "	</dict>" +
					 "</dict>" +
					 "</plist>";
		
		return PropertyList.fromString(xmlString);
	}
	
	private void verifyPolicyPlist(PropertyList policyPlist, UUID policyUuid, String policyName, long seed, long filterType, PropertyList filterCriteria) {
		// check the policy data
		UUID policyUuid2 = (UUID) policyPlist.get(iOSDevicesDefines.kCobra_iOS_PolicyID_Param);
		String policyName2 = (String) policyPlist.get(iOSDevicesDefines.kCobra_iOS_PolicyName_Param);
		long seed2 = (long) policyPlist.get(iOSDevicesDefines.kCobra_iOS_PolicySeed_Param);
		
		assertTrue(policyUuid.compareTo(policyUuid2) == 0);
		assertTrue(policyName.compareToIgnoreCase(policyName2) == 0);
		assertTrue(seed == seed2);
		if (filterType >= 0) {
			long filterType2 = (long) policyPlist.get(iOSDevicesDefines.kCobra_iOS_PolicyFilterType_Param);
			assertTrue(filterType == filterType2);
		}
		if (filterCriteria != null) {
			PropertyList filterCriteria2 = (PropertyList) policyPlist.get(iOSDevicesDefines.kCobra_iOS_PolicyFilterCriteria_Param);
			assertTrue(filterCriteria.toXMLString().compareTo(filterCriteria2.toXMLString()) == 0);
		}
	}
	
	// =============== End of Policy Tests ===============
	
	
	// =============== Configuration Profile Tests ===============
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_install_configuration_profile_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		long[] deviceIds = { 1 };
		ConfigurationProfile configurationProfileDetails = new ConfigurationProfile();
		configurationProfileDetails.setPayloadName("TestConfigProfile1");
		configurationProfileDetails.setPayloadDescription("Test Configuration Profile");
		configurationProfileDetails.setConfigurationType(1);
		configurationProfileDetails.setPayloadIdentifier("test.identifier." + UUID.randomUUID().toString());
		configurationProfileDetails.setPayloadOrganization("Test Organization");
		configurationProfileDetails.setPayloadRemovalOptions(3);
		configurationProfileDetails.setPlatformType(11);	/* Android */
		configurationProfileDetails.setSeed(1);

		CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallConfigurationProfileCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				deviceIds, 
				configurationProfileDetails);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		long[] deviceIds2 = (long[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(deviceIds,  deviceIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_InstallProfileFromRepository);
		
		// check the assignment data
		PropertyList requesttData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		assertNotNull(requesttData);
		long configurationProfileId2 = (long) requesttData.get(iOSDevicesDefines.kCobra_iOS_ConfigurationProfileID_Param);
		int ConfigurationType2 = (int) requesttData.get(iOSDevicesDefines.kCobra_iOS_ConfigurationProfileConfigurationType_Param);
		int seed2 = (int) requesttData.get(iOSDevicesDefines.kCobra_iOS_ConfigurationProfileSeed_Param);
		String payloadDescription2 = (String) requesttData.get(iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadDescription_Param);
		String payloadIdentifier2 = (String) requesttData.get(iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadIdentifier_Param);
		String payloadName2 = (String) requesttData.get(iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadName_Param);
		String payloadOrganization2 = (String) requesttData.get(iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadOrganization_Param);
		int payloadRemovalOptions2 = (int) requesttData.get(iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePayloadRemovalOptions_Param);
		int platformType2 = (int) requesttData.get(iOSDevicesDefines.kCobra_iOS_ConfigurationProfilePlatformType_Param);
		assertTrue(configurationProfileId2 == configurationProfileDetails.getId());
		assertTrue(ConfigurationType2 == configurationProfileDetails.getConfigurationType());
		assertTrue(seed2 == configurationProfileDetails.getSeed());
		assertTrue(payloadDescription2.compareToIgnoreCase(configurationProfileDetails.getPayloadDescription()) == 0);
		assertTrue(payloadIdentifier2.compareToIgnoreCase(configurationProfileDetails.getPayloadIdentifier()) == 0);
		assertTrue(payloadName2.compareToIgnoreCase(configurationProfileDetails.getPayloadName()) == 0);
		assertTrue(payloadOrganization2.compareToIgnoreCase(configurationProfileDetails.getPayloadOrganization()) == 0);
		assertTrue(payloadRemovalOptions2 == configurationProfileDetails.getPayloadRemovalOptions());
		assertTrue(platformType2 == configurationProfileDetails.getPlatformType());
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remove_configuration_profile_from_device_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		long[] configurationProfileAssociationIds = { 1 };

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveConfigurationProfileFromDeviceCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				configurationProfileAssociationIds);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		long[] configurationProfileAssociationIds2 = (long[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(configurationProfileAssociationIds,  configurationProfileAssociationIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoveProfile);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_assign_configuration_profile_to_policy_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		UUID[] configurationProfileUUIDs = { UUID.randomUUID() };
		UUID policyUuid = UUID.randomUUID();
		int assignmentType = 1;

		CobraAdminMiscDatabaseCommand command = CommandFactory.createAssignConfigurationProfileToPolicyCommand(
				configurationProfileUUIDs,
				policyUuid,
				assignmentType,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the assignment data
		PropertyList assignmentData = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(assignmentData);
		UUID[] configurationProfileUUIDs2 = (UUID[]) assignmentData.get(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfileList_Param);
		UUID policyUuid2 = (UUID) assignmentData.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
		long assignmentType2 = (long) assignmentData.get(iOSDevicesDefines.kCobra_iOS_Policy_AssignmentType_Param);
		assertTrue(Arrays.equals(configurationProfileUUIDs, configurationProfileUUIDs2));
		assertTrue(policyUuid.equals(policyUuid2));
		assertTrue(assignmentType == assignmentType2);

		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_AddConfigProfilesToPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remove_configuration_profile_from_policy_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		UUID[] configurationProfileUUIDs = { UUID.randomUUID() };
		UUID policyUuid = UUID.randomUUID();

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveConfigurationProfileFromPolicyCommand(
				configurationProfileUUIDs,
				policyUuid,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the assignment data
		PropertyList assignmentData = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(assignmentData);
		UUID[] configurationProfileUUIDs2 = (UUID[]) assignmentData.get(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfileList_Param);
		UUID policyUuid2 = (UUID) assignmentData.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
		assertTrue(Arrays.equals(configurationProfileUUIDs, configurationProfileUUIDs2));
		assertTrue(policyUuid.equals(policyUuid2));

		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_RemoveConfigProfilesFromPolicy);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_set_availablity_time_command_for_polic_configuration_profile() throws IOException, GeneralSecurityException {
		// 'Always' available
		can_create_set_availablity_time_command_for_polic_configuration_profile(0);
		// with 'Start Time' and 'End Time' specified
		can_create_set_availablity_time_command_for_polic_configuration_profile(1);
	}

	private void can_create_set_availablity_time_command_for_polic_configuration_profile(int availabilitySelector) throws IOException, GeneralSecurityException {
		UUID[] configProfileUuids = {UUID.fromString("3372FA64-0EF0-4599-ADBB-4D3543715864")};
        UUID policyUuid = UUID.fromString("977725F5-BCE0-4D8C-BB24-7834C445E3D6");
        String startTime = "2015-05-01T23:46:09Z";
        String endTime = "2030-05-01T23:46:09Z";

		CobraAdminMiscDatabaseCommand command = CommandFactory.createSetAvailabilityTimeForPolicyConfigProfileCommand(
				configProfileUuids, policyUuid, availabilitySelector, startTime, endTime,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		assertTrue(param.containsKey(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param));
		
		// check the policy-action assignment data
		PropertyList assignments = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_Data_Param);
		assertNotNull(assignments);
		UUID[] configProfileUuids2 = (UUID[]) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_ConfigProfileList_Param);
		UUID policyUuid2 = (UUID) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_PolicyID_Param);
		long availabilitySelector2 = (long) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilitySelector_Param);
		assertTrue(Arrays.equals(configProfileUuids, configProfileUuids2));
		assertTrue(policyUuid.toString().compareToIgnoreCase(policyUuid2.toString()) == 0);
		assertTrue(availabilitySelector == availabilitySelector2);
		
		if (availabilitySelector == 0) {
			assertFalse(assignments.containsKey(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityStartTime_Param));
			assertFalse(assignments.containsKey(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityEndTime_Param));
		} else {
			String startTime2 = (String) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityStartTime_Param);
			String endTime2 = (String) assignments.get(iOSDevicesDefines.kCobra_iOS_Policy_AvailabilityEndTime_Param);
			assertTrue(startTime.compareToIgnoreCase(startTime2) == 0);
			assertTrue(endTime.compareToIgnoreCase(endTime2) == 0);	
		}
		
		// check the Operation Type 
		long operationType = (long) param.get(iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_Param);
		assertTrue(operationType == iOSDevicesDefines.kCobra_iOS_DatabaseOperation_OperationType_SetAvailabilityTimeForConfProfilesInPolicy);
	}
	// =============== End of Configuration Profile Tests ===============
	
	
	// =============== Provisioning Profile Tests ===============
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_install_provisioning_profile_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		long[] deviceIds = { 1 };
		ProvisioningProfile provisioningProfileDetails = new ProvisioningProfile();
		provisioningProfileDetails.setProfileUUID(UUID.randomUUID().toString());
		provisioningProfileDetails.setProfileName("unit_test_provisioning_profile");
		provisioningProfileDetails.setProfileExpiryDate( "2015-05-01T23:46:09Z");
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallProvisioningProfileCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				deviceIds, 
				provisioningProfileDetails);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		long[] deviceIds2 = (long[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(deviceIds,  deviceIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_InstallProvisioningProfileFromRepository);
		
		// check the assignment data
		PropertyList requesttData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		assertNotNull(requesttData);
		String profileUuid2 = (String) requesttData.get(iOSDevicesDefines.kCobra_iOS_ProvisioningProfileUUID_Param);
		String profileName2 = (String) requesttData.get(iOSDevicesDefines.kCobra_iOS_ProvisioningProfileName_Param);
		String profileExpiryDate = (String) requesttData.get(iOSDevicesDefines.kCobra_iOS_ProvisioningProfileExpiryDate_Param);
		assertTrue(profileUuid2.compareToIgnoreCase(provisioningProfileDetails.getProfileUUID()) == 0);
		assertTrue(profileName2.compareToIgnoreCase(provisioningProfileDetails.getProfileName()) == 0);
		assertTrue(profileExpiryDate.compareToIgnoreCase(provisioningProfileDetails.getProfileExpiryDate()) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remove_provisioning_profile_from_device_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		long[] provisioningProfileAssociationIds = { 1 };

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveProvisioningProfileFromDeviceCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				provisioningProfileAssociationIds);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		long[] provisioningProfileAssociationIds2 = (long[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(provisioningProfileAssociationIds,  provisioningProfileAssociationIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoveProvisioningProfile);
	}
	
	// =============== End of Provisioning Profile Tests ===============
	
	
	// =============== Device-Related Tests ===============
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_lock_devices_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		int[] deviceIds = { 1 };
		String passcode = "unit_test_passcode";
		String codedPasscode = (new CPLATPassword(passcode)).Encrypt(CobraCommandDefs.kBlowfishMDMDeviceLockPasswordKey.getBytes("UTF-8"));

		CobraAdminMiscDatabaseCommand command = CommandFactory.createLockDevicesCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				deviceIds,
				passcode);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		int[] deviceIds2 = (int[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(deviceIds, deviceIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_DeviceLock);
		
		// verify the request data
		PropertyList requestData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		int[] deviceIds3 = (int[]) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_AndroidRecordIDList_Param);
		String passcode2 = (String) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param);
		assertTrue(Arrays.equals(deviceIds, deviceIds3));
		assertTrue(codedPasscode.compareToIgnoreCase(passcode2) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_set_activation_lock_options_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		int[] deviceIds = { 1 };
		int activationLock = 1;

		CobraAdminMiscDatabaseCommand command = CommandFactory.createSetActivationLockOptionsCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				deviceIds,
				activationLock);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		int[] deviceIds2 = (int[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(deviceIds, deviceIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_SetActivationLockOptions);
		
		// verify the request data
		PropertyList requestData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		int[] deviceIds3 = (int[]) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_AndroidRecordIDList_Param);
		long activationLock2 = (long) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_ChangeActivationLock_Param);
		assertTrue(Arrays.equals(deviceIds, deviceIds3));
		assertTrue(activationLock == activationLock2);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_send_message_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		int[] deviceIds = { 1 };
		String message = "unit test message";

		CobraAdminMiscDatabaseCommand command = CommandFactory.createSendMessageCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				message,
				deviceIds);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		int[] deviceIds2 = (int[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(deviceIds, deviceIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_ShowMessage);
		
		// verify the request data
		PropertyList requestData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		String message2 = (String) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_MessageText_Param);
		assertTrue(requestData.containsKey(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Timestamp_Param));
		assertTrue(message.compareToIgnoreCase(message2) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_clear_passcode_command() 
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		int[] androidDeviceIds = { 1 };
		int[] iosDeviceIds = { 2 };
		String newPasscode = "unit_test_new_passcode";
		String codedNewPasscode = (new CPLATPassword(newPasscode)).Encrypt(CobraCommandDefs.kBlowfishMDMDeviceLockPasswordKey.getBytes("UTF-8"));
		
		// concatenate andriodDevceiIds and iosDevices 
		int[] allDeviceIds = new int[androidDeviceIds.length + iosDeviceIds.length];
		System.arraycopy(androidDeviceIds, 0, allDeviceIds, 0, androidDeviceIds.length);
		System.arraycopy(iosDeviceIds, 0, allDeviceIds, androidDeviceIds.length, iosDeviceIds.length);
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createClearPasscodeCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				androidDeviceIds, 
				iosDeviceIds,
				newPasscode);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		int[] allDeviceIds2 = (int[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(allDeviceIds, allDeviceIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_ClearPasscode);
		
		// verify the request data
		PropertyList requestData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		int[] androidDeviceIds2 = (int[]) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_AndroidRecordIDList_Param);
		int[] iosDeviceIds2 = (int[]) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_iOSRecordIDList_Param);
		String newPasscode2 = (String) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_NewLockPassword_Param);
		
		assertTrue(Arrays.equals(androidDeviceIds, androidDeviceIds2));
		assertTrue(Arrays.equals(iosDeviceIds, iosDeviceIds2));
		assertTrue(codedNewPasscode.compareToIgnoreCase(newPasscode2) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_remote_erase_command() 
			throws IOException, GeneralSecurityException {
		int[] androidDeviceIds = { 1 };
		int[] iosDeviceIds = { 2 };
		boolean eraseSDCard = true;
		
		// concatenate andriodDevceiIds and iosDevices 
		int[] allDeviceIds = new int[androidDeviceIds.length + iosDeviceIds.length];
		System.arraycopy(androidDeviceIds, 0, allDeviceIds, 0, androidDeviceIds.length);
		System.arraycopy(iosDeviceIds, 0, allDeviceIds, androidDeviceIds.length, iosDeviceIds.length);
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoteEraseCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				androidDeviceIds, 
				iosDeviceIds,
				eraseSDCard);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		int[] allDeviceIds2 = (int[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(allDeviceIds, allDeviceIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_RemoteErase);
		
		// verify the request data
		PropertyList requestData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		int[] androidDeviceIds2 = (int[]) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_AndroidRecordIDList_Param);
		int[] iosDeviceIds2 = (int[]) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_iOSRecordIDList_Param);
		boolean eraseSDCard2 = (boolean) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_EraseSDCard_Param);
		
		assertTrue(Arrays.equals(androidDeviceIds, androidDeviceIds2));
		assertTrue(Arrays.equals(iosDeviceIds, iosDeviceIds2));
		assertTrue(eraseSDCard == eraseSDCard2);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_update_device_info_command() 
			throws IOException, GeneralSecurityException {
		int[] deviceIds = { 1, 2 };
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createUpdateDeviceInfoCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				deviceIds);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));

		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		int[] deviceIds2 = (int[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(deviceIds, deviceIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_UpdateDeviceInfo);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_set_roaming_options_command() 
			throws Exception {
		int[] deviceIds = { 1, 2 };
		Boolean voiceRoaming = true;
		Boolean dataRoaming = true;
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createSetRoamingOptionsCommand(
				LOGIN_RETURNED_ADMIN_UUID,
				deviceIds,
				voiceRoaming, dataRoaming);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		int[] deviceIds2 = (int[]) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RecordIDList_Param);
		long commandId2 = (long) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_CommandID_Param);
		assertTrue(Arrays.equals(deviceIds, deviceIds2));
		assertTrue(commandId2 == CobraCommandDefs.kCobra_Admin_SendMDMCommand_ChangeRoamingOptions);
		
		// verify the request data
		PropertyList requestData = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_Param);
		Boolean voiceRoaming2 = (Boolean) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_VoiceRoaming_Param);
		Boolean dataRoaming2 = (Boolean) requestData.get(CobraCommandDefs.kCobra_Admin_SendMDMCommand_RequestData_DataRoaming_Param);
		
		assertTrue(voiceRoaming == voiceRoaming2);
		assertTrue(dataRoaming == dataRoaming2);
	}
	
	// =============== End of  Device-Related Tests ===============
	
	
	// =============== Custom Field Tests ===============
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_custome_field_command() 
			throws Exception {
		com.absolute.am.model.CustomField newCustomField = populateCustomField(
				"unit_test_for_boolean: [" + UUID.randomUUID().toString() + "]",
				"description - unit_test_for_boolean: [" + UUID.randomUUID().toString() + "]",
				"unit_test_for_boolean_var" );
		
		ArrayList<com.absolute.am.dal.model.CustomField> customFieldList = populateCustomFieldList();
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createCustomFieldCommand(
				newCustomField,
				customFieldList,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		PropertyList dynamicFields = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_CustomField_DynamicFields);
		PropertyList fieldActions = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_FieldActions_Param);
		
		// dynamicFields
		// the first one should be new field 
		PropertyList newCustomFieldPlist = (PropertyList) dynamicFields.get(newCustomField.uniqueId);
		assertNotNull(newCustomFieldPlist);
		veryfyDynamicCustomField(newCustomFieldPlist, newCustomField);
		// and then the followed existing custom fields 
		for (com.absolute.am.dal.model.CustomField field : customFieldList) {
			PropertyList newCustomFieldPlist2 = (PropertyList) dynamicFields.get(field.id);
			assertNotNull(newCustomFieldPlist2);
			veryfyDynamicCustomField(newCustomFieldPlist2, field);
		}
		
		// FieldActions [customFieldActionDefinition]
		for (String uuid : newCustomField.customFieldActionDefinitionIds) {
			PropertyList fieldActionPlist = (PropertyList) fieldActions.get(uuid);
			assertNotNull(fieldActionPlist);
			veryfyFieldAction(fieldActionPlist, newCustomField);
		}
		
		for (com.absolute.am.dal.model.CustomField field : customFieldList) {
			for (CustomFieldActionDefinition def : field.customFieldActionDefinitions) {
				PropertyList fieldActionPlist = (PropertyList) fieldActions.get(def.id);
				assertNotNull(fieldActionPlist);
				veryfyFieldAction(fieldActionPlist, def);
			}
		}
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_update_custome_field_command() 
			throws Exception {
		ArrayList<com.absolute.am.dal.model.CustomField> customFieldList = populateCustomFieldList();
		
		com.absolute.am.dal.model.CustomField firstCustomField = customFieldList.get(0);
		
		String customFieldUuid = firstCustomField.id;
		com.absolute.am.model.CustomField updatedCustomField = populateCustomField(
				firstCustomField.name + " - update",
				firstCustomField.description + " - update",
				firstCustomField.variableName + "_update"
				);
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.updateCustomFieldCommand(
				customFieldUuid,
				updatedCustomField,
				customFieldList,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
		
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		PropertyList dynamicFields = (PropertyList) param.get(CobraCommandDefs.kCobra_Admin_CustomField_DynamicFields);
		PropertyList fieldActions = (PropertyList) param.get(iOSDevicesDefines.kCobra_iOS_FieldActions_Param);
		
		// dynamicFields
		// and then the followed existing custom fields 
		for (com.absolute.am.dal.model.CustomField field : customFieldList) {
			PropertyList customFieldPlist2 = (PropertyList) dynamicFields.get(field.id);
			assertNotNull(customFieldPlist2);
			if (field.id.compareToIgnoreCase(customFieldUuid) == 0)
			{
				String name = (String) customFieldPlist2.get(CobraCommandDefs.kCobra_Admin_CustomField_Name);
				String variableName = (String) customFieldPlist2.get(CobraCommandDefs.kCobra_Admin_CustomField_VariableName);
				String description = (String) customFieldPlist2.get(CobraCommandDefs.kCobra_Admin_CustomField_Description);
				assertTrue(name.compareToIgnoreCase(updatedCustomField.name) == 0);
				assertTrue(description.compareToIgnoreCase(updatedCustomField.description) == 0);
				assertTrue(variableName.compareToIgnoreCase(updatedCustomField.variableName) == 0);
			} else {
				veryfyDynamicCustomField(customFieldPlist2, field);
			}
		}
		
		for (com.absolute.am.dal.model.CustomField field : customFieldList) {
			for (CustomFieldActionDefinition def : field.customFieldActionDefinitions) {
				PropertyList fieldActionPlist = (PropertyList) fieldActions.get(def.id);
				assertNotNull(fieldActionPlist);
				veryfyFieldAction(fieldActionPlist, def);
			}
		}
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_create_modify_custome_field_from_device_command() 
			throws Exception {
		// with deleting item
		can_create_modify_custome_field_from_device_command(true);
		// without deleting item
		can_create_modify_custome_field_from_device_command(false);
	}
	
	private com.absolute.am.model.CustomField populateCustomField(String name, String description, String variableName)
	{
		com.absolute.am.model.CustomField cf = new com.absolute.am.model.CustomField();
		
		cf.name = name;
		cf.description = description;
		cf.variableName = variableName;
		cf.dataType = 3;	// Boolean
		cf.displayType = 1;
		cf.enumerationList = null;
		
		return cf;
	}
	
	private ArrayList<com.absolute.am.dal.model.CustomField> populateCustomFieldList()
	{
		ArrayList<com.absolute.am.dal.model.CustomField> list = new ArrayList<com.absolute.am.dal.model.CustomField>();
		
		CustomField cf1 = new CustomField();
		cf1.id = UUID.randomUUID().toString();
		cf1.name = "unit_test_cf_001";
		cf1.seed = 2;
		cf1.dataType = 1;	// String
		cf1.displayType = 1;
		cf1.evaluationMethod = 2;
		cf1.autoAssignToAllMachines = 0;
		cf1.description = "description - " + cf1.name;
		cf1.enumerationList = null;
		cf1.deviceType = 2;
		cf1.variableName = "unit_test_cf_001_var";
		cf1.customFieldActionDefinitions.add(populateCustomFieldActionDefinition(UUID.randomUUID().toString(), cf1.name, 1, 1));
		cf1.customFieldActionDefinitions.add(populateCustomFieldActionDefinition(UUID.randomUUID().toString(), cf1.name, 1, 2));
		list.add(cf1);
		
		CustomField cf2 = new CustomField();
		cf2.id = UUID.randomUUID().toString();
		cf2.name = "unit_test_cf_002";
		cf2.seed = 3;
		cf2.dataType = 2;	// NUmber
		cf2.displayType = 1;
		cf2.evaluationMethod = 2;
		cf2.autoAssignToAllMachines = 0;
		cf2.description = "description - " + cf1.name;
		cf2.enumerationList = null;
		cf2.deviceType = 2;
		cf2.variableName = "unit_test_cf_002_var";
		cf2.customFieldActionDefinitions.add(populateCustomFieldActionDefinition(UUID.randomUUID().toString(), cf2.name, 1, 1));
		cf2.customFieldActionDefinitions.add(populateCustomFieldActionDefinition(UUID.randomUUID().toString(), cf2.name, 1, 2));
		list.add(cf2);
		
		return list;
	}
	
	private CustomFieldActionDefinition populateCustomFieldActionDefinition(String id, String name, int seed, int platform)
	{
		CustomFieldActionDefinition def = new CustomFieldActionDefinition();
		def.id  = id;
		def.executableOptions = "";
		def.executablePartialPath = "";
		def.executableTypeSelector = 0;
		def.executeOnlyWithFullInventory = 0;
		def.name = name;
		def.pListDomain = "";
		def.pListKey = "";
		def.pListLocationSelector = 1;
		def.platform = platform;
		def.registryPath = "";
		def.replaceLineFeeds = 1;
		def.requiresAdminPrivileges = 0;
		def.returnExecutionErrors = 0;
		def.scriptText = "";
		def.seed = seed;
		def.sourceFile = "";
		def.sourceFileChecksum = "";
		def.sourceTypeSelector = 0;
		def.transferExecutableFolder = 0;
		def.userContext = "";
		def.userContextPassword = "";
		def.userContextSelector = 1;
		
		return def;
	}
	
	private void veryfyDynamicCustomField(PropertyList dynamicField, com.absolute.am.model.CustomField customField) {
		String[] customFIeldActionDefinitionIds = (String[]) dynamicField.get(iOSDevicesDefines.kCobra_iOS_ActionList_Param);
		assertTrue(customFIeldActionDefinitionIds.length >= 2);
		long autoAssignToAllMachines = (long) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_AutoAssignToAllMachines);
		int dataType = (int) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_DataType);
		String description = (String) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_Description);
		long deviceType = (long) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_DeviceType);
		int displayType = (int) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_DisplayType);
		long evaluationMethod = (long) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_EvaluationMethod);
		String customFieldUuid = (String) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_FieldID);
		String name = (String) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_Name);
		long seed = (long) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_Seed);
		String variableName = (String) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_VariableName);
		
		assertTrue(autoAssignToAllMachines == customField.defaultAutoAssignToAllMachines);
		assertTrue(dataType == customField.dataType);
		assertTrue(description.compareToIgnoreCase(customField.description) == 0);
		assertTrue(deviceType == customField.defaultDeviceType);
		assertTrue(displayType == customField.displayType);
		assertTrue(evaluationMethod == customField.defaultEvaluationMethod);
		assertTrue(customFieldUuid.compareToIgnoreCase(customField.uniqueId) == 0);
		assertTrue(name.compareToIgnoreCase(customField.name) == 0);
		assertTrue(seed == customField.defaultSeed);
		assertTrue(variableName.compareToIgnoreCase(customField.variableName) == 0);
	}
	
	private void veryfyDynamicCustomField(PropertyList dynamicField, com.absolute.am.dal.model.CustomField customField) {
		Object[] customFIeldActionDefinitionIds = (Object[]) dynamicField.get(iOSDevicesDefines.kCobra_iOS_ActionList_Param);
		assertTrue(customFIeldActionDefinitionIds.length >= 2);
		int autoAssignToAllMachines = (int) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_AutoAssignToAllMachines);
		int dataType = (int) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_DataType);
		String description = (String) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_Description);
		int deviceType = (int) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_DeviceType);
		int displayType = (int) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_DisplayType);
		int evaluationMethod = (int) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_EvaluationMethod);
		String customFieldUuid = (String) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_FieldID);
		String name = (String) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_Name);
		int seed = (int) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_Seed);
		String variableName = (String) dynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_VariableName);
		
		assertTrue(autoAssignToAllMachines == customField.autoAssignToAllMachines);
		assertTrue(dataType == customField.dataType);
		assertTrue(description.compareToIgnoreCase(customField.description) == 0);
		assertTrue(deviceType == customField.deviceType);
		assertTrue(displayType == customField.displayType);
		assertTrue(evaluationMethod == customField.evaluationMethod);
		assertTrue(customFieldUuid.compareToIgnoreCase(customField.id) == 0);
		assertTrue(name.compareToIgnoreCase(customField.name) == 0);
		assertTrue(seed == customField.seed);
		assertTrue(variableName.compareToIgnoreCase(customField.variableName) == 0);
	}
	
	private void veryfyFieldAction(PropertyList fieldAction, com.absolute.am.model.CustomField customField) {
		String customFieldActionDefinitionId = (String) fieldAction.get(iOSDevicesDefines.kCobra_iOS_ActionID_Param);
		assertTrue(customField.customFieldActionDefinitionIds.contains(customFieldActionDefinitionId));
		String name = (String) fieldAction.get(iOSDevicesDefines.kCobra_iOS_CustomFieldName_Param);
		assertTrue(name.compareToIgnoreCase(customField.name) == 0);
	}
	
	private void veryfyFieldAction(PropertyList fieldAction, CustomFieldActionDefinition def) {
		String customFieldActionDefinitionId = (String) fieldAction.get(iOSDevicesDefines.kCobra_iOS_ActionID_Param);
		assertTrue(customFieldActionDefinitionId.compareToIgnoreCase(def.id) == 0);
		String name = (String) fieldAction.get(iOSDevicesDefines.kCobra_iOS_CustomFieldName_Param);
		assertTrue(name.compareToIgnoreCase(def.name) == 0);
		int platform = (int) fieldAction.get(iOSDevicesDefines.kCobra_iOS_CustomeFieldPlatForm_Param);
		assertTrue(platform == def.platform);
	}
	
	@SuppressWarnings("unchecked")
	private void can_create_modify_custome_field_from_device_command(boolean deleteItems) 
			throws Exception {
		Long deviceId = 1L;
		String targetIdentifier = "584D2075-1285-4EB0-8A83-210E6E114127";
		CustomFieldItem[] customFieldItems = {
				populateCustomFieldItem(1, "value: " + UUID.randomUUID().toString()),	// String typed custom field
				populateCustomFieldItem(2, "123"),	// String typed custom field
		};
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.modifyCustomFieldFromDeviceCommand(
				deviceId,
				targetIdentifier, 
				customFieldItems, 
				deleteItems,
				LOGIN_RETURNED_ADMIN_UUID);
		
		PropertyList pl = command.buildCommandDictionary();
	
		assertNotNull(pl);
		assertTrue(pl.containsKey(CobraProtocol.kCobra_XML_CommandParameters));
		
		// check command parameter plist
		PropertyList param = (PropertyList) pl.get(CobraProtocol.kCobra_XML_CommandParameters);
		assertNotNull(param);
		long deviceType = (long) param.get(CobraCommandDefs.kCobra_Admin_CustomField_DeviceType);
		ArrayList<Long> deviceIds = (ArrayList<Long>) param.get(CobraCommandDefs.kCobra_Admin_CustomField_RecordIDListMobileDevices);
		
		assertTrue(deviceType == 2);	// the device type is hardcoded as 2
		assertTrue(deviceIds.size() == 1);
		
		if (deleteItems) {
			ArrayList<String> deviceFieldIds = (ArrayList<String>) param.get(CobraCommandDefs.kCobra_Admin_CustomField_SharedDynamicFields);
			ArrayList<String> removedFields = (ArrayList<String>) param.get(CobraCommandDefs.kCobra_Admin_CustomField_RemovedFields);
			assertTrue(deviceFieldIds.size() == 0);
			assertTrue(removedFields.size() == customFieldItems.length);
			assertTrue(customFieldItems[0].id.compareToIgnoreCase(removedFields.get(0)) == 0);
		} else {
			ArrayList<HashMap<String, String>> sharedDynamicFields = (ArrayList<HashMap<String, String>>) param.get(CobraCommandDefs.kCobra_Admin_CustomField_SharedDynamicFields);
			ArrayList<String> removedFields2 = (ArrayList<String>) param.get(CobraCommandDefs.kCobra_Admin_CustomField_RemovedFields);
			ArrayList<String> targetIdentifiers = (ArrayList<String>) param.get(CobraCommandDefs.kCobra_Admin_CustomField_TargetIdentifierList);
			assertNotNull(sharedDynamicFields);
			assertNotNull(sharedDynamicFields.size() == customFieldItems.length);
			for (int i = 0; i < customFieldItems.length; i ++) {
				HashMap<String, String> sharedDynamicField = (HashMap<String, String>) sharedDynamicFields.get(i);
				String fieldId = (String) sharedDynamicField.get(CobraCommandDefs.kCobra_Admin_CustomField_FieldID);
				String value = (String) sharedDynamicField.get(getCustomFieldItemValueTypeSTringForDataType(customFieldItems[i].type));
				assertTrue(fieldId.compareToIgnoreCase(customFieldItems[i].id) == 0);
				assertTrue(value.compareToIgnoreCase(customFieldItems[i].value) == 0);
			}
			assertTrue(removedFields2.size() == 0);
			assertTrue(targetIdentifiers.size() == 1);
			assertTrue(targetIdentifiers.get(0).compareToIgnoreCase(targetIdentifier) == 0);
		}
	}
	
	// Data Type: 1=String, 2=Number, 3=Boolean, 4=Date, 5=File Version, 6=IP Address, 7=Enumeration
	private CustomFieldItem populateCustomFieldItem(int dataType, String dataValue)
	{
		CustomFieldItem cfi = new CustomFieldItem();
		cfi.id = UUID.randomUUID().toString();
		cfi.value = dataValue;
		cfi.type = dataType;
		cfi.valueHigh32 = null;
		cfi.valueLow32 = null;
		
		return cfi;
	}
	
	private String getCustomFieldItemValueTypeSTringForDataType(int datatype) {
		String itemValueType = CobraCommandDefs.kCobra_Admin_CustomField_Value;
		// Data Type: 1=String, 2=Number, 3=Boolean, 4=Date, 5=File Version, 6=IP Address, 7=Enumeration
		if(datatype == 3 || datatype == 2 || datatype == 6 || datatype == 5){
			itemValueType = CobraCommandDefs.kCobra_Admin_CustomField_NumberValue;
		} else if(datatype == 4){
			itemValueType = CobraCommandDefs.kCobra_Admin_CustomField_DateValue;
		}
		
		return itemValueType;
	}
	// =============== End of Custom Field Tests ===============
}

