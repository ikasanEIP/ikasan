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
package org.ikasan.exclusion.service;

import org.ikasan.exclusion.dao.BlackListDao;
import org.ikasan.exclusion.dao.ExclusionEventDao;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.serialiser.Serialiser;
import org.ikasan.spec.serialiser.SerialiserFactory;
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

/**
 * Test class for ExclusionServiceDefaultImpl based on
 * the implementation of a ExclusionService contract.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/exclusion-service-conf.xml",
        "/substitute-components.xml",
        "/h2db-datasource-conf.xml"
        })

public class ExclusionServiceDefaultImplTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    FlowEvent flowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    @Resource
    BlackListDao exclusionServiceBlacklistDao;

    @Resource
    ExclusionEventDao<String,ExclusionEvent> exclusionServiceExclusionEventDao;

    @Resource
    SerialiserFactory serialiserFactory;

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_moduleName()
    {
        new ExclusionServiceDefaultImpl(null, null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_flowName()
    {
        new ExclusionServiceDefaultImpl("moduleName", null, null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_blacklist_dao()
    {
        new ExclusionServiceDefaultImpl("moduleName", "flowName", null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_exclusionEvent_dao()
    {
        new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDao, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_serialiser()
    {
        new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDao, exclusionServiceExclusionEventDao, null);
    }

    /**
     * Test exclusion add, contains, remove, get
     */
    @DirtiesContext
    @Test
    public void test_exclusionService_operations()
    {
        ExclusionService exclusionService = new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDao, exclusionServiceExclusionEventDao, serialiserFactory.getDefaultSerialiser());

        final FlowEvent expiredFlowEvent = mockery.mock(FlowEvent.class, "expired-flow-event");

        Assert.assertFalse("Should not be blacklisted", exclusionService.isBlackListed("123456"));
        exclusionService.addBlacklisted("123456", "uri");
        Assert.assertTrue("Should be blacklisted", exclusionService.isBlackListed("123456"));

        exclusionService.addBlacklisted("123456", "uri");
        Assert.assertTrue("Should be blacklisted", exclusionService.isBlackListed("123456"));

        exclusionService.addBlacklisted("123456", "uri");
        Assert.assertTrue("Should be blacklisted", exclusionService.isBlackListed("123456"));

        // this flowEvent should exist in blacklist
        Assert.assertEquals("uri", exclusionService.getErrorUri("123456"));

        // this flowEvent does not exist in blacklist
        String errorUri = exclusionService.getErrorUri("111111");
        Assert.assertEquals(null, errorUri);

        exclusionService.removeBlacklisted("123456");
        exclusionService.removeBlacklisted("123456");
        Assert.assertFalse("Should not be blacklisted", exclusionService.isBlackListed("123456"));
    }

    /**
     * Test exclusion housekeep
     */
    @DirtiesContext
    @Test
    public void test_exclusionService_housekeep_with_housekeepable()
    {
        ExclusionService exclusionService = new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDao, exclusionServiceExclusionEventDao, serialiserFactory.getDefaultSerialiser());
        exclusionService.setTimeToLive(-1L);

        Assert.assertFalse("Should not be blacklisted", exclusionService.isBlackListed("123456"));

        exclusionService.addBlacklisted("123456", "uri");
        Assert.assertTrue("Should be blacklisted", exclusionService.isBlackListed("123456"));

        exclusionService.housekeep();
        Assert.assertFalse("Should not be blacklisted", exclusionService.isBlackListed("123456"));
    }

    /**
     * Test exclusion housekeep
     */
    @DirtiesContext
    @Test
    public void test_exclusionService_housekeep_without_housekeepable()
    {
        ExclusionService exclusionService = new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDao, exclusionServiceExclusionEventDao, serialiserFactory.getDefaultSerialiser());
        exclusionService.setTimeToLive(10000L);

        Assert.assertFalse("Should not be blacklisted", exclusionService.isBlackListed("123456"));

        exclusionService.addBlacklisted("123456", "uri");
        Assert.assertTrue("Should be blacklisted", exclusionService.isBlackListed("123456"));

        exclusionService.housekeep();
        Assert.assertTrue("Should be blacklisted", exclusionService.isBlackListed("123456"));
    }

    /**
     * Test exclusion housekeep
     */
    @DirtiesContext
    @Test
    public void test_exclusionService_park()
    {
        final String payload = "this is payload content";

        ExclusionService exclusionService = new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDao, exclusionServiceExclusionEventDao, serialiserFactory.getDefaultSerialiser());

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // when creating exclusionEvent
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
           }
        });

        exclusionService.addBlacklisted("123456", "uri");
        exclusionService.park(flowEvent, "123456");
        ExclusionEvent exclusionEvent = exclusionServiceExclusionEventDao.find("moduleName", "flowName", "123456");
        Object exclusionEventPayloadBytes = exclusionEvent.getEvent();
        Object exclusionEventPayload = serialiserFactory.getDefaultSerialiser().deserialise(exclusionEventPayloadBytes);
        Assert.assertTrue("Should be equal", exclusionEventPayload.equals(payload));

        this.mockery.assertIsSatisfied();
    }

}
