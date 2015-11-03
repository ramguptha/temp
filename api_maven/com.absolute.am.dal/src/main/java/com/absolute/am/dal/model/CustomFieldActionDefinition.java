package com.absolute.am.dal.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "custom_field_action_definitions")
public class CustomFieldActionDefinition {
	@Id
	@Column(name = "id")
	public String id;
	
	@Column(name = "Name")
	public String name;
	
	@Column(name = "Platform")
	public Integer platform;
	
	@Column(name = "ReplaceLineFeeds")
	public Integer replaceLineFeeds;
	
	@Column(name = "RequiresAdminPrivileges")
	public Integer requiresAdminPrivileges;
	
	@Column(name = "Seed")
	public Integer seed;
	
	@Column(name = "ExecuteOnlyWithFullInventory")
	public Integer executeOnlyWithFullInventory;
	
	@Column(name = "ReturnExecutionErrors")
	public Integer returnExecutionErrors;
	
	@Column(name = "SourceTypeSelector")
	public Integer sourceTypeSelector;
	
	@Column(name = "SourceFile")
	public String sourceFile;
	
	@Column(name = "SourceFileChecksum")
	public String sourceFileChecksum;
	
	@Column(name = "ExecutableOptions")
	public String executableOptions;
	
	@Column(name = "ExecutableTypeSelector")
	public Integer executableTypeSelector;
	
	@Column(name = "TransferExecutableFolder")
	public Integer transferExecutableFolder;
	
	@Column(name = "UserContext")
	public String userContext;
	
	@Column(name = "UserContextPassword")
	public String userContextPassword;
	
	@Column(name = "ExecutablePartialPath")
	public String executablePartialPath;
	
	@Column(name = "UserContextSelector")
	public Integer userContextSelector;
	
	@Column(name = "ScriptText")
	public String scriptText;
	
	@Column(name = "PListDomain")
	public String pListDomain;
	
	@Column(name = "PListKey")
	public String pListKey;
	
	@Column(name = "PListLocationSelector")
	public Integer pListLocationSelector;
	
	@Column(name = "RegistryPath")
	public String registryPath;
}
