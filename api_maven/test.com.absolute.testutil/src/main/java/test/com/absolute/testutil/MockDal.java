package test.com.absolute.testutil;
 
import com.absolute.am.dal.IViewHandler;
import com.absolute.am.sqlitedal.Dal;

public class MockDal extends Dal {
	
	private MockViewHandler m_viewHandler;
	
	public MockDal() {
		m_viewHandler = null;
	}
	
	public MockDal(MockViewHandler mockViewHandler){
		m_viewHandler=mockViewHandler;
	}
	
	@Override
	public IViewHandler getViewHandler(){
		
		return m_viewHandler;
	}
}