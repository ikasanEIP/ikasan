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
package org.ikasan.console.pointtopointflow.service;

import java.util.LinkedHashSet;
import java.util.Set;

import org.ikasan.console.module.Module;
import org.ikasan.console.pointtopointflow.PointToPointFlowImpl;
import org.ikasan.console.pointtopointflow.PointToPointFlowProfileImpl;
import org.ikasan.console.pointtopointflow.dao.StubbedDao;
import org.ikasan.spec.management.PointToPointFlow;
import org.ikasan.spec.management.PointToPointFlowProfile;
import org.junit.After;
import org.junit.Test;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import javax.annotation.Resource;

/**
 * JUnit based test class for testing PointToPointFlowServiceImpl
 * 
 * TODO Add more tests with various inputs and outputs, especially dealing with sets.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "/hsqldb-datasource-conf.xml", "/console-service-conf.xml", "/test-service-conf.xml"})
public class PointToPointFlowServiceImplTest
{

    /** The PointToPointFlow Service we are using in several tests */
    @Resource
    private PointToPointFlowProfileService pointToPointFlowProfileService;
    
    /** use a stubbedDao instance to populate the persistence for testing */
    @Resource
    StubbedDao stubbedDao;

    /** Test that a constructor throws an IllegalArgumentException if we pass it a null DAO */
    @Test(expected=IllegalArgumentException.class)
    public void testConstructorWithNullDao()
    {
        new PointToPointFlowProfileServiceImpl(null);
    }

    /**
      * Test that calling getAllPointToPointFlowProfiles returns an Empty Set if none were found.
      */
    @Test
    public void testGetAllPointToPointFlowProfilesReturnsEmpty()
    {
        // Setup
        final Set<PointToPointFlowProfile> returnedPointToPointFlowProfiles = new LinkedHashSet<PointToPointFlowProfile>();
        
        // Test
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = pointToPointFlowProfileService.getAllPointToPointFlowProfiles();
        // Verify
        Assert.assertTrue(pointToPointFlowProfiles.isEmpty());
    }
    
    /**
     * Test that calling getAllPointToPointFlowProfiles returns a Set of PointToPointProfiles
     */
    @Test
    public void testGetAllPointToPointFlowProfiles()
    {
        // Setup
        final Set<PointToPointFlowProfile> returnedPointToPointFlowProfiles = new LinkedHashSet<PointToPointFlowProfile>();
        final PointToPointFlowProfile pointToPointFlowProfile1 = new PointToPointFlowProfileImpl();
        returnedPointToPointFlowProfiles.add(pointToPointFlowProfile1);
        final PointToPointFlowProfile pointToPointFlowProfile2 = new PointToPointFlowProfileImpl();
        returnedPointToPointFlowProfiles.add(pointToPointFlowProfile2);

        final Set<PointToPointFlow<Module>> pointToPointFlows1 = new LinkedHashSet<PointToPointFlow<Module>>();
        final PointToPointFlow<Module> pointToPointFlow1 = new PointToPointFlowImpl();
        final Set<PointToPointFlow<Module>> pointToPointFlows2 = new LinkedHashSet<PointToPointFlow<Module>>();
        final PointToPointFlow<Module> pointToPointFlow2 = new PointToPointFlowImpl();
        pointToPointFlows1.add(pointToPointFlow1);
        pointToPointFlows2.add(pointToPointFlow2);
        
        this.stubbedDao.save(pointToPointFlowProfile1);
        this.stubbedDao.save(pointToPointFlowProfile2);
        
        // Test
        Set<PointToPointFlowProfile> pointToPointFlowProfiles = pointToPointFlowProfileService.getAllPointToPointFlowProfiles();
        // Verify
        Assert.assertEquals(2, pointToPointFlowProfiles.size());
    }

