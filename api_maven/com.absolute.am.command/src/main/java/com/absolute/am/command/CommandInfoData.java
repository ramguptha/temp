package com.absolute.am.command;

import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.UUID;

class CommandInfoData {

	public int CommandID = 0;
	public int CommandVersion = 0;
	public Date CommandExecutionDateTime = Calendar.getInstance(TimeZone.getTimeZone("UTC")).getTime();
	public int ServerType = CobraProtocol.kCobraUnknownServer;
	public UUID CommandUUID = UUID.randomUUID();
	public boolean DeferUncompletedTasks = false;
	public boolean CommandWakeUpMachineIfNotAvail = false;
	public int CommandHistoryOption = CobraProtocol.kCobra_XML_CommandHistoryOption_AlwaysAdd;
	public long CommandQueueRecordID = 0;
	public UUID AgentSerialnumber = null;

	public CommandInfoData() {
	}

	public CommandInfoData(int inCommandID, int inCommandVersion,
			int inServerType, Date inCommandDate) {
		CommandID = inCommandID;
		CommandVersion = inCommandVersion;
		ServerType = inServerType;
		CommandExecutionDateTime = inCommandDate;
	}

	public CommandInfoData(int inCommandID, int inCommandVersion,
			int inServerType) {
		CommandID = inCommandID;
		CommandVersion = inCommandVersion;
		ServerType = inServerType;
	}

	public CommandInfoData(int inCommandID, int inServerType,
			Date inCommandDate) {
		CommandID = inCommandID;
		ServerType = inServerType;
		CommandExecutionDateTime = inCommandDate;
	}

	public CommandInfoData(int inCommandID, int inServerType) {
		CommandID = inCommandID;
		ServerType = inServerType;
	}
	
	public CommandInfoData(int inCommandID) {
		CommandID = inCommandID;
	}

}
