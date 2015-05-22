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
package org.ikasan.console.pointtopointflow.dao;

import java.util.Set;
import java.util.TreeSet;

import javax.annotation.Resource;

import org.ikasan.console.module.Module;
import org.ikasan.console.pointtopointflow.PointToPointFlowImpl;
import org.ikasan.console.pointtopointflow.PointToPointFlowProfileImpl;
import org.ikasan.spec.management.PointToPointFlow;
import org.ikasan.spec.management.PointToPointFlowProfile;
import org.jmock.Mockery;
import org.jmock.lib.legacy.ClassImposteriser;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * JUnit based test class for testing HibernatePointToPointFlowProfileDao
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/hsqldb-datasource-conf.xml", "/console-service-conf.xml", "/test-service-conf.xml"})
public class HibernatePointToPointFlowProfileDaoTest
{
    /**
     * The context that the tests run in, allows for mocking actual concrete
     * classes
     */
    private Mockery context = new Mockery()
    {
        {
            setImposteriser(ClassImposteriser.INSTANCE);
        }
    };

    @Resource
    PointToPointFlowProfileDao pointToPointFlowProfileDao;
    
    @Resource
    StubbedDao stubbedDao;
    
    /**
     * Setup persisted entries for the tests
     */
    @Before
    public void setup()
    {
        PointToPointFlowProfile pointToPointFlowProfile = new PointToPointFlowProfileImpl();
        pointToPointFlowProfile.setName("test1");

        PointToPointFlow<Module> pointToPointFlow = new PointToPointFlowImpl();
        Set<PointToPointFlow<Module>> pointToPointFlows = new TreeSet<PointToPointFlow<Module>>();
        this.stubbedDao.save(pointToPointFlowProfile);
    }

    /**
     * Test find all point to point profiles
     */
    @Test
    public void test_findAllPointToPointProfiles()
    {
        Set<PointToPointFlowProfile> profiles = pointToPointFlowProfileDao.findAllPointToPointFlowProfiles();
        Assert.assertTrue("should not be null", profiles != null);
    }
    
    /**
     * Test find all point to point profiles
     */
    @Test
    public void test_findPointToPointFlowProfiles()
    {
        Set<Long> ids = new TreeSet<Long>();
        ids.add(Long.valueOf(100));
        Set<PointToPointFlowProfile> profiles = pointToPointFlowProfileDao.findPointToPointFlowProfiles(ids);
        Assert.assertTrue("should not be null", profiles != null);
    }
    
    /**
     * Clear persisted entries
     */
    @After
    public void teardown()
    {
        this.stubbedDao.deleteAll();
    }

}
