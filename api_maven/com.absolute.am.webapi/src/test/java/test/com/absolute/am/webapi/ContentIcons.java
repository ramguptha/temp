/**
 * 
 */
package test.com.absolute.am.webapi;

import static org.junit.Assert.*;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.client.ClientProtocolException;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import test.com.absolute.testutil.Helpers;

/**
 * @author dlavin
 * 
 */
public class ContentIcons extends LoggedInTest {
	private static String CONTENT_ICONS_API_FORMAT_STRING = "api/content/icons/%1$s-%2$s.png";
	private static String CONTENT_ICONS_WITH_CATEGORY_API_FORMAT_STRING = "api/content/icons/%1$s-%2$s.png?category=%3$s";
	private static String STATIC_ICONS_CATEGORIES_ENDPOINT_FORMAT_STRING = "static/icons/categories/%1$s-%2$s.png";
	private static String STATIC_ICONS_DEFAULT_ENDPOINT_FORMAT_STRING = "static/icons/%1$s-default.png";

	private static String CATEGORY_DOCUMENTS = "documents";

	private static String ICON_SIZE = "64";

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_get_icons_for_all_filetypes_and_sizes()
			throws ClientProtocolException, IOException,
			NoSuchAlgorithmException, KeyManagementException {

		String[] fileTypes = {
				// The first 6 are special because they are the normalized file
				// types. There is no icon with the same name,
				// they are mapped to different names, e.g. aif->aiff,
				// htm->html, etc.
				"aif",
				"htm",
				"jpg",
				"mpg",
				"tif",
				"txt",

				// The remainder are regular filetypes, and a unique icon exists
				// for each one.
				"3g2", "3gp", "3gp2", "3gpp", "aifc", "aiff", "amr", "avi",
				"bmp", "bwf", "cdda", "doc", "docm", "docx", "gif", "html",
				"jpeg", "key", "m2v", "m4a", "m4b", "m4p", "m4v", "mov", "mp3",
				"mp4", "mpeg", "mqv", "numbers", "pages", "pdf", "png", "pps",
				"ppsx", "ppt", "pptx", "quicktime", "rtf", "rtfd", "swa",
				"text", "tiff", "wav", "wave", "webarchive", "wmv", "xls",
				"xlsb", "xlsm", "xlsx", "xml", "xsl" };

		String[] iconSizes = { "16", "32", "64" };

		System.out.println("There are " + iconSizes.length
				+ " iconSizes to process.");
		System.out.println("There are " + fileTypes.length
				+ " fileTypes to process.");
		for (int sizeIndex = 0; sizeIndex < iconSizes.length; sizeIndex++) {
			for (int typeIndex = 0; typeIndex < fileTypes.length; typeIndex++) {
				String URI = String.format(CONTENT_ICONS_API_FORMAT_STRING,
						iconSizes[sizeIndex], fileTypes[typeIndex]);
				String URL = Helpers.WEBAPI_BASE_URL + URI;
				System.out.println("URI is" + URL);
				Helpers.doGETCheckStatusReturnBody(logonCookie, URL, 200, 200);
			}
		}
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void get_category_icon_when_filetype_unknown()
			throws NoSuchAlgorithmException, ClientProtocolException,
			IOException, KeyManagementException {
		// First, get the category icon directly, and calculate the MD5 hash.
		MessageDigest digest = MessageDigest.getInstance("MD5");

		String expectedFile = Helpers.WEBAPI_BASE_URL
				+ String.format(STATIC_ICONS_CATEGORIES_ENDPOINT_FORMAT_STRING,
						ICON_SIZE, CATEGORY_DOCUMENTS);
		System.out.println("expectedFile=" + expectedFile);
		// GET the the hash of the expected file.
		digest.reset();
		byte[] expectedFileHash = getFile(
				Helpers.createHttpClientWithoutCertificateChecking(),
				expectedFile, null, digest);

		// Get the icon returned for an unknown filetype, but known category
		String requestedFile = Helpers.WEBAPI_BASE_URL
				+ String.format(CONTENT_ICONS_WITH_CATEGORY_API_FORMAT_STRING,
						ICON_SIZE, "no_such_file_extension", CATEGORY_DOCUMENTS);
		System.out.println("requestedFile=" + requestedFile);

		digest.reset();
		byte[] actualFileHash = getFile(
				Helpers.createHttpClientWithoutCertificateChecking(),
				requestedFile, null, digest);

		assertArrayEquals("Actual icon file does not match expected icon.",
				expectedFileHash, actualFileHash);

	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void get_default_icon_when_category_unknown()
			throws NoSuchAlgorithmException, ClientProtocolException,
			IOException, KeyManagementException {
		// First, get the category icon directly, and calculate the MD5 hash.
		MessageDigest digest = MessageDigest.getInstance("MD5");

		String expectedFile = Helpers.WEBAPI_BASE_URL
				+ String.format(STATIC_ICONS_DEFAULT_ENDPOINT_FORMAT_STRING,
						ICON_SIZE);
		System.out.println("expectedFile=" + expectedFile);
		// GET the the hash of the expected file.
		digest.reset();
		byte[] expectedFileHash = getFile(
				Helpers.createHttpClientWithoutCertificateChecking(),
				expectedFile, null, digest);

		// Get the icon returned for an unknown filetype, but known category
		String requestedFile = Helpers.WEBAPI_BASE_URL
				+ String.format(CONTENT_ICONS_WITH_CATEGORY_API_FORMAT_STRING,
						ICON_SIZE, "no_such_file_extension", "no_such_category");
		System.out.println("requestedFile=" + requestedFile);

		digest.reset();
		byte[] actualFileHash = getFile(
				Helpers.createHttpClientWithoutCertificateChecking(),
				requestedFile, null, digest);

		assertArrayEquals("Actual icon file does not match expected icon.",
				expectedFileHash, actualFileHash);
	}

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void get_404_when_no_icon_exists() throws ClientProtocolException,
			IOException, NoSuchAlgorithmException, KeyManagementException {

		String noSuchIconURL = Helpers.WEBAPI_BASE_URL
				+ String.format(CONTENT_ICONS_API_FORMAT_STRING, "99", "txt"); // This
																				// size
																				// does
																				// not
																				// exist.
		System.out.println("noSuchIconURL =" + noSuchIconURL);

		Helpers.doGETCheckStatusReturnBody(logonCookie, noSuchIconURL, 404, 404);
	}
}
