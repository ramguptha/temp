package com.absolute.am.dal.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


@Entity
@Table(name = "enum_AgentPlatform")
public class AgentPlatform implements Serializable{

	/**
	 * 
	 */
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "key")
	private long key;

	@Column(name = "value_en")
	private String value_en;
	
	public long getKey() {
		return key;
	}
	public void setKey(long key) {
		this.key = key;
	}
	public String getValue_en() {
		return value_en;
	}
	public void setValue_en(String value_en) {
		this.value_en = value_en;
	}
	
	public static long getSerialversionuid() {
		return serialVersionUID;
	}
	
}