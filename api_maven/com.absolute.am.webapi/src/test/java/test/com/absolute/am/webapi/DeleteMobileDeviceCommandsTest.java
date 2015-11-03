/**
 * 
 */
package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertNotEquals;

import org.apache.http.client.ClientProtocolException;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

import com.absolute.am.model.Result;
import com.absolute.util.StringUtilities;

/**
 * @author ephilippov
 *
 */
public class DeleteMobileDeviceCommandsTest extends LoggedInTest {
	private static final String 
			DELETE_QUEUED_COMMAND_API      = COMMANDS_API + "/queued/delete", 
			DELETE_HIST_COMMAND_API        = COMMANDS_API + "/history/delete",
			SEND_MESSAGE_COMMAND_API       = COMMANDS_API + "/sendmessage",
			UPDATE_DEVICE_INFO_COMMAND_API = COMMANDS_API + "/updatedeviceinfo",
			QUEUED_COMMANDS_VIEW_ALL       = COMMANDS_API + "/queued/views/all",
			HIST_COMMANDS_VIEW_ALL         = COMMANDS_API + "/history/views/all";
	
	private int commandId;
	private String[] testDeviceIds;
	
	@Test
	@Category(com.absolute.util.helper.SlowTest.class)
	public void test_master_list() throws Exception {
		
		setup();
		
		// test deleting a history command
		can_view_history_commands();
		can_successfully_delete_command(DELETE_HIST_COMMAND_API, HIST_COMMANDS_VIEW_ALL);
		
		// test deleting a queued command
		can_view_queued_commands();
		can_successfully_delete_command(DELETE_QUEUED_COMMAND_API, QUEUED_COMMANDS_VIEW_ALL);
	}
	
	private void setup() throws Exception{
		
		testDeviceIds = Helpers.getDeviceIdsForDeviceNames(logonCookie, test.com.absolute.testdata.configuration.MobileDevices.MOBILE_DEVICE_NAMES[0]);
		send_message_to_device();
		update_device_info();
		// sleep to let the sync service catch up
		Thread.sleep(10000);
	}
	
	private void send_message_to_device() throws Exception {
		
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(testDeviceIds[0]);
		sb.append("],");
		sb.append("\"message\":\"Hello from test.com.absolute.am.webapi test at " + StringUtilities.toISO8601W3CString(new Date()) + ".\"");
		sb.append("}");
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + SEND_MESSAGE_COMMAND_API, sb.toString());
	}

	private void update_device_info() throws Exception {
		
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		sb.append("\"deviceIds\":[");
		sb.append(testDeviceIds[0]);
		sb.append("]");
		sb.append("}");

		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + UPDATE_DEVICE_INFO_COMMAND_API, sb.toString());
	}
	
	public void can_view_history_commands() throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {

		Object[] rows = get_rows_from_result(Helpers.WEBAPI_BASE_URL + HIST_COMMANDS_VIEW_ALL);
				
		Assert.assertTrue("There should be history commands existing in the system.", rows.length > 0);
	}

	public void can_view_queued_commands() throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {

		Object[] rows = get_rows_from_result(Helpers.WEBAPI_BASE_URL + QUEUED_COMMANDS_VIEW_ALL);
		
		Assert.assertTrue("There should be queued commands existing in the system.", rows.length > 0);
	}
	
	@SuppressWarnings("unchecked")
	public void can_successfully_delete_command(String urlCommand, String urlCommandViewAll) 
			throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException, InterruptedException {
		
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		sb.append("\"commandIds\":[");
		sb.append(commandId);
		sb.append("]");
		sb.append("}");
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + urlCommand, sb.toString(), 200, 209);
				
		// sleep to let the sync service catch up
		Thread.sleep(10000);
		
		Object[] rows = get_rows_from_result(Helpers.WEBAPI_BASE_URL + urlCommandViewAll + "?$orderby=621C7271-7F20-41BB-8ACC-54906673D050%20desc");
		
		// let's search all the rows in case some additional queued commands have shown up
		for(int i=0; i<rows.length; i++){
			assertNotEquals(((ArrayList<Object>)rows[0]).get(0).toString(), String.valueOf(commandId));
		}
	}
	
	private Object[] get_rows_from_result(String URL) throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {
		
		String commandsQueuedView = Helpers.doGETCheckStatusReturnBody(logonCookie, 
				URL, 
				200, 200);
		
		ObjectMapper mapper = new ObjectMapper();
		Result result = mapper.readValue(commandsQueuedView, Result.class);
		
		return result.getRows();
	}
}
