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

import org.ikasan.mapping.model.*;
import org.ikasan.mapping.service.configuration.MappingConfigurationServiceConfiguration;


/**
 * 
 * @author Ikasan Development Team
 *
 */
public interface MappingConfigurationService
{
	/**
     * This method is responsible for resolving a target system configuration value from the Mapping Configuration Cache
     * based on the following parameters. It ignores non relevant source values:
     *
     * @param clientName the name of the Configuration Service Client for whom the the mapping is being performed.
     * @param configurationTypeName the name of the configuration type that we are resolving the configuration value for.
     * @param sourceContext the source context name that we are resolving the configuration value for.
     * @param targetContext the target context name that we are resolving the configuration value for.
     * @param sourceSystemValues the values on the source side used to resolve the target configuration value.
     *
     * @return the target configuration value mapped to all the above arguments.
     */
    public String getTargetConfigurationValueWithIgnores(final String clientName, final String configurationTypeName, final String sourceContext, final String targetContext,
            final List<String> sourceSystemValues);

    /**
     * This method is responsible for resolving a target system configuration value from the Mapping Configuration Cache
     * based on the following parameters. It ignores non relevant source values:
     *
     * @param clientName the name of the Configuration Service Client for whom the the mapping is being performed.
     * @param configurationTypeName the name of the configuration type that we are resolving the configuration value for.
     * @param sourceContext the source context name that we are resolving the configuration value for.
     * @param targetContext the target context name that we are resolving the configuration value for.
     * @param sourceSystemValues the values on the source side used to resolve the target configuration value.
     *
     * @return the target configuration value mapped to all the above arguments.
     */
    public String getTargetConfigurationValueWithIgnoresWithOrdinality(final String clientName, final String configurationTypeName, final String sourceContext, final String targetContext,
                                                         final List<QueryParameterImpl> sourceSystemValues);

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
     *
     * @param clientName
     * @param configurationType
     * @param sourceContext
     * @param targetContext
     * @param sourceSystemValues
     * @return
     */
    public List<String> getTargetConfigurationValues(final String clientName, String configurationType,
                                                     String sourceContext, String targetContext, List<String> sourceSystemValues);

    /**
     *
     * @param clientName
     * @param configurationType
     * @param sourceContext
     * @param targetContext
     * @param sourceSystemValues
     * @return
     */
    public List<String> getTargetConfigurationValuesWithOrdinality(final String clientName, String configurationType,
                                                                   String sourceContext, String targetContext, List<QueryParameterImpl> sourceSystemValues);

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
     * This method is responsible for saving a {@link ParameterName} to the Mapping Configuration database.
     *
     * @param query the {@link ParameterName} to add to the database.
     * @return
     */
    public Long saveParameterName(ParameterName query);

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
     * associations. It also adds the required number of {@link ParameterName} objects to the database and associates
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
    public Long addMappingConfiguration(Long sourceContextId, Long targetContextId, int numberOfParams,
                                        Long configurationTypeId, Long configurationServiceClientId, List<String> keyLocationQueries,
                                        String description);

    public Long addMappingConfiguration(MappingConfiguration mappingConfiguration, List<ParameterName> parameterNames);

    /**
     * This method retrieves a {@link MappingConfiguration} based on its id.
     * 
     * @param id
     * @return
     */
    public MappingConfiguration getMappingConfigurationById(Long id);

    /**
     * This method retrieves a list of {@link MappingConfiguration} based on their configurationServiceClientId.
     * 
     * @param configurationServiceClientId
     * @return
     */
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationServiceClientId(Long configurationServiceClientId);

    /**
     * This method retrieves a list of {@link MappingConfiguration} based on their configurationTypeId.
     * 
     * @param configurationTypeId
     * @return
     */
    public List<MappingConfiguration> getMappingConfigurationsByConfigurationTypeId(Long configurationTypeId);

    /**
     * This method retrieves a list of {@link MappingConfiguration} based on their sourceContextId.
     * 
     * @param sourceContextId
     * @return
     */
    public List<MappingConfiguration> getMappingConfigurationsBySourceContextId(Long sourceContextId);

    /**
     * This method retrieves a list of {@link MappingConfiguration} based on their targetContextId.
     * 
     * @param targetContextId
     * @return
     */
    public List<MappingConfiguration> getMappingConfigurationsByTargetContextId(Long targetContextId);

