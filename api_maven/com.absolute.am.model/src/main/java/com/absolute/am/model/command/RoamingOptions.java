/**
 * 
 */
package com.absolute.am.model.command;

import java.util.Arrays;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RoamingOptions {

	private int[] deviceIds;
	private Boolean voice;
	private Boolean data;
	
	/**
	 * The device Id list
	 */
	public int[] getDeviceIds() {
		return deviceIds;
	}
	public void setDeviceIds(int[] deviceIds) {
		this.deviceIds = deviceIds;
	}
	
	/**
	 * The voice option is enabled
	 */
	public Boolean getVoice() {
		return voice;
	}
	public void setVoice(Boolean voice) {
		this.voice = voice;
	}
	
	/**
	 * The data option is enabled
	 */
	public Boolean getData() {
		return data;
	}
	public void setData(Boolean data) {
		this.data = data;
	}
	
	@Override
	public String toString() {			
		return "RoamingOptions: voice=" + voice + 
				" data=" + data + 
				" deviceIds=" + Arrays.toString(deviceIds) +
				".";
	}		
}
