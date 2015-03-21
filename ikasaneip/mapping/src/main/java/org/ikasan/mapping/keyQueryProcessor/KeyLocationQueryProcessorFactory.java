/*
 * $Id: KeyLocationQueryProcessorFactory.java 31879 2013-07-30 15:03:33Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/main/java/com/mizuho/cmi2/mappingConfiguration/keyQueryProcessor/KeyLocationQueryProcessorFactory.java $
 *
 * ====================================================================
 *
 * Copyright (c) 2000-2011 by Mizuho International plc.
 * All Rights Reserved.
 *
 * ====================================================================
 *
 */
package org.ikasan.mapping.keyQueryProcessor;

import java.util.Map;

import org.ikasan.mapping.dao.MappingConfigurationDao;
import org.ikasan.mapping.model.ConfigurationServiceClient;


/**
 * @author CMI2 Development Team
 *
 */
public class KeyLocationQueryProcessorFactory
{
    private MappingConfigurationDao mappingConfigurationDao;
    private Map<String, KeyLocationQueryProcessor> keyLocationQueryProcessorImplementations;

    /**
     * Constructor
     * 
     * @param keyLocationQueryProcessorImplementations
     */
    public KeyLocationQueryProcessorFactory(MappingConfigurationDao mappingConfigurationDao, Map<String, KeyLocationQueryProcessor> keyLocationQueryProcessorImplementations)
    {
        this.mappingConfigurationDao = mappingConfigurationDao;
        if(this.mappingConfigurationDao == null)
        {
            throw new IllegalArgumentException("mappingConfigurationDao cannot be null!");
        }
        this.keyLocationQueryProcessorImplementations = keyLocationQueryProcessorImplementations;
        if(this.keyLocationQueryProcessorImplementations == null)
        {
            throw new IllegalArgumentException("keyLocationQueryProcessorImplementations cannot be null!");
        }
        if(this.keyLocationQueryProcessorImplementations.size() == 0)
        {
            throw new IllegalArgumentException("keyLocationQueryProcessorImplementations is empty!");
        }
    }

    /**
     * 
     * @param configurationServiceServiceClientName
     * @return
     */
    public KeyLocationQueryProcessor getKeyLocationQueryProcessor(String configurationServiceClientName) throws KeyLocationQueryProcessorException
    {
        ConfigurationServiceClient configurationServiceClient = this.mappingConfigurationDao
                .getConfigurationServiceClientByName(configurationServiceClientName);

        if(configurationServiceClient == null)
        {
            throw new KeyLocationQueryProcessorException("No configuration service client found with name: " 
                    + configurationServiceClientName);
        }

        KeyLocationQueryProcessor keyLocationQueryProcessor 
            = this.keyLocationQueryProcessorImplementations.get(configurationServiceClient.getKeyLocationQueryProcessorType());

        if(keyLocationQueryProcessor == null)
        {
            throw new KeyLocationQueryProcessorException("No key location query processor found for configuration service client: " 
                    + configurationServiceClientName);
        }

        return keyLocationQueryProcessor;
    }
}