    /**
     * This method retrieves a {@link MappingConfiguration} context by client name, configuration type as well as the
     * source and target contexts.
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
     * This method retrieves a  list of {@link MappingConfiguration} by client name, configuration type as well as the
     * source and target contexts.
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
     * This method retrieves a list of {@link MappingConfigurationLite} by client name, configuration type as well as the
     * source and target contexts.
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
     * This method retrieves a {@link ConfigurationContext} based on its id.
     * 
     * @param id
     * @return
     */
    public ConfigurationContext getConfigurationContextById(Long id);

    /**
     * This method retrieves a {@link ConfigurationServiceClient} based on its id.
     * 
     * @param id
     * @return
     */
    public ConfigurationServiceClient getConfigurationServiceClientById(Long id);

    /**
     * This method retrieves a {@link ConfigurationType} based on its id.
     * 
     * @param id
     * @return
     */
    public ConfigurationType getConfigurationTypeById(Long id);

    /**
     * This method retrieves a list of {@link KeyLocationQuery} based on the mappingConfigurationId
     *  
     * @param mappingConfigurationId
     * @return
     */
    public List<ParameterName> getParameterNamesByMappingConfigurationId(Long mappingConfigurationId);

    /**
     * This method retrieves a list of {@link SourceConfigurationValue} based on the mappingConfigurationId
     *  
     * @param mappingConfigurationId
     * @return
     */
    public List<SourceConfigurationValue> getSourceConfigurationValueByMappingConfigurationId(Long mappingConfigurationId);

    /**
     * This method retrieves a {@link TargetConfigurationValue} based on its id.
     * 
     * @param id
     * @return
     */
    public TargetConfigurationValue getTargetConfigurationValueById(Long id);

    /**
     * This method retrieves a list of {@link SourceConfigurationValue} based on the targetConfigurationValueId
     *  
     * @param targetConfigurationValueId
     * @return
     */
    public List<SourceConfigurationValue> getSourceConfigurationValuesByTargetConfigurationValueId(Long targetConfigurationValueId);

    /**
     * Method to delete a {@link SourceConfigurationValue}
     * 
     * @param sourceConfigurationValue
     */
    public void deleteSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue);

    /**
     * Method to delete a {@link TargetConfigurationValue}
     * 
     * @param sourceConfigurationValue
     */
    public void deleteTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue);

    /**
     *  Method to delete a {@link MappingConfiguration}
     *  
     * @param mappingConfiguration
     */
    public void deleteMappingConfiguration(MappingConfiguration mappingConfiguration);

    /**
     * Get the next sequence number.
     * 
     * @return
     */
    public Long getNextSequenceNumber();

    /**
     * The the number of {@link SourceConfigurationValue} referencing a {@link TargetConfigurationValue}
     * 
     * @param targetConfigurationValue
     * @return
     */
    public Long getNumberOfSourceConfigurationValuesReferencingTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue);

    /**
     * This method retrieves a list of {@link ConfigurationType} based on the client name
     * 
     * @param clientname
     * @return
     */
    public List<ConfigurationType> getConfigurationTypesByClientName(final String clientname);

    /**
     * This method retrieves a list of {@link ConfigurationContext} based on the client name and the configuration type.
     * 
     * @param clientName
     * @param type
     * @return
     */
    public List<ConfigurationContext> getSourceConfigurationContextsByClientNameAndType(final String clientName, final String type);

    /**
     * This method retrieves a list of {@link ConfigurationContext} based on the client name and the configuration type and source context name.
     * 
     * @param clientName
     * @param type
     * @param sourceContext
     * @return
     */
    public List<ConfigurationContext> getTargetConfigurationContextByClientNameTypeAndSourceContext(final String clientName, final String type, final String sourceContext);
    
    /**
     * Set the configuration.
     * 
     * @param configuration
     */
    public void setConfiguration(MappingConfigurationServiceConfiguration configuration);

    /**
     *
     * @param groupId
     * @return
     */
    public List<ManyToManyTargetConfigurationValue> getManyToManyTargetConfigurationValues(Long groupId);

    /**
     *
     * @param targetConfigurationValue
     * @return
     */
    public Long storeManyToManyTargetConfigurationValue(ManyToManyTargetConfigurationValue targetConfigurationValue);

    /**
     *
     * @param targetConfigurationValue
     */
    public void deleteManyToManyTargetConfigurationValue(ManyToManyTargetConfigurationValue targetConfigurationValue);
}
