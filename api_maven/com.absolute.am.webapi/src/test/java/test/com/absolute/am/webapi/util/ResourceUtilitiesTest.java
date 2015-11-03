package test.com.absolute.am.webapi.util;

import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.MockDal;
import test.com.absolute.testutil.MockViewHandler;

import com.absolute.util.exception.AMWebAPILocalizableException;
import com.absolute.am.dal.ResultSet;
import com.absolute.am.dal.Row;
import com.absolute.am.webapi.util.ResourceUtilities;

public class ResourceUtilitiesTest {
	
	private static final String supportedLocales = "en,fr,de,es,ja,sv,no";
	private MockDal mockDal;
		
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void master_test() throws Exception, IOException {
		
		init();
		
		can_get_supported_db_suffix();
		can_get_unsupported_db_suffix();
		can_get_resource_string_for_locale();
		can_get_english_only_resource_string_for_locale();
		cannot_get_non_existing_resource();
		can_get_formatted_resource_string_for_locale();
		can_get_language_from_locale();
		cannot_get_language_from_invalid_locale();
		can_get_country_from_locale();
		cannot_check_uniqueness_with_invalid_data_item();
		can_check_uniqueness();
	}
			
	public void cannot_check_uniqueness_with_invalid_data_item() throws Exception {
		
		try {
			ResourceUtilities.getNonUniqueDataItemCount("_en", mockDal, "TestName4", "TEST_VIEW", "NOT_EXISTING_COLUMN_INFO_ITEM_ID");
		} catch (Exception e) {
			assertTrue(e.getMessage().equals("The column info item id was not found within the view."));
		}
	}
	
	public void can_check_uniqueness() throws Exception {
		
		// false for existing name
		assertTrue(0 != ResourceUtilities.getNonUniqueDataItemCount("_en", mockDal, "TestName4", "TEST_VIEW", "4403D9A3-59D0-4D15-BDF5-252AE8CA59A7"));
		
		// true for new name
		assertTrue(0 == ResourceUtilities.getNonUniqueDataItemCount("_en", mockDal, "NonExistingPolicyName", "TEST_VIEW", "4403D9A3-59D0-4D15-BDF5-252AE8CA59A7"));
	}
	
	public void can_get_supported_db_suffix() {
		
		String requestedLocale = "es_MX";
		String expectedDbSuffix = "_es";
		
		String actualDbSuffix = ResourceUtilities.getSupportedLocaleDbSuffix(requestedLocale, supportedLocales);
		
		assertTrue(expectedDbSuffix.equalsIgnoreCase(actualDbSuffix));
	}
	
	public void can_get_unsupported_db_suffix() {
		
		String requestedLocale = "da_DK";
		String expectedDbSuffix = "_en";
		
		String actualDbSuffix = ResourceUtilities.getSupportedLocaleDbSuffix(requestedLocale, supportedLocales);

		assertTrue(expectedDbSuffix.equalsIgnoreCase(actualDbSuffix));
	}
	
	public void can_get_resource_string_for_locale() throws IOException {
		
		String resourceKey = "TEST_STRING";
		String actualString;
		try {
			actualString = ResourceUtilities.getResourceStringForLocale(resourceKey, "files/Test", "ja_JP");
			String expectedString = "test string";// TODO FileUtilities.loadResourceFileAsString("files/String_ja.txt");
			
			assertTrue(expectedString.equalsIgnoreCase(actualString));
		} catch (IOException e) {
			assertTrue("failed to retrieve resource string!", false);
		}
	}
	
	public void can_get_english_only_resource_string_for_locale()  throws IOException {
		
		String expectedString = "this string only exists in English";
		String resourceKey = "TEST_STRING_ENGLISH_ONLY";
		try {
			String actualString = ResourceUtilities.getResourceStringForLocale(resourceKey, "files/Test", "ja_JP");
			assertTrue(expectedString.equalsIgnoreCase(actualString));
		} catch (IOException e) {
			assertTrue("failed to retrieve resource string!", false);
		}
	}
	