//    /**
//     * TODO Test that calling getAllPointToPointFlowProfiles returns a Set of PointToPointFlowProfiles that 
//     * have their PointToPointFlows in the correct order
//     */
//    @Test
//    public void testGetAllPointToPointFlowProfilesWithOrder()
//    {
//        // Expectations
//        context.checking(new Expectations()
//        {
//            {
//                one(pointToPointFlowProfileDao).findAllPointToPointFlowProfiles();
//                will(returnValue(new LinkedHashSet<PointToPointFlowProfile>()));
//            }
//        });
//        // Test
//        Set<PointToPointFlowProfile> pointToPointFlowProfiles = pointToPointFlowProfileService.getAllPointToPointFlowProfiles();
//        // Verify
//        Assert.assertTrue(pointToPointFlowProfiles.isEmpty());
//        context.assertIsSatisfied();
//    }
//    
//    /**
//     * Test that calling getAllPointToPointFlowProfileIds returns an Empty Set if none were found.
//     */
//    @Test
//    public void testGetAllPointToPointFlowProfileIdsReturnsEmpty()
//    {
//        // Setup
//        final Set<PointToPointFlowProfile> returnedPointToPointFlowProfiles = new LinkedHashSet<PointToPointFlowProfile>();
//        
//        // Expectations
//        context.checking(new Expectations()
//        {
//            {
//                one(pointToPointFlowProfileDao).findAllPointToPointFlowProfiles();
//                will(returnValue(returnedPointToPointFlowProfiles));
//            }
//        });
//        // Test
//        Set<Long> pointToPointFlowProfileIds = pointToPointFlowProfileService.getAllPointToPointFlowProfileIds();
//        // Verify
//        Assert.assertTrue(pointToPointFlowProfileIds.isEmpty());
//        context.assertIsSatisfied();
//    }
//
//    /**
//     * Test that calling getAllPointToPointFlowProfileIds returns a Set of PointToPointProfileIds
//     */
//    @Test
//    public void testGetAllPointToPointFlowProfileIds()
//    {
//        // Setup
//        final Set<PointToPointFlowProfile> returnedPointToPointFlowProfiles = new LinkedHashSet<PointToPointFlowProfile>();
//        final PointToPointFlowProfile pointToPointFlowProfile1 = context.mock(PointToPointFlowProfile.class);
//        returnedPointToPointFlowProfiles.add(pointToPointFlowProfile1);
//        final PointToPointFlowProfile pointToPointFlowProfile2 = context.mock(PointToPointFlowProfile.class);
//        returnedPointToPointFlowProfiles.add(pointToPointFlowProfile2);
//
//        final Set<PointToPointFlow> pointToPointFlows1 = new LinkedHashSet<PointToPointFlow>();
//        final PointToPointFlow pointToPointFlow1 = context.mock(PointToPointFlow.class);
//        final Set<PointToPointFlow> pointToPointFlows2 = new LinkedHashSet<PointToPointFlow>();
//        final PointToPointFlow pointToPointFlow2 = context.mock(PointToPointFlow.class);
//        pointToPointFlows1.add(pointToPointFlow1);
//        pointToPointFlows2.add(pointToPointFlow2);
//        
//        // Expectations
//        context.checking(new Expectations()
//        {
//            {
//                one(pointToPointFlowProfileDao).findAllPointToPointFlowProfiles();
//                will(returnValue(returnedPointToPointFlowProfiles));
//
//                one(pointToPointFlowProfile1).getPointToPointFlows();
//                will(returnValue(pointToPointFlows1));
//                one(pointToPointFlowProfile1).setPointToPointFlows(pointToPointFlows1);
//                one(pointToPointFlowProfile1).getId();
//                will(returnValue(new Long(1)));
//                
//                one(pointToPointFlowProfile2).getPointToPointFlows();
//                will(returnValue(pointToPointFlows2));
//                one(pointToPointFlowProfile2).setPointToPointFlows(pointToPointFlows2);
//                one(pointToPointFlowProfile2).getId();
//                will(returnValue(new Long(2)));
//            }
//        });
//        // Test
//        Set<Long> pointToPointFlowProfileIds = pointToPointFlowProfileService.getAllPointToPointFlowProfileIds();
//        // Verify
//        Assert.assertEquals(2, pointToPointFlowProfileIds.size());
//        context.assertIsSatisfied();
//    }
//    
//    /**
//     * Test that calling getModuleIdsFromPointToPointFlowProfiles with null Ids returns an Empty Set of Module ids
//     */
//    @Test
//    public void testGetModuleIdsFromPointToPointFlowProfilesWithNullIds()
//    {
//        // Setup
//        final Set<Long> pointToPointFlowProfileIds = null;
//        
//        // Expectations
//        context.checking(new Expectations()
//        {
//            {
//                one(pointToPointFlowProfileDao).findPointToPointFlowProfiles(pointToPointFlowProfileIds);
//                will(returnValue(new LinkedHashSet<PointToPointFlowProfile>()));
//            }
//        });
//        // Test
//        Set<Long> moduleIds = pointToPointFlowProfileService.getModuleIdsFromPointToPointFlowProfiles(pointToPointFlowProfileIds);
//        // Verify
//        Assert.assertTrue(moduleIds.isEmpty());
//        context.assertIsSatisfied();
//    }
//    
//    /**
//     * Test that calling getModuleIdsFromPointToPointFlowProfiles with an empty Set of Ids returns an Empty Set of Module ids
//     */
//    @Test
//    public void testGetModuleIdsFromPointToPointFlowProfilesWithNoIds()
//    {
//        // Setup
//        final Set<Long> pointToPointFlowProfileIds = new LinkedHashSet<Long>();
//        
//        // Expectations
//        context.checking(new Expectations()
//        {
//            {
//                one(pointToPointFlowProfileDao).findPointToPointFlowProfiles(pointToPointFlowProfileIds);
//                will(returnValue(new LinkedHashSet<PointToPointFlowProfile>()));
//            }
//        });
//        // Test
//        Set<Long> moduleIds = pointToPointFlowProfileService.getModuleIdsFromPointToPointFlowProfiles(pointToPointFlowProfileIds);
//        // Verify
//        Assert.assertTrue(moduleIds.isEmpty());
//        context.assertIsSatisfied();
//    }
//
//    /**
//     * Test that calling getModuleIdsFromPointToPointFlowProfiles with an Id that doesn't exist returns an Empty Set of Module ids
//     */
//    @Test
//    public void testGetModuleIdsFromPointToPointFlowProfilesWithNonExistentId()
//    {
//        // Setup
//        final Set<Long> pointToPointFlowProfileIds = new LinkedHashSet<Long>();
//        pointToPointFlowProfileIds.add(new Long(-1));
//        
//        // Expectations
//        context.checking(new Expectations()
//        {
//            {
//                one(pointToPointFlowProfileDao).findPointToPointFlowProfiles(pointToPointFlowProfileIds);
//                will(returnValue(new LinkedHashSet<PointToPointFlowProfile>()));
//            }
//        });
//        // Test
//        Set<Long> moduleIds = pointToPointFlowProfileService.getModuleIdsFromPointToPointFlowProfiles(pointToPointFlowProfileIds);
//        // Verify
//        Assert.assertTrue(moduleIds.isEmpty());
//        context.assertIsSatisfied();
//    }
//
//    /**
//     * Test that calling getModuleIdsFromPointToPointFlowProfiles with a valid Id returns a Set of Module ids
//     */
//    @Test
//    public void testGetModuleIdsFromPointToPointFlowProfiles()
//    {
//        // Setup
//        final Set<Long> pointToPointFlowProfileIds = new LinkedHashSet<Long>();
//        pointToPointFlowProfileIds.add(new Long(1));
//
//        final PointToPointFlowProfile pointToPointFlowProfile = context.mock(PointToPointFlowProfile.class);
//        final PointToPointFlow pointToPointFlow = context.mock(PointToPointFlow.class);
//        final Module fromModule = context.mock(Module.class);
//        final Module toModule = context.mock(Module.class);
//        
//        final Set<PointToPointFlow> pointToPointFlows = new LinkedHashSet<PointToPointFlow>();
//        pointToPointFlows.add(pointToPointFlow);
//        final Set<PointToPointFlowProfile> pointToPointFlowProfiles = new LinkedHashSet<PointToPointFlowProfile>();
//        pointToPointFlowProfiles.add(pointToPointFlowProfile);
//        
//        // Expectations
//        context.checking(new Expectations()
//        {
//            {
//                one(pointToPointFlowProfileDao).findPointToPointFlowProfiles(pointToPointFlowProfileIds);
//                will(returnValue(pointToPointFlowProfiles));
//                one(pointToPointFlowProfile).getPointToPointFlows();
//                will(returnValue(pointToPointFlows));
//                one(pointToPointFlow).getFromModule();
//                will(returnValue(fromModule));
//                one(pointToPointFlow).getToModule();
//                will(returnValue(toModule));
//                one(fromModule).getId();
//                will(returnValue(new Long(9)));
//                one(toModule).getId();
//                will(returnValue(new Long(10)));
//            }
//        });
//        // Test
//        Set<Long> moduleIds = pointToPointFlowProfileService.getModuleIdsFromPointToPointFlowProfiles(pointToPointFlowProfileIds);
//        // Verify
//        Assert.assertEquals(2, moduleIds.size());
//        context.assertIsSatisfied();        
//    }
//
//    /**
//     * Test that calling getModuleIdsFromPointToPointFlowProfiles with a valid Id returns a 
//     * Set of reduced Module ids as the modules are NULL
//     */
//    @Test
//    public void testGetModuleIdsFromPointToPointFlowProfilesWithNullModule()
//    {
//        // Setup
//        final Set<Long> pointToPointFlowProfileIds = new LinkedHashSet<Long>();
//        pointToPointFlowProfileIds.add(new Long(1));
//
//        final PointToPointFlowProfile pointToPointFlowProfile = context.mock(PointToPointFlowProfile.class);
//        final PointToPointFlow pointToPointFlow = context.mock(PointToPointFlow.class);
//        final Module fromModule = null;
//        final Module toModule = null;
//        
//        final Set<PointToPointFlow> pointToPointFlows = new LinkedHashSet<PointToPointFlow>();
//        pointToPointFlows.add(pointToPointFlow);
//        final Set<PointToPointFlowProfile> pointToPointFlowProfiles = new LinkedHashSet<PointToPointFlowProfile>();
//        pointToPointFlowProfiles.add(pointToPointFlowProfile);
//        
//        // Expectations
//        context.checking(new Expectations()
//        {
//            {
//                one(pointToPointFlowProfileDao).findPointToPointFlowProfiles(pointToPointFlowProfileIds);
//                will(returnValue(pointToPointFlowProfiles));
//                one(pointToPointFlowProfile).getPointToPointFlows();
//                will(returnValue(pointToPointFlows));
//                one(pointToPointFlow).getFromModule();
//                will(returnValue(fromModule));
//                one(pointToPointFlow).getToModule();
//                will(returnValue(toModule));
//                //one(fromModule).getId();
//                //will(returnValue(new Long(9)));
//                //one(toModule).getId();
//                //will(returnValue(new Long(10)));
//            }
//        });
//        // Test
//        Set<Long> moduleIds = pointToPointFlowProfileService.getModuleIdsFromPointToPointFlowProfiles(pointToPointFlowProfileIds);
//        // Verify
//        Assert.assertTrue(moduleIds.isEmpty());
//        context.assertIsSatisfied();        
//    }
    
    
    /**
     * Clear persisted entries
     */
    @After
    public void teardown()
    {
        this.stubbedDao.deleteAll();
    }
}
