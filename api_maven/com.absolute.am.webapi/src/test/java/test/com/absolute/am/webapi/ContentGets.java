/**
 * 
 */
package test.com.absolute.am.webapi;

import java.util.ArrayList;

import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.model.Result;
import com.absolute.util.StringUtilities;

import test.com.absolute.testdata.configuration.ContentFiles;
import test.com.absolute.testdata.configuration.Policies;
import test.com.absolute.testutil.Helpers;


import static org.junit.Assert.*;


/**
 * @author dlavin
 *
 */
public class ContentGets extends LoggedInTest {
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_policies_for_content() throws Exception {
		String contentIds[] = Helpers.getContentIdsForContentNames(logonCookie, ContentFiles.CONTENT_FILE_NAMES[0]);
		can_get_policies_for_content(contentIds[0]);
	}
	
	public void can_get_policies_for_content(String contentId) throws Exception {
					
		String endpoint = Helpers.WEBAPI_BASE_URL + CONTENT_API + "/" + contentId + "/policies";
		String viewResult = Helpers.doGETCheckStatusReturnBody(logonCookie, endpoint, 200, 200);
				
		assertNotNull(viewResult);
		System.out.println(endpoint + " result = " + viewResult);
		
		assertTrue("result contains " + Policies.STANDARD_POLICY_NAMES[0], viewResult.contains(Policies.STANDARD_POLICY_NAMES[0]));
		assertTrue("result contains " + Policies.STANDARD_POLICY_NAMES[1], viewResult.contains(Policies.STANDARD_POLICY_NAMES[1]));
		assertTrue("result contains " + Policies.STANDARD_POLICY_NAMES[2], viewResult.contains(Policies.STANDARD_POLICY_NAMES[2]));

		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(viewResult, Result.class);
		assertTrue("check that the result meta data has at least one column", result.getMetaData().getColumnMetaData().size() > 0);
		assertTrue("check that the result has at least one row", result.getRows().length > 0);
		
		@SuppressWarnings("unchecked")
		ArrayList<Object> row = (ArrayList<Object>)result.getRows()[0];
		String row0AsString = StringUtilities.CollectionToString(row, ",");
		System.out.println("row[0]=" + row0AsString);
		
	}

	/**
	 * There is no real basis for the value of the timeout, other than the first time I ran this test it took 61.4 seconds for 1500 repetitions.
	 * If the test duration is exceeded, it doesn't necessarily mean that there is something wrong, just that the result should be reviewed.  
	 * @throws Exception
	 */
	@Test(timeout=250000)
	@Category(com.absolute.util.helper.FastTest.class)
	public void repeat_can_get_policies_for_content() throws Exception {
		
		String contentIds[] = Helpers.getContentIdsForContentNames(logonCookie, ContentFiles.CONTENT_FILE_NAMES[0]);
		
		for (int i=0; i<500; i++) {
			System.out.println("Iteration " + i);
			can_get_policies_for_content(contentIds[0]);
		}
	}

}
