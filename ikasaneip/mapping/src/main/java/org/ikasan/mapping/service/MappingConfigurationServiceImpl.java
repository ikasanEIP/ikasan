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

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.ikasan.mapping.dao.MappingConfigurationDao;
import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessor;
import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessorException;
import org.ikasan.mapping.keyQueryProcessor.KeyLocationQueryProcessorFactory;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.MappingConfigurationLite;
import org.ikasan.mapping.model.PlatformConfiguration;
import org.ikasan.mapping.model.SourceConfigurationGroupSequence;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.springframework.dao.DataAccessException;



/**
 * @author Ikasan Development Team
 *
 */
public class MappingConfigurationServiceImpl implements MappingConfigurationService
{
    private Logger logger = Logger.getLogger(MappingConfigurationServiceImpl.class);

    /** Access to market data */
    protected final MappingConfigurationDao dao;
    protected final KeyLocationQueryProcessorFactory keyLocationQueryProcessorFactory;

    /**
     * Constructor
     * 
     * @param dao the {@link MappingConfigurationDao} to set on construction of this object.
     * @param keyLocationQueryProcessorFactory the {@link KeyLocationQueryProcessorFactory}
     * to set on construction of this object.
     */
    public MappingConfigurationServiceImpl(final MappingConfigurationDao dao,
            KeyLocationQueryProcessorFactory keyLocationQueryProcessorFactory)
    {
        this.dao = dao;
        if (this.dao == null)
        {
            throw new IllegalArgumentException("The MappingConfigurationDao cannot be null.");
        }
        this.keyLocationQueryProcessorFactory = keyLocationQueryProcessorFactory;
        if (this.keyLocationQueryProcessorFactory == null)
        {
            throw new IllegalArgumentException("The keyLocationQueryProcessorFactory cannot be null.");
        }
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#saveMappingConfiguration(org.ikasan.mapping.model.MappingConfiguration)
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
            throw new MappingConfigurationServiceException(e);
        }

