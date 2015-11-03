package com.absolute.am.model.push;

public class SubscriptionMessage{
	
	public String endpoint;
    public String action;
    
    public SubscriptionMessage() {
    }

	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}
	
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}
}