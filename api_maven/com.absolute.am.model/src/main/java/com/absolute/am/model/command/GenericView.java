package com.absolute.am.model.command;

import java.util.Map;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ephilippov
 *
 */
@XmlRootElement
public class GenericView {

	public String[] guids;
	public String rootTable;
	public String sortBy;
	public String sortDir;
	public Map<String, Object> filter;			
}