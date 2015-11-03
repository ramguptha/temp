/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.model.command;

import java.util.Arrays;
import java.util.UUID;


/**
 * @author maboulkhoudoud
 *
 */
public class ComputerSendMessage {

	private String message;
	private UUID[] serialNumbers;

	

	/**
	 * @return the devices
	 */
	public UUID[] getSerialNumbers() {
		return serialNumbers;
	}


	/**
	 * @param devices the devices to set
	 */
	public void setSerialNumbers(UUID[] serialNumbers) {
		this.serialNumbers = serialNumbers;
	}


	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}


	/**
	 * @param message the message to set
	 */
	public void setMessage(String message) {
		this.message = message;
	}


	@Override
	public String toString() {	
		
		return "SendMessage: message=" + message + 
				" serialNumbers=" + Arrays.toString(serialNumbers);
	}			
}
