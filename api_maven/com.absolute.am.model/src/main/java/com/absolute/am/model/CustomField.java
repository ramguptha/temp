package com.absolute.am.model;

import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 *
 */
@XmlRootElement
public class CustomField {

	public String uniqueId = null;
	public String name = null;
	public String description = null;
	public String variableName = null;
	public Integer dataType = null;
	public Integer displayType = null;
	public String[] enumerationList = null;
	public ArrayList<String> customFieldActionDefinitionIds = new ArrayList<String>();
	
	// default values for some properties that we don't want the user to change ( probably )
	public final int defaultAutoAssignToAllMachines = 0;
	public final int defaultDeviceType = 2;
	public final int defaultEvaluationMethod = 2;
	public final int defaultSeed = 1;
	
}