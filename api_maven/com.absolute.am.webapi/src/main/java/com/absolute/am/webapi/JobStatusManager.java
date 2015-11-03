package com.absolute.am.webapi;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


public class JobStatusManager implements IJobStatus{
	private Map<String, JobStatusDetails> m_jobs = new HashMap<String, IJobStatus.JobStatusDetails>();

	@Override
	public synchronized String createJobStatusDetails() {
		String jobId = null;
		jobId = UUID.randomUUID().toString();
		JobStatusDetails jobStatusDetails = new JobStatusDetails();
		m_jobs.put(jobId, jobStatusDetails);
		return jobId;
	}
	

	@Override
	public synchronized JobStatusDetails getJobStatusDetails(String jobId) {
		JobStatusDetails jobStatusDetails = null;
		jobStatusDetails = m_jobs.get(jobId);
		return jobStatusDetails;
	}

	@Override
	public synchronized boolean updateJobStatusDetails(String jobId,
			JobStatusDetails jobStatusDetails) {
		boolean updated = false;
		if (m_jobs.containsKey(jobId)) {
			m_jobs.put(jobId, jobStatusDetails);
			updated = true;
		}
		return updated;
	}

	@Override
	public synchronized boolean deleteJobStatusDetails(String jobId) {
		boolean deleted = false;
		if (m_jobs.containsKey(jobId)) {
			m_jobs.remove(jobId);
			deleted = true;
		}
		return deleted;
	}

}
