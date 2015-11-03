package com.absolute.am.command;

import java.io.BufferedOutputStream;
import java.io.CharArrayWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;

import javax.net.SocketFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.bouncycastle.asn1.x509.Certificate;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.crypto.params.RSAKeyParameters;
import org.bouncycastle.crypto.tls.AlertDescription;
import org.bouncycastle.crypto.tls.CertificateRequest;
import org.bouncycastle.crypto.tls.DefaultTlsClient;
import org.bouncycastle.crypto.tls.DefaultTlsSignerCredentials;
import org.bouncycastle.crypto.tls.TlsAuthentication;
import org.bouncycastle.crypto.tls.TlsClientProtocol;
import org.bouncycastle.crypto.tls.TlsCredentials;
import org.bouncycastle.crypto.tls.TlsFatalAlert;
import org.bouncycastle.crypto.tls.TlsKeyExchange;
import org.bouncycastle.crypto.tls.TlsRSAKeyExchange;
import org.bouncycastle.crypto.util.PublicKeyFactory;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import com.absolute.util.PropertyList;
import com.absolute.util.StringUtilities;
import com.absolute.util.exception.AMWebAPILocalizableException;

public class AMServerProtocol {

	private static Logger m_logger = LoggerFactory
			.getLogger(AMServerProtocol.class.getName());

	private static final int FILE_UPLOAD_CHUNK_LENGTH = 32 * 1024;

	// Data provided by the user.
	private AMServerProtocolSettings m_settings;

	// Data determined by this class.
	protected Certificate m_certificateProvidedByPeer;
	protected Socket m_clientSocket;
	protected TlsClientProtocol m_tlsProtocolHandler;

	public AMServerProtocol(AMServerProtocolSettings settings) {
		this.m_settings = settings;
	}

	/**
	 * @return the serverHostname
	 */
	public AMServerProtocolSettings getSettings() {
		return m_settings;
	}

	/**
	 * @param serverHostname the serverHostname to set
	 */
	public void setSettings(AMServerProtocolSettings settings) {
		this.m_settings = settings;
	}

	/**
	 * @return the certificateProvidedByPeer
	 * @throws AMWebAPILocalizableException
	 */
	public Certificate getCertificateProvidedByPeer() throws AMWebAPILocalizableException {
		if (null == m_certificateProvidedByPeer) {
			AMWebAPILocalizableException ex = new AMWebAPILocalizableException(
				createExceptionMap("AMSERVERPROTOCOL_CERT_ONLY_AVAILABLE_AFTER_CONNECTION_ESTABLISHED", null, null, null));
			throw ex;
		}

		return m_certificateProvidedByPeer;
	}

	/**
	 * Returns the peer certificate in PEM format (i.e. -----BEGIN CERTIFICATE-----, base64 cert, -----END CERTIFICATE-----).
	 * 
	 * @return
	 * @throws IOException
	 * @throws AMWebAPILocalizableException
	 */
	public String getCertificateProvidedByPeerInPEMFormat() throws IOException, AMWebAPILocalizableException {
		CharArrayWriter caw = new CharArrayWriter();
		PemWriter writer = new PemWriter(caw);
		PemObject pemObject = new org.bouncycastle.util.io.pem.PemObject("CERTIFICATE", getCertificateProvidedByPeer().getEncoded());
		
		writer.writeObject(pemObject);
		writer.flush();
		writer.close();

		return caw.toString();
	}

	/**
	 * Returns the Server Unique ID as found in the peer server certificate.
	 * 
	 * @return
	 * @throws IOException
	 * @throws AMWebAPILocalizableException
	 */
	public String getPeerServerUniqueId() throws IOException, AMWebAPILocalizableException {
		return X509Helper.GetServerUniqueIDFromCert(getCertificateProvidedByPeer());
	}

	private String getServerDecription() {
		return getSettings().getServerHostname() + ":" + getSettings().getServerPort();
	}

	private class MyTlsAuthentication implements TlsAuthentication {
		private MyTlsClient m_tlsClient;

		public MyTlsAuthentication(MyTlsClient tlsClient) {
			m_tlsClient = tlsClient;
		}

		@Override
		public TlsCredentials getClientCredentials(CertificateRequest arg0)
				throws IOException {

			if (m_logger.isDebugEnabled())
				m_logger.debug("Server [{}] getClientCredentials called, arg0={}", getServerDecription(), arg0.toString());

			return new DefaultTlsSignerCredentials(null, null, null);
		}