        return id;
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#saveSourceConfigurationValue(org.ikasan.mapping.model.SourceConfigurationValue)
     */
    @Override
    public Long saveSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        return this.dao.storeSourceConfigurationValue(sourceConfigurationValue);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#saveTargetConfigurationValue(org.ikasan.mapping.model.TargetConfigurationValue)
     */
    @Override
    public Long saveTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
    {
        return this.dao.storeTargetConfigurationValue(targetConfigurationValue);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.stateModel.service.StateModelHistoryService#recordStateModelHistory(com.mizuho.cmi2.stateModel.model.StateModelHistory)
     */
    @Override
    public String getTargetConfigurationValue(final String clientName, final String configurationType, final String sourceSystem, 
            final String targetSystem, final List<String> sourceSystemValues)
    {
        return this.dao.getTargetConfigurationValue(clientName, configurationType, sourceSystem, targetSystem, sourceSystemValues);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public String getTargetConfigurationValue(final String clientName, String configurationType, String sourceSystem, String targetSystem,
            String sourceSystemValue)
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add(sourceSystemValue);

        return this.dao.getTargetConfigurationValue(clientName, configurationType, sourceSystem, targetSystem, sourceSystemValues);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String, byte[])
     */
    @Override
    public String getTargetConfigurationValue(final String clientName, final String configurationType, final String sourceContext,
            final String targetContext, final byte[] payload) throws MappingConfigurationServiceException
    {
        String returnValue = null;

        try
        {
            // We need to get all the key location queries from the database
            List<String> keyLocationQueries = this.dao.getKeyLocationQuery(configurationType, sourceContext, targetContext, clientName);

            // We then delegate to the KeyLocationQueryProcessorFactory to get the appropriate KeyLocationQueryProcessor for the
            // clientName passed in as an argument to this method.
            KeyLocationQueryProcessor keyLocationQueryProcessor = this.keyLocationQueryProcessorFactory.getKeyLocationQueryProcessor(clientName);

            List<String> sourceSystemValues = new ArrayList<String>();

            // We then want to iterate over the all the key location query strings passed into this method and 
            // delegate to the KeyLocationQueryProcessor to get the source system values used to get the target
            // system value related to the mapping configuration.
            for(String keyLocationQuery: keyLocationQueries)
            {
                String queryResult = keyLocationQueryProcessor.getKeyValueFromPayload(keyLocationQuery, payload);

                if(queryResult.length() == 0)
                {
                    throw new KeyLocationQueryProcessorException("Evaluation of key location query '" + keyLocationQuery 
                        +"' returned null or an empty string");
                }
                else
                {
                    sourceSystemValues.add(queryResult);
                }
            }

            // Now delegate to the dao to get the target configuration value from the database.
            returnValue = this.dao.getTargetConfigurationValue(clientName, configurationType, sourceContext, targetContext, sourceSystemValues);

            if(returnValue == null || returnValue.length() == 0)
            {
                StringBuffer sourceSystemValuesSB = new StringBuffer();

                sourceSystemValuesSB.append("[SourceSystemValues = ");
                for(String sourceSystemValue: sourceSystemValues)
                {
                    sourceSystemValuesSB.append(sourceSystemValue).append(" ");
                }
                sourceSystemValuesSB.append("]");

                throw new MappingConfigurationServiceException("The Mapping Configuration Service has been unable to resolve a target configuration value. " +
                        "[Client = " + clientName + "] [MappingConfigurationType = " + configurationType + "] [SourceContext = " + sourceContext + "] " +
                        "[TargetContext = " + targetContext + "] " + sourceSystemValuesSB.toString());
            }
        }
        catch (KeyLocationQueryProcessorException e)
        {
            throw new MappingConfigurationServiceException(e);
        }

        return returnValue;
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
     * @see org.ikasan.mapping.service.MappingConfigurationService#addConfigurationType(org.ikasan.mapping.model.ConfigurationType)
     */
    @Override
    public Long saveConfigurationType(ConfigurationType configurationType)
    {
        return this.dao.storeConfigurationType(configurationType);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#addConfigurationConext(org.ikasan.mapping.model.ConfigurationContext)
     */
    @Override
    public Long saveConfigurationConext(ConfigurationContext configurationContext)
    {
        return this.dao.storeConfigurationContext(configurationContext);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#addConfigurationServiceClient(org.ikasan.mapping.model.ConfigurationServiceClient)
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
    public Long addMappingConfiguration(Long sourceContextId, Long targetContextId, Long numberOfParams,
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
            KeyLocationQuery keyLocationQuery = new KeyLocationQuery();
            keyLocationQuery.setMappingConfigurationId(mappingConfigurationId);
            keyLocationQuery.setValue(keyLocationQueryString);

            this.dao.storeKeyLocationQuery(keyLocationQuery);
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
     * @see org.ikasan.mapping.service.MappingConfigurationService#getKeyLocationQueriesByMappingConfigurationId(java.lang.Long)
     */
    @Override
    public List<KeyLocationQuery> getKeyLocationQueriesByMappingConfigurationId(Long mappingConfigurationId)
    {
        return this.dao.getKeyLocationQueriesByMappingConfigurationId(mappingConfigurationId);
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
     * @see org.ikasan.mapping.service.MappingConfigurationService#deleteSourceConfigurationValue(org.ikasan.mapping.model.SourceConfigurationValue)
     */
    @Override
    public void deleteSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        this.dao.deleteSourceConfigurationValue(sourceConfigurationValue);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#deleteTargetConfigurationValue(org.ikasan.mapping.model.TargetConfigurationValue)
     */
    @Override
    public void deleteTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue)
    {
        this.dao.deleteTargetConfigurationValue(targetConfigurationValue);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.swiftAlliance.swiftGateway.service.LauSequenceService#getNextSequenceNumber()
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
     * @see org.ikasan.mapping.service.MappingConfigurationService#getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(org.ikasan.mapping.model.TargetConfigurationValue)
     */
    @Override
    public Long getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(
            TargetConfigurationValue targetConfigurationValue)
    {
        return this.dao.getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(targetConfigurationValue);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#saveKeyLocationQuery(org.ikasan.mapping.model.KeyLocationQuery)
     */
    @Override
    public Long saveKeyLocationQuery(KeyLocationQuery query)
    {
        return this.dao.storeKeyLocationQuery(query);
    }

    /* (non-Javadoc)
     * @see org.ikasan.mapping.service.MappingConfigurationService#mappingConfigurationExists(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public boolean mappingConfigurationExists(String clientName, String mappingConfigurationType,
            String sourceContextName, String targetContextName)
    {
        Long numberOfResults = this.dao.getNumberOfMappingConfigurations(clientName, mappingConfigurationType, sourceContextName, targetContextName);

        logger.info("Number of results returned = " + numberOfResults);

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
     * @see org.ikasan.mapping.service.MappingConfigurationService#deleteMappingConfiguration(org.ikasan.mapping.model.MappingConfiguration)
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
	 * @see org.ikasan.mapping.service.MappingConfigurationService#getPlatformConfigurationByName(java.lang.String)
	 */
	@Override
	public PlatformConfiguration getPlatformConfigurationByName(String name)
	{
		return this.dao.getPlatformConfigurationByName(name);
	}
}
