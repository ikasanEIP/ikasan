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
package org.ikasan.configurationService.service;

import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.annotation.Resource;
import org.ikasan.configurationService.model.PlatformConfiguration;
import org.ikasan.configurationService.model.PlatformConfigurationConfiguredResource;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.PlatformConfigurationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

/**
 * 
 * @author Ikasan Development Team
 *
 */
@SpringJUnitConfig(classes = {TestConfiguration.class})
class PlatformConfigurationTest
{
	
	@Resource
	ConfiguredResourceConfigurationService configurationService;
	
	@Resource
    PlatformConfigurationService platformConfigurationService;

    @BeforeEach
    void setup()
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();
		
		Configuration platformConfiguration = this.configurationService.getConfiguration(platformConfigurationConfiguredResource);
		
		// create the configuration if it does not already exist!
		if(platformConfiguration == null)
		{
			platformConfigurationConfiguredResource.setConfiguration(new PlatformConfiguration());
			platformConfiguration = this.configurationService.createConfiguration(platformConfigurationConfiguredResource);
		}
		
		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)platformConfiguration.getParameters();      

		ConfigurationParameter mapParam = null;
        
        for(ConfigurationParameter parameter: parameters)
        {
            if(parameter.getName().equals("configurationMap"))
        	{
        		mapParam = parameter;
        	}
        }
        
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("value1", "value1");
        map.put("value2", "value2");
        
        mapParam.setValue(map);
    	
		configurationService.saveConfiguration(platformConfiguration);      
	}

    @Test
        @DirtiesContext
    void test_platform_configuration_map_1()
    {
        assertEquals("value1", platformConfigurationService.getConfigurationValue("value1"));
    }

    @Test
        @DirtiesContext
    void test_platform_configuration_map_2()
    {
        assertEquals("value2", platformConfigurationService.getConfigurationValue("value2"));
    }
}
