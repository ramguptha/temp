/**
 * 
 */
package com.absolute.am.dal;

import java.util.List;
import java.util.UUID;

import com.absolute.am.dal.model.MobileMedia;

/**
 * @author klavin
 * 
 */


public interface IContentHandler {

	public MobileMedia getContent(long contentId) throws Exception; 
	
	public UUID[] getMediaUniqueIds(List<Long> contentIds) throws Exception; 		
	
	/**
	 * Searches for a content item with the given Display Name.
	 * @param displayName The Display Name to search for.
	 * @return MobileMedia if found, null otherwise.
	 */
	public MobileMedia getContentByDisplayName(String displayName);
}
