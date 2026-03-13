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
package org.ikasan.spec.solr;

import java.util.List;

/**
 * This Service allows solr related classes to be initialised as well
 * as providing some general methods.
 * 
 * @author Ikasan Development Team
 */
public interface SolrInitialisationService
{

    /**
     * Initializes a Solr Cloud configuration by specifying the cluster URLs,
     * data retention policy, and connection timeout parameters.
     *
     * @param solrCloudUrls the list of URLs for connecting to the Solr Cloud cluster
     * @param daysToKeep the number of days for which records should be retained in the Solr index
     * @param connectionTimeoutMilli the timeout value in milliseconds for establishing connections to the Solr Cloud cluster
     */
    void initCloud(List<String> solrCloudUrls, int daysToKeep,
                          int connectionTimeoutMilli);

    /**
     * Initializes a standalone Solr service by setting up the connection details
     * and specifying the data retention policy.
     *
     * @param solrUrl the URL of the Solr instance to connect to
     * @param daysToKeep specifies the number of days for which records should be retained
     * @param connectionTimeoutMilli the timeout value in milliseconds for establishing Solr connections
     */
    void initStandalone(String solrUrl, int daysToKeep,
                               int connectionTimeoutMilli);
}
