/**
 * 
 */
package test.com.absolute.am.webapi;

import java.security.MessageDigest;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.model.JobIdResult;
import com.absolute.am.model.JobStatusResult;
import com.absolute.am.model.JobTask;

import test.com.absolute.am.webapi.ContentUpload;
import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;


import static org.junit.Assert.*;


/**
 * @author klavin
 *
 */
public class JobStatusTest extends LoggedInTest {
	private static final String JOB_API = "api/job/";

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_job_task_out_of_range() throws Exception {
		boolean exceptionCaught = false;
		try {
			JobTask jt = new JobTask("Name", 0, 2);
			jt.setSubtask(2,  new JobTask("Another", 10, 2));
		} catch (Exception e) {
			exceptionCaught = true;
		}
		assertTrue(exceptionCaught);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_job_task_percent_complete() throws Exception {
		JobTask job = new JobTask("Job", 0, 2);
		
		JobTask file = new JobTask("File", 0, 2);
		job.setSubtask(0, file);
		
		JobTask file1 = new JobTask("File_1", 0, 2);
		JobTask file2 = new JobTask("File_2", 0, 2);
		file.setSubtask(0, file1);
		file.setSubtask(1, file2);

		JobTask file1_subtask_1 = new JobTask("File_1_subtask_1", 100, 0);
		JobTask file1_subtask_2 = new JobTask("File_1_subtask_2", 100, 0);
		file1.setSubtask(0, file1_subtask_1);
		file1.setSubtask(1, file1_subtask_2);

		JobTask policy = new JobTask("Policy", 0, 2);
		job.setSubtask(1, policy);
		
		int percent = job.calculatePercentCmpl();
		assertEquals(25, percent);
		
		JobTask policy1 = new JobTask("Policy_1", 0, 2);
		JobTask policy2 = new JobTask("Policy_2", 0, 2);
		policy.setSubtask(0, policy1);
		policy.setSubtask(1, policy2);

		JobTask policy1_subtask_1 = new JobTask("Policy_1_subtask_1", 100, 0);
		JobTask policy1_subtask_2 = new JobTask("Policy_1_subtask_2", 100, 0);
		policy1.setSubtask(0, policy1_subtask_1);
		policy1.setSubtask(1, policy1_subtask_2);

		percent = job.calculatePercentCmpl();
		assertEquals(50, percent);
		
		JobTask file2_subtask_1 = new JobTask("File_2_subtask_1", 100, 0);
		JobTask file2_subtask_2 = new JobTask("File_2_subtask_2", 100, 0);
		file2.setSubtask(0, file2_subtask_1);
		file2.setSubtask(1, file2_subtask_2);

		JobTask policy2_subtask_1 = new JobTask("Policy_2_subtask_1", 100, 0);
		JobTask policy2_subtask_2 = new JobTask("Policy_2_subtask_2", 100, 0);
		policy2.setSubtask(0, policy2_subtask_1);
		policy2.setSubtask(1, policy2_subtask_2);

		percent = job.calculatePercentCmpl();
		assertEquals(100, percent);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_status_for_batch() throws Exception {

		String[] remoteFileNames = new String[] {"Big4MB.pdf"};
		String[] displayFileNames = new String[] {"WebAPIUnitTest_Big4MB_job_status.pdf"};

		Helpers.deleteFilesFromSystem(logonCookie, displayFileNames);
		
		String fileUrlLocation = ContentUpload.class.getProtectionDomain().getCodeSource().getLocation().getFile();

		// Upload all of the files.
		MessageDigest digest = MessageDigest.getInstance("MD5");

		String localFilePath = "\\" + fileUrlLocation + "\\..\\..\\src\\test\\resources\\files\\Big4MB.pdf";
		uploadFile(Helpers.createHttpClientWithoutCertificateChecking(), remoteFileNames[0], localFilePath.substring(1), digest);			
			
		String[] policyIds = Helpers.getPolicyIdsForPolicyNames(logonCookie, Policies.STANDARD_POLICY_NAMES[0], Policies.STANDARD_POLICY_NAMES[1]);
		String result = Helpers.addFilesToSystem(logonCookie, remoteFileNames, displayFileNames, policyIds);
		System.out.println("addFilesToSystem Result=" + result);
		ObjectMapper mapper = new ObjectMapper();
		JobIdResult jobId = mapper.readValue(result, JobIdResult.class);
		System.out.println("addFilesToSystem jobId=" + jobId.getJobId());

		Helpers.waitForAdminConsoleToCatchUp();
		
		// TODO: confirm that the uploaded files are available for download.
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + "api/views/allmobilecontent?$top=500", 200, 200);

		System.out.println("view/allmobilecontent Result=" + viewResult);
		int foundInAllMobileContentCount = 0;
		for(String remoteFileName: remoteFileNames) {
			if (viewResult.indexOf(remoteFileName) != -1) {
				foundInAllMobileContentCount++;				
			} else {
				System.out.println("File not found:" + remoteFileName);
			}
			// Also check that the temp file is no longer available.
			// This asserts of an unexpected result code is sent back.
			Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + CONTENT_UPLOAD_API + "/" + remoteFileName, 404, 404);
		}
		
		assertEquals(remoteFileNames.length, foundInAllMobileContentCount);

		result = Helpers.doGETCheckStatusReturnBody(logonCookie, Helpers.WEBAPI_BASE_URL + JOB_API + jobId.getJobId() + "/status", 200, 200);
		mapper = new ObjectMapper();
		JobStatusResult jobStatus = mapper.readValue(result, JobStatusResult.class);
		assertTrue(jobStatus.getPercentComplete() == 100);		
	}
	
}
