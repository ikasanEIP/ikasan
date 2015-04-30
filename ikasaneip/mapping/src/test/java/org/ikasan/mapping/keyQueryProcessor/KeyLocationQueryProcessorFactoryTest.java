/*
 * $Id: KeyLocationQueryProcessorFactoryTest.java 40152 2014-10-17 15:57:49Z stewmi $
 * $URL: https://svc-vcs-prd.uk.mizuho-sc.com:18080/svn/architecture/cmi2/trunk/projects/mappingConfigurationService/api/src/test/java/com/mizuho/cmi2/mappingConfiguration/keyQueryProcessor/KeyLocationQueryProcessorFactoryTest.java $
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

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.ikasan.mapping.dao.MappingConfigurationDao;
import org.ikasan.mapping.keyQueryProcessor.impl.XPathKeyLocationQueryProcessor;
import org.ikasan.mapping.model.ConfigurationContext;
import org.ikasan.mapping.model.ConfigurationServiceClient;
import org.ikasan.mapping.model.ConfigurationType;
import org.ikasan.mapping.model.KeyLocationQuery;
import org.ikasan.mapping.model.MappingConfiguration;
import org.ikasan.mapping.model.SourceConfigurationValue;
import org.ikasan.mapping.model.TargetConfigurationValue;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate3.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


/**
 * @author CMI2 Development Team
 *
 */
@SuppressWarnings("unqualified-field-access")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={
        "/mapping-conf.xml",
        "/hsqldb-config.xml",
        "/substitute-components.xml",
        "/mock-components.xml"
})
public class KeyLocationQueryProcessorFactoryTest
{
    /** Object being tested */
    @Resource private MappingConfigurationDao xaMappingConfigurationDao;

    Map<String, KeyLocationQueryProcessor> keyLocationQueryProcessorImplementations;
    
    /**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     */
    /**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     */
    @Before public void setup()
    {
   	
    	ConfigurationServiceClient configurationServiceClient = this.addConfigurationServiceClient("CMI2", 
            "org.ikasan.mapping.keyQueryProcessor.impl.XPathKeyLocationQueryProcessor");
        ConfigurationType dealerToDealer = this.addConfigurationType("Dealer and Product to Account");
        ConfigurationType salesPersonToSalesPerson = this.addConfigurationType("Salesperson to Salesperson Mapping");
        ConfigurationType productTypeToTradeBook = this.addConfigurationType("Product Type to Tradebook Mapping");

        ConfigurationContext context1 = this.addConfigurationContext("Tradeweb", "Tradeweb");
        ConfigurationContext context2 = this.addConfigurationContext("Bloomberg", "Bloomberg");

        Long configurationContextId1 = this.addMappingConfiguration(context1, context2, new Long(2), 
            dealerToDealer, configurationServiceClient, "description context 1");
        Long configurationContextId2 = this.addMappingConfiguration(context1, context2, new Long(1), 
            salesPersonToSalesPerson, configurationServiceClient, "description context 2");
        Long configurationContextId3 = this.addMappingConfiguration(context1, context2, new Long(1), 
            productTypeToTradeBook, configurationServiceClient, "description context 2");

        this.addKeyLocationQuery("some xpath", configurationContextId1);
        this.addKeyLocationQuery("another xpath", configurationContextId1);
        this.addKeyLocationQuery("some xpath", configurationContextId2);
        this.addKeyLocationQuery("some xpath", configurationContextId3);

        TargetConfigurationValue targetId1 = this.addTargetSystemConfiguration("BARCLON");
        TargetConfigurationValue targetId2 = this.addTargetSystemConfiguration("BNPPAR");

        this.addSourceSystemConfiguration("BARX", configurationContextId1, targetId1);
        this.addSourceSystemConfiguration("TRSY", configurationContextId1, targetId1);
        this.addSourceSystemConfiguration("AGCY", configurationContextId1, targetId1);
        this.addSourceSystemConfiguration("MBS", configurationContextId1, targetId1);

        this.addSourceSystemConfiguration("BNPP", configurationContextId1, targetId2);
        this.addSourceSystemConfiguration("TRSY", configurationContextId1, targetId2);
        this.addSourceSystemConfiguration("AGCY", configurationContextId1, targetId2);
        this.addSourceSystemConfiguration("MBS", configurationContextId1, targetId2);

        TargetConfigurationValue targetId3 = this.addTargetSystemConfiguration("ZEKRAA");
        TargetConfigurationValue targetId4 = this.addTargetSystemConfiguration("VIDAUISA");
        TargetConfigurationValue targetId5 = this.addTargetSystemConfiguration("BEN");
        TargetConfigurationValue targetId6 = this.addTargetSystemConfiguration("IMONDIV");

        this.addSourceSystemConfiguration("azehra", configurationContextId2, targetId3);
        this.addSourceSystemConfiguration("isabelv", configurationContextId2, targetId4);
        this.addSourceSystemConfiguration("briordan2", configurationContextId2, targetId5);
        this.addSourceSystemConfiguration("vimondi", configurationContextId2, targetId6);

        TargetConfigurationValue targetId7 = this.addTargetSystemConfiguration("YENGOVT");
        TargetConfigurationValue targetId8 = this.addTargetSystemConfiguration("YENTBFB");

        this.addSourceSystemConfiguration("false", configurationContextId3, targetId7);
        this.addSourceSystemConfiguration("true", configurationContextId3, targetId8);

        keyLocationQueryProcessorImplementations = new HashMap<String, KeyLocationQueryProcessor>();
        keyLocationQueryProcessorImplementations.put("org.ikasan.mapping.keyQueryProcessor.impl.XPathKeyLocationQueryProcessor"
            , new XPathKeyLocationQueryProcessor());
    }

