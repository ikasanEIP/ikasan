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

import org.ikasan.configurationService.model.ConfigurationParameterObjectImpl;
import org.ikasan.configurationService.model.PlatformConfigurationConfiguredResource;
import org.ikasan.spec.configuration.*;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 
 * @author Ikasan Development Team
 *
 */
public class PlatformConfigurationServiceImpl implements PlatformConfigurationService
{
	private static final int DEFAULT_RESULT_SIZE = 2000;
	
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
        
        if(configuration == null){
            return null;
        }
        Optional<Map<String,String> > parameterMap = ((List<ConfigurationParameter>)configuration.getParameters()).stream()
            .filter(parameter -> parameter instanceof ConfigurationParameterObjectImpl
                && parameter.getValue() != null
                && parameter.getValue() instanceof Map)
            .map(parameter -> (Map<String,String>)parameter.getValue())
            .findAny()
        ;
        

        if(!parameterMap.isPresent())
        {
        	throw new RuntimeException("Cannot resolve the platform configuration map containing the platform configuration!");
        }
        
        return parameterMap.get().get(paramName) == null ? "":parameterMap.get().get(paramName);
	}

	@Override
	public void saveConfigurationValue(String paramName, String value)
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();

		Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);

        if(configuration == null){
            return ;
        }

        Optional<Map<String,String> > parameterMap = ((List<ConfigurationParameter>)configuration.getParameters()).stream()
            .filter(parameter -> parameter instanceof ConfigurationParameterObjectImpl
                && parameter.getValue() != null
                && parameter.getValue() instanceof Map)
            .map(parameter -> (Map<String,String>)parameter.getValue())
            .findAny()
            ;


        if(!parameterMap.isPresent())
        {
            throw new RuntimeException("Cannot resolve the platform configuration map containing the platform configuration!");
        }

		parameterMap.get() .put(paramName, value);

		this.configurationManagement.saveConfiguration(configuration);
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
        
		return "";
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
        
        return "";
	}

	@Override
	public void saveWebServiceUsername(String username)
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();

		Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);

		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();

		for(ConfigurationParameter parameter: parameters)
		{
			if(parameter.getName().equals("webServiceUserAccount"))
			{
				parameter.setValue(username);
			}
		}

		this.configurationManagement.saveConfiguration(configuration);
	}

	@Override
	public void saveWebServicePassword(String password)
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();

		Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);

		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();

		for(ConfigurationParameter parameter: parameters)
		{
			if(parameter.getName().equals("webServiceUserPassword"))
			{
				parameter.setValue(password);
			}
		}

		this.configurationManagement.saveConfiguration(configuration);
	}

	@Override
	public void saveSolrUsername(String username)
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();

		Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);

		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();

		for(ConfigurationParameter parameter: parameters)
		{
			if(parameter.getName().equals("solrUserAccount"))
			{
				parameter.setValue(username);
			}
		}

		this.configurationManagement.saveConfiguration(configuration);
	}

	@Override
	public void saveSolrPassword(String password)
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();

		Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);

		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();

		for(ConfigurationParameter parameter: parameters)
		{
			if(parameter.getName().equals("solrUserPassword"))
			{
				parameter.setValue(password);
			}
		}

		this.configurationManagement.saveConfiguration(configuration);
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.PlatformConfigurationService#getWebServiceUsername()
	 */
	@Override
	public String getSolrUsername()
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();

		Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);

		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();

		for(ConfigurationParameter parameter: parameters)
		{
			if(parameter.getName().equals("solrUserAccount"))
			{
				return (String)parameter.getValue();
			}
		}

		return "";
	}

	/* (non-Javadoc)
	 * @see org.ikasan.spec.configuration.PlatformConfigurationService#getWebServicePassword()
	 */
	@Override
	public String getSolrPassword()
	{
		PlatformConfigurationConfiguredResource platformConfigurationConfiguredResource = new PlatformConfigurationConfiguredResource();

		Configuration configuration = this.configurationManagement.getConfiguration(platformConfigurationConfiguredResource);

		final List<ConfigurationParameter> parameters = (List<ConfigurationParameter>)configuration.getParameters();

		for(ConfigurationParameter parameter: parameters)
		{
			if(parameter.getName().equals("solrUserPassword"))
			{
				return (String)parameter.getValue();
			}
		}

		return "";
	}

	/* (non-Javadoc)
         * @see org.ikasan.spec.configuration.PlatformConfigurationService#getSearchResultSetSize()
         */
	@Override
	public Integer getSearchResultSetSize()
	{
		String resultSetSizeString = this.getConfigurationValue(PlatformConfigurationConstants.RESULT_SET_SIZE);
		
		Integer size = null;
		
		try
		{
			size = new Integer(resultSetSizeString);
		}
		catch(NumberFormatException e)
		{
			size = DEFAULT_RESULT_SIZE;
		}
		
		return size;
	}	

}
