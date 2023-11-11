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
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Mockery;
import org.jmock.imposters.ByteBuddyClassImposteriser;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Test class for ExclusionServiceFactory.
 * 
 * @author Ikasan Development Team
 */
@SpringJUnitConfig(locations = {
        "/exclusion-service-conf.xml",
        "/substitute-components.xml",
        "/h2db-datasource-conf.xml"
})
class ExclusionServiceFactoryTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ByteBuddyClassImposteriser.INSTANCE);
    }};

    @Resource
    BlackListDaoFactory blackListDaoFactory;

    @Resource
    ExclusionEventDao exclusionEventDao;

    @Resource
    SerialiserFactory serialiserFactory;

    @Test
    void test_failed_constructor_null_blacklist_dao()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExclusionServiceFactory(null, null, null);
        });
    }

    @Test
    void test_failed_constructor_null_exclusionEvent_dao()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExclusionServiceFactory(blackListDaoFactory, null, null);
        });
    }

    @Test
    void test_failed_constructor_null_serialiser()
    {
        assertThrows(IllegalArgumentException.class, () -> {
            new ExclusionServiceFactory(blackListDaoFactory, exclusionEventDao, null);
        });
    }

    /**
     * Test exclusion
     */
    @Test
    void test_exclusionServiceFactory_operations()
    {
        ExclusionServiceFactory exclusionServiceFactory = new ExclusionServiceFactory(blackListDaoFactory, exclusionEventDao, serialiserFactory);
        assertNotNull(exclusionServiceFactory.getExclusionService("moduleName", "flowName"), "Should not be null");
        this.mockery.assertIsSatisfied();
    }
}
