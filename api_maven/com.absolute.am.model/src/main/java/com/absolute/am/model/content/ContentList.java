/**
 * 
 */
package com.absolute.am.model.content;

import java.util.Arrays;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author dlavin
 *
 */
@XmlRootElement
public class ContentList {

	private int[] contentIds;

	
	/**
	 * The content ids
	 */
	public int[] getContentIds() {
		return contentIds;
	}
	public void setContentIds(int[] contentIds) {
		this.contentIds = contentIds;
	}


	@Override
	public String toString() {	
		
		return "ContentList: contentIds=" + Arrays.toString(contentIds);
	}			
}
