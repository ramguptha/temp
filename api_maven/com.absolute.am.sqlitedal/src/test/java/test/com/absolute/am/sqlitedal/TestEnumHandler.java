package test.com.absolute.am.sqlitedal;

import java.util.ArrayList;
import java.util.Map;

import org.junit.Test;

import com.absolute.am.dal.IDal;
import com.absolute.am.dal.IEnumHandler;

import static org.junit.Assert.*;

import org.junit.experimental.categories.Category;

public class TestEnumHandler {
	
	private static final String ENUM_TABLE_NAME_AGENT_PLATFORM = "enum_AgentPlatform";
	private static final String LOCALE_SUFFIX_ENGLISH = "_en";
	private static final String LOCALE_SHORT_NAME_ENGLISH = "en";
    private static final String MAP_NAME = "name";
    private static final String MAP_VALUE = "value";
    private static final String ENUM_VALUE_AGENT_PLATFORM_MAC_OS_X = "Mac OS X";
    private static final String ENUM_KEY_AGENT_PLATFORM_1 = "1";

	// Note: These tests are running against a saved local copy of AdminDatabase. 
	// So device id=15 exists in that database and its got policies 1 and 28
	// assigned to it.

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_values_for_agent_platform() throws Exception {
		IDal dal = Util.getDal();

		IEnumHandler enumHandler = dal.getEnumHandler();

		ArrayList<Map<String, String>> enumValues = enumHandler.getValuesForTable(ENUM_TABLE_NAME_AGENT_PLATFORM, LOCALE_SUFFIX_ENGLISH);
		assertNotNull(enumValues);
		assertTrue(enumValues.size() > 0);
		assertTrue(arrayListMapContains(enumValues, ENUM_VALUE_AGENT_PLATFORM_MAC_OS_X, ENUM_KEY_AGENT_PLATFORM_1));
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_enum_key_for_value() throws Exception {
		IDal dal = Util.getDal();

		IEnumHandler enumHandler = dal.getEnumHandler();

		String key = enumHandler.getEnumKeyForValue(ENUM_TABLE_NAME_AGENT_PLATFORM, ENUM_VALUE_AGENT_PLATFORM_MAC_OS_X);
		assertNotNull(key);
		assertTrue(key.compareToIgnoreCase(ENUM_KEY_AGENT_PLATFORM_1) == 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_enum_value_for_key() throws Exception {
		IDal dal = Util.getDal();

		IEnumHandler enumHandler = dal.getEnumHandler();

		String val = enumHandler.getEnumValueForKey(ENUM_TABLE_NAME_AGENT_PLATFORM, ENUM_KEY_AGENT_PLATFORM_1);
		assertNotNull(val);
		assertTrue(val.compareToIgnoreCase(ENUM_VALUE_AGENT_PLATFORM_MAC_OS_X) == 0);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_enum_value_for_key_and_locale() throws Exception {
		IDal dal = Util.getDal();

		IEnumHandler enumHandler = dal.getEnumHandler();

		String val = enumHandler.getEnumValueForKey(ENUM_TABLE_NAME_AGENT_PLATFORM, ENUM_KEY_AGENT_PLATFORM_1, LOCALE_SHORT_NAME_ENGLISH);
		assertNotNull(val);
		assertTrue(val.compareToIgnoreCase(ENUM_VALUE_AGENT_PLATFORM_MAC_OS_X) == 0);
	}
	
	private boolean arrayListMapContains(ArrayList<Map<String, String>> arrayListMap, String name_value, String value_value) {
		boolean containsEntry = false;
		for (int i = 0; i < arrayListMap.size(); i++) {
			Map<String, String> map = arrayListMap.get(i);
			if (map.get(MAP_NAME).compareToIgnoreCase(name_value) == 0 &&
				map.get(MAP_VALUE).compareToIgnoreCase(value_value) == 0) {
					containsEntry = true;
					break;
				}
		}
		return containsEntry;
	}
}
