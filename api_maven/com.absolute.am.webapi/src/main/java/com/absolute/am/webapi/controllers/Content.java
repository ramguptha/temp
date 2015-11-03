/**
 * 
 */
package com.absolute.am.webapi.controllers;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.DigestOutputStream;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.enunciate.jaxrs.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.xml.sax.SAXException;

import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.annotations.Right;
import com.absolute.am.webapi.annotations.Right.AMRight;
import com.absolute.am.model.JobIdResult;
import com.absolute.am.model.ViewDescription;
import com.absolute.am.model.ViewDescriptionList;
import com.absolute.am.model.Result;
import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.CPLATPassword;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.ColumnConstants;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IPolicyHandler;
import com.absolute.am.dal.model.MobileMedia;
import com.absolute.am.dal.model.iOsPolicies;
import com.absolute.am.model.content.BatchFileUpload;
import com.absolute.am.model.content.ContentList;
import com.absolute.am.model.content.FileInfo;
import com.absolute.am.webapi.model.exception.AMWebAPILocalizedException;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.model.exception.InternalServerErrorException;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.model.exception.WebAPIException;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.am.webapi.util.ViewUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;


/**
 * <h3>User API</h3>
 * <p>This API is used to manage mobile content (files).</p>
 * 
 * @author dlavin
 *
 */
@Path ("/content")
public class Content {
	
    private static Logger m_logger = LoggerFactory.getLogger(Content.class.getName());
    
    private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	private static final String HTTP_HEADER_CONTENT_RANGE = "Content-Range";
	private static final String INI_FOLDER_FOR_TEMPORARY_UPLOADS = "com.absolute.webapi.controllers.content.folderForTemporaryUploads";
	private static final String VIEW_NAME_ONE_MOBILE_CONTENT = "onemobilecontent";
	private static final String VIEW_NAME_POLICIES_FOR_CONTENT = "policiesforcontent";
	private static final String VIEW_NAME_DEVICES_FOR_POLICIES_FOR_CONTENT = "devicesforpoliciesforcontent";
	private static final String VIEW_NAME_ALL_MOBILE_CONTENT = "allmobilecontent";	

	// Constants used to find fileType/fileCategory icons.
	private static final String STATIC_ICONS_FILETYPES_FORMAT_PATH = "/static/icons/filetypes/%1$s-%2$s.png";
	private static final String STATIC_ICONS_CATEGORIES_FORMAT_PATH = "/static/icons/categories/%1$s-%2$s.png";
	private static final String STATIC_ICONS_DEFAULT_FORMAT_PATH = "/static/icons/%1$s-default.png";
	
	private HttpSession m_session;
	
	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	private static final ViewDescription[] m_viewDescriptions = new ViewDescription[] {
		new ViewDescription(ViewHelper.VH_VIEW_NAME_ALL, "All Mobile Content", 0),
	};

    private static final ExecutorService m_executor = Executors.newCachedThreadPool();
    
	/**
	 * <p>Get a list of named views available for mobile content. Please refer to <strong>/api/mobiledevices/views</strong> –
	 *    GET for an example of the response to this request.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @return Get a list of named views available for mobile content end-point.
	 * @throws Exception 
	 */
	@GET @Path("/views")
	@Produces({ MediaType.APPLICATION_JSON })
	public ViewDescriptionList getViewsForContent() throws Exception  {
		ViewDescriptionList result = new ViewDescriptionList();	
		result.setViewDescriptions(m_viewDescriptions);
		
		return result;
	}
	
