package com.absolute.am.webapi.push;

import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.atmosphere.cpr.AtmosphereFramework;
import org.atmosphere.cpr.AtmosphereResource;
import org.atmosphere.cpr.Broadcaster;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.absolute.am.sqlitedal.Dal;
import com.absolute.am.webapi.Application;
import com.absolute.util.PropertyList;

public class Manager {
	
	private Broadcaster broadcaster = null;
	private AtmosphereFramework framework;
	private Map<String, ArrayList<String>> subscriptions = new HashMap<String, ArrayList<String>>();
	private Map<String, ArrayList<String>> tableToViewMap = new HashMap<String, ArrayList<String>>();
	
    private final String PLIST_FILE_VIEW_DEFINITIONS = "WebAPI_StandardViewDefinitions.xml",
    		PLIST_FILE_EXTENDED_INFO_ITEMS = "ExtendedInfoItems.xml",
    		PLIST_FILE_INFO_ITEMS = "InformationItems.xml",
    		PLIST_FILE_INFO_ITEMS_ITEM_DEFINITIONS = "ItemDefinitions",
    		PLIST_FILE_VIEW_DEFINITIONS_VIEWS = "View Definitions";
    
    private final Logger logger = LoggerFactory.getLogger(Manager.class);
    
    // Initialize the tableToEndpointMap from the view_config property files
	@SuppressWarnings("unchecked")
	public Manager(AtmosphereFramework framework) {
		String viewConfigPath = Application.getRuntimeProperties().get(Dal.PROP_VIEW_CONFIG_FOLDER).toString();
		PropertyList views = null, infoItems = null, extendedInfoItems = null;
		
		this.framework = framework;
		
		try {
			views = PropertyList.fromInputStream(new FileInputStream(viewConfigPath + "/" + PLIST_FILE_VIEW_DEFINITIONS));
			infoItems = PropertyList.fromInputStream(new FileInputStream(viewConfigPath + "/" + PLIST_FILE_INFO_ITEMS));
			extendedInfoItems = PropertyList.fromInputStream(new FileInputStream(viewConfigPath + "/" + PLIST_FILE_EXTENDED_INFO_ITEMS));
		} catch (Exception e) {
			logger.debug("Failed to open/parse the view_config property files: " + e.getMessage());
		}
		
		Map<String, Object> itemDefinitions = PropertyList.getElementAsMap(infoItems, PLIST_FILE_INFO_ITEMS_ITEM_DEFINITIONS),
				extendedItemDefinitions = PropertyList.getElementAsMap(extendedInfoItems, PLIST_FILE_INFO_ITEMS_ITEM_DEFINITIONS),
				viewDefinitions = PropertyList.getElementAsMap(views, PLIST_FILE_VIEW_DEFINITIONS_VIEWS);
		
		for (Map.Entry<String, Object> item : viewDefinitions.entrySet()) {
			Map<String, Object> viewProperties = (Map<String, Object>) item.getValue();
			Map<String, Object> viewColumnProperties = (Map<String, Object>) viewProperties.get("ColumnProperties");
			
			for (Map.Entry<String, Object> column : viewColumnProperties.entrySet()){
				
				String infoItemId = ((Map<String, Object>)column.getValue()).get("InfoItemID").toString();
				Object infoItemProperties = itemDefinitions.get(infoItemId);
				
				if ( infoItemProperties == null){
					infoItemProperties = extendedItemDefinitions.get(infoItemId); 
				}
				
				if (infoItemProperties != null) {
					String viewName = item.getKey();
					String tableName = ((Map<String, Object>)infoItemProperties).get("DB_TableName").toString();
					ArrayList<String> viewNames;
					
					if ( tableToViewMap.containsKey(tableName)){
						viewNames = tableToViewMap.get(tableName);
						if( !viewNames.contains(viewName)){
							viewNames.add(viewName);
						}
					} else {
						viewNames = new ArrayList<String>();
						viewNames.add(viewName);
						tableToViewMap.put(tableName, viewNames);
					}
					
				}
				
			}
			
		}
	}
		