	public void cannot_get_non_existing_resource()  throws IOException {
		
		String resourceKey = "NON_EXISTING_RESOURCE_KEY";
		try {
			ResourceUtilities.getResourceStringForLocale(resourceKey, "files/Test", "ja_JP");
		} catch (Exception e) {
			assertTrue("this exception is expected!", true);
		}
	}
	
	public void can_get_formatted_resource_string_for_locale()  throws IOException {
		
		String resourceKey = "TEST_COMPOUND_STRING";
		String actualString;
		try {
			String expectedString = "test message number 5";// TODO FileUtilities.loadResourceFileAsString("/String2_ja.txt");
			actualString = ResourceUtilities.getLocalizedFormattedString(resourceKey, new String[]{"5"}, "ja_JP", "files/Test");			
			assertTrue(expectedString.equalsIgnoreCase(actualString));
		} catch (IOException e) {
			assertTrue("failed to retrieve resource string!", false);
		}
	}
	
	public void can_get_language_from_locale() {
		
		String locale = "es_MX";
		String expectedLanguage = "es";
		
		String actualLanguage = ResourceUtilities.getLanguageFromLocale(locale);
		
		assertTrue(expectedLanguage.equalsIgnoreCase(actualLanguage));
	}
	
	public void cannot_get_language_from_invalid_locale() {
		
		String locale = "invalid_US";
		String expectedLanguage = "en";
		
		String actualLanguage = ResourceUtilities.getLanguageFromLocale(locale);
		
		assertTrue(expectedLanguage.equalsIgnoreCase(actualLanguage));
	}
	
	public void can_get_country_from_locale() {
		
		String locale = "ja-JP";
		String expectedCountry = "JP";
		
		String actualCountry = ResourceUtilities.getCountryFromLocale(locale);
		
		assertTrue(expectedCountry.equalsIgnoreCase(actualCountry));
	}
	
	private void init() throws AMWebAPILocalizableException{
		MockViewHandler viewHandler = new MockViewHandler();
		ResultSet result = new ResultSet();
		
		// only setting the "ShortDisplayName" meta-data here, but in reality there are other ones too
		ArrayList<Map<String, Object>> columnMetaData = new ArrayList<Map<String, Object>>();
		Map<String, Object> col1 = new HashMap<>(), col2 = new HashMap<>(), col3 = new HashMap<>(), col4 = new HashMap<>();
		col1.put("ShortDisplayName", "Id");
		col1.put("InfoItemID", "3A151D0D-6510-4182-B1F5-75B1C69DE603");
		col2.put("ShortDisplayName", "Name");
		col2.put("InfoItemID", "4403D9A3-59D0-4D15-BDF5-252AE8CA59A7");
		col3.put("ShortDisplayName", "UniqueID");
		col3.put("InfoItemID", "4499CCAD-64CE-418B-8A8C-0ADD5E3EE483");
		col4.put("ShortDisplayName", "Smart policy");
		col4.put("InfoItemID", "A2C410BE-6C06-4141-9545-3487FDE0B7F7");
		
		columnMetaData.add(col1);
		columnMetaData.add(col2);
		columnMetaData.add(col3);
		columnMetaData.add(col4);
		
		result.setColumnMetaData(columnMetaData);
		
		Object[] row1 = {1, "TestName1", "10", "Nope"};
		Object[] row2 = {2, "TestName2", "11", "Nope"};
		Object[] row3 = {3, "TestName3", "12", "Nope"};
		Object[] row4 = {4, "TestName4", "13", "Yep"};
		
		result.addRow(new Row(row1));
		result.addRow(new Row(row2));
		result.addRow(new Row(row3));
		result.addRow(new Row(row4));
		
		viewHandler.setResult(result);
		
		mockDal = new MockDal(viewHandler);
	}
	
}

