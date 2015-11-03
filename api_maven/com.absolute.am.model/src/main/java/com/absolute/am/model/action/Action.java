package com.absolute.am.model.action;

import java.util.Map;



public class Action {
	private int id;
	private String uniqueID;
	private int seed;
	private String name;	
	private int actionType;
	private String description;
	private int supportedPlatforms;
	private Map<String, Object> actionData; 
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}

	public String getUniqueID() {
		return uniqueID;
	}
	public void setUniqueID(String uniqueID) {
		this.uniqueID = uniqueID;
	}
	
	public int getSeed() {
		return seed;
	}
	public void setSeed(int seed) {
		this.seed = seed;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	public int getActionType() {
		return actionType;
	}
	public void setActionType(int actionType) {
		this.actionType = actionType;
	}
	
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	public int getSupportedPlatforms() {
		return supportedPlatforms;
	}
	public void setSupportedPlatforms(int supportedPlatforms) {
		this.supportedPlatforms = supportedPlatforms;
	}
	
	public Map<String, Object> getActionData() {
		return actionData;
	}
	
	public void setActionData(Map<String, Object> actionData) {
		this.actionData = actionData;
	}
}
