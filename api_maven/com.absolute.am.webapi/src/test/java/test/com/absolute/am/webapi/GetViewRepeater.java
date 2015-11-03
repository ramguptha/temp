package test.com.absolute.am.webapi;

import org.junit.Test;
import org.junit.experimental.categories.Category;

public class GetViewRepeater extends GetViewTest {
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void runNTimes() throws Exception {
		
		for (int i=0; i<10; i++) {
			System.out.println("Iteration " + i);
			can_get_content_views();
			can_get_content_views_all();
			cant_get_content_views_other();
			can_get_policies_views();
			can_get_policies_views_all();
			cant_get_policies_views_other();
			can_get_mobile_devices_views();
			can_get_mobile_devices_views_all();
			cant_get_mobile_devices_views_other();
			can_get_commands_history_views();
			can_get_commands_history_views_all();
			cant_get_commands_history_views_other();
			can_get_queued_commands_views();
			can_get_queued_commands_views_all();
			cant_get_queued_commands_views_other();
			//Thread.sleep(1000);
		}
	}
	
	
}
