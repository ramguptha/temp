/**
* Copyright (c) 2015 Absolute Software Corporation, All rights reserved.
* Reproduction or transmission in whole or in part, in any form or by any means,
* electronic, mechanical or otherwise, is prohibited without the prior written
* consent of the copyright owner.
*/

package com.absolute.util;

public class ImageUtilities {

	/**
	 * Test to see whether an byte array represents an image
	 * @param a byte array representing an image
	 * @param a signature of an image format ( typically PNG/JPEG )
	 * @param a flag whether we're checking for a JPEG type image ( special case )
	 * @return true/false based on whether the iconArray represents an image based on the passed params
	 * 
	 */
	public static boolean compareImageBytes(byte[] iconArray, byte[] signatureArray, boolean isJpeg) {
		
		if(null == iconArray || null == signatureArray || iconArray.length < signatureArray.length) {
			return false;
		}
		
		for (int i = 0; i < signatureArray.length; i ++ )
		{
			// bytes at index 4 and 5 should be ignored in test for JPEG signature
			if(isJpeg && (i == 4 || i == 5)) {
				continue;
			}
			
			if(iconArray[i] != signatureArray[i] ) {
				return false;
			}
		}
		
		return true;
	}
}
