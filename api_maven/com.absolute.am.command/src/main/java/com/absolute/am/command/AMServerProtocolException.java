package com.absolute.am.command;

import com.absolute.util.PropertyList;

public class AMServerProtocolException extends Exception {
		
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private long m_code = 0;
	//private String m_message;
	private String m_description;
	private String m_debugMessage;
	private String m_contextMessage;
	private String m_serverHostAndPort;

	
	public AMServerProtocolException(String contextMessage, String serverHostAndPort, PropertyList serverResponse) {

		super(contextMessage);
		
		this.m_contextMessage = contextMessage;
		this.m_code = (long)serverResponse.get(CobraProtocol.kCobra_XML_CommandResultError);
		this.m_description = (String)serverResponse.get(CobraProtocol.kCobra_XML_CommandResultErroString);
		this.m_debugMessage = (String)serverResponse.get(CobraProtocol.kCobra_XML_CommandResultErrorDebugInfo);
		this.m_serverHostAndPort = serverHostAndPort;
	}
	
	
	public AMServerProtocolException(String contextMessage, String serverHostAndPort, String description) {
		
		super(contextMessage);

		this.m_contextMessage = contextMessage;
		this.m_code = 0;
		this.m_description = description;
		this.m_debugMessage = "";		
		this.m_serverHostAndPort = serverHostAndPort;
	}
	
	public long getCode() {
		return m_code;
	}

	public String getDescription() {
		return m_description;
	}

	public String getDebugMessage() {
		return m_debugMessage;
	}
	
	public String getContextMessage() {
		return m_contextMessage;
	}
	
	public String getServerHostAndPort() {
		return m_serverHostAndPort;
	}

	/* (non-Javadoc)
	 * @see java.lang.Throwable#getMessage()
	 */
	@Override
	public String getMessage() {
		// TODO Auto-generated method stub
		StringBuilder sb = new StringBuilder();

		sb.append(m_contextMessage);
		sb.append(String.format(" Error: (0x%1$08X)", m_code));
		sb.append(" Server: ").append(m_serverHostAndPort);

		if (m_description != null && m_description.length()>0) {
			sb.append(" Error description: '").append(m_description);
		}
				
		if (m_debugMessage != null && m_debugMessage.length() > 0) {
			sb.append(" Error debug message: '").append(m_debugMessage);
		}

		return sb.toString();
	}	
}
