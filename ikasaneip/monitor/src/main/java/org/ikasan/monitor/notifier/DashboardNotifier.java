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
package org.ikasan.monitor.notifier;

import org.springframework.http.*;
import org.jasypt.contrib.org.apache.commons.codec_1_3.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.ikasan.spec.monitor.Notifier;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.util.Arrays;

/**
 * Ikasan default dashboard notifier implementation.
 *
 * @author Ikasan Development Team
 */
public class DashboardNotifier implements Notifier<String>
{
    /**
     * logger instance
     */
    private static Logger logger = LoggerFactory.getLogger(DashboardNotifier.class);

    /**
     * only interested in state changes
     */
    boolean notifyStateChangesOnly = true;

    /**
     * the base url of the dashboard
     */
    private String dashboardBaseUrl;

    /**
     * the platform configuration service
     */
    protected PlatformConfigurationService platformConfigurationService;

    private RestTemplate restTemplate;

    public DashboardNotifier()
    {
        restTemplate = new RestTemplate();
        restTemplate
            .setMessageConverters(Arrays.asList(new ByteArrayHttpMessageConverter(), new StringHttpMessageConverter()));
    }

    @Override public void invoke(String environment, String moduleName, String flowName, String state)
    {
        notify(environment, moduleName, flowName, state);
    }

    @Override public void setNotifyStateChangesOnly(boolean notifyStateChangesOnly)
    {
        this.notifyStateChangesOnly = notifyStateChangesOnly;
    }

    @Override public boolean isNotifyStateChangesOnly()
    {
        return this.notifyStateChangesOnly;
    }

    /**
     * @return the dashboardBaseUrl
     */
    public void setDashboardBaseUrl(String dashboardBaseUrl)
    {
        this.dashboardBaseUrl = dashboardBaseUrl;
    }

    /**
     * @param platformConfigurationService
     */
    public void setPlatformConfigurationService(PlatformConfigurationService platformConfigurationService)
    {
        this.platformConfigurationService = platformConfigurationService;
    }

    /**
     * @return the dashboardBaseUrl
     */
    public String getDashboardBaseUrl()
    {
        return dashboardBaseUrl;
    }

    /**
     * @return the platformConfigurationService
     */
    public PlatformConfigurationService getPlatformConfigurationService()
    {
        return platformConfigurationService;
    }

    /**
     * Internal notify method
     *
     * @param environment
     * @param moduleName
     * @param flowName
     * @param state
     */
    protected void notify(String environment, String moduleName, String flowName, String state)
    {
        String url = null;
        try
        {
            logger.info("this.platformConfigurationService: " + this.platformConfigurationService);
            // We are trying to get the database configuration resource first
            if (this.platformConfigurationService != null)
            {
                url = platformConfigurationService.getConfigurationValue("dashboardBaseUrl");
            }
            logger.debug("url: " + url);
            // If we do not have a database persisted configuration value we will try to get the one from the file system/
            if ((url == null || url.length() == 0) && this.dashboardBaseUrl != null
                && this.dashboardBaseUrl.length() > 0)
            {
                url = this.dashboardBaseUrl;
            }
            // Otherwise we'll throw an exception!
            if (url == null || url.length() == 0)
            {
                throw new RuntimeException("Cannot notify dashboard. The dashboard URL is null or empty string!");
            }
            url = url + "/rest/topologyCache/updateCache/"
                + moduleName.replace(" ", "%20")
                + "/"
                +  flowName.replace(" ", "%20");

            logger.info(String.format("Notifiy Ikasan Dashboard of flow state change with call to URL[%s] and State[%s].", url, state));
            HttpEntity request = initRequest(state, moduleName, null, null);
            ResponseEntity<String> respose = restTemplate.exchange(new URI(url), HttpMethod.PUT, request, String.class);

            logger.info(String.format("Notifiy Ikasan Dashboard response. HTTP Status Code[%s], HTTP Response Message[%s]"
                , respose.getStatusCode().toString(), respose.getBody()));
        }
        catch (final HttpClientErrorException e)
        {
            throw new RuntimeException("An exception occurred trying to notify the dashboard!", e);
        }
        catch (Exception e)
        {
            throw new RuntimeException("An exception occurred trying to notify the dashboard!", e);
        }
    }

    private HttpEntity initRequest(String body, String module, String user, String password)
    {
        HttpHeaders headers = new HttpHeaders();
        if (user != null && password != null)
        {
            String credentials = user + ":" + password;
            String encodedCridentials = new String(Base64.encodeBase64(credentials.getBytes()));
            headers.set(HttpHeaders.AUTHORIZATION, "Basic " + encodedCridentials);
        }
        headers.set(HttpHeaders.USER_AGENT, module);
        headers.set(HttpHeaders.CONTENT_TYPE,"application/json");
        return new HttpEntity(body, headers);
    }
}