	// Handle the broadcasting of messages when a table is updated via sync service
	public void tablesUpdated(String[] tables){
		if( broadcaster != null ){
			ArrayList<String> views = new ArrayList<String>();
			boolean subscribed;
			
			for(int i=0; i < tables.length; i++){
				ArrayList<String> viewArray = tableToViewMap.get(tables[i]);
				if( viewArray != null){
					for (String view : viewArray){
						if( !views.contains(view)){
							views.add(view);
						}
					}
				}
			}
			
			for (String view : views){
				subscribed = false;
				
				// make sure that the view has at least one subscribed user
				for (Entry<String, ArrayList<String>> entry : subscriptions.entrySet()) {
					ArrayList<String> subscriptions = entry.getValue();
				    if( subscriptions.contains(view)){
				    	subscribed = true;
				    	break;
				    }
				}
				
				if( subscribed ){
					JSONObject tablesJSON = new JSONObject();
					tablesJSON.put("updated", true);
					tablesJSON.put("endpoint", view);
					
					framework.metaBroadcaster().broadcastTo("/" + view, tablesJSON.toString());
					
					logger.debug("Broadcasting to " + "/" + view);
				}
			}
		}
	}
	

	
	// Handle a message coming in from a user
	public void handleMessage(String jsonMessage, AtmosphereResource atmosphereResource){
		String action, uuid = atmosphereResource.uuid();
		JSONObject jsonMessageObj = new JSONObject(jsonMessage);
		action = jsonMessageObj.getString("action");
		// This may not be the best way to set the broadcaster...WATCH OUT
		broadcaster = atmosphereResource.getBroadcaster();
		
		if( action.equals("subscribe") ){
			ArrayList<String> subscriptionsUserList;
			String endpoint = jsonMessageObj.getString("endpoint");
			
			if ( endpoint != null){
				if( subscriptions.containsKey(uuid) ){
					subscriptionsUserList = subscriptions.get(uuid);
					
					if( !subscriptionsUserList.contains(endpoint) ){
						subscriptionsUserList.add(endpoint);
						
						Broadcaster b = framework.getBroadcasterFactory().lookup("/" + endpoint, true);
						b.addAtmosphereResource(atmosphereResource);

						logger.debug("Added an atmosphereResource to a broadcaster for " + uuid + " with id " + b.getID());
					}
				} else {
					subscriptionsUserList = new ArrayList<String>();
					subscriptionsUserList.add(endpoint);
					subscriptions.put(uuid, subscriptionsUserList);
					
					Broadcaster b = framework.getBroadcasterFactory().lookup("/" + endpoint, true);
					b.addAtmosphereResource(atmosphereResource);
					
					logger.debug("Added an atmosphereResource to a broadcaster for " + uuid + " with id " + b.getID());
				}
			}
		} else if( action.equals("unsubscribe") ){
			ArrayList<String> subscriptionsUserList;
			String endpoint = jsonMessageObj.getString("endpoint");
			
			if ( endpoint != null){
				if( subscriptions.containsKey(uuid) ){
					subscriptionsUserList = subscriptions.get(uuid);
					subscriptionsUserList.remove(endpoint);
					
					Broadcaster b = framework.getBroadcasterFactory().lookup("/" + endpoint, true);
					b.removeAtmosphereResource(atmosphereResource);
					
					if(subscriptionsUserList.size() == 0){
						subscriptions.remove(uuid);
					}
				}
			}
		} else if( action.equals("list") ){
			JSONObject JSONresponse = new JSONObject();
			
			if( subscriptions.containsKey(uuid) ){
				JSONresponse.put("result", subscriptions.get(uuid));
			} else {
				JSONresponse.put("result", "");
			}
			
			// add a custom request header so that we may filter the broadcast to this specific client
			broadcaster.broadcast(JSONresponse.toString(), atmosphereResource);
		}
	}
	
	// Clear the user's endpoint subscription preferences ( called on user disconnect )
	public void removeUser(String uuid){		
		if( subscriptions.containsKey(uuid) ){
			subscriptions.remove(uuid);
		}
	}
}
