/* 
 * $Id$
 * $URL$
 *
 * ====================================================================
 * Ikasan Enterprise Integration Platform
 * 
 * Distributed under the Modified BSD License.
 * Copyright notice: The copyright for this software and a full listing 
 * of individual contributors are as shown in the packaged copyright.txt 
 * file. 
 * 
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without 
 * modification, are permitted provided that the following conditions are met:
 *
 *  - Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  - Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  - Neither the name of the ORGANIZATION nor the names of its contributors may
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE 
 * FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL 
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR 
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER 
 * CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
 * OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE 
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 * ====================================================================
 */
package org.ikasan.platform.service;

import java.io.IOException;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.AuthState;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;

import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.protocol.ClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.ExecutionContext;
import org.apache.http.protocol.HttpContext;
import org.apache.log4j.Logger;
import org.ikasan.platform.service.HousekeeperService;

/**
 * A HTTP Implementation of the Housekeeper interface
 * 
 * @author Ikasan Development Team
 */
public class HousekeeperServiceHttpImpl implements HousekeeperService {
	/** Logger for this class */
	private static Logger logger = Logger
			.getLogger(HousekeeperServiceHttpImpl.class);

	/** The URL to execute the Wiretap Event Housekeeping */
	private String wiretapEventHousekeepingUrl;

	/**
	 * Constructor
	 * 
	 * @param wiretapEventHousekeepingUrl
	 *            - wiretapEventHousekeepingUrl to set
	 */
	public HousekeeperServiceHttpImpl(String wiretapEventHousekeepingUrl) {
		this.wiretapEventHousekeepingUrl = wiretapEventHousekeepingUrl;
	}

	/**
	 * Housekeeps Wiretapped Events
	 * 
	 * TODO Error handling is lazy and captures any exception, this may actually
	 * be OK but needs review.
	 */
	public void housekeepWiretapEvents() {
		// Create the client and set the username and password
		DefaultHttpClient httpclient = new DefaultHttpClient();
		Credentials defaultcreds = new UsernamePasswordCredentials(
				"housekeeper", "housekeeper");
		// TODO Using AuthScope.ANY is not best practice, we can probably
		// Get the correct details from the URL passed in
		httpclient.getCredentialsProvider().setCredentials(AuthScope.ANY,
				defaultcreds);

		// Generate BASIC scheme object and stick it to the local execution
		// context
		BasicHttpContext localcontext = new BasicHttpContext();
		BasicScheme basicAuth = new BasicScheme();
		localcontext.setAttribute("preemptive-auth", basicAuth);

		// We're going to Preemptively Auth here
		httpclient.addRequestInterceptor(new PreemptiveAuth(), 0);

		HttpPost httppost = new HttpPost(this.wiretapEventHousekeepingUrl);
		try {
			logger.info("Calling [" + this.wiretapEventHousekeepingUrl + "]");
			HttpResponse response = httpclient.execute(httppost, localcontext);
			int statusCode = response.getStatusLine().getStatusCode();
			// TODO Investigate why we get a 302 back, probably because of the
			// authentication moving us
			if (!(statusCode == HttpStatus.SC_MOVED_TEMPORARILY || statusCode == HttpStatus.SC_OK)) {
				logger.error("Call failed, Status Code = [" + statusCode + "]");
				throw new Exception();
			}
			logger.info("housekeepWiretapEvents was called successfully.");
		} catch (Exception e) {
			logger.error("Call to housekeep failed.");
		} finally {
			// When HttpClient instance is no longer needed,
			// shut down the connection manager to ensure
			// immediate deallocation of all system resources
			httpclient.getConnectionManager().shutdown();
		}
	}

	/** Helper class to sort out preemtive auth */
	static class PreemptiveAuth implements HttpRequestInterceptor
	{

		public void process(final HttpRequest request, final HttpContext context)
				throws HttpException, IOException
		{

			AuthState authState = (AuthState) context
					.getAttribute(ClientContext.TARGET_AUTH_STATE);

			// If no auth scheme available yet, try to initialize it preemptively
			// TODO Trace through this to see if it ever gets called.
			if (authState.getAuthScheme() == null)
			{
				AuthScheme authScheme = (AuthScheme) context.getAttribute("preemptive-auth");
				CredentialsProvider credsProvider = (CredentialsProvider) context.getAttribute(ClientContext.CREDS_PROVIDER);
				HttpHost targetHost = (HttpHost) context.getAttribute(ExecutionContext.HTTP_TARGET_HOST);
				if (authScheme != null)
				{
					Credentials creds = credsProvider
							.getCredentials(new AuthScope(targetHost
									.getHostName(), targetHost.getPort()));
					if (creds == null)
					{
						throw new HttpException(
								"No credentials for preemptive authentication");
					}
					authState.setAuthScheme(authScheme);
					authState.setCredentials(creds);
				}
			}
		}
	}
}
