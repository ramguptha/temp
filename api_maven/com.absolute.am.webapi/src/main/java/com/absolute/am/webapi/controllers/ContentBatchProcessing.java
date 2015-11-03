package com.absolute.am.webapi.controllers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.CPLATPassword;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraCommandDefs;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.command.IProgressReporter;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IPolicyHandler;
import com.absolute.am.dal.model.MobileMedia;
import com.absolute.am.dal.model.iOsPolicies;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.IJobStatus.JobStatusDetails;
import com.absolute.am.model.JobTask;
import com.absolute.am.model.content.BatchFileUpload;
import com.absolute.am.model.content.FileInfo;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.model.exception.InternalServerErrorException;
import com.absolute.am.model.policy.PolicyAssignment;
import com.absolute.am.webapi.util.ResourceUtilities;
import com.absolute.util.FileUtilities;
import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;

public class ContentBatchProcessing implements Runnable, IProgressReporter{
    private static Logger m_logger = LoggerFactory.getLogger(ContentBatchProcessing.class.getName()); 
    
    private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	private static final String STATIC_ICONS_DEFAULT_ICON_SIZE = "64";

	private BatchFileUpload m_batchFileUpload;
	private AMServerProtocol m_amServerProtocol;
	private ServletContext m_sc;
	private UUID m_adminUuid;
	private Object m_syncServiceSession;
	private IDal m_dal;
	private HttpSession m_session;
	private Map<String, FileUploadStatus> m_fileUploadStatus;
	
	protected String m_jobId;
	protected JobTask m_rootTask;
	protected JobTask m_currentTask;
	protected JobStatusDetails m_jobInfo;


