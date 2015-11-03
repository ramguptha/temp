/**
 * 
 */
package com.absolute.am.webapi.controllers;

import java.util.Arrays;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.IJobStatus.JobStatusDetails;
import com.absolute.am.model.JobStatusResult;
import com.absolute.am.webapi.model.exception.BadRequestException;
import com.absolute.am.webapi.model.exception.NotFoundException;
import com.absolute.am.webapi.util.ResourceUtilities;


/**
 * <h3>Job API</h3>
 * <p>The Job API is used to query the status of long running jobs. As an example, the /api/content/batch endpoint will start a long running job
 *    in the background and the client can later query the status of that job using this endpoint.</p>
 * <p>Any endpoint that that starts a long running job will return a job id to the client. This job id is then used to query the status.</p>
 * 
 * @author klavin
 */
@Path ("/job")
public class Job {
	@SuppressWarnings("unused")
	private static Logger m_logger = LoggerFactory.getLogger(Job.class.getName()); 
    
    private static final String m_Base = ResourceUtilities.WEBAPI_BASE;

	/**
	 * The servlet request. This is injected by JAX-RS when the object is created.
	 */
	private @Context HttpServletRequest m_servletRequest;
	
	private HttpSession m_session;

	/**
	 * <p>This endpoint returns the status of the job identified by id. The job’s percent complete value is returned to the client.</p>
	 * 
	 * <p>Example JSON response:</p>
	 * <pre>
	 *{
	 * &emsp;"percentComplete":14
	 *}
	 * </pre>
	 * 
	 * <p>If the job processing resulted in an error, then that error will be returned as if it originated from this endpoint.
	 *    Refer to the Status codes section of the endpoint that initiated the job for details on the possible error status codes. </p>
	 * 
	 * <p>Rights required:</br>
	 *    None</p>
	 *    
	 * @param jobId jobId
	 * @return Returns the status of the job identified by id
	 * @throws Exception
	 */
	@GET @Path("/{id}/status")
	@Produces({ MediaType.APPLICATION_JSON })
	public JobStatusResult getStatusForJob(
			@Context UriInfo ui,
			@PathParam("id") String jobId) throws Exception {
		MDC.put("jobId", jobId);
		m_session = m_servletRequest.getSession();
		String locale = SessionState.getLocale(m_session);
		JobStatusResult jobStatus = null; 
		JobStatusDetails jobInfo =	Application.getJobStatusMgr().getJobStatusDetails(jobId);
		if (jobInfo == null) {
			throw new NotFoundException("JOB_JOB_NOT_FOUND", null, locale, m_Base, "id", jobId);
		}
		// Check the session id, to make sure this job is for the session.
		String[] jobIdsForSession = SessionState.getJobIds(m_session);
		if (!Arrays.asList(jobIdsForSession).contains(jobId)) {
			throw new BadRequestException("JOB_JOB_DOES_NOT_BELONG_TO_THIS_SESSION", null, locale, m_Base, "id", jobId);
		}
		if (jobInfo.getException() != null) {
			throw jobInfo.getException();
		}
		jobStatus = new JobStatusResult();
		jobStatus.setPercentComplete(jobInfo.getPercentCmpl());
		MDC.remove("jobId");
		return jobStatus;
	}

}
