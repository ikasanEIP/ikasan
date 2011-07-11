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
 * Test class for {@link ConfigurationParameter}
 * 
 * @author Ikasan Development Team
 *
 */
public class ConfigurationParameterTest
{
    /**
     * Test failed constructor due to null configurationParameter name.
     */
    @Test(expected = IllegalArgumentException.class)
    public void test_failedConstructorDueToNullName()
    {
        new Configuration(null, null);
    }

    /**
     * Test configurationParameter equality.
     */
    @Test
    public void test_equality()
    {
        ConfigurationParameter configurationParameter1 = new ConfigurationParameter("configurationParameterName", "configurationParameterValue", "configurationDescription");
        ConfigurationParameter configurationParameter2 = new ConfigurationParameter("configurationParameterName", "configurationParameterValue", "configurationDescription");
        Assert.assertTrue(configurationParameter1.equals(configurationParameter2));
    }

    /**
     * Test configurationParameter name inequality.
     */
    @Test
    public void test_inequalityDueToName()
    {
        ConfigurationParameter configurationParameter1 = new ConfigurationParameter("configurationParameterName1", "configurationParameterValue", "configurationDescription");
        ConfigurationParameter configurationParameter2 = new ConfigurationParameter("configurationParameterName2", "configurationParameterValue", "configurationDescription");
        Assert.assertFalse(configurationParameter1.equals(configurationParameter2));
    }

    /**
     * Test configurationParameter value inequality.
     */
    @Test
    public void test_inequalityDueToValue()
    {
        ConfigurationParameter configurationParameter1 = new ConfigurationParameter("configurationParameterName", "configurationParameterValue1", "configurationDescription");
        ConfigurationParameter configurationParameter2 = new ConfigurationParameter("configurationParameterName", "configurationParameterValue2", "configurationDescription");
        Assert.assertFalse(configurationParameter1.equals(configurationParameter2));
    }

    /**
     * Test configurationParameter description inequality.
     */
    @Test
    public void test_inequalityDueToDescription()
    {
        ConfigurationParameter configurationParameter1 = new ConfigurationParameter("configurationParameterName", "configurationParameterValue", "configurationDescription1");
        ConfigurationParameter configurationParameter2 = new ConfigurationParameter("configurationParameterName", "configurationParameterValue", "configurationDescription2");
        Assert.assertFalse(configurationParameter1.equals(configurationParameter2));
    }

    /**
     * Test configurationParameter hashCode.
     */
    @Test
    public void test_hashCode()
    {
        Map<ConfigurationParameter,String> parametersMap = new HashMap<ConfigurationParameter,String>();
        parametersMap.put(new ConfigurationParameter("nameOne", "valueOne"), "One");
        parametersMap.put(new ConfigurationParameter("nameOne", "valueTwo"), "Two");
        parametersMap.put(new ConfigurationParameter("nameOne", null), "Three");
        parametersMap.put(new ConfigurationParameter("nameOne", null), "Three");
        
        Assert.assertTrue(parametersMap.size() == 3);
    }
}
