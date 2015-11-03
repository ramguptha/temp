package test.com.absolute.am.sqlitedal;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.ICustomFieldHandler;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.model.CustomField;

public class TestCustomFieldHandler {
	private static final String CUSTOM_FIELD_UUID = "244918A6-5EF0-4754-B3DC-287002CDD5C5";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_custom_fields() throws Exception {
		IDal dal = Util.getDal();
		ICustomFieldHandler customFieldHandler = dal.getCustomFieldHandler();

		ArrayList<CustomField> customFieldList = customFieldHandler.getCustomFields();
		assertTrue(customFieldList.size() > 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_custom_fields_with_field_actions() throws Exception {
		IDal dal = Util.getDal();
		ICustomFieldHandler customFieldHandler = dal.getCustomFieldHandler();

		ArrayList<CustomField> customFieldList = customFieldHandler.getCustomFieldsWithActions();
		assertTrue(customFieldList.size() > 0);
		assertTrue(((CustomField) customFieldList.toArray()[0]).customFieldActionDefinitions.size() > 0);
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_custom_field() throws Exception {
		IDal dal = Util.getDal();
		ICustomFieldHandler customFieldHandler = dal.getCustomFieldHandler();

		CustomField customField = customFieldHandler.getCustomField(CUSTOM_FIELD_UUID);
		assertNotNull(customField);
		assertNotNull(customField.id.compareToIgnoreCase(CUSTOM_FIELD_UUID) == 0);
	}
}
