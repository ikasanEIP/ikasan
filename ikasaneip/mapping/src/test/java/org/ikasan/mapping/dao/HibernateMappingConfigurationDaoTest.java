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

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.ikasan.mapping.model.*;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.orm.hibernate4.HibernateTemplate;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;



/**
 * @author Ikasan Development Team
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
public class HibernateMappingConfigurationDaoTest
{
    /** Object being tested */
    @Resource private MappingConfigurationDao xaMappingConfigurationDao;

    /**
     * Before each test case, inject a mock {@link HibernateTemplate} to dao implementation
     * being tested
     */
    @Before
    public void setup()
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


        ConfigurationType manyToManyMapping = this.addConfigurationType("Many to Many");

        Long configurationContextId4 = this.addMappingConfiguration(context1, context2, new Long(-1),
                manyToManyMapping, configurationServiceClient, "description context 4");

        ArrayList<String> sourceValues = new ArrayList<>();
        sourceValues.add("s1");
        sourceValues.add("s2");
        sourceValues.add("s3");
        sourceValues.add("s4");

        ArrayList<String> targetValues = new ArrayList<>();
        targetValues.add("t1");
        targetValues.add("t2");
        targetValues.add("t3");
        targetValues.add("t4");

        this.addManyToManySourceSystemConfiguration(sourceValues, targetValues, configurationContextId4, 1l);

        sourceValues = new ArrayList<>();
        sourceValues.add("s5");
        sourceValues.add("s6");
        sourceValues.add("s7");
        sourceValues.add("s8");

        targetValues = new ArrayList<>();
        targetValues.add("t5");
        targetValues.add("t6");
        targetValues.add("t7");
        targetValues.add("t8");
        targetValues.add("t9");
        targetValues.add("t10");
        targetValues.add("t11");
        targetValues.add("t12");

        this.addManyToManySourceSystemConfiguration(sourceValues, targetValues, configurationContextId4, 2l);

        sourceValues = new ArrayList<>();
        sourceValues.add("alone");

        targetValues = new ArrayList<>();
        targetValues.add("t5");
        targetValues.add("t6");
        targetValues.add("t7");
        targetValues.add("t8");
        targetValues.add("t9");
        targetValues.add("t10");
        targetValues.add("t11");
        targetValues.add("t12");

        this.addManyToManySourceSystemConfiguration(sourceValues, targetValues, configurationContextId4, 3l);
    }

    @Test
    @DirtiesContext
    public void test_success_many_to_many()
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("s1");
        sourceSystemValues.add("s2");
        sourceSystemValues.add("s3");
        sourceSystemValues.add("s4");

        List<String> result = this.xaMappingConfigurationDao.getTargetConfigurationValues("CMI2", "Many to Many", "Tradeweb",
                "Bloomberg", sourceSystemValues);

        Assert.assertEquals(4, result.size());
        Assert.assertTrue(result.contains("t1"));
        Assert.assertTrue(result.contains("t2"));
        Assert.assertTrue(result.contains("t3"));
        Assert.assertTrue(result.contains("t4"));

        sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("s5");
        sourceSystemValues.add("s6");
        sourceSystemValues.add("s7");
        sourceSystemValues.add("s8");

        result = this.xaMappingConfigurationDao.getTargetConfigurationValues("CMI2", "Many to Many", "Tradeweb",
                "Bloomberg", sourceSystemValues);

        Assert.assertEquals(8, result.size());

        sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("s1");
        sourceSystemValues.add("s6");
        sourceSystemValues.add("s7");
        sourceSystemValues.add("s8");

        result = this.xaMappingConfigurationDao.getTargetConfigurationValues("CMI2", "Many to Many", "Tradeweb",
                "Bloomberg", sourceSystemValues);

        Assert.assertEquals(0, result.size());

        sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("alone");

        result = this.xaMappingConfigurationDao.getTargetConfigurationValues("CMI2", "Many to Many", "Tradeweb",
                "Bloomberg", sourceSystemValues);

        Assert.assertEquals(8, result.size());
    }

    @Test 
    @DirtiesContext
    public void test_success_no_results()
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("no_results");

        String result = this.xaMappingConfigurationDao.getTargetConfigurationValue("CMI2", "Salesperson to Salesperson Mapping", "Tradeweb", 
            "Bloomberg", sourceSystemValues);

        Assert.assertEquals(null, result);
    }

    @Test
    @DirtiesContext
    public void test_success_1_paramater_mapping()
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("briordan2");

        String result = this.xaMappingConfigurationDao.getTargetConfigurationValue("CMI2", "Salesperson to Salesperson Mapping", "Tradeweb", 
            "Bloomberg", sourceSystemValues);

        Assert.assertEquals("BEN", result);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configuration()
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("briordan2");

        MappingConfiguration result = this.xaMappingConfigurationDao.getMappingConfiguration("CMI2", "Salesperson to Salesperson Mapping", "Tradeweb", 
            "Bloomberg");

        Assert.assertNotNull(result);
    }

    @Test
    @DirtiesContext
    public void test_success_delete_mapping_configuration()
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("briordan2");

        MappingConfiguration result = this.xaMappingConfigurationDao.getMappingConfiguration("CMI2", "Salesperson to Salesperson Mapping", "Tradeweb", 
            "Bloomberg");

        Assert.assertNotNull(result);

        this.xaMappingConfigurationDao.deleteMappingConfiguration(result);       

        result = this.xaMappingConfigurationDao.getMappingConfiguration("CMI2", "Salesperson to Salesperson Mapping", "Tradeweb", 
                "Bloomberg");

        Assert.assertNull(result);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations()
    {

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("CMI2", "Salesperson to Salesperson Mapping", "Tradeweb", 
            "Bloomberg");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations_null_target_context()
    {

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("CMI2", "Salesperson to Salesperson Mapping", "Tradeweb", 
            null);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations_empty_string_target_context()
    {

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("CMI2", "Salesperson to Salesperson Mapping", "Tradeweb", 
            "");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations_null_source_context()
    {

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("CMI2", "Salesperson to Salesperson Mapping", null, 
            "Bloomberg");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations_empty_string_source_context()
    {

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("CMI2", "Salesperson to Salesperson Mapping", "", 
            "Bloomberg");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations_null_mapping_configuration_name()
    {

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("CMI2", null, "Tradeweb", 
            "Bloomberg");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 4);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations_empty_string_mapping_configuration_name()
    {

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("CMI2", "", "Tradeweb", 
            "Bloomberg");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 4);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations_null_client_name()
    {

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations(null, "Salesperson to Salesperson Mapping", "Tradeweb", 
            "Bloomberg");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    @DirtiesContext
    public void test_success_getSourceConfigurationValuesByTargetConfigurationValueId()
    {
        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("CMI2", "Salesperson to Salesperson Mapping", "", 
                "Bloomberg");
        
        for(MappingConfiguration mappingConfiguration: result)
        {
            Set<SourceConfigurationValue> sourceConfigurationValues =  mappingConfiguration.getSourceConfigurationValues();
            Iterator<SourceConfigurationValue> sourceConfigurationValuesItr = sourceConfigurationValues.iterator();

            while(sourceConfigurationValuesItr.hasNext())
            {
                List<SourceConfigurationValue> results = this.xaMappingConfigurationDao.getSourceConfigurationValuesByTargetConfigurationValueId
                    (sourceConfigurationValuesItr.next().getTargetConfigurationValue().getId());

                Assert.assertNotNull(results);
                Assert.assertEquals(results.size(), mappingConfiguration.getNumberOfParams().intValue());
            }
        }
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations_empty_string_client_name()
    {

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("", "Salesperson to Salesperson Mapping", "Tradeweb", 
            "Bloomberg");

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 1);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configurations_narrow_by_client()
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("briordan2");

        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getMappingConfigurations("CMI2", null, null, null);

        Assert.assertNotNull(result);
        Assert.assertEquals(result.size(), 4);
    }

    @Test
    @DirtiesContext
    public void test_success_get_mapping_configuration_no_result()
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("briordan2");

        MappingConfiguration result = this.xaMappingConfigurationDao.getMappingConfiguration("BAD CLIENT", "Salesperson to Salesperson Mapping", "Tradeweb", 
            "Bloomberg");

        Assert.assertNull(result);
    }

    @Test
    @DirtiesContext
    public void test_success_2_paramater_mapping()
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("BARX");
        sourceSystemValues.add("TRSY");

        String result = this.xaMappingConfigurationDao.getTargetConfigurationValue("CMI2", "Dealer and Product to Account", "Tradeweb", 
            "Bloomberg", sourceSystemValues);

        Assert.assertEquals("BARCLON", result);
    }

    @Test
    @DirtiesContext
    public void test_success_2_paramater_mapping_2()
    {
        List<String> sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("true");

        String result = this.xaMappingConfigurationDao.getTargetConfigurationValue("CMI2", "Product Type to Tradebook Mapping", "Tradeweb", 
            "Bloomberg", sourceSystemValues);

        Assert.assertEquals("YENTBFB", result);

        sourceSystemValues = new ArrayList<String>();
        sourceSystemValues.add("false");

        result = this.xaMappingConfigurationDao.getTargetConfigurationValue("CMI2", "Product Type to Tradebook Mapping", "Tradeweb", 
            "Bloomberg", sourceSystemValues);

        Assert.assertEquals("YENGOVT", result);
    }

    @Test
    @DirtiesContext
    public void test_success_get_configuration_service_by_name()
    {
        ConfigurationServiceClient result = this.xaMappingConfigurationDao.getConfigurationServiceClientByName("CMI2");

        Assert.assertEquals("CMI2", result.getName());
        Assert.assertEquals("org.ikasan.mapping.keyQueryProcessor.impl.XPathKeyLocationQueryProcessor"
            , result.getKeyLocationQueryProcessorType());
    }

    
    @Test
    @DirtiesContext
    public void test_get_all_configuration_context_success()
    {
        List<ConfigurationContext> result = this.xaMappingConfigurationDao.getAllConfigurationContexts();

        Assert.assertEquals(2, result.size());
    }

    @Test
    @DirtiesContext
    public void test_get_configuration_context_by_id_success()
    {
        List<ConfigurationContext> result = this.xaMappingConfigurationDao.getAllConfigurationContexts();

        Assert.assertEquals(2, result.size());

        for(ConfigurationContext configurationContext: result)
        {
            ConfigurationContext configurationContextResult = this.xaMappingConfigurationDao
                    .getConfigurationContextById(configurationContext.getId());

            Assert.assertEquals(configurationContext, configurationContextResult);
        }
    }

    @Test
    @DirtiesContext
    public void test_get_all_configuration_types_success()
    {
        List<ConfigurationType> result = this.xaMappingConfigurationDao.getAllConfigurationTypes();

        Assert.assertEquals(4, result.size());
    }

    @Test
    @DirtiesContext
    public void test_get_configuration_type_by_id_success()
    {
        List<ConfigurationType> result = this.xaMappingConfigurationDao.getAllConfigurationTypes();

        Assert.assertEquals(4, result.size());

        for(ConfigurationType configurationType: result)
        {
            ConfigurationType configurationTypeResult = this.xaMappingConfigurationDao
                    .getConfigurationTypeById(configurationType.getId());

            Assert.assertEquals(configurationType, configurationTypeResult);
        }
    }

    @Test
    @DirtiesContext
    public void test_get_all_configuration_service_clients_success()
    {
        List<ConfigurationServiceClient> result = this.xaMappingConfigurationDao.getAllConfigurationServiceClients();

        Assert.assertEquals(1, result.size());
    }

    @Test
    @DirtiesContext
    public void test_get_configuration_service_clients_by_id_success()
    {
        List<ConfigurationServiceClient> result = this.xaMappingConfigurationDao.getAllConfigurationServiceClients();
        
        Assert.assertEquals(1, result.size());

        for(ConfigurationServiceClient configurationServiceClient: result)
        {
            ConfigurationServiceClient configurationServiceClientResult = this.xaMappingConfigurationDao
                    .getConfigurationServiceClientById(configurationServiceClient.getId());

            Assert.assertEquals(configurationServiceClient, configurationServiceClientResult);
        }
    }

    @Test
    @DirtiesContext
    public void test_get_key_location_queries_success()
    {
        List<String> result = this.xaMappingConfigurationDao.getKeyLocationQuery
                ("Salesperson to Salesperson Mapping", "Tradeweb", "Bloomberg", "CMI2");

        Assert.assertEquals(1, result.size());
    }

    @Test
    @DirtiesContext
    public void test_get_key_location_queries_buy_mapping_configuration_id_success()
    {
        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getAllMappingConfigurations();

        for(MappingConfiguration mappingConfiguration: result)
        {
            List<KeyLocationQuery> mappingConfigurationResult = this.xaMappingConfigurationDao
                    .getKeyLocationQueriesByMappingConfigurationId(mappingConfiguration.getId());
            
//            Assert.assertTrue(mappingConfigurationResult.size() > 0);
        }
    }

    @Test
    @DirtiesContext
    public void test_get_mapping_configuration_by_id_success()
    {
        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getAllMappingConfigurations();

        for(MappingConfiguration mappingConfiguration: result)
        {
            MappingConfiguration mappingConfigurationResult = this.xaMappingConfigurationDao
                    .getMappingConfigurationById(mappingConfiguration.getId());
            
            Assert.assertEquals(mappingConfiguration.getId(), mappingConfigurationResult.getId());
        }
    }

    @Test
    @DirtiesContext
    public void test_get_mapping_configuration_by_configuration_service_client_id_success()
    {
        List<ConfigurationServiceClient> result = this.xaMappingConfigurationDao.getAllConfigurationServiceClients();

        Assert.assertEquals(1, result.size());

        List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
                .getMappingConfigurationsByConfigurationServiceClientId(result.get(0).getId());

        Assert.assertEquals(4, mappingConfigurations.size());
    }

    @Test
    @DirtiesContext
    public void test_get_mapping_configuration_by_configuration_type_id_success()
    {
        List<ConfigurationType> result = this.xaMappingConfigurationDao.getAllConfigurationTypes();
        
        Assert.assertEquals(4, result.size());

        for(ConfigurationType configurationType: result)
        {
            List<MappingConfiguration> mappingConfigurations = this.xaMappingConfigurationDao
                    .getMappingConfigurationsByConfigurationTypeId(configurationType.getId());
            
            Assert.assertTrue(mappingConfigurations.size() > 0);
        }
    }

    @Test
    @DirtiesContext
    public void test_get_mapping_configuration_by_source_context_id_success()
    {
        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getAllMappingConfigurations();

        for(MappingConfiguration mappingConfiguration: result)
        {
            List<MappingConfiguration> mappingConfigurationResults = this.xaMappingConfigurationDao
                    .getMappingConfigurationsBySourceContextId(mappingConfiguration.getSourceContext().getId());

            for(MappingConfiguration mappingConfigurationResult: mappingConfigurationResults)
            {
                Assert.assertEquals(mappingConfiguration.getSourceContext(), mappingConfigurationResult.getSourceContext());
            }
        }
    }

    @Test
    @DirtiesContext
    public void test_get_source_configuration_value_by_mapping_configuration_id_success()
    {
        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getAllMappingConfigurations();

        for(MappingConfiguration mappingConfiguration: result)
        {
            List<SourceConfigurationValue> sourceConfigurationValues = this.xaMappingConfigurationDao
                    .getSourceConfigurationValueByMappingConfigurationId(mappingConfiguration.getId());
            
//            Assert.assertTrue(sourceConfigurationValues.size() > 0);
        }
    }

    @Test
    @DirtiesContext
    public void test_get_target_configuration_value_by_id_success()
    {
        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getAllMappingConfigurations();

        for(MappingConfiguration mappingConfiguration: result)
        {
            List<SourceConfigurationValue> sourceConfigurationValues = this.xaMappingConfigurationDao
                    .getSourceConfigurationValueByMappingConfigurationId(mappingConfiguration.getId());
            
//            Assert.assertTrue(sourceConfigurationValues.size() > 0);
//
//            for(SourceConfigurationValue sourceConfigurationValue: sourceConfigurationValues)
//            {
//                TargetConfigurationValue value = sourceConfigurationValue.getTargetConfigurationValue();
//
//                Assert.assertNotNull(value);
//            }
        }
    }

    @Test
    @DirtiesContext
    public void test_get_mapping_configuration_by_target_context_id_success()
    {
        List<MappingConfiguration> result = this.xaMappingConfigurationDao.getAllMappingConfigurations();

        for(MappingConfiguration mappingConfiguration: result)
        {
            List<MappingConfiguration> mappingConfigurationResults = this.xaMappingConfigurationDao
                    .getMappingConfigurationsByTargetContextId(mappingConfiguration.getTargetContext().getId());

            for(MappingConfiguration mappingConfigurationResult: mappingConfigurationResults)
            {
                Assert.assertEquals(mappingConfiguration.getTargetContext(), mappingConfigurationResult.getTargetContext());
            }
        }
    }

    /**
     *
     * @param name
     * @param keyLocationQueryProcessorType
     * @return
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
     *
     * @param value
     * @param mappingConfigurationId
     * @return
     */
    private Long addKeyLocationQuery(String value, Long mappingConfigurationId)
    {
        KeyLocationQuery keyLocationQuery = new KeyLocationQuery();
        keyLocationQuery.setValue(value);
        keyLocationQuery.setMappingConfigurationId(mappingConfigurationId);

        return this.xaMappingConfigurationDao.storeKeyLocationQuery(keyLocationQuery);
    }

    /**
     *
     * @param name
     * @return
     */
    private ConfigurationType addConfigurationType(String name)
    {
        ConfigurationType configurationType = new ConfigurationType();
        configurationType.setName(name);

        this.xaMappingConfigurationDao.storeConfigurationType(configurationType);

        return configurationType;
    }

    /**
     *
     * @param name
     * @param description
     * @return
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
     *
     * @param sourceContext
     * @param targetContext
     * @param numberOfParams
     * @param configurationType
     * @param configurationServiceClient
     * @param description
     * @return
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
     * @param mappingConfigurationId
     * @param targetConfigurationValue
     * @return
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
    }


    private void addManyToManySourceSystemConfiguration(List<String> sourceSystemValues, List<String> targetSystemValues, Long mappingConfigurationId, Long groupingId)
    {
        for(String sourceValue: sourceSystemValues)
        {
            SourceConfigurationValue value = new SourceConfigurationValue();

            value.setMappingConfigurationId(mappingConfigurationId);
            value.setSourceSystemValue(sourceValue);
            value.setSourceConfigGroupId(groupingId);

            this.xaMappingConfigurationDao.storeSourceConfigurationValue(value);
        }

        for(String targetValue: targetSystemValues)
        {
            ManyToManyTargetConfigurationValue value = new ManyToManyTargetConfigurationValue();
            value.setTargetSystemValue(targetValue);

            Long targetId = this.xaMappingConfigurationDao.storeManyToManyTargetConfigurationValue(value);

            SourceValueTargetValueGrouping grouping = new SourceValueTargetValueGrouping();
            SourceValueTargetValueGroupingPk pk = new SourceValueTargetValueGroupingPk();
            pk.setGroupingId(groupingId);
            pk.setTargetValueId(targetId);

            grouping.setId(pk);
            grouping.setTargetValueId(targetId);
            grouping.setGroupingId(groupingId);

            this.xaMappingConfigurationDao.storeSourceValueTargetValueGrouping(grouping);
        }
    }
}
