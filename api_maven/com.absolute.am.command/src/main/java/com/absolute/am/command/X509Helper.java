package com.absolute.am.command;

import java.io.FileReader;
import java.io.IOException;

import org.bouncycastle.asn1.x500.RDN;
import org.bouncycastle.asn1.x500.style.RFC4519Style;
import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class X509Helper {
	
	private static Logger m_logger = LoggerFactory.getLogger(X509Helper.class.getName());
	
	public static String CERTIFICATE_FILENAME_EXTENSION = ".pem";
	
	
	private static boolean containsFilePathCharacters(String testString) {
		return (testString.indexOf("..") != -1);
	}

	
	/**
	 * Helper method to extract the X500UniqueIdentifier from the certificate. Throws an exception if it fails.
	 * @param cert The certificate to extract the identifier from.
	 * @return The X500UniqueIdentifier in the certificate.
	 * @throws IOException When not found. Note: IOException was chosen because TlsAuthentication.notifyServerCertificate is
	 * spec'd to throw them. 
	 */
	public static String GetServerUniqueIDFromCert(org.bouncycastle.asn1.x509.Certificate cert) throws IOException {
		String retVal = null;
		
		// Enumerate through the RDNs of the Subject, until the x500UniqueIdentifier is found.
		RDN[] rdns = cert.getSubject().getRDNs();
		for(int i=0; i<rdns.length; i++) {
			org.bouncycastle.asn1.x500.AttributeTypeAndValue[] atav = rdns[i].getTypesAndValues();
			for(int j=0; j<atav.length; j++) {
				if (atav[j].getType().equals(RFC4519Style.x500UniqueIdentifier)) {
					retVal = atav[j].getValue().toString();
					break;
				} 
			}
		}
		
		// Validate the unique id - non null, length > 0, and does not contain any file path characters.
		if (retVal == null || 
				(retVal != null && (retVal.length() == 0 || containsFilePathCharacters(retVal)))) {
			m_logger.debug("GetServerUniqueIDFromCert() failed to find valid X500UniqueIdentifier. Subject={} retVal={} containsFilePathCharacters={}",
					cert.getSubject().toString(),
					retVal,
					containsFilePathCharacters(retVal));
			throw new IOException("X500UniqueIdentifier missing/invalid in certificate");
		}
				
		return retVal;
	}

	/**
	 * Helper to m_logger.info a description of the certificate.
	 * @param prefix - a message/label to prefix each line of output with.
	 * @param cert - the cert to log.
	 */
	public static void logCertInfo(String prefix, org.bouncycastle.asn1.x509.Certificate cert) {
		if (m_logger.isInfoEnabled()) {
			m_logger.info("{}: Subject: {}", prefix, cert.getSubject().toString());
			m_logger.info("{}: Issuer: {}", prefix, cert.getIssuer().toString());
			m_logger.info("{}: Start Date: {}", prefix, cert.getStartDate());
			m_logger.info("{}: End Date: {}", prefix, cert.getEndDate());
			m_logger.info("{}: Signature: {}", prefix, cert.getSignature().getString().toString());
		}
	}		

	/**
	 * Helper to m_logger.debug a description of the certificate.
	 * @param prefix - a message/label to prefix each line of output with.
	 * @param cert - the cert to log.
	 */
	public static void logCertDebug(String prefix, org.bouncycastle.asn1.x509.Certificate cert) {
		if (m_logger.isDebugEnabled()) {
			m_logger.debug("{}: Subject: {}", prefix, cert.getSubject().toString());
			m_logger.debug("{}: Issuer: {}", prefix, cert.getIssuer().toString());
			m_logger.debug("{}: Start Date: {}", prefix, cert.getStartDate());
			m_logger.debug("{}: End Date: {}", prefix, cert.getEndDate());
			m_logger.debug("{}: Signature: {}", prefix, cert.getSignature().getString().toString());
		}
	}		

	/**
	 * Helper to check that a reveived cert matches one of those in the folder of trusted certs.
	 * @param cert - the cert to check
	 * @param pathToTrustedCerts - the folder where the trusted certs are stored - the server unique id
	 * of the trusted cert is used as the filename with a .pem extension.
	 * @throws IOException
	 */
	public static void checkCertIsTrusted(org.bouncycastle.asn1.x509.Certificate cert, String pathToTrustedCerts) throws IOException {
		
		String serverUniqueId = X509Helper.GetServerUniqueIDFromCert(cert);	// this throws when not found.
		
		String pathToTrustedCertForThisServerUniqueId = pathToTrustedCerts + serverUniqueId + CERTIFICATE_FILENAME_EXTENSION;
		PemReader reader = new PemReader(new FileReader(pathToTrustedCertForThisServerUniqueId));
		PemObject pemObject = reader.readPemObject();
		reader.close();
		
		if (!pemObject.getType().equalsIgnoreCase("CERTIFICATE")) {
			m_logger.error("PEM file for {} contains pemObject.getType()={}, expected a CERTIFICATE.", serverUniqueId, pemObject.getType());
			throw new IOException("PEM file for " + serverUniqueId + " does not contain a certificate.");
		}

		org.bouncycastle.asn1.x509.Certificate refCert;
		refCert = org.bouncycastle.asn1.x509.Certificate.getInstance(pemObject.getContent());

		if (!refCert.equals(cert)) {
			m_logger.error("Returned cert does not match trusted cert.");
			X509Helper.logCertInfo("Returned cert", refCert);				 
			X509Helper.logCertInfo("Trusted cert", refCert);
			
			throw new IOException("Invalid SSL certificate. It does not match the trusted cert.");			
		}	
	}

}
