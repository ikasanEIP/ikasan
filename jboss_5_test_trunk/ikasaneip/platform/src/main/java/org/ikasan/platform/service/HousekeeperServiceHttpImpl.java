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

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.log4j.Logger;
import org.ikasan.platform.service.HousekeeperService;

/**
 * A HTTP Implementation of the Housekeeper interface 
 * 
 * @author Ikasan Development Team
 */
public class HousekeeperServiceHttpImpl implements HousekeeperService 
{
    /** Logger for this class */
    private static Logger logger = Logger.getLogger(HousekeeperServiceHttpImpl.class);
    
    /** The URL to execute the Wiretap Event Housekeeping */
    private String wiretapEventHousekeepingUrl;
    
    /**
     * Constructor
     * @param wiretapEventHousekeepingUrl - wiretapEventHousekeepingUrl to set
     */
    public HousekeeperServiceHttpImpl(String wiretapEventHousekeepingUrl)
    {
        this.wiretapEventHousekeepingUrl = wiretapEventHousekeepingUrl;
    }
    
    /**
     * Housekeeps Wiretapped Events
     * 
     * TODO Error handling is lazy and captures any exception, 
     * this may actually be OK but needs review.
     */
    public void housekeepWiretapEvents()
    {
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget = new HttpGet(this.wiretapEventHousekeepingUrl);
        try
        {
            logger.info("Calling [" + this.wiretapEventHousekeepingUrl + "]");
            HttpResponse response = httpclient.execute(httpget);
            int statusCode = response.getStatusLine().getStatusCode();
            if (statusCode != HttpStatus.SC_OK)
            {
                logger.error("Call failed, Status Code = [" + statusCode + "]");
                throw new Exception();
            }
            logger.info("housekeepWiretapEvents was called successfully.");
        }
        catch (Exception e)
        {
            logger.error("Call to housekeep failed.");
        }
    }

}
