package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IUserPrefHandler;
import com.absolute.am.dal.model.UserPreference;

public class TestUserPrefHandler {
	
	private static final String USER_UUID = "4d7da135-dd6f-43b3-aa2b-28524601c6d5";
	private static final String PREFERENCE_KEY = "contentRelatedListColumns";
	private static final String PREFERENCE_KEY_UNIT_TEST = "unit_test_user_pref_20150709";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void test_master_list() throws Exception {
		// the order of the test cases is essential
		can_get_pref();
		can_get_user_pref_list();
		can_put_user_pref();
		can_delete_pref();
	}
	
	private void can_get_pref() throws Exception {
		IDal dal = Util.getDal();
		
		IUserPrefHandler userPrefHandler = dal.getUserPrefHandler();

		UserPreference pref = userPrefHandler.getPref(USER_UUID, PREFERENCE_KEY);
		assertNotNull(pref);
		assertTrue(pref.getName().compareToIgnoreCase(PREFERENCE_KEY) == 0);
	}
	
	private void can_get_user_pref_list() throws Exception {
		IDal dal = Util.getDal();
		
		IUserPrefHandler userPrefHandler = dal.getUserPrefHandler();

		List<UserPreference> pref = userPrefHandler.getUserPrefList(USER_UUID);
		assertNotNull(pref);
		assertTrue(pref.size() > 0);
		assertTrue(((UserPreference)pref.toArray()[0]).getName().length() > 0);
	}

	private void can_put_user_pref() throws Exception {
		IDal dal = Util.getDal();
		
		IUserPrefHandler userPrefHandler = dal.getUserPrefHandler();

		userPrefHandler.putPref(
				USER_UUID, 
				PREFERENCE_KEY_UNIT_TEST,
				"{\"name\":\"unit test user pref 20150709\"}",
				"application/json; charset=utf-8", 
				0,
				""
				);
		
		UserPreference pref = userPrefHandler.getPref(USER_UUID, PREFERENCE_KEY_UNIT_TEST);
		assertNotNull(pref);
		assertTrue(pref.getName().compareToIgnoreCase(PREFERENCE_KEY_UNIT_TEST) == 0);
	}
	
	private void can_delete_pref() throws Exception {
		IDal dal = Util.getDal();
		
		IUserPrefHandler userPrefHandler = dal.getUserPrefHandler();

		userPrefHandler.deletePref(USER_UUID,PREFERENCE_KEY_UNIT_TEST);
		
		UserPreference pref = userPrefHandler.getPref(USER_UUID, PREFERENCE_KEY_UNIT_TEST);
		assertNull(pref);
	}

}
