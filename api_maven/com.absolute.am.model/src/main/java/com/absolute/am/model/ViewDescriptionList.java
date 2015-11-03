/**
 * 
 */
package com.absolute.am.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author klavin
 *
 */
@XmlRootElement
public class ViewDescriptionList {

	private ViewDescription[] viewDescriptions;

	/**
	 * The view description list
	 */
	public ViewDescription[] getViewDescriptions() {
		return viewDescriptions;
	}

	public void setViewDescriptions(ViewDescription[] viewDescriptions) {
		this.viewDescriptions = viewDescriptions;
	} 
}
