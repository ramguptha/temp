import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import test.com.absolute.am.webapi.ContentUpload;

/**
 * This is a suite of test cases that should be used as a regression test on a new build.
 * @author dlavin
 *
 */
@RunWith(Suite.class)
@SuiteClasses({
	ContentUpload.class,
	ContentUpload.class,
	ContentUpload.class,
	ContentUpload.class,
	ContentUpload.class,
	ContentUpload.class,
	ContentUpload.class,
	ContentUpload.class,
	ContentUpload.class,
	ContentUpload.class
})
public class ContentUploadRepeatedTests {

}
