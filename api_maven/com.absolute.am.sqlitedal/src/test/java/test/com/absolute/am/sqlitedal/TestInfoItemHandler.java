package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IInfoItemHandler;

public class TestInfoItemHandler {
	@SuppressWarnings("unchecked")
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_custom_fields_with_field_actions() throws Exception {
		IDal dal = Util.getDal();
		IInfoItemHandler infoItemHandler = dal.getInfoItemHandler();

		ArrayList<Map<String, String>> customFieldInfoItemList = infoItemHandler.getCustomInfoItemInfo(true);
		assertNotNull(customFieldInfoItemList);
		assertTrue(customFieldInfoItemList.size() > 0);
		assertTrue(((Map<String,String>) customFieldInfoItemList.toArray()[0]).get("id").toString().length() > 0);
	}
}