	public ContentBatchProcessing(HttpServletRequest servletRequest, String jobId, BatchFileUpload batchFileUpload) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		m_jobId = jobId;
		m_batchFileUpload = batchFileUpload;
		m_amServerProtocol = new AMServerProtocol(SessionState.getAMServerProtocolSettings(servletRequest.getSession()));
		m_sc = servletRequest.getServletContext();
		m_adminUuid = SessionState.getAdminUUID(servletRequest.getSession());
		m_syncServiceSession = SessionState.getSyncServiceSession(servletRequest.getSession());
		m_session = servletRequest.getSession();
		m_dal = Application.getDal(m_session);
		// copy the fileUploadStatus to local storage and take ownership of it.
		// Delete the fileUploadStatus from the session to prevent the TempFileCleanerSessionListener
		// from cleaning it up before its been processed
		m_fileUploadStatus = new HashMap<String, FileUploadStatus>();
		FileInfo[] newFiles = m_batchFileUpload.getNewFiles();
		for(int i=0; i<newFiles.length; i++) {				
			FileUploadStatus fileUploadStatus = SessionState.getFileUploadStatus(servletRequest.getSession(), newFiles[i].getFileName());
			m_fileUploadStatus.put(newFiles[i].getFileName(), fileUploadStatus);
			SessionState.setFileUploadStatus(servletRequest.getSession(), newFiles[i].getFileName(), null);
		}
		m_jobInfo = Application.getJobStatusMgr().getJobStatusDetails(m_jobId);
	}

	@Override
	public void run() {
		// Algorithm
		// For each file
		//	upload the file to AM (1 AM Server command).
		//  save the unique id of the file in the list of files
		// For each policy assignment
		//  assign list of media to policy (1 AM Server command).
		//  set availability time for list of media, for this policy (1 AM Server Command).

		// percent = 0, nrOfSubtasks = 2
		m_rootTask = new JobTask("ContentBatch", 0, 2);
		
		try {
			// TODO: validation - check that the referenced file exists in the uploads folder
			// TODO: validation - if a file with the same name already exists, the upload will fail. Provide an endpoint to easily check this.
			FileInfo[] newFiles = m_batchFileUpload.getNewFiles();
			UUID[] newMediaUUIDs = new UUID[newFiles.length];
			m_logger.debug("newFiles[].length={}", newFiles.length);	

			HttpSession session = this.m_session;
			String locale = SessionState.getLocale(session);
			String contextMessage;
			
			// For each file
			//	upload the file to AM (1 AM Server command).
			//  save the unique id of the file in the list of files
			
			// percent = 0, nrOfSubtasks = newFiles.length
			JobTask fileTask = new JobTask("FileTask", 0, newFiles.length);
			m_rootTask.setSubtask(0, fileTask);

			int fileIndex = 0;
			int policyIndex = 0;
			for(fileIndex = 0; fileIndex < newFiles.length; fileIndex++) {
				// percent = 0, nrOfSubtasks = 2
				JobTask fileSubTask = new JobTask("FileSubTask_" + fileIndex+1, 0, 2);
				fileTask.setSubtask(fileIndex, fileSubTask);

				m_logger.debug("newFiles[{}].getDisplayName()={}", fileIndex, newFiles[fileIndex].getDisplayName());
				UUID newMediaUUID = UUID.randomUUID();
				newMediaUUIDs[fileIndex] = newMediaUUID;
				
				String fileName = newFiles[fileIndex].getFileName();
				FileUploadStatus fileUploadStatus = m_fileUploadStatus.get(fileName);

				MobileMedia mediaInfo = new MobileMedia();
				mediaInfo.setUniqueId(newMediaUUID.toString());
				mediaInfo.setFilename(fileName);
				// FileType is normalized. AbsoluteSafe uses this value to pick the appropriate viewer.
				mediaInfo.setFileType(
						Content.getNormalizedFileType(newFiles[fileIndex].getFileType()));
				mediaInfo.setFileModDate(newFiles[fileIndex].getFileModDate());
				mediaInfo.setFileSize(fileUploadStatus.getTotalLength());
				mediaInfo.setDisplayName(newFiles[fileIndex].getDisplayName());
				mediaInfo.setDescription(newFiles[fileIndex].getDescription());
				// because we use the file category in the file path to load the icon, it has to be checked for path characters (E.g. ..) to prevent
				// access to the rest of the file system.
				if (Content.hasIllegalPathChars(newFiles[fileIndex].getCategory())) {
					throw new BadRequestException("CONTENTBATCHPROCESSING_FILE_CATEGORY_HAS_ILLEGAL_CHARACTERS", null, locale, m_Base, 
							"category", newFiles[fileIndex].getCategory());
				}
				mediaInfo.setIcon(
						loadIconForFileTypeOrCategory(m_sc, 
								Content.getNormalizedFileType(newFiles[fileIndex].getFileType()), 
								newFiles[fileIndex].getCategory(), locale));
				mediaInfo.setFileMD5(StringUtilities.toHexString(fileUploadStatus.getMessageDigest().digest()).toLowerCase());// AM uses lowercase, so we do too.
				mediaInfo.setCategory(newFiles[fileIndex].getCategory());
				mediaInfo.setTransferOnWifiOnly(newFiles[fileIndex].isTransferOnWifiOnly());
				mediaInfo.setSeed(1);
				
				if (newFiles[fileIndex].getPassphrase() != null && newFiles[fileIndex].getPassphrase().length() > 0) {
					mediaInfo.setPassPhraseHash(CPLATPassword.Hash(newFiles[fileIndex].getPassphrase()));
					mediaInfo.setCanEmail(false);
					mediaInfo.setCanPrint(false);
					mediaInfo.setCanLeaveApp(false);
				} else {
					mediaInfo.setPassPhraseHash("");
					mediaInfo.setCanEmail(newFiles[fileIndex].isCanEmail());
					mediaInfo.setCanPrint(newFiles[fileIndex].isCanPrint());
					mediaInfo.setCanLeaveApp(newFiles[fileIndex].isCanLeaveApp());
				}
				String randomString = StringUtilities.generateRandomString(StringUtilities.DEFAULT_RANDOM_PASSWORD_CHARSET, 16);
				CPLATPassword encryptionKey = new CPLATPassword(randomString);
				mediaInfo.setEncryptionKey(encryptionKey.Encrypt(CobraCommandDefs.kBlowfishMDMPayloadKey.getBytes("UTF-8")));

				CobraAdminMiscDatabaseCommand addMediaCommand = CommandFactory.createAddMediaCommand(
						mediaInfo, m_adminUuid);								
				
				//m_logger.debug("addMediaCommand={}", addMediaCommand.ToXml());
				// percent = 0, nrOfSubtasks = 0
				m_currentTask = new JobTask("File.AddMedia" + fileIndex+1, 0, 0);
				fileSubTask.setSubtask(0, m_currentTask);
				contextMessage = ResourceUtilities.getLocalizedFormattedString(
						"CONTENTBATCHPROCESSING_ADD_CONTENT_DESCRIPTION_FAILED", 
						new String[]{newFiles[fileIndex].getDisplayName()}, 
						locale, m_Base);
				m_amServerProtocol.sendCommandAndValidateResponse(addMediaCommand, contextMessage, this);

				// percent = 0, nrOfSubtasks = 0
				m_currentTask = new JobTask("File.SendFile" + fileIndex+1, 0, 0);
				fileSubTask.setSubtask(1, m_currentTask);
				m_amServerProtocol.sendFile(fileUploadStatus.getLocalFilePath(), this);
				contextMessage = ResourceUtilities.getLocalizedFormattedString(
						"CONTENTBATCHPROCESSING_ADD_CONTENTS_FAILED", 
						new String[]{mediaInfo.getDisplayName()}, 
						locale, m_Base);
				PropertyList finalResult = m_amServerProtocol.getAndValidateResponse(contextMessage);

				// TODO: provide the final result to the sync service, so it can update the database
				m_logger.debug("Added media, finalResult={}", finalResult.toXMLString());
				
				// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
				try {
					// If the session has ended this will throw an exception.
					// Just catch the exception rather than keep expensive bookkeeping
					// to decide if session is still active.
					Application.getSyncService().prioritySync( 
							m_syncServiceSession, finalResult);
				} catch (Exception e) {
					e.printStackTrace();
				}
				
				// delete the local copy of the file.
				File localFile = new File(fileUploadStatus.getLocalFilePath());
				if (!localFile.delete()) {
					m_logger.debug("Failed to delete temp file [{}].", fileUploadStatus.getLocalFilePath());
				}
				m_fileUploadStatus.remove(newFiles[fileIndex].getFileName());
			}
			
			PolicyAssignment[] assignToPolicies = m_batchFileUpload.getAssignToPolicies();
			m_logger.debug("assignToPolicies[]={} length={}", assignToPolicies, assignToPolicies.length);
			if (assignToPolicies != null) {
				// percent = 0, nrOfSubtasks = assignToPolicies.length
				JobTask policiesTask = new JobTask("PoliciesTask", 0, assignToPolicies.length);
				m_rootTask.setSubtask(1, policiesTask);

				// For each policy assignment
				//  assign list of media to policy (1 AM Server command).
				//  set availability time for list of media, for this policy (1 AM Server Command).

				String[] policyUUIDs = new String[assignToPolicies.length];
				String[] policyNames = new String[assignToPolicies.length];
									
				IPolicyHandler policyHandler = m_dal.getPolicyHandler();
				for (int i = 0; i < assignToPolicies.length; i++) {
					long policyId = assignToPolicies[i].getPolicyId();
	
					iOsPolicies policy = policyHandler.getPolicy(policyId);
					if (policy == null) {
						throw new BadRequestException("CONTENTBATCHPROCESSING_NO_POLICY_FOUND_FOR_POLICYID", new Object[]{policyId}, locale, m_Base);
					}
					policyUUIDs[i] = policy.getUniqueId();
					policyNames[i] = policy.getName();
				}

				for (policyIndex = 0; policyIndex < assignToPolicies.length; policyIndex++) {
					// percent = 0, nrOfSubtasks = 2
					JobTask policySubTask = new JobTask("PolicySubTask_" + policyIndex+1, 0, 2);
					policiesTask.setSubtask(policyIndex, policySubTask);
					
					MDC.put("assignToPolicies", 
							String.format("Policy [%1$s] uniqueID=%2$s assignmentType=%3$s availabilitySelector=%4$s startTime=%5$s endTime=%6$s.", 
									policyIndex,
							policyUUIDs[policyIndex],
							assignToPolicies[policyIndex].getAssignmentType(),
							assignToPolicies[policyIndex].getAvailabilitySelector(),
							assignToPolicies[policyIndex].getStartTime(),
							assignToPolicies[policyIndex].getEndTime()
							));
								
					CobraAdminMiscDatabaseCommand assignToPolicyCommand = CommandFactory.createAssignMediaToPolicyCommand(
							newMediaUUIDs, 
							UUID.fromString(policyUUIDs[policyIndex]),
							assignToPolicies[policyIndex].getAssignmentType(),
							m_adminUuid);
						
					//m_logger.debug("assignMediaToPolicyCommand={}", assignToPolicyCommand.ToXml());
					
					// percent = 0, nrOfSubtasks = 0
					m_currentTask = new JobTask("Policy.AssignMedia" + policyIndex+1, 0, 0);
					policySubTask.setSubtask(0, m_currentTask);
					contextMessage = ResourceUtilities.getLocalizedFormattedString(
							"CONTENTBATCHPROCESSING_ASSIGN_CONTENT_TO_POLICY_FAILED", 
							new String[]{policyNames[policyIndex]}, 
							locale, m_Base);
					PropertyList assignToPolicyResult = m_amServerProtocol.sendCommandAndValidateResponse(assignToPolicyCommand, 
							contextMessage, this);
					//m_logger.debug("Assign content to policy result={}",  assignToPolicyResult.toXMLString());

					
					// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
					try {
						// If the session has ended this will throw an exception.
						// Just catch the exception rather than keep expensive bookkeeping
						// to decide if session is still active.
						Application.getSyncService().prioritySync( 
								m_syncServiceSession, assignToPolicyResult);
					} catch (Exception e) {
						m_logger.debug("Ignoring exception reported by prioritySync() in background job. Ex={}", e);
					}						
	
					// TODO: If the AvailabilitySelector is the default value, then we don't need to do anything here.
					CobraAdminMiscDatabaseCommand setAvailabilityTimeCommand = CommandFactory.createSetAvailabilityTimeForPolicyMediaCommand(
							newMediaUUIDs, 
							UUID.fromString(policyUUIDs[policyIndex]),
							assignToPolicies[policyIndex].getAvailabilitySelector(),
							assignToPolicies[policyIndex].getStartTime(),
							assignToPolicies[policyIndex].getEndTime(),						
							m_adminUuid);
						
					// percent = 0, nrOfSubtasks = 0
					m_currentTask = new JobTask("Policy.SetAvail" + policyIndex+1, 0, 0);
					policySubTask.setSubtask(1, m_currentTask);

					//m_logger.debug("Set availability command={}", setAvailabilityTimeCommand.ToXml());
					contextMessage = ResourceUtilities.getLocalizedFormattedString(
							"CONTENTBATCHPROCESSING_SET_AVAILABILITY_TIME_FOR_POLICY_FAILED", 
							new String[]{policyNames[policyIndex]}, 
							locale, m_Base);
					PropertyList setAvailabilityTimeResult = m_amServerProtocol.sendCommandAndValidateResponse(setAvailabilityTimeCommand, contextMessage, this);
					//m_logger.debug("Set availability time result={}",  setAvailabilityTimeResult.toXMLString());

					// When successful, the SyncService is instructed to update it's tables based on the changes described in the result.
					try {
						// If the session has ended this will throw an exception.
						// Just catch the exception rather than keep expensive bookkeeping
						// to decide if session is still active.
						Application.getSyncService().prioritySync( 
								m_syncServiceSession, setAvailabilityTimeResult);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
				MDC.remove("assignToPolicies");
			}
			m_jobInfo.setPercentCmpl(100);
			Application.getJobStatusMgr().updateJobStatusDetails(m_jobId, m_jobInfo);

		} catch (Exception e) {
			m_jobInfo.setException(e);
			Application.getJobStatusMgr().updateJobStatusDetails(m_jobId, m_jobInfo);
		} finally {
			try {
				if (m_amServerProtocol != null) {
					m_amServerProtocol.close();
				}
			} catch (IOException e) {
				m_jobInfo.setException(e);
				Application.getJobStatusMgr().updateJobStatusDetails(m_jobId, m_jobInfo);
			}

			// clean up temp files.
			Iterator<Entry<String, FileUploadStatus>> entries = m_fileUploadStatus.entrySet().iterator();
			while (entries.hasNext()) {
				Entry<String, FileUploadStatus> thisEntry = (Entry<String, FileUploadStatus>) entries.next();
				FileUploadStatus fileUploadStatus = (FileUploadStatus) thisEntry.getValue();
				File localFile = new File(fileUploadStatus.getLocalFilePath());
				if (!localFile.delete()) {
					m_logger.debug("Failed to delete temp file [{}].", fileUploadStatus.getLocalFilePath());
				}
			}
		}
	} 

	@Override
	public void reportProgress(int percent) {
		m_currentTask.setPercent(percent);
		int Taskpercent = m_rootTask.calculatePercentCmpl();
		m_jobInfo.setPercentCmpl(Taskpercent);
		Application.getJobStatusMgr().updateJobStatusDetails(m_jobId, m_jobInfo);
	}	
	
	/**
	 * Helper method to load the icon for a fileType or fileCategory. This icon data is then uploaded the the 
	 * AM server as part of the process to add content/media. 
	 * @param servletContext - the servlet context, used to map relative/absolute paths.
	 * @param fileType - the type of the file
	 * @param fileCategory - an optional category for the file e.g. documents. 
	 * @param locale  - the locale of the current session
	 * @return the loaded file.
	 * @throws IOException
	 */
	private static byte[] loadIconForFileTypeOrCategory(
			ServletContext servletContext,
			String fileType,
			String fileCategory, 
			String locale) throws IOException {
		
		String relativePath = Content.getRelativePathToIconForFileTypeOrCategory(servletContext, fileType, fileCategory, STATIC_ICONS_DEFAULT_ICON_SIZE);
		if (relativePath == null) {
			throw new InternalServerErrorException("SERVER_ERROR", "CONTENTBATCHPROCESSING_FAILED_TO_FIND_ICON_FOR_FILE", null, locale, m_Base,
					"fileType", fileType, "fileCategory", fileCategory);
		}
					
		String iconFilePath = servletContext.getRealPath(relativePath);
		byte[] retVal = FileUtilities.loadFile(iconFilePath);
		return retVal;				
	}

}
