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
package org.ikasan.solr.embedded;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.request.CoreAdminRequest;
import org.apache.solr.client.solrj.response.CoreAdminResponse;
import org.apache.solr.core.NodeConfig;
import org.apache.solr.core.SolrResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class IkasanEmbeddedSolrAutoConfiguration
{
    private static Logger logger = LoggerFactory.getLogger(IkasanEmbeddedSolrAutoConfiguration.class);

    public static final String CORE_NAME = "ikasan";
    public static final String NODE_NAME = "ikasan-node";
    public static final String CONFIG_SET = "minimal";

    @Value("${solr.temp.dir}")
    private String solrTempDir;


    @Bean
    public EmbeddedSolrServer solrServer() throws IOException, SolrServerException, URISyntaxException
    {
        Path path = Paths.get(this.getClass().getClassLoader().getSystemResource(this.solrTempDir).toURI());

        SolrResourceLoader loader = new SolrResourceLoader(path, this.getClass().getClassLoader());
        NodeConfig config = new NodeConfig.NodeConfigBuilder(NODE_NAME, loader)
            .setConfigSetBaseDirectory(Paths.get("solr").resolve("configsets").toString())
            .build();

        EmbeddedSolrServer server = new EmbeddedSolrServer(config, CORE_NAME);

        CoreAdminResponse status =
            CoreAdminRequest.getStatus(CORE_NAME, server);

        if(status.getCoreStatus(CORE_NAME).get("instanceDir") == null)
        {
            logger.info("Creating new {} solr core!", CORE_NAME);
            CoreAdminRequest.Create createRequest = new CoreAdminRequest.Create();
            createRequest.setCoreName(CORE_NAME);
            createRequest.setConfigSet(CONFIG_SET);
            server.request(createRequest);
        }
        else
        {
            logger.info("Core {} is already created!", CORE_NAME);
        }

        return server;
    }
}
