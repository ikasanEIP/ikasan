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
package org.ikasan.dashboard.configurationManagement.rest;

import org.apache.log4j.Logger;
import org.ikasan.dashboard.configurationManagement.util.ModuleConfigurationExportHelper;
import org.ikasan.dashboard.ui.framework.cache.TopologyStateCache;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.topology.model.Module;
import org.ikasan.topology.service.TopologyService;
import org.springframework.beans.factory.annotation.Autowired;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

/**
 * Module application implementing the REST contract
 */
@Path("/configuration")
public class ConfigurationApplication
{
	private static Logger logger = Logger.getLogger(ConfigurationApplication.class);

	@Autowired
	private TopologyService topologyService;

    @Autowired
    private ConfigurationManagement<ConfiguredResource, Configuration> configurationService;

    /**
     * Registers the applications we implement and the Spring-Jersey glue
     */
    public ConfigurationApplication()
    {
    }

    @GET
	@Path("/get/{moduleName}")
    @Produces(MediaType.APPLICATION_JSON)
    public String getModule(@PathParam("moduleName") String moduleName)
    {
        logger.info("Getting configuration: ModuleName: "
        		+ moduleName);

        Module module = this.topologyService.getModuleByName(moduleName);

        ModuleConfigurationExportHelper helper
                = new ModuleConfigurationExportHelper(module, this.configurationService);

        return  helper.getModuleConfigurationExportXml();
    }
    
}