    @Test(expected = IllegalArgumentException.class) 
    @DirtiesContext
    public void test_null_dao_fail() throws KeyLocationQueryProcessorException
    {
        new KeyLocationQueryProcessorFactory(null,
            this.keyLocationQueryProcessorImplementations);
    }

    @Test(expected = IllegalArgumentException.class) 
    @DirtiesContext
    public void test_null_processor_implementations_fail() throws KeyLocationQueryProcessorException
    {
        new KeyLocationQueryProcessorFactory(this.xaMappingConfigurationDao,
            null);
    }

    @Test(expected = IllegalArgumentException.class) 
    @DirtiesContext
    public void test_empty_processor_implementations_fail() throws KeyLocationQueryProcessorException
    {
        new KeyLocationQueryProcessorFactory(this.xaMappingConfigurationDao,
            new HashMap<String, KeyLocationQueryProcessor>());
    }

    @Test
    @DirtiesContext
    public void test_get_factory_success() throws KeyLocationQueryProcessorException
    {
        KeyLocationQueryProcessorFactory factory = new KeyLocationQueryProcessorFactory(this.xaMappingConfigurationDao,
            this.keyLocationQueryProcessorImplementations);

        KeyLocationQueryProcessor keyLocationQueryProcessor = factory.getKeyLocationQueryProcessor("CMI2");

        Assert.assertTrue(keyLocationQueryProcessor instanceof XPathKeyLocationQueryProcessor);
    }

    @Test (expected = KeyLocationQueryProcessorException.class) 
    @DirtiesContext
    public void test_get_bad_client() throws KeyLocationQueryProcessorException
    {
        KeyLocationQueryProcessorFactory factory = new KeyLocationQueryProcessorFactory(this.xaMappingConfigurationDao,
            this.keyLocationQueryProcessorImplementations);

        factory.getKeyLocationQueryProcessor("BAD_CLIENT");
    }

    @Test (expected = KeyLocationQueryProcessorException.class) 
    @DirtiesContext
    public void test_get_bad_processor() throws KeyLocationQueryProcessorException
    {
        KeyLocationQueryProcessorFactory factory = new KeyLocationQueryProcessorFactory(this.xaMappingConfigurationDao,
            this.keyLocationQueryProcessorImplementations);

        factory.getKeyLocationQueryProcessor("CMI3");
    }

