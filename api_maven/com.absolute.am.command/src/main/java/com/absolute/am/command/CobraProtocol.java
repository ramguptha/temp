/**
 * 
 */
package com.absolute.am.command;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.GeneralSecurityException;
import java.util.UUID;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.CipherOutputStream;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.parsers.ParserConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.absolute.util.PropertyList;

/**
 * @author dlavin
 *
 */
public class CobraProtocol {
    private static Logger m_logger = LoggerFactory.getLogger(CobraProtocol.class.getName()); 

	public static final int errCobraErrorBase = (1<<29);				// 0x20000000, 536870912
	
	public static final int errCobra_MDMErrorBase = errCobraErrorBase+14600;
	
    public static final short kCobraXMLDataCompressed = (1<<0);
    
    public static final short kCobraXMLDataBlowFishEncrypted = (1<<1);
    public static final short kCobraStreamHasTransactionID = (1<<2);
    public static final short kCobraStreamDoneMessage = (1<<3);

    public static final byte kCobraProtocolHeaderVersion = 1;
    public static final byte kCobraProtocolHeaderVersionSSL = 2;
    public static final byte kCobraMaxProtocolHeaderVersion	= 2; // keep in sync with the newest protocol version

    public static final short kCobraServerPort = 3971;
    public static final short kCobraAgentPort = 3970;

    // values for 'ServerType' in CommandInfoData.
    public static final int kCobraUnknownServer = 0;
    public static final int kCobraAgentServer = 1;
    public static final int kCobraAdminServer = 2;
    public static final int kCobraDistributionServer = 3;
    public static final int kCobraLicensingServer = 4;
    public static final int kCobraStagingServer = 5;
    public static final int kCobraServerServer = 6;
    public static final int kCobraSyncServicesServer = 7;


    private static final String kBlowfishStreamEncryptionKey = "Es war einmal und ist nicht mehr...";

    private static byte[] getBytesForCommandBody(ICobraCommand command, Boolean useEncryption, Boolean useCompression)
    		throws GeneralSecurityException, IOException {

    	// TODO: Determine if these streams need to be explicitly closed when an exception occurs?  
    	ByteArrayOutputStream baos = new ByteArrayOutputStream();
        OutputStream os = baos;
        CPLATOutputStream cplatos = null;
        CipherOutputStream zpcos = null;
        
        try {
	        if (useEncryption) {
	    		SecretKeySpec KS = new SecretKeySpec(kBlowfishStreamEncryptionKey.getBytes("UTF-8"), "Blowfish");
	    		Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
	    		cipher.init(Cipher.ENCRYPT_MODE, KS);
				zpcos = new CipherOutputStream(baos, cipher);
	    		os = zpcos;
	        }
	
	        if (useCompression) {
	        	// Wrap the output stream with a compression stream.
	        	// The second parameter forces DeflaterOutputStream to flush itself and any inner streams 
	        	// when told to do so. Without this, DeflaterOutputStream won't flush, but it will call flush on
	        	// the inner streams, which messes up the ZeroPaddedCipherOutputStream.
	        	os = new DeflaterOutputStream(os, true); 
	        }
	
	        cplatos = new CPLATOutputStream(os);
	        PropertyList plist = command.buildCommandDictionary();
	        cplatos.writePropertyList(plist);
	        
	        cplatos.flush();
        } finally {
        	if (null != cplatos ) {
        		cplatos.close();
        	}
        	if (null != os ) {
        		os.close();
        	}
        	if (null != zpcos ) {
        		zpcos.close();
        	}
        }
  
    	return baos.toByteArray();      
    }

