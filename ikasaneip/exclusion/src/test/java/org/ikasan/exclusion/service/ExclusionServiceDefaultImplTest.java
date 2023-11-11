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

import jakarta.annotation.Resource;
import org.ikasan.exclusion.dao.BlackListDaoFactory;
import org.ikasan.spec.exclusion.ExclusionEvent;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.exclusion.ExclusionService;
import org.ikasan.spec.flow.FlowEvent;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for ExclusionServiceDefaultImpl based on
 * the implementation of a ExclusionService contract.
 * 
 * @author Ikasan Development Team
 */
@SpringJUnitConfig(locations = {
        "/exclusion-service-conf.xml",
        "/substitute-components.xml",
        "/h2db-datasource-conf.xml"
})
class ExclusionServiceDefaultImplTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    FlowEvent flowEvent = mockery.mock(FlowEvent.class, "mockFlowEvent");

    @Resource
    BlackListDaoFactory exclusionServiceBlacklistDaoFactory;

    @Resource
    ExclusionEventDao<String,ExclusionEvent> exclusionServiceExclusionEventDao;

    @Resource
    SerialiserFactory serialiserFactory;

    @Test
    void test_failed_constructor_null_moduleName()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExclusionServiceDefaultImpl(null, null, null, null, null);
        });
    }

    @Test
    void test_failed_constructor_null_flowName()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExclusionServiceDefaultImpl("moduleName", null, null, null, null);
        });
    }

    @Test
    void test_failed_constructor_null_blacklist_dao()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExclusionServiceDefaultImpl("moduleName", "flowName", null, null, null);
        });
    }

    @Test
    void test_failed_constructor_null_exclusionEvent_dao()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDaoFactory.getBlackListDao(), null, null);
        });
    }

    @Test
    void test_failed_constructor_null_serialiser()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDaoFactory.getBlackListDao(), exclusionServiceExclusionEventDao, null);
        });
    }

    /**
     * Test exclusion add, contains, remove, get
     */
    @DirtiesContext
            @Test
    void test_exclusionService_operations()
    {
        ExclusionService exclusionService = new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDaoFactory.getBlackListDao(), exclusionServiceExclusionEventDao, serialiserFactory.getDefaultSerialiser());

        final FlowEvent expiredFlowEvent = mockery.mock(FlowEvent.class, "expired-flow-event");
        final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

        assertFalse(exclusionService.isBlackListed("123456"), "Should not be blacklisted");
        exclusionService.addBlacklisted("123456", "uri", flowInvocationContext);
        assertTrue(exclusionService.isBlackListed("123456"), "Should be blacklisted");

        exclusionService.addBlacklisted("123456", "uri", flowInvocationContext);
        assertTrue(exclusionService.isBlackListed("123456"), "Should be blacklisted");

        exclusionService.addBlacklisted("123456", "uri", flowInvocationContext);
        assertTrue(exclusionService.isBlackListed("123456"), "Should be blacklisted");

        // this flowEvent should exist in blacklist
        assertEquals("uri", exclusionService.getErrorUri("123456"));

        // this flowEvent does not exist in blacklist
        String errorUri = exclusionService.getErrorUri("111111");
        assertNull(errorUri);

        exclusionService.removeBlacklisted("123456");
        exclusionService.removeBlacklisted("123456");
        assertFalse(exclusionService.isBlackListed("123456"), "Should not be blacklisted");
    }

    /**
     * Test exclusion housekeep
     */
    @DirtiesContext
            @Test
    void test_exclusionService_housekeep_with_housekeepable()
    {
        ExclusionService exclusionService = new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDaoFactory.getBlackListDao(), exclusionServiceExclusionEventDao, serialiserFactory.getDefaultSerialiser());
        exclusionService.setTimeToLive(-1L);

        final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

        assertFalse(exclusionService.isBlackListed("123456"), "Should not be blacklisted");

        exclusionService.addBlacklisted("123456", "uri", flowInvocationContext);
        assertTrue(exclusionService.isBlackListed("123456"), "Should be blacklisted");

        exclusionService.housekeep();
        assertFalse(exclusionService.isBlackListed("123456"), "Should not be blacklisted");
    }

    /**
     * Test exclusion housekeep
     */
    @DirtiesContext
            @Test
    void test_exclusionService_housekeep_without_housekeepable()
    {
        ExclusionService exclusionService = new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDaoFactory.getBlackListDao(), exclusionServiceExclusionEventDao, serialiserFactory.getDefaultSerialiser());
        exclusionService.setTimeToLive(10000L);

        final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

        assertFalse(exclusionService.isBlackListed("123456"), "Should not be blacklisted");

        exclusionService.addBlacklisted("123456", "uri", flowInvocationContext);
        assertTrue(exclusionService.isBlackListed("123456"), "Should be blacklisted");

        exclusionService.housekeep();
        assertTrue(exclusionService.isBlackListed("123456"), "Should be blacklisted");
    }

    /**
     * Test exclusion housekeep
     */
    @DirtiesContext
            @Test
    void test_exclusionService_park()
    {
        final String payload = "this is payload content";

        ExclusionService exclusionService = new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceBlacklistDaoFactory.getBlackListDao(), exclusionServiceExclusionEventDao, serialiserFactory.getDefaultSerialiser());
        final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

        // expectations
        mockery.checking(new Expectations()
        {
            {
                // when creating exclusionEvent
                exactly(1).of(flowEvent).getPayload();
                will(returnValue(payload));
           }
        });

        exclusionService.addBlacklisted("123456", "uri", flowInvocationContext);
        exclusionService.park(flowEvent, "123456");
        ExclusionEvent exclusionEvent = exclusionServiceExclusionEventDao.find("moduleName", "flowName", "123456");
        Object exclusionEventPayloadBytes = exclusionEvent.getEvent();
        Object exclusionEventPayload = serialiserFactory.getDefaultSerialiser().deserialise(exclusionEventPayloadBytes);
        assertEquals(exclusionEventPayload, payload, "Should be equal");

        this.mockery.assertIsSatisfied();
    }

}