    /**
     * Helper method to add the configuration type to the database.
     * 
     * @param id
     * @param name
     */
    private ConfigurationServiceClient addConfigurationServiceClient(String name, String keyLocationQueryProcessorType)
    {
        ConfigurationServiceClient configurationServiceClient = new ConfigurationServiceClient();
        configurationServiceClient.setName(name);
        configurationServiceClient.setKeyLocationQueryProcessorType(keyLocationQueryProcessorType);

        this.xaMappingConfigurationDao.storeConfigurationServiceClient(configurationServiceClient);
        
        return configurationServiceClient;
    }

    /**
     * Helper method to add the configuration type to the database.
     * 
     * @param id
     * @param name
     */
    private Long addKeyLocationQuery(String value, Long mappingConfigurationId)
    {
        KeyLocationQuery keyLocationQuery = new KeyLocationQuery();
        keyLocationQuery.setValue(value);
        keyLocationQuery.setMappingConfigurationId(mappingConfigurationId);

        return this.xaMappingConfigurationDao.storeKeyLocationQuery(keyLocationQuery);
    }

    /**
     * Helper method to add the configuration type to the database.
     * 
     * @param id
     * @param name
     */
    private ConfigurationType addConfigurationType(String name)
    {
        ConfigurationType configurationType = new ConfigurationType();
        configurationType.setName(name);

        this.xaMappingConfigurationDao.storeConfigurationType(configurationType);

        return configurationType;
    }

    /**
     * Helper method to add the configuration type to the database.
     * 
     * @param id
     * @param name
     */
    private ConfigurationContext addConfigurationContext(String name, String description)
    {
        ConfigurationContext configurationContext = new ConfigurationContext();
        configurationContext.setName(name);
        configurationContext.setDescription(description);

        this.xaMappingConfigurationDao.storeConfigurationContext(configurationContext);
        
        return configurationContext;
    }

    /**
     * Helper method to add a configuration context to the database.
     * 
     * @param sourceContext
     * @param targetContext
     * @param numberOfParams
     * @param configurationTypeId
     */
    private Long addMappingConfiguration(ConfigurationContext sourceContext, ConfigurationContext targetContext, Long numberOfParams, ConfigurationType configurationType,
    		ConfigurationServiceClient configurationServiceClient, String description)
    {
        MappingConfiguration configurationContext = new MappingConfiguration();
        configurationContext.setConfigurationType(configurationType);
        configurationContext.setNumberOfParams(numberOfParams);
        configurationContext.setSourceContext(sourceContext);
        configurationContext.setTargetContext(targetContext);
        configurationContext.setConfigurationServiceClient(configurationServiceClient);
        configurationContext.setDescription(description);

        return this.xaMappingConfigurationDao.storeMappingConfiguration(configurationContext);
    }

    /**
     * Helper method to add a source system value to the database.
     * 
     * @param sourceSystemValue
     * @param configurationContextId
     * @param targetConfigurationValueId
     */
    private Long addSourceSystemConfiguration(String sourceSystemValue, Long mappingConfigurationId, TargetConfigurationValue targetConfigurationValue)
    {
        SourceConfigurationValue sourceConfigurationValue = new SourceConfigurationValue();
        sourceConfigurationValue.setMappingConfigurationId(mappingConfigurationId);
        sourceConfigurationValue.setSourceSystemValue(sourceSystemValue);
        sourceConfigurationValue.setTargetConfigurationValue(targetConfigurationValue);

        return this.xaMappingConfigurationDao.storeSourceConfigurationValue(sourceConfigurationValue);
    }

    /**
     * Helper method to add a target system value to the database.
     * 
     * @param targetSystemValue
     */
    private TargetConfigurationValue addTargetSystemConfiguration(String targetSystemValue)
    {
        TargetConfigurationValue targetConfigurationValue = new TargetConfigurationValue();
        targetConfigurationValue.setTargetSystemValue(targetSystemValue);

        this.xaMappingConfigurationDao.storeTargetConfigurationValue(targetConfigurationValue);

        return targetConfigurationValue;
    }}
