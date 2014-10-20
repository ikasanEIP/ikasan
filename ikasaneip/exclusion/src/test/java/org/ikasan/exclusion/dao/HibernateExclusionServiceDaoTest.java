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

import junit.framework.Assert;
import org.ikasan.exclusion.model.ExclusionEvent;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * Test class for HibernateExclusionServiceDao.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/exclusion-service-conf.xml",
        "/hsqldb-datasource-conf.xml"
        })

public class HibernateExclusionServiceDaoTest
{
    /**
     * Mockery for mocking concrete classes
     */
    private Mockery mockery = new Mockery()
    {{
        setImposteriser(ClassImposteriser.INSTANCE);
    }};

    @Resource
    ExclusionServiceDao<ExclusionEvent> exclusionServiceDao;

    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_contains_add_remove_operations()
    {
        ExclusionEvent exclusionEvent = new ExclusionEvent("moduleName", "flowName", "123456");
        Assert.assertFalse("Should not be found", exclusionServiceDao.contains(exclusionEvent) );

        exclusionServiceDao.add(exclusionEvent);
        Assert.assertTrue("Should be found", exclusionServiceDao.contains(exclusionEvent));

        exclusionServiceDao.remove(exclusionEvent);
        Assert.assertFalse("Should not be found", exclusionServiceDao.contains(exclusionEvent) );

        this.mockery.assertIsSatisfied();
    }

    /**
     * Test exclusion
     */
    @DirtiesContext
    @Test
    public void test_deleteExpired_operation()
    {
        // new event with 1 milli expiry
        ExclusionEvent exclusionEvent = new ExclusionEvent("moduleName", "flowName", "123456");
        ExclusionEvent exclusionEventExpired = new ExclusionEvent("moduleName", "flowName", "1234567", 1L);
        Assert.assertFalse("Non expired should not be found", exclusionServiceDao.contains(exclusionEvent) );
        Assert.assertFalse("Expired should not be found", exclusionServiceDao.contains(exclusionEventExpired) );

        exclusionServiceDao.add(exclusionEvent);
        exclusionServiceDao.add(exclusionEventExpired);
        Assert.assertTrue("Non expired should be found", exclusionServiceDao.contains(exclusionEvent));
        Assert.assertTrue("Expired should be found", exclusionServiceDao.contains(exclusionEventExpired));

        exclusionServiceDao.deleteExpired();
        Assert.assertTrue("Should be found after deleteAll", exclusionServiceDao.contains(exclusionEvent));
        Assert.assertFalse("Should not be found after deleteAll", exclusionServiceDao.contains(exclusionEventExpired));

        this.mockery.assertIsSatisfied();
    }
}