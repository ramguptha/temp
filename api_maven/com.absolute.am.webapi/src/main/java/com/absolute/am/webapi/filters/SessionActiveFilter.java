/**
 * 
 */
package com.absolute.am.webapi.filters;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

import com.absolute.am.webapi.Application;
import com.absolute.am.webapi.controllers.Login;

/**
 * This filter rejects access if there is no currently active session.
 *
 */
public class SessionActiveFilter implements Filter {
	
    private static Logger m_logger = LoggerFactory.getLogger(SessionActiveFilter.class.getName());
	
	protected ServletContext servletContext;
	protected String bypassPatterns;
	

	/** (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	@Override
	public void destroy() {
	}

	/**
	 * Rejects a request if there is no currently active session.
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse,
			FilterChain filterChain) throws IOException, ServletException {
		
		// Clear the MDC map so logging data is not crossing sessions.
		MDC.clear();
		
		HttpServletRequest httpRequest = (HttpServletRequest)servletRequest;
		HttpServletResponse httpResponse = (HttpServletResponse)servletResponse;
		MDC.put("RemoteAddr", httpRequest.getRemoteAddr());
		String cleanPath = httpRequest.getRequestURI().substring(httpRequest.getContextPath().length());
		boolean isSSPRequest = cleanPath.startsWith("/api/ssp"), isSSPSession;
		
		// we should never cache the web api data
		httpResponse.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1.
		httpResponse.setHeader("Pragma", "no-cache"); // HTTP 1.0.
		httpResponse.setDateHeader("Expires", 0); // Proxies.
		
		httpResponse.setHeader(Login.WEB_API_VERSION_PARAM, Application.WEB_API_VERSION); // Set the AM Web API version
		
		if (!matchesBypassPatterns(cleanPath)) {
			
			// check that there is an active session
			HttpSession session = httpRequest.getSession(false);
			if ( session == null ) {
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			} else {				
				// set the MDC
				Application.putSessionIdInMDC(session.getId());
			}
			
			// let's make sure that API request matches the session type
			isSSPSession = (session.getAttribute(com.absolute.am.webapi.ssp.controllers.Login.IS_SSP_SESSION) != null);
			
			if( (isSSPRequest && !isSSPSession) || (!isSSPRequest && isSSPSession) ){
				httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
		} else {
			m_logger.debug("SessionFilter allowing path {} contentType={}.", cleanPath, servletRequest.getContentType());
		}
		
		filterChain.doFilter(servletRequest, servletResponse);

		// Clear the MDC map so logging data doesn't cross sessions.
		MDC.clear();
		
	}

	/**
	 * Checks if a the requested URI can bypass this filter.
	 * @param aURI the URI to check
	 * @return <code>true</code> if the URI matches the configured exclude patterns
	 */
	private boolean matchesBypassPatterns(String aURI) {
		boolean retVal = false;
		
		if (null != bypassPatterns &&
				!bypassPatterns.isEmpty()) {

			if (aURI != null ) {
				retVal = aURI.matches(bypassPatterns);	
			}		
		}
		
		servletContext.log("SessionActiveFilter.matchesBypassPatterns returning " + retVal + " for aURI=" + aURI);
		
		return retVal;
	}

	/**
	 * Save the context and load the bypassPatterns.
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		servletContext = filterConfig.getServletContext();
		bypassPatterns = filterConfig.getInitParameter("bypassPatterns");
		if (null == bypassPatterns) {
			bypassPatterns = "";
		}
		servletContext.log("SessionActiveFilter initialized. bypassPatterns=" + bypassPatterns);
		
		servletContext.log("Logging System.getenv() settings.");
		try {
			
			StringBuilder sb = new StringBuilder();
			Map<String, String> envProps = System.getenv();
			for(Entry<String, String> entry : envProps.entrySet()) {
				sb.append(entry.getKey()).append("=").append(entry.getValue().toString());
				sb.append("\n");
			}
			servletContext.log(sb.toString());
		} catch (Exception ex) {
			servletContext.log("Failed, ex=" + ex.toString());
		}		
		
		
		servletContext.log("Logging System.getenv() settings.");
		try {
			StringBuilder sb = new StringBuilder();
			Properties props = System.getProperties();
			for(Entry<Object, Object> propEntry : props.entrySet()) {
				sb.append(propEntry.getKey().toString()).append("=").append(propEntry.getValue().toString());
				sb.append("\n");			
			}
			servletContext.log(sb.toString());
		} catch (Exception ex) {
			servletContext.log("Failed, ex=" + ex.toString());
		}
	}
}
