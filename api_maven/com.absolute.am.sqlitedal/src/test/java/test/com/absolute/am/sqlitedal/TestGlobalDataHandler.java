package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IGlobalDataHandler;

public class TestGlobalDataHandler {
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_data_schema_version() throws Exception {
		IDal dal = Util.getDal();
		
		IGlobalDataHandler globalDataHandler = dal.getGlobalDataHandler();

		int version = globalDataHandler.getDatabaseSchemaVersion();
		assertTrue(version > 0);
	}
}
