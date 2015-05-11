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
package org.ikasan.module.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.jboss.security.Base64Encoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * 
 * @author Ikasan Development Team
 *
 */
public class HttpTransport
{
    private static final Logger LOG = LoggerFactory.getLogger(HttpTransport.class);

    private String user;

    private String password;

    public void setCredentials(String userId, String password)
    {
        this.user = userId;
        this.password = password;
    }

    /**
     * Allows the caller to make http get request to specific url
     * 
     * @param targetURL
     * @param urlParameters
     * @return
     * @throws Exception
     */
    public String executeGet(String targetURL, String urlParameters)
    {
        String result = "";
        if (urlParameters != null && urlParameters.length() > 0)
        {
            targetURL = targetURL + "?" + urlParameters;
        }
        try
        {
            HttpURLConnection connection = getConnection("GET", targetURL);
            result = execute(connection, urlParameters);
        }
        catch (Exception e)
        {
            LOG.error("Exception occurred while calling HTTP endpoint " + targetURL + " With Parameters " + urlParameters, e);
            return null;
        }
        return result;
    }

    /**
     * Allows the caller to make a http post request to the given targetUrl with the given urlParameters
     * 
     * @param targetURL
     * @param urlParameters
     * @return Returns the response of the http post request
     * @throws Exception
     */
    public String executePost(String targetURL, String urlParameters)
    {
        String result = "";
        try
        {
            HttpURLConnection connection = getConnection("POST", targetURL);
            result = execute(connection, urlParameters);
        }
        catch (Exception e)
        {
            LOG.error("Exception occurred while calling HTTP endpoint " + targetURL + " With Parameters " + urlParameters, e);
            return null;
        }
        return result;
    }

    private HttpURLConnection getConnection(String requestMethod, String targetUrl) throws Exception
    {
        URL url = new URL(targetUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(requestMethod);
        connection.setRequestProperty("User-Agent", "Mozilla/5.0");
        if (user != null && user.length() > 0)
        {
            LOG.info("Adding authentication information for user: {}", user);
            String userPwd = user.concat(":").concat(password);
            String encoding = Base64Encoder.encode(userPwd.getBytes());
            connection.setRequestProperty("Authorization", "Basic " + encoding);
        }
        connection.setUseCaches(false);
        connection.setDoInput(true);
        connection.setDoOutput(true);
        return connection;
    }

    private String execute(HttpURLConnection connection, String urlParameters)
    {
        try
        {
            if (connection.getRequestMethod().equalsIgnoreCase("POST"))
            {
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(urlParameters);
                wr.flush();
                wr.close();
            }
            BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuffer response = new StringBuffer();
            while ((line = rd.readLine()) != null)
            {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            return response.toString();
        }
        catch (Exception ex)
        {
            LOG.error("Exception occurred while calling HTTP endpoint " + connection.getURL().getPath() + " With Parameters " + urlParameters, ex);
            return null;
        }
        finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }
}
