package test.com.absolute.am.sqlitedal;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.experimental.categories.Category;

import com.absolute.am.dal.IContentHandler;
import com.absolute.am.dal.IDal;
import com.absolute.am.dal.model.MobileMedia;

import static org.junit.Assert.*;

public class TestContentHandler {
	
	private static final long MOBILE_MEDIA_ID_VISIO = 7;
	private static final String MOBILE_MEDIA_DISPLAY_NAME_VISIO = "Visio-AMWebUI_SystemComponents_2";
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_content() throws Exception {
		IDal dal = Util.getDal();
		IContentHandler contentHandler = dal.getContentHandler();

		MobileMedia mobileMedia = contentHandler.getContent(MOBILE_MEDIA_ID_VISIO);
		assertTrue(mobileMedia.getId() == MOBILE_MEDIA_ID_VISIO);
	}	
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_content_uuids() throws Exception {
		List<Long> mediaIds = Arrays.asList(MOBILE_MEDIA_ID_VISIO);
		IDal dal = Util.getDal();
		IContentHandler contentHandler = dal.getContentHandler();

		UUID[] uuids = contentHandler.getMediaUniqueIds(mediaIds);
		assertTrue(uuids.length == mediaIds.size());
		assertTrue(uuids[0].toString().length() > 0);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_content_by_display_name() throws Exception {
		IDal dal = Util.getDal();
		IContentHandler contentHandler = dal.getContentHandler();

		MobileMedia mobileMedia = contentHandler.getContentByDisplayName(MOBILE_MEDIA_DISPLAY_NAME_VISIO);
		assertNotNull("check getContentByDisplayName returned non null value", mobileMedia);
		System.out.println("mobileMedia.getId() for returned object is " + mobileMedia.getId());
		assertTrue(mobileMedia.getDisplayName().compareToIgnoreCase(MOBILE_MEDIA_DISPLAY_NAME_VISIO) == 0);
		assertTrue(mobileMedia.getCategory().compareToIgnoreCase("Documents") == 0);
		assertTrue(mobileMedia.getFileType().compareToIgnoreCase("PDF") == 0);
	}
	
}
