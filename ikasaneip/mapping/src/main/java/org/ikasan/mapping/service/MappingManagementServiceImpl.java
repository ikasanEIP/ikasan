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
package org.ikasan.mapping.service;

import java.util.List;
import org.slf4j.Logger; import org.slf4j.LoggerFactory;
import org.ikasan.mapping.dao.MappingConfigurationDao;
import org.ikasan.mapping.model.*;
import org.ikasan.mapping.service.configuration.MappingConfigurationServiceConfiguration;
import org.springframework.dao.DataAccessException;

/**
 * @author Ikasan Development Team
 *
 */
public class MappingManagementServiceImpl implements MappingManagementService
{
    private static Logger logger = LoggerFactory.getLogger(MappingManagementServiceImpl.class);

    
    protected final MappingConfigurationDao dao;
    protected MappingConfigurationServiceConfiguration configuration;

    /**
     * Constructor
     *
     * @param dao
     */
    public MappingManagementServiceImpl(final MappingConfigurationDao dao)
    {
        this.dao = dao;
        if (this.dao == null)
        {
            throw new IllegalArgumentException("The MappingConfigurationDao cannot be null.");
        }
    }

    /* (non-Javadoc)
         * @see org.ikasan.mapping.service.MappingConfigurationService#saveMappingConfiguration(org.ikasan.mapping.window.MappingConfiguration)
         */
    @Override
    public Long saveMappingConfiguration(MappingConfiguration mappingConfiguration) throws MappingConfigurationServiceException
    {
        Long id;
        
        try
        {
            id = this.dao.storeMappingConfiguration(mappingConfiguration);
        }
        catch(DataAccessException e)
        {
        	logger.error("An error has occurred trying to save a mapping configuration", e);
            throw new MappingConfigurationServiceException(e);
        }

        return id;
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#saveSourceConfigurationValue(org.ikasan.mapping.window.SourceConfigurationValue)
     */
    @Override
    public Long saveSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        return this.dao.storeSourceConfigurationValue(sourceConfigurationValue);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#saveTargetConfigurationValue(org.ikasan.mapping.window.TargetConfigurationValue)
     */
    @Override
    public Long saveTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
    {
        return this.dao.storeTargetConfigurationValue(targetConfigurationValue);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getAllConfigurationTypes()
     */
    @Override
    public List<ConfigurationType> getAllConfigurationTypes()
    {
        return this.dao.getAllConfigurationTypes();
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getAllConfigurationContexts()
     */
    @Override
    public List<ConfigurationContext> getAllConfigurationContexts()
    {
        return this.dao.getAllConfigurationContexts();
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getAllConfigurationServiceClients()
     */
    @Override
    public List<ConfigurationServiceClient> getAllConfigurationServiceClients()
    {
        return this.dao.getAllConfigurationServiceClients();
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#addConfigurationType(org.ikasan.mapping.window.ConfigurationType)
     */
    @Override
    public Long saveConfigurationType(ConfigurationType configurationType)
    {
        return this.dao.storeConfigurationType(configurationType);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#addConfigurationConext(org.ikasan.mapping.window.ConfigurationContext)
     */
    @Override
    public Long saveConfigurationConext(ConfigurationContext configurationContext)
    {
        return this.dao.storeConfigurationContext(configurationContext);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#addConfigurationServiceClient(org.ikasan.mapping.window.ConfigurationServiceClient)
     */
    @Override
    public Long saveConfigurationServiceClient(ConfigurationServiceClient configurationServiceClient)
    {
        return this.dao.storeConfigurationServiceClient(configurationServiceClient);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#addMappingConfiguration(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Long, java.util.List, java.util.List, java.lang.String)
     */
    @Override
    public Long addMappingConfiguration(Long sourceContextId, Long targetContextId, int numberOfParams,
                                        Long configurationTypeId, Long configurationServiceClientId, List<String> keyLocationQueries,
                                        String description)
    {
    	ConfigurationType configurationType = this.getConfigurationTypeById(configurationTypeId);
    	ConfigurationServiceClient configurationServiceClient = this.dao.getConfigurationServiceClientById(configurationServiceClientId);
    	ConfigurationContext sourceConfigurationContext = this.dao.getConfigurationContextById(sourceContextId);
    	ConfigurationContext targetConfigurationContext = this.dao.getConfigurationContextById(targetContextId);
    	
        MappingConfiguration mappingConfiguration = new MappingConfiguration();
        mappingConfiguration.setConfigurationServiceClient(configurationServiceClient);
        mappingConfiguration.setConfigurationType(configurationType);
        mappingConfiguration.setSourceContext(sourceConfigurationContext);
        mappingConfiguration.setTargetContext(targetConfigurationContext);
        mappingConfiguration.setNumberOfParams(numberOfParams);
        mappingConfiguration.setDescription(description);

        Long mappingConfigurationId = this.dao.storeMappingConfiguration(mappingConfiguration);

        for(String keyLocationQueryString: keyLocationQueries)
        {
            ParameterName parameterName = new ParameterName();
            parameterName.setMappingConfigurationId(mappingConfigurationId);
            parameterName.setName(keyLocationQueryString);

            this.dao.storeParameterName(parameterName);
        }

        return mappingConfigurationId;
    }

    @Override
    public Long addMappingConfiguration(MappingConfiguration mappingConfiguration, List<ParameterName> parameterNames)
    {
        Long mappingConfigurationId = this.dao.storeMappingConfiguration(mappingConfiguration);

        if(parameterNames != null)
        {
            for (ParameterName parameterName : parameterNames)
            {
                parameterName.setMappingConfigurationId(mappingConfigurationId);

                this.dao.storeParameterName(parameterName);
            }
        }

        return mappingConfigurationId;
    }

    public List<MappingConfiguration> getMappingConfigurations(
			String clientName, String mappingConfigurationType,
			String sourceContextName, String targetContextName) {
		return this.dao.getMappingConfigurations(clientName, mappingConfigurationType, 
				sourceContextName, targetContextName);
	}

    public List<MappingConfigurationLite> getMappingConfigurationLites(
        String clientName, String mappingConfigurationType,
        String sourceContextName, String targetContextName) {
        return this.dao.getMappingConfigurationLites(clientName, mappingConfigurationType, 
                sourceContextName, targetContextName);
    }

	/* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getMappingConfigurationById(java.lang.Long)
     */
    @Override
    public MappingConfiguration getMappingConfigurationById(Long id)
    {
        return this.dao.getMappingConfigurationById(id);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getMappingConfiguration(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public MappingConfiguration getMappingConfiguration(String clientName, String mappingConfigurationType,
            String sourceContextName, String targetContextName)
    {
        return this.dao.getMappingConfiguration(clientName, mappingConfigurationType, sourceContextName, targetContextName);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getMappingConfigurationsByConfigurationServiceClientId(java.lang.Long)
     */
    @Override
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationServiceClientId(
            Long configurationServiceClientId)
    {
        return this.dao.getMappingConfigurationsByConfigurationServiceClientId(configurationServiceClientId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getMappingConfigurationsByConfigurationTypeId(java.lang.Long)
     */
    @Override
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationTypeId(Long configurationTypeId)
    {
        return this.dao.getMappingConfigurationsByConfigurationTypeId(configurationTypeId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getMappingConfigurationsBySourceContextId(java.lang.Long)
     */
    @Override
    public List<MappingConfiguration> getMappingConfigurationsBySourceContextId(Long sourceContextId)
    {
        return this.dao.getMappingConfigurationsBySourceContextId(sourceContextId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getMappingConfigurationsByTargetContextId(java.lang.Long)
     */
    @Override
    public List<MappingConfiguration> getMappingConfigurationsByTargetContextId(Long targetContextId)
    {
        return this.dao.getMappingConfigurationsByTargetContextId(targetContextId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getConfigurationContextById(java.lang.Long)
     */
    @Override
    public ConfigurationContext getConfigurationContextById(Long id)
    {
        return this.dao.getConfigurationContextById(id);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getConfigurationServiceClientById(java.lang.Long)
     */
    @Override
    public ConfigurationServiceClient getConfigurationServiceClientById(Long id)
    {
        return this.dao.getConfigurationServiceClientById(id);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getConfigurationTypeById(java.lang.Long)
     */
    @Override
    public ConfigurationType getConfigurationTypeById(Long id)
    {
        return this.dao.getConfigurationTypeById(id);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getParameterNameByMappingConfigurationId(java.lang.Long)
     */
    @Override
    public List<ParameterName> getParameterNamesByMappingConfigurationId(Long mappingConfigurationId)
    {
        return this.dao.getParameterNameByMappingConfigurationId(mappingConfigurationId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getSourceConfigurationValueByMappingConfigurationId(java.lang.Long)
     */
    @Override
    public List<SourceConfigurationValue> getSourceConfigurationValueByMappingConfigurationId(
            Long mappingConfigurationId)
    {
        return this.dao.getSourceConfigurationValueByMappingConfigurationId(mappingConfigurationId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getTargetConfigurationValueById(java.lang.Long)
     */
    @Override
    public TargetConfigurationValue getTargetConfigurationValueById(Long id)
    {
        return this.dao.getTargetConfigurationValueById(id);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getSourceConfigurationValuesByTargetConfigurationValueId(java.lang.Long)
     */
    @Override
    public List<SourceConfigurationValue> getSourceConfigurationValuesByTargetConfigurationValueId(
            Long targetConfigurationValueId)
    {
        return this.dao.getSourceConfigurationValuesByTargetConfigurationValueId(targetConfigurationValueId);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#deleteSourceConfigurationValue(org.ikasan.mapping.window.SourceConfigurationValue)
     */
    @Override
    public void deleteSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        this.dao.deleteSourceConfigurationValue(sourceConfigurationValue);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#deleteTargetConfigurationValue(org.ikasan.mapping.window.TargetConfigurationValue)
     */
    @Override
    public void deleteTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
    {
        this.dao.deleteTargetConfigurationValue(targetConfigurationValue);
    }

    /*
     * (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getNextSequenceNumber()
     */
    @Override
    public Long getNextSequenceNumber()
    {
        SourceConfigurationGroupSequence sequence = null;
        Long sequenceNumber = null;

        sequence = dao.getSourceConfigurationGroupSequence();

        sequenceNumber = sequence.getSequenceNumber();

        sequence.setSequenceNumber(new Long(sequence.getSequenceNumber() + 1));
        dao.saveSourceConfigurationGroupSequence(sequence);

        return sequenceNumber;
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(org.ikasan.mapping.window.TargetConfigurationValue)
     */
    @Override
    public Long getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(
            TargetConfigurationValue targetConfigurationValue)
    {
        return this.dao.getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(targetConfigurationValue);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#saveKeyLocationQuery(org.ikasan.mapping.window.KeyLocationQuery)
     */
    @Override
    public Long saveParameterName(ParameterName parameterName)
    {
        return this.dao.storeParameterName(parameterName);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#mappingConfigurationExists(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean mappingConfigurationExists(String clientName, String mappingConfigurationType,
            String sourceContextName, String targetContextName)
    {
        Long numberOfResults = this.dao.getNumberOfMappingConfigurations(clientName, mappingConfigurationType, sourceContextName, targetContextName);

        logger.debug("Number of results returned = " + numberOfResults);

        if(numberOfResults > 0)
        {
            return true;
        }
        else
        {
            return false;
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#deleteMappingConfiguration(org.ikasan.mapping.window.MappingConfiguration)
     */
    @Override
    public void deleteMappingConfiguration(MappingConfiguration mappingConfiguration)
    {
        this.dao.deleteMappingConfiguration(mappingConfiguration);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getAllConfigurationTypeByName(java.lang.String)
     */
    @Override
    public ConfigurationType getAllConfigurationTypeByName(String name)
    {
        return this.dao.getConfigurationTypeByName(name);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getAllConfigurationContextByName(java.lang.String)
     */
    @Override
    public ConfigurationContext getAllConfigurationContextByName(String name)
    {
        return this.dao.getConfigurationContextByName(name);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getAllConfigurationClientByName(java.lang.String)
     */
    @Override
    public ConfigurationServiceClient getAllConfigurationClientByName(String name)
    {
        return this.dao.getConfigurationServiceClientByName(name);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getConfigurationTypesByClientName(java.lang.String)
     */
    @Override
    public List<ConfigurationType> getConfigurationTypesByClientName(String clientname)
    {
        return this.dao.getConfigurationTypesByClientName(clientname);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getSourceConfigurationContextByClientNameAndType(java.lang.String, java.lang.String)
     */
    @Override
    public List<ConfigurationContext> getSourceConfigurationContextsByClientNameAndType(String clientName, String type)
    {
        return this.dao.getSourceConfigurationContextByClientNameAndType(clientName, type);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getTargetConfigurationContextByClientNameTypeAndSourceContext(java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public List<ConfigurationContext> getTargetConfigurationContextByClientNameTypeAndSourceContext(String clientName,
            String type, String sourceContext)
    {
        return this.dao.getTargetConfigurationContextByClientNameTypeAndSourceContext(clientName, type, sourceContext); 
    }

	/* (non-Javadoc)
	 * @see org.ikasan.mapping.service.MappingConfigurationService#setConfiguration(org.ikasan.mapping.service.configuration.MappingConfigurationServiceConfiguration)
	 */
	@Override
	public void setConfiguration(
			MappingConfigurationServiceConfiguration configuration)
	{
		this.configuration = configuration;
	}

    @Override
    public List<ManyToManyTargetConfigurationValue> getManyToManyTargetConfigurationValues(Long groupId)
    {
        return this.dao.getManyToManyTargetConfigurationValues(groupId);
    }

    @Override
    public Long storeManyToManyTargetConfigurationValue(ManyToManyTargetConfigurationValue targetConfigurationValue)
    {
        return this.dao.storeManyToManyTargetConfigurationValue(targetConfigurationValue);
    }

    @Override
    public void deleteManyToManyTargetConfigurationValue(ManyToManyTargetConfigurationValue targetConfigurationValue)
    {
        this.dao.deleteManyToManyTargetConfigurationValue(targetConfigurationValue);
    }


}
