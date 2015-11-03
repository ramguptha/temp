package com.absolute.am.model.command;

import com.absolute.am.model.policyaction.PolicyUuidActionUuidMapping;

public class ExecuteActionsOnPolicies {
		private PolicyUuidActionUuidMapping[] policyUuidActionUuidMappings;
		private boolean executeImmediately;
		
		/**
		 * The list policy unique id & action unique id mappings
		 */
		public PolicyUuidActionUuidMapping[] getPolicyUuidActionUuidMappings() {
			return policyUuidActionUuidMappings;
		}
		public void setDeviceIds(PolicyUuidActionUuidMapping[] policyUuidActionUuidMappings) {
			this.policyUuidActionUuidMappings = policyUuidActionUuidMappings;
		}	
		
		/**
		 * Whether the actions are executed immediately on the devices, or wait until next scheduled check-in
		 */
		public boolean getExecuteImmediately() {
			return executeImmediately;
		}
		public void setExecuteImmediately(boolean executeImmediately) {
			this.executeImmediately = executeImmediately;
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			String policyUuidActionUuidMappingsAsString = "";
			for (PolicyUuidActionUuidMapping map : policyUuidActionUuidMappings) {
				sb.append("{\"actionUuid\":\"" + map.getActionUuid() + "\"," + "\"policyUuid\":" + map.getPolicyUuid() + "\"},");
			}
			if (sb.length() > 0) {
				policyUuidActionUuidMappingsAsString = sb.substring(0, sb.length() - 2);
			}
			
			return "ExecuteActionsOnPolicies: policyUuidActionUuidMappings=[" + policyUuidActionUuidMappingsAsString + 
					"], executeImmediately=" + Boolean.toString(executeImmediately) +
					".";
		}
}
