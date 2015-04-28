/*
 * $Id: MappingConfigurationService.java 44074 2015-03-17 10:43:23Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/service/MappingConfigurationService.java $
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

import java.util.List;

import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.MappingConfigurationLite;
import org.ikasan.mapping.model.PlatformConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;


/**
 * 
 * @author CMI2 Development Team
 *
 */
public interface MappingConfigurationService
{
    /**
     * This method is responsible for resolving a target system configuration value from the Mapping Configuration Cache
     * based on the following parameters:
     *
     * @param clientName the name of the Configuration Service Client for whom the the mapping is being performed.
     * @param configurationTypeName the name of the configuration type that we are resolving the configuration value for.
     * @param sourceContext the source context name that we are resolving the configuration value for.
     * @param targetContext the target context name that we are resolving the configuration value for.
     * @param sourceSystemValues the values on the source side used to resolve the target configuration value.
     * 
     * @return the target configuration value mapped to all the above arguments.
     */
    public String getTargetConfigurationValue(final String clientName, final String configurationTypeName, final String sourceContext, final String targetContext,
            final List<String> sourceSystemValues);

    /**
     * This method is responsible for resolving a target system configuration value from the Mapping Configuration Cache
     * based on the following parameters:
     * 
     * @param clientName the name of the Configuration Service Client for whom the the mapping is being performed.
     * @param configurationTypeName the name of the configuration type that we are resolving the configuration value for.
     * @param sourceContext the source context name that we are resolving the configuration value for.
     * @param targetContext the target context name that we are resolving the configuration value for.
     * @param sourceSystemValue the value on the source side used to resolve the target configuration value.
     * 
     * @return the target configuration value mapped to all the above arguments.
     */
    public String getTargetConfigurationValue(final String clientName, final String configurationTypeName, final String sourceContext, final String targetContext,
            final String sourceSystemValue);

    /**
     * This method is responsible for resolving a target system configuration value from the Mapping Configuration Cache
     * based on the following parameters:
     * 
     * @param clientName the name of the Configuration Service Client for whom the the mapping is being performed. 
     * @param configurationTypeName the name of the configuration type that we are resolving the configuration value for.
     * @param sourceContext the source context name that we are resolving the configuration value for.
     * @param targetContext the target context name that we are resolving the configuration value for.
     * @param payload the payload from where the source configuration values will be resolved from using the associated
     * key location queries.
     * @return
     * @throws MappingConfigurationServiceException 
     */
    public String getTargetConfigurationValue(final String clientName, final String configurationTypeName, final String sourceContext, final String targetContext,
            byte[] payload) throws MappingConfigurationServiceException;

    /**
     * This method is responsible for returning all {@link ConfigurationType} hibernate
     * mapping objects that are stored in the Mapping Configuration database.
     * 
     * @return a List of all {@link ConfigurationType} in the Mapping Configuration database.
     */
    public List<ConfigurationType> getAllConfigurationTypes();

    /**
     * This method is responsible for returning a {@link ConfigurationType} hibernate
     * mapping object that are stored in the Mapping Configuration database based on
     * its name.
     *  
     * @param name
     * @return
     */
    public ConfigurationType getAllConfigurationTypeByName(String name);

    /**
     * This method is responsible for saving a {@link ConfigurationType} to the Mapping Configuration database.
     * 
     * @param configurationType the {@link ConfigurationType} to add to the database.
     */
    public Long saveConfigurationType(ConfigurationType configurationType);

    /**
     * This method is responsible for returning all {@link ConfigurationContext} hibernate
     * mapping objects that are stored in the Mapping Configuration database.
     * 
     * @return a List of all {@link ConfigurationContext} in the Mapping Configuration database.
     */
    public List<ConfigurationContext> getAllConfigurationContexts();

    /**
     * This method is responsible for returning a {@link ConfigurationContext} hibernate
     * mapping object that are stored in the Mapping Configuration database based on
     * its name.
     *  
     * @param name
     * @return
     */
    public ConfigurationContext getAllConfigurationContextByName(String name);

    /**
     * This method is responsible for saving a {@link ConfigurationContext} to the Mapping Configuration database.
     * 
     * @param configurationType the {@link ConfigurationContext} to add to the database.
     */
    public Long saveConfigurationConext(ConfigurationContext configurationContext);

    /**
     * This method is responsible for returning all {@link ConfigurationServiceClient} hibernate
     * mapping objects that are stored in the Mapping Configuration database.
     * 
     * @return a List of all {@link ConfigurationServiceClient} in the Mapping Configuration database.
     */
    public List<ConfigurationServiceClient> getAllConfigurationServiceClients();

