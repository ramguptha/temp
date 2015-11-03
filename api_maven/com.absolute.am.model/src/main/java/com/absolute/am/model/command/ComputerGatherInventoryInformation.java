/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/
package com.absolute.am.model.command;

import java.util.UUID;


/**
 * @author maboulkhoudoud
 *
 */
public class ComputerGatherInventoryInformation {

	private UUID[] serialNumbers;
	private boolean fullInv;
	private boolean withFonts;
	private boolean withPrinters;
	private boolean withServices;
	private boolean withStartupItems;
	
	
	/**
	 * @return the serialNumbers
	 */
	public UUID[] getSerialNumbers() {
		return serialNumbers;
	}


	/**
	 * @param serialNumbers the serialNumbers to set
	 */
	public void setSerialNumbers(UUID[] serialNumbers) {
		this.serialNumbers = serialNumbers;
	}


	/**
	 * @return the fullInv
	 */
	public boolean isFullInv() {
		return fullInv;
	}


	/**
	 * @param fullInv the fullInv to set
	 */
	public void setFullInv(boolean fullInv) {
		this.fullInv = fullInv;
	}


	/**
	 * @return the withFonts
	 */
	public boolean isWithFonts() {
		return withFonts;
	}


	/**
	 * @param withFonts the withFonts to set
	 */
	public void setWithFonts(boolean withFonts) {
		this.withFonts = withFonts;
	}


	/**
	 * @return the withPrinters
	 */
	public boolean isWithPrinters() {
		return withPrinters;
	}


	/**
	 * @param withPrinters the withPrinters to set
	 */
	public void setWithPrinters(boolean withPrinters) {
		this.withPrinters = withPrinters;
	}


	/**
	 * @return the withServices
	 */
	public boolean isWithServices() {
		return withServices;
	}


	/**
	 * @param withServices the withServices to set
	 */
	public void setWithServices(boolean withServices) {
		this.withServices = withServices;
	}


	/**
	 * @return the withStartupItems
	 */
	public boolean isWithStartupItems() {
		return withStartupItems;
	}


	/**
	 * @param withStartupItems the withStartupItems to set
	 */
	public void setWithStartupItems(boolean withStartupItems) {
		this.withStartupItems = withStartupItems;
	}

	@Override
	public String toString() {	
		
		return "ComputerGatherInventoryInformation: fullInv=" + fullInv + 
				" withFonts=" + withFonts + " withPrinters=" + withPrinters + 
				" withServices=" + withServices + " withStartupItems=" + withStartupItems;
	}			
}
