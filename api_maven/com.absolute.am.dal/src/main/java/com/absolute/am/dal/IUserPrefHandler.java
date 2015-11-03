/**
 * 
 */
package com.absolute.am.dal;

import java.util.ArrayList;
import com.absolute.am.dal.model.UserPreference;


public interface IUserPrefHandler {

	/**
	 * Store a user preference 
	 * @param userUID
	 * @param keyId
	 * @param value
	 * @param contentType
	 * @param isFile
	 * @param destFilePath
	 * @throws Exception
	 */
	void putPref(String userUID, String keyId, String value, String contentType, int isFile, String destFilePath) 
			throws Exception;
	
	/**
	 * Retrieve a user preference
	 * @param userUID
	 * @param keyId
	 * @return
	 * @throws Exception
	 */
	UserPreference getPref(String userUID, String keyId) throws Exception;
	
	/**
	 * Get all user prefs
	 * @param userUID
	 * @return
	 * @throws Exception
	 */
	ArrayList<UserPreference> getUserPrefList(String userUID) throws Exception;
	
	/**
	 * Delete a user pref
	 * @param userUID
	 * @param keyId
	 * @throws Exception
	 */
	void deletePref(String userUID, String keyId) throws Exception;
}
