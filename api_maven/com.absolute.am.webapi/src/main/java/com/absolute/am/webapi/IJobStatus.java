package com.absolute.am.webapi;


public interface IJobStatus {
	
	/**
	 * Create a new JobStatusDetails and return the jobId
	 * @return
	 */
	public String createJobStatusDetails();

	/**
	 * Return the JobStatusDetails for this jobId
	 * @param jobId
	 * @return
	 */
	public JobStatusDetails getJobStatusDetails(String jobId);
	
	/**
	 * Update the JobStatusDetails for this jobId
	 * @param JobId
	 * @param jobInfo
	 * @return true if the job was updated, false otherwise
	 */
	public boolean updateJobStatusDetails(String jobId, JobStatusDetails jobStatusDetails);
	
	/**
	 * Delete the JobStatusDetails for this job.
	 * @param jobId
	 * @return true if the job was deleted, false otherwise
	 */
	public boolean deleteJobStatusDetails(String jobId);
	
	public class JobStatusDetails {
		private int m_percentCmpl;
		private Exception m_exception;

		public int getPercentCmpl() {
			return m_percentCmpl;
		}
		public void setPercentCmpl(int percentCmpl) {
			if (percentCmpl > m_percentCmpl) {
				this.m_percentCmpl = percentCmpl;
			}
		}
		public Exception getException() {
			return m_exception;
		}
		public void setException(Exception exception) {
			this.m_exception = exception;
		}
	}

}
