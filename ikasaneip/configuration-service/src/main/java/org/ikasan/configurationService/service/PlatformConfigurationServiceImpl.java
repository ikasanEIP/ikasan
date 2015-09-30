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

import java.util.List;

import org.ikasan.configurationService.model.ConfigurationParameterMapImpl;
import org.ikasan.configurationService.model.PlatformConfigurationConfiguredResource;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationManagement;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.ikasan.spec.configuration.PlatformConfigurationService;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class PlatformConfigurationServiceImpl implements PlatformConfigurationService
{
	protected ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement;
	
	public PlatformConfigurationServiceImpl(ConfigurationManagement<ConfiguredResource, Configuration> configurationManagement)
	{
		this.configurationManagement = configurationManagement;
		if(this.configurationManagement == null)
		{
			throw new IllegalArgumentException("configuration management cannot be null!");
		}
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.PlatformConfigurationService#getConfigurationValue(java.lang.String)
	 */
	@Override
	public String getConfigurationValue(String paramName)
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();
        
        Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);
        
        final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();
        
        ConfigurationParameterMapImpl parameterMap = null;
        
        for(ConfigurationParameter parameter: parameters)
        {
        	if(parameter instanceof ConfigurationParameterMapImpl)
        	{
        		parameterMap = (ConfigurationParameterMapImpl)parameter;
        	}
        }
        
        if(parameterMap == null)
        {
        	throw new RuntimeException("Cannot resolve the platform configuration map containing the platform configuration!");
        }
        
        
        return parameterMap.getValue().get(paramName);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.PlatformConfigurationService#getWebServiceUsername()
	 */
	@Override
	public String getWebServiceUsername()
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();
        
        Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);
        
        final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();
        
        for(ConfigurationParameter parameter: parameters)
        {
        	if(parameter.getName().equals("webServiceUserAccount"))
        	{
        		return (String)parameter.getValue();
        	}
        }
        
		return null;
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.PlatformConfigurationService#getWebServicePassword()
	 */
	@Override
	public String getWebServicePassword()
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();
        
        Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);
        
        final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();
        
        for(ConfigurationParameter parameter: parameters)
        {
        	if(parameter.getName().equals("webServiceUserPassword"))
        	{
        		return (String)parameter.getValue();
        	}
        }
        
        return null;
	}

	

}
