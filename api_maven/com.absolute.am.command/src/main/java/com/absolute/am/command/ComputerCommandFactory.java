/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
 * Reproduction or transmission in whole or in part, in any form or by any means,
 * electronic, mechanical or otherwise, is prohibited without the prior written
 * consent of the copyright owner.
 */
package com.absolute.am.command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;
import com.absolute.util.*;

/**
 * @author maboulkhoudoud
 * 
 */
public class ComputerCommandFactory {
	/**
	 * Returns a "ForceFullInventory" command for given serial numbers.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param computerSerials
	 *            - an array of computer serials identifying the computers to be
	 *            requested for inventory.
	 * @param booleans
	 *            - Support for additional information like fonts, printers,
	 *            serivces, startup items
	 * @return The Cobra command to be send to the AM Server
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createForceFullInventoryCommand(
			UUID adminUUID, UUID[] computerSerials, boolean fullInv,
			boolean withFonts, boolean withPrinters, boolean withServices,
			boolean withStartupItems) throws IOException,
			GeneralSecurityException {

		// Build the minimum CommandParameters plist to make the AM Server
		// accept the command
		PropertyList commandParameters = buildStandardComputerDefs();

		// Add all our command specific elements follow:

		// <key>AgentSerialList</key>
		// <array>
		// <string>F794B328-0376-4305-8768-4FA1F72B8082</string>
		// </array>
		commandParameters.put(
				CobraCommandDefs.kCobra_Admin_Computer_AgentSerialList_Param,
				computerSerials);

		// <key>CommandID</key>
		// <integer>2019</integer>
		commandParameters.put(
				CobraCommandDefs.kCobra_Admin_Computer_CommandID_Param,
				CobraCommandDefs.kCobra_AgentGetFullInventoryInfo_Command);

		// <key>CommandParameters</key>
		// <dict>
		// <key>ForceFullInventory</key>
		// <true/>
		// <key>WithFonts</key>
		// <false/>
		// <key>WithPrinters</key>
		// <false/>
		// <key>WithServices</key>
		// <false/>
		// <key>WithStartupItems</key>
		// <false/>
		// </dict>
		PropertyList inventoryCommandParameters = new PropertyList();
		inventoryCommandParameters
				.put(CobraCommandDefs.kCobra_GetFullInventory_ForceFullInventory_Param,
						fullInv);
		inventoryCommandParameters.put(
				CobraCommandDefs.kCobra_GetFullInventory_WithFonts_Param,
				withFonts);
		inventoryCommandParameters.put(
				CobraCommandDefs.kCobra_GetFullInventory_WithPrinters_Param,
				withPrinters);
		inventoryCommandParameters.put(
				CobraCommandDefs.kCobra_GetFullInventory_WithWinServices_Param,
				withServices);
		inventoryCommandParameters
				.put(CobraCommandDefs.kCobra_GetFullInventory_WithStartupItems_Param,
						withStartupItems);
		commandParameters.put(
				CobraCommandDefs.kCobra_Admin_Computer_CommandParameters_Param,
				inventoryCommandParameters);

		// <key>CommandID</key>
		// <integer>1000</integer>
		// <key>ServerID</key>
		// <integer>2</integer>
		CommandInfoData commandInfoData = new CommandInfoData(
				CobraCommandDefs.kCobra_Admin_Queue_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(
				commandInfoData, adminUUID, commandParameters);

		return command;
	}

	/**
	 * Returns a "SendMessage" command for given device serial numbers.
	 * 
	 * @param adminUUID
	 *            - the AdminUUID from the response to the login command
	 * @param computerSerials
	 *            - an array of computer serials identifying the computers to be
	 *            requested for inventory.
	 * @return The Cobra command to be send to the AM Server
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createSendMessageCommand(
			UUID adminUUID, UUID[] computerSerials, String MessageText,
			String headerLocalizedString, String timeoutLocalizedString)
			throws IOException, GeneralSecurityException {

		// Build the minimum CommandParameters plist to make the AM Server
		// accept the command
		PropertyList commandParameters = buildStandardComputerDefs();

		// Add all our command specific elements follow:

		// <key>AgentSerialList</key>
		// <array>
		// <string>F794B328-0376-4305-8768-4FA1F72B8082</string>
		// </array>
		commandParameters.put(
				CobraCommandDefs.kCobra_Admin_Computer_AgentSerialList_Param,
				computerSerials);

		// <key>CommandID</key>
		// <integer>2000</integer>
		commandParameters.put(
				CobraCommandDefs.kCobra_Admin_Computer_CommandID_Param,
				CobraCommandDefs.kCobra_AgentSendMessage_Command);

		// <key>CommandParameters</key>
		// <dict>
		// <key>HeaderText</key>
		// <string>Message from Absolute Manage Admin</string>
		// <key>MessageText</key>
		// <string>This Is a Test Message</string>
		// <key>Timeout</key>
		// <integer>0</integer>
		// <key>TimeoutCounterText</key>
		// <string>If you do nothing, this dialog will be closed automatically
		// in %@.</string>
		// <key>WithCancel</key>
		// <false/>
		// </dict>
		PropertyList sendMessageCommandParameters = new PropertyList();
		sendMessageCommandParameters.put(
				CobraCommandDefs.kCobra_AgentSendMessage_HeaderText_Param,
				headerLocalizedString);
		sendMessageCommandParameters.put(
				CobraCommandDefs.kCobra_AgentSendMessage_Text_Param,
				MessageText);
		sendMessageCommandParameters.put(
				CobraCommandDefs.kCobra_AgentSendMessage_Timeout_Param, 0);
		sendMessageCommandParameters
				.put(CobraCommandDefs.kCobra_AgentSendMessage_TimeoutCounterText_Param,
						timeoutLocalizedString);
		sendMessageCommandParameters.put(
				CobraCommandDefs.kCobra_AgentSendMessage_WithCancel_Param,
				false);
		commandParameters.put(
				CobraCommandDefs.kCobra_Admin_Computer_CommandParameters_Param,
				sendMessageCommandParameters);

		// <key>CommandID</key>
		// <integer>1000</integer>
		// <key>ServerID</key>
		// <integer>2</integer>
		CommandInfoData commandInfoData = new CommandInfoData(
				CobraCommandDefs.kCobra_Admin_Queue_Command,
				CobraProtocol.kCobraAdminServer);

		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(
				commandInfoData, adminUUID, commandParameters);

		return command;
	}

	/**
	 * Returns a "delete history commands" command for a list of command ids.
	 * @param adminUUID - the AdminUUID from the response to the login command
	 * @param commandIds - an array of command ids identifying the commands to be deleted.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createDeleteHistoryCommandsCommand(
			UUID adminUUID,
			int[] commandIds) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		commandParameters.put(CobraCommandDefs.kCobra_DeleteComputerCommands_CommandQueueRecordIDList, commandIds);
		commandParameters.put(CobraCommandDefs.kCobra_DeleteComputerCommands_CommandRecordID, 0);
		commandParameters.put(CobraCommandDefs.kCobra_DeleteComputerCommands_DeleteFromHistoryDB, true);
		commandParameters.put(CobraCommandDefs.kCobra_DeleteComputerCommands_LogToHistory, false);
		
		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_DeleteComputerCommands_Command, CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		
		return command;
	}
	
	/**
	 * Returns a "delete queued commands" command for a list of command ids.
	 * @param adminUUID - the AdminUUID from the response to the login command
	 * @param commandIds - an array of command ids identifying the commands to be deleted.
	 * @return
	 * @throws IOException
	 * @throws GeneralSecurityException
	 */
	public static CobraAdminMiscDatabaseCommand createDeleteQueuedCommandsCommand(
			UUID adminUUID,
			int[] commandIds) throws IOException,
			GeneralSecurityException {

		PropertyList commandParameters = new PropertyList();

		commandParameters.put(CobraCommandDefs.kCobra_DeleteComputerCommands_CommandQueueRecordIDList, commandIds);
		commandParameters.put(CobraCommandDefs.kCobra_DeleteComputerCommands_CommandRecordID, 0);
		commandParameters.put(CobraCommandDefs.kCobra_DeleteComputerCommands_DeleteFromHistoryDB, false);
		commandParameters.put(CobraCommandDefs.kCobra_DeleteComputerCommands_LogToHistory, true);
		
		CommandInfoData commandInfoData = new CommandInfoData(CobraCommandDefs.kCobra_DeleteComputerCommands_Command, CobraProtocol.kCobraAdminServer);
		CobraAdminMiscDatabaseCommand command = new CobraAdminMiscDatabaseCommand(commandInfoData, adminUUID, commandParameters);
		
		return command;
	}
	
