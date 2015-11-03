/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "agent_info")

public class ComputerInfo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private long id = 1L;
	
	@Column(name = "heartbeat_record_id")
	private Integer hearbeatRecordID;
	
	@Column(name = "last_modified")
	private String lastModified;
	
	@Column(name = "MachineTimeAtHearbeat")
	private String machineTimeAtHeartbeat;
	
	@Column(name = "AgentSerial")
	private String agentSerial;
	
	@Column(name = "AgentPlatform")
	private Integer agentPlatform;
	
	@Column(name = "AgentName")
	private String agentName;
	
	@Column(name = "AgentVersion")
	private Integer agentVersion;
	
	@Column(name = "AgentBuildno")
	private Integer agentBuildNo;
	
	@Column(name = "SDServerAddress")
	private String sdServerAddress;
	
	@Column(name = "SDServerPort")
	private Integer sdServerPort;
	
	@Column(name = "SDServerCheckInterval")
	private Integer sdServerCheckInterval;
	
	@Column(name = "SLServerAddress")
	private String slServerAddress;
	
	@Column(name = "SLServerPort")
	private Integer slServerPort;
	
	@Column(name = "SLServerCheckInterval")
	private Integer slServerCheckInterval;
	
	@Column(name = "CustomAgentName")
	private String customAgentName;
	
	@Column(name = "UseCustomAgentName")
	private Integer useCustomAgentName;
	
	@Column(name = "HeartbeatInterval")
	private Integer heartbeatInterval;
	
	@Column(name = "InventoryPushInterval")
	private Integer inventoryPushInterval;
	
	@Column(name = "ServerPort")
	private Integer serverPort;
	
	@Column(name = "AgentPort")
	private Integer agentPort;
	
	@Column(name = "MachineTrackingEnabled")
	private Integer machineTrackingEnabled;
	
	@Column(name = "RecordCreationDate")
	private String recordCreationDate;
	
	@Column(name = "LastHeartbeat")
	private String lastHeartbeat;
	
	@Column(name = "ESN")
	private String ESN;
	
	@Column(name = "AbsoluteRemoteEnabled")
	private Integer absoluteRemoteEnabled;
	
	@Column(name = "AbsoluteRemotePort")
	private Integer absoluteRemotePort;
	
	@Column(name = "AbsoluteRemoteUserConfirmationRequired")
	private Integer absoluteRemoteUserConfirmationRequired;
	
	@Column(name = "DisableOSSoftwareUpdates")
	private Integer disableOSSoftwareUpdates;
	
	@Column(name = "UserInfo0")
	private String userInfo0;
	
	@Column(name = "UserInfo1")
	private String userInfo1;
	
	@Column(name = "UserInfo2")
	private String userInfo2;
	
	@Column(name = "UserInfo3")
	private String userInfo3;
	
	@Column(name = "UserInfo4")
	private String userInfo4;
	
	@Column(name = "UserInfo5")
	private String userInfo5;
	
	@Column(name = "UserInfo6")
	private String userInfo6;
	
	@Column(name = "UserInfo7")
	private String userInfo7;
	
	@Column(name = "UserInfo8")
	private String userInfo8;
	
	@Column(name = "UserInfo9")
	private String userInfo9;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Integer getHearbeatRecordID()
	{
		return hearbeatRecordID;
	}
	public void setHearbeatRecordID(Integer hearbeatRecordID)
	{
		this.hearbeatRecordID = hearbeatRecordID;
	}
	public String getLastModified()
	{
		return lastModified;
	}
	public void setLastModified(String lastModified)
	{
		this.lastModified = lastModified;
	}
	public String getMachineTimeAtHeartbeat()
	{
		return machineTimeAtHeartbeat;
	}
	public void setMachineTimeAtHeartbeat(String machineTimeAtHeartbeat)
	{
		this.machineTimeAtHeartbeat = machineTimeAtHeartbeat;
	}
	public String getAgentSerial()
	{
		return agentSerial;
	}
	public void setAgentSerial(String agentSerial)
	{
		this.agentSerial = agentSerial;
	}
	public Integer getAgentPlatform()
	{
		return agentPlatform;
	}
	public void setAgentPlatform(Integer agentPlatform)
	{
		this.agentPlatform = agentPlatform;
	}
	public String getAgentName()
	{
		return agentName;
	}
	public void setAgentName(String agentName)
	{
		this.agentName = agentName;
	}
	public Integer getAgentVersion()
	{
		return agentVersion;
	}
	public void setAgentVersion(Integer agentVersion)
	{
		this.agentVersion = agentVersion;
	}
	public Integer getAgentBuildNo()
	{
		return agentBuildNo;
	}
	public void setAgentBuildNo(Integer agentBuildNo)
	{
		this.agentBuildNo = agentBuildNo;
	}
	public String getSdServerAddress()
	{
		return sdServerAddress;
	}
	public void setSdServerAddress(String sdServerAddress)
	{
		this.sdServerAddress = sdServerAddress;
	}
	public Integer getSdServerPort()
	{
		return sdServerPort;
	}
	public void setSdServerPort(Integer sdServerPort)
	{
		this.sdServerPort = sdServerPort;
	}
	public Integer getSdServerCheckInterval()
	{
		return sdServerCheckInterval;
	}
	public void setSdServerCheckInterval(Integer sdServerCheckInterval)
	{
		this.sdServerCheckInterval = sdServerCheckInterval;
	}
	public String getSlServerAddress()
	{
		return slServerAddress;
	}
	public void setSlServerAddress(String slServerAddress)
	{
		this.slServerAddress = slServerAddress;
	}
	public Integer getSlServerPort()
	{
		return slServerPort;
	}
	public void setSlServerPort(Integer slServerPort)
	{
		this.slServerPort = slServerPort;
	}
	public Integer getSlServerCheckInterval()
	{
		return slServerCheckInterval;
	}
	public void setSlServerCheckInterval(Integer slServerCheckInterval)
	{
		this.slServerCheckInterval = slServerCheckInterval;
	}
	public String getCustomAgentName()
	{
		return customAgentName;
	}
	public void setCustomAgentName(String customAgentName)
	{
		this.customAgentName = customAgentName;
	}
	public Integer getUseCustomAgentName()
	{
		return useCustomAgentName;
	}
	public void setUseCustomAgentName(Integer useCustomAgentName)
	{
		this.useCustomAgentName = useCustomAgentName;
	}
	public Integer getHeartbeatInterval()
	{
		return heartbeatInterval;
	}
	public void setHeartbeatInterval(Integer heartbeatInterval)
	{
		this.heartbeatInterval = heartbeatInterval;
	}
	public Integer getInventoryPushInterval()
	{
		return inventoryPushInterval;
	}
	public void setInventoryPushInterval(Integer inventoryPushInterval)
	{
		this.inventoryPushInterval = inventoryPushInterval;
	}
	public Integer getServerPort()
	{
		return serverPort;
	}
	public void setServerPort(Integer serverPort)
	{
		this.serverPort = serverPort;
	}
	public Integer getAgentPort()
	{
		return agentPort;
	}
	public void setAgentPort(Integer agentPort)
	{
		this.agentPort = agentPort;
	}
	public Integer getMachineTrackingEnabled()
	{
		return machineTrackingEnabled;
	}
	public void setMachineTrackingEnabled(Integer machineTrackingEnabled)
	{
		this.machineTrackingEnabled = machineTrackingEnabled;
	}
	public String getRecordCreationDate()
	{
		return recordCreationDate;
	}
	public void setRecordCreationDate(String recordCreationDate)
	{
		this.recordCreationDate = recordCreationDate;
	}
	public String getLastHeartbeat()
	{
		return lastHeartbeat;
	}
	public void setLastHeartbeat(String lastHeartbeat)
	{
		this.lastHeartbeat = lastHeartbeat;
	}
	public String getESN()
	{
		return ESN;
	}
	public void setESN(String eSN)
	{
		ESN = eSN;
	}
	public Integer getAbsoluteRemoteEnabled()
	{
		return absoluteRemoteEnabled;
	}
	public void setAbsoluteRemoteEnabled(Integer absoluteRemoteEnabled)
	{
		this.absoluteRemoteEnabled = absoluteRemoteEnabled;
	}
	public Integer getAbsoluteRemotePort()
	{
		return absoluteRemotePort;
	}
	public void setAbsoluteRemotePort(Integer absoluteRemotePort)
	{
		this.absoluteRemotePort = absoluteRemotePort;
	}
	public Integer getAbsoluteRemoteUserConfirmationRequired()
	{
		return absoluteRemoteUserConfirmationRequired;
	}
	public void setAbsoluteRemoteUserConfirmationRequired(
			Integer absoluteRemoteUserConfirmationRequired)
	{
		this.absoluteRemoteUserConfirmationRequired = absoluteRemoteUserConfirmationRequired;
	}
	public Integer getDisableOSSoftwareUpdates()
	{
		return disableOSSoftwareUpdates;
	}
	public void setDisableOSSoftwareUpdates(Integer disableOSSoftwareUpdates)
	{
		this.disableOSSoftwareUpdates = disableOSSoftwareUpdates;
	}
	public String getUserInfo0()
	{
		return userInfo0;
	}
	public void setUserInfo0(String userInfo0)
	{
		this.userInfo0 = userInfo0;
	}
	public String getUserInfo1()
	{
		return userInfo1;
	}
	public void setUserInfo1(String userInfo1)
	{
		this.userInfo1 = userInfo1;
	}
	public String getUserInfo2()
	{
		return userInfo2;
	}
	public void setUserInfo2(String userInfo2)
	{
		this.userInfo2 = userInfo2;
	}
	public String getUserInfo3()
	{
		return userInfo3;
	}
	public void setUserInfo3(String userInfo3)
	{
		this.userInfo3 = userInfo3;
	}
	public String getUserInfo4()
	{
		return userInfo4;
	}
	public void setUserInfo4(String userInfo4)
	{
		this.userInfo4 = userInfo4;
	}
	public String getUserInfo5()
	{
		return userInfo5;
	}
	public void setUserInfo5(String userInfo5)
	{
		this.userInfo5 = userInfo5;
	}
	public String getUserInfo6()
	{
		return userInfo6;
	}
	public void setUserInfo6(String userInfo6)
	{
		this.userInfo6 = userInfo6;
	}
	public String getUserInfo7()
	{
		return userInfo7;
	}
	public void setUserInfo7(String userInfo7)
	{
		this.userInfo7 = userInfo7;
	}
	public String getUserInfo8()
	{
		return userInfo8;
	}
	public void setUserInfo8(String userInfo8)
	{
		this.userInfo8 = userInfo8;
	}
	public String getUserInfo9()
	{
		return userInfo9;
	}
	public void setUserInfo9(String userInfo9)
	{
		this.userInfo9 = userInfo9;
	}
}