	/**
	 * <p>Returns a list of the content files existing on the mobile devices. The result is a multi-row result set, but the exact content depends on the definition of the view.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param viewname The given <strong>viewname</strong>
	 * @return Returns a list of the content files.
	 * @throws Exception 
	 */
	@GET @Path("/views/{viewname}")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "The <strong>viewName</strong> is not found.")
		})
	public Result getView(@PathParam("viewname") String viewname,
			@Context UriInfo ui) throws Exception {
		
		MDC.put("viewname", viewname);
		m_logger.debug("Content.getView called");
		m_session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(m_session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(m_session);
		
		if (!ViewUtilities.isValidViewName(viewname, m_viewDescriptions)) {
			throw new NotFoundException("VIEW_NOT_FOUND", null, locale, m_Base, "viewname", viewname);
		}
		
		if (viewname.compareToIgnoreCase(ViewHelper.VH_VIEW_NAME_ALL) == 0) {
			viewname = VIEW_NAME_ALL_MOBILE_CONTENT;
		}

		Result result = null;
		IDal dal = Application.getDal(m_session);

		result = ViewHelper.getViewDetails(
					dal,
					viewname,
					ui.getQueryParameters(),
					null,
					dbLocaleSuffix);
		
		MDC.remove("viewname");
		
		return result;
	}
	
	/**
	 * <p>Upload a portion of a file. The Content-Range HTTP header is used to determine what section of the file is being uploaded. 
	 *    See http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html for details. Here is an example (taken from that reference) of what that header
	 *    will look like for a file 1234 bytes long that is uploaded in 500 byte chunks:</p>
	 * <pre>
	 * The first 500 bytes:
	 * &emsp; bytes 0-499/1234
	 * The second 500 bytes:
	 * &emsp;bytes 500-999/1234
	 * All except for the first 500 bytes:
	 * &emsp; bytes 500-1233/1234
	 * The last 500 bytes:
	 * &emsp; bytes 734-1233/1234
	 * </pre>
	 *    
	 * <p>Rights required:</br>
	 *    AllowModifyMobileMedia </p>
	 *    
	 * @param fileName The given file name
	 * @param fileChunkIs InputStream
	 * @return
	 * @throws Exception
	 */
	@POST @Path("/upload/{fileName}")
	@Consumes(MediaType.APPLICATION_OCTET_STREAM)
	@Right(AMRight.AllowModifyMobileMedia)
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "When the offset is > 0 but this is the first block that the server has received."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public void postTempFileChunk(
			@PathParam("fileName") String fileName,
			InputStream fileChunkIs) throws Exception  {
		
		// TODO: validate the filename, don't allow names that are too long
		MDC.put("fileName", fileName);
		m_session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(m_session);
		
		long startTimeMilliSeconds = System.currentTimeMillis();
		// The content uploader relies on the Content-Range HTTP header. 
		// See http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html for the details.
		String contentRange = m_servletRequest.getHeader(HTTP_HEADER_CONTENT_RANGE);
		MDC.put("contentRange", contentRange);
		// Parse the offset, length and total length from the content range header
		// Here are some examples for the Content-Range header (http://www.w3.org/Protocols/rfc2616/rfc2616-sec14.html):
		//		The first 500 bytes:
		//	    	bytes 0-499/1234
		//		The second 500 bytes:
		//			bytes 500-999/1234
		//		All except for the first 500 bytes:
		//			bytes 500-1233/1234
		//		The last 500 bytes:
		//			bytes 734-1233/1234		
		
		Scanner scanner = new Scanner(contentRange);
		String pattern = "\\s|[\\-]+|/";

		scanner.useDelimiter(pattern);
		scanner.next();
		long offset = scanner.nextLong();

		long chunkEndOffset = scanner.nextLong();
		long totalSize = scanner.nextLong();
		scanner.close();

		// Can upload files of zero length
		if (!(totalSize >= 0 && offset >= 0 && chunkEndOffset >= 0)) {
			throw new BadRequestException("CONTENT_CONTENT_RANGE_IS_NOT_VALID", null, locale, m_Base);
		}

		FileUploadStatus fileUploadStatus = SessionState.getFileUploadStatus(m_session, fileName);
		
		// Sanity check: if the offset > 0 then we should already have a fileUploadStatus in this session
		if (offset != 0 && null == fileUploadStatus) {	
			throw new BadRequestException("CONTENT_UPLOAD_CHUNKS_MUST_START_AT_OFFSET_ZERO", null, locale, m_Base);
		}
	
		boolean firstChunkAndFileNameMatchesAnExistingDisplayName = false;
		
		// This is the first chunk for this file and it is the first time we have received it.
		if (offset == 0) {
			
			if (fileUploadStatus != null) {
				// the upload was started previously. Discard it and start again.
				File localFile = new File(fileUploadStatus.getLocalFilePath());
				if (localFile.exists()) {
					if (!localFile.delete()) {
						m_logger.debug("Failed to delete temp file [{}].", fileUploadStatus.getLocalFilePath());
					}
				}

				SessionState.setFileUploadStatus(m_session, fileName, null);
				fileUploadStatus = null;
			}
			
			String tempUploadFolder = m_servletRequest.getServletContext().getInitParameter(INI_FOLDER_FOR_TEMPORARY_UPLOADS);
			if (null == tempUploadFolder) {
				throw new InternalServerErrorException("CONFIGURATION_ERROR", "SETTING_NOT_FOUND", new Object[]{INI_FOLDER_FOR_TEMPORARY_UPLOADS}, locale, m_Base);
			}
			StringBuilder pathStringBuilder = new StringBuilder(tempUploadFolder);
			
			if (!tempUploadFolder.endsWith("\\") && !tempUploadFolder.endsWith("/")) {
				pathStringBuilder.append("/");
			}
			pathStringBuilder.append(UUID.randomUUID().toString());
			MDC.put("pathToLocalFile", pathStringBuilder.toString());
			
			fileUploadStatus = new FileUploadStatus(pathStringBuilder.toString(), totalSize, 0, MessageDigest.getInstance("MD5"));
			// Create the initial empty file.
			File theFile = new File(fileUploadStatus.getLocalFilePath());
			if ( theFile.exists() ) {
				theFile.delete();
			}
			theFile.createNewFile();

			// Check if a file with DisplayName equal to this filename already exists in the database.
			// If it does, a status code of 205 Reset Content will be returned. This is a success code, so
			// the upload can continue. But it gives the client the opportunity to customize the DisplayName
			// before finalizing the content upload.
			IDal dal = Application.getDal(m_session);
			MobileMedia mobileMedia = dal
					.getContentHandler()
					.getContentByDisplayName(fileName); 
			if ( mobileMedia != null) {
				firstChunkAndFileNameMatchesAnExistingDisplayName = true;
				m_logger.debug("file with same displayName({}) found, id={}", fileName, mobileMedia.getId());
			}
		}
		// If the offset doesn't match, then this is a repeat of a chunk we already received.
		if (offset != fileUploadStatus.getCurrentLength()) {
			m_logger.debug("offset({}) != fileUploadStatus.getCurrentLength({})", offset, fileUploadStatus.getCurrentLength());			
			
			// read all of the input data and discard it
			byte[] tmp = new byte[1024];
			while(fileChunkIs.read(tmp) != -1) {
				// discard it.
			}
		} else {
			//TODO: Append the posted data to the end of the file.
			FileOutputStream fos = new FileOutputStream(fileUploadStatus.getLocalFilePath(), true); // true means open for append
			DigestOutputStream dos = new DigestOutputStream(fos, fileUploadStatus.getMessageDigest());
			

			int len=0;
			byte[] tmp = new byte[1024];
			len = fileChunkIs.read(tmp);
			while(len != -1) {
				dos.write(tmp, 0, len);
				len = fileChunkIs.read(tmp);				
			}
			
			fos.flush();
			fos.close();

			// After the block has been uploaded and saved, update the attribute
			fileUploadStatus.setCurrentLength(chunkEndOffset + 1);
			SessionState.setFileUploadStatus(m_session, fileName, fileUploadStatus);
		}
		
		long duration = System.currentTimeMillis() - startTimeMilliSeconds;

		// There have been some reports of excessive delays in this method. 
		if (m_logger.isDebugEnabled() && duration > 50) {
			m_logger.debug("postTempFileChunk completed in {}ms. firstChunkAndFileNameMatchesAnExistingDisplayName={}", duration, firstChunkAndFileNameMatchesAnExistingDisplayName);
		}
		MDC.remove("fileName");
		MDC.remove("contentRange");

		if (firstChunkAndFileNameMatchesAnExistingDisplayName) {
			// This is not a fatal error, but the client needs to know. 
			// The default DisplayName for a file is the FileName. The AM Server enforces a uniqueness constraint
			// on the DisplayName. When a file with a DisplayName that matches this FileName already exists on the
			// server, this status code enables the client to offer the end-user the option to customize the
			// DisplayName. 
			// 205 is Reset Content: it is definitely not perfect code, but it is also not worth defining a custom
			// code for this situation. There is no Response.Status.ResetContent, so using 205 directly.
			throw new WebApplicationException(205); 
		}
	}

	/**
	 * <p>Used to confirm that the end point is responding.</p>
	 * 
	 * @param fileName The given file name
	 * @return Input Stream
	 * @throws FileNotFoundException 
	 * @throws UnsupportedEncodingException 
	 */
	@GET @Path("/upload/{fileName}")
	@Produces({ MediaType.APPLICATION_OCTET_STREAM })
	public InputStream getTempFile(
			@PathParam("fileName") String fileName
			) throws FileNotFoundException, UnsupportedEncodingException {

		// TODO: validate the filename, don't allow names that are too long
		MDC.put("fileName", fileName);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		FileUploadStatus fileUploadStatus = SessionState.getFileUploadStatus(session, fileName);
		
		// The file was never uploaded.
		if (null == fileUploadStatus ) {
			throw new NotFoundException("CONTENT_TEMP_UPLOAD_FILE_NOT_FOUND", null, locale, m_Base, "fileName", fileName);
		}
		
		// The file is not complete
		if (fileUploadStatus.getCurrentLength() != fileUploadStatus.getTotalLength()) {
			m_logger.error("Attempt to get file that has not been 100% uploaded. fileName={} getCurrentLength()={} getTotalLength()={}",
					fileName, fileUploadStatus.getCurrentLength(), fileUploadStatus.getTotalLength());
			throw new NotFoundException("CONTENT_CANNOT_GET_FILE_THAT_IS_ONLY_PARTIALLY_UPLOADED", null, locale, m_Base, "fileName", fileName);
		}

		FileInputStream fis = new FileInputStream(fileUploadStatus.getLocalFilePath());
		MDC.remove("fileName");
		
		return fis;
	}	

	
	/**
	 * <p>Get the attributes of the file identified by {id}. The response is a single row ResultSet with meta data. 
	 *    It includes the following attributes: File Name, Display Name, Category, File Type, Size, Description, Passphrase, 
	 *    File Can Leave AbsoluteSafe, Can E-mail, Can Print, Can Download Over Wi-Fi only.</p>
	 * <p>The content of the file itself is not retrievable.</p>
	 * <p>The icon associated with the file is retrieved from a separate endpoint, /api/content/{id}/icon.</p>
	 * <p>The list of associated policies is available at /api/content/{id}/policies.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 *    
	 * @param mediaId the given file id
	 * @return Returns all attributes of a media file.
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@GET @Path("/{id}")
	@Produces({ MediaType.APPLICATION_JSON })
	public Result getContentForId(
			@Context UriInfo ui,
			@PathParam("id") String mediaId) throws Exception  {
		
		MDC.put("mediaId", mediaId);	
		m_logger.debug("Content.getContentForId called");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);
		
		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(mediaId);

		Result result = null;
		IDal dal = Application.getDal(session);

		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_ONE_MOBILE_CONTENT, 
				null,
				userParams,
				dbLocaleSuffix);
			
		MDC.remove("mediaId");
		
		if (result.getRows().length < 1) {
			throw new NotFoundException("CONTENT_CONTENT_NOT_FOUND", null, locale, m_Base, "id", mediaId);
		}
		
		return result;
	}	
	
	/**
	 * <p>Update the attributes of the file identified by {id}. The body of the POST is a JSON representation of the updated file attributes.</p>
	 * <p>Example body of POST:</p>
	 * <pre>
	 * {
	 * &emsp;"id":376,
	 * &emsp;"seed":7,
	 * &emsp;"fileName":"MyMediaFile.pdf",
	 * &emsp;"displayName":"My Media File",
	 * &emsp;"description":"This is a fake media file.",
	 * &emsp;"category":"Documents",
	 * &emsp;"fileModDate":"2012-10-12T22:39:31Z",
	 * &emsp;"fileSize":7168,
	 * &emsp;"fileType":"PDF",
	 * &emsp;"canLeaveApp":true,
	 * &emsp;"canEmail":false,
	 * &emsp;"canPrint":false,
	 * &emsp;"transferOnWifiOnly":true,
	 * &emsp;"passphrase":"topsecret"
	 * }
	 * </pre>
	 * <p>The <strong>seed</strong> value is a version number for the object. 
	 *    If the object has been changed on the server since the client retrieved it, the POST will fail with HTTP Status <strong>409 Conflict</strong>.</p>
	 * <p>If the passphrase is being changed, then the passphrase should be the plain text passphrase and the server will convert it and store it as a hash. 
	 *    If the passphrase is not being changed, then the passphrase should be the same value as originally retrieved from the server (which will actually be a hash).</p>
	 * <p>The passphrase field should be set to an empty string to remove passphrase protection from the file.</p>
	 * <p>Business rules prevent some attributes from being updated. Changes to these will be silently ignored. The list of modifiable attributes include: displayName, description,
	 *    category, canLeaveApp, canEmail, canPrint, transferOnWifiOnly, passphrase.</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyMobileMedia </p>
	 *    
	 * @param mediaId the given file id
	 * @param newFileInfo FileInfo
	 * @return Updates attributes of a media file.
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@POST @Path("/{id}")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyMobileMedia)
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "A file with this {id} could not be found."),
		  @ResponseCode ( code = 409, condition = "The seed value of the record in the database has changed since editing started.")
		})
	public void updateContentForId(
			@Context UriInfo ui,
			@PathParam("id") long mediaId,
			FileInfo newFileInfo) throws Exception  {
		
		// TBD: do we want to support uploading/replacing the content? The Admin Console allows this (with restrictions, e.g. can’t change the file type).
		// It could be indicated by changing the filename parameter.
				

		MDC.put("mediaId", "" + mediaId);
		m_logger.debug("Content.updateContentForId called");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		IDal dal = Application.getDal(session);
		MobileMedia oldMobileMedia = dal.getContentHandler().getContent(mediaId);

		if (oldMobileMedia == null) {
			throw new NotFoundException("CONTENT_CONTENT_ITEM_WAS_NOT_FOUND", null, locale, m_Base, "id", mediaId);
		}
		if (oldMobileMedia.getSeed() != newFileInfo.getSeed()) {
			throw new WebAPIException(Response.Status.CONFLICT, "CONTENT_THIS_CONTENT_ITEM_HAS_BEEN_MODIFIED_BY_ANOTHER_USER",
					"SEED_MISMATCH", null, locale, m_Base, "Id", mediaId);
		}
		
		
		String newContentName= newFileInfo.getDisplayName();
		if (!newContentName.equals(oldMobileMedia.getDisplayName()) && 0 != ResourceUtilities.getNonUniqueDataItemCount(SessionState.getLocaleDbSuffix(session), Application.getDal(session),
				newContentName, VIEW_NAME_ALL_MOBILE_CONTENT, ColumnConstants.COLUMN_INFO_ITEM_ID_MOBILE_MEDIA_DISPLAY_NAME))
		{
			throw new WebAPIException(Response.Status.CONFLICT, "CONTENT_NAME_ALREADY_EXISTS",
					"DUPLICATE_NAME", null, locale, m_Base);
		}
		MobileMedia newMobileMedia = new MobileMedia();
		newMobileMedia.setSeed(newFileInfo.getSeed());		
		newMobileMedia.setDisplayName(newFileInfo.getDisplayName());
		newMobileMedia.setDescription(newFileInfo.getDescription());
		newMobileMedia.setCategory(newFileInfo.getCategory());

		// fileTypes are normalized. AbsoluteSafe uses this type (not the file extension) to determine
		// what viewer to use.
		newMobileMedia.setTransferOnWifiOnly(newFileInfo.isTransferOnWifiOnly());
		// copy these over from oldMobileMedia
		// NOTE: for release 1, we don't allow the user to replace
		// the content of the file, all they can do is edit the file metadata
		// So for now, just copy missing metadata from oldMobileMedia
		// In a later release the file content can be replaced so this code will
		// need to change.
		newMobileMedia.setFilename(oldMobileMedia.getFilename());
		newMobileMedia.setFileModDate(oldMobileMedia.getFileModDate());	
		newMobileMedia.setFileType(oldMobileMedia.getFileType());		
		newMobileMedia.setEncryptionKey(oldMobileMedia.getEncryptionKey());
		newMobileMedia.setFileMD5(oldMobileMedia.getFileMD5());
		newMobileMedia.setIcon(oldMobileMedia.getIcon());
		newMobileMedia.setId(oldMobileMedia.getId());
		newMobileMedia.setLastModified(oldMobileMedia.getLastModified());
		newMobileMedia.setUniqueId(oldMobileMedia.getUniqueId());
		newMobileMedia.setFileSize(oldMobileMedia.getFileSize());
		
		String passphrase = newFileInfo.getPassphrase();
		if (passphrase == null || passphrase.equals(oldMobileMedia.getPassPhraseHash())) {
			newMobileMedia.setPassPhraseHash(oldMobileMedia.getPassPhraseHash());
			newMobileMedia.setCanLeaveApp(oldMobileMedia.getCanLeaveApp());
			newMobileMedia.setCanEmail(oldMobileMedia.getCanEmail());
			newMobileMedia.setCanPrint(oldMobileMedia.getCanPrint());
		} else if (passphrase.length() > 0) {
			newMobileMedia.setPassPhraseHash(CPLATPassword.Hash(passphrase));
			newMobileMedia.setCanLeaveApp(false);
			newMobileMedia.setCanEmail(false);
			newMobileMedia.setCanPrint(false);
		} else {
			newMobileMedia.setPassPhraseHash("");
			newMobileMedia.setCanLeaveApp(newFileInfo.isCanLeaveApp());
			newMobileMedia.setCanEmail(newFileInfo.isCanEmail());
			newMobileMedia.setCanPrint(newFileInfo.isCanPrint());
		}
		// NOTE: We may need to process the encryptionKey, like we do in "Add"
		// once update allows the user to choose a different file.
		// Leave that field as is for now.


		CobraAdminMiscDatabaseCommand updateMediaCommand = CommandFactory.createUpdateMediaCommand(
				oldMobileMedia, newMobileMedia, 
				SessionState.getAdminUUID(session));
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getLocalizedFormattedString(
					"CONTENT_UPDATE_CONTENT_FAILED", 
					new String[]{oldMobileMedia.getDisplayName()}, 
					locale, m_Base);
			PropertyList result = amServerProtocol.sendCommandAndValidateResponse(updateMediaCommand, contextMessage);
			amServerProtocol.close();
			
			// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
			Application.getSyncService().prioritySync( 
					SessionState.getSyncServiceSession(session), 
					result);
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
		
		MDC.remove("mediaId");
	}	

	/**
	 * <p>Remove the file identified by {id}. Both the file and the meta data are removed.</p>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyMobileMedia </p>
	 *    
	 * @param mediaId the given file id
	 * @return
	 * @throws GeneralSecurityException 
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 * @throws RuntimeException 
	 * @throws AMServerProtocolException 
	 * @throws FileNotFoundException 
	 */
	@DELETE @Path("/{id}")	
	@Right(AMRight.AllowModifyMobileMedia)
	@StatusCodes ({
		  @ResponseCode ( code = 204, condition = "The file was deleted, there is no content/body in the response."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "There is no content item with this id.")
		})
	public void deleteContentForId(
			@PathParam("id") int mediaId) throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, SAXException, AMServerProtocolException  {
		
		MDC.put("mediaId", "" + mediaId);
		m_logger.debug("Content.deleteContentForId called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		int[] fileIds = new int[] { mediaId };
		CobraAdminMiscDatabaseCommand removeMediaCommand = CommandFactory.createRemoveMediaCommandForFileIds(
				fileIds,
				SessionState.getAdminUUID(session));
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("CONTENT_DELETE_CONTENT_FAILED", m_Base, locale);
			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(removeMediaCommand, contextMessage);
			amServerProtocol.close();

			// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
			Application.getSyncService().prioritySync( 
					SessionState.getSyncServiceSession(session), 
					response );
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
		
		MDC.remove("mediaId");
	}	

	/**
	 * <p>Delete a list of content items. The body of the DELETE is a list of ids. Here is an example:</p>
	 * <pre>
	 * {
	 * &emsp;"contentIds":[1,2,3,4]
	 * }
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    AllowModifyMobileMedia </p>
	 * @param contentList the list of the content files to be deleted   
	 * @return
	 * @throws IOException 
	 * @throws GeneralSecurityException 
	 * @throws RuntimeException 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 * @throws AMServerProtocolException 
	 */
	@POST @Path("/delete")
	@Consumes(MediaType.APPLICATION_JSON)
	@Right(AMRight.AllowModifyMobileMedia)
	@StatusCodes ({
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint."),
		  @ResponseCode ( code = 404, condition = "One or more of the content items does not exist.")
		})
	public void deleteContentForIdList(
			ContentList contentList) throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, SAXException, AMServerProtocolException {
		
		MDC.put("contentList", contentList.toString());		
		m_logger.debug("deleteContentForIdList called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		// TODO: add the array to the MDC in a manner that supports logging (e.g. convert to a string).
		int[] idArr = contentList.getContentIds();
		if (idArr.length <= 0) {
			throw new BadRequestException("CONTENT_MUST_SPECIFY_CONTENT_IDS_TO_DELETE", null, locale, m_Base, "contentList", contentList);
		}
		
		CobraAdminMiscDatabaseCommand removeMediaCommand = CommandFactory.createRemoveMediaCommandForFileIds(
				idArr,
				SessionState.getAdminUUID(session));
		
		AMServerProtocol amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(session));
		try {
			String contextMessage = ResourceUtilities.getResourceStringForLocale("CONTENT_DELETE_CONTENT_FAILED", m_Base, locale);
			PropertyList response = amServerProtocol.sendCommandAndValidateResponse(removeMediaCommand, contextMessage);
			
			amServerProtocol.close();

			// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
			Application.getSyncService().prioritySync( 
					SessionState.getSyncServiceSession(session), 
					response );
		} catch (AMWebAPILocalizableException e) {
			//localize and re-throw
			AMWebAPILocalizedException ex = new AMWebAPILocalizedException(e,  locale, "/command/Command");
			throw ex;
		} finally {
			amServerProtocol.close();
		}
		
		MDC.remove("contentList");
	}
	
	
	/**
	 * 3.6.5	/api/content/{id}/icon – GET
	 * TODO
	 * TBD: Not Implemented yet.
	 * 
	 * Returns the icon associated with the file identified by {id}. Icons are in PNG format and will be returned with Content-Type: image/png.
	 * 
	 * Status codes:
	 * 404 Not Found: there is no content item with this id.
	 * 
	 * @return
	 */

	
	/**
	 * <p>Returns the list of policies that this content is associated with. The response is a multi-row <strong>ResultSet</strong> with meta data. 
	 *    The response includes the following attributes: Policy Name, Availability, Availability Start Time, Availability End Time.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param mediaId the given file id
	 * @return Returns the list of policies that this content is associated with
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@GET @Path("/{id}/policies")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no content item with this id.")
		})
	public Result getPoliciesForId(
			@Context UriInfo ui,
			@PathParam("id") long mediaId) throws Exception {
		
		MDC.put("mediaId", "" + mediaId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		String dbLocaleSuffix = SessionState.getLocaleDbSuffix(session);

		Result result = null;
		IDal dal = Application.getDal(session);

		String uniqueID = null;
		MobileMedia mobileMedia = dal.getContentHandler().getContent(mediaId);
		if (mobileMedia != null) {
			uniqueID = mobileMedia.getUniqueId();
		}

		if (uniqueID == null) {
			throw new NotFoundException("CONTENT_THERE_IS_NO_CONTENT_ITEM_FOR_THIS_ID", null, locale, m_Base, "id", mediaId);
		}			
	
		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(uniqueID);
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_POLICIES_FOR_CONTENT, 
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("mediaId");
		return result;
	}	

	/**
	 * <p>Returns a list of devices that this content is available on. The response is a multi-row <strong>ResultSet</strong> with meta data. 
	 *    The response includes the following attributes: Mobile Device Name, Mobile Device Model, Mobile Device OS Version, Mobile Device Serial Number, 
	 *    Mobile Device Phone Number, Mobile Device Last Contact Time, Mobile Device OS Build Number, Mobile Device SIM ICC Identifier, Mobile Device IMEI.</p>
	 * 
	 * <p>Rights required:</br>
	 *    None </p>
	 * 
	 * @param mediaId the given file id
	 * @return Returns a list of devices that this content is available on.
	 * @throws Exception 
	 */
	@GET @Path("/{id}/devices")
	@Produces({ MediaType.APPLICATION_JSON })
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "There is no content item with this id.")
		})
	public Result getDevicesForId(
			@Context UriInfo ui,
			@PathParam("id") long mediaId) throws Exception {

		MDC.put("mediaId", "" + mediaId);
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session),
				dbLocaleSuffix = SessionState.getLocaleDbSuffix(session),
				uniqueID = null;
		Result result = null;
		IDal dal = Application.getDal(session);
		MobileMedia mobileMedia = dal.getContentHandler().getContent(mediaId);
		
		if (mobileMedia != null) {
			uniqueID = mobileMedia.getUniqueId();
		}

		if (uniqueID == null) {
			throw new NotFoundException("CONTENT_THERE_IS_NO_CONTENT_ITEM_FOR_THIS_ID", null, locale, m_Base, "id", mediaId);
		}
		
		IPolicyHandler policyHandler = dal.getPolicyHandler();
		String policyIds = policyHandler.getPolicyIdsForMediaAsString(uniqueID);
		StringBuilder smartPolicyIds = new StringBuilder(), nonSmartPolicyIds = new StringBuilder();
		
		if (policyIds != null && policyIds.length() > 0) {
			String[] ids = policyIds.split(",");
			
			for (int i = 0; i < ids.length; i++) {
				iOsPolicies policy = policyHandler.getPolicy(Long.parseLong(ids[i]));
				String policyQuery = policy.getFilterQuery();
				
				if (policyQuery == null || policyQuery.isEmpty()) {
					if (nonSmartPolicyIds.length() > 0) {
						nonSmartPolicyIds.append(",");
					}
					nonSmartPolicyIds.append(ids[i]);
				} else {
					// TODO When we fully support smart policies this SQL will hopefully 
					// disappear. Don't use this example as a blueprint for a way forward.
					if (!policyQuery.contains("__iphone_info_id")) {
						throw new RuntimeException(ResourceUtilities.getResourceStringForLocale("CONTENT_POLICY_FILTERQUERY_DOES_NOT_CONTAIN_IPHONE_INFO_ID", m_Base, locale));
					}
					if (smartPolicyIds.length() > 0) {
						smartPolicyIds.append(" UNION ");
					}
					StringBuilder filterQuery = new StringBuilder();
					filterQuery.append("select __iphone_info_id as id from (");
					filterQuery.append(policy.getFilterQuery());
					filterQuery.append(")");
					smartPolicyIds.append(filterQuery);
				}
			}
		}

		ArrayList<String> userParams = new ArrayList<String>();
		userParams.add(nonSmartPolicyIds.toString());
		userParams.add(smartPolicyIds.toString());
		
		result = ViewHelper.getViewDetails(
				dal,
				VIEW_NAME_DEVICES_FOR_POLICIES_FOR_CONTENT,
				ui.getQueryParameters(),
				userParams,
				dbLocaleSuffix);
		
		MDC.remove("mediaId");
		return result;
	}
	
	public static boolean hasIllegalPathChars(String input) {
		// Allow / and \ but don't allow ... because it could lead to directory browsing
		if (input.indexOf("..") != -1) {
			return true;
		}
		return false;
	}
	
	
	/**
	 * Helper method to find the icon of the correct size to associate with a fileType or fileCategory.
	 * @param request - the request object, used to get access
	 * @param fileType
	 * @param fileCategory
	 * @param iconSize
	 * @return the relative path to the icon file to use OR null if one is not found.
	 */
	public static String getRelativePathToIconForFileTypeOrCategory(
			ServletContext servletContext,
			String fileType, 
			String fileCategory, 
			String iconSize) {
			
		// For consistency we have to normalize the file type first. This makes the icons for the non-normalized file types obsolete, and they will be deleted. 
		fileType = getNormalizedFileType(fileType);
		String relativePath = String.format(STATIC_ICONS_FILETYPES_FORMAT_PATH, iconSize.toLowerCase(), fileType.toLowerCase());
		String absolutePath = servletContext.getRealPath(relativePath);
		File iconFile = new File(absolutePath);
		if (iconFile.exists()) {
			return relativePath;
		}
						
		if (fileCategory != null) {
	
			m_logger.debug("icon file [{}] not found, checking for category icon.", absolutePath);

			relativePath = String.format(STATIC_ICONS_CATEGORIES_FORMAT_PATH, iconSize.toLowerCase(), fileCategory.toLowerCase());
			absolutePath = servletContext.getRealPath(relativePath);
			iconFile = new File(absolutePath);
			if (iconFile.exists()) {
				return relativePath;
			}			
		}
		
		m_logger.debug("icon file [{}] not found, trying default", absolutePath);
		
		relativePath = String.format(STATIC_ICONS_DEFAULT_FORMAT_PATH, iconSize.toLowerCase());
		absolutePath = servletContext.getRealPath(relativePath);
		iconFile = new File(absolutePath);
		if (!iconFile.exists()) {
			m_logger.debug("icon file [{}] not found, giving up.", absolutePath);
			relativePath = null;			
		}
		
		return relativePath;
	}

	/**
	 * <p>Uploads meta data for one or more new files, associates each file with zero or more policies
	 * and assigns the availability period for each policy.</p>
	 * 
	 * <p>The file content must have already been uploaded to /api/content/upload/{fileName},
	 * where {fileName} is the same value provided in the JSON of the body (see definition of <strong>file_*</strong> below).<br/>
	 * After this POST request has been processed successfully, the temporary copy of the file
	 * uploaded to /api/content/upload/{fileName} will be deleted and cannot be referenced in future requests.</p>
	 * 
	 * <p>The structure of the POST request is as follows:</p>
	 * <pre>
	 * {
	 * &emsp;"newFiles":[ {file_1},...
	 * &emsp;             {file_N}
	 * &emsp;           ],
	 * &emsp;"assignToPolicies":[ {policyAssignment_1},...
	 * &emsp;                     {policyAssignment_N}
	 * &emsp;                   ]
	 * }
	 * </pre>
	 * 
	 * <p>The <strong>file_*</strong> objects have the same structure as that used with POST to /api/content/{id}.</p>
	 * 
	 * <p>The <strong>policyAssignment_*</strong> objects have the following structure:</p>
	 * <pre>
	 * {
	 * &emsp;"policyId": 7,
	 * &emsp;"assignmentType": 1,
	 * &emsp;"availabilitySelector": 2,
	 * &emsp;"startTime":"2012-10-18T19:01:00Z",
	 * &emsp;"endTime":"2012-10-19T20:12:00Z"
	 * }
	 * </pre>
	 * 
	 * <p>The <strong>policyId</strong> is the id of the policy entity.</p>
	 * <p>The <strong>assignmentType</strong> value is in the range of 1 to 4, where these values mean:<br />
	 * 1 = On demand, Auto remove (kCobra_iOS_Policy_MediaFile_PolicyOptional), 2 = Auto-install, Auto-remove (kCobra_iOS_Policy_MediaFile_PolicyLocked),
	 * 3 = Auto-install (kCobra_iOS_Policy_MediaFile_Required), 4 = On Demand (kCobra_iOS_Policy_MediaFile_OnDemand).</p>
	 * 
	 * <p>The <strong>availabilitySelector</strong> value can be one of: 0 = Always, 1 = Daily interval, 2 = Fixed period.<br/>
	 * The value chosen determines the expected values for startTime and endTime.</p>
	 * 
	 * <p><strong>startTime</strong> is used when availabilitySelector has values 1 or 2.</p>
	 * 
	 * <p>When availabilitySelector is set to 1, startTime should be of the format HH:MM representing a time of day using a 24 hour clock and in UTC.<br/>
	 * When availabilitySelector is set to 2, startTime should be a full UTC date and time string in ISO-8601 format
	 * (the same as all date strings used throughout this API).</p>
	 * <p><strong>endTime</strong> is used in the same manner as startTime and follows the same rules.</p>
	 * 
	 * <p>A response will be returned immediately. The work to add the content is started in the background.<br />
	 * The response will include a <strong>jobid</strong> that can be used to query the current status (see /api/job/{jobid}/status}.</p>
	 * 
	 * <p>Example JSON response:</p>
	 * <pre>
	 * {
	 * &emsp;"jobid":"6874404A-839C-4046-9706-9CB3B6457B1E"
	 * }
	 * </pre>
	 * 
	 * @summary Batch Upload Files
	 * @param batchFileUpload The files to be uploaded
	 * @return The JobID of the worker thread that is processing the batched data
	 */
	@POST @Path("/batch")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces({ MediaType.APPLICATION_JSON })
	@Right({AMRight.AllowModifyMobileMedia, AMRight.AllowModifyiOSPolicies})
	@StatusCodes ({
		  @ResponseCode ( code = 400, condition = "A referenced file has not been uploaded, a referenced policy does not exist."),
		  @ResponseCode ( code = 403, condition = "The user is not authorized to access this endpoint.")
		})
	public JobIdResult postBatch(BatchFileUpload batchFileUpload) throws Exception  {
		
		m_logger.debug("Content/batch.POST called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);
		
		// Check that each file has already been uploaded
		FileInfo[] newFiles = batchFileUpload.getNewFiles();
		if (newFiles == null) {
			m_logger.debug("Batch upload has no files associated with it.");
			throw new BadRequestException("CONTENT_BATCH_UPLOAD_HAS_NO_FILES_ASSOCIATED_WITH_IT", null, locale, m_Base);
		}
		for(int i=0; i<newFiles.length; i++) {				
			String fileName = newFiles[i].getFileName();
			FileUploadStatus fileUploadStatus = SessionState.getFileUploadStatus(session, fileName);
			if (null == fileUploadStatus) {
				m_logger.debug("File has not been uploaded, name=" + fileName);
				throw new BadRequestException("CONTENT_FILE_HAS_NOT_BEEN_UPLOADED", new Object[]{fileName}, locale, m_Base);
			}
		}
		// All of the files are ok. Spawn a worker thread to do the work and return.
		JobIdResult jobIdResult = new JobIdResult();
		String jobId = Application.getJobStatusMgr().createJobStatusDetails();
		jobIdResult.setJobId(jobId);
		SessionState.setJobId(m_servletRequest.getSession(), jobId);
		m_executor.submit(new ContentBatchProcessing(m_servletRequest, jobId, batchFileUpload));    		
		return jobIdResult;
	}	
	
	/**
	 * <p>This endpoint is used to get the appropriate icon for a file based on file type, category and icon size.</p>
	 * <p><strong>/api/content/icons/{iconSize}-{fileType}.png?category={fileCategory}</strong></p>
	 * 
	 * <p>If an icon of the correct size exists for the given fileType, it will be returned.<br/>
	 * Else, if an icon of the correct size exists for the given fileCategory, it will be returned.<br/>
	 * Else, if a default icon of the correct size exists, it will be returned.<br/>
	 * Else 404.</p>
	 * 
	 * <p>Examples:</p>
	 * <pre>
	 * /api/content/icons/64-jpg.png
	 * /api/content/icons/32-jpg.png?category=pictures
	 * /api/content/icons/64-xyz.png?category=pictures
	 * </pre>
	 * 
	 * <p>Rights required:</br>
	 *    None – any user may access this endpoint.</p>
	 * 
	 * @param iconSize {iconSize} = 16, 32, 64
	 * @param fileType {fileType} = the extension of the file, e.g. txt, pdf, etc. This is <span style="text-decoration: underline;">not</span> case sensitive.
	 * @param fileCategory {fileCategory} = this is optional, and when present it is the category that the user is associating with the content. This is <span style="text-decoration: underline;">not</span> case sensitive.
	 * @return This endpoint is used to get the appropriate icon.
	 * @throws Exception 
	 * @throws FileNotFoundException 
	 */
	@GET @Path("/icons/{iconSize}-{fileType}.png")
	@StatusCodes ({
		  @ResponseCode ( code = 404, condition = "When no suitable icon can be found. This will only occur when the requested {iconSize} is not one of the supported sizes.")
		})
	public Void getIconForFileType(
			@Context HttpServletRequest request,
			@Context HttpServletResponse response,
			@PathParam("iconSize") String iconSize,
			@PathParam("fileType") String fileType,		
			@QueryParam("category") String fileCategory) throws Exception  {
		
		//java.lang.Void - This special return type is used because Jersey incorrectly reports an error
		// when a GET operation has a return type of void (all lowercase).
		
		MDC.put("iconSize", iconSize);
		MDC.put("fileType", fileType);
		MDC.put("category", fileCategory);
		
		m_logger.debug("Content.getIconForFileType called.");
		HttpSession session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(session);

		if (fileCategory != null && hasIllegalPathChars(fileCategory)) {
			m_logger.error("Illegal path charcters in fileCategory [{}].", fileCategory);
			throw new BadRequestException("CONTENT_ILLEGAL_PATH_CHARACTERS_IN_FILECATEGORY", null, locale, m_Base, "fileCategory", fileCategory);
		}

		//String simulatedFileName = "/static/icons/filetypes/txt.png";
		String relativeFilePath = getRelativePathToIconForFileTypeOrCategory(request.getServletContext(), fileType, fileCategory, iconSize);
		if (relativeFilePath == null) {
			throw new NotFoundException("CONTENT_ICON_NOT_FOUND", null, locale, m_Base, "iconSize", iconSize, "fileType", fileType, "category", fileCategory);
		}
		
		request.getRequestDispatcher(relativeFilePath).forward(request, response);
		MDC.remove("iconSize");
		MDC.remove("fileType");
		MDC.remove("category");
		
		return null;
	}	
	
	/**
	 * Helper method to normalize file types. 
	 * See NormalizeMobileMediaFileType in \Cobra\Admin\Source\Common\iOSDevicesInterface.cpp.
	 */
	public static String getNormalizedFileType(String fileType) {
		
		fileType = fileType.toUpperCase();
		if ( fileType.equals("TXT") ) fileType = "TEXT";
		else if ( fileType.equals("JPG") ) fileType = "JPEG";
		else if ( fileType.equals("TIF") ) fileType = "TIFF";
		else if ( fileType.equals("AIF") ) fileType = "AIFF";
		else if ( fileType.equals("HTM") ) fileType = "HTML";
		else if ( fileType.equals("MPG") ) fileType = "MPEG";
		return fileType;
	}
}
