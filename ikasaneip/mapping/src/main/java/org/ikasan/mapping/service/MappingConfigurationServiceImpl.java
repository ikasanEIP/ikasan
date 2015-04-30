/*
 * $Id: MappingConfigurationServiceImpl.java 44074 2015-03-17 10:43:23Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/service/MappingConfigurationServiceImpl.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2012 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
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
 * @author CMI2 Development Team
 *
 */
public class MappingConfigurationServiceImpl implements MappingConfigurationService
{
	private Logger logger = Logger.getLogger(MappingConfigurationServiceImpl.class);

    /** Access to market data */
    protected final MappingConfigurationDao dao;

    /**
     * Constructor
     * 
     * @param dao the {@link MappingConfigurationDao} to set on construction of this object.
     * @param keyLocationQueryProcessorFactory the {@link KeyLocationQueryProcessorFactory}
     * to set on construction of this object.
     */
    public MappingConfigurationServiceImpl(final MappingConfigurationDao dao)
    {
        this.dao = dao;
        if (this.dao == null)
        {
            throw new IllegalArgumentException("The MappingConfigurationDao cannot be null.");
        }
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#saveMappingConfiguration(com.mizuho.cmi2.mappingConfiguration.model.MappingConfiguration)
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
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#saveSourceConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.SourceConfigurationValue)
     */
    @Override
    public Long saveSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        return this.dao.storeSourceConfigurationValue(sourceConfigurationValue);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#saveTargetConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.TargetConfigurationValue)
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
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getTargetConfigurationValue(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getAllConfigurationTypes()
     */
    @Override
    public List<ConfigurationType> getAllConfigurationTypes()
    {
        return this.dao.getAllConfigurationTypes();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getAllConfigurationContexts()
     */
    @Override
    public List<ConfigurationContext> getAllConfigurationContexts()
    {
        return this.dao.getAllConfigurationContexts();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getAllConfigurationServiceClients()
     */
    @Override
    public List<ConfigurationServiceClient> getAllConfigurationServiceClients()
    {
        return this.dao.getAllConfigurationServiceClients();
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#addConfigurationType(com.mizuho.cmi2.mappingConfiguration.model.ConfigurationType)
     */
    @Override
    public Long saveConfigurationType(ConfigurationType configurationType)
    {
        return this.dao.storeConfigurationType(configurationType);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#addConfigurationConext(com.mizuho.cmi2.mappingConfiguration.model.ConfigurationContext)
     */
    @Override
    public Long saveConfigurationConext(ConfigurationContext configurationContext)
    {
        return this.dao.storeConfigurationContext(configurationContext);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#addConfigurationServiceClient(com.mizuho.cmi2.mappingConfiguration.model.ConfigurationServiceClient)
     */
    @Override
    public Long saveConfigurationServiceClient(ConfigurationServiceClient configurationServiceClient)
    {
        return this.dao.storeConfigurationServiceClient(configurationServiceClient);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#addMappingConfiguration(java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Long, java.lang.Long, java.util.List, java.util.List, java.lang.String)
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
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getMappingConfigurationById(java.lang.Long)
     */
    @Override
    public MappingConfiguration getMappingConfigurationById(Long id)
    {
        return this.dao.getMappingConfigurationById(id);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getMappingConfiguration(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
     */
    @Override
    public MappingConfiguration getMappingConfiguration(String clientName, String mappingConfigurationType,
            String sourceContextName, String targetContextName)
    {
        return this.dao.getMappingConfiguration(clientName, mappingConfigurationType, sourceContextName, targetContextName);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getMappingConfigurationsByConfigurationServiceClientId(java.lang.Long)
     */
    @Override
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationServiceClientId(
            Long configurationServiceClientId)
    {
        return this.dao.getMappingConfigurationsByConfigurationServiceClientId(configurationServiceClientId);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getMappingConfigurationsByConfigurationTypeId(java.lang.Long)
     */
    @Override
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationTypeId(Long configurationTypeId)
    {
        return this.dao.getMappingConfigurationsByConfigurationTypeId(configurationTypeId);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getMappingConfigurationsBySourceContextId(java.lang.Long)
     */
    @Override
    public List<MappingConfiguration> getMappingConfigurationsBySourceContextId(Long sourceContextId)
    {
        return this.dao.getMappingConfigurationsBySourceContextId(sourceContextId);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getMappingConfigurationsByTargetContextId(java.lang.Long)
     */
    @Override
    public List<MappingConfiguration> getMappingConfigurationsByTargetContextId(Long targetContextId)
    {
        return this.dao.getMappingConfigurationsByTargetContextId(targetContextId);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getConfigurationContextById(java.lang.Long)
     */
    @Override
    public ConfigurationContext getConfigurationContextById(Long id)
    {
        return this.dao.getConfigurationContextById(id);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getConfigurationServiceClientById(java.lang.Long)
     */
    @Override
    public ConfigurationServiceClient getConfigurationServiceClientById(Long id)
    {
        return this.dao.getConfigurationServiceClientById(id);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getConfigurationTypeById(java.lang.Long)
     */
    @Override
    public ConfigurationType getConfigurationTypeById(Long id)
    {
        return this.dao.getConfigurationTypeById(id);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getKeyLocationQueriesByMappingConfigurationId(java.lang.Long)
     */
    @Override
    public List<KeyLocationQuery> getKeyLocationQueriesByMappingConfigurationId(Long mappingConfigurationId)
    {
        return this.dao.getKeyLocationQueriesByMappingConfigurationId(mappingConfigurationId);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getSourceConfigurationValueByMappingConfigurationId(java.lang.Long)
     */
    @Override
    public List<SourceConfigurationValue> getSourceConfigurationValueByMappingConfigurationId(
            Long mappingConfigurationId)
    {
        return this.dao.getSourceConfigurationValueByMappingConfigurationId(mappingConfigurationId);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getTargetConfigurationValueById(java.lang.Long)
     */
    @Override
    public TargetConfigurationValue getTargetConfigurationValueById(Long id)
    {
        return this.dao.getTargetConfigurationValueById(id);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getSourceConfigurationValuesByTargetConfigurationValueId(java.lang.Long)
     */
    @Override
    public List<SourceConfigurationValue> getSourceConfigurationValuesByTargetConfigurationValueId(
            Long targetConfigurationValueId)
    {
        return this.dao.getSourceConfigurationValuesByTargetConfigurationValueId(targetConfigurationValueId);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#deleteSourceConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.SourceConfigurationValue)
     */
    @Override
    public void deleteSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue)
    {
        this.dao.deleteSourceConfigurationValue(sourceConfigurationValue);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#deleteTargetConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.TargetConfigurationValue)
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
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(com.mizuho.cmi2.mappingConfiguration.model.TargetConfigurationValue)
     */
    @Override
    public Long getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(
            TargetConfigurationValue targetConfigurationValue)
    {
        return this.dao.getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(targetConfigurationValue);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#saveKeyLocationQuery(com.mizuho.cmi2.mappingConfiguration.model.KeyLocationQuery)
     */
    @Override
    public Long saveKeyLocationQuery(KeyLocationQuery query)
    {
        return this.dao.storeKeyLocationQuery(query);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#mappingConfigurationExists(java.lang.String, java.lang.String, java.lang.String, java.lang.String)
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
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#deleteMappingConfiguration(com.mizuho.cmi2.mappingConfiguration.model.MappingConfiguration)
     */
    @Override
    public void deleteMappingConfiguration(MappingConfiguration mappingConfiguration)
    {
        this.dao.deleteMappingConfiguration(mappingConfiguration);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getAllConfigurationTypeByName(java.lang.String)
     */
    @Override
    public ConfigurationType getAllConfigurationTypeByName(String name)
    {
        return this.dao.getConfigurationTypeByName(name);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getAllConfigurationContextByName(java.lang.String)
     */
    @Override
    public ConfigurationContext getAllConfigurationContextByName(String name)
    {
        return this.dao.getConfigurationContextByName(name);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getAllConfigurationClientByName(java.lang.String)
     */
    @Override
    public ConfigurationServiceClient getAllConfigurationClientByName(String name)
    {
        return this.dao.getConfigurationServiceClientByName(name);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getConfigurationTypesByClientName(java.lang.String)
     */
    @Override
    public List<ConfigurationType> getConfigurationTypesByClientName(String clientname)
    {
        return this.dao.getConfigurationTypesByClientName(clientname);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getSourceConfigurationContextByClientNameAndType(java.lang.String, java.lang.String)
     */
    @Override
    public List<ConfigurationContext> getSourceConfigurationContextsByClientNameAndType(String clientName, String type)
    {
        return this.dao.getSourceConfigurationContextByClientNameAndType(clientName, type);
    }

    /* (non-Javadoc)
     * @see com.mizuho.cmi2.mappingConfiguration.service.MappingConfigurationService#getTargetConfigurationContextByClientNameTypeAndSourceContext(java.lang.String, java.lang.String, java.lang.String)
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
