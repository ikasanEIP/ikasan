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

import org.ikasan.exclusion.dao.BlackListDaoFactory;
import org.ikasan.spec.exclusion.ExclusionEventDao;
import org.ikasan.spec.serialiser.SerialiserFactory;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Test class for ExclusionServiceFactory.
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

public class ExclusionServiceFactoryTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    @Resource
    BlackListDaoFactory blackListDaoFactory;

    @Resource
    ExclusionEventDao exclusionEventDao;

    @Resource
    SerialiserFactory serialiserFactory;

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_blacklist_dao()
    {
        new ExclusionServiceFactory(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_exclusionEvent_dao()
    {
        new ExclusionServiceFactory(blackListDaoFactory, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_serialiser()
    {
        new ExclusionServiceFactory(blackListDaoFactory, exclusionEventDao, null);
    }

    /**
     * Test exclusion
     */
    @Test
    public void test_exclusionServiceFactory_operations()
    {
        ExclusionServiceFactory exclusionServiceFactory = new ExclusionServiceFactory(blackListDaoFactory, exclusionEventDao, serialiserFactory);
        Assert.assertNotNull("Should not be null", exclusionServiceFactory.getExclusionService("moduleName", "flowName"));
        this.mockery.assertIsSatisfied();
    }
}
