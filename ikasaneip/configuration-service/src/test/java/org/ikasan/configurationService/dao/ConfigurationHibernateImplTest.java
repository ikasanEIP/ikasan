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
package org.ikasan.configurationService.dao;

import org.ikasan.configurationService.model.ConfigurationParameterObjectImpl;
import org.ikasan.configurationService.model.DefaultConfiguration;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.*;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

/**
 * Test class for ConfiguredResourceConfigurationService based on
 * the implementation of a ConfigurationService contract.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations = {
        "/configuration-service-conf.xml",
        "/serialiser-service-conf.xml",
        "/transaction-conf.xml",
        "/h2-datasource-conf.xml",
        "/substitute-components.xml"
})
public class ConfigurationHibernateImplTest
{

    @Resource
    ConfigurationDao configurationServiceDao;
    @Resource
    SerialiserFactory ikasanSerialiserFactory;

    private Serialiser<Object,byte[]> serialiser;

    @Before
    public void setup(){
        serialiser = ikasanSerialiserFactory.getDefaultSerialiser();
    }

    @Test
    @DirtiesContext
    public void save_followed_by_find_when_object_is_String() {

        String resourceId = "testResourceId";
        String stringValue = "stringTestValue";
        byte[] serialisedValue = serialiser.serialise(stringValue);
        ConfigurationParameter stringParam = new ConfigurationParameterObjectImpl("stringValue",stringValue, serialisedValue, "testDescription");


        Configuration<List<ConfigurationParameter>> configuration = new DefaultConfiguration(resourceId, Arrays.asList(stringParam));

        // test save
        configurationServiceDao.save(configuration);


        // get results and compare values
        Configuration<List<ConfigurationParameter>> result = configurationServiceDao.findByConfigurationId(resourceId);

        assertArrayEquals(serialisedValue,result.getParameters().get(0).getSerialisedValue());
        assertEquals("stringValue",result.getParameters().get(0).getName());

    }

    @Test
    @DirtiesContext
    public void save_followed_by_find_when_object_is_Integer() {

        String resourceId = "testResourceId";
        Integer integerValue = 100;
        byte[] serialisedValue = serialiser.serialise(integerValue);
        ConfigurationParameter intgerParam = new ConfigurationParameterObjectImpl("intValue", integerValue, serialisedValue, "testDescription");

        Configuration<List<ConfigurationParameter>> configuration = new DefaultConfiguration(resourceId, Arrays.asList(intgerParam));

        configurationServiceDao.save(configuration);

        // get results and compare values
        Configuration<List<ConfigurationParameter>> result = configurationServiceDao.findByConfigurationId(resourceId);

        assertArrayEquals(serialisedValue,result.getParameters().get(0).getSerialisedValue());
        assertEquals("intValue",result.getParameters().get(0).getName());

    }

    @Test
    @DirtiesContext
    public void save_followed_by_find_when_object_is_Long() {

        String resourceId = "testResourceId";
        Long longValue = 100l;
        byte[] serialisedValue = serialiser.serialise(longValue);
        ConfigurationParameter longParam = new ConfigurationParameterObjectImpl("longValue", longValue, serialisedValue,"testDescription");

        Configuration<List<ConfigurationParameter>> configuration = new DefaultConfiguration(resourceId, Arrays.asList(longParam));

        configurationServiceDao.save(configuration);

        // get results and compare values
        Configuration<List<ConfigurationParameter>> result = configurationServiceDao.findByConfigurationId(resourceId);

        assertArrayEquals(serialisedValue,result.getParameters().get(0).getSerialisedValue());
        assertEquals("longValue",result.getParameters().get(0).getName());

    }

    @Test
    @DirtiesContext
    public void save_followed_by_find_when_object_is_List() {

        String resourceId = "testResourceId";
        List listValue = new ArrayList(Arrays.asList("a","b","c"));
        byte[] serialisedValue = serialiser.serialise(listValue);
        ConfigurationParameter listParam = new ConfigurationParameterObjectImpl("listValue", listValue, serialisedValue, "testDescription");

        Configuration<List<ConfigurationParameter>> configuration = new DefaultConfiguration(resourceId, Arrays.asList(listParam));

        configurationServiceDao.save(configuration);


        // get results and compare values
        Configuration<List<ConfigurationParameter>> result = configurationServiceDao.findByConfigurationId(resourceId);

        assertArrayEquals(serialisedValue,result.getParameters().get(0).getSerialisedValue());
        assertEquals("listValue",result.getParameters().get(0).getName());

    }

    @Test
    @DirtiesContext
    public void save_followed_by_find_when_object_is_map() {

        String resourceId = "testResourceId";
        Map<String,String> mapValue = new HashMap<>();
        mapValue.put("key","value");
        byte[] serialisedValue = serialiser.serialise(mapValue);
        ConfigurationParameter listParam = new ConfigurationParameterObjectImpl("mapValue", mapValue, serialisedValue, "testDescription");

        Configuration<List<ConfigurationParameter>> configuration = new DefaultConfiguration(resourceId, Arrays.asList(listParam));

        configurationServiceDao.save(configuration);


        // get results and compare values
        Configuration<List<ConfigurationParameter>> result = configurationServiceDao.findByConfigurationId(resourceId);

        assertArrayEquals(serialisedValue,result.getParameters().get(0).getSerialisedValue());
        assertEquals("mapValue",result.getParameters().get(0).getName());

    }

}