	/**
	 * Helper to build the standard minimum CommandParameters plist required to
	 * send a computer command to the AM Server.
	 * 
	 * @return The CommandParameters that the AM Server requires but will
	 *         generally be static amongst commands.
	 */
	private static PropertyList buildStandardComputerDefs() {
		PropertyList commandParameters = new PropertyList();

		// <key>ExecuteCommandNow</key>
		// <true/>
		commandParameters.put(CobraProtocol.kCobra_XML_ExecuteCommandNow, true);

		// <key>CommandUUID</key>
		// <string>8A155DB9-2CCE-427F-94E8-3CC4FA5DB476</string>
		commandParameters.put(CobraProtocol.kCobra_XML_CommandUUID,
				UUID.randomUUID());

		// <key>CommandVersion</key>
		// <integer>1</integer>
		commandParameters.put(CobraProtocol.kCobra_XML_CommandVersion, 1);

		// <key>CommandInterval</key>
		// <integer>0</integer>
		commandParameters.put(CobraProtocol.kCobra_XML_CommandInterval, 0);

		// <key>CommandIntervalUnit</key>
		// <integer>2</integer>
		commandParameters.put(CobraProtocol.kCobra_XML_CommandIntervalUnit, 2);

		// <key>CommandDeferUncompletedTasks</key>
		// <true/>
		commandParameters.put(CobraProtocol.kCobra_XML_DeferUncompletedTasks,
				true);

		// <key>CommandWakeUpMachineIfNotAvail</key>
		// <false/>
		commandParameters.put(
				CobraProtocol.kCobra_XML_CommandWakeUpMachineIfNotAvail, false);

		// <key>CommandHistoryOption</key>
		// <integer>1</integer>
		commandParameters.put(CobraProtocol.kCobra_XML_CommandHistoryOption,
				CobraProtocol.kCobra_XML_CommandHistoryOption_AlwaysAdd);

		return commandParameters;
	}
}
