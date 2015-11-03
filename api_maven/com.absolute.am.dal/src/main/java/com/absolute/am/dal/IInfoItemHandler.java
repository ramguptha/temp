package com.absolute.am.dal;

import java.util.ArrayList;
import java.util.Map;


public interface IInfoItemHandler {

	public ArrayList<Map<String, String>> getCustomInfoItemInfo(boolean forMobile) throws Exception; 
	
}