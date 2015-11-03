package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IComputerAdminAccessHandler;
import com.absolute.am.dal.IDal;

public class TestComputerAdminAccessHandler {
	private static final String ADMIN_UUID_FOR_ADMIN_USER = "132E8DCD-18F2-4F8B-ACAF-A25653EC09DF";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_admin_has_access_to_computer() throws Exception {
		IDal dal = Util.getDal();
		IComputerAdminAccessHandler computerHandler = dal.getComputerAdminAccessHandler();

		boolean access = computerHandler.adminHasAccessToComputer(ADMIN_UUID_FOR_ADMIN_USER, 4);
		assertTrue(access);
	}
	
	//adminHasAccessToComputer
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_refresh_access_for_admin() throws Exception {
		IDal dal = Util.getDal();
		IComputerAdminAccessHandler computerHandler = dal.getComputerAdminAccessHandler();

		computerHandler.refreshAccessForAdmin(ADMIN_UUID_FOR_ADMIN_USER);
		
		// this test passes if reach to this point
		assertTrue(true);
	}
}
