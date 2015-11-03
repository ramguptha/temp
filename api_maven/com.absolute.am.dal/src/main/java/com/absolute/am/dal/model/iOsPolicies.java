package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "iOS_policies")

public class iOsPolicies implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id")
	private long id = 1L;
	
	@Column(name = "last_modified")
	private String lastModified;
	
	@Column(name = "UniqueId")
	private String uniqueId;
	
	@Column(name = "Seed")
	private Integer seed;
	
	@Column(name = "Name")
	private String name;
	
	@Column(name = "FilterCriteria")
	private byte[] filterCriteria;
	
	@Column(name = "FilterQuery")
	private String filterQuery;

	@Column(name = "SchemaVersion")
	private Integer schemaVersion;
	
	@Column(name = "FilterTables")
	private String filterTables;
	
	@Column(name = "FilterType")
	private Integer filterType;
		
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLastModified() {
		return lastModified;
	}
	public void setLastModified(String lastModified) {
		this.lastModified = lastModified;
	}
	public String getUniqueId() {
		return uniqueId;
	}
	public void setUniqueId(String uniqueId) {
		this.uniqueId = uniqueId;
	}
	public Integer getSeed() {
		return seed;
	}
	public void setSeed(Integer seed) {
		this.seed = seed;
	}

	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public byte[] getFilterCriteria() {
		return filterCriteria;
	}
	public void setFilterCriteria(byte[] filterCriteria) {
		this.filterCriteria = filterCriteria;
	}
	public String getFilterQuery() {
		return filterQuery;
	}
	public void setFilterQuery(String filterQuery) {
		this.filterQuery = filterQuery;
	}
	public Integer getSchemaVersion() {
		return schemaVersion;
	}
	public void setSchemaVersion(Integer schemaVersion) {
		this.schemaVersion = schemaVersion;
	}
	public String getFilterTables() {
		return filterTables;
	}
	public void setFilterTables(String filterTables) {
		this.filterTables = filterTables;
	}
	public Integer getFilterType() {
		return filterType;
	}
	public void setFilterType(Integer filterType) {
		this.filterType = filterType;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}