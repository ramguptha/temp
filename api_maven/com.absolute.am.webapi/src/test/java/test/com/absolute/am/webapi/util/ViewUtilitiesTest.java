package test.com.absolute.am.webapi.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.model.ViewDescription;

public class ViewUtilitiesTest {
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void is_valid_view_name() {
		
		final ViewDescription[] m_viewTestDescriptions = new ViewDescription[] {
			new ViewDescription("testall1", "Test Description 1", 0),
			new ViewDescription("testall2", "Test Description 2", 1)
		};
		final String viewname = "testall2";
		
		assertTrue(com.absolute.am.webapi.util.ViewUtilities.isValidViewName(viewname, m_viewTestDescriptions));
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void is_not_valid_view_name() {
		
		final ViewDescription[] m_viewTestDescriptions = new ViewDescription[] {
			new ViewDescription("testall1", "Test Description 1", 0),
			new ViewDescription("testall2", "Test Description 2", 1)
		};
		final String viewname = "testall3";
		
		assertFalse(com.absolute.am.webapi.util.ViewUtilities.isValidViewName(viewname, m_viewTestDescriptions));
	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void is_not_valid_partial_view_name() {
		
		final ViewDescription[] m_viewTestDescriptions = new ViewDescription[] {
			new ViewDescription("testall1", "Test Description 1", 0),
			new ViewDescription("testall2", "Test Description 2", 1)
		};
		final String viewname = "all";
		
		assertFalse(com.absolute.am.webapi.util.ViewUtilities.isValidViewName(viewname, m_viewTestDescriptions));
	}

}
