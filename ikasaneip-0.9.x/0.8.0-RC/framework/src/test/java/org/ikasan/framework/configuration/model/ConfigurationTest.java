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
package org.ikasan.framework.configuration.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for {@link Configuration}
 * 
 * @author Ikasan Development Team
 *
 */
public class ConfigurationTest
{
    /**
     * Test failed constructor due to null configurationId.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorDueToNullConfigurationId()
    {
        new Configuration(null, null);
    }

    /**
     * Test failed constructor due to null configurationParameters.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorDueToNullConfigurationParameters()
    {
        new Configuration("configurationId", null);
    }

    /**
     * Test configuration equality.
     */
    @Test
    public void test_equality()
    {
        Configuration configuration1 = new Configuration("configurationName", "configurationDescription", new ArrayList<ConfigurationParameter>());
        Configuration configuration2 = new Configuration("configurationName", "configurationDescription", new ArrayList<ConfigurationParameter>());
        Assert.assertTrue(configuration1.equals(configuration2));
    }
    
    /**
     * Test configuration name inequality.
     */
    @Test
    public void test_name_inequality()
    {
        Configuration configuration1 = new Configuration("configurationName1", "configurationDescription", new ArrayList<ConfigurationParameter>());
        Configuration configuration2 = new Configuration("configurationName2", "configurationDescription", new ArrayList<ConfigurationParameter>());
        Assert.assertFalse(configuration1.equals(configuration2));
    }
    
    /**
     * Test configuration description inequality.
     */
    @Test
    public void test_description_inequality()
    {
        Configuration configuration1 = new Configuration("configurationName", "configurationDescription1", new ArrayList<ConfigurationParameter>());
        Configuration configuration2 = new Configuration("configurationName", "configurationDescription2", new ArrayList<ConfigurationParameter>());
        Assert.assertFalse(configuration1.equals(configuration2));
    }
    
    /**
     * Test configuration parameter inequality.
     */
    @Test
    public void test_parameter_inequality()
    {
        ConfigurationParameter configurationParameter1 = new ConfigurationParameter("name", "value");
        List<ConfigurationParameter> configurationParameters1 = new ArrayList<ConfigurationParameter>();
        configurationParameters1.add(configurationParameter1);
        
        List<ConfigurationParameter> configurationParameters2 = new ArrayList<ConfigurationParameter>();
        
        // test when parameters are not equal
        Configuration configuration1 = new Configuration("configurationName", "configurationDescription", configurationParameters1);
        Configuration configuration2 = new Configuration("configurationName", "configurationDescription", configurationParameters2);
        Assert.assertFalse(configuration1.equals(configuration2));

        // make parameters equal and reassert the test
        ConfigurationParameter configurationParameter2 = new ConfigurationParameter("name", "value");
        configuration2.getConfigurationParameters().add(configurationParameter2);
        Assert.assertTrue(configuration1.equals(configuration2));
        
        // make parameters content unequal and reassert the test
        configurationParameter2.setName("different");
        Assert.assertFalse(configuration1.equals(configuration2));
    }
    
    /**
     * Test configuration hashCode.
     */
    @Test
    public void test_hashCode()
    {
        Map<Configuration, String> map = new HashMap<Configuration,String>();
        List<ConfigurationParameter> configurationParameters1 = new ArrayList<ConfigurationParameter>();
        List<ConfigurationParameter> configurationParameters2 = new ArrayList<ConfigurationParameter>();
        List<ConfigurationParameter> configurationParameters3 = new ArrayList<ConfigurationParameter>();
        
        map.put(new Configuration("componentIdOne", configurationParameters1), "one");
        map.put(new Configuration("componentIdOne", configurationParameters1), "oneA");
        map.put(new Configuration("componentIdTwo", configurationParameters2), "two");
        map.put(new Configuration("componentIdTwo", configurationParameters2), "twoB");
        map.put(new Configuration("componentIdThree", configurationParameters3), "three");
        map.put(new Configuration("componentIdThree", configurationParameters3), "threeC");
        
        Assert.assertTrue(map.size() == 3);
    }

}
