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
 * This class should be used by Fixtures as a transport mechanism to make <b> HTTP </b> <b>POST and GET</b> calls
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
