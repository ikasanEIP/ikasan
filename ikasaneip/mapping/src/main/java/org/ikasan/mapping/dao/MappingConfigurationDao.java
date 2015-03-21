/*
 * $Id: MappingConfigurationDao.java 44074 2015-03-17 10:43:23Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/dao/MappingConfigurationDao.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.dao;

import java.util.List;

import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.MappingConfigurationLite;
import org.ikasan.mapping.model.SourceConfigurationGroupSequence;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.springframework.dao.DataAccessException;


/**
 * @author CMI2 Development Team
 *
 */
public interface MappingConfigurationDao
{
    /**
     * 
     * @param clientName
     * @param configurationType
     * @param sourceSystem
     * @param targetSystem
     * @param sourceSystemValues
     * @return
     */
    public String getTargetConfigurationValue(final String clientName, String configurationType,
            String sourceContext, String targetContext, List<String> sourceSystemValues);

    /**
     * 
     * @param configurationServiceClientName
     * @return
     */
    public ConfigurationServiceClient getConfigurationServiceClientByName(String configurationServiceClientName);

    /**
     * Get a list of key location queries from the database.
     * 
     * @param configurationType
     * @param sourceSystem
     * @param targetSystem
     * @param configurationServiceClientName
     * @return
     */
    public List<String> getKeyLocationQuery(final String configurationType, final String sourceSystem, final String targetSystem,
            final String configurationServiceClientName);

    /**
     * 
     * @return
     */
    public List<ConfigurationType> getAllConfigurationTypes();

    /**
     * 
     * @return
     */
    public ConfigurationType getConfigurationTypeByName(String name);

    /**
     * 
     * @return
     */
    public List<ConfigurationContext> getAllConfigurationContexts();

    /**
     * 
     * @return
     */
    public ConfigurationContext getConfigurationContextByName(String name);

    /**
     * 
     * @return
     */
    public List<ConfigurationServiceClient> getAllConfigurationServiceClients();

    /**
     * Store the configuration context.
     * 
     * @param configurationContext
     */
    public Long storeConfigurationContext(ConfigurationContext configurationContext);


    /**
     * Store the configuration type.
     * 
     * @param configurationType
     */
    public Long storeConfigurationType(ConfigurationType configurationType);

    /**
     * Store the configuration context.
     * 
     * @param configurationContext
     */
    public Long storeMappingConfiguration(MappingConfiguration mappingConfiguration) throws DataAccessException;

    /**
     * Store the configuration context.
     * 
     * @param configurationContext
     */
    public Long storeSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue);

    /**
     * Store the configuration context.
     * 
     * @param configurationContext
     */
    public Long storeTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue);

    /**
     * Store the configuration service client.
     * 
     * @param configurationContext
     */
    public Long storeConfigurationServiceClient(ConfigurationServiceClient configurationServiceClient);

    /**
     * Store the key location query.
     * 
     * @param configurationContext
     */
    public Long storeKeyLocationQuery(KeyLocationQuery keyLocationQuery);

    /**
     * 
     * @param id
     * @return
     */
    public MappingConfiguration getMappingConfigurationById(Long id);

    /**
     * This method retrieves a mapping configuration context by client name, configuration type as well as the
     * source and target contests.
     * 
     * @param clientName
     * @param mappingConfigurationType
     * @param sourceContextName
     * @param targetContextName
     * @return
     */
    public MappingConfiguration getMappingConfiguration(String clientName, String mappingConfigurationType, String sourceContextName,
            String targetContextName);

    /**
     * This method checks for the existence on a mapping configuration.
     * 
     * @param clientName
     * @param mappingConfigurationType
     * @param sourceContextName
     * @param targetContextName
     * @return
     */
    public Long getNumberOfMappingConfigurations(String clientName, String mappingConfigurationType, String sourceContextName,
            String targetContextName);

    /**
     * 
     * @param clientName
     * @param mappingConfigurationType
     * @param sourceContextName
     * @param targetContextName
     * @return
     */
    public List<MappingConfiguration> getMappingConfigurations(String clientName, String mappingConfigurationType, String sourceContextName,
            String targetContextName);

    /**
     * 
     * @param clientName
     * @param mappingConfigurationType
     * @param sourceContextName
     * @param targetContextName
     * @return
     */
    public List<MappingConfigurationLite> getMappingConfigurationLites(final String clientName, final String mappingConfigurationType,
        final String sourceContextName, final String targetContextName);

    /**
     * 
     * @param configurationServiceClientId
     * @return
     */
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationServiceClientId(Long configurationServiceClientId);

    /**
     * 
     * @param configurationTypeId
     * @return
     */
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationTypeId(Long configurationTypeId);

    /**
     * 
     * @param sourceContextId
     * @return
     */
    public List<MappingConfiguration> getMappingConfigurationsBySourceContextId(Long sourceContextId);

    /**
     * 
     * @param targetContextId
     * @return
     */
    public List<MappingConfiguration> getMappingConfigurationsByTargetContextId(Long targetContextId);

    /**
     * 
     * @param targetContextId
     * @return
     */
    public List<MappingConfiguration> getAllMappingConfigurations();

    /**
     * 
     * @param id
     * @return
     */
    public ConfigurationContext getConfigurationContextById(Long id);

    /**
     * 
     * @param id
     * @return
     */
    public ConfigurationServiceClient getConfigurationServiceClientById(Long id);

    /**
     * 
     * @param id
     * @return
     */
    public ConfigurationType getConfigurationTypeById(Long id);

    /**
     * 
     * @param mappingConfigurationId
     * @return
     */
    public List<KeyLocationQuery> getKeyLocationQueriesByMappingConfigurationId(Long mappingConfigurationId);

    /**
     * 
     * @param mappingConfigurationId
     * @return
     */
    public List<SourceConfigurationValue> getSourceConfigurationValueByMappingConfigurationId(Long mappingConfigurationId);

    /**
     * 
     * @param mappingConfigurationId
     * @return
     */
    public List<SourceConfigurationValue> getSourceConfigurationValuesByTargetConfigurationValueId(Long targetConfigurationValueId);

    /**
     * 
     * @param sourceConfigurationValue
     */
    public void deleteSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue);

    /**
     * 
     * @param sourceConfigurationValue
     */
    public void deleteTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue);

    /**
     * 
     * @param mappingConfiguration
     */
    public void deleteMappingConfiguration(MappingConfiguration mappingConfiguration);

    /**
     * 
     * @param id
     * @return
     */
    public TargetConfigurationValue getTargetConfigurationValueById(Long id);

    /**
     * @return
     */
    public SourceConfigurationGroupSequence getSourceConfigurationGroupSequence();

    /**
     * @param sequence
     */
    public void saveSourceConfigurationGroupSequence(SourceConfigurationGroupSequence sequence);

    /**
     * 
     * @param targetConfigurationValue
     * @return
     */
    public Long getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue);

    /**
     * 
     * @param clientname
     * @return
     */
    public List<ConfigurationType> getConfigurationTypesByClientName(final String clientname);

    /**
     * 
     * @param clientName
     * @param type
     * @return
     */
    public List<ConfigurationContext> getSourceConfigurationContextByClientNameAndType(final String clientName, final String type);

    /**
     * 
     * @param clientName
     * @param type
     * @param sourceContext
     * @return
     */
    public List<ConfigurationContext> getTargetConfigurationContextByClientNameTypeAndSourceContext(final String clientName, final String type, final String sourceContext);
}
