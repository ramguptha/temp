package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IActionHandler;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.model.MobileAction;

public class TestActionHandler {
	private static final long ACTION_ID = 1;  
	private static final String ACTION_UNIQUE_ID_FOR_TEST = "61D64847-E92B-4012-BB7C-A152622EB919";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_action() throws Exception {
		IDal dal = Util.getDal();
		
		IActionHandler actionHandler = dal.getActionHandler();

		MobileAction action = actionHandler.getAction(ACTION_ID);
		assertTrue(action.getId() == ACTION_ID);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_action_uuid_for_action_name() throws Exception {
		String actionNames = "test";
		IDal dal = Util.getDal();
		IActionHandler actionHandler = dal.getActionHandler();

		String[] actionUuids = actionHandler.getActionUniqueIdsForActionNames(actionNames);
		
		assertTrue(actionUuids.length == 1);
		assertTrue(actionUuids[0].compareToIgnoreCase(ACTION_UNIQUE_ID_FOR_TEST) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_action_uuids() throws Exception {
		List<Long> actionIds = Arrays.asList(ACTION_ID);
		
		IDal dal = Util.getDal();
		IActionHandler actionHandler = dal.getActionHandler();

		String[] actionUuids = actionHandler.getActionUniqueIds(actionIds);
		
		assertTrue(actionUuids.length == 1);
		assertTrue(actionUuids[0].compareToIgnoreCase(ACTION_UNIQUE_ID_FOR_TEST) == 0);
	}
}
