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
package org.ikasan.error.reporting.service;

import org.ikasan.error.reporting.dao.ErrorReportingServiceDao;
import org.ikasan.error.reporting.model.ErrorOccurrence;
import org.ikasan.spec.error.reporting.ErrorReportingService;
import org.ikasan.spec.flow.FlowEvent;
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
 * Test class for ErrorReportingServiceDefaultImpl based on
 * the implementation of a ErrorReportingService contract.
 * 
 * @author Ikasan Development Team
 */
@RunWith(SpringJUnit4ClassRunner.class)
//specifies the Spring configuration to load for this test fixture
@ContextConfiguration(locations={
        "/error-reporting-service-conf.xml",
        "/h2db-datasource-conf.xml"
        })

public class ErrorReportingServiceDefaultImplTest
{
    @Resource
    ErrorReportingServiceDao errorReportingServiceDao;

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_moduleName()
    {
        new ErrorReportingServiceDefaultImpl(null, null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_flowName()
    {
        new ErrorReportingServiceDefaultImpl("moduleName", null, null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_failed_constructor_null_dao()
    {
        new ErrorReportingServiceDefaultImpl("moduleName", "flowName", null);
    }

    /**
     * Test notify
     */
    @DirtiesContext
    @Test
    public void test_errorReporting_notify_no_inflight_event()
    {
        ErrorReportingService<?,ErrorOccurrence> errorReportingService = new ErrorReportingServiceDefaultImpl("moduleName", "flowName", errorReportingServiceDao);
        String uri = errorReportingService.notify("flowElementName", new Exception("test"));
        ErrorOccurrence errorOccurrence = errorReportingService.find(uri);
        Assert.assertNotNull("Should not be null", errorOccurrence);
    }

    /**
     * Test notify
     */
    @DirtiesContext
    @Test
    public void test_errorReporting_notify_with_inflight_string_event()
    {
        ErrorReportingService<String,ErrorOccurrence> errorReportingService = new ErrorReportingServiceDefaultImpl("moduleName", "flowName", errorReportingServiceDao);
        String uri = errorReportingService.notify("flowElementName", "string based event", new Exception("test"));
        ErrorOccurrence errorOccurrence = errorReportingService.find(uri);
        Assert.assertNotNull("Should not be null", errorOccurrence);
    }

    /**
     * Test exclusion housekeep
     */
    @DirtiesContext
    @Test
    public void test_exclusionService_housekeep()
    {
//        ExclusionService exclusionService = new ExclusionServiceDefaultImpl("moduleName", "flowName", exclusionServiceDao);
//        exclusionService.setTimeToLive(-1L);
//
//        // expectations
//        mockery.checking(new Expectations()
//        {
//            {
//                // when checked in the backlist
//                exactly(1).of(flowEvent).getIdentifier();
//                will(returnValue("123456"));
//
//                // when added to the backlist
//                exactly(1).of(flowEvent).getIdentifier();
//                will(returnValue("123456"));
//
//                // when checked in the backlist
//                exactly(1).of(flowEvent).getIdentifier();
//                will(returnValue("123456"));
//
//                // housekeep with remove from the backlist
//                exactly(1).of(flowEvent).getIdentifier();
//                will(returnValue("123456"));
//            }
//        });
//
//        Assert.assertFalse("Should not be blacklisted", exclusionService.isBlackListed(flowEvent));
//
//        exclusionService.addBlacklisted(flowEvent);
//        Assert.assertTrue("Should be blacklisted", exclusionService.isBlackListed(flowEvent));
//
//        exclusionService.housekeep();
//        Assert.assertFalse("Should not be blacklisted", exclusionService.isBlackListed(flowEvent));
//
//        this.mockery.assertIsSatisfied();
    }

}
