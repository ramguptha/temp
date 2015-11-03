/**
 * Copyright (c) 2015 Absolute Software Corporation, All rights reserved.  
 * Reproduction or transmission in whole or in part, in any form or by any means, 
 * electronic, mechanical or otherwise, is prohibited without the prior written 
 * consent of the copyright owner.
 */
package test.com.absolute.am.command;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.UUID;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import com.absolute.am.command.AMServerProtocol;
import com.absolute.am.command.AMServerProtocolException;
import com.absolute.am.command.CobraAdminMiscDatabaseCommand;
import com.absolute.am.command.CobraProtocol;
import com.absolute.am.command.CommandFactory;
import com.absolute.am.dal.model.ConfigurationProfile;
import com.absolute.util.PropertyList;
import com.absolute.util.exception.AMWebAPILocalizableException;

public class ConfigurationProfileCommandsTest extends LoggedInTest {

	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_install_config_profiles() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException{ 
	
		ConfigurationProfile configurationProfileDetails1 = new ConfigurationProfile();
		configurationProfileDetails1.setPayloadName("TestConfigProfile1");
		configurationProfileDetails1.setPayloadDescription("Test Configuration Profile");
		configurationProfileDetails1.setConfigurationType(1);
		configurationProfileDetails1.setPayloadIdentifier("test.identifier." + UUID.randomUUID().toString());
		configurationProfileDetails1.setPayloadOrganization("Test Organization");
		configurationProfileDetails1.setPayloadRemovalOptions(3);
		configurationProfileDetails1.setPlatformType(11);	/* Android */
		configurationProfileDetails1.setSeed(1);
		
		// TODO: Read IDs from a config file(?)
		long[] deviceId = new long[2];
		deviceId[0] = 123;
		deviceId[1] = 456;
		
		CobraAdminMiscDatabaseCommand command = CommandFactory.createInstallConfigurationProfileCommand(loginReturnedAdminUUID, deviceId, configurationProfileDetails1);
		System.out.println("command=" + command.toXml());
		
		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "Install Configuration Profile on device failed.");
			System.out.println("Final result=" + finalResult.toXMLString());
			long resultError = (Long)finalResult.get(CobraProtocol.kCobra_XML_CommandResultError);
			Assert.assertEquals(0, resultError);
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}

	}
	
	@Test
	@Category(com.absolute.util.helper.FastTest.class)
	public void can_remove_config_profiles() 
			throws IOException, GeneralSecurityException, RuntimeException, ParserConfigurationException, 
				SAXException, AMServerProtocolException, AMWebAPILocalizableException{ 
	
		// TODO: Read IDs from a config file(?)
		long[] configurationProfileAssociationIds = new long[2];
		configurationProfileAssociationIds[0] = 9998;
		configurationProfileAssociationIds[1] = 9999;

		CobraAdminMiscDatabaseCommand command = CommandFactory.createRemoveConfigurationProfileFromDeviceCommand(loginReturnedAdminUUID, configurationProfileAssociationIds);
		System.out.println("command=" + command.toXml());
		
		AMServerProtocol amServerProtocol = getAMServerProtocol();

		try {
			PropertyList finalResult = amServerProtocol.sendCommandAndValidateResponse(command, "Remove Configuration Profile from device failed.");
			System.out.println("Final result=" + finalResult.toXMLString());
			long resultError = (Long)finalResult.get(CobraProtocol.kCobra_XML_CommandResultError);
			Assert.assertEquals(0, resultError);
		} catch (AMWebAPILocalizableException e) {
			System.out.println("AMWebAPILocalizableException: e.getMessage()=" + e.getMessage());
			throw e;
		}
	}
}
