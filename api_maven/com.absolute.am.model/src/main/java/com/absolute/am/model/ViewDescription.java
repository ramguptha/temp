/**
 * 
 */
package com.absolute.am.model;


/**
 * @author klavin
 * 
 * Metadata to describe a view.
 *
 */

public class ViewDescription {
	private String viewName;
	private String viewDisplayName;
	private int id;
	
	public ViewDescription(){};
	
	public ViewDescription(String viewName, String viewDisplayName, int id) {
		this.viewName = viewName;
		this.viewDisplayName = viewDisplayName;
		this.id = id;
	}

	/**
	 * The view name
	 */
	public String getViewName() {
		return viewName;
	}

	public void setViewName(String viewName) {
		this.viewName = viewName;
	}

	/**
	 * The view display name
	 */
	public String getViewDisplayName() {
		return viewDisplayName;
	}

	public void setViewDisplayName(String viewDisplayName) {
		this.viewDisplayName = viewDisplayName;
	}

	/**
	 * The view id
	 */
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
}