		@Override
		public void notifyServerCertificate(org.bouncycastle.crypto.tls.Certificate arg0) throws IOException {
			Certificate[] certs = arg0.getCertificateList();
			if (m_logger.isDebugEnabled())
				m_logger.debug("Server [{}] notifyServerCertificate called, certs.length={}.", getServerDecription(), certs.length);

			// TODO: Currently AM only uses a root self signed cert. If that changes, this will need to be updated to handle a chain.
			m_certificateProvidedByPeer = org.bouncycastle.asn1.x509.Certificate.getInstance(certs[0].getEncoded());

			// Check if we have a matching certificate in our trusted certs folder.
			//
			// TODO: This isn't using the keystore, and it really should as that's what its for,
			// I note however that I am unable to import the server certificate manually, so...
			String pathToTrustedCerts = getSettings().getPathToTrustedCertificates();
			if (pathToTrustedCerts != null && pathToTrustedCerts.length() > 0) {
				try {
					X509Helper.checkCertIsTrusted(m_certificateProvidedByPeer, getSettings().getPathToTrustedCertificates());
				} 
				catch (IOException e) {
					// save the exception, so the outer class can access it.
					m_tlsClient.setLastError(e.toString());
					throw e;
				}
			}
		}
	}

	private class MyTlsClient extends DefaultTlsClient {

		private MyTlsAuthentication m_tlsAuthentication;
		private String lastError;

		/**
		 * @return the lastError
		 */
		public String getLastError() {
			return lastError;
		}

		/**
		 * @param lastError the lastError to set
		 */
		public void setLastError(String lastError) {
			this.lastError = lastError;
		}

		public MyTlsClient() {
			m_tlsAuthentication = new MyTlsAuthentication(this);
		}

		@Override
		public TlsAuthentication getAuthentication() throws IOException {
			return m_tlsAuthentication;
		}

		// This is the magic that makes it work with Absolute Manage, due to their server certificate issues...
		@Override
		protected TlsKeyExchange createRSAKeyExchange() {
			// Override the return object with our own inline override of the method that mucks up the comms...
	        return new TlsRSAKeyExchange(supportedSignatureAlgorithms) {
	        	// This method was copied from the parent class, and the last two calls have been commented out.
	        	// I don't know that the protocol is all that secure given the modifications below, but whatever.
	        	public void processServerCertificate(org.bouncycastle.crypto.tls.Certificate serverCertificate)
    		        throws IOException {
        		
    		        if (serverCertificate.isEmpty()) {
    		            throw new TlsFatalAlert(AlertDescription.bad_certificate);
    		        }

    		        org.bouncycastle.asn1.x509.Certificate x509Cert = serverCertificate.getCertificateAt(0);

    		        SubjectPublicKeyInfo keyInfo = x509Cert.getSubjectPublicKeyInfo();
    		        try {
    		            this.serverPublicKey = PublicKeyFactory.createKey(keyInfo);
    		        }
    		        catch (RuntimeException e) {
    		            throw new TlsFatalAlert(AlertDescription.unsupported_certificate);
    		        }

    		        // Sanity check the PublicKeyFactory
    		        if (this.serverPublicKey.isPrivate()) {
    		            throw new TlsFatalAlert(AlertDescription.internal_error);
    		        }

    		        this.rsaServerPublicKey = validateRSAPublicKey((RSAKeyParameters)this.serverPublicKey);

//	    		    // This breaks the COBRA protocol, for whatever reason, via the server certificate.
//	    		    // I don't know what the AM guys have done, but it doesn't validate on the client.
//		    		TlsUtils.validateKeyUsage(x509Cert, KeyUsage.keyEncipherment);
//	    		    
//	    		    // And funnily enough, this does nothing in the super class in the current version.
//	    		    // That being the case, unless the library is updated, skipping this should be ok...
//		    		super.processServerCertificate(serverCertificate);
//	    		        
//	    		    The above call is trying to execute a method whose sole content is these two comments
//	    		    if (supportedSignatureAlgorithms == null) {
//	    		        /*
//	    		         * TODO RFC 2264 7.4.2. Unless otherwise specified, the signing algorithm for the
//	    		         * certificate must be the same as the algorithm for the certificate key.
//	    		         */
//	    		    }
//	    		    else {
//	    		        /*
//	    		         * TODO RFC 5264 7.4.2. If the client provided a "signature_algorithms" extension, then
//	    		         * all certificates provided by the server MUST be signed by a hash/signature algorithm
//	    		         * pair that appears in that extension.
//	    		         */
//	    		    }
    		    }	        	
	        };
	    }
	}

	
	/**
	 * Send a command. Do not wait for a response.
	 * 
	 * @param command - the command to send
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws AMServerProtocolException
	 * @throws AMWebAPILocalizableException
	 */
	public void sendCommand(ICobraCommand theCommand) throws IOException, GeneralSecurityException, 
			AMServerProtocolException, AMWebAPILocalizableException {

		int headerLen = 34;
		byte[] commandWithHeader = CobraProtocol.commandToBytes(theCommand);

		String serverHostname = getSettings().getServerHostname();
		Short serverPort = getSettings().getServerPort();

		if (null == m_clientSocket) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Connecting to {}...", getServerDecription());

			// TODO: This must timeout reasonably quickly when the server is not reachable.
			SocketFactory sf = SocketFactory.getDefault();
			Socket simpleSock = null;
			try {
				simpleSock = sf.createSocket(serverHostname, serverPort);
			} catch (IOException ioe) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("Failed to connect to {}. Exception was: {}.", getServerDecription(), ioe.toString());
				// throw new AMServerProtocolException("Connecting", getServerDecription(), ioe.toString());
				AMWebAPILocalizableException ex = new AMWebAPILocalizableException(
						createExceptionMap("AMSERVERPROTOCOL_CONNECTING",
								new String[] { getServerDecription() },
								"AMSERVERPROTOCOL_DESCRIPTION",
								new String[] { ioe.toString() }));
				throw ex;
			}

