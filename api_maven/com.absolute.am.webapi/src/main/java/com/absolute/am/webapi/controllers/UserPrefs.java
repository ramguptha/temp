
package com.absolute.am.webapi.controllers;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.codehaus.enunciate.jaxrs.*;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.model.UserPreference;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.model.exception.InternalServerErrorException;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.model.userprefs.KeyContentType;
import com.absolute.am.webapi.util.ResourceUtilities;

/**
 * <h3>User API</h3>
 * <p>The purpose of this API is to provide the client with a mechanism to manage key/value pairs that are to be associated with the currently logged in user.</p>
 * <p>The key/value pairs are placed in persistent storage and will exist beyond the current session.</p>
 *
 */
@Path ("/user/prefs")
public class UserPrefs {

    private static Logger m_logger = LoggerFactory.getLogger(UserPrefs.class.getName());
	private static final String INI_USERPREFS_FOLDER = "com.absolute.webapi.controllers.userprefs.folderForUserPrefsFiles";
	private static final String INI_USERPREFS_REQUEST_LENGTH_LIMIT = "com.absolute.webapi.controllers.userprefs.requestLengthLimit";
	
	private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;

	/**
	 * <p>Returns the value associated with the named key. The <strong>HTTP Content-Type</strong> header will be set to the same value that was provided
	 *    in the original POST request that created the key/value pair.</p>
	 * <p>Rights required:</br>
	 *    None – all users have access to this endpoint.</p>
	 * 
	 * @param keyId The named key
	 * @return Returns the value associated with the named key for current user
	 * @throws Exception 
	 */
	@GET @Path("/{key}")
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "The key was not found ")
		})
	public Response getUserPrefByKey(@PathParam("key") String keyId) throws Exception  {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
	    String userUID = session.getAttribute(SessionState.ADMIN_UUID).toString();
	    
		m_logger.debug("UserPrefs.getUserPrefByKey called, key={}, ADMIN_UUID={}.", keyId, userUID);

		UserPreference pref = null;
		IDal dal = Application.getDal(session);
		
		pref = dal.getUserPrefHandler().getPref(userUID, keyId);

		if (null == pref) {
			throw new NotFoundException("USERPREFS_KEY_NOT_FOUND", null, locale, m_Base, "key", keyId);
		}
		
		boolean isFile =  (1 == pref.getIsFile());
		
		if (isFile)	{
			try {
				FileInputStream inputStream = new FileInputStream(pref.getFilePath());
	    		return Response.ok(inputStream).type(pref.getContentType()).build();

	    	} catch (FileNotFoundException ex) {
	  			throw new NotFoundException("USERPREFS_FILE_NOT_FOUND", null, locale, m_Base, "file", pref.getFilePath(), "message", ex.getMessage());
	    	}
	    } else {	//text or json types
	    	  return Response.ok(pref.getValue()).type(pref.getContentType()).build();
	    }

	}
	
	/**
	 * <p>Get a list of keys and their content-types for the user. The client may then use GET/POST/DELETE to retrieve or modify the key/value pairs.</p>
	 * <p>Rights required:</br>
	 *    None – all users have access to this endpoint.</p>
	 * 
	 * @return Returns list of keys and content-types for current user
	 * @throws Exception 
	 */
	@GET
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 200, condition = "The list is returned. If there are no keys, an empty list should be returned.")
		})	
	public Response getUserPrefList() 
			throws Exception  {

	    HttpSession session = m_servletRequest.getSession();
	    String userUID = session.getAttribute(SessionState.ADMIN_UUID).toString();
	    
		m_logger.debug("UserPrefs.getUserPrefKeys called, ADMIN_UUID={}.", userUID);
		
		ArrayList<KeyContentType> kctlist = new ArrayList<KeyContentType>();		
		IDal dal = Application.getDal(session);
		
		ArrayList<UserPreference> rset = dal.getUserPrefHandler().getUserPrefList(userUID);
			
		for (UserPreference up : rset) {
			KeyContentType row = new KeyContentType(up.getName(), up.getContentType());
			kctlist.add(row);
		}
	
	    return Response.ok(kctlist).build();
	}
	
	/**
	 * <p>This will delete the key/value pair. The response has no body.</p>
	 * 
	 * @param keyId The named key.
	 * @return
	 * @throws Exception 
	 */
	@DELETE @Path("/{key}")
	@StatusCodes ({
		  @ResponseCode ( code = 204, condition = "Content deleted. This will be returned even when the key is not found."),
		})
	public void deleteUserPrefByKey(@PathParam("key") String keyId) throws Exception  {

	    HttpSession session = m_servletRequest.getSession();
	    String userUID = session.getAttribute(SessionState.ADMIN_UUID).toString();
	    
		m_logger.debug("UserPrefs.deleteUserPrefKey called,  key={}, ADMIN_UUID={}.", keyId, userUID);
		
		UserPreference pref = null;
		boolean isFile = false;		
		IDal dal = Application.getDal(session);

		pref = dal.getUserPrefHandler().getPref(userUID, keyId);
	
		if (pref != null) {
			isFile = (1 == pref.getIsFile());
		}
	
		dal.getUserPrefHandler().deletePref(userUID, keyId);
	      
	    if (isFile) {
	    	try {
	    		File file = new File(pref.getFilePath());
			   
	    		if (file.delete()) {
	    			m_logger.debug("File deleted, file={},  key={}, ADMIN_UUID={}.", pref.getFilePath(), keyId, userUID);
	    		} else {
	    			//no need to error out application
	    			m_logger.error("File cannot be deleted, file={},  key={}, ADMIN_UUID={}.", pref.getFilePath(), keyId, userUID);
	    		}
			   
	    	} catch(Exception e) {
	    		//no need to error out application
	    		m_logger.error("Error deleting file, file={},  key={}, ADMIN_UUID={}.", pref.getFilePath(), keyId, userUID);
	    	} finally {}
	    }
	    return;
	}	
	
	
	/**
	 * <p>Creates or updates the key/value pair. 
	 *    The body of the request is saved to persistent storage as the "value" and will be returned by a GET request to the same URI. 
	 *    The <strong>Content-Type</strong> from the request is also stored and associated with the key so it can be returned in the GET request.</p>
	 * <p>The response has no body.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None – all users have access to this endpoint.</p>
	 *    
	 * @param keyId The named key.
	 * @param dataChunkIs Input Stream.
	 * @return The response has no body.
	 * @throws Exception 
	 */
	@POST
	@Path("/{key}")
	@StatusCodes ({
		  @ResponseCode ( code = 204, condition = "No Content: indicates that the value was successfully saved."),
		  @ResponseCode ( code = 413, condition = "Request entity too large: indicates that the data is too large to be stored here. The limit will be a configurable attribute of the server, and is likely to exceed several kilobytes.")
		})
	public Response postUserPrefByKey(
			@PathParam("key") String keyId,
			InputStream dataChunkIs) throws Exception  {

		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
	    String userUID = session.getAttribute(SessionState.ADMIN_UUID).toString();

		//return status code 413 if request is too large:
		int reqLength = m_servletRequest.getContentLength();
		int reqLengthLimit = 0;
		String limit = m_servletRequest.getServletContext().getInitParameter(INI_USERPREFS_REQUEST_LENGTH_LIMIT);
		if (null != limit && !limit.isEmpty()) {
			reqLengthLimit = Integer.parseInt(limit);
		} else {
			throw new InternalServerErrorException("CONFIGURATION_ERROR", "SETTING_NOT_FOUND", new Object[]{INI_USERPREFS_REQUEST_LENGTH_LIMIT}, locale, m_Base);
		}
		
		//length=-1 for 2GB+ or if the length is not known
		if ((reqLength > reqLengthLimit) || (reqLength < 0)) {
			m_logger.debug("Request is too large, length={} bytes, limit={} bytes, key={}, ADMIN_UUID={}.",
					reqLength, reqLengthLimit, keyId, userUID);
			return Response.status(413).build();
		}
		
		String contentType = m_servletRequest.getContentType().toLowerCase();
		String encoding = com.absolute.util.StringUtilities.getCharsetFromContentType(contentType);
		if (encoding == "") {
			encoding = "UTF-8";
		}
	
		boolean isFile = isContentBinary(contentType);
		
		String destFolder;
		String destFileExtension;
		String destFilePath = "";
		
		int isFileInt;
		if (isFile) {
			isFileInt = 1;
		} else {
			isFileInt = 0;
		}
		
		ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
		String value = "";		//value of string-type preference
		int len = 0;
		
		if (isFile) {
			//format: <folderForUserPrefsFiles>/<UserUID>/<keyId>.<extension>
			destFolder = m_servletRequest.getServletContext().getInitParameter(INI_USERPREFS_FOLDER) + "/" + userUID + "/";
			destFileExtension = getFileExtensionFromContentType(contentType);
			destFilePath = destFolder + keyId + "." + destFileExtension;
					
			//create folder if not exists
			File dir = new File(destFolder);
			if (!dir.exists()) {
		    	m_logger.debug("UserPrefs creating folder:{}", destFolder);
		    	if (!dir.mkdirs()) {
		    		throw new FileNotFoundException("Failed to create folder:" + destFolder);
		    	}
			}
			
			// Read the input data to a byte array.
			byte[] tmp = new byte[8*1024];
			len = dataChunkIs.read(tmp);
			while (len != -1) {
				baoStream.write(tmp, 0, len);
				len = dataChunkIs.read(tmp);				
			}
			
			try {
				//save byte array into a file
				FileOutputStream foStream = new FileOutputStream(destFilePath); 
				baoStream.writeTo(foStream); 
				foStream.close();
			} finally {}

		} else {

			byte[] tmp = new byte[1024];
			len = dataChunkIs.read(tmp);
			while(len != -1) {
				baoStream.write(tmp, 0, len);
				len = dataChunkIs.read(tmp);				
			}

			value = baoStream.toString(encoding);
		}
		
		baoStream.flush();
		baoStream.close();

		IDal dal = Application.getDal(session);

		dal.getUserPrefHandler().putPref(userUID, keyId, value, contentType, isFileInt, destFilePath);

		return Response.noContent().build();	//status code = 204
	}
	

	private static String getFileExtensionFromContentType(String contentType) {

		String extension;	    
		if (contentType.startsWith("application/zip")) {
			extension = "zip";
		} else if (contentType.startsWith("application/pdf")) {
			extension = "pdf";
		} else if (contentType.startsWith("image/gif")) {
			extension = "gif";
		} else if (contentType.startsWith("image/jpeg")) {
			extension = "jpg";
		} else if (contentType.startsWith("image/png")) {
			extension = "png";
		} else {
			extension = "dat";
		}
			
		return extension;
	}
	
	
	private static boolean isContentBinary(String contentType) {
		if (contentType.startsWith("text")
				|| contentType.startsWith("application/json")
				|| contentType.startsWith("application/xml")) {
			return false;
		} else {
			return true;
		}
	}

}