    private static void writeCommand(OutputStream output, CobraProtocolHeader header, ICobraCommand command, UUID transactionId) throws IOException, GeneralSecurityException {
      
        CPLATOutputStream headerOutputStream = new CPLATOutputStream(output);
        header.Write(headerOutputStream);

        // Transaction ID is an optional header item
        if ((header.Flags & kCobraStreamHasTransactionID) != 0) {
        	// TODO: can we sanity check the transactionId?
            headerOutputStream.writeUUID(transactionId);
        }

        // Optional headers used on SSL channels only.
        if (header.Version >= kCobraProtocolHeaderVersionSSL) {
            // TODO: pick up a version number from somwhere.
            // Format is (Maj << 8) | (Min << 4) | (BugFixVer).
            short version = (short)((6 << 8) | (0 << 4) | 3);
            headerOutputStream.writeShort(version);
            headerOutputStream.writeUUID(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        }

        byte[] commandBody = getBytesForCommandBody(command, 
        		(header.Flags & kCobraXMLDataBlowFishEncrypted) > 0, 
        		(header.Flags & kCobraXMLDataCompressed) > 0);
        

        // write the length
        headerOutputStream.writeInt(commandBody.length);
        
        // write body
        headerOutputStream.write(commandBody);

        headerOutputStream.flush();
    }


    private static PropertyList getCommandFromStream(InputStream inputStream, Boolean useEncryption, Boolean useCompression) 
    		throws GeneralSecurityException, IOException, ParserConfigurationException, SAXException {

    	InputStream is = inputStream;
    	
    	// TODO: Determine if these streams need to be explicitly closed when an exception occurs?
    	// When encryption is enabled, wrap the input stream with a decryption stream. 
    	if (useEncryption) {
			SecretKeySpec KS = new SecretKeySpec(kBlowfishStreamEncryptionKey.getBytes("UTF-8"), "Blowfish");
			Cipher cipher = Cipher.getInstance("Blowfish/ECB/NoPadding");
			cipher.init(Cipher.DECRYPT_MODE, KS);
			CipherInputStream cis = new CipherInputStream(is, cipher);
			is = cis;
    	}

    	// When compression is enabled, wrap the input stream with an inflater.
    	if (useCompression) {
    		InflaterInputStream iis = new InflaterInputStream(is);
    		is = iis;
    	}
    	
    	CPLATInputStream cplatIs = new CPLATInputStream(is);
    	PropertyList commandPlist = cplatIs.readPropertyList();
    	
    	return commandPlist;      
    }

    public static PropertyList readCommand(InputStream input) 
    		throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException
    {            
        CPLATInputStream headerInputStream = new CPLATInputStream(input);
        CobraProtocolHeader header = new CobraProtocolHeader();
        header.Read(headerInputStream);

        // Transaction ID is an optional header item
        if ((header.Flags & kCobraStreamHasTransactionID) != 0) {
        	// TODO: If transactionUUID is present, how is it returned to the caller?
        	// This is in the stream so we have to read it in order to get to the next piece of 
        	// data, even though we don't use this value.
            @SuppressWarnings("unused")
			UUID transactionUUID = null;
            transactionUUID = headerInputStream.readUUID();
        }

        // Optional headers used on SSL channels only.
        if (header.Version >= kCobraProtocolHeaderVersionSSL) {
        	// TODO: If version and serverUUID are to be used somewhere, how are they returned to the caller?
        	// These two items are in the stream so we have to read them in order to get to the next piece of 
        	// data, even though we don't use either of them.       	
        	@SuppressWarnings("unused")
            short version = headerInputStream.readShort();
        	@SuppressWarnings("unused")
            UUID serverUUID = headerInputStream.readUUID();
        }

        @SuppressWarnings("unused")
        int dataLength = headerInputStream.readInt();            

        PropertyList retVal = getCommandFromStream(input, 
        		(header.Flags & kCobraXMLDataBlowFishEncrypted) > 0, 
        		(header.Flags & kCobraXMLDataCompressed) > 0);
        
        return retVal;
    }    

    public static final String kCobra_XML_ServerID = "ServerID";											//!< \a number; the server ID
    public static final String kCobra_XML_CommandID = "CommandID";											//!< \a number; the command ID
    public static final String kCobra_XML_CommandVersion = "CommandVersion";								//!< \a number; the version of the command

    public static final String kCobra_XML_CommandDescription = "CommandDescription";						//!< \a string; a description of the command
    public static final String kCobra_XML_CommandExecutionDateTime = "CommandExecutionDateTime";			//!< \a date; the date and time when the command should be executed
    public static final String kCobra_XML_CommandInterval = "CommandInterval";								//!< \a number; at which interval should the command be run (0=run once)
    public static final String kCobra_XML_CommandIntervalUnit = "CommandIntervalUnit";						//!< \a number; the Units at which the command gets repeated (e.g. hours, minutes)
    public static final String kCobra_XML_DeferUncompletedTasks = "CommandDeferUncompletedTasks";	//!< \a boolean; if true then the command get's deferred if target cannot be reach
    public static final String kCobra_XML_ExecuteCommandNow = "ExecuteCommandNow";							//!< \a boolean; if true then the command is executed now and the CommandExecutionDateTime is ignored

    // add in 4.1
    public static final String kCobra_XML_CommandWakeUpMachineIfNotAvail = "CommandWakeUpMachineIfNotAvail";				//!< \a boolean; if true then the a WOL is send to wake up machine
    public static final String kCobra_XML_CommandHistoryOption = "CommandHistoryOption";					//!< \a number; options about when to add command to history

        public static final String kCobra_XML_CommandParameters = "CommandParameters";							//!< \a dictionary; list of parameters for the command
        public static final String kCobra_XML_CommandParams = "CommandParams";							//!< \a dictionary; list of parameters for the command

    public static final String kCobra_XML_CommandUUID = "CommandUUID";										//!< \a UUID; a UUID unique to each command
    public static final String kCobra_XML_AdminUUID = "AdminUUID";											//!< \a UUID; a UUID which defines the admin sending this command

    public static final String kCobra_XML_ServerSerialno = "SeedValue";											//!< \a string; this is the serialno of the server. This is used to make sure no other admin can talk to agent later

    public static final String kCobra_XML_AgentSerialList = "AgentSerialList";								//!< \a array; a list of UUID of the target machine
    public static final String kCobra_XML_TargetListObjects = "TargetListObjects";							//!< \a array; a list object from the commands target list
    public static final String kCobra_XML_AgentSerial = "AgentSerial";										//!< \a UUID; a UUID of the target machine
    public static final String kCobra_XML_AgentVersion = "AgentVersion";									//!< \a number; agent version [added in 2.0]
    public static final String kCobra_XML_AgentBuildnumber = "AgentBuildnumber";							//!< \a number; agent build number [added in 2.0]
    public static final String kCobra_XML_TargetIP = "TargetIP";											//!< \a string; the ip address of the target machine to contact
    public static final String kCobra_XML_TargetPort = "TargetPort";										//!< \a number; the port of the target machine to contact

    public static final String kCobra_XML_ServerBusyRetryCounter = "ServerBusyRetryCounter";				//!< \a number; number of retries for specific command because of server busy error

    public static final String kCobra_XML_AdminAppUUID = "AdminAppUUID";									//!< \a UUID; a UUID which identifies the admin app
    public static final String kCobra_XML_AdminAppListenPort = "AdminAppListenPort";						//!< \a number; the port the admin is listening on

    public static final String kCobra_XML_CommandResultParameters = "CommandResultParameters";				//!< \a dictionary; the results from the execution of the command
    public static final String kCobra_XML_CommandResultError = "CommandResultError";						//!< \a number; the error of the command
    public static final String kCobra_XML_CommandResultErroString = "CommandResultErrorString";			//!< \a string; optional; a string description of the error
    public static final String kCobra_XML_CommandResultErrorDebugInfo = "CommandResultErrorDebugInfo";		//!< \a string; additional debug info about the error

    public static final String kCobra_XML_CommandList = "Commands";											//!< \a array; list of commands need to be queued or executed
/*
    #define kCobra_XML_CommandIntervalUnit_Minutes		1
    #define kCobra_XML_CommandIntervalUnit_Hours			2
    #define kCobra_XML_CommandIntervalUnit_Days				3
    #define kCobra_XML_CommandIntervalUnit_Weeks			4
    #define kCobra_XML_CommandIntervalUnit_Months			5
    #define kCobra_XML_CommandIntervalUnit_Years			6
            */

    public static final int kCobra_XML_CommandHistoryOption_AlwaysAdd = 1;
    public static final int kCobra_XML_CommandHistoryOption_AddOnError = 2;
    public static final int kCobra_XML_CommandHistoryOption_NeverAdd = 3;


//#define kDialogResultTargetList_Param				"TargetList"

    public static final int kCobra_NoError = 0;

    /**
     * Helper method to construct the binary format of the command, ready to be sent over the wire.
     * Encryption and compression are applied.
     * @param command The command to send.
     * @return a binary representation of the command, compressed and encrypted as required.
     * @throws IOException
     * @throws GeneralSecurityException
     */
    static public byte[] commandToBytes(ICobraCommand command) throws IOException, GeneralSecurityException {

    	PropertyList commandAsPropertyList = command.buildCommandDictionary();
        String result = commandAsPropertyList.toXMLString();
        m_logger.debug("CommandDictionary:" + result);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        CobraProtocolHeader header = new CobraProtocolHeader();
        header.Flags = CobraProtocol.kCobraXMLDataCompressed | CobraProtocol.kCobraXMLDataBlowFishEncrypted;
        header.Version = CobraProtocol.kCobraProtocolHeaderVersionSSL;
        CobraProtocol.writeCommand(baos, header, command, null);
        byte[] retVal = baos.toByteArray();
        return retVal;
    }
}