			// send the Cobra header
			if (m_logger.isDebugEnabled())
				m_logger.debug(
						"Connected to {}, sending header [{}] and activating SSL.",
						getServerDecription(), 
						StringUtilities.toHexString(commandWithHeader, 0, headerLen));

			simpleSock.getOutputStream().write(commandWithHeader, 0, headerLen);

			m_tlsProtocolHandler = new TlsClientProtocol(simpleSock.getInputStream(), simpleSock.getOutputStream());
			MyTlsClient myTlsClient = new MyTlsClient();

			try {
				m_tlsProtocolHandler.connect(myTlsClient);
			} catch (IOException ioe) {
				m_logger.error(
						"Failed to activate secure connection to {}. TLSClient last error: {}. Exception was: {}",
						getServerDecription(), myTlsClient.getLastError(),
						ioe.toString());
				// Note: deliberately don't return too much info, as it can be used by a hacker to better understand the system
				// throw new AMServerProtocolException("Activating secure connection", getServerDecription(), "");
				AMWebAPILocalizableException ex = new AMWebAPILocalizableException(
					createExceptionMap(
						"AMSERVERPROTOCOL_ACTIVATING_SECURE_CONNECTION",
						new String[] { getServerDecription() }, null, null));
				throw ex;
			}
		} else {

			if (m_logger.isDebugEnabled())
				m_logger.debug("Using existing connection to send command.");
			headerLen = 0; // forces the write statement below to write all of
							// the message, including the header.
		}

		if (m_logger.isDebugEnabled())
			m_logger.debug("Writing remainder of command over SSL.");
			
		m_tlsProtocolHandler.getOutputStream().write(commandWithHeader, headerLen, commandWithHeader.length - headerLen);
		m_tlsProtocolHandler.getOutputStream().flush();
	}

	/**
	 * Reads a response from the stream.
	 * 
	 * @return
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws GeneralSecurityException
	 * @throws IOException
	 */
	public PropertyList getResponse() throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException {
		// Read response from the server.
		PropertyList retVal = CobraProtocol.readCommand(m_tlsProtocolHandler.getInputStream());

		return retVal;
	}

	/**
	 * Reads a response from the stream and throws an exception if the response
	 * contains an error code.
	 * 
	 * @param contextMessage - an optional context message to be included with the exception message.
	 * 
	 * @return
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws AMServerProtocolException
	 */
	public PropertyList getAndValidateResponse(String contextMessage)
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException {
		// Read response from the server.
		PropertyList serverResponse = CobraProtocol.readCommand(m_tlsProtocolHandler.getInputStream());

		long resultError = (long) serverResponse.get(CobraProtocol.kCobra_XML_CommandResultError);
		if (resultError != 0) {
			if (m_logger.isDebugEnabled())
				m_logger.debug("Error response received from server when [{}]. Response = [{}].", contextMessage, serverResponse.toXMLString());

			throw new AMServerProtocolException(contextMessage, getSettings().getServerHostAndPort(), serverResponse);
		}

		return serverResponse;
	}

	/**
	 * Send a command and read back a response from the stream.
	 * 
	 * @param theCommand - the command to send
	 * 
	 * @return
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 * @throws AMWebAPILocalizableException
	 */
	public PropertyList sendCommandAndGetResponse(ICobraCommand theCommand)
			throws IOException, GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException, AMWebAPILocalizableException {

		sendCommand(theCommand);
		return getResponse();
	}
	
	/**
	 * Send a command, read back the response and confirm that the result error
	 * code is zero. An exception is thrown if the result error code is not
	 * zero.
	 * 
	 * @param command
	 * @param contextMessage - an optional context message to be included with any exceptions thrown.
	 * 
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 * @throws AMWebAPILocalizableException
	 * @throws Exception
	 */
	public PropertyList sendCommandAndValidateResponse(ICobraCommand theCommand, String contextMessage) 
			throws RuntimeException, IOException, GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException, AMWebAPILocalizableException {
			
		PropertyList response = sendCommandAndValidateResponse(theCommand, contextMessage, null);
		return response;
	}

	/**
	 * Send a command, read back the response and confirm that the result error
	 * code is zero. An exception is thrown if the result error code is not
	 * zero.
	 * 
	 * @param command
	 * @param contextMessage - an optional context message to be included with any exceptions thrown.
	 * @param progressReporter - callback to report progress
	 * 
	 * @throws GeneralSecurityException
	 * @throws IOException
	 * @throws SAXException
	 * @throws ParserConfigurationException
	 * @throws AMServerProtocolException
	 * @throws AMWebAPILocalizableException
	 * @throws Exception
	 */
	public PropertyList sendCommandAndValidateResponse(ICobraCommand theCommand, String contextMessage, IProgressReporter progressReporter)
			throws RuntimeException, IOException, GeneralSecurityException, ParserConfigurationException, SAXException, AMServerProtocolException, AMWebAPILocalizableException {

		sendCommand(theCommand);
		PropertyList response = getAndValidateResponse(contextMessage);
		if (progressReporter != null) {
			progressReporter.reportProgress(100);
		}
		return response;
	}

	private OutputStream getOutputStream() throws IOException {
		if (m_tlsProtocolHandler != null) {
			return m_tlsProtocolHandler.getOutputStream();
		} 
		else {
			return m_clientSocket.getOutputStream();
		}
	}

	/**
	 * Send a file over the channel. The length is written first, followed by
	 * all bytes from the file.
	 * 
	 * @param use64BitLengths- when true, the length of the file will be written as a 64bit value
	 * @param filePath - the path to the file to send.
	 * 
	 * @throws IOException
	 */
	public void sendFile(String filePath, IProgressReporter progressReporter)
			throws IOException {

		// get the length of the file and write it to the stream
		File theFile = new File(filePath);
		long fileLength64 = theFile.length();

		BufferedOutputStream bufferedOS = new BufferedOutputStream(getOutputStream());
		CPLATOutputStream cplatOutputStream = new CPLATOutputStream(bufferedOS);
		cplatOutputStream.writeLong(fileLength64);

		FileInputStream fis = null;
		// open the file, read in blocks and write them out.
		try {
			fis = new FileInputStream(theFile);

			byte[] temp = new byte[FILE_UPLOAD_CHUNK_LENGTH];
			int len = fis.read(temp);
			long offset = 0;

			while (len != -1) {
				if (m_logger.isDebugEnabled())
					m_logger.debug("SendFile offset {}.", offset);

				cplatOutputStream.write(temp, 0, len);
				int percent = (int) Math.ceil(((double) (offset + len) / (double) fileLength64) * 100);
				progressReporter.reportProgress(percent);
				len = fis.read(temp);
				offset += len;
			}
			progressReporter.reportProgress(100);
		} finally {
			try {
				cplatOutputStream.flush();
				fis.close();
			} catch (Exception ex) {
				m_logger.warn("An error occurred during clean-up", ex);
			}
		}
	}

	/**
	 * Closes the communication channel.
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		if (m_tlsProtocolHandler != null) {
			m_tlsProtocolHandler.close();
			m_tlsProtocolHandler = null;
		}

		if (m_clientSocket != null) {
			m_clientSocket.close();
			m_clientSocket = null;
		}
	}

	private Map<String, Object> createExceptionMap(String message,
			String[] msgParams, String description, String[] descrParams) {
		Map<String, Object> exParams = new HashMap<String, Object>();
		exParams.put(AMWebAPILocalizableException.MESSAGE_KEY, message);
		exParams.put(AMWebAPILocalizableException.MESSAGE_KEY_PARAMS, msgParams);
		exParams.put(AMWebAPILocalizableException.ERROR_DESCRIPTION_KEY,
				description);
		exParams.put(AMWebAPILocalizableException.ERROR_DESCRIPTION_KEY_PARAMS,
				descrParams);
		return exParams;
	}
}
