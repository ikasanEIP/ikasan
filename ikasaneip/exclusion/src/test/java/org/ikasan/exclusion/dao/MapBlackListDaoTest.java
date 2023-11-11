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
package org.ikasan.exclusion.dao;

import org.ikasan.exclusion.model.BlackListEvent;
import org.ikasan.exclusion.model.BlackListLinkedHashMap;
import org.ikasan.spec.flow.FlowInvocationContext;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Test class for MapBlackListDao.
 * 
 * @author Ikasan Development Team
 */
@SpringJUnitConfig(locations = {
        "/exclusion-service-conf.xml",
        "/substitute-components.xml",
        "/h2db-datasource-conf.xml"
})
class MapBlackListDaoTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    /**
     * Test exclusion
     */
    @DirtiesContext
            @Test
    void test_contains_add_find_remove_operations()
    {
        BlackListDao blackListDao = new MapBlackListDao( new BlackListLinkedHashMap(2) );

        final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

        BlackListEvent blackListEvent = new BlackListEvent("moduleName", "flowName", "123456", "errorUri", flowInvocationContext);
        assertFalse(blackListDao.contains("moduleName", "flowName", "123456"), "Should not be found");

        blackListDao.insert(blackListEvent);
        assertTrue(blackListDao.contains("moduleName", "flowName", "123456"), "Should be found");

        assertEquals(blackListEvent, blackListDao.find("moduleName", "flowName", "123456"), "Should match");

        blackListDao.delete("moduleName", "flowName", "123456");
        assertFalse(blackListDao.contains("moduleName", "flowName", "123456"), "Should not be found");
    }

    /**
     * Test exclusion
     */
    @DirtiesContext
            @Test
    void test_deleteExpired_operation()
    {
        BlackListDao blackListDao = new MapBlackListDao( new BlackListLinkedHashMap(2) );

        final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

        // new event with 1 milli expiry
        BlackListEvent blackListEvent = new BlackListEvent("moduleName", "flowName", "123456",  "errorUri", flowInvocationContext);
        BlackListEvent blackListEventExpired = new BlackListEvent("moduleName", "flowName", "1234567",  "errorUri", flowInvocationContext, -1L);
        assertFalse(blackListDao.contains("moduleName", "flowName", "123456") , "Non expired should not be found");
        assertFalse(blackListDao.contains("moduleName", "flowName", "1234567") , "Expired should not be found");

        blackListDao.insert(blackListEvent);
        blackListDao.insert(blackListEventExpired);
        assertTrue(blackListDao.contains("moduleName", "flowName", "123456"), "Non expired should be found");
        assertTrue(blackListDao.contains("moduleName", "flowName", "1234567"), "Expired should be found");

        blackListDao.deleteExpired();
        assertTrue(blackListDao.contains("moduleName", "flowName", "123456"), "Should be found after deleteAll");
        assertFalse(blackListDao.contains("moduleName", "flowName", "1234567"), "Should not be found after deleteAll");
    }

    /**
     * Test exclusion
     */
    @DirtiesContext
            @Test
    void test_roll_operation()
    {
        MapBlackListDao exclusionServiceDao = new MapBlackListDao( new BlackListLinkedHashMap(5) );
        final FlowInvocationContext flowInvocationContext = mockery.mock(FlowInvocationContext.class, "flowInvocationContext");

        // new event with 1 milli expiry
        BlackListEvent blackListEvent1 = new BlackListEvent("moduleName", "flowName", "1234561", "errorUri", flowInvocationContext);
        exclusionServiceDao.insert(blackListEvent1);
        BlackListEvent blackListEvent2 = new BlackListEvent("moduleName", "flowName", "1234562", "errorUri", flowInvocationContext);
        exclusionServiceDao.insert(blackListEvent2);
        BlackListEvent blackListEvent3 = new BlackListEvent("moduleName", "flowName", "1234563", "errorUri", flowInvocationContext);
        exclusionServiceDao.insert(blackListEvent3);
        BlackListEvent blackListEvent4 = new BlackListEvent("moduleName", "flowName", "1234564", "errorUri", flowInvocationContext);
        exclusionServiceDao.insert(blackListEvent4);
        BlackListEvent blackListEvent5 = new BlackListEvent("moduleName", "flowName", "1234565", "errorUri", flowInvocationContext);
        exclusionServiceDao.insert(blackListEvent5);
        BlackListEvent blackListEvent6 = new BlackListEvent("moduleName", "flowName", "1234566", "errorUri", flowInvocationContext);
        exclusionServiceDao.insert(blackListEvent6);
        BlackListEvent blackListEvent7 = new BlackListEvent("moduleName", "flowName", "1234567", "errorUri", flowInvocationContext);
        exclusionServiceDao.insert(blackListEvent7);

        assertFalse(exclusionServiceDao.contains("moduleName", "flowName", "1234561"), "blacklisted should not contain exclusionEvent1");
        assertFalse(exclusionServiceDao.contains("moduleName", "flowName", "1234562"), "blacklisted should not contain exclusionEvent2");
        assertTrue(exclusionServiceDao.contains("moduleName", "flowName", "1234563"), "blacklisted should still contain exclusionEvent3");
        assertTrue(exclusionServiceDao.contains("moduleName", "flowName", "1234564"), "blacklisted should still contain exclusionEvent4");
        assertTrue(exclusionServiceDao.contains("moduleName", "flowName", "1234565"), "blacklisted should still contain exclusionEvent5");
    }

}
