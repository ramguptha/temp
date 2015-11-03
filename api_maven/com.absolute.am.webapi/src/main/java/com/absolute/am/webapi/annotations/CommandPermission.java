package com.absolute.am.webapi.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom annotation used to mark endpoints as requiring authorization. The client should be assigned the right
 * to issue specific commands in the AM Console.
 * See also {@link Right} and {@link com.absolute.am.webapi.filters.AuthorizationResourceFilterFactory}.
 * @author dlavin
 *
 */
@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface CommandPermission {
	AMCommand value();
	
	/**
	 * Endpoints are marked with the {@link CommandPermission} annotation to indicate the command permission
	 * required to execute a command. This enumeration is the list of all known command permissions. 
	 * <br>
	 * <br>
	 * These values were taken from UInt32 CommandIDToCommandBitNumber(UInt32 inCommandID) in the file
	 * \Cobra\Common\Source\CobraUtilities.cpp and must be kept in sync.
	 */
	public enum AMCommand {
		/*
		 * The following entries are valid but only apply to PCs or MACs. They are commented out to prevent
		 * accidental use as their names are similar to some of the mobile device commands. */
		kCobra_AgentSendMessage_Command(1),/*
		kCobra_AgentRestart_Command(2),
		kCobra_AgentExecuteFile_Command(3),
		kCobra_AgentExecuteFileWin_Command(3),
		kCobra_AgentTransferFile_Command(6),
		kCobra_AgentRenameFile_Command(7),
		kCobra_AgentDeleteFile_Command(8),
		kCobra_AgentCreateAlias_Command(9),
		kCobra_AgentCopyFile_Command(10),
		kCobra_AgentMoveFile_Command(11),
		kCobra_AgentCreateFolder_Command(12),
		kCobra_AgentSetAgentSetting_Command(13),
		kCobra_AgentProcessesInfo_Command(14),
		kCobra_AgentTerminateProcess_Command(15),*/
		kCobra_AgentGetFullInventoryInfo_Command(16),/*
		kCobra_AgentOpenFile_Command(17),
		kCobra_AgentWOL_Command(18),
		kCobra_AgentFindFile_Command(19),
		kCobra_AgentCheckForPackages_Command(20),
		kCobra_AgentRunLicensingScan_Command(21),
		kCobra_AgentInstalledSoftwareInfo_Command(22),
		kCobra_AgentReImage_Command(23),
		kCobra_AgentChangeWinServicesOperationState_Command(24),
		kCobra_AgentRegistry_Command(25),
		kCobra_AgentFindRegistry_Command(26),
		kCobra_AgentViewFile_Command(27),
		kCobra_AgentExecuteScript_Command(28),
		kCobra_AgentTimeMachine_Command(29),
		kCobra_AgentPowerManagement_Command(30),
		kCobra_AgentComplianceReport_Command(31),
		kCobra_AgentInstallSDPackages_Command(32),
		kCobra_AgentGetDirectoryContent_Command(33),
		kCobra_Admin_ReImageWin_Command(34),*/
		kCobra_Admin_SendMDMCommandPseudo_InstallProfile(35),
		kCobra_Admin_SendMDMCommandPseudo_RemoveProfile(36),
		kCobra_Admin_SendMDMCommandPseudo_InstallProvisioningProfile(37),
		kCobra_Admin_SendMDMCommandPseudo_RemoveProvisioningProfile(38),
		kCobra_Admin_SendMDMCommandPseudo_DeviceLock(39),
		kCobra_Admin_SendMDMCommandPseudo_ClearPasscode(40),
		kCobra_Admin_SendMDMCommandPseudo_RemoteErase(41),
		kCobra_Admin_SendMDMCommandPseudo_UpdateDeviceInfo(42),
		kCobra_Admin_SendMDMCommandPseudo_SendMessageToDevice(43),
		kCobra_Admin_SendMDMCommandPseudo_InstallApplication(44),
		kCobra_Admin_SendMDMCommandPseudo_RemoveApplication(45),
		kCobra_Admin_SendMDMCommandPseudo_ChangeRoamingOptions(46),
		kCobra_Admin_SendMDMCommandPseudo_ChangeAppConfiguration(54),
		kCobra_Admin_SendMDMCommandPseudo_ChangeActivationLockOptions(58),
		kCobra_Admin_SendMDMCommandPseudo_SetOrganizationInfo(59);
		
		private final int bitNumber;

		AMCommand(int bitNumber) {
			if (bitNumber < 0 || bitNumber >= 64) {
				throw new IllegalArgumentException("Invalid bit number. Range 0-63.");
			}
			this.bitNumber = bitNumber;
		}
		
		/**
		 * Get the bit number for this command.
		 * @return A number in the range of 0-64.
		 */
		public int getBitNumber() {
			return bitNumber;
		}
		
		/**
		 * Generates a mask based on the bit number.
		 * @return A mask with a single bit set.
		 */
		public long getBitNumberAsMask() {
			return (1L << bitNumber);
		}
	}
}
