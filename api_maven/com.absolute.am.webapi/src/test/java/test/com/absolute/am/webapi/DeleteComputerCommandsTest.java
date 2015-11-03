/**
 * 
 */
package test.com.absolute.am.webapi;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Date;

import static org.junit.Assert.assertEquals;

import org.apache.http.client.ClientProtocolException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testdata.configuration.Computers;
import test.com.absolute.testutil.Helpers;

import com.absolute.util.StringUtilities;

/**
 * @author ephilippov
 *
 */
public class DeleteComputerCommandsTest extends LoggedInTest {
	private static final String
			SEND_MESSAGE_COMMAND_API       = COMPUTER_COMMANDS_API + "/sendmessage",
			DELETE_QUEUED_COMMAND_API      = COMPUTER_COMMANDS_API + "/queued/delete", 
			DELETE_HIST_COMMAND_API        = COMPUTER_COMMANDS_API + "/history/delete",
			QUEUED_COMMANDS_VIEW_ALL       = COMPUTER_COMMANDS_API + "/queued/views/all",
			HIST_COMMANDS_VIEW_ALL         = COMPUTER_COMMANDS_API + "/history/views/all";
	
	private int commandId;
	private String commandName;
	private String[] testDeviceSerial;
	
	@Test
	@Category(com.absolute.util.helper.SlowTest.class)
	public void test_master_list() throws Exception {
		
		setup();
		
		// test deleting the command from command history
		can_view_command_histories(commandName, HIST_COMMANDS_VIEW_ALL);
		can_successfully_delete_command(DELETE_HIST_COMMAND_API, HIST_COMMANDS_VIEW_ALL);
		
		// test deleting a queued command
		can_view_queued_commands(commandName, QUEUED_COMMANDS_VIEW_ALL);
		can_successfully_delete_command(DELETE_QUEUED_COMMAND_API, QUEUED_COMMANDS_VIEW_ALL);
	}

	private void setup() throws Exception{
		commandName = "Send Message";
		testDeviceSerial = Helpers.getComputerSerialsForComputerNames(logonCookie, Computers.COMPUTER_NAMES[0]);
		send_message_to_device();
		// sleep to let the sync service catch up
		Thread.sleep(10000);
	}
	
	private void send_message_to_device() throws Exception {
		
		StringBuilder sb = new StringBuilder();

		sb.append("{");
		sb.append("\"serialNumbers\":[\"");
		sb.append(testDeviceSerial[0]);
		sb.append("\"],");
		sb.append("\"message\":\"Hello from test.com.absolute.am.webapi test at " + StringUtilities.toISO8601W3CString(new Date()) + ".\"");
		sb.append("}");
		
		Helpers.postJsonRequestAndGetResult(logonCookie, Helpers.WEBAPI_BASE_URL + SEND_MESSAGE_COMMAND_API, sb.toString());
	}

	public void can_view_queued_commands(String commandNameInList, String urlCommand) 
			throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {
		
		String deviceName = "";
		Object[] rows = Helpers.get_rows_from_query_url(logonCookie, Helpers.WEBAPI_BASE_URL + urlCommand);
		
		for(int i=0; i<rows.length; i++){
			String commandName2 = "";
			@SuppressWarnings("unchecked")
			ArrayList<Object> row = (ArrayList<Object>)rows[i];
			
			String id = row.get(1).toString();
			if (row.get(3) != null) {
				commandName2 = row.get(3).toString();
			}
			if (row.get(4) != null) {
				deviceName = row.get(4).toString();
			}
			
			if (commandName2.equalsIgnoreCase(commandName) && deviceName.equalsIgnoreCase(Computers.COMPUTER_NAMES[0])){
				commandId = Integer.parseInt(id.toString());
				break;
			}
		}
		
		assertEquals(Computers.COMPUTER_NAMES[0], deviceName);
		assertEquals(commandNameInList, commandName);
		Assert.assertTrue(commandId > 0);
	}
	
	public void can_view_command_histories(String commandNameInList, String urlCommand) 
			throws KeyManagementException, ClientProtocolException, NoSuchAlgorithmException, IOException {
		
		String deviceName = "";
		Object[] rows = Helpers.get_rows_from_query_url(logonCookie, Helpers.WEBAPI_BASE_URL + urlCommand);
		
		for(int i=0; i<rows.length; i++){
			String commandName2 = "";
			@SuppressWarnings("unchecked")
			ArrayList<Object> row = (ArrayList<Object>)rows[i];
			
			String id = row.get(1).toString();
			if (row.get(4) != null) {
				commandName2 = row.get(4).toString();
			}
			if (row.get(5) != null) {
				deviceName = row.get(5).toString();
			}
			
			if (commandName2.equalsIgnoreCase(commandName) && deviceName.equalsIgnoreCase(Computers.COMPUTER_NAMES[0])){
				commandId = Integer.parseInt(id.toString());
				break;
			}
		}
		
		assertEquals(Computers.COMPUTER_NAMES[0], deviceName);
		assertEquals(commandNameInList, commandName);
		Assert.assertTrue(commandId > 0);
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
		
		Helpers.postJsonRequestGetResultCheckStatus(logonCookie, Helpers.WEBAPI_BASE_URL + urlCommand, sb.toString(), 200,209);
		
		// sleep to let the sync service catch up
		Thread.sleep(10000);
		
		Object[] rows = Helpers.get_rows_from_query_url(logonCookie, Helpers.WEBAPI_BASE_URL + urlCommandViewAll);
		
		// let's search all the rows in case some additional queued commands have shown up
		for(int i=0; i<rows.length; i++){
			String commandId2 = "";
			if (((ArrayList<Object>)rows[i]).get(0) != null){
				commandId2 = ((ArrayList<Object>)rows[i]).get(0).toString();
			}
			Assert.assertNotEquals(commandId2, String.valueOf(commandId));
		}
	}
}
