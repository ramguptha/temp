package test.com.absolute.testutil;

import java.util.ArrayList;
import java.util.HashMap;
import com.absolute.am.dal.IViewHandler;
import com.absolute.am.dal.ResultSet;
import com.absolute.am.model.command.GenericView;

public class MockViewHandler implements IViewHandler {
	
	public MockViewHandler(ResultSet result) {
		this.result = result;
	}
	
	public MockViewHandler() {
		result=null;
	}
	
	private ResultSet result;
				
	@Override
	public ResultSet queryView(String viewInfo, 
			HashMap<String, String> uiParams, ArrayList<String> userParams, String dbLocaleSuffix) throws Exception {
		
		return result;
	}
	
	public void setResult(ResultSet result) {
		
		this.result=result;
	}

	@Override
	public ResultSet queryViewColumnMetaData(String arg0, String arg1)
			throws Exception {
		return result;
	}

	@Override
	public ResultSet queryAdHocView(GenericView view, HashMap<String, String> uiParams, ArrayList<String> userParams, String dbLocaleSuffix) throws Exception {

		return result;
	}
}