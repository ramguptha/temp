/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package com.absolute.am.dal.model;

import java.util.ArrayList;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

@Entity
@Table(name = "custom_field_definitions")
public class CustomField {

	@Id
	@Column(name = "id")
	public String id;
	
	@Column(name = "Name")
	public String name;
	
	@Column(name = "Seed")
	public Integer seed;
	
	@Column(name = "DataType")
	public Integer dataType;
	
	@Column(name = "DisplayType")
	public Integer displayType;
	
	@Column(name = "EvaluationMethod")
	public Integer evaluationMethod;
	
	@Column(name = "AutoAssignToAllMachines")
	public Integer autoAssignToAllMachines;
	
	@Column(name = "Description")
	public String description;
	
	@Column(name = "EnumerationList")
	public byte[] enumerationList;
	
	@Column(name = "DeviceType")
	public Integer deviceType;
	
	@Column(name = "VariableName")
	public String variableName;
	
	@Transient
	public ArrayList<CustomFieldActionDefinition> customFieldActionDefinitions = new ArrayList<CustomFieldActionDefinition>();
}