    /**
     * This method is responsible for returning a {@link ConfigurationServiceClient} hibernate
     * mapping object that are stored in the Mapping Configuration database based on
     * its name.
     *  
     * @param name
     * @return
     */
    public ConfigurationServiceClient getAllConfigurationClientByName(String name);

    /**
     * This method is responsible for saving a {@link ConfigurationServiceClient} to the Mapping Configuration database.
     * 
     * @param configurationType the {@link ConfigurationServiceClient} to add to the database.
     */
    public Long saveConfigurationServiceClient(ConfigurationServiceClient configurationServiceClient);

    /**
     * This method is responsible for saving a {@link MappingConfiguration} to the Mapping Configuration database.
     *
     * @param mappingConfiguration the {@link MappingConfiguration} to add to the database.
     * @return
     */
    public Long saveMappingConfiguration(MappingConfiguration mappingConfiguration) throws MappingConfigurationServiceException;

    /**
     * This method is responsible for saving a {@link SourceConfigurationValue} to the Mapping Configuration database.
     *
     * @param sourceConfigurationValue the {@link SourceConfigurationValue} to add to the database.
     * @return
     */
    public Long saveSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue);

    /**
     * This method is responsible for saving a {@link TargetConfigurationValue} to the Mapping Configuration database.
     *
     * @param targetConfigurationValue the {@link TargetConfigurationValue} to add to the database.
     * @return
     */
    public Long saveTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue);

    /**
     * This method is responsible for saving a {@link KeyLocationQuery} to the Mapping Configuration database.
     *
     * @param query the {@link KeyLocationQuery} to add to the database.
     * @return
     */
    public Long saveKeyLocationQuery(KeyLocationQuery query);

    /**
     * This method checks for the existence on a mapping configuration.
     * 
     * @param clientName
     * @param mappingConfigurationType
     * @param sourceContextName
     * @param targetContextName
     * @return
     */
    public boolean mappingConfigurationExists(String clientName, String mappingConfigurationType, String sourceContextName,
            String targetContextName);

    /**
     * This method is responsible for adding a {@link MappingConfiguration} objects to the database with the appropriate
     * associations. It also adds the required number of {@link KeyLocationQuery} objects to the database and associates 
     * them with the newly created {@link MappingConfiguration} record.
     * 
     * @param sourceContextId the primary key id of the source {@link ConfigurationContext} record we want to associate
     * the new {@link MappingConfiguration} record with.
     * @param targetContextId the primary key id of the target {@link ConfigurationContext} record we want to associate
     * the new {@link MappingConfiguration} record with.
     * @param numberOfParams the number of input parameters required when resolving the mapping.
     * @param configurationTypeId the primary key id of the {@link ConfigurationType} record we want to associate
     * the new {@link MappingConfiguration} record with.
     * @param configurationServiceClientId the primary key id of the {@link ConfigurationServiceClient} record we want to 
     * associate the new {@link MappingConfiguration} record with.
     * @param keyLocationQueries a {@link List} of {@link String} objects representing the key location queries to be associated
     * with the MappingConfiguration.
     * @param description the description of the {@link MappingConfiguration}
     */
    public Long addMappingConfiguration(Long sourceContextId, Long targetContextId, Long numberOfParams,
            Long configurationTypeId, Long configurationServiceClientId, List<String> keyLocationQueries,
            String description);

    /**
     * This method retrieves a MappingConfiguration based on its id.
     * 
     * @param id
     * @return
     */
    public MappingConfiguration getMappingConfigurationById(Long id);

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
     * This method retrieves a mapping configuration context by client name, configuration type as well as the
     * source and target contests.
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
    public List<MappingConfigurationLite> getMappingConfigurationLites(String clientName, String mappingConfigurationType, String sourceContextName,
        String targetContextName);

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
     * @param id
     * @return
     */
    public TargetConfigurationValue getTargetConfigurationValueById(Long id);

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
     * @return
     */
    public Long getNextSequenceNumber();

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
    public List<ConfigurationContext> getSourceConfigurationContextsByClientNameAndType(final String clientName, final String type);

    /**
     * 
     * @param clientName
     * @param type
     * @param sourceContext
     * @return
     */
    public List<ConfigurationContext> getTargetConfigurationContextByClientNameTypeAndSourceContext(final String clientName, final String type, final String sourceContext);


    /**
     * 
     * @param name
     * @return
     */
    public PlatformConfiguration getPlatformConfigurationByName(String name);
}
