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
package org.ikasan.mapping.dao;

import java.util.List;

import org.ikasan.mapping.model.*;
import org.springframework.dao.DataAccessException;


/**
 * @author Ikasan Development Team
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
     * @param numParams
     * @return
     */
    public String getTargetConfigurationValueWithIgnores(final String clientName, final String configurationType, final String sourceSystem
            , final String targetSystem, final List<String> sourceSystemValues, final int numParams);

    /**
     *
     * @param clientName
     * @param configurationType
     * @param sourceSystem
     * @param targetSystem
     * @param sourceSystemValues
     * @param numParams
     * @return
     */
    public String getTargetConfigurationValueWithIgnoresWithOrdinality(final String clientName, final String configurationType, final String sourceSystem
            , final String targetSystem, final List<QueryParameterImpl> sourceSystemValues, final int numParams);

    /**
     *
     * @param clientName
     * @param configurationType
     * @param sourceContext
     * @param targetContext
     * @param sourceSystemValues
     * @return
     */
    public String getTargetConfigurationValue(final String clientName, String configurationType,
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
    public String getTargetConfigurationValueWithOrdinality(final String clientName, String configurationType,
                                              String sourceContext, String targetContext, List<QueryParameterImpl> sourceSystemValues);

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
     * 
     * @param configurationServiceClientName
     * @return
     */
    public ConfigurationServiceClient getConfigurationServiceClientByName(String configurationServiceClientName);

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

    public Long storeMappingConfiguration(MappingConfiguration mappingConfiguration) throws DataAccessException;

    public Long storeSourceConfigurationValue(SourceConfigurationValue sourceConfigurationValue);
    
    public List<SourceConfigurationValue> getSourceConfigurationValues(Long mappingConfigurationId, List<String> values);

    public Long storeTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue);

    public Long storeManyToManyTargetConfigurationValue(ManyToManyTargetConfigurationValue targetConfigurationValue);

    public Long storeConfigurationServiceClient(ConfigurationServiceClient configurationServiceClient);

    public void storeSourceValueTargetValueGrouping(SourceValueTargetValueGrouping sourceValueTargetValueGrouping);

    public Long storeParameterName(ParameterName parameterName);

    public Long getNumberOfSourceValuesForGroupId(Long groupId);

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
    public List<ParameterName> getParameterNameByMappingConfigurationId(Long mappingConfigurationId);

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
     * @param targetConfigurationValue
     */
    public void deleteTargetConfigurationValue(TargetConfigurationValue targetConfigurationValue);

    /**
     *
     * @param targetConfigurationValue
     */
    public void deleteManyToManyTargetConfigurationValue(ManyToManyTargetConfigurationValue targetConfigurationValue);

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

    /**
     *
     * @param groupId
     * @return
     */
    public List<ManyToManyTargetConfigurationValue> getManyToManyTargetConfigurationValues(Long groupId);


    /**
     * 
     * @param clientName
     * @param configurationType
     * @param sourceSystem
     * @param targetSystem
     * @param targetSystemValues
     * @return
     */
    public String getReverseMapping(final String clientName, final String configurationType, final String sourceSystem
            , final String targetSystem, String targetSystemValues);
}
