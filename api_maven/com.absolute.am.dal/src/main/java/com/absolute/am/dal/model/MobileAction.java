/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.dal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "mobile_actions")

public class MobileAction {
	/**
	 * 
	 */
	
	@Id
	@Column(name = "id")
	private long id = 1L;
	
	@Column(name = "UniqueID")
	private String uniqueID;
	
	@Column(name = "Seed")
	private Integer seed;
	
	@Column(name = "ActionType")
	private Integer actionType;
	
	@Column(name = "SupportedPlatforms")
	private Integer supportedPlatforms;
	
	@Column(name = "DisplayName")
	private String displayName;
	
	@Column(name = "Description")
	private String description;
	
	@Column(name = "ActionData")
	private byte[] actionData;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	public Integer getSeed() {
		return seed;
	}
	public void setSeed(Integer seed) {
		this.seed = seed;
	}
	public Integer getActionType() {
		return actionType;
	}
	public void setActionType(Integer actionType) {
		this.actionType = actionType;
	}
	public Integer getSupportedPlatforms() {
		return supportedPlatforms;
	}
	public void setSupportedPlatforms(Integer supportedPlatforms) {
		this.supportedPlatforms = supportedPlatforms;
	}
	public String getDisplayName() {
		return displayName;
	}
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public byte[] getActionData() {
		return actionData;
	}
	public void setActionData(byte[] actionData) {
		this.actionData = actionData;
	}
}
