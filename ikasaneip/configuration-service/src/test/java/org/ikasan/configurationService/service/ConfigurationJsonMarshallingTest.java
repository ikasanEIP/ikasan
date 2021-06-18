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
package org.ikasan.configurationService.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator;
import com.fasterxml.jackson.databind.jsontype.PolymorphicTypeValidator;
import org.ikasan.configurationService.dao.ConfigurationDao;
import org.ikasan.configurationService.model.ConfigurationParameterMapImpl;
import org.ikasan.configurationService.model.ConfigurationParameterStringImpl;
import org.ikasan.configurationService.model.DefaultConfiguration;
import org.ikasan.spec.configuration.Configuration;
import org.ikasan.spec.configuration.ConfigurationParameter;
import org.ikasan.spec.configuration.ConfigurationService;
import org.ikasan.spec.configuration.ConfiguredResource;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test class for ConfiguredResourceConfigurationService based on
 * the implementation of a ConfigurationService contract.
 *
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(classes = {TestConfiguration.class})
public class ConfigurationJsonMarshallingTest {
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery() {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    static ObjectMapper objectMapper = new ObjectMapper();

    @Resource
    ConfigurationDao configurationServiceDao;

    @Resource
    ConfigurationService configurationService;

    ConfiguredResource configuredResource = mockery.mock(ConfiguredResource.class, "mockedConfiguredResource");

    @Test
    public void test_pojo_to_json() throws JsonProcessingException
    {
        PolymorphicTypeValidator ptv = BasicPolymorphicTypeValidator
                .builder()
                .build();
        objectMapper.activateDefaultTypingAsProperty(ptv, ObjectMapper.DefaultTyping.EVERYTHING,"_class");
        String expected = "{\"agentName\":\"secretAgentMan\",\"sampleScheduleConfigurations\":[{\"jobName\":\"jobName\",\"jobGroup\":\"jobGroup\",\"cron\":\"cron command\",\"timezone\":\"BST\",\"commandLine\":\"command line\",\"stdOut\":\"stdOut.out\",\"stdErr\":\"stdErr.out\",\"threshold\":1000}]}";
        SampleSchedulerAgentConfiguration sampleSchedulerAgentConfiguration = new SampleSchedulerAgentConfiguration();
        sampleSchedulerAgentConfiguration.setAgentName("secretAgentMan");
        SampleSchedulerConfiguration sampleSchedulerConfiguration = new SampleSchedulerConfiguration();
        sampleSchedulerConfiguration.setCommandLine("command line");
        sampleSchedulerConfiguration.setCron("cron command");
        sampleSchedulerConfiguration.setJobGroup("jobGroup");
        sampleSchedulerConfiguration.setJobName("jobName");
        sampleSchedulerConfiguration.setStdErr("stdErr.out");
        sampleSchedulerConfiguration.setStdOut("stdOut.out");
        sampleSchedulerConfiguration.setThreshold(1000);
        sampleSchedulerConfiguration.setTimezone("BST");
        sampleSchedulerAgentConfiguration.getSampleScheduleConfigurations().add(sampleSchedulerConfiguration);

        String result = objectMapper.writeValueAsString(sampleSchedulerAgentConfiguration);

        Assert.assertEquals(expected,result);
    }


}
